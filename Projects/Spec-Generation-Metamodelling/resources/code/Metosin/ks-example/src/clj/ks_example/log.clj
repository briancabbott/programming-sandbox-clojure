(ns ks-example.log
  (:require [clojure.tools.logging :as log])
  (:import [java.util.logging LogManager Logger Level]
           [org.slf4j.bridge SLF4JBridgeHandler]))

;
; Init JUL->SLF4J bridge:
;

(.reset (LogManager/getLogManager))
(SLF4JBridgeHandler/install)
(.setLevel (Logger/getLogger "global") Level/INFO)

;
; Put stuff to logging context:
;

(defmacro with-ctx [ctx & body]
  `(try
     (doseq [[k# v#] ~ctx]
       (org.slf4j.MDC/put (name k#) v#))
     ~@body
     (finally
       (doseq [k# ~(->> ctx keys (map name) vec)]
         (org.slf4j.MDC/remove k#)))))
