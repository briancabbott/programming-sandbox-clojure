(ns bootbook.components.http-kit
  (:require [org.httpkit.server :as http-kit]
            [com.stuartsierra.component :as component]))

(defrecord Http-kit [config handler http-kit]
  component/Lifecycle

  (start [this]
    (let [http-config   (get-in config [:config :http])
          ring-handler  (get-in handler [:handler])
          server        (http-kit/run-server ring-handler http-config)]
      (println "HTTP server started at port" (:port http-config))
      (assoc this :http-kit server)))

  (stop [this]
    (if http-kit
      (http-kit))
    (assoc this :http-kit nil)))

(defn create []
  (map->Http-kit {}))
