(ns ks-front-routing.ui.view.books-view
  (:require [ks-front-routing.ui.routing :as r]
            [ks-front-routing.ui.view.view :refer [render-view]]
            [ks-front-routing.books :refer [books]]))

(defn book-view [book-id {:keys [title]}]
  [:li.book
   [:a (r/href :book {:book-id book-id}) title]])

(defmethod render-view :books []
  [:div.content
   [:h1 "Books"]
   (for [[book-id book] books]
     [book-view book-id book])])
