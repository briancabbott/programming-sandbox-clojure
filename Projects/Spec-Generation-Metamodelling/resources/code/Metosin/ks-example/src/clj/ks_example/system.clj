(ns ks-example.system
  (:require [com.stuartsierra.component :as component :refer [using]]
            [ks-example.components.env :as env]
            [ks-example.components.http-kit :as http-kit]
            [ks-example.components.context :as context]
            [ks-example.components.handler :as handler]
            [ks-example.components.db :as db]))

(def base {:db            (using (db/create) [:env])
           :context       (using (context/create) [:db :env])
           :handler       (using (handler/create 'ks-example.app/app) [:env :context])
           :http-server   (using (http-kit/create) [:handler :env])})

(defn base-system [config]
  (component/map->SystemMap (assoc base :env (env/create config))))

(defn start-base-system [& [config]]
  (component/start (base-system config)))
