(ns user)

(def main 'ks-front-routing.server.main/-main)

(defn go []
 (-> main namespace symbol require)
 ((resolve main)))
