(ns example.throws-example
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [testit.core :refer :all]))

(deftest =throws=>-examples
  (fact "Match exception class"
    (/ 1 0) =throws=> java.lang.ArithmeticException)

  (fact "Match exception class and message"
    (/ 1 0) =throws=> (java.lang.ArithmeticException. "Divide by zero"))

  (fact "Match against predicate"
    (/ 1 0) =throws=> #(-> % .getMessage (str/starts-with? "Divide")))

  (fact "Match ex-info exceptions"
    (throw (ex-info "oh no" {:reason "too lazy"}))
    =throws=>
    (ex-info? "oh no" any))

  (let [e (ex-info "oh no" {:reason "too lazy"})]
    (facts
      (throw e) =throws=> (ex-info? "oh no" {:reason "too lazy"})
      (throw e) =throws=> (ex-info? string? {:reason "too lazy"})
      (throw e) =throws=> (ex-info? string? {:reason string?})
      (throw e) =throws=> (ex-info? any {:reason "too lazy"})
      (throw e) =throws=> (ex-info? "oh no" any)
      (throw e) =throws=> (ex-info? any any)))

  (fact
    (->> (java.lang.ArithmeticException. "3")
         (java.lang.RuntimeException. "2")
         (java.util.concurrent.ExecutionException. "1")
         (throw))
    =throws=> [(Exception. "1")
               (Exception. "2")
               (Exception. "3")]))
