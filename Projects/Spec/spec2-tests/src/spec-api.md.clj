

"A soft limit on how many times a branching spec (or/alt/*/opt-keys/multi-spec)
 can be recursed through during generation. After this a non-recursive branch
 will be chosen."
*recursion-limit*


"The number of times an anonymous fn specified by fspec will be (generatively)
 tested during conform"
*fspec-iterations*


"The number of elements validated in a collection spec'ed with 'every'"
 *coll-check-limit*


"The number of errors reported by explain in a collection spec'ed with 'every'"
 *coll-error-limit*


"Returns the registry map, prefer 'get-spec' to lookup a spec by name"
registry


"returns x if x is a spec object, else logical false"
spec?


"Returns x if x is a schema object, else logical false"
 schema?


"Returns spec registered for keyword/symbol/var k, or nil."
 get-spec


"returns the spec/regex at end of alias chain starting with k, nil
 if not found, k if k not ident"
 reg-resolve


"returns the spec/regex at end of alias chain starting with k, throws
 if not found, k if k not ident"
 reg-resolve!


"returns x if x is a (clojure.spec) regex op, else logical false"
 regex?


"tests the validity of a conform return value"
 invalid?


"Create a spec object from an explicated spec form. This is an extension
 point for adding new spec forms. Generally, consumers should instead call
 `spec*` instead."
 create-spec


"Returns a spec object given a fully-qualified spec op form, symbol, set,
 or registry identifier. If needed, use 'explicate' to qualify forms."
 spec*


"Returns a schema object given a fully-qualified schema definition.
 If needed use 'explicate' to qualify forms."
 schema*


"Given a spec and a value, returns :clojure.spec-alpha2/invalid
 if value does not match spec, else the (possibly destructured) value."
 conform


"Given a spec and a value created by or compliant with a call to
 'conform' with the same spec, returns a value with all conform
 destructuring undone."
 unform


"returns the spec as data"
 form

"returns an abbreviated description of the spec as data"
 describe


"Given a spec and a value x which ought to conform, returns nil if x
 conforms, else a map with at least the key ::problems whose value is
 a collection of problem-maps, where problem-map has at least :path :pred and :val
 keys describing the predicate and the value that failed at that path."
 explain-data


"Default printer for explain-data. nil indicates a successful validation."
 explain-printer


"Prints explanation data (per 'explain-data') to *out* using the printer in *explain-out*,
 by default explain-printer."
 explain-out


"Given a spec and a value that fails to conform, prints an explanation to *out*."
 explain


"Given a spec and a value that fails to conform, returns an explanation as a string."
 explain-str


"Helper function that returns true when x is valid for spec."
 valid?


"Given a spec, returns the generator for it, or throws if none can
 be constructed. Optionally an overrides map can be provided which
 should map spec names or paths (vectors of keywords) to no-arg
 generator-creating fns. These will be used instead of the generators at those
 names/paths. Note that parent generator (in the spec or overrides
 map) will supersede those of any subtrees. A generator for a regex
 op must always return a sequential collection (i.e. a generator for
 s/? should return either an empty sequence/vector or a
 sequence/vector with one item in it)"
 gen


"Return a fully-qualified form given a namespace name context and a form"
 explicate


"Qualify symbol s by resolving it or using the current *ns*."
 ns-qualify


"Given a literal vector or map schema, expand to a proper explicated spec
 form, which when evaluated yields a schema object."
 schema


"Takes schemas and unions them, returning a schema object"
 union


"Given a function symbol, set of constants, or anonymous function,
 returns a spec object."
 spec


"Given a namespace-qualified keyword or resolvable symbol k, and a
 spec-name or spec object, makes an entry in the registry mapping k
 to the spec. Use nil to remove an entry in the registry for k."
 register


"Given a namespace-qualified keyword or resolvable symbol k, and a
 spec-name or symbolic spec, makes an entry in the registry mapping k
 to the spec. Use nil to remove an entry in the registry for k."
 def


"Takes a spec and a no-arg, generator-returning fn and returns a version of
 that spec that uses that generator"
 with-gen


"Given namespace-qualified keywords, switches those specs to closed
 mode checking."
 close-specs


"Given namespace-qualified keywords, switches those specs to open
 mode checking."
 open-specs


"Takes map-validating specs (e.g. 'keys' specs) and
 returns a spec that returns a conformed map satisfying all of the
 specs.  Unlike 'and', merge can generate maps satisfying the
 union of the predicates."
 merge


