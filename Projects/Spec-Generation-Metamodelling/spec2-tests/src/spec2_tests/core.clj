(ns spec2-tests.core
  (:require [clojure.spec-alpha2 :as s]
            [clojure.spec-alpha2.gen :as gen]
            [clojure.spec-alpha2.test :as test])
  (:gen-class))


;; Spec Defs
; "Return a fully-qualified form given a namespace name context and a form"
;  explicate

(s/def ::field-1 string?)
(s/def ::field-2 string?)
(s/def ::field-3 string?)
(s/def ::field-4 string?)
(s/def object-parent (s/keys :req [::field-1
                                   ::field-2
                                   ::field-3
                                   ::field-4]))
(s/explicate 'names.of.namespace :names.of.namespace/object-parent)

;; Spec Gens
(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
