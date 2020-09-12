(ns clojure-bootcamp-intro.data-types)

; hint: http://clojure.org/cheatsheet

;;
;; =========================================================
;; Literals with Java types: 
;; =========================================================
;;

; Define symbols that refer to some basic Java data-types.

(def string-literal   nil)
(def long-literal     nil)
(def boolean-literal  nil)
(def char-literal     nil)

; Define more symbols, this time make them clojure vector,
; hash set, and hash map.

(def vector-literal   nil)
(def set-literal      nil)
(def map-literal      nil)

;;
;; Truthy:
;;

; Everything is true expect:
;   false
;   nil

(if true    (println "Yes, true is true"))
(if "foo"   (println "Yes, \"foo\" is true"))
(if 0       (println "Yes, 0 is true"))
(if println (println "Yes, symbols are also true"))

(if false   (println "Not printted"))
(if nil     (println "Not printted"))

; By the way, the = function tests equality using Object.equals:

; note: The 'str' function takes any number of arguments and it
; calls .toString an each of them and returns a new string with
; all arguments conjoined:

(= "foobar" (str "foo" "bar"))
;=> true

; If you really want the java == functionality, use
; identical? function.

(identical? "foobar" (str "foo" "bar"))
;=> false

; Pro tip: = takes any number of arguments:
(= "foo" (str "f" "oo") (str "fo" "o") "foo")
;=> true

; Pro tip:
; Can you invoke < and > functions with multiple arguments?

;;
;; =========================================================
;; Functions:
;; =========================================================
;;

; Modify this function so that it returns string "hello":

(defn hello []
  )

; Modify this function so that it returns given string in upper-case:
; hint: Java methods can be called using . special form.

(defn shout [s]
  )

;
; Making functions:
;

; Anonymous function using fn:

(def multiplier (fn [m v] (* m v)))

(multiplier 2 21)
;=> 42

; Same, makes function and binds a symbol to it
; in one step:

(defn multiplier [m v]
  (* m v))

; Pro-tip:
(macroexpand-1 '(defn multiplier [m v] (* m v)))
;=> (def multiplier (clojure.core/fn ([m v] (* m v))))

(multiplier 2 21)
;=> 42

; Return a new function that multiplies given value with
; value given as argument 'm':

(defn make-multiplier-of [m]
  )

; Bonus: change previous function so that it does not use
; the 'fn' form. hint: partial

; Functions with multiple arities:

(defn plus
  ([a b]
    (+ a b))
  ([a b c]
    (+ a b c)))

(plus 1 2)
;=> 3

(plus 1 2 3)
;=> 6

; apply
;   - sometimes you have arguments in vector and the function
;     accepts them as separate arguments:

; try: (plus [1 2 3])
;=> ArityException Wrong number of args (1) passed to: data-types$plus

(apply plus [1 2 3])
;=> 6

;;
;; =========================================================
;; Vectors in detail:
;; =========================================================
;;

; Modify this function so that it adds 'value' to given vector 'v'
; hint: conj

(defn add-to-vector [v value]
  )

; Note that 'conj' adds to "most convenient place" in collections. In case
; of vectors thats to the end.

; Return value using an index:
; hint: nth

(defn get-from-vector [v index]
  )

; Given a vector of numbers, return a new vector that
; has only odd numbers:
; hint: vector, odd?

(defn odd-values [v]
  )

; Helper function for next question:

(defn doubler [v]
  (* 2 v))

; Return vector with all values doubled:
; hint: map

(defn double-values [v]
  )

; Return the sum of all values in vector:
; hint: reduce

(defn sum-of-values [v]
  )

;;
;; =========================================================
;; Sets in detail:
;; =========================================================
;;

; Add value to set:

(defn add-to-set [s value]
  )

; Remove from set:

(defn remove-from-set [s value]
  )

; Sets are also functions:

; Change 'is-viwel' so that it returns truthy value if
; given character is a vowel:

(def vowels #{\a \e \i \o \u \y})

(defn is-vowel [c]
  )

;;
;; =========================================================
;; Maps in detail:
;; =========================================================
;;

(defn add-to-map [m key value]
  )

(defn remove-from-map [m key]
  )

; Guess what, maps are also functions.

(def twitter {"jarppe"  "@jarppe"
              "tommi"   "@ikitommi"})

(defn get-twitter-handle [user-name]
  )

;;
;; =========================================================
;; Keywords:
;;   Interned strings
;; =========================================================
;;

(def keyword-literal      :foo)
(def keyword-from-string  (keyword "foo"))

; Why we need keywords?
; Comparing strings is done with String.equals, character by character
(if (= "foozaa" "foozab") "they differ")

; Comparing keywords is dome by ==
(if (= :foozaa :foozab) "they differ")

; Keywords are functions:

(def twitter {:jarppe  "@jarppe"
              :tommi   "@ikitommi"})

(:tommi twitter)
;=> "@ikitommi"

;;
;; =========================================================
;; Lists:
;;   By default, lists evaluate to function calls:
;; =========================================================
;;

(+ 1 2)
;=> 3

; Use quote to stop evaluation:

(quote (+ 1 2))
;=> (+ 1 2)

; ' is a reader macro for quote

'(+ 1 2)
;=> (+ 1 2)

; Remember how conj added to the end of vector? The "most convenient place to add"
; in lists is head.

(conj '(1 2 3) 4)
;=> (4 1 2 3)

;;
;; =========================================================
;; Sequences:
;;   Think java.util.Iterator
;; =========================================================
;;

; Getting a seq to things:

(seq [1 2 3])
;=> (1 2 3)

(seq '(1 2 3))
;=> (1 2 3)

(seq "hello")
;=> (\h \e \l \l \o)

(seq {:a 1 :b 2})
;=> ([:a 1] [:b 2])

(seq [])
;=> nil

(seq '())
;=> nil

; Creating a sequence: cons

(cons "a" (cons "b" (cons "c" nil)))
;=> ("a" "b" "c")

(cons "a" ["b" "c"])
;=> ("a" "b" "c")

(conj (cons "a" nil) "b")
;=> ("b" "a")

;;
;; =========================================================
;; Regular expressions:
;; =========================================================
;;

(def p (re-pattern "\\d+"))  ; Calls java.util.regex.Pattern/compile

(re-matches p "123")
;=> "123"

(re-matches p "a123")
;=> nil

; Try also:
;  re-find and re-seq

; Same, but using literal form:

(def p #"\d+")    ; notice, no escaping on \

(re-matches p "123")
;=> "123"
