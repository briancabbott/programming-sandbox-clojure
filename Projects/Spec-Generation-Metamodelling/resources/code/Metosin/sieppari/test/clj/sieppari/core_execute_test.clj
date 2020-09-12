(ns sieppari.core-execute-test
  (:require [clojure.test :refer :all]
            [testit.core :refer :all]
            [sieppari.core :as s]
            [sieppari.context :as sc]
            [clojure.string :as str]))

;;
;; Following tests use a test-chain that has some interceptors
;; that fail on each stage function (enter, leave, error). The
;; idea is that the tests override the expected stage functions
;; with test specific function. This ensures that no unexpected
;; stage functions are called.
;;

; Make an interceptor with given name and set all stage functions
; to report unexpected invocation. Tests should override expected
; stages.

(defn unexpected [name stage]
  (fn [ctx]
    (throw (ex-info "unexpected invocation"
                    {:name name
                     :stage stage
                     :ctx ctx}))))

(defn make-test-interceptor [name]
  {:name name
   :enter (unexpected name :enter)
   :leave (unexpected name :leave)
   :error (unexpected name :error)})

; Test stack with three interceptors and a handler:

(def test-chain [(make-test-interceptor :a)
                 (make-test-interceptor :b)
                 (make-test-interceptor :c)
                 (unexpected :handler nil)])

(def a-index 0)
(def b-index 1)
(def c-index 2)
(def h-index 3)

;; Helper: always throws an exception with specific marker
;; in data part:

(def error (ex-info "oh no" {::error-marker true}))

(defn always-throw [ctx]
  (throw error))

;; Helper: return error handler function that ensures
;; that `ctx` contains an exception caused by `always-throw`,
;; clears the exception and sets response to given response:

(defn handle-error [overwrite-context]
  (fn [ctx]
    (assert (-> ctx :error ex-data (= {::error-marker true})))
    (-> ctx
        (dissoc :error)
        (merge overwrite-context))))

(defn handle-error-response [response]
  (fn [ctx]
    (assert (not (some? (:response ctx))))
    (assert (-> ctx :error ex-data (= {::error-marker true})))
    (-> ctx
        (dissoc :error)
        (assoc :response response))))

;;
;; Tests:
;;

;;`execute-context` Tests

(deftest execute-context-test
  (fact "enable all enter and leave stages, add `inc` interceptor"
    (-> test-chain
        (assoc-in [a-index :enter] identity)
        (assoc-in [b-index :enter] identity)
        (assoc-in [c-index :enter] identity)
        (assoc-in [h-index] {:enter (fn [ctx] (update ctx :data inc))})
        (assoc-in [c-index :leave] identity)
        (assoc-in [b-index :leave] identity)
        (assoc-in [a-index :leave] identity)
        (s/execute-context {:data 41}))
    => {:data 42}))

(deftest execute-context-enter-b-causes-exception-test
  (fact ":b causes an exception"
    (-> test-chain
        (assoc-in [a-index :enter] identity)
        (assoc-in [b-index :enter] always-throw)
        (assoc-in [b-index :error] identity)
        (assoc-in [a-index :error] identity)
        (s/execute-context {:data 41}))
    =throws=> error))

(deftest execute-context-enter-c-causes-exception-a-handles-test
  (fact ":c enter causes an exception, :b sees error, :a handles"
    (-> test-chain
        (assoc-in [a-index :enter] identity)
        (assoc-in [b-index :enter] identity)
        (assoc-in [c-index :enter] always-throw)
        (assoc-in [c-index :error] identity)
        (assoc-in [b-index :error] identity)
        (assoc-in [a-index :error] (handle-error {:data :fixed-by-a}))
        (s/execute-context {:data 41}))
    => {:data :fixed-by-a}))

(deftest execute-context-enter-c-causes-exception-b-handles-test
  (fact ":c enter causes an exception, :b handles"
    (-> test-chain
        (assoc-in [a-index :enter] identity)
        (assoc-in [b-index :enter] identity)
        (assoc-in [c-index :enter] always-throw)
        (assoc-in [c-index :error] identity)
        (assoc-in [b-index :error] (handle-error {:data :fixed-by-b}))
        (assoc-in [a-index :leave] identity)
        (s/execute-context {:data 41}))
    => {:data :fixed-by-b}))

(deftest execute-context-enter-b-sets-response-test
  (fact ":b sets the response, no invocation of :c nor :handler"
    (-> test-chain
        (assoc-in [a-index :enter] identity)
        (assoc-in [b-index :enter] (fn [ctx] (sc/terminate
                                               (assoc ctx :data :response-by-b))))
        (assoc-in [b-index :leave] identity)
        (assoc-in [a-index :leave] identity)
        (s/execute-context {:data 41}))
    => {:data :response-by-b}))

(deftest execute-context-empty-interceptors-test
  (facts "interceptor chain can be empty"
    (s/execute-context [] {}) => nil))

(defn make-logging-interceptor [name]
  {:name name
   :enter (fn [ctx]
            (update ctx :request conj [:enter name]))
   :leave (fn [ctx]
            (update ctx :response conj [:leave name]))})

(defn logging-handler [request]
  (conj request [:handler]))

