(ns user
  (:require [prime-factorization.config :refer [env]]
            [clojure.spec.alpha :as s]
            [expound.alpha :as expound]
            [mount.core :as mount]
            [prime-factorization.core :refer [start-app]]))

(alter-var-root #'s/*explain-out* (constantly expound/printer))

(defn start []
  (mount/start-without #'prime-factorization.core/repl-server))

(defn stop []
  (mount/stop-except #'prime-factorization.core/repl-server))

(defn restart []
  (stop)
  (start))