"takes a pred and validates collection elements against that pred.

 Note that 'every' does not do exhaustive checking, rather it samples
 *coll-check-limit* elements. Nor (as a result) does it do any
 conforming of elements. 'explain' will report at most *coll-error-limit*
 problems.  Thus 'every' should be suitable for potentially large
 collections.

 Takes several kwargs options that further constrain the collection:

 :kind - a pred that the collection type must satisfy, e.g. vector?
       (default nil) Note that if :kind is specified and :into is
       not, this pred must generate in order for every to generate.
 :count - specifies coll has exactly this count (default nil)
 :min-count, :max-count - coll has count (<= min-count count max-count) (defaults nil)
 :distinct - all the elements are distinct (default nil)

 And additional args that control gen

 :gen-max - the maximum coll size to generate (default 20)
 :into - one of [], (), {}, #{} - the default collection to generate into
     (default: empty coll as generated by :kind pred if supplied, else [])

 Optionally takes :gen generator-fn, which must be a fn of no args that
 returns a test.check generator

 See also - coll-of, every-kv"
 every


"like 'every' but takes separate key and val preds and works on associative collections.
 Same options as 'every', :into defaults to {}
 See also - map-of"
 every-kv


"Returns a spec for a collection of items satisfying pred. Unlike
 'every', coll-of will exhaustively conform every value.

 Same options as 'every'. conform will produce a collection
 corresponding to :into if supplied, else will match the input collection,
 avoiding rebuilding when possible.

 See also - every, map-of"
 coll-of


"Returns a spec for a map whose keys satisfy kpred and vals satisfy
 vpred. Unlike 'every-kv', map-of will exhaustively conform every
 value.

 Same options as 'every', :kind defaults to map?, with the addition of:

 :conform-keys - conform keys as well as values (default false)

 See also - every-kv"
 map-of

"Returns a regex op that matches zero or more values matching
 pred. Produces a vector of matches iff there is at least one match"
 *

"Returns a regex op that matches one or more values matching
 pred. Produces a vector of matches"
 +

"Returns a regex op that matches zero or one value matching
 pred. Produces a single value (not a collection) if matched."
 ?

