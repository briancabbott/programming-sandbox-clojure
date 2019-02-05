(ns user
  (:require [adder.config :refer [env]]
            [clojure.spec.alpha :as s]
            [expound.alpha :as expound]
            [mount.core :as mount]
            [adder.core :refer [start-app]]))

(alter-var-root #'s/*explain-out* (constantly expound/printer))

(defn start []
  (mount/start-without #'adder.core/repl-server))

(defn stop []
  (mount/stop-except #'adder.core/repl-server))

(defn restart []
  (stop)
  (start))


