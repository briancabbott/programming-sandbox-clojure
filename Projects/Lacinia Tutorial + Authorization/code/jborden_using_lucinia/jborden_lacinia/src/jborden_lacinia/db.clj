(ns jborden-lacinia.db
    (:require [clojure.data.json :as json]
              [environ.core :refer [env]]
              [leaderboard-api.core :as core]
              [yesql.core :refer [defqueries]]))
  
  ;; still need to put a password in for this
  ;; need to be sure the database is password protected!
  (def db-spec {:classname "org.postgresql.Driver"
                :subprotocol "postgresql"
                :subname (str "//"
                              (or (:db-host env)
                                  (System/getenv "OPENSHIFT_PG_HOST"))
                              ":"
                              (or (:db-port env)
                                  (System/getenv "OPENSHIFT_PG_PORT"))
                              "/"
                              (or (:db-name env)
                                  (System/getenv "OPENSHIFT_PG_DATABASE")))
                :user (or (:db-username env)
                          (System/getenv "OPENSHIFT_PG_USERNAME"))
                :password (or (:db-password env)
                              (System/getenv "OPENSHIFT_PG_PASSWORD"))})
  
  (defqueries "sql/operations.sql"
    {:connection db-spec})
  
  ;; see: https://gist.github.com/alexpw/2166820
  (defmacro check-error
    "Usage: (check-error (create-developer! (core/new-developer \"foo@bar.com\")))"
    [body]
    `(try ~body (catch Exception e# (throw (Exception.(:cause (Throwable->map (.getNextException e#))))))))
  
  (defn resolve-game
    [context args _value]
    (let [developer (:authorization @(:cache context))]
      (first
       (check-error (get-game (assoc args :developer developer))))))
       