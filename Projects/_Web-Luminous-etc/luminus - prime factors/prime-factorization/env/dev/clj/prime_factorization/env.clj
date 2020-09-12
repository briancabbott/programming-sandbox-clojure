(ns prime-factorization.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [prime-factorization.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[prime-factorization started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[prime-factorization has shut down successfully]=-"))
   :middleware wrap-dev})
