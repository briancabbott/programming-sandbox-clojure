(ns pure-funcs.prod
  (:require [pure-funcs.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
