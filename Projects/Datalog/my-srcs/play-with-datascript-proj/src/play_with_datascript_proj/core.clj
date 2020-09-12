(ns play-with-datascript-proj.core
  (:require [datascript.core :as ds-core]
            [datascript.query :as ds-query]
            [datascript.parser :as ds-parse]
            )
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [conn (ds-core/create-conn)
        q '[:find ?ent
            :in $
            :where [?ent :entity/property]]
        q-res (ds-core/q q (ds-core/db conn))
        q-struct (ds-parse/parse-query q)
        ]
    (prn "q-res")
    (prn q-res)
    ; ds-query/)
    (prn )
    )
  (println "Hello, World!"))
