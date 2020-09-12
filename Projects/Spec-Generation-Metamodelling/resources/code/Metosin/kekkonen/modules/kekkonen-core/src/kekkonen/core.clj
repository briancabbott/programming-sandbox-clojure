(ns kekkonen.core
  (:require [schema.core :as s]
            [plumbing.core :as p]
            [clojure.string :as str]
            [plumbing.map :as pm]
            [kekkonen.common :as kc]
            [linked.core :as linked]
            [kekkonen.interceptor :as interceptor]
            [clojure.walk :as walk]
            [schema.coerce :as sc]
            [schema.utils :as su]
            [clojure.set :as set])
  (:import [clojure.lang Var IPersistentMap Symbol PersistentVector AFunction Keyword]
           [java.io Writer])
  (:refer-clojure :exclude [namespace]))

;;
;; Common
;;

(s/defschema Function
  (s/=> {s/Keyword s/Any} s/Any))

(s/defschema KeywordMap
  {s/Keyword s/Any})

;;
;; Interceptors
;;

(defrecord Interceptor [name input output enter leave error])

(s/defschema InterceptorLike
  (s/conditional
    fn? Function
    :else (s/constrained
            {(s/optional-key :name) (s/maybe (s/cond-pre s/Keyword s/Str s/Symbol))
             (s/optional-key :input) s/Any
             (s/optional-key :output) s/Any
             (s/optional-key :enter) (s/maybe Function)
             (s/optional-key :leave) (s/maybe Function)
             (s/optional-key :error) (s/maybe Function)}
            (fn [{:keys [enter leave error]}] (or enter leave error))
            'enter-leave-or-error-required)))

;;
;; Context & Handler
;;

(s/defschema Context
  (merge
    KeywordMap
    {(s/optional-key :data) s/Any}))

(s/defschema Handler
  {:handle Function
   :type s/Keyword
   :name s/Keyword
   :ns (s/maybe s/Keyword)
   :action s/Keyword
   :description (s/maybe s/Str)

   ;; extra meta-data
   :meta KeywordMap
   ;; interceptors
   :interceptors [Interceptor]

   ;; schemas
   :input s/Any
   :output s/Any

   (s/optional-key :source-map) {:line s/Int
                                 :column s/Int
                                 :file s/Str
                                 :ns s/Symbol
                                 :name s/Symbol}
   s/Keyword s/Any})

;;
;; Type Resolution
;;

(s/defn type-resolver [& types :- [s/Keyword]]
  (fn [meta]
    (reduce
      (fn [_ type]
        (if (or (some-> meta type true?) (some-> meta :type (= type)))
          (reduced (-> meta (assoc :type type) (dissoc type)))))
      nil types)))

(s/defn any-type-resolver [meta]
  (if (:type meta) meta))

(def default-type-resolver (type-resolver :handler))

;;
;; Exposing handler meta-data
;;

(defn stringify-schema [schema]
  (walk/prewalk
    (fn [x]
      (if-not (or (and (map? x) (not (record? x))) (vector? x) (string? x) (keyword? x) (nil? x))
        (pr-str x) x))
    schema))

; TODO: pass Schemas as-is -> implement https://github.com/metosin/web-schemas
(s/defn public-handler
  [handler :- Handler]
  (some-> handler
          (select-keys [:input :name :ns :output :source-map :type :action])
          (update-in [:input] stringify-schema)
          (update-in [:output] stringify-schema)))

;;
;; Collecting
;;

(defprotocol Collector
  (-collect [this type-resolver]))

(s/defn collect
  [collector type-resolver]
  (-collect collector type-resolver))

;;
;; Handlers
;;

(s/defn ^:private user-meta [meta :- KeywordMap]
  (dissoc
    meta
    ; reserved dispatcher handler stuff
    :type :input :output :description
    ; clojure var meta
    :line :column :file :name :ns :doc
    ; cloverage meta
    :end-line :end-column :idx
    ; plumbing details
    :schema :plumbing.fnk.impl/positional-info
    ; arglist
    :arglists))

(s/defn handler
  ([meta :- KeywordMap]
    (handler (dissoc meta :handle) (:handle meta)))
  ([meta :- KeywordMap, f :- Function]
    (assert (:name meta) "handler should have :name")
    (vary-meta f merge {:type :handler} meta)))

(defn handler? [x]
  (and (map? x) (:handle x) (:type x)))

;;
;; Namespaces
;;

(s/defrecord Namespace [name :- s/Keyword, meta :- KeywordMap]
  Collector
  (-collect [this _]
    this))

(s/defn namespace [meta :- KeywordMap]
  (->Namespace (:name meta) (dissoc meta :name)))

;;
;; Collection helpers
;;

(extend-type AFunction
  Collector
  (-collect [this type-resolver]
    (if-let [{:keys [name description type] :as meta} (type-resolver (meta this))]
      (let [{:keys [input output]} (kc/extract-schema this)]
        (if name
          {(namespace
             {:name (keyword name)})
           {:handle this
            :type type
            :name (keyword name)
            :meta (user-meta meta)
            :description (or description "")
            :input input
            :output output}}))
      (throw (ex-info (format "Function %s can't be type-resolved" this) {:target this})))))

(extend-type Var
  Collector
  (-collect [this type-resolver]
    (if-let [{:keys [line column file ns name doc type] :as meta} (type-resolver (meta this))]
      (let [{:keys [input output]} (kc/extract-schema this)]
        {(namespace
           {:name (keyword name)})
         {:handle @this
          :type type
          :name (keyword name)
          :meta (user-meta meta)
          :description doc
          :input input
          :output output
          :source-map {:line line
                       :column column
                       :file file
                       :ns (ns-name ns)
                       :name name}}})
      (throw (ex-info (format "Var %s can't be type-resolved" this) {:target this})))))

(extend-type Symbol
  Collector
  (-collect [this type-resolver]
    (require this)
    (some->> this
             ns-publics
             (map val)
             (filter #(type-resolver (meta %)))
             (map #(-collect % type-resolver))
             (apply merge))))

(extend-type Keyword
  Collector
  (-collect [this _]
    (namespace {:name this})))

(extend-type PersistentVector
  Collector
  (-collect [this type-resolver]
    (->> this
         (map #(-collect % type-resolver))
         (apply merge))))

(extend-type IPersistentMap
  Collector
  (-collect [this type-resolver]
    (p/for-map [[k v] this]
      (-collect k type-resolver) (-collect v type-resolver))))

;;
;; Dispatcher
;;

(s/defrecord Dispatcher
  [handlers :- {s/Keyword Handler}
   context :- KeywordMap
   coercion :- {:input (s/maybe KeywordMap)
                :output s/Any}
   meta :- KeywordMap])

(defmethod clojure.core/print-method Dispatcher
  [_ ^Writer writer]
  (.write writer "#<Dispatcher>"))

;;
;; Working with contexts
;;

(s/defn get-dispatcher [context :- Context]
  (get context ::dispatcher))

(s/defn get-handler [context :- Context]
  (get context ::handler))

(s/defn with-context [dispatcher :- Dispatcher, context :- Context]
  (update-in dispatcher [:context] kc/deep-merge context))

(s/defn context-copy
  "Returns a function that assocs in a value from to-kws path into from-kws in a context"
  [from :- [s/Any], to :- [s/Any]]
  (s/fn [context :- Context]
    (assoc-in context to (get-in context from {}))))

(s/defn context-dissoc [from-kws :- [s/Any]]
  "Returns a function that dissocs in a value from from-kws in a context"
  (s/fn [context :- Context]
    (kc/dissoc-in context from-kws)))

;;
;; coercion
;;

(def ^:private memoized-coercer (memoize sc/coercer))

(defn coerce! [schema matcher value in type]
  (let [coercer (memoized-coercer schema matcher)
        coerced (coercer value)]
    (if-not (su/error? coerced)
      coerced
      (throw
        (ex-info
          "Coercion error"
          {:type type
           :in in
           :value value
           :schema schema
           :error (su/error-val coerced)})))))

(defn coercion [data]
  (let [ks->coerce (into {} (pm/flatten data))]
    (fn [context schema]
      (reduce-kv
        (fn [ctx ks coerce]
          (if-let [coercion-schema (get-in schema ks)]
            (update-in ctx ks (partial coerce coercion-schema))
            ctx))
        context
        ks->coerce))))

(defn input-coerce!
  ([context schema]
   (if-let [dispatcher (get-dispatcher context)]
     (input-coerce! context schema (-> dispatcher :coercion :input))
     (throw (ex-info "no attached dispatcher." {}))))
  ([context schema key->matcher]
   (if-not (kc/any-map-schema? schema)
     (as-> context context
           (if-let [coercion (::coercion context)]
             (coercion context schema)
             context)
           (if key->matcher
             (reduce-kv
               (fn [ctx k matcher]
                 (let [schema (select-keys schema [k])
                       schema (if (seq schema) schema s/Any)]
                   (merge ctx (coerce! schema matcher (select-keys ctx [k]) nil ::request))))
               context
               key->matcher)
             context))
     context)))

;;
;; Interceptors
;;

(defn- initialize [context dispatcher handler mode]
  (let [ctx (assoc (kc/deep-merge (:context dispatcher) context)
              ::dispatcher dispatcher
              ::handler handler
              ::mode mode)]
    ctx))

(defn- with-input-schema [interceptor]
  (merge
    (kc/extract-schema (:enter interceptor) nil)
    interceptor))

(defn- with-input-coercion [interceptor]
  (if-let [input (:input interceptor)]
    (update interceptor :enter (fn [f]
                                 (fn [context]
                                   (let [dispatcher (::dispatcher context)
                                         input-matcher (-> dispatcher :coercion :input)]
                                     (f (input-coerce! context input input-matcher))))))
    interceptor))

(defn- with-string-name [interceptor]
  (if (:name interceptor)
    (update interceptor :name str)
    interceptor))

(defn interceptor [interceptor-or-a-function]
  (map->Interceptor
    (s/validate
      InterceptorLike
      (->
        (cond
          (fn? interceptor-or-a-function) {:enter interceptor-or-a-function}
          (map? interceptor-or-a-function) interceptor-or-a-function
          :else (throw (ex-info (str "Can't coerce into an interceptor: " interceptor-or-a-function) {})))
        with-input-schema
        with-input-coercion
        with-string-name))))

(defn interceptors [data]
  (assert (vector? data) "interceptors must be defined as a vector")
  (map
    (fn [x] (interceptor (if (vector? x) (apply (first x) (rest x)) x)))
    (keep identity data)))

;;
;; Dispatching to handlers
;;

(s/defn some-handler :- (s/maybe Handler)
  "Returns a handler or nil"
  [dispatcher, action :- s/Keyword]
  (get (:handlers dispatcher) action))

(defn- invalid-action! [action]
  (throw (ex-info (str "Invalid action: " action) {:type ::dispatch, :value action})))

(def ^:private validate-or-invoke? #{:validate :invoke})
(def ^:private invoke? (partial = :invoke))

(def ^:private execute-handler
  {:name ::handle
   :enter (fn [context]
            (let [{:keys [handle input output]} (::handler context)
                  mode (::mode context)
                  {{input-coercion :input, output-coercion :output} :coercion} (::dispatcher context)]
              (let [context (if (validate-or-invoke? mode)
                              (input-coerce! context input input-coercion) context)
                    response (if (invoke? mode)
                               (as-> (handle context) response
                                     (if (and output output-coercion)
                                       (coerce! output output-coercion response nil ::response)
                                       response)))]
                (assoc context :response response))))})

(defn dispatch [dispatcher mode action context]
  (if-let [{:keys [interceptors] :as handler} (some-handler dispatcher action)]
    (let [context (-> context
                      (initialize dispatcher handler mode)
                      (interceptor/enqueue interceptors)
                      (interceptor/execute))]
      (if (contains? context :response)
        (:response context)
        (invalid-action! action)))
    (invalid-action! action)))

(s/defn check
  "Checks an action handler with the given context."
  ([dispatcher :- Dispatcher, action :- s/Keyword]
    (dispatch dispatcher :check action {}))
  ([dispatcher :- Dispatcher, action :- s/Keyword, context :- Context]
    (dispatch dispatcher :check action context)))

(s/defn validate
  "Checks if context is valid for the handler (without calling the body).
  Returns nil or throws an exception."
  ([dispatcher :- Dispatcher, action :- s/Keyword]
    (dispatch dispatcher :validate action {}))
  ([dispatcher :- Dispatcher, action :- s/Keyword, context :- Context]
    (dispatch dispatcher :validate action context)))

(s/defn invoke
  "Invokes an action handler with the given context."
  ([dispatcher :- Dispatcher, action :- s/Keyword]
    (dispatch dispatcher :invoke action {}))
  ([dispatcher :- Dispatcher, action :- s/Keyword, context :- Context]
    (dispatch dispatcher :invoke action context)))

;;
;; Listing handlers
;;

(defn- filter-by-path [handlers path]
  (if-not path
    handlers
    (seq
      (filter
        (fn [{:keys [ns]}]
          (if ns
            (let [path-seq (str/split (subs (str path) 1) #"[\.]")
                  action-seq (str/split (subs (str ns) 1) #"[\.]")]
              (= path-seq (take (count path-seq) action-seq)))
            true))
        handlers))))

(defn- map-handlers [dispatcher mode prefix context success failure]
  (-> dispatcher
      :handlers
      vals
      (filter-by-path prefix)
      (->>
        (map
          (fn [handler]
            (try
              (when-not (= mode :all)
                (dispatch dispatcher mode (:action handler) context))
              [handler (success handler)]
              (catch Exception e
                (if (-> e ex-data :type (= ::dispatch))
                  [nil nil]
                  [handler (failure e)])))))
        (filter first)
        (into {}))))

(s/defn all-handlers :- [Handler]
  "Returns all handlers filtered by namespace"
  [dispatcher :- Dispatcher
   prefix :- (s/maybe s/Keyword)]
  (keep second (map-handlers dispatcher :all prefix {} identity (constantly nil))))

(s/defn available-handlers :- [Handler]
  "Returns all available handlers based on namespace and context"
  [dispatcher :- Dispatcher
   prefix :- (s/maybe s/Keyword)
   context :- Context]
  (keep first (map-handlers dispatcher :check prefix context identity (constantly nil))))

(s/defn dispatch-handlers :- {Handler s/Any}
  "Returns a map of action -> errors based on mode, namespace and context."
  [dispatcher :- Dispatcher
   mode :- (s/enum :check :validate)
   prefix :- (s/maybe s/Keyword)
   context :- Context]
  (map-handlers dispatcher mode prefix context (constantly nil) ex-data))

;;
;; Creating a Dispatcher
;;

(defn- extract-interceptors [meta metas]
  (reduce
    (fn [acc [k v]]
      (if-let [factory (get meta k)]
        (if-let [interceptors (seq (interceptors (kc/vectorize (factory v))))]
          (concat acc interceptors)
          acc)
        acc))
    []
    (apply concat metas)))

(defn- collect-and-enrich [{:keys [handlers type-resolver meta interceptors]}]
  (let [handler-ns (fn [m] (if (seq m) (->> m (map :name) (map name) (str/join ".") keyword)))
        collect-ns-meta (fn [m] (if (seq m) (->> m (map :meta) (filterv (complement empty?)))))
        handler-action (fn [n ns] (keyword (str/join "/" (map name (filter identity [ns n])))))
        reorder (fn [h m]
                  (if-let [invalid-keys (seq (set/difference (set (keys m)) (set (keys meta))))]
                    (throw (ex-info
                             "invalid meta-data on handler"
                             {:name (:name h)
                              :invalid-keys invalid-keys
                              :allowed-keys (keys meta)}))
                    (into
                      (linked/map)
                      (keep
                        (fn [k]
                          (if-let [v (m k)]
                            [k v]))
                        (keys meta)))))
        enrich (fn [h m]
                 (let [ns (handler-ns m)
                       action (handler-action (:name h) ns)
                       ns-meta (collect-ns-meta m)
                       user-meta (:meta h)
                       all-meta (map
                                  (partial reorder h)
                                  (if-not (empty? user-meta)
                                    (conj ns-meta user-meta)
                                    ns-meta))
                       interceptors (mapv
                                      interceptor
                                      (concat
                                        interceptors
                                        (extract-interceptors meta all-meta)
                                        [execute-handler]))
                       input (apply kc/merge-map-schemas (:input h) (keep :input interceptors))]

                   (merge h {:ns ns
                             :interceptors interceptors
                             :input (if (seq input) input s/Any)
                             :action action})))
        traverse (fn traverse [x m]
                   (flatten
                     (for [[k v] x]
                       (if (handler? v)
                         (enrich v m)
                         (traverse v (conj m k))))))]
    (-> handlers
        (collect type-resolver)
        (traverse [])
        (->> (group-by :action)
             (p/map-vals first)))))

;;
;; Public API
;;

(s/defschema Options
  {:handlers s/Any
   (s/optional-key :context) KeywordMap
   (s/optional-key :type-resolver) Function
   (s/optional-key :interceptors) [InterceptorLike]
   (s/optional-key :coercion) {(s/optional-key :input) (s/maybe KeywordMap)
                               (s/optional-key :output) s/Any}
   (s/optional-key :meta) (s/cond-pre [[(s/one s/Keyword 'key) Function]] KeywordMap)
   s/Keyword s/Any})

(s/def +default-options+ :- Options
  {:handlers {}
   :context {}
   :interceptors []
   :coercion {:input {:data (constantly nil)}
              :output (constantly nil)}
   :type-resolver default-type-resolver
   :meta {:interceptors interceptors
          :summary nil
          :description nil
          :no-doc nil}})

(s/defn dispatcher :- Dispatcher
  "Creates a Dispatcher"
  [options :- Options]
  (let [options (kc/deep-merge-map-like +default-options+ options)
        handlers (collect-and-enrich options)]
    (map->Dispatcher
      (merge
        (select-keys options [:context :coercion :meta])
        {:handlers handlers}))))

(s/defn transform-handlers
  "Applies f to all handlers. If the call returns nil,
  the handler is removed."
  [dispatcher :- Dispatcher, f :- Function]
  (update-in dispatcher [:handlers] (fn [handlers]
                                      (->> handlers
                                           (p/map-vals f)
                                           (filter (p/fn-> second))
                                           (into (empty handlers))))))

(s/defn inject
  "Injects handlers into an existing Dispatcher"
  [dispatcher :- Dispatcher, handlers :- (s/constrained s/Any (complement nil?) 'not-nil)]
  (if handlers
    (let [handler (collect-and-enrich
                    (merge dispatcher {:handlers handlers :type-resolver any-type-resolver}))]
      (update-in dispatcher [:handlers] merge handler))))
