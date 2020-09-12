(task-options!
  pom {:project     'sparkfund/cljs-test-problem
       :version     "0.0.1-DEMO"
       :description "Demonstrates a problem with boot-cljs-test"})

;begin our actual Boot project
(set-env!
  :source-paths   #{"src/", "test/"}
  :resource-paths #{}
  :asset-paths    #{}
  :dependencies
  '[[boot/core "2.7.1"]
    [org.clojure/clojure "1.9.0-alpha16"]
    [org.clojure/clojurescript "1.9.542"]
    [crisptrutski/boot-cljs-test "0.3.0" :scope "test"]])

(require '[crisptrutski.boot-cljs-test :refer [test-cljs]])
(task-options! test-cljs {:exit? true, :verbosity 3, :keep-errors? true})

(deftask test-in-phantom
  "Prerequisites: you've installed phantomjs"
  []
  (test-cljs :js-env :phantom))

(deftask test-in-chrome
  "Prerequisites: npm install --global karma karma-chrome-launcher karma-cljs-test"
  []
  (test-cljs :js-env :chrome))

(deftask test-in-firefox
  "Prerequisites: npm install --global karma karma-chrome-launcher karma-cljs-test"
  []
  (test-cljs :js-env :firefox))
