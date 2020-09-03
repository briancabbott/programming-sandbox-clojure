(defproject spec2-tests "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}

  :plugins [[lein-git-deps "0.0.1-SNAPSHOT"]]
  :git-dependencies [["https://github.com/clojure/spec-alpha2"]]

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/test.check "0.9.0"]]

  ; :repositories [["spec-alpha2" {:url "https://github.com/clojure/spec-alpha2" d514b06b25c41a676b95afcc9bfac8ca34c5741e}]
  :main ^:skip-aot spec2-tests.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
