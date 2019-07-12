(ns core-async-urls.core
  [:require
    [core-async-urls.target-urls :refer [urls]]
    [core-async-urls.clj-http-getter :as clj-http]
    [core-async-urls.http-kit-getter :as httpkit]
    [core-async-urls.full-contact-getter :as fullcontact]
    ]
  (:gen-class))




(defn -main
  "I don't do a whole lot ... yet."
  [& args]

  ; (time ;; CLJ-HTTP: "Elapsed time: 105638.052985 msecs"
  ;   (clj-http/get-content urls nil))

  ; (time ;; HTTP-KIT "Elapsed time: 39979.947609 msecs"
  ;   (httpkit/get-content urls nil))

  ; (time ;; 27917.242835 msecs
  ;   (fullcontact/get-content urls nil))

  ; (time ;; "Elapsed time: 6053.375303 msecs"
  ;   (fullcontact/get-content-v2 urls nil))
  )
