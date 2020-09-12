(ns ks-example.components.http-kit
  (:require [org.httpkit.server :as http-kit]
            [com.stuartsierra.component :as component]))

(defrecord Http-kit [env handler http-kit]
  component/Lifecycle
  (start [this]
    (let [config  (get-in env [:config :http])
          handler (get-in handler [:handler])]
      (println "Starting http-kit on port" (:port config))
      (assoc this :http-kit (http-kit/run-server handler config))))
  (stop [this]
    (when http-kit
      (http-kit))
    (assoc this :http-kit nil)))

(defn create []
  (map->Http-kit {}))
