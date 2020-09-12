(ns ^:figwheel-no-load pure-funcs.dev
  (:require
    [pure-funcs.core :as core]
    [devtools.core :as devtools]))

(devtools/install!)

(enable-console-print!)

(core/init!)
