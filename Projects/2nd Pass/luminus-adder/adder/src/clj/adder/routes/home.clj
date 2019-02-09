(ns adder.routes.home
  (:require [adder.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]))

(defn home-page []
  (layout/render
    "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn about-page []
  (layout/render "about.html"))


; Page Handler for calculation functions...
(defn page-hdlr-calculate-addition
  []
  (println "Hello from page-hdlr-calculate-addition function...")
  )

; A Little Calculator function for Addition...
(defn calculate-addition 
  [number-one number-two]
  (+ number-one number-two))

(defroutes home-routes
  (GET "/" request (home-page request))
  (POST "/" request (page-hdlr-calculate-addition))
  (GET "/about" [] (about-page)))

