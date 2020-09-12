(ns ks-example.users)

(def pena {:_id       "1"
           :email     "pena@example.com"
           :fullname  "Pena Banaani"})

(defn find-user-with-apikey [db apikey]
  (if (= apikey "pena")
    pena))

(defn find-user-with-password [db email password]
  (if (and (= email "pena@example.com") (= password "pena"))
    pena))
