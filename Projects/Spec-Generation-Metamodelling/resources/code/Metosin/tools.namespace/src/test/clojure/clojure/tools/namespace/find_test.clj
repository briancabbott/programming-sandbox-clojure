(ns clojure.tools.namespace.find-test
  (:require [clojure.test :refer [deftest is]]
            [clojure.java.io :as io]
            [clojure.tools.namespace.test-helpers :as help]
            [clojure.tools.namespace.find :as find])
  (:import (java.io File)))

(deftest t-find-clj-and-cljc-files
  "main.clj depends on one.cljc which depends on two.clj.
  two.cljs also exists but should not be returned"
  (let [dir (help/create-temp-dir "t-find-clj-and-cljc-files")
        main-clj (help/create-source dir 'example.main :clj '[example.one])
        one-cljc (help/create-source dir 'example.one :cljc '[example.two])
        two-clj (help/create-source dir 'example.two :clj)
        two-cljs (help/create-source dir 'example.two :cljs)]
    (is (help/same-files?
         [main-clj one-cljc two-clj]
         (find/find-sources-in-dir dir)))))

(deftest t-find-cljs-and-cljc-files
  "main.cljs depends on one.cljc which depends on two.cljs.
  two.clj also exists but should not be returned"
  (let [dir (help/create-temp-dir "t-find-cljs-and-cljc-files")
        main-cljs (help/create-source dir 'example.main :cljs '[example.one])
        one-cljc (help/create-source dir 'example.one :cljc '[example.two])
        two-clj (help/create-source dir 'example.two :clj)
        two-cljs (help/create-source dir 'example.two :cljs)]
    (is (help/same-files?
         [main-cljs one-cljc two-cljs]
         (find/find-sources-in-dir dir find/cljs)))))

(deftest t-invalid-ns-path
  "example.one namespace is defined in cljc file and can be found
  at two different paths on the classpath, in other case
  the path doesn't match namespace."
  (let [dir (help/create-temp-dir "t-invalid-ns-path")
        main-clj (help/create-source dir 'example.main :clj '[example.one])
        one-cljc (help/create-source dir 'example.one :cljc)
        one-cljc-copy (io/file dir "public/js/out/example/one.cljc")]
    (io/copy one-cljc (doto one-cljc-copy (io/make-parents)))

    (is (= [main-clj one-cljc] (find/find-sources-in-dir dir)))))
