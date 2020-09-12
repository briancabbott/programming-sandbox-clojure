(defproject bootbook "0.0.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.7.0-beta1"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.clojure/tools.reader "0.9.1"]

                 ; Common libs:
                 [prismatic/schema "0.4.0"]
                 [prismatic/plumbing "0.4.2"]
                 [metosin/potpuri "0.2.2"]

                 ; Workflow:
                 [com.stuartsierra/component "0.2.3"]
                 [org.clojure/tools.nrepl "0.2.10"]

                 ; Server:
                 [http-kit "2.1.19"]
                 [ring/ring-core "1.3.2"]
                 [ring/ring-devel "1.3.2"]
                 [ring/ring-defaults "0.1.4"]
                 [bidi "1.18.10"]
                 [metosin/ring-http-response "0.6.1" :exclusions [ring/ring-core]]
                 [ring-webjars "0.1.0" :exclusions [org.slf4j/slf4j-nop]]
                 [metosin/ring-middleware-format "0.6.0"]
                 [hiccup "1.0.5"]
                 [clj-time "0.9.0"]

                 ; Client:
                 [org.clojure/clojurescript "0.0-3208"]
                 [reagent "0.5.0"]
                 [com.domkm/silk "0.0.4"]
                 [cljs-http "0.1.30" :exclusions [com.cemerick/austin]]

                 ; Assets
                 [org.webjars/bootstrap "3.3.2-1"]
                 [org.webjars/bootswatch-paper "3.3.1+2"]
                 [org.webjars/font-awesome "4.3.0-1"]
                 [org.webjars/es5-shim "4.0.6"]

                 ; Logging: use logback with slf4j, redirect JUL, JCL and Log4J:
                 [org.clojure/tools.logging "0.3.1"]
                 [ch.qos.logback/logback-classic "1.1.2"]
                 [org.slf4j/slf4j-api "1.7.10"]
                 [org.slf4j/jul-to-slf4j "1.7.10"]        ; JUL to SLF4J
                 [org.slf4j/jcl-over-slf4j "1.7.10"]      ; JCL to SLF4J
                 [org.slf4j/log4j-over-slf4j "1.7.10"]]   ; Log4j to SLF4J

  :source-paths ["src/clj" "src/cljs" "src/cljc"]
  :test-paths ["test/clj" "test/cljs" "test/cljc"]

  :profiles {:dev {:source-paths ["src/dev-clj"]
                   :dependencies [[figwheel "0.2.7" :exclusions [org.clojure/clojurescript]]
                                  [org.clojure/tools.namespace "0.2.10"]
                                  [reloaded.repl "0.1.0"]
                                  [ring-mock "0.1.5"]
                                  [flare "0.2.8"]]
                   :plugins [[lein-pdo "0.1.1"]
                             [lein-cljsbuild "1.0.5"]
                             [deraen/lein-less4j "0.2.1"]
                             [lein-figwheel "0.2.7" :exclusions [org.clojure/clojurescript]]
                             [com.cemerick/clojurescript.test "0.3.3"]
                             [lein2-eclipse "2.0.0" :exclusions [org.clojure/clojure]]]
                   :resource-paths ["target/generated"]
                   :injections [(require 'flare.clojure-test)
                                (flare.clojure-test/install!)]}
             :uberjar {:resource-paths  ["target/adv"]
                       :less  {:compression true
                               :target-path "target/adv/public/css"}
                       :main  bootbook.main
                       :aot   [bootbook.main]}}

  :less {:source-paths  ["src/less"]
         :target-path   "target/generated/public/css"
         :source-map    true}

  :figwheel {:http-server-root  "public"
             :server-port       3449
             :css-dirs          ["target/generated/public/css"]
             :repl              false
             :server-logfile    "target/figwheel-logfile.log"}

  :cljsbuild {:builds {:dev {:source-paths ["src/cljs" "src/cljc" "src/dev-cljs"]
                             :compiler {:main            "bootbook.ui.figwheel"
                                        :asset-path      "js/out"
                                        :output-to       "target/generated/public/js/bootbook.js"
                                        :output-dir      "target/generated/public/js/out"
                                        :source-map      true
                                        :optimizations   :none
                                        :cache-analysis  true
                                        :pretty-print    true}}
                       :adv {:source-paths ["src/cljs" "src/cljc"]
                             :compiler {:main           "bootbook.ui.main"
                                        :output-to      "target/adv/public/js/bootbook.js"
                                        :optimizations  :advanced
                                        :elide-asserts  true
                                        :pretty-print   false}}}}

  :uberjar-name      "bootbook.jar"
  :auto-clean        false
  :min-lein-version  "2.5.1"

  :aliases {"develop" ["do" "clean" ["pdo" ["figwheel"] ["less4j" "auto"]]]
            "uberjar" ["with-profile" "uberjar" "do" ["cljsbuild" "once" "adv"] ["less4j" "once"] "uberjar"]})
