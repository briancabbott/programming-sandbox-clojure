(ns crux.fixtures.kv
  (:require [clojure.java.io :as io]
            [crux.fixtures :as fix]
            [crux.system :as sys]))

(def ^:dynamic *kv-opts* {})

(defn with-kv-store* [f]
  (fix/with-tmp-dir "kv" [db-dir]
    (with-open [sys (-> (sys/prep-system
                         {:kv-store (merge (when-let [db-dir-suffix (:db-dir-suffix *kv-opts*)]
                                             {:db-dir (io/file db-dir db-dir-suffix)})
                                           *kv-opts*)})
                        (sys/start-system))]
      (f (:kv-store sys)))))

(defmacro with-kv-store [bindings & body]
  `(with-kv-store* (fn [~@bindings] ~@body)))

(def rocks-dep {:crux/module `crux.rocksdb/->kv-store, :db-dir-suffix "rocksdb"})
(def lmdb-dep {:crux/module `crux.lmdb/->kv-store, :db-dir-suffix "lmdb", :env-mapsize 4096})
(def memkv-dep {:crux/module `crux.mem-kv/->kv-store})

(defn with-each-kv-store* [f]
  (doseq [kv-opts [memkv-dep
                   rocks-dep
                   {:crux/module `crux.rocksdb.jnr/->kv-store
                    :db-dir-suffix "rocksdb-jnr"}
                   lmdb-dep
                   {:crux/module `crux.lmdb.jnr/->kv-store
                    :db-dir-suffix "lmdb-jnr"
                    :env-mapsize 4096}]]
    (binding [*kv-opts* (merge *kv-opts* kv-opts)]
      (f))))

(defmacro with-each-kv-store [& body]
  `(with-each-kv-store* (fn [] ~@body)))

(defn with-kv-store-opts* [kv-opts f]
  (fix/with-tmp-dir "db-dir" [db-dir]
    (letfn [(->kv-opts [module]
              (merge (when-let [db-dir-suffix (:db-dir-suffix kv-opts)]
                       {:db-dir (io/file db-dir db-dir-suffix module)})
                     kv-opts))]
      (fix/with-opts {:crux/tx-log {:kv-store (->kv-opts "tx-log")}
                      :crux/document-store {:kv-store (->kv-opts "doc-store")}
                      :crux/indexer {:kv-store (->kv-opts "indexer")}}
        f))))

(defmacro with-kv-store-opts [kv-dep & body]
  `(with-kv-store-opts* ~kv-dep (fn [] ~@body)))
