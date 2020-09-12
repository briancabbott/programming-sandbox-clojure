(ns ks-front-routing.ui.view.authors-view
  (:require [ks-front-routing.ui.routing :as r]
            [ks-front-routing.ui.view.view :refer [render-view]]
            [ks-front-routing.books :as b]))

(defmethod render-view :authors []
  [:div.content
   [:h1 "Authors"]
   (for [[id name] b/authors]
     [:div {:key id} name])])
