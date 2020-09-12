(ns ks-example.foo-test
  (:require [clojure.test :refer [use-fixtures deftest is run-tests]]
            [org.httpkit.client :as http]
            [com.stuartsierra.component :as component]
            [ks-example.system :as system]
            [ks-example.util :as util :refer [POST]]))

(def base-sys (system/base-system {:http {:port 9000}}))
(use-fixtures :once (util/system-fixture base-sys))

(deftest test-post
  (is (-> (POST "/api/echo" {:hello "world"}) :body (= {:hello "world"}))))
