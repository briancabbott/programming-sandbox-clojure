(ns sparkfund.ui-test
  (:require
    [sparkfund.ui :as ui]
    [cljs.test :refer-macros [deftest testing is]]))

(deftest plus-one
  (testing "simple passing test"
    (is (= 1 (ui/plus-one 0))))
  (testing "deliberately failing case"
    (is (= 5 (ui/plus-one 1)))))
