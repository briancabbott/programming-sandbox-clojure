(ns bootbook.routes
  (:require [bidi.bidi]
            [bidi.ring]
            [bootbook.index :refer [index-response]]
            [bootbook.books :as books]))

(def routes ["" {"/"    {:get (constantly index-response)}
                 "/api" {"/books" books/routes}}])

(def handler (-> routes
                 bidi.bidi/compile-route
                 bidi.ring/make-handler))
