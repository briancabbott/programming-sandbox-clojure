(ns core-async-urls.http-kit-getter
  [:require [org.httpkit.client :as httpkit]])

(defn get-content [ts-urls st-urls]
  (doall
    (for [url ts-urls]
      (do
        (try
          (prn (str "calling get for url: " url))
          (let [ts-xt (clojure.string/replace url "TX-URL: " "")
                st-xt (clojure.string/replace ts-xt "SENT-URL: " "")
                ts-url-content (httpkit/get st-xt)]
            (prn (str "returned (ts) content was: " @ts-url-content)))
          (catch Exception e (prn (str "Caught Exception: " (.getMessage e))))))
          )))
