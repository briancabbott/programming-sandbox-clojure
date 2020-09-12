(ns crux.fixtures.calcite
  (:require [crux.calcite :as cal]
            [crux.fixtures :as fix]
            [crux.node :as n])
  (:import java.sql.DriverManager
           java.sql.PreparedStatement))

(def ^:dynamic ^java.sql.Connection *conn*)

(defn with-calcite-connection [f]
  (with-open [conn (cal/jdbc-connection fix/*api*)]
    (binding [*conn* conn]
      (f))))

(defn with-calcite-connection-scan-only [f]
  (with-open [conn (cal/jdbc-connection fix/*api* true)]
    (binding [*conn* conn]
      (f))))

(defn with-avatica-connection [f]
  (with-open [conn (DriverManager/getConnection "jdbc:avatica:remote:url=http://localhost:1503;serialization=protobuf;timeZone=UTC")]
    (binding [*conn* conn]
      (f))))

(def with-calcite-module
  (fix/with-opts {::cal/server {:port 1503}}))

(def with-scan-only
  (fix/with-opts {::cal/server {::cal/scan-only? true}}))

(defn query [q]
  (with-open [stmt (.createStatement *conn*)
              rs (.executeQuery stmt q)]
    (->> rs resultset-seq (into []))))

(defn exec-prepared-query [^PreparedStatement p & args]
  (doseq [[i v] args]
    (if (string? v)
      (.setString p i v)
      (if (number? v)
        (.setInt p i v))))
  (with-open [rs (.executeQuery p)]
    (->> rs resultset-seq (into []))))

(defn prepared-query [q & args]
  (apply exec-prepared-query (.prepareStatement *conn* q) args))

(defn explain [q]
  (:plan (first (query (str "explain plan for " q)))))
