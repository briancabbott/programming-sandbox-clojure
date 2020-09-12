(ns ks-example.components.db
  (:require [com.stuartsierra.component :as component]
            [monger.core :as m]
            [monger.collection :as mc]
            [monger.joda-time]
            [ks-example.components.context :as context]))

(defrecord Db [env conn db fs]
  component/Lifecycle
  (start [this]
    (let [config   (get-in env [:config :db])
          db-conn  (m/connect (:server config))
          db-api   (m/get-db db-conn (:db config))]
      (assoc this
             :conn  db-conn
             :db    db-api
             :fs    (m/get-gridfs db-conn (:db config)))))

  (stop [this]
    (if conn
      (m/disconnect conn))
    (assoc this :conn nil :db nil :fs nil))

  context/ContextProvider
  (provide-context [this]
    {:db  db
     :fs  fs}))

(defn create []
  (map->Db {}))
