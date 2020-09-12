# Introduction to test.check

test.check is a tool for writing property-based tests. This differs from
traditional unit-testing, where you write individual test-cases. With
test.check you write universal quantifications, properties that should hold
true for all input. For example, for all vectors, reversing the vector should
preserve the count. Reversing it twice should equal the input. In this guide,
we'll cover the thought process for coming up with properties, as well as the
practice of writing the tests themselves.

## A simple example

First, let's start with an example, suppose we want to test a sort function.
It's easy to come up with some trivial properties for our function, namely that
the output should be in ascending order. We also might want to make sure that
the count of the input is preserved. Our test might look like:

```clojure
(require '[clojure.test.check :as tc]
         '[clojure.test.check.generators :as gen]
         '[clojure.test.check.properties :as prop #?@(:cljs [:include-macros true])])

(def property
  (prop/for-all [v (gen/vector gen/int)]
    (let [s (sort v)]
      (and (= (count v) (count s))
           (or (empty? s)
               (apply <= s))))))

;; test our property
(tc/quick-check 100 property)
;; => {:result true,
;; =>  :pass? true,
;; =>  :num-tests 100,
;; =>  :time-elapsed-ms 90,
;; =>  :seed 1528578896309}
```

What if we were to forget to actually sort our vector? The test will fail, and
then test.check will try and find 'smaller' inputs that still cause the test
to fail. For example, the function might originally fail with input:
`[5 4 2 2 2]`, but test.check will shrink this down to `[0 -1]` (or `[1 0]`).

```clojure
(def bad-property
  (prop/for-all [v (gen/vector gen/int)]
    (or (empty? v) (apply <= v))))

(tc/quick-check 100 bad-property)
;; => {:num-tests 6,
;; =>  :seed 1528579035247,
;; =>  :fail [[-2 -4 -4 -3]],
;; =>  :failed-after-ms 1,
;; =>  :result false,
;; =>  :result-data nil,
;; =>  :failing-size 5,
;; =>  :pass? false,
;; =>  :shrunk
;; =>  {:total-nodes-visited 16,
;; =>   :depth 4,
;; =>   :pass? false,
;; =>   :result false,
;; =>   :result-data nil,
;; =>   :time-shrinking-ms 1,
;; =>   :smallest [[0 -1]]}}
```

This process of shrinking is done automatically, even for our more complex
generators that we write ourselves.

## Generators

In order to write our property, we'll use generators. A generator knows how to
generate random values for a specific type. The `test.check.generators`
namespace has many built-in generators, as well as combinators for creating
your own new generators. You can write sophisticated generators just by
combining the existing generators with the given combinators. As we write
generators, we can see them in practice with the `sample` function:

```clojure
(require '[clojure.test.check.generators :as gen])

(gen/sample gen/int)
;; => (0 1 -1 0 -1 4 4 2 7 1)
```

we can ask for more samples:

```clojure
(gen/sample gen/int 20)
;; => (0 1 1 0 2 -4 0 5 -7 -8 4 5 3 11 -9 -4 6 -5 -3 0)
```

or get a lazy-seq of values:


```clojure
(take 1 (gen/sample-seq gen/int))
;; => (0)
```

You may notice that as you ask for more values, the 'size' of the generated
values increases. As test.check generates more values, it increases the
'size' of the generated values. This allows tests to fail early, for simple
values, and only increase the size as the test continues to pass.

### Compound generators

Some generators take other generators as arguments. For example the `vector`
and `list` generator:


```clojure
(gen/sample (gen/vector gen/nat))
;; => ([] [] [1] [1] [] [] [5 6 6 2 0 1] [3 7 5] [2 0 0 6 2 5 8] [9 1 9 3 8 3 5])

(gen/sample (gen/list gen/boolean))
;; => (() () (false) (false true false) (false true) (false true true true) (true) (false false true true) () (true))

(gen/sample (gen/map gen/keyword gen/boolean) 5)
;; => ({} {:z false} {:k true} {:v8Z false} {:9E false, :3uww false, :2s true})
```

Sometimes we'll want to create heterogeneous collections. The `tuple` generator
allows us to do this:

```clojure
(gen/sample (gen/tuple gen/nat gen/boolean gen/ratio))
;; => ([0 false 0] [1 false 0] [0 false 2] [0 false -1/3] [1 true 2] [1 false 0] [2 false 3/5] [3 true -1] [3 true -5/3] [6 false 9/5])
```

### Generator combinators

There are several generator combinators, we'll take a look at `fmap`,
`such-that` and `bind`.

#### fmap

`fmap` allows us to create a new generator by applying a function to the
values generated by another generator. Let's say we want to to create a set of
natural numbers. We can create a set by calling `set` on a vector. So let's
create a vector of natural numbers (using the `nat` generator), and then use
`fmap` to call `set` on the values:

