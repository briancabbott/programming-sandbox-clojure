


(let [urls ["http://server.com/api/1" "http://server.com/api/2"
            "http://server.com/api/3"]
      ;; send the request concurrently (asynchronously)
      futures (doall (map http/get urls))]
  (doseq [resp futures]
    ;; wait for server response synchronously
    (println (-> @resp :opts :url) " status: " (:status @resp))
    )
