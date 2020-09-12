(ns ks-front-routing.ui.routing
  (:require [reagent.core :as reagent]
            [clojure.set :refer [rename-keys]]
            [domkm.silk :as silk]))

;;
;; Current route:
;;

(defonce route (reagent/atom nil))

;;
;; Known routes:
;;

(def routes
  (silk/routes [[:index    [[]]]
                [:books    [["books"]]]
                [:book     [["book" :book-id]]]
                [:authors  [["authors"]]]
                [:settings [["settings"]]]]))


(defn update-route!
  "Update the contents of the route atom to the route of current URL."
  [_]
  (reset! route (-> (silk/arrive routes (subs js/location.hash 1))
                    (rename-keys {:domkm.silk/name :page-id})
                    (or {:page-id :404}))))

(defn url
  "Return an URL for given page-id and optional page params"
  ([page-id]
   (url page-id nil))
  ([page-id params]
   (str "#" (silk/depart routes page-id params))))

(defn href
  "Helpper to create a attribute for anchor element with a href refering to
   given page-id and optional params"
  ([page-id]
   (href page-id nil))
  ([page-id params]
   {:href (url page-id params)}))

(defn goto!
  "Set URL to represent the given page-id and optional page params"
  ([page-id]
   (goto! page-id nil))
  ([page-id params]
   (set! js/window.location (url page-id params))))

(defn goto-handler
  "Returns a function that can be used as a event handler. Function prevents the
  default action and then sets the URl to represent given page-id and optional
  page params"
  ([page-id]
   (goto-handler page-id nil))
  ([page-id params]
   (fn [e]
     (.preventDefault e)
     (goto! page-id params))))

(defn init-hook!
  "Register listener to update the route atom when URL changes"
  []
  (set! js/window.onhashchange update-route!)
  (update-route! nil))
