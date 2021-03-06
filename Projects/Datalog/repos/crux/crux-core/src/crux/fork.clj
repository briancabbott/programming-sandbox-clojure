(ns ^:no-doc crux.fork
  (:require [clojure.set :as set]
            [crux.api :as api]
            [crux.codec :as c]
            [crux.db :as db]
            [crux.io :as cio]
            [crux.memory :as mem])
  (:import crux.codec.EntityTx
           java.util.Date))

(defn- merge-seqs
  ([inner mem] (merge-seqs inner mem #(.compare mem/buffer-comparator %1 %2)))
  ([inner mem compare]
   (letfn [(merge-seqs* [inner mem]
             (lazy-seq
              (let [[i1 & more-inner] inner
                    [m1 & more-mem] mem]
                (cond
                  (empty? inner) mem
                  (empty? mem) inner
                  :else (let [cmp (compare i1 m1)]
                          (cond
                            (neg? cmp) (cons i1 (merge-seqs* more-inner mem))
                            (zero? cmp) (cons m1 (merge-seqs* more-inner more-mem))
                            :else (cons m1 (merge-seqs* inner more-mem))))))))]
     (merge-seqs* inner mem))))

(defn- date-min [^Date d1, ^Date d2]
  (if (and d1 d2)
    (Date. (min (.getTime d1) (.getTime d2)))
    (or d1 d2)))

(defn- inc-date [^Date d1]
  (Date. (inc (.getTime d1))))

(defrecord ForkedIndexStore [inner-index-store mem-index-store
                             evicted-eids
                             capped-valid-time capped-transact-time]
  db/IndexStore
  (av [this a min-v entity-resolver-fn]
    (merge-seqs (db/av inner-index-store a min-v entity-resolver-fn)
                (db/av mem-index-store a min-v entity-resolver-fn)))

  (ave [this a v min-e entity-resolver-fn]
    (merge-seqs (db/ave inner-index-store a v min-e entity-resolver-fn)
                (db/ave mem-index-store a v min-e entity-resolver-fn)))

  (ae [this a min-e entity-resolver-fn]
    (merge-seqs (db/ae inner-index-store a min-e entity-resolver-fn)
                (db/ae mem-index-store a min-e entity-resolver-fn)))

  (aev [this a e min-v entity-resolver-fn]
    (merge-seqs (db/aev inner-index-store a e min-v entity-resolver-fn)
                (db/aev mem-index-store a e min-v entity-resolver-fn)))

  (entity-as-of-resolver [this eid valid-time transact-time]
    (some-> ^EntityTx (db/entity-as-of this eid valid-time transact-time)
            (.content-hash)
            c/->id-buffer))

  (entity-as-of [this eid valid-time transact-time]
    (->> [(when-not (contains? (into #{} (map #(c/->id-buffer %)) evicted-eids) eid)
            (db/entity-as-of inner-index-store eid
                             (cond-> valid-time capped-valid-time (date-min capped-valid-time))
                             (cond-> transact-time capped-transact-time (date-min capped-transact-time))))
          (db/entity-as-of mem-index-store eid valid-time transact-time)]
         (remove nil?)
         (sort-by (juxt #(.vt ^EntityTx %) #(.tx-id ^EntityTx %)))
         last))

  (entity-history [this eid sort-order opts]
    (merge-seqs (when-not (contains? evicted-eids eid)
                  (db/entity-history inner-index-store eid sort-order
                                     (case sort-order
                                       :asc (-> opts
                                                (cond-> capped-valid-time (update-in [:end :crux.db/valid-time] date-min (inc-date capped-valid-time)))
                                                (cond-> capped-transact-time (update-in [:end :crux.tx/tx-time] date-min (inc-date capped-transact-time))))
                                       :desc (-> opts
                                                 (cond-> capped-valid-time (update-in [:start :crux.db/valid-time] date-min capped-valid-time))
                                                 (cond-> capped-transact-time (update-in [:start :crux.tx/tx-time] date-min capped-transact-time))))))
                (db/entity-history mem-index-store eid sort-order opts)

                (case [sort-order (boolean (:with-corrections? opts))]
                  [:asc false] #(compare (.vt ^EntityTx %1) (.vt ^EntityTx %2))
                  [:asc true] #(compare [(.vt ^EntityTx %1) (.tx-id ^EntityTx %1)]
                                        [(.vt ^EntityTx %2) (.tx-id ^EntityTx %2)])
                  [:desc false] #(compare (.vt ^EntityTx %2) (.vt ^EntityTx %1))
                  [:desc true] #(compare [(.vt ^EntityTx %2) (.tx-id ^EntityTx %2)]
                                         [(.vt ^EntityTx %1) (.tx-id ^EntityTx %1)]))))

  (decode-value [this value-buffer]
    (or (db/decode-value mem-index-store value-buffer)
        (db/decode-value inner-index-store value-buffer)))

  (encode-value [this value]
    (db/encode-value mem-index-store value))

  (open-nested-index-store ^java.io.Closeable [this]
    (->ForkedIndexStore (db/open-nested-index-store inner-index-store)
                        (db/open-nested-index-store mem-index-store)
                        evicted-eids
                        capped-valid-time
                        capped-transact-time))

  java.io.Closeable
  (close [_]
    (cio/try-close mem-index-store)
    (cio/try-close inner-index-store)))

(defrecord ForkedIndexer [inner-indexer mem-indexer !evicted-eids !etxs capped-valid-time capped-transact-time]
  db/Indexer
  (index-docs [this docs]
    (db/index-docs mem-indexer docs))

  (unindex-eids [this eids]
    (swap! !evicted-eids set/union (set eids))
    (db/unindex-eids mem-indexer eids)

    (with-open [inner-index-store (db/open-index-store inner-indexer)
                mem-index-store (db/open-index-store mem-indexer)]
      (let [tombstones (->> (for [eid eids
                                  etx (concat (db/entity-history inner-index-store eid :asc
                                                                 {:with-corrections? true})
                                              (db/entity-history mem-index-store eid :asc
                                                                 {:with-corrections? true}))
                                  :let [content-hash (.content-hash ^EntityTx etx)]
                                  :when content-hash]
                              [content-hash {:crux.db/id eid, :crux.db/evicted? true}])
                            (into {}))]
        {:tombstones tombstones})))

  (index-entity-txs [this tx entity-txs]
    (swap! !etxs merge (->> entity-txs
                            (into {} (map (juxt (fn [^EntityTx etx]
                                                  [(.eid etx) (.vt etx) (.tt etx) (.tx-id etx)])
                                                identity)))))
    (db/index-entity-txs mem-indexer tx entity-txs))

  (store-index-meta [this k v]
    (db/store-index-meta mem-indexer k v))

  (read-index-meta [this k]
    (db/read-index-meta this k nil))

  (read-index-meta [this k not-found]
    (let [v (db/read-index-meta mem-indexer k ::not-found)]
      (if (not= v ::not-found)
        v
        (db/read-index-meta inner-indexer k not-found))))

  (latest-completed-tx [this]
    (or (db/latest-completed-tx mem-indexer)
        (db/latest-completed-tx inner-indexer)))

  (mark-tx-as-failed [this tx]
    (db/mark-tx-as-failed mem-indexer tx))

  (tx-failed? [this tx-id]
    (or (db/tx-failed? mem-indexer tx-id)
        (db/tx-failed? inner-indexer tx-id)))

  (open-index-store ^java.io.Closeable [this]
    (->ForkedIndexStore (db/open-index-store inner-indexer)
                        (db/open-index-store mem-indexer)
                        @!evicted-eids
                        capped-valid-time
                        capped-transact-time)))

(defn newly-evicted-eids [indexer]
  @(:!evicted-eids indexer))

(defn new-etxs [indexer]
  (vals @(:!etxs indexer)))

(defn ->forked-indexer [inner-indexer mem-indexer
                        capped-valid-time capped-transact-time]
  (->ForkedIndexer inner-indexer mem-indexer
                   (atom #{}) (atom {})
                   capped-valid-time capped-transact-time))

(defrecord ForkedDocumentStore [doc-store !docs]
  db/DocumentStore
  (submit-docs [_ docs]
    (swap! !docs merge docs))

  (fetch-docs [_ ids]
    (let [overridden-docs (select-keys @!docs ids)]
      (into overridden-docs (db/fetch-docs doc-store (set/difference (set ids) (set (keys overridden-docs))))))))

(defn new-docs [doc-store]
  @(:!docs doc-store))

(defn ->forked-document-store [doc-store]
  (->ForkedDocumentStore doc-store (atom {})))
