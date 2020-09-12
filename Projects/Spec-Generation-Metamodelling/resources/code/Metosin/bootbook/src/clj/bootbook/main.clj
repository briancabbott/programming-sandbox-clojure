(ns bootbook.main
  (:gen-class))

(defn -main [& [port]]
  (require 'bootbook.system)
  ((resolve 'bootbook.system/start-base-system) (if port {:http {:port (Integer/parseInt port)}}))
  (println "Server ready"))
