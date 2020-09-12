(ns ks-example.components.context
  (:require [com.stuartsierra.component :as component]))

(defprotocol ContextProvider
  (provide-context [this]))

(defn context-provider? [component]
  (satisfies? ContextProvider component))

(defprotocol Context
  (extract-context [component]))

(defn wrap-context [handler context]
  (let [c (extract-context context)]
    (fn [req]
      (handler (assoc req :system/ctx c)))))

(defrecord ContextComoponent []
  component/Lifecycle
  (start [this]
    this)
  (stop [this]
    this)

  Context
  (extract-context [this]
    (->> (vals this)
         (filter context-provider?)
         (map provide-context)
         (apply merge))))

(defn create []
  (->ContextComoponent))
