(set-env!
  :resource-paths #{"src"}
  :dependencies   '[[boot/core        "2.2.0"  :scope "provided"]
                    [adzerk/bootlaces "0.1.11" :scope "test"]])

(require '[adzerk.bootlaces :refer :all])

(def +version+ "0.1.0-SNAPSHOT")

(bootlaces! +version+)

(task-options!
  pom {:project 'metosin/boot-leiningen
       :version +version+
       :description "Provides Boot task to generate project.clj"
       :url     "https://github.com/metosin/boot-leiningen"
       :scm     {:url "https//github.com/metosin/boot-leiningen"}
       :license {"EPL" "http://www.eclipse.org/legal/epl-v10.html"}})
