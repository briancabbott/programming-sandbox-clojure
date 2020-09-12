(defproject ks-front-routing "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.7.0-beta2"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.clojure/tools.reader "0.9.2"]

                 [http-kit "2.1.19"]
                 [ring/ring-core "1.3.2"]
                 [ring/ring-defaults "0.1.5"]
                 [metosin/compojure-api "0.20.1"]
                 [metosin/ring-http-response "0.6.1"]
                 [hiccup "1.0.5"]

                 ; Client:
                 [org.clojure/clojurescript "0.0-3269"]
                 [reagent "0.5.0"]
                 [com.domkm/silk "0.0.4"]
                 [cljs-http "0.1.30"]]

  :source-paths ["src/clj"]
  :test-paths ["test/clj"]

  :plugins [[lein-pdo "0.1.1"]
            [lein-cljsbuild "1.0.5"]
            [deraen/lein-less4j "0.2.1"]
            [lein-figwheel "0.3.1" :exclusions [org.clojure/clojurescript]]]

  :profiles {:dev     {:resource-paths ["target/dev"]}
             :uberjar {:resource-paths ["target/adv"]
                       :less           {:compression true
                                        :target-path "target/adv/public/css"}
                       :main           ks-front-routing.server.main
                       :aot            [ks-front-routing.server.main]}}

  :figwheel {:http-server-root "public"
             :server-port      3449
             :css-dirs         ["target/dev/public/css"]
             :repl             false
             :server-logfile   "target/figwheel-logfile.log"}

  :less {:source-paths ["src/less"]
         :target-path  "target/dev/public/css"
         :source-map   true}

  :cljsbuild {:builds {:dev {:source-paths ["src/clj"]
                             :figwheel     true
                             :compiler     {:main                 ks-front-routing.ui.main
                                            :asset-path           "js/out"
                                            :output-to            "target/dev/public/js/app.js"
                                            :output-dir           "target/dev/public/js/out"
                                            :source-map           true
                                            :source-map-timestamp true
                                            :optimizations        :none
                                            :cache-analysis       true
                                            :pretty-print         true}}
                       :adv {:compiler {:main          ks-front-routing.ui.main
                                        :output-to     "target/adv/public/js/app.js"
                                        :optimizations :advanced
                                        :elide-asserts true
                                        :pretty-print  false}}}}

  :uberjar-name "ks-front-routing.jar"
  :auto-clean false
  :min-lein-version "2.5.1"

  :aliases {"develop" ["do" "clean" ["pdo" ["less4j" "auto"] ["figwheel"]]]
            "build"   ["with-profile" "uberjar" "do" "clean" ["less4j" "once"] ["cljsbuild" "once" "adv"] "uberjar"]})
