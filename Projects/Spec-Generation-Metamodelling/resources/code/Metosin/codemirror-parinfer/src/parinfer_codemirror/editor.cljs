(ns parinfer-codemirror.editor
  "Glues Parinfer's formatter to a CodeMirror editor"
  (:require [clojure.string :as str]
            [parinfer-codemirror.editor-support :refer [update-cursor! fix-text!]]
            cljsjs.codemirror
            cljsjs.codemirror.addon.selection.active-line
            cljsjs.codemirror.addon.edit.matchbrackets
            parinfer-codemirror.clojure-parinfer-mode))

(defn before-change
  "Called before any change is applied to the editor."
  [cm change]
  ;; keep CodeMirror from reacting to a change from "setValue"
  ;; if it is not a new value.
  (when (and (= "setValue" (.-origin change))
             (= (.getValue cm) (str/join "\n" (.-text change))))
    (.cancel change)))

(defn on-change
  "Called after any change is applied to the editor."
  [mode cm change]
  (when (not= "setValue" (.-origin change))
    (fix-text! cm {:mode mode :change change})
    ; (update-cursor! cm change)
    ))

(defn on-cursor-activity
  "Called after the cursor moves in the editor."
  [mode cm]
  (fix-text! cm {:mode mode}))

(defn on-tab
  "Indent selection or insert two spaces when tab is pressed.
  from: https://github.com/codemirror/CodeMirror/issues/988#issuecomment-14921785"
  [cm]
  (if (.somethingSelected cm)
    (.indentSelection cm)
    (let [n (.getOption cm "indentUnit")
          spaces (apply str (repeat n " "))]
      (.replaceSelection cm spaces))))

(def default-opts
  {:mode "clojure-parinfer"
   :matchBrackets true
   :extraKeys {:Tab on-tab
               :Shift-Tab "indentLess"}})
