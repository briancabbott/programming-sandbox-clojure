(defproject guesthouse "0.1.0-SNAPSHOT"
  :description "Example guestbook project for demonstrating fnhouse"
  :url "https://github.com/plumatic/fnhouse/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[prismatic/plumbing "0.2.1"]
                 [prismatic/fnhouse "0.1.1"]
                 [org.clojure/clojure "1.5.1"]
                 [clj-http "0.9.0"]
                 [ring/ring-core "1.0.0-RC1"]
                 [ring/ring-jetty-adapter "1.0.0-RC1"]
                 [ring/ring-json "0.2.0"]])
