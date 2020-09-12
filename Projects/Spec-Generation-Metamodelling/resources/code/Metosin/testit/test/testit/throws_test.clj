(ns testit.throws-test
  (:require [clojure.test :refer [deftest]]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [testit.core :refer [any ex-info? fact facts => =throws=>]]))

(deftest test-exceptions
  (fact "Match exception class"
    (/ 1 0) =throws=> java.lang.ArithmeticException)
  (fact "Match exception class and message"
    (/ 1 0) =throws=> (java.lang.ArithmeticException. "Divide by zero"))
  (fact "Match against predicate"
    (/ 1 0) =throws=> #(-> % .getMessage (str/starts-with? "Divide")))
  (let [ei (ex-info "oh no" {:reason "too lazy"})]
    (facts "Special helper for ex-info"
      (throw ei) =throws=> (ex-info? "oh no" {:reason "too lazy"})
      (throw ei) =throws=> (ex-info? string? {:reason "too lazy"})
      (throw ei) =throws=> (ex-info? string? {:reason string?})
      (throw ei) =throws=> (ex-info? any {:reason "too lazy"})
      (throw ei) =throws=> (ex-info? "oh no" any)
      (throw ei) =throws=> {:reason string?})))

;; FIXME: these tests are expected to fail, figure out how to test that they really do fail
#_(deftest test-exception-failures
  (let [ei (ex-info "oh no" {:reason "too lazy"})]
    (fact "Wrong message"
      (throw ei) =throws=> (ex-info? "oh noz" {:reason "too lazy"}))
    (fact "Data does not match"
      (throw ei) =throws=> (ex-info? "oh no" {:reason "too lazyz"}))))

(deftest test-excption-causes
  (fact
    (->> (java.lang.ArithmeticException. "3")
         (java.lang.RuntimeException. "2")
         (java.util.concurrent.ExecutionException. "1")
         (throw))
    =throws=> [(Exception. "1")
               (Exception. "2")
               (Exception. "3")])
  (fact
    (throw (ex-info "1" {:n 1 :a 42})) =throws=> [(ex-info? any {:n 1})])
  (fact
    (->> (ex-info "1" {:n 1 :a 42})
         (ex-info "2" {:n 2 :a 42})
         (ex-info "3" {:n 3 :a 42})
         (throw))
    =throws=> [(ex-info? "3" {:n 3})
               (ex-info? any any)
               (ex-info? any {:a 42})]))

;; For some reason (macroexpand '(...)) doesn't work with lein test
;; eval needs qualified macro name

(deftest fact-arity
  (facts
    (eval '(testit.core/fact 1 => 1))
    => nil?

    (eval '(testit.core/fact "foo" 1 => 1))
    => nil?

    (eval '(testit.core/fact 1 :bad))
    =throws=> [any (ex-info? any {::s/problems [{:path [:args :arrow]
                                                 :pred 'clojure.core/symbol?
                                                 :val :bad}]})]

    (eval '(testit.core/fact 1 =>))
    =throws=> [any (ex-info? any {::s/problems [{:path [:args :expected]
                                                 :reason "Insufficient input"}]})]

    (eval '(testit.core/fact 1))
    =throws=> [any (ex-info? any {::s/problems [{:path [:args :arrow]
                                                 :reason "Insufficient input"}]})]

    (eval '(testit.core/fact 1 => 1 :bad))
    =throws=> [any (ex-info? any {::s/problems [{:path [:args]
                                                 :reason "Extra input"}]})]

    (eval '(testit.core/fact "foo" 1 => 1 :bad))
    =throws=> [any (ex-info? any {::s/problems [{:path [:args]
                                                 :reason "Extra input"}]})]))

(deftest facts-arity
  (facts
    (eval '(testit.core/facts 1 => 1))
    => nil?

    (eval '(testit.core/facts "foo" 1 => 1))
    => nil?

    (eval '(testit.core/facts "foo" 1 => 1, 2 => 2))
    => nil?

    (eval '(testit.core/facts 1 => 1 :extra))
    =throws=> [any (ex-info? any {::s/problems [{:path [:args :body :arrow]
                                                 :reason "Insufficient input"}]})]

    (eval '(testit.core/facts 1 => 1, 2 =>))
    =throws=> [any (ex-info? any {::s/problems [{:path [:args :body :expected]
                                                 :reason "Insufficient input"}]})]))

(deftest facts-for-arity
  (facts
    (eval '(testit.core/facts-for 1, => 1, => number?))
    => nil?

    (eval '(testit.core/facts-for "foo" 1, => 1, => number?))
    => nil?

    (eval '(testit.core/facts-for "foo" 1, => 1, =>))
    =throws=> [any (ex-info? any {::s/problems [{:path [:args :fact-forms :expected]
                                                 :reason "Insufficient input"}]})]

    (eval '(testit.core/facts-for "foo" 1, => 1, 2 =>))
    =throws=> [any (ex-info? any {::s/problems [{:path [:args :fact-forms :arrow]
                                                 :pred 'clojure.core/symbol?
                                                 :val 2}]})]))
