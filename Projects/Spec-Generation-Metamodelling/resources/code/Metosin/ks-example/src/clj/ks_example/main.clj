(ns ks-example.main
  (:gen-class))

(defn -main [& args]
  (require 'ks-example.system)
  ((resolve 'ks-example.system/start-base-system) {})
  (println "Server ready"))
