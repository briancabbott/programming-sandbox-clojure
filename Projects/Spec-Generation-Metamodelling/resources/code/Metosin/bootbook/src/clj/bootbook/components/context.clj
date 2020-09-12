(ns bootbook.components.context
  (:require [com.stuartsierra.component :as component]))

(defprotocol ContextProvider
  (provide-context [this]))

(defn context-provider? [component]
  (satisfies? ContextProvider component))

(defprotocol Context
  (extract-context [component]))

(defrecord ContextComoponent []
  component/Lifecycle
  (start [this] this)
  (stop [this] this)

  Context
  (extract-context [this]
    (->> (vals this)
         (filter context-provider?)
         (map provide-context)
         (apply merge))))

(defn create []
  (->ContextComoponent))

(defn wrap-context
  "Ring middleware that adds context to requests"
  [handler context]
  (let [c (extract-context context)]
    (fn [req]
      (handler (assoc req :system/ctx c)))))

