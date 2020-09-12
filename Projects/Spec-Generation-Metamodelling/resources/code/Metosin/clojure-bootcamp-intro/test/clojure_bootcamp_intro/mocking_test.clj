(ns clojure-bootcamp-intro.mocking-test
  (:require [midje.sweet :refer :all]
            [clojure-bootcamp-intro.mocking :as api]))

; Fist implement api/find-user-by-id so that these tests work:

;;
;; Test the 'database' API:
;;

(facts
  (api/find-user-by-id "tommi")  => (contains {:twitter "@ikitommi"})
  (api/find-user-by-id "foo")  => nil)


; Next implement api/get-user so that these tests work:

;;
;; Test the 'REST' API:
;;

(facts
  (api/get-user {:params {:id "tommi"}}) => (contains {:status  200})
  (api/get-user {:params {:id "foo"}}) => (contains {:status  404}))

; Note that those tests hit the 'database'.
; These mock the api/find-user-by-id so that DB is not hit:

(facts
  
  (fact "test 200 case"
    (api/get-user {:params {:id ..id...}}) => (contains {:status 200  :body ..user..})
    (provided
      (api/find-user-by-id ..id..) => ..user..))
  
  (fact "test 404 case"
    (api/get-user {:params {:id ..id...}}) => (contains {:status 404})
    (provided
      (api/find-user-by-id ..id..) => nil)))

; For more advanced stuff, see https://github.com/marick/Midje

