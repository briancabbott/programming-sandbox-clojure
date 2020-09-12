(ns ks-front-routing.server.main
  (:gen-class))

(defn -main [& args]
  (println "Server starting...")
  (require 'ks-front-routing.server.server)
  ((resolve 'ks-front-routing.server.server/start-server))
  (println "Server ready"))