```clojure
(gen/sample (gen/fmap set (gen/vector gen/nat)))
;; => (#{} #{1} #{1} #{3} #{0 4} #{1 3 4 5} #{0 6} #{3 4 5 7} #{0 3 4 5 7} #{1 5})
```

Imagine you have a record, that has a convenience creation function, `foo`. You
can create random `foo`s by generating the types of the arguments to `foo` with
`tuple`, and then using `(fmap foo (tuple ...))`.

#### such-that

`such-that` allows us to create a generator that passes a predicate. Imagine we
wanted to generate non-empty lists, we can use `such-that` to filter out empty
lists:

```clojure
(gen/sample (gen/such-that not-empty (gen/list gen/boolean)))
;; => ((true) (true) (false) (true false) (false) (true) (false false true true) (false) (true) (false))
```

#### bind

`bind` allows us to create a new generator based on the _value_ of a previously
created generator. For example, say we wanted to generate a vector of keywords,
and then choose a random element from it, and return both the vector and the
random element. `bind` takes a generator, and a function that takes a value
from that generator, and creates a new generator.

```clojure
(def keyword-vector (gen/such-that not-empty (gen/vector gen/keyword)))
(def vec-and-elem
  (gen/bind keyword-vector
            (fn [v] (gen/tuple (gen/elements v) (gen/return v)))))

(gen/sample vec-and-elem 4)
;; => ([:va [:va :b4]] [:Zu1 [:w :Zu1]] [:2 [:2]] [:27X [:27X :KW]])
```

This allows us to build quite sophisticated generators.

### Record generators

Let's go through an example of generating random values of our own
`defrecord`s. Let's create a simple user record:

```clojure
(defrecord User [user-name user-id email active?])

;; recall that a helper function is automatically generated
;; for us

(->User "reiddraper" 15 "reid@example.com" true)
;; #user.User{:user-name "reiddraper",
;;            :user-id 15,
;;            :email "reid@example.com",
;;            :active? true}
```

We can use the `->User` helper function to construct our user. First, let's
look at the generators we'll use for the arguments. For the user-name, we can
just use an alphanumeric string, user IDs will be natural numbers, we'll
construct our own simple email generator, and we'll use booleans to denote
whether the user account is active. Let's write a simple email address
generator:

```clojure
(def domain (gen/elements ["gmail.com" "hotmail.com" "computer.org"]))
(def email-gen
  (gen/fmap (fn [[name domain-name]]
              (str name "@" domain-name))
            (gen/tuple (gen/not-empty gen/string-alphanumeric) domain)))

(last (gen/sample email-gen))
;; => "CW6161Q6@hotmail.com"
```

To put it all together, we'll use `fmap` to call our record constructor, and
`tuple` to create a vector of the arguments:

```clojure
(def user-gen
  (gen/fmap (partial apply ->User)
            (gen/tuple (gen/not-empty gen/string-alphanumeric)
                       gen/nat
                       email-gen
                       gen/boolean)))

(last (gen/sample user-gen))
;; => #user.User{:user-name "kWodcsE2",
;;               :user-id 1,
;;               :email "r2ed3VE@computer.org",
;;               :active? true}
```

### Recursive generators

---
NOTE: Writing recursive generators was significantly simplified in version
0.5.9. For the old way, see the [0.5.8
documentation](https://github.com/clojure/test.check/blob/v0.5.8/doc/intro.md#recursive-generators).

---

Writing recursive, or tree-shaped generators is easy using `gen/recursive-gen`.
`recursive-gen` takes two arguments, a compound generator, and a scalar
generator. We'll start with a simple example, and then move into something more
complex. First, let's generate a nested vector of booleans. So our compound
generator will be `gen/vector` and our scalar will be `gen/boolean`:

```clojure
(def nested-vector-of-boolean (gen/recursive-gen gen/vector gen/boolean))
(last (gen/sample nested-vector-of-boolean 20))
;; => [[[true] true] [[] []]]
```

Now, let's make our own, JSON-like generator. We'll allow `gen/list` and
`gen/map` as our compound types and `gen/int` and `gen/boolean` as our scalar
types. Since `recursive-gen` only accepts one of each type of generator, we'll
combine our compound types with a simple function, and the two scalars with
`gen/one-of`.

```clojure
(def compound (fn [inner-gen]
                  (gen/one-of [(gen/list inner-gen)
                               (gen/map inner-gen inner-gen)])))
(def scalars (gen/one-of [gen/int gen/boolean]))
(def my-json-like-thing (gen/recursive-gen compound scalars))
(last (gen/sample my-json-like-thing 20))
;; =>
;; (()
;;  {{4 -11, 1 -19} (false),
;;  {} {1 6},
;;  (false false) {true -3, false false, -7 1}})
```

And we see we got a list whose first element is the empty list. The second
element is a map with int keys and values. Etc.

---

Check out [page two](generator-examples.md) for more examples of using
generators in practice.