(ns clojure-bootcamp-intro.data-types-test
  (:require [midje.sweet :refer :all]
            [clojure-bootcamp-intro.data-types :as d]))

(facts "Literals with Java types"
  (fact (class d/string-literal)  => String)
  (fact (class d/long-literal)    => Long)
  (fact (class d/boolean-literal) => Boolean)
  (fact (class d/char-literal)    => Character))

(facts "Functions"
  (fact (d/hello) => "hello")
  (fact (d/shout "hello") => "HELLO")
  
  (fact ((d/make-multiplier-of 3) 5) => 15))

(facts "Vector, map and set"
  (fact (vector? d/vector-literal) => true)
  (fact (set? d/set-literal)       => true)
  (fact (map? d/map-literal)       => true)
  
  ; Notice that they implement java.util interfaces:
  
  (fact (instance? java.util.List d/vector-literal))
  (fact (instance? java.util.Set  d/set-literal))
  (fact (instance? java.util.Map  d/map-literal)))

(facts "Vectors in detail"
  (fact (d/add-to-vector [1 2 3] 4) => [1 2 3 4])
  (fact (d/get-from-vector [1 2 3] 1) => 2)
  
  (fact (d/odd-values [1 2 3 4]) => [1 3])
  (fact (d/double-values [1 2 3 4]) => [2 4 6 8])
  
  (fact (d/sum-of-values [1 2 3]) => 6)) 

(facts "Sets in detail"
  (fact (d/add-to-set #{1 2} 3) => #{1 2 3})
  (fact (d/remove-from-set #{1 2} 2) => #{1})
  
  (fact (d/is-vowel \a) => truthy)
  (fact (d/is-vowel \x) => falsey))

(facts "Maps in detail"
  (fact (d/add-to-map {"a" 1} "b" 2) => {"a" 1 "b" 2})
  (fact (d/remove-from-map {"a" 1 "b" 2} "b") => {"a" 1})
  
  (fact (d/get-twitter-handle "tommi") => "@ikitommi")
  (fact (d/get-twitter-handle "foo")   => nil))

