(defproject metosin/sieppari "0.0.0-alpha13"
  :description "Small, fast, and complete interceptor library."
  :url "https://github.com/metosin/sieppari"
  :license {:name "Eclipse Public License", :url "https://www.eclipse.org/legal/epl-2.0/"}
  :deploy-repositories [["releases" :clojars]]
  :lein-release {:deploy-via :clojars}

  :dependencies []
  :test-paths ["test/clj" "test/cljs" "test/cljc"]

  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/clojure "1.10.1" :scope "provided"]
                                  [org.clojure/clojurescript "1.10.758"]
                                  ;; Add-ons:
                                  [org.clojure/core.async "1.2.603"]
                                  [manifold "0.1.8"]
                                  [funcool/promesa "5.1.0"]
                                  ;; Testing:
                                  [metosin/testit "0.4.0"]
                                  [lambdaisland/kaocha "1.0.632"]
                                  [lambdaisland/kaocha-cljs "0.0-71"]
                                  ;; Dev:
                                  [org.clojure/tools.namespace "1.0.0"]
                                  ;; Perf testing:
                                  [criterium "0.4.5"]
                                  [com.clojure-goes-fast/clj-async-profiler "0.5.0-SNAPSHOT"]
                                  [io.pedestal/pedestal.interceptor "0.5.7"]
                                  [org.slf4j/slf4j-nop "1.7.30"]]}

             ;; needed because of https://github.com/lambdaisland/kaocha-cljs#known-issues
             :test-cljs {:source-paths ["test/cljc" "test/cljs"]}
             :examples {:source-paths ["examples"]}
             :perf {:jvm-opts ^:replace ["-server" "-Xms4096m" "-Xmx4096m" "-Dclojure.compiler.direct-linking=true"]}}

  :aliases {"kaocha" ["with-profile" "+dev-deps,+test-cljs" "run" "-m" "kaocha.runner" "--reporter" "kaocha.report/documentation"]
            "perf" ["with-profile" "default,dev,examples,perf"]
            "perf-test" ["perf" "run" "-m" "example.perf-testing"]})
