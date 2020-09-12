(ns ks-example.date-time-utils)

(ns ks-example.date-time-utils
  (:require [clojure.edn :as edn]
            [clj-time.core :as t]
            [clj-time.format :as tf]
            [clj-time.coerce :as tc]
            [clj-time.local :as tl])
  (:import [org.joda.time DateTime DateTimeZone]
           [org.joda.time.format DateTimeFormatter ISODateTimeFormat]))

;;
;; Configure JodaTime:
;;

; Default time-zone is UTC. Life is simple.

(DateTimeZone/setDefault DateTimeZone/UTC)

; Local TZ:

(def local-zone (DateTimeZone/forID "Europe/Helsinki"))

;;
;; EDN with Joda DateTime support:
;;

;; Print Joda DateTime's like java.util.Date's.

(def ^:private ^DateTimeFormatter iso-8601-formatter (ISODateTimeFormat/dateTime))

(defmethod print-method DateTime [^DateTime x ^java.io.Writer writer]
  (.write writer "#inst \"")
  (.printTo iso-8601-formatter writer x)
  (.write writer "\""))

;; Use this as opts to clojure.edn/read to

(def edn-opts {:readers {'inst (fn [inst] (.parseDateTime iso-8601-formatter inst))}})

;;
;; Now:
;;

(defn now ^DateTime [] (t/now))
(defn timestamp ^Long [] (System/currentTimeMillis))

;;
;; datetime?
;;

(defn datetime? [v]
  (instance? DateTime v))

;;
;; Coerce:
;;

(defprotocol ToDateTime
  (->date-time [value]))

(extend-protocol ToDateTime
  java.lang.Long
  (->date-time [value]
    (tc/from-long value))

  java.util.Date
  (->date-time [value]
    (tc/from-date value))

  org.joda.time.DateTime
  (->date-time [value]
    value)

  java.lang.String
  (->date-time [value]
    (tc/from-string value))

  nil
  (->date-time [_]
    nil))

; schema coercer:

(defn datetime-matcher [schema]
  (if (= org.joda.time.DateTime schema)
    ->date-time))
