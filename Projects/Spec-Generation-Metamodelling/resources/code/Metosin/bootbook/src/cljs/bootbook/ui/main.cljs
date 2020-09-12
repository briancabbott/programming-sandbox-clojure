(ns bootbook.ui.main
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [clojure.string :as string]
            [reagent.core :as reagent :refer [atom]]
            [cljs.core.async :refer [<!] :as a]
            [cljs-http.client :as http]))


(defn nav-bar []
  [:div.nav-bar
   [:ul
    [:li [:a.brand {:href "#"}
          [:fa.fa.fa-book]
          " BootBook"]]]])

(defn search-books [search-value books]
  (go
    (->> {:query-params {:search search-value}}
         (http/get "/api/books")
         <!
         :body
         (reset! books))))

(defn search-form [books]
  (let [search-value (atom nil)]
    (fn []
      [:form.search {:on-submit (fn [e] (.preventDefault e) (search-books @search-value books))}
       [:input {:type        "text"
                :value       @search-value
                :on-change   (fn [e] (reset! search-value (-> e .-target .-value)))
                :autofocus   true
                :placeholder "Search..."}]])))


(defn book-view [{:keys [title langs authors]}]
  [:div.book {:key title}
   [:div
    [:span.title title]
    [:span.langs (for [lang langs]
                   [:span {:key lang} (-> lang name string/capitalize)])]]
   [:div.authos (string/join ", " (map (comp string/capitalize name) authors))]])

(defn book-list [books]
  [:div.books
   (if-let [book-list (seq @books)]
     (map book-view book-list))])

(defn main-view []
  (let [books (atom nil)]
    (fn []
      [:div
       [nav-bar]
       [search-form books]
       [book-list books]])))

(defn init! []
  (reagent/render [main-view] (js/document.getElementById "app")))

(init!)
