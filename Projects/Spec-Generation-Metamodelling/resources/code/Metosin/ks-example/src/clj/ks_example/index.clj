(ns ks-example.index
  (:require [clojure.java.io :as io]
            [hiccup.core :refer [html]]
            [hiccup.page :refer [html5 include-css include-js]])
  (:import [org.apache.commons.codec.digest DigestUtils]))

(defn resource-chksum [resource-name]
  (some-> resource-name
          (io/resource)
          (io/input-stream)
          (DigestUtils/md5Hex)
          (.substring 24)))

(defn chksum-in-query [resource-name]
  (str resource-name "?_=" (resource-chksum resource-name)))

(def index-page
  (html5
    [:head
     [:title "KS Example"]
     [:meta {:charset "utf-8"}]
     [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge"}]
     [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
     (include-css (chksum-in-query "static/css/ks-example.css"))]
    [:body
     [:div#app]
     "<!--[if lt IE 10]>"
     (include-js "static/shim/es5-shim.js" "static/shim/es5-sham.js")
     "<![endif]-->"
     (include-js (chksum-in-query "static/js/ks-example.js"))]))
