(ns core-async-urls.full-contact-getter
  [:require
    [full.http.client :as fullcontact]
    [clojure.core.async :as async :refer :all]])

(defn get-content [ts-urls st-urls]
  (doall
    (for [url ts-urls]
      (do
        (try
          ; (prn (str "calling get for url: " url))

          (let [ts-xt (clojure.string/replace url "TX-URL: " "")
                st-xt (clojure.string/replace ts-xt "SENT-URL: " "")
                ts-url-content (fullcontact/req> {:url st-xt})
                resp (<!! (go (<! ts-url-content)))]
            ; (prn (str "returned (ts) content was: " ts-url-content))
            ; (prn (type ts-url-content))
            ; (prn (type resp))
            ; (prn "do I block?")
            ; (prn (str "we got our content back: " ))
            ; (prn "if I block, Im finished blocking now!")

            (condp = (type resp)
            clojure.lang.PersistentArrayMap (do (prn resp))
            clojure.lang.ExceptionInfo (prn "was exception")
            clojure.lang.LazySeq (doall (prn "was a ls") (pr-str resp))             ; My catch clause --
            org.httpkit.BytesInputStream (do (prn (slurp resp)) (prn "was a bis")) ; sentiment

            )
            )
          (catch Exception e (prn (str "OIFEIJEOJ Caught Exception: " (.getMessage e))))))
          )))


;; Perform the get-request first, entirely alone, then listen for the responses later, after all reqs have been made..
(def reqs (atom []))
(defn get-content-v2 [ts-urls st-urls]
  (doall
    (for [url ts-urls]
      (do
        (try
          ; (prn (str "calling get for url: " url))

          (let [ts-xt (clojure.string/replace url "TX-URL: " "")
                st-xt (clojure.string/replace ts-xt "SENT-URL: " "")
                ts-url-content (fullcontact/req> {:url st-xt})]
            (swap! reqs conj ts-url-content)
            ; (condp = (type resp)
            ; clojure.lang.PersistentArrayMap (do (prn resp))
            ; clojure.lang.ExceptionInfo (prn "was exception")
            ; clojure.lang.LazySeq (doall (prn "was a ls") (pr-str resp))             ; My catch clause --
            ; org.httpkit.BytesInputStream (do (prn (slurp resp)) (prn "was a bis")) ; sentiment
            ;
            ; )
            )
          (catch Exception e (prn (str "OIFEIJEOJ Caught Exception: " (.getMessage e))))))
          ))

  (doall (for [req @reqs]
    (do
      (try
      (let [resp (<!! (go (<! req )))]
        (condp = (type resp)
        clojure.lang.PersistentArrayMap (do (prn resp))                        ; Transcript data...
        clojure.lang.ExceptionInfo (prn "was exception")
        clojure.lang.LazySeq (doall (prn "was a ls") (pr-str resp))            ; My catch clause --
        org.httpkit.BytesInputStream (do (prn (slurp resp)) (prn "was a bis")) ; sentiment

        ))
        (catch Exception e (prn (str "Exception: " (.getMessage e)))))))))
