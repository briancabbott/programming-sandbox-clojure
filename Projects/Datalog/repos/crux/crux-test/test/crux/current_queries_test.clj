(ns crux.current-queries-test
  (:require [clojure.test :as t]
            [crux.api :as api]
            [crux.fixtures :as fix :refer [*api*]]
            [crux.fixtures.every-api :as every-api]
            [crux.node :as n])
  (:import (java.time Instant Duration)
           java.util.Date))

(t/use-fixtures :once every-api/with-embedded-kafka-cluster)
(t/use-fixtures :each
  (partial fix/with-opts {:crux/bus {:sync? true}
                          :crux/node {:slow-queries-min-threshold (Duration/ofNanos 1)}})
  every-api/with-each-api-implementation)

(t/deftest test-cleaning-recent-queries
  (let [clean-completed-queries @#'n/clean-completed-queries
        queries [{:finished-at (Date.)
                  ::query-id 1}
                 {:finished-at (Date/from (.minus (.toInstant (Date.))
                                                  (Duration/ofSeconds 5)))
                  ::query-id 2}
                 {:finished-at (Date/from (.minus (.toInstant (Date.))
                                                  (Duration/ofSeconds 10)))
                  ::query-id 3}]]
    (t/testing "test recent-queries - check max count expiration"
      (t/is (= [1]
               (->> (clean-completed-queries queries
                                             {:recent-queries-max-age (Duration/ofSeconds 8)
                                              :recent-queries-max-count 1})
                    (map ::query-id))))

      (t/is (= [1 2]
               (->> (clean-completed-queries queries
                                             {:recent-queries-max-age (Duration/ofSeconds 8)
                                              :recent-queries-max-count 2})
                    (map ::query-id)))))

    (t/testing "test recent-queries - check time expiration"
      (t/is (= [1]
               (->> (clean-completed-queries queries
                                             {:recent-queries-max-age (Duration/ofSeconds 4)
                                              :recent-queries-max-count 5})
                    (map ::query-id))))
      (t/is (= [1 2]
               (->> (clean-completed-queries queries
                                             {:recent-queries-max-age (Duration/ofSeconds 8)
                                              :recent-queries-max-count 5})
                    (map ::query-id)))))))

(t/deftest test-cleaning-slowest-queries
  (let [clean-slowest-queries @#'n/clean-slowest-queries
        started-at (Date.)
        queries [{:started-at (Date/from (.minus (.toInstant started-at)
                                                 (Duration/ofSeconds 10)))
                  :finished-at (Date/from (.minus (.toInstant started-at)
                                                  (Duration/ofSeconds 9)))
                  ::query-id 3}
                 {:started-at (Date/from (.minus (.toInstant started-at)
                                                 (Duration/ofSeconds 10)))
                  :finished-at (Date/from (.minus (.toInstant started-at)
                                                  (Duration/ofSeconds 5)))
                  ::query-id 2}
                 {:started-at (Date/from (.minus (.toInstant started-at)
                                                 (Duration/ofSeconds 10)))
                  :finished-at (Date/from (.minus (.toInstant started-at)
                                                  (Duration/ofSeconds 1)))
                  ::query-id 1}]]
    (t/testing "test slowest-queries - check max count expiration & ordering"
      (t/is (= [1]
               (->> (clean-slowest-queries queries
                                           {:slow-queries-max-age (Duration/ofSeconds 8)
                                            :slow-queries-max-count 1})
                    (map ::query-id))))

      (t/is (= [1 2]
               (->> (clean-slowest-queries queries
                                           {:slow-queries-max-age (Duration/ofSeconds 8)
                                            :slow-queries-max-count 2})
                    (map ::query-id)))))

    (t/testing "test slowest-queries - check time expiration & ordering"
      (t/is (= [1]
               (->> (clean-slowest-queries queries
                                           {:slow-queries-max-age (Duration/ofSeconds 4)
                                            :slow-queries-max-count 5})
                    (map ::query-id))))
      (t/is (= [1 2]
               (->> (clean-slowest-queries queries
                                           {:slow-queries-max-age (Duration/ofSeconds 8)
                                            :slow-queries-max-count 5})
                    (map ::query-id)))))))

(t/deftest test-recent-queries
  (fix/submit+await-tx [[:crux.tx/put {:crux.db/id :ivan :name "Ivan"}]
                        [:crux.tx/put {:crux.db/id :petr :name "Petr"}]])

  (let [db (api/db *api*)]
    (api/q db
           '{:find [e]
             :where [[e :name "Ivan"]]})

    (t/testing "test recent-query - post successful query"
      (let [recent-queries (api/recent-queries *api*)]
        (t/is (= :completed (:status (first recent-queries))))))

    (t/is (thrown-with-msg? Exception
                            #"Find refers to unknown variable: f"
                            (api/q db '{:find [f], :where [[e :crux.db/id _]]})))

    (t/testing "test recent-query - post failed query"
      (let [malformed-query (first (api/recent-queries *api*))]
        (t/is (= '{:find [f], :where [[e :crux.db/id _]]} (:query malformed-query)))
        (t/is (= :failed (:status malformed-query)))))))

(t/deftest test-slowest-queries
  (fix/submit+await-tx (mapv
                        (fn [n] [:crux.tx/put
                                 {:crux.db/id (keyword (str "ivan" n))
                                  :name (str "ivan" n)}])
                        (range 100)))
  (let [db (api/db *api*)]
    (api/q db
           '{:find [e n]
             :where [[e :name n]]})

    (t/testing "test slowest-queries - post query (min threshold - 1 nanosecond)"
      (t/is (= :completed (:status (first (api/slowest-queries *api*))))))

    (t/testing "test slowest-queries - test `slow-query?` check")
    (let [start (Instant/now)
          query-info {:started-at (Date/from start) :finished-at (Date/from (.plusSeconds start 2))}]
      (t/is (n/slow-query? query-info {:slow-queries-min-threshold (Duration/ofSeconds 1)}))
      (t/is (not (n/slow-query? query-info {:slow-queries-min-threshold (Duration/ofSeconds 10)}))))))

(t/deftest test-active-queries
  (fix/submit+await-tx [[:crux.tx/put {:crux.db/id :ivan :name "Ivan"}]
                        [:crux.tx/put {:crux.db/id :petr :name "Petr"}]])

  (let [db (api/db *api*)]
    (t/testing "test active-queries - streaming query"
      (with-open [res (api/open-q db '{:find [e], :where [[e :name "Ivan"]]})]
        (t/testing "test active-queries - streaming query (not realized)"
          (let [streaming-query (first (api/active-queries *api*))]
            (when-not (= :remote every-api/*node-type*)
              ;; can't reliably get active-queries on remote for short queries
              (t/is (= '{:find [e] :where [[e :name "Ivan"]]} (:query streaming-query)))))))

      (t/testing "test active-queries & recent-queries - streaming query (result realized)"
        (t/is (empty? (api/active-queries *api*)))
        (t/is (= :completed (:status (first (api/recent-queries *api*)))))))))
