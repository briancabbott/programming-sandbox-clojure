(ns parinfer-codemirror.example
  (:require [parinfer-codemirror.editor :as editor]))

(defn create-editor!
  "Create a parinfer editor."
  ([el] (create-editor! el nil))
  ;; codemirror opts vs. parinfer opts
  ([el {:keys [parinfer-mode]
        :or {parinfer-mode :indent-mode}
        :as opts}]
   (let [cm (js/CodeMirror.fromTextArea el (clj->js (merge editor/default-opts opts)))]

     (.on cm "change" (partial editor/on-change parinfer-mode))
     (.on cm "beforeChange" editor/before-change)
     (.on cm "cursorActivity" (partial editor/on-cursor-activity parinfer-mode))

     cm)))

(def text (atom nil))

(defonce cm1 (atom nil))
(defonce cm2 (atom nil))

(defn render-dev! []
  (let [el1 (js/document.getElementById "code-indent-mode")
        el2 (js/document.getElementById "code-paren-mode")]

    (swap! cm1 (fn [cm]
                 (if cm (.toTextArea cm))
                 (create-editor! el1)))
    (swap! cm2 (fn [cm]
                 (if cm (.toTextArea cm))
                 (create-editor! el2 {:parinfer-mode :paren-mode})))

    (.on @cm1 "change" (fn [cm change]
                         (when (not= "setValue" (.-origin change))
                           (reset! text (.getValue cm))
                           (.setValue @cm2 (.getValue cm)))))
    (.on @cm2 "change" (fn [cm change]
                         (when (not= "setValue" (.-origin change))
                           (reset! text (.getValue cm))
                           (.setValue @cm1 (.getValue cm)))))))

(render-dev!)
