(ns user
  (:require [reloaded.repl :refer [system init start stop go reset]]
            [monger.collection :as mc]
            [monger.operators :refer :all]))

(defn set-opts [& {:keys [system opts]
                   :or {system 'ks-example.system/base-system
                        opts {}}}]
  (reloaded.repl/set-init!
    (fn []
      (require (symbol (namespace system)))
      ((resolve system) opts))))

(set-opts)

(defn db [] (-> system :db :db))
