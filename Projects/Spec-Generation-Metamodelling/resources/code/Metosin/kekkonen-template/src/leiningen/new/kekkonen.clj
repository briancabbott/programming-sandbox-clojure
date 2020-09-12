(ns leiningen.new.kekkonen
  (:require [leiningen.new.templates :refer [renderer name-to-path ->files]]
            [leiningen.core.main :as main]
            [clojure.string :as s]))

(def render (renderer "kekkonen"))

(defn template-data [name opts]
  {:name           name
   :sanitized      (name-to-path name)})

(defmulti option-files (fn [option data] option))

(defmethod option-files :base [_ data]
  [["src/{{sanitized}}/handler.clj" (render "handler.clj" data)]
   ["src/{{sanitized}}/main.clj"    (render "main.clj" data)]
   ["src/{{sanitized}}/system.clj"  (render "system.clj" data)]
   ["src/user.clj"                  (render "user.clj" data)]
   ["project.clj"                   (render "project.clj" data)]
   ["README.md"                     (render "README.md" data)]])

(defn active-options [args]
  (for [arg args :when (re-matches #"\+[A-Za-z0-9-]+" arg)]
    (keyword (subs arg 1))))

(defn kekkonen
  [name & args]
  (let [opts  (cons :base (active-options args))
        data  (template-data name opts)
        files (reduce into [] (map #(option-files % data) opts))]
    (main/info "Generating a fresh new kekkonen.")
    (apply ->files data files)))
