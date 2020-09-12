(ns ks-example.integration
  (:require [schema.core :as s]
            [schema.coerce :as sc]
            [schema.utils :as su]
            [plumbing.core :as p :refer [defnk]]
            [ring.util.http-response :as resp :refer [ok]]
            [monger.collection :as mc]
            [ks-example.db :as db])
  (:import [org.joda.time DateTime]))

; should be in utils etc:
(defn validator [schema]
  (let [coercer (sc/coercer schema sc/+json-coercions+)]
    (fn [data]
      (let [result (coercer data)]
        (if (su/error? result)
          (resp/bad-request! {:error result})
          result)))))


(def IncomingMessage {:source   s/Str
                      :message  s/Str})

(def MessageData (assoc IncomingMessage
                        :_id  s/Str
                        :ts   DateTime))

(def ->incoming-message (validator IncomingMessage))
(def ->message-data (validator MessageData))

(defnk save-incoming! [params [:system/ctx db]]
  (let [message (-> params
                    ->incoming-message
                    (assoc
                      :_id  (db/create-id)
                      :ts   (DateTime/now)))]
    (mc/insert db :incoming message)
    (ok message)))

(defnk find-by-source [[:params source] [:system/ctx db]]
  (ok (map ->message-data (mc/find-maps db :incoming {:source source}))))
