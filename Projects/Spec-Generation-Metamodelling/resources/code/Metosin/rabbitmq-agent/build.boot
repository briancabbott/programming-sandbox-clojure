#!/usr/bin/env boot
(set-env!
 :resource-paths #{"src"}
 :dependencies '[[org.clojure/clojure "1.8.0" :scope "provided"]
                 [org.clojure/tools.logging "0.3.1"]
                 [com.novemberain/langohr "3.5.1"]
                 [newrelic-platform/metrics-publish "2.0.1"]])

(def version "1.0.0")

(deftask build
  "Builds an uberjar of this project that can be run with java -jar"
  []
  (comp
    (aot :namespace '#{rabbitmq-agent.core})
    (pom :project 'metosin/rabbitmq-agent
         :version version)
    (uber)
    (jar :main 'rabbitmq-agent.core :file (format "rabbitmq-agent-v%s.jar" version))))
