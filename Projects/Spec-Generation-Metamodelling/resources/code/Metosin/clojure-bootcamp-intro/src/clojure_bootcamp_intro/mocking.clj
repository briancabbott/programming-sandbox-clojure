(ns clojure-bootcamp-intro.mocking)

; Imaginary database API:

(def database {"tommi" {:name "Tommi"
                        :twitter "@ikitommi"}
               "jarppe" {:name "Jarppe"
                         :twitter "@jarppe"}})

(defn find-user-by-id [id]
  (println "DB: find-user-by-id:" id)
  ; TODO: fetch the user from 'database' 
  )

; Imaginary database REST API to test:

(defn get-user [request]
  (let [id    (get-in request [:params :id])
        user  (find-user-by-id id)]
    ; TODO: Implemet this so that:
    ;  if user is found, returns a map with:
    ;     :status  200
    ;     :body    user information
    ;  if user is not found, returns a map with:
    ;     :status  404
    ;     :body    Some error message
    ))
