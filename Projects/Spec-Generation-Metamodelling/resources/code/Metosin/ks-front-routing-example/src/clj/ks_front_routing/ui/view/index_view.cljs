(ns ks-front-routing.ui.view.index-view
  (:require [ks-front-routing.ui.view.view :refer [render-view]]))

(defmethod render-view :index [_]
  [:div
   [:h1 "Index page"]])
