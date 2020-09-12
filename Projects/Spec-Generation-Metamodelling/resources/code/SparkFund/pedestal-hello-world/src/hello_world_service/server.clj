(ns hello-world-service.server
  (:gen-class) ; for -main method in uberjar
  (:require [io.pedestal.http :as server]
            [hello-world-service.service :as service]))

;; This is an adapted service map, that can be started and stopped
;; From the REPL you can call server/start and server/stop on this service
#_(defonce runnable-service (server/create-server service/service))
(defn runnable-service [] (server/create-server service/service)) ;; for war testing - don't want to create a server

(defn run-dev
  "The entry-point for 'lein run-dev'"
  [& args]
  (println "\nCreating your [DEV] server...")
  (-> service/service ;; start with production configuration
      (merge {:env :dev
              ;; do not block thread that starts web server
              ::server/join? false
              ;; Routes can be a function that resolve routes,
              ;;  we can use this to set the routes to be reloadable
              ::server/routes #(deref #'service/routes)
              ;; all origins are allowed in dev mode
              ::server/allowed-origins {:creds true :allowed-origins (constantly true)}})
      ;; Wire up interceptor chains
      server/default-interceptors
      server/dev-interceptors
      server/create-server
      server/start))



(defn -main
  "The entry-point for 'lein run'"
  [& args]
  (println "\nCreating your server...")
  (server/start (runnable-service)))

#_
(defn servlet-init [this config]
  #_(server/init service/service)
  (server/servlet-init service/service config))

#_
(defn servlet-destroy [this]
  (server/servlet-destroy this))

#_
(defn servlet-service [this servlet-req servlet-resp]
  (server/servlet-service this servlet-req servlet-resp))


(defonce servlet (atom nil))

(defn servlet-init
  [_ _]
  (prn "Can I print here?")
  (reset! servlet (server/servlet-init service/service nil)) ; service is your "settings map".
  (prn "Got here and " @servlet))

(defn servlet-service
  [_ request response]
  (prn "Can I print here? in servlet?")
  (io.pedestal.http/servlet-service @servlet request response))

(defn servlet-destroy
  [_]
  (io.pedestal.http/servlet-destroy @servlet)
  (reset! servlet nil))
