(defproject clojure-bootcamp-intro "0.1.0-SNAPSHOT"
  :description "Metosin clojure-bootcamp intro"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :profiles {:dev {:dependencies [[midje "1.6.0"]]
                   :plugins [[lein-midje "2.0.1"]
                             [lein2-eclipse "2.0.0"]
                             [lein-idea "1.0.1"]]}})
