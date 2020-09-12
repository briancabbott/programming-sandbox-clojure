(ns bootbook.components.config
  (:require [clojure.java.io :as io]
            [com.stuartsierra.component :as component]
            [bootbook.components.context :as context]
            [potpuri.core :refer [deep-merge]]))

(defn load-config []
  (-> "config.edn" io/resource slurp read-string))

(defrecord Config [init-config config]
  component/Lifecycle
  (start [this]
    (assoc this :config (deep-merge (load-config) (or init-config {}))))
  (stop [this]
    (assoc this :config nil))

  context/ContextProvider
  (provide-context [this]
    {:config config}))

(defn create [init-config]
  (map->Config {:init-config init-config}))
