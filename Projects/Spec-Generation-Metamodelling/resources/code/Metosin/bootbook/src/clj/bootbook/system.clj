(ns bootbook.system
  (:require [com.stuartsierra.component :as component :refer [using]]
            [bootbook.components.config :as config]
            [bootbook.components.http-kit :as http-kit]
            [bootbook.components.context :as context]
            [bootbook.components.handler :as handler]
            [bootbook.components.db :as db]))

(def base {:db            (db/create)
           :context       (using (context/create) [:config :db])
           :handler       (using (handler/create 'bootbook.handler/handler) [:config :context])
           :http-server   (using (http-kit/create) [:config :handler])})

(defn base-system [config]
  (component/map->SystemMap (assoc base :config (config/create config))))

(defn start-base-system [& [config]]
  (component/start (base-system config)))
