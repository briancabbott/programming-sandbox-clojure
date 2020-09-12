(defproject ks-example "0.1.0-SNAPSHOT"
  :description "Example Clojure/ClojureScript app"

  :dependencies [; Common:
                 [org.clojure/clojure "1.6.0"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [prismatic/schema "0.4.0"]
                 [prismatic/plumbing "0.4.1"]
                 [metosin/potpuri "0.2.1"]

                 ; Server:
                 [http-kit "2.1.19"]
                 [ring/ring-core "1.3.2"]
                 [ring/ring-devel "1.3.2"]
                 [ring/ring-defaults "0.1.4"]
                 [metosin/compojure-api "0.19.0" :exclusions [ring org.clojure/clojurescript ch.qos.logback/logback-classic]]
                 [metosin/ring-http-response "0.6.1" :exclusions [ring/ring-core]]
                 [metosin/ring-swagger-ui "2.0.24"]
                 [ring-webjars "0.1.0" :exclusions [org.slf4j/slf4j-nop]]
                 [hiccup "1.0.5"]
                 [org.mindrot/jbcrypt "0.3m"]
                 [clj-time "0.9.0"]
                 [com.stuartsierra/component "0.2.3"]
                 [org.clojure/tools.nrepl "0.2.7"]

                 ; DB:
                 [com.novemberain/monger "2.1.0"]

                 ; Client:
                 [org.clojure/clojurescript "0.0-3123"]
                 [reagent "0.5.0"]
                 [com.domkm/silk "0.0.4"]
                 [cljs-http "0.1.27" :exclusions [com.cemerick/austin]]
                 [com.andrewmcveigh/cljs-time "0.3.2" :exclusions [com.cemerick/austin]]

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

  :source-paths ["src/clj" "target/generated/clj"]
  :test-paths ["test/clj" "test/cljx"]
  :resource-paths ["resources"]

  :uberjar-name "ks-example.jar"
  :auto-clean false

  :cljx {:builds [{:rules         :clj
                   :source-paths  ["src/cljx"]
                   :output-path   "target/generated/clj"}
                  {:rules         :cljs
                   :source-paths  ["src/cljx"]
                   :output-path   "target/generated/cljs"}]}

  :less {:source-paths  ["src/less"]
         :target-path   "target/generated/static/css"
         :source-map    true}

  :figwheel {:http-server-root  "static"
             :server-port       3450
             :css-dirs          ["target/generated/static/css"]
             :repl              false
             :server-logfile    "target/figwheel-logfile.log"}

  :cljsbuild {:builds {:dev {:source-paths ["src/cljs" "target/generated/cljs" "src/dev-cljs"]
                             :compiler {:main            "ks-example.ui.figwheel"
                                        :asset-path      "static/js/out"
                                        :output-to       "target/generated/static/js/ks-example.js"
                                        :output-dir      "target/generated/static/js/out"
                                        :preamble        ["reagent/react.js"]
                                        :source-map      true
                                        :optimizations   :none
                                        :cache-analysis  true
                                        :pretty-print    true}}
                       :adv {:source-paths ["src/cljs" "target/generated/cljs"]
                             :compiler {:main           "ks-example.ui.main"
                                        :output-to      "target/cljsbuild-adv/js/ks-example.js"
                                        :source-map     "target/cljsbuild-adv/js/ks-example.js.map"
                                        :output-dir     "target/cljsbuild-adv-out"
                                        :preamble       ["reagent/react.min.js"]
                                        :optimizations  :advanced
                                        :elide-asserts  true
                                        :pretty-print   false}}}}

  :profiles {:dev {:source-paths ["src/dev-clj"]
                   :dependencies [[com.cemerick/piggieback "0.1.5" :exclusions [org.clojure/clojurescript]]
                                  [figwheel "0.2.5"]
                                  [flare "0.2.8"]
                                  [org.clojure/tools.namespace "0.2.10"]
                                  [reloaded.repl "0.1.0"]]
                   :plugins [[lein-pdo "0.1.1"]
                             [lein-cljsbuild "1.0.5"]
                             [com.keminglabs/cljx "0.6.0" :exclusions [org.clojure/clojure com.cemerick/piggieback]]
                             [deraen/lein-less4j "0.2.1"]
                             [lein-figwheel "0.2.5" :exclusions [org.clojure/clojurescript]]
                             [com.cemerick/clojurescript.test "0.3.3"]]
                   :resource-paths ["target/generated"]
                   :injections [(require 'flare.clojure-test)
                                (flare.clojure-test/install!)]
                   :repl-options {:init-ns user}}
             :uberjar {:resource-paths  ["target/cljsbuild-adv"]
                       :less  {:compression true}
                       :main  ks-example.main
                       :aot   [ks-example.main]}}

  :aliases {"develop" ["do" "clean" ["cljx" "once"] ["pdo" ["cljx" "auto"] ["figwheel"] ["less4j" "auto"]]]
            "build" ["do" ["cljx" "once"] ["cljsbuild" "once" "adv"] ["less4j" "once"] "uberjar"]})
