(ns crux.bench.sorted-maps-microbench
  (:require [clojure.java.io :as io]
            [crux.api :as crux]
            [crux.bench :as bench]
            [crux.kafka.embedded :as ek]
            [crux.fixtures :as fix]
            [crux.kafka :as k]
            [crux.rocksdb :as rocks]))

(defn submit-batches [node]
  (for [doc-batch (->> (for [n (range 25000)]
                         [:crux.tx/put {:crux.db/id (keyword (str "doc-" n))
                                        :nested-map {:foo :bar
                                                     :baz :quux
                                                     :doc-idx n}}])
                       (partition-all 1000))]
    (crux/submit-tx node (vec doc-batch))))

(defn run-benches [node [submit-bench-type await-bench-type]]
  (bench/run-bench await-bench-type
    (let [submitted-tx (-> (bench/run-bench submit-bench-type
                             (-> {:success? true}
                                 (with-meta {:submitted-tx (last (submit-batches node))})))
                           meta
                           :submitted-tx)]

      (crux/await-tx node submitted-tx (java.time.Duration/ofSeconds 20))
      {:success? true})))

(defn run-sorted-maps-microbench [node]
  (bench/with-bench-ns :sorted-maps
    (run-benches node [:initial-submits :initial-await])
    (run-benches node [:subsequent-submits :subsequent-await])))

(comment
  (fix/with-tmp-dir "crux" [tmp-dir]
    (with-open [ek (ek/start-embedded-kafka #::ek{:zookeeper-data-dir (io/file tmp-dir "zk-data")
                                                  :kafka-dir (io/file tmp-dir "kafka-data")
                                                  :kafka-log-dir (io/file tmp-dir "kafka-log")})
                node (crux/start-node {::k/kafka-config {:bootstrap-servers "localhost:9092"}
                                       :crux/tx-log {:crux/module `k/->tx-log, :kafka-config ::k/kafka-config}
                                       :crux/document-store {:crux/module `k/->document-store
                                                             :kafka-config ::k/kafka-config
                                                             :local-document-store {:kv-store {:crux/module `rocks/->kv-store,
                                                                                               :db-dir (io/file tmp-dir "doc-store")}}}
                                       :crux/indexer {:kv-store {:crux/module `rocks/->kv-store, :db-dir (io/file tmp-dir "indexer")}}})]
      (bench/with-bench-ns :sorted-maps
        (run-benches node [:initial-submits :initial-await])))))
