(ns ks-front-routing.ui.view.common
  (:require [ks-front-routing.ui.view.view :refer [render-view]]))

(defmethod render-view :default [{:keys [page-id]}]
  [:div
   [:h1 (str "Page not found (" #_(name page-id) ")")]])
