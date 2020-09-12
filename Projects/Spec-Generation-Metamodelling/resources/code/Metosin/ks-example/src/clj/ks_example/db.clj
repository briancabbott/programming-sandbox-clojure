(ns ks-example.db)

(defn create-id ^String []
  (str (org.bson.types.ObjectId.)))
