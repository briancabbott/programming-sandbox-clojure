; (ns clojure-game-geek.schema
;     "Contains custom resolvers and a function to provide the schema."
;     (:require 
;         [clojure.java.io :as io] 
;         [com.walmartlabs.lacinia.util :as util] 
;         [com.walmartlabs.lacinia.schema :as schema] 
;         [clojure.edn :as edn]))

; (defn resolver-map
;     []
;     (prn "Just an empty resolver for now!!!")
;     {:query/game-by-id (fn [context args value] 
;                          nil)})

; (defn load-schema 
;     []
;     (-> (io/resource "cgg-schema.edn")
;         slurp
;         edn/read-string
;         (util/attach-resolvers (resolver-map))
;         schema/compile))