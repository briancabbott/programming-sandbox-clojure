(ns {{name}}.system
  (:require [com.stuartsierra.component :as component]
            [palikka.components.http-kit :as http-kit]
            [{{name}}.handler :as handler]))

(defn new-system [config]
  (component/map->SystemMap
    {:state {:counter (atom 0)
             :file (atom nil)}
     :http (component/using
             (http-kit/create
               (:http config)
               {:fn
                (if (:dev-mode? config)
                  ; re-create handler on every request
                  (fn [system] #((handler/create system) %))
                  handler/create)})
             [:state])}))
