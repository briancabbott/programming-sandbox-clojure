(defproject metosin.forks/tools.namespace "0.3.0-SNAPSHOT"
  :description ""
  :parent [org.clojure/pom.contrib "0.1.2"]
  :url "https://github.com/clojure/tools.namespace"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :source-paths ["src/main/clojure"]
  :test-paths ["src/test/clojure"]
  :dependencies [[org.clojure/clojure "1.7.0" :scope "provided"]
                 [org.clojure/java.classpath "0.2.3"]
                 [org.clojure/tools.reader "0.10.0"]]
  :min-lein-version "2.0.0")
