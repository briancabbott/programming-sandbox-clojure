(ns bootbook.components.db
  (:require [com.stuartsierra.component :as component]
            [bootbook.components.context :as context]))

(defrecord Db [storage]
  component/Lifecycle
  (start [this] (assoc this :storage (atom {})))
  (stop [this]  (assoc this :storage nil))

  context/ContextProvider
  (provide-context [this] {:db storage}))

(defn create []
  (map->Db {}))
