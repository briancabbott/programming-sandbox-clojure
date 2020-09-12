(ns dommy.attrs
  "DEPRECATED namespace. These functions have been moved to dommy.core"
  (:require [dommy.core :as dommy]))

(def ^{:deprecated "1.0.0", boolean true} has-class? dommy/has-class?)
(def ^{:deprecated "1.0.0"} add-class! dommy/add-class!)
(def ^{:deprecated "1.0.0"} remove-class! dommy/remove-class!)
(def ^{:deprecated "1.0.0"} toggle-class! dommy/toggle-class!)
(def ^{:deprecated "1.0.0"} set-style! dommy/set-style!)
(def ^{:deprecated "1.0.0"} style dommy/style)
(def ^{:deprecated "1.0.0"} set-px! dommy/set-px!)
(def ^{:deprecated "1.0.0"} px dommy/px)
(def ^{:deprecated "1.0.0"} set-attr! dommy/set-attr!)
(def ^{:deprecated "1.0.0"} remove-attr! dommy/remove-attr!)
(def ^{:deprecated "1.0.0"} attr dommy/attr)
(def ^{:deprecated "1.0.0"} toggle-attr! dommy/toggle-attr!)
(def ^{:deprecated "1.0.0", boolean true} hidden? dommy/hidden?)
(def ^{:deprecated "1.0.0"} toggle! dommy/toggle!)
(def ^{:deprecated "1.0.0"} hide! dommy/hide!)
(def ^{:deprecated "1.0.0"} show! dommy/show!)
(def ^{:deprecated "1.0.0"} bounding-client-rect dommy/bounding-client-rect)
(def ^{:deprecated "1.0.0"} scroll-into-view dommy/scroll-into-view)