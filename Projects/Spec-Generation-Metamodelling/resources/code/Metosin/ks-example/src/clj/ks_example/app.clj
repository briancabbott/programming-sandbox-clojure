(ns ks-example.app
  (:require [ring.middleware.webjars :refer [wrap-webjars]]
            [compojure.route :as r]
            [ks-example.routes :refer [app-routes]]
            [ks-example.session :refer [wrap-session-middlewares]]))

(defn wrap-resources [handler resource-path opts]
  (let [resources (r/resources resource-path opts)]
    (fn [req]
      (or (resources req)
          (handler req)))))

(def app (-> #'app-routes
             (wrap-session-middlewares)
             (wrap-resources "/static" {:root "static"})
             (wrap-webjars)))
