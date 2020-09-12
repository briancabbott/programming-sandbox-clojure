(ns ks-example.ui.figwheel
  (:require [figwheel.client :as fw]
            [ks-example.ui.main :as m]))

(fw/watch-and-reload
  :websocket-url "ws://localhost:3450/figwheel-ws"
  :jsload-callback m/init!
  :url-rewriter (fn [u]
                  ; NOTE: Quick fix to reload files from our own server
                  ; instead of figwheel server
                  (clojure.string/replace u ":3450" ":3000")))
