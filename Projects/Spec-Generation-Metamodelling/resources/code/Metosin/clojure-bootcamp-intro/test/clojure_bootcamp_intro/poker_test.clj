(ns clojure-bootcamp-intro.poker-test
  (:require [midje.sweet :refer :all]
            [clojure-bootcamp-intro.poker :refer :all]))

(facts card
  (card ..suite.. ..value..)         => [..suite.. ..value..]
  (suite (card ..suite.. ..value..)) => ..suite..
  (value (card ..suite.. ..value..)) => ..value..)


(facts deck
  (count deck) => 52
  (take 5 deck) => [[:c 2]  [:c 3]  [:c 4]  [:c 5]  [:c 6]]
  (count (filter (partial suite? :h) deck)) => 13
  (count (shuffle-cards deck)) => 52
  (shuffle-cards deck) =not=> deck)

(facts hands
  (first (hands deck)) => [[:c 2]  [:c 3]  [:c 4]  [:c 5]  [:c 6]])

(facts high
  (high [[:c 2]  [:c 3]  [:c 4]   [:c 5]  [:c 10]]) => 10
  (high [[:c 2]  [:c 3]  [:c 10]  [:c 5]  [:d 6]])  => 10)

(facts color?
  (color? [[:c 2]  [:c 3]  [:c 4]  [:c 5]  [:c 6]]) => truthy
  (color? [[:c 2]  [:c 3]  [:c 4]  [:c 5]  [:d 6]]) => falsey)

(facts pair?
  (pair? [[:c 2] [:h 4] [:h 6] [:d 2] [:h 7]]) => truthy
  (pair? [[:c 2] [:h 4] [:h 6] [:c 3] [:h 7]]) => falsey)

(facts three-of-kind?
  (three-of-kind? [[:c 2] [:h 4] [:h 2] [:d 2] [:h 7]]) => truthy
  (three-of-kind? [[:c 2] [:h 4] [:h 6] [:d 2] [:h 7]]) => falsey)

(facts straight?
  (straight? [[:d 5] [:c 2] [:c 4] [:d 6] [:h 3]])  => truthy
  (straight? [[:d 5] [:c 2] [:c 4] [:d 8] [:h 3]])  => falsey)

(facts straight-flush?
  (straight-flush? [[:c 5]  [:c 2] [:c 4] [:c 6] [:c 3]]) => truthy
  (straight-flush? [[:c 10] [:c 2] [:c 4] [:c 6] [:c 3]]) => falsey
  (straight-flush? [[:d 5]  [:c 2] [:c 4] [:c 6] [:c 3]]) => falsey)

(facts rate
  (rate [[:c 5] [:c 2] [:c 4] [:c 6] [:c 3]]) => 400
  (rate [[:d 5] [:c 2] [:c 4] [:d 6] [:h 3]]) => 300
  (rate [[:c 2] [:h 4] [:h 2] [:d 2] [:h 7]]) => 200
  (rate [[:c 2] [:h 4] [:h 6] [:d 2] [:h 7]]) => 100
  (rate [[:c 2] [:c 3] [:c 4] [:c 5] [:c 9]]) => 9)
