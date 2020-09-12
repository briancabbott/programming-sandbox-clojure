(ns clojure-bootcamp-intro.poker)

; Let's implement card using a vector, first element 
; is the suite and second is the card value:

(defn card [card-suite card-value]
  [card-suite card-value])

(defn suite [card]
  (first card))

(defn value [card]
  (second card))

(defn suite? [s card]
  (= (suite card) s))

; suites:
;   clubs    = :c
;   diamonds = :d
;   hearts   = :h
;   spades   = :s

; values:
;   normal cards 2 .. 14
;   jack   = :j
;   queen  = :q
;   king   = :k
;   ace    = :a

(def deck
  ; TODO: Make deck with 52 cards, 13 from each suite
  [])

(defn shuffle-cards [cards]
  ; TODO: Shuffle cards
  cards)

(defn hands [cards]
  ; TODO: Return seq of hands, each with 5 cards
  )

(defn high [hand]
  ; TODO: Return value of highest card in hand
  )

(defn color? [hand]
  ; TODO: Return truthy if all cards belong to same suite
  )

(defn pair? [hand]
  ; TODO: Return truthy if hand contains a pair
  )

(defn three-of-kind? [hand]
  ; TODO: Return truthy if hand contains three of a kind
  )

(defn straight? [hand]
  ; TODO: Return truthy if cards have consecutive  values
  )

(defn straight-flush? [hand]
  ; TODO: Return truthy if hand is a straing flush?
  )

(defn rate [hand]
  ; TODO: Return:
  ;    400 for straight flush
  ;    300 for straight
  ;    200 for three-of-kind
  ;    100 for a pair
  ; If non of the above match, return value of highest card.
  )

; Place your bets and have some fn

(defn play []
  (let [game-deck  (shuffle-cards deck)
        game-hands (hands game-deck)
        hand1      (first game-hands)
        hand2      (second game-hands)
        value1     (rate hand1)
        value2     (rate hand2)]
    (println "player 1:" hand1 "=>" value1)
    (println "player 2:" hand2 "=>" value2)
    (cond
      (> value1 value2)  (println "Winner: player 1")
      (< value1 value2)  (println "Winner: player 2")
      :else              (println "It's a tie!"))))