(deftest execute-context-inject-interceptor-test
  (fact ":b injects interceptor :x to chain, ensure the order is correct"
    (-> [(make-logging-interceptor :a)
         {:enter (fn [ctx] (sc/inject ctx (make-logging-interceptor :x)))}
         (make-logging-interceptor :c)
         logging-handler]
        (s/execute-context {:request []})
        :response)
    => [[:enter :a]
        [:enter :x]
        [:enter :c]
        [:handler]
        [:leave :c]
        [:leave :x]
        [:leave :a]]))


;;`execute` Tests

(deftest happy-case-test
  (fact "enable all enter and leave stages, use `inc` as handler"
    (-> test-chain
        (assoc-in [a-index :enter] identity)
        (assoc-in [b-index :enter] identity)
        (assoc-in [c-index :enter] identity)
        (assoc-in [h-index] inc)
        (assoc-in [c-index :leave] identity)
        (assoc-in [b-index :leave] identity)
        (assoc-in [a-index :leave] identity)
        (s/execute 41))
    => 42))

(deftest enter-b-causes-exception-test
  (fact ":b causes an exception"
    (-> test-chain
        (assoc-in [a-index :enter] identity)
        (assoc-in [b-index :enter] always-throw)
        (assoc-in [b-index :error] identity)
        (assoc-in [a-index :error] identity)
        (s/execute 41))
    =throws=> error))

(deftest enter-c-causes-exception-a-handles-test
  (fact ":c enter causes an exception, :b sees error, :a handles"
    (-> test-chain
        (assoc-in [a-index :enter] identity)
        (assoc-in [b-index :enter] identity)
        (assoc-in [c-index :enter] always-throw)
        (assoc-in [c-index :error] identity)
        (assoc-in [b-index :error] identity)
        (assoc-in [a-index :error] (handle-error-response :fixed-by-a))
        (s/execute 41))
    => :fixed-by-a))

(deftest enter-c-causes-exception-b-handles-test
  (fact ":c enter causes an exception, :b handles"
    (-> test-chain
        (assoc-in [a-index :enter] identity)
        (assoc-in [b-index :enter] identity)
        (assoc-in [c-index :enter] always-throw)
        (assoc-in [c-index :error] identity)
        (assoc-in [b-index :error] (handle-error-response :fixed-by-b))
        (assoc-in [a-index :leave] identity)
        (s/execute 41))
    => :fixed-by-b))

(deftest handler-causes-exception-b-handles-test
  (fact
    (-> test-chain
        (assoc-in [a-index :enter] identity)
        (assoc-in [b-index :enter] identity)
        (assoc-in [c-index :enter] identity)
        (assoc-in [h-index] always-throw)
        (assoc-in [c-index :error] identity)
        (assoc-in [b-index :error] (handle-error-response :fixed-by-b))
        (assoc-in [a-index :leave] identity)
        (s/execute 41))
    => :fixed-by-b))

(deftest enter-b-sets-response-test
  (fact ":b sets the response, no invocation of :c nor :handler"
    (-> test-chain
        (assoc-in [a-index :enter] identity)
        (assoc-in [b-index :enter] (fn [ctx] (sc/terminate ctx :response-by-b)))
        (assoc-in [b-index :leave] identity)
        (assoc-in [a-index :leave] identity)
        (s/execute 41))
    => :response-by-b))

(deftest nil-response-test
  (fact "nil response is allowed"
    (s/execute [(constantly nil)] {})
    => nil))

(deftest empty-interceptors-test
  (facts "interceptor chain can be empty"
    (s/execute [] {}) => nil
    (s/execute nil {}) => nil))

(defn make-logging-interceptor [name]
  {:name name
   :enter (fn [ctx] (update ctx :request conj [:enter name]))
   :leave (fn [ctx] (update ctx :response conj [:leave name]))})

(defn logging-handler [request]
  (conj request [:handler]))

(deftest inject-interceptor-test
  (fact ":b injects interceptor :x to chain, ensure the order is correct"
    (-> [(make-logging-interceptor :a)
         {:enter (fn [ctx] (sc/inject ctx (make-logging-interceptor :x)))}
         (make-logging-interceptor :c)
         logging-handler]
        (s/execute []))
    => [[:enter :a]
        [:enter :x]
        [:enter :c]
        [:handler]
        [:leave :c]
        [:leave :x]
        [:leave :a]]))

; TODO: figure out how enqueue should work? Should enqueue add interceptors just
; before the handler?
#_(deftest enqueue-interceptor-test
    (fact ":b enqueues interceptor :x to chain, ensure the order is correct"
      (-> [(make-logging-interceptor :a)
           {:enter (fn [ctx] (sc/enqueue ctx (make-logging-interceptor :x)))}
           (make-logging-interceptor :c)
           logging-handler]
          (sc/into-interceptors)
          (s/execute []))
      => [[:enter :a]
          [:enter :c]
          [:enter :x]
          [:handler]
          [:leave :x]
          [:leave :c]
          [:leave :a]]))

(defrecord UnsupportedContext [])

(defn invalid-context-class-exception? [e]
  (-> e .getMessage (str/starts-with? "Unsupported Context on :enter")))

(deftest invalid-context-test

  (fact "fails on sync"
    (s/execute [{:enter map->UnsupportedContext}] {:x 40})
    =throws=> invalid-context-class-exception?)

  (testing "async"
    (let [on-error (promise)]
      (s/execute [{:enter map->UnsupportedContext}] {:x 40} ::irrelevant on-error)

      (fact "responds failure"
        @on-error => invalid-context-class-exception?))))
