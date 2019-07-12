(defproject core-async-urls "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [
                 [org.clojure/clojure "1.10.1"]
                 [org.clojure/core.async "0.4.500"]

                 [clj-http "3.10.0"]
                 [http-kit "2.4.0-alpha3"]

                 ; [fullcontact/full.http "1.0.4"]
                 ; [fullcontact/camelsnake "0.9.1"]
                 [fullcontact-camelsnake "0.9.2-SNAPSHOT"]
                 [fullcontact-http "1.0.5-SNAPSHOT"]

                 [fullcontact-async "1.0.1-SNAPSHOT"]
                 [fullcontact-core "1.0.7"]
                 [fullcontact-json "0.11.1-SNAPSHOT"]
                 [asm/asm-all "2.2"]
                 [org.yaml/snakeyaml "1.17"]
                 [org.slf4j/slf4j-api "1.7.21"]
                 [org.slf4j/jul-to-slf4j "1.7.21"]
                 [joda-time "2.10.3"]
                 [com.fasterxml.jackson.core/jackson-core "2.9.9"]
                 [com.fasterxml.jackson.dataformat/jackson-dataformat-smile "2.9.9"]
                 [com.fasterxml.jackson.dataformat/jackson-dataformat-cbor "2.9.9"]
    ]
  :main ^:skip-aot core-async-urls.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
