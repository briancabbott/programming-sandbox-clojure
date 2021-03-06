(ns schema-tools.walk
  "Provides walk function which can be used to transform schemas while
  preserving their structure and type."
  (:require [schema.core :as s])
  #?(:clj (:import [java.util Map$Entry])))

(defprotocol WalkableSchema
  (-walk [this inner outer]))

(defn- schema-record?
  "Tests if the parameter is Schema record. I.e. not vector, map or other
  collection but implements Schema protocol."
  [x]
  (and (record? x)
       #?(:clj  (instance? schema.core.Schema x)
          :cljs (satisfies? schema.core.Schema x))))

(defn walk
  "Calls `inner` for sub-schemas of this schema, creating new Schema of the same
  type as given and preserving the metadata. Calls `outer` with the created
  Schema."
  {:added "0.3.0"}
  [inner outer this]
  (cond
    ; Schemas with children
    (satisfies? WalkableSchema this) (-walk this inner outer)
    ; Leaf schemas - Rest Schema records should be the leaf schemas.
    (schema-record? this) (outer this)
    ; Regular clojure datastructures
    (record? this) (outer (with-meta (reduce (fn [r x] (conj r (inner x))) this this) (meta this)))
    #?@(:clj [(list? this) (outer (with-meta (apply list (map inner this)) (meta this)))])
    (seq? this) (outer (with-meta (doall (map inner this)) (meta this)))
    (coll? this) (outer (with-meta (into (empty this) (map inner this)) (meta this)))
    :else (outer this)))

(defn postwalk
  "Performs a depth-first, post-order traversal of `schema`.  Calls `f` on
  each sub-form, uses f's return value in place of the original.
  Works with Schemas implementing schema-tools.walk/WalkableSchema,
  implementation is provided for built-in schemas.
  Consumes seqs as with doall."
  {:added "0.8"}
  [f schema]
  (walk (partial postwalk f) f schema))

(defn prewalk
  "Like postwalk, but does pre-order traversal."
  {:added "0.8"}
  [f schema]
  (walk (partial prewalk f) identity (f schema)))

(extend-protocol WalkableSchema
  ;; Walk for map-entries doesn't have to return new map-entry, because
  ;; the result is used in (into {} ...) and vector will
  ;; work in that case.
  #?(:clj Map$Entry
     :cljs MapEntry)
  (-walk [this inner outer]
    (outer (with-meta (vec (map inner this)) (meta this))))

  schema.core.Maybe
  (-walk [this inner outer]
    (outer (with-meta (s/maybe (inner (:schema this))) (meta this))))

  schema.core.Both
  (-walk [this inner outer]
    (outer (with-meta (apply s/both (map inner (:schemas this))) (meta this))))

  schema.core.Either
  (-walk [this inner outer]
    (outer (with-meta (apply s/either (map inner (:schemas this))) (meta this))))

  #?@(:clj [schema.core.Recursive
            (-walk [this inner outer]
                   (outer (with-meta (s/recursive (inner (:derefable this))) (meta this))))])

  schema.core.Predicate
  (-walk [this _ outer]
    (outer this))

  schema.core.NamedSchema
  (-walk [this inner outer]
    (outer (with-meta (s/named (inner (:schema this)) (:name this)) (meta this))))

  schema.core.ConditionalSchema
  (-walk [this inner outer]
    (outer (with-meta (s/->ConditionalSchema
                        (doall (for [[pred schema] (:preds-and-schemas this)]
                                 [pred (inner schema)]))
                        (:error-symbol this))
                      (meta this))))

  schema.core.CondPre
  (-walk [this inner outer]
    (outer (with-meta (apply s/cond-pre (map inner (:schemas this))) (meta this))))

  schema.core.Constrained
  (-walk [this inner outer]
    (outer (with-meta (s/constrained (inner (:schema this)) (:postcondition this) (:post-name this)) (meta this)))))
