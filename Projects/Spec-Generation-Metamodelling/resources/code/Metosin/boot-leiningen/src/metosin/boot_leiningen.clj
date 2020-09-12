(ns metosin.boot-leiningen
  (:require [boot.core :refer [get-env deftask]]
            [boot.util :refer [pp-str]]
            [clojure.java.io :as io]))

(defn- generate-lein-project-file! [& {:keys [keep-project] :or {:keep-project true}}]
  (let [pfile (io/file "project.clj")
        {:keys [project version] :as pom} (:task-options (meta #'boot.task.built-in/pom))
        prop #(when-let [x (get pom %2)] [%1 x])
        head (list* 'defproject (or project 'boot-project) (or version "0.0.0-SNAPSHOT")
                    (concat
                      (prop :url :url)
                      (when-let [x (get pom :license)] [:licenses (mapv (fn [[name url]] {:name name :url url}) x)])
                      (prop :description :description)
                      (prop :scm :scm)
                      [:dependencies (get-env :dependencies)
                       :source-paths (vec (concat (get-env :source-paths)
                                                  (get-env :resource-paths)))]))
        proj (pp-str (concat head (mapcat identity (get-env :lein))))]
    (if-not keep-project (.deleteOnExit pfile))
    (spit pfile proj)))

(deftask lein-generate
  "Generate a leiningen `project.clj` file.
   This task generates a leiningen `project.clj` file based on the boot
   environment configuration, including project name and version (generated
   if not present), dependencies, and source paths. Additional keys may be added
   to the generated `project.clj` file by specifying a `:lein` key in the boot
   environment whose value is a map of keys-value pairs to add to `project.clj`."
  []
  (generate-lein-project-file! :keep-project true))
