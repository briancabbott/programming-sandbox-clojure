(ns ^:figwheel-always ks-front-routing.ui.main
  (:require [reagent.core :as reagent]
            [goog.dom :as dom]
            [ks-front-routing.ui.routing :as r]
            [ks-front-routing.ui.view.nav-bar-view :refer [nav-bar]]
            [ks-front-routing.ui.view.view :refer [render-view]]
            [ks-front-routing.ui.view.views]))

(defn main-view []
  [:div
   [nav-bar]
   (render-view @r/route)])

(defn init! []
  (r/init-hook!)
  (reagent/render [main-view] (dom/getElement "app")))

(init!)
