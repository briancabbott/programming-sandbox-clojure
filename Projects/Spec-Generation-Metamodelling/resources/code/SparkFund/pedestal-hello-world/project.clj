(defproject hello-world-service "0.0.1-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [io.pedestal/pedestal.service "0.4.0-SNAPSHOT" #_"0.3.1"]
                 [ch.qos.logback/logback-classic "1.1.2" :exclusions [org.slf4j/slf4j-api]]
                 [org.slf4j/jul-to-slf4j "1.7.7"]
                 [org.slf4j/jcl-over-slf4j "1.7.7"]
                 [org.slf4j/log4j-over-slf4j "1.7.7"]]
  :deploy-repositories [["private" {:url "s3://maven-repo2/releases/"
                                    :username :env/aws_access_key
                                    :passphrase :env/aws_secret_key}]]
  :repositories [["private" {:url "s3://maven-repo2/releases/"
                             :username :env/aws_access_key
                             :passphrase :env/aws_secret_key}]]
  :min-lein-version "2.0.0"
  :resource-paths ["config", "resources"]
  :profiles {:dev {:aliases {"run-dev" ["trampoline" "run" "-m" "hello-world-service.server/run-dev"]
                             "jetty-runner" ["trampoline" "with-profile" "jetty-runner" "run"]} ; eg: lein jetty-runner target/hello-world.war
                   :dependencies [[io.pedestal/pedestal.service-tools "0.3.1"]
                                  [org.clojure/tools.namespace "0.2.6"] ;; Has to be specified otherwise excluding lein plugins will cause 0.1.3 to be picked by service-tools
                                  [io.pedestal/pedestal.jetty "0.3.1"]]}
             :jetty-runner {:dependencies ^:replace [[org.clojure/clojure "1.6.0"] ; Not sure why this needs to be here for just running a pure java jar? Get complaints if missing.
                                                     [org.eclipse.jetty/jetty-runner "9.2.10.v20150310" :exclusions [org.glassfish/javax.el]] ; version range used on javax.el
                                                     [org.glassfish/javax.el "3.0.0" :scope "test"]]
                            :main org.eclipse.jetty.runner.Runner}}
  :main ^{:skip-aot true} hello-world-service.server
  :plugins [[ohpauleez/lein-pedestal "0.1.0-beta10"]
            [pandect "0.5.1"]
            [lein-lock "0.1.0-SNAPSHOT"] ;; a private plugin has to be downloaded after lein-maven-s3-wagon? Not sure how to bootstrap!
            [lein-maven-s3-wagon "0.2.4"]]
  :pedestal {:server-ns hello-world-service.server
             :url-pattern "/*"})
