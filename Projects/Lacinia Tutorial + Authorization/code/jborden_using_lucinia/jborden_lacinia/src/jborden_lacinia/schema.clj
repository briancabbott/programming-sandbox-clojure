;; (ns leaderboard-api.schema
(ns jborden-lacinia.schema
    (:require [jborden-lacinia.db :as db]
              [clojure.java.io :as io]
              [clojure.edn :as edn]
              [com.walmartlabs.lacinia.schema :as schema]
              [com.walmartlabs.lacinia.util :refer [attach-resolvers]]))
   
   (defn leaderboard-schema
    []
    (-> (io/resource "edn/leaderboard-schema.edn")
        slurp
        edn/read-string
        (attach-resolvers {:resolve-game db/resolve-game})
        schema/compile)