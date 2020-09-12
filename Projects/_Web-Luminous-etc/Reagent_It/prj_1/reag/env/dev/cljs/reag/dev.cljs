(ns ^:figwheel-no-load reag.dev
  (:require
    [reag.core :as core]
    [devtools.core :as devtools]))

(devtools/install!)

(enable-console-print!)

(core/init!)
