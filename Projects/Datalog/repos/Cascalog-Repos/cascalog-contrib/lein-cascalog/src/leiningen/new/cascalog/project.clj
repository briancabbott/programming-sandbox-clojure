(defproject {{name}} "0.1.0-SNAPSHOT"
  :description "TODO: Project Description."
  :source-path "src/clj"
  :jvm-opts ["-Xmx768m" "-server"]
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [cascalog "1.8.5"]]
  :dev-dependencies [[org.apache.hadoop/hadoop-core "0.20.2-dev"]
                     [midje-cascalog "0.3.1"]]) ;; for testing: http://goo.gl/EXyEV
