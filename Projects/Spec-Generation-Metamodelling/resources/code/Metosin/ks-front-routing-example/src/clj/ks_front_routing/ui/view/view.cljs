(ns ks-front-routing.ui.view.view)

;;
;; Multimethod accepting one argument that should be a route map containing
;; a :page-id and optional :params
;;

(defmulti render-view :page-id)
