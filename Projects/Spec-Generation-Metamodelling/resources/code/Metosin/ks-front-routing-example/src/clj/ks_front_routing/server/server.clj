(ns ks-front-routing.server.server
  (:require [clojure.java.io :as io]
            [org.httpkit.server :as http-kit]
            [compojure.api.sweet :refer :all]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.http-response :refer [ok] :as resp]))

;;
;; Routes:
;;

(defapi api-routes
  (GET* "/" []
    (-> "index.html"
        (io/resource)
        (io/input-stream)
        (ok)
        (resp/content-type "text/html; charset=\"UTF-8\""))))

(def handler (-> #'api-routes
                 (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))))

;;
;; Server start/stop:
;;

(defonce server (atom nil))

(defn stop-server []
  (when-let [s @server]
    (s)
    (reset! server nil)))

(defn start-server []
  (stop-server)
  (reset! server (http-kit/run-server handler {:port 8080}))
  (println "Server listening port 8080..."))
