(ns vega-tools.validate-test
  (:require [vega-tools.validate :refer [check validate]]
            [cljs.test :refer-macros [deftest is testing]])
  (:require-macros [vega-tools.testing]))

(def background-error
  {:message "should be string"
   :data-path "/background"
   :schema-path "#/defs/background/type"
   :keyword "type"})

(deftest test-check
  (testing "Empty specification is fine."
    (is (nil? (check {}))))
  (testing "Wrong datatype is reported."
    ;; :background is specified to be a string.
    (let [result (check {:background 123})]
      (is (= (first result) background-error)))))

(deftest test-validate
  (is (= (validate {:background "red"}) {:background "red"}))
  (is (thrown-with-ex-data? {:errors [background-error]}
                            (validate {:background 123}))))
