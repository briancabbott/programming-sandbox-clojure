(ns prime-factorization.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[prime-factorization started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[prime-factorization has shut down successfully]=-"))
   :middleware identity})
