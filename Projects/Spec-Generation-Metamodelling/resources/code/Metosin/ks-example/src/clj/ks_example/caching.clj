(ns ks-example.caching)

(def no-cache     "max-age=0,no-cache,no-store")
(def cache-30d    "public,max-age=2592000,s-maxage=2592000")

(defn add-cache-header [response value]
  (if response
    (assoc-in response [:headers "cache-control"] value)))

(defn cache-middleware [handler value]
  (fn [request]
    (-> request handler (add-cache-header value))))
