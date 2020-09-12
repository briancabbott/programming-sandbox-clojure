(ns bootbook.handler
  (:require [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.webjars :refer [wrap-webjars]]
            [ring.middleware.format :refer [wrap-restful-format]]
            [bootbook.routes :as routes]))

(defn wrap-middlewares [routes]
  (-> routes
      (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
      (wrap-restful-format :formats [:edn :json-kw])
      (wrap-webjars)))

(def handler (wrap-middlewares #'routes/handler))
