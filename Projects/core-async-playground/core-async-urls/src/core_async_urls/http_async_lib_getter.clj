(ns core-async-urls.http-async-lib-getter
  [:require
    [clojure.core.async :refer [<!! go <! ]]
    [core-async-urls.http-async-lib :as http-async]])

(def url-resp-map (atom {}))

(defn get-content [ts-urls st-urls]
  (doall
    (for [url ts-urls]
      (do
        (try
          (prn (str "calling get for url: " url))

          (let [ts-xt (clojure.string/replace url "TX-URL: " "")
                st-xt (clojure.string/replace ts-xt "SENT-URL: " "")
                resp-chan (http-async/async-http-req> {:url st-xt})]
            (swap! url-resp-map assoc (keyword st-xt) resp-chan))
          (catch Exception e (prn (str "Caught Exception: " (.getMessage e)))))
          )))
  (prn (str "finished with initial requests (" (count ts-urls) ") requests made."))

  (doall
    (for [k (keys @url-resp-map)]
      (let [resp-val (<!! (go (<! (k @url-resp-map))))]
        (condp = (type resp-val)
        clojure.lang.PersistentArrayMap (do (prn "PersistentArrayMap")(prn resp-val))                         ; Transcript
        clojure.lang.ExceptionInfo (prn "was exception")
        clojure.lang.LazySeq (doall (prn "was a LazySeq") (pr-str resp-val))             ; My catch clause --
        org.httpkit.BytesInputStream (do (prn "was a BytesInputStream") (prn (slurp resp-val)) )  ; Sentiment
        )
        (prn (str "response for url: " k " was: " resp-val))))))
