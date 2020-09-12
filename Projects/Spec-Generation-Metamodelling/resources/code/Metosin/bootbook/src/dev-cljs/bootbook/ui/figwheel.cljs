(ns bootbook.ui.figwheel
  (:require [figwheel.client :as fw]
            [bootbook.ui.main :as m]))

(fw/watch-and-reload
  :websocket-url "ws://localhost:3449/figwheel-ws"
  :jsload-callback m/init!)