"Takes key+pred pairs, e.g.
 (s/alt :even even? :small #(< % 42))

 Returns a regex op that returns a map entry containing the key of the
 first matching pred and the corresponding value. Thus the
 'key' and 'val' functions can be used to refer generically to the
 components of the tagged return"
 alt


"Takes key+pred pairs, e.g.

 (s/cat :e even? :o odd?)

 Returns a regex op that matches (all) values in sequence, returning a map
 containing the keys of each pred and the corresponding value."
 cat


"takes a regex op re, and predicates. Returns a regex-op that consumes
 input as per re but subjects the resulting value to the
 conjunction of the predicates, and any conforming they might perform."
 &


"takes a regex op and returns a non-regex op that describes a nested
 sequential collection."
 nest


"takes a predicate function with the semantics of conform i.e. it should return either a
 (possibly converted) value or :clojure.spec-alpha2/invalid, and returns a
 spec that uses it as a predicate/conformer. Optionally takes a
 second fn that does unform of result of first"
 conformer


"takes :args :ret and (optional) :fn kwargs whose values are preds
 and returns a spec whose conform/explain take a fn and validates it
 using generative testing. The conformed value is always the fn itself.

 See 'fdef' for a single operation that creates an fspec and
 registers it, as well as a full description of :args, :ret and :fn

 fspecs can generate functions that validate the arguments and
 fabricate a return value compliant with the :ret spec, ignoring
 the :fn spec if present.

 Optionally takes :gen generator-fn, which must be a fn of no args
 that returns a test.check generator."
 fspec


"Takes a symbol naming a function, and one or more of the following:

 :args A regex spec for the function arguments as they were a list to be
   passed to apply - in this way, a single spec can handle functions with
   multiple arities
 :ret A spec for the function's return value
 :fn A spec of the relationship between args and ret - the
   value passed is {:args conformed-args :ret conformed-ret} and is
   expected to contain predicates that relate those values

 Qualifies fn-sym with resolve, or using *ns* if no resolution found.
 Registers an fspec in the global registry, where it can be retrieved
 by calling get-spec with the var or fully-qualified symbol.

 Once registered, function specs are included in doc, checked by
 instrument, tested by the runner clojure.spec-alpha2.test/check, and (if
 a macro) used to explain errors during macroexpansion.

 Note that :fn specs require the presence of :args and :ret specs to
 conform values, and so :fn specs will be ignored if :args or :ret
 are missing.

 Returns the qualified fn-sym.

 For example, to register function specs for the symbol function:

 (s/fdef clojure.core/symbol
    :args (s/alt :separate (s/cat :ns string? :n string?)
                 :str string?
                 :sym symbol?)
    :ret symbol?)"
 fdef


"Creates and returns a map validating spec. :req and :opt are both
 vectors of namespaced-qualified keywords. The validator will ensure
 the :req keys are present. The :opt keys serve as documentation and
 may be used by the generator.

 The :req key vector supports 'and' and 'or' for key groups:

 (s/keys :req [::x ::y (or ::secret (and ::user ::pwd))] :opt [::z])

 There are also -un versions of :req and :opt. These allow
 you to connect unqualified keys to specs.  In each case, fully
 qualfied keywords are passed, which name the specs, but unqualified
 keys (with the same name component) are expected and checked at
 conform-time, and generated during gen:

 (s/keys :req-un [:my.ns/x :my.ns/y])

 The above says keys :x and :y are required, and will be validated
 and generated by specs (if they exist) named :my.ns/x :my.ns/y
 respectively.

 In addition, the values of *all* namespace-qualified keys will be validated
 (and possibly destructured) by any registered specs. Note: there is
 no support for inline value specification, by design.

 Optionally takes :gen generator-fn, which must be a fn of no args that
 returns a test.check generator."
 keys


"Takes a keyset and a selection pattern and returns a spec that
 validates a map. The keyset specifies what keys may be in the map
 and the specs to use if the keys are unqualified. The selection
 pattern indicates what keys must be in the map, and any nested
 maps."
 select


"Takes the name of a spec/predicate-returning multimethod and a
 tag-restoring keyword or fn (retag).  Returns a spec that when
 conforming or explaining data will pass it to the multimethod to get
 an appropriate spec. You can e.g. use multi-spec to dynamically and
 extensibly associate specs with 'tagged' data (i.e. data where one
 of the fields indicates the shape of the rest of the structure).

 (defmulti mspec :tag)

 The methods should ignore their argument and return a predicate/spec:
 (defmethod mspec :int [_] (s/keys :req-un [::tag ::i]))

 retag is used during generation to retag generated values with
 matching tags. retag can either be a keyword, at which key the
 dispatch-tag will be assoc'ed, or a fn of generated value and
 dispatch-tag that should return an appropriately retagged value.

 Note that because the tags themselves comprise an open set,
 the tag key spec cannot enumerate the values, but can e.g.
 test for keyword?.

 Note also that the dispatch values of the multimethod will be
 included in the path, i.e. in reporting and gen overrides, even
 though those values are not evident in the spec."
 multi-spec


"takes one or more preds and returns a spec for a tuple, a vector
 where each element conforms to the corresponding pred. Each element
 will be referred to in paths using its ordinal."
 tuple


"Takes key+pred pairs, e.g.

 (s/or :even even? :small #(< % 42))

 Returns a destructuring spec that returns a map entry containing the
 key of the first matching pred and the corresponding value. Thus the
 'key' and 'val' functions can be used to refer generically to the
 components of the tagged return."
 or


"Takes predicate/spec-forms, e.g.

 (s/and even? #(< % 42))

 Returns a spec that returns the conformed value. Successive
 conformed values propagate through rest of predicates."
 and


"takes the same arguments as spec/keys and returns a regex op that matches sequences of key/values,
 converts them into a map, and conforms that map with a corresponding
 spec/keys call:

 user=> (s/conform (s/keys :req-un [::a ::c]) {:a 1 :c 2})
 {:a 1, :c 2}
 user=> (s/conform (s/keys* :req-un [::a ::c]) [:a 1 :c 2])
 {:a 1, :c 2}

 the resulting regex op can be composed into a larger regex:

 user=> (s/conform (s/cat :i1 integer? :m (s/keys* :req-un [::a ::c]) :i2 integer?) [42 :a 1 :c 2 :d 4 99])
        {:i1 42, :m {:a 1, :c 2, :d 4}, :i2 99}"
 keys*


"takes a spec and returns a spec that has the same properties except
 'conform' returns the original (not the conformed) value."
 nonconforming


"returns a spec that accepts nil and values satisfying pred"
 nilable


"generates a number (default 10) of values compatible with spec and maps conform over them,
 returning a sequence of [val conformed-val] tuples. Optionally takes
 a generator overrides map as per gen"
 exercise


"exercises the fn named by sym (a symbol) by applying it to
 n (default 10) generated samples of its args spec. When fspec is
 supplied its arg spec is used, and sym-or-f can be a fn.  Returns a
 sequence of tuples of [args ret]. "
 exercise-fn


"Return true if inst at or after start and before end"
 inst-in-range?


"Return true if start <= val, val < end and val is a fixed
 precision integer."
 int-in-range?


"If true, compiler will enable spec asserts, which are then
 subject to runtime control via check-asserts? If false, compiler
 will eliminate all spec assert overhead. See 'assert'.

 Initially set to boolean value of clojure.spec.compile-asserts
 system property. Defaults to true."
 *compile-asserts*


"Returns the value set by check-asserts."
 check-asserts?


"Enable or disable spec asserts that have been compiled
 with '*compile-asserts*' true.  See 'assert'.

 Initially set to boolean value of clojure.spec.check-asserts
 system property. Defaults to false."
 check-asserts


"Do not call this directly, use 'assert'."
 assert*


"spec-checking assert expression. Returns x if x is valid? according
 to spec, else throws an ex-info with explain-data plus ::failure of
 :assertion-failed.

 Can be disabled at either compile time or runtime:

 If *compile-asserts* is false at compile time, compiles to x. Defaults
 to value of 'clojure.spec.compile-asserts' system property, or true if
 not set.

 If (check-asserts?) is false at runtime, always returns x. Defaults to
 value of 'clojure.spec.check-asserts' system property, or false if not
 set. You can toggle check-asserts? with (check-asserts bool)."
 assert


"Do not call directly."
 sig-map


"Do not call directly, use `defop`"
 op-spec


"Defines a new spec op with op-name defined by the form. Defines a macro for op-name with docstring that
 expands to a call to spec* with the explicated form. args are replaced in the form. Creates a create-spec
 method implementation for op-name that creates a spec whose body is form.

 Opts allowed:
  :gen - takes a no-arg function returning a generator to use"
 defop


;; Load the spec op implementations
(load "/clojure/spec_alpha2/impl")


"Returns a spec that validates insts in the range from start
 (inclusive) to end (exclusive)."
 inst-in

"Returns a spec that validates fixed precision integers in the
 range from start (inclusive) to end (exclusive)."
 int-in

"Specs a 64-bit floating point number. Options:
  :infinite? - whether +/- infinity allowed (default true)
  :NaN?      - whether NaN allowed (default true)
  :min       - minimum value (inclusive, default none)
  :max       - maximum value (inclusive, default none)"
double-in















dynalock (Object.))
dynaload
quick-check-ref
quick-check
for-all*-ref

"Dynamically loaded clojure.test.check.properties/for-all*."
for-all*

"Generate a single value using generator."
generate
delay-impl

"given body that returns a generator, returns a
generator that delegates to that, but delays
creation until used."
delay

"Dynamically loads test.check generator named s."
gen-for-name

"Implementation macro, do not call directly."
lazy-combinator

"Implementation macro, do not call directly."
lazy-combinators

"Implementation macro, do not call directly."
lazy-prim

"Implementation macro, do not call directly."
lazy-prims
lazy-prims
   - any
   - any-printable
   - boolean
   - bytes
   - char
   - char-alpha
   - char-alphanumeric
   - char-ascii
   - double
   - int keyword
   - keyword-ns
   - large-integer
   - ratio
   - simple-type
   - simple-type-printable
   - string
   - string-ascii
   - string-alphanumeric
   - symbol
   - symbol-ns
   - uuid

"Returns a generator of a sequence catenated from results of
 gens, each of which should generate something sequential."
 cat

qualified?

gen-builtins

"Given a predicate, returns a built-in generator if one exists."
gen-for-pred


(gen-for-name 'clojure.test.check.generators/int)
(gen-for-name 'unqualified)
(gen-for-name 'clojure.core/+)
(gen-for-name 'clojure.core/name-does-not-exist)
(gen-for-name 'ns.does.not.exist/f)











(defprotocol Spec :extend-via-metadata true
  (conform* [spec x])
  (unform* [spec y])
  (explain* [spec path via in x])
  (gen* [spec overrides path rmap])
  (with-gen* [spec gfn])
  (describe* [spec]))

(defprotocol Schema (keyspecs* [spec] "Returns map of key to symbolic spec"))
(defprotocol Select "Marker protocol for selects")
(defprotocol Closable "A spec that can conform with closed semantics" (close* [spec] "Returns a Closed version of this spec"))
(defprotocol Closed "Protocol for specs that conform with closed semantics"
  (conform-closed* [spec x])
  (explain-closed* [spec path via in x])
  (open* [spec]))
