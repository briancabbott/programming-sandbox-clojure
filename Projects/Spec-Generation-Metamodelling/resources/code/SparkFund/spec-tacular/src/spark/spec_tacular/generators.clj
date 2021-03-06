(ns spark.spec-tacular.generators
  "Provides generators to be used in conjunction
  with [clojure.test.check.generators](https://github.com/clojure/test.check)"
  (:refer-clojure :exclude [assoc!])
  (:use spark.spec-tacular
        [spark.spec-tacular.datomic :exclude [db]])
  (:require [datomic.api :as db]
            [spark.spec-tacular.schema :as schema]
            [clj-time.core :as time]
            [clj-time.coerce :as timec]
            [clojure.test.check :as tc]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.clojure-test :as ct]))

(def ^{:doc "map of generators for primitive specs, by keyword name."
       :private true}
  prim-gens
  {:keyword gen/keyword
   :string  gen/string-ascii
   :boolean gen/boolean 
   :long    gen/int
   :bigint  (gen/fmap #(java.math.BigInteger. %) gen/int)
   :float   (gen/fmap #(float (/ % 100.0)) gen/int)
   :double  gen/double
   :bigdec  (gen/fmap #(/ (java.math.BigDecimal. %) 100.0M) gen/int)
   :instant (gen/fmap #(java.util.Date. %) gen/int)
   :uuid    (gen/fmap #(java.util.UUID/nameUUIDFromBytes %)
                      (gen/resize 10 gen/bytes))
   :uri     (gen/fmap #(java.net.URI. %) gen/string-alpha-numeric)
   :bytes   gen/bytes
   :ref     gen/simple-type-printable
   :calendarday
   (gen/fmap (fn [date]
               (clojure.core/let [dt (timec/from-date date)]
                 (time/date-time (time/year dt) (time/month dt) (time/day dt))))
             (gen/fmap #(java.util.Date. %) gen/int))
   })

(defmulti instance-generator
  "Returns a generator for the given spec.  Optional second argument
  can be used to override generators for the items in the spec, and
  should be a map from field names to generators."
  (fn [spec & rest] spec))

(defn- item-gen
  [{iname :name [cardinality type-key] :type required :required? unique? :unique?}
   overrides]
  (let [maybe-optionize (if required identity #(gen/one-of [(gen/return nil) %]))
        maybe-unique (if (and unique? (= type-key :string))
                       #(gen/bind % (fn [s] (gen/return (str (gensym) s))))
                       identity)]
    [iname (-> (or (get overrides iname) 
                   (let [generator (instance-generator type-key)]
                     (assert generator (str "missing definition of sub-generator: "
                                            type-key))
                     (-> (case cardinality
                           :one generator
                           :many (gen/bind (gen/vector generator)
                                   ;; distinct works for primitives but
                                   ;; not for spec instances for some reason
                                   (fn [coll] (gen/return (distinct coll)))))
                         maybe-unique
                         maybe-optionize))))]))

(defn- spec-gen [spec overrides & [pre]]
  (cond
    (:elements spec)
    ,(gen/one-of (map instance-generator (:elements spec)))
    (:items spec)
    ,(gen/bind (gen/frequency [[8 (gen/return true)] [2 (gen/return false)]])
       (fn [use-pre?]
         (if-let [insts (and use-pre? pre (get @pre (:name spec)))]
           (gen/return (rand-nth insts))
           (let [kvs (->> (:items spec)
                          (filter (fn [item]
                                    (or (not (= (get-spec (second (:type item))) spec))
                                        (not use-pre?)) ;; hijack use-pre? to stop infinite recursion
                                    ))
                          (map #(item-gen % overrides)))
                 mapgen (gen/fmap #(->> % (filter second) (into {}))
                                  (apply gen/hash-map (apply concat kvs)))
                 factory (get-ctor (:name spec))]
             (gen/bind (gen/fmap factory mapgen)
               (fn [sp]
                 (do (when (and pre sp)
                       (swap! pre update-in [(:name spec)] #(conj % sp)))
                     (gen/return sp))))))))
    (:values spec)
    (gen/one-of (map #(gen/return %) (:values spec)))))

(defmethod instance-generator :default
  [spec-name & [overrides]]
  (if (and (primitive? spec-name)
           (not (:values (get-spec spec-name))))
    (get prim-gens spec-name)
    (spec-gen (get-spec spec-name) overrides)))

(defn- spec-subset
  "generates a map from a generator but with just a subset of the keys.
   could be missing required fields"
  [sp-gen]
  (gen/bind sp-gen
    (fn [sp]
      (let [spec (get-spec sp)
            subset-gen (gen/vector gen/boolean (count (:items spec)) (count (:items spec)))]
        (gen/bind subset-gen
          (fn [subset]
            (let [keep (->> (map vector (:items spec) subset)
                            (filter second)
                            (map (comp :name first)))]
              (gen/return (into {} (map (fn [k] [k (get sp k)]) keep))))))))))

(defn- spec-children
  "Returns the set of spec names that need to be defined prior to this one.
  CAUTION this involves manually breaking any cycles in the spec dependency graph."
  [spec-key]
  (let [spec (get-spec spec-key)]
    (or (:elements spec)
        (->> (:items spec)
             (filter (fn [{iname :name [cardinality sub-sp-nm] :type}] true))
             (map #(-> % :type second))
             (set)))))

(defn- spec-dependencies
  "recursive dependencies for a collection of spec keys 
  (fixpoint, including originally supplied keys).
  Returns a set."
  [spec-keys]
  (let [next-set (apply clojure.set/union spec-keys (map spec-children spec-keys))]
    (if (= spec-keys next-set) next-set (recur next-set))))

(defn- mk-spec-generators
  "returns a map of keys->[env -> generator], including generators for all required deps.  
   Implementations in prim-gen-map (key->[env -> generator]) override auto-building
   of compound gens, but also provide gens for terminals like strings etc."
  [spec-key-set prim-gen-map & [pre]]
  (let [deps (spec-dependencies spec-key-set)]
    (into {} (map (fn [d]
                    [d (if (contains? prim-gen-map d)
                         (get prim-gen-map d)
                         #(spec-gen (get-spec d) % pre))])
                  deps))))

(defn ^:no-doc mk-spec-generator
  "Generates a default generator for the given key.
   Uses prim-gens to implement generators for primitive types. 
   ex: (last (gen/sample (mk-spec-generator :Contact) 1))"
  [spec-key & [pre]]
  (let [spec-gen-env (mk-spec-generators #{spec-key} prim-gens pre)]
    ((get spec-gen-env spec-key) spec-gen-env)))

(defn update-generator 
  "Returns a spec instance \"subset\" (intended to be smaller than
  generating an instance of `spec` with [[instance-generator]])
  suitable to use as an argument to [[assoc!]]."
  [spec]
  (if-let [spec-key (:name (get-spec spec))]
    (let [sp-gen (mk-spec-generator spec-key)]
      (spec-subset sp-gen))
    (throw (ex-info "Expecting spec object or name of spec" {:actual spec}))))

(defn ^:no-doc graph-generator [spec]
  (let [pre (atom {})
        sp-gen (mk-spec-generator (:name spec) pre)]
    (gen/bind (gen/such-that #(> (count %) 3) (gen/not-empty (gen/vector sp-gen)) 50)
      #(gen/return {:expected %}))))

(defn check-create! 
  "Checks that `original` can be created on the database."
  {:added "0.6.0"}
  [conn-ctx original]
  (let [actual (create! conn-ctx original)]
    (if (refless= actual original) actual
        (throw (ex-info "creation mismatch: output doesn't reflect input" 
                        {:actual actual :expected original})))))

(defn check-update!
  "Checks that `original` can be updated with `updates`, and the
  result should be a shallow merge of `original` into `updates`.

  Generate as suitable update map with [[update-generator]]."
  {:added "0.6.0"}
  [conn-ctx original updates]
  (let [expected (merge original updates)
        actual   (update! conn-ctx original updates)]
    (if (refless= expected actual) actual
        (throw (ex-info "update mismatch, output is not equivalent to input"
                        {:original original :updates updates :actual actual})))))

(defn prop-creation-update
  "Defines a property that tests whether instances of a given spec
  name can be created, updated, and sent to and from the database."
  {:added "0.6.0"}
  [conn-ctx-thunk spec-key]
  (let [spec     (get-spec spec-key)
        fields   (map :name (:items spec))]
    (prop/for-all [{:keys [conn-ctx original updates]}
                   (gen/bind (instance-generator spec-key)
                     (fn [sp]
                       (gen/bind (update-generator (get-spec sp))
                         (fn [updates]
                           (gen/return {:conn-ctx (conn-ctx-thunk)
                                        :original sp
                                        :updates updates})))))]
      (and (every? #(check-component! spec % (get original %)) fields)
           (when-let [created (check-create! conn-ctx original)]
             (or (= created :skip) (check-update! conn-ctx created updates)))))))

