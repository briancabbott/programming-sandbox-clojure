(ns ks-front-routing.ui.view.book-view
  (:require [ks-front-routing.ui.routing :as r]
            [ks-front-routing.ui.view.view :refer [render-view]]
            [ks-front-routing.books :as b]))

(defmethod render-view :book [{:keys [book-id]}]
  (let [{:keys [title authors]} (b/books book-id)]
    [:div.content
     [:h1 title]
     [:ul.authors
      (for [author authors]
        [:li {:key author} (b/authors author)])]
     [:a (r/href :books) [:fa.fa-arrow-left] " Back to books"]]))
