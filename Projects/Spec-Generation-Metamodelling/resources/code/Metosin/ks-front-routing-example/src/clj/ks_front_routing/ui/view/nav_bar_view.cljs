(ns ks-front-routing.ui.view.nav-bar-view
  (:require [clojure.string :as string]
            [ks-front-routing.ui.routing :as r]))

(defn a-attrs [page-id active]
  {:class (if (= page-id active) "active")
   :href  (r/url page-id)})

(defn nav-bar []
  (let [active (:page-id @r/route)]
    [:div.nav-bar
     [:ul
      [:li [:a.brand (a-attrs :index active) [:i.fa.fa-gift] " Brand"]]
      (for [n [:books :authors :settings]]
        [:li {:key n} [:a (a-attrs n active) (-> n name string/capitalize)]])]]))
