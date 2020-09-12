(ns ks-front-routing.books
  (:require [clojure.string :as string]))

(def books (reduce (fn [acc {:keys [title] :as book}]
                     (let [book-id (-> title (string/lower-case) (string/replace #"\s+" "-"))]
                       (assoc acc book-id (assoc book :book-id book-id))))
                   {}
                   [{:title   "The Joy of Clojure"
                     :langs   #{:clojure}
                     :pages   328
                     :authors [:fogus :houser]}
                    {:title   "Erlang Programming"
                     :langs   #{:erlang}
                     :pages   470
                     :authors [:cesarini :thompson]}
                    {:title   "Clojure Data Analysis Cookbook"
                     :langs   #{:clojure}
                     :pages   326
                     :authors [:rochester]}
                    {:title   "Programming Concurrency on the JVM"
                     :langs   #{:java :ruby :groovy :scala :clojure}
                     :pages   270
                     :authors [:venkat]}
                    {:title   "The Little Schemer"
                     :langs   #{:scheme}
                     :pages   196
                     :authors [:friedman :felleisen :sussman]}
                    {:title   "Types and Programming Languages"
                     :langs   #{:haskel :java :fortran}
                     :pages   623
                     :authors [:pierce]}]))

(def authors {:fogus     "Michael Fogus"
              :houser    "Chris Houser"
              :cesarini  "Francesco Cesarini"
              :thompson  "Simon Thompson"
              :rochester "Eric Rochester"
              :venkat    "Venkat Subramaniam"
              :friedman  "Daniel P. Friedman"
              :felleisen "Matthias Felleisen"
              :pierce    "Benjamin C. Pierce"})
