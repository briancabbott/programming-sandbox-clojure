(ns core-async-urls.clj-http-getter
  [:require [clj-http.client :as http-client]])

;; Perform Get Operations using the "standard" or commonly used clj-http

(defn get-content [ts-urls st-urls]
  (doall
    (for [url ts-urls]
      (do
        (try
          (prn (str "calling get for url: " url))

          (let [ts-xt (clojure.string/replace url "TX-URL: " "")
                st-xt (clojure.string/replace ts-xt "SENT-URL: " "")
                ts-url-content (http-client/get st-xt)]
            (prn (str "returned (ts) content was: " ts-url-content)))
          (catch Exception e (prn (str "Caught Exception: " (.getMessage e))))))
          )))
