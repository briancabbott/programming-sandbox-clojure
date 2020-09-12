(ns ks-example.util
  (:require [org.httpkit.client :as http]
            [com.stuartsierra.component :as component]
            [ks-example.system]))

(defonce system nil)

(defn POST [url & [body]]
  (let [port      (get-in system [:env :config :http :port])
        full-url  (format "http://127.0.0.1:%d%s" port url)
        headers   {"accept" "application/edn"}
        opts      {:form-params  body
                   :headers      headers}
        resp      @(http/post full-url opts)]
    (assoc resp :body (some-> resp :body slurp read-string))))

(defn system-fixture [base-system]
  (alter-var-root #'system (constantly base-system))
  (fn [f]
    (alter-var-root #'system component/stop)
    (alter-var-root #'system component/start)
    (try
      (f)
      (finally
        (alter-var-root #'system component/stop)))))
