This repo is a demonstration of [this bug report](https://github.com/crisptrutski/boot-cljs-test/issues/60) on [boot-cljs-test](https://github.com/crisptrutski/boot-cljs-test)

This repo includes a very simple set of tests, one passing and one failing.
It uses [boot-cljs-test](https://github.com/crisptrutski/boot-cljs-test) to run the tests.

## PhantomJS

Running the tests with Phantom works just fine:

```console
$ boot test-in-phantom
Adding: ([adzerk/boot-cljs "1.7.228-2"] [doo "0.1.7"]) to :dependencies
Compiling ClojureScript...
• cljs_test/generated_test_suite.js
Running cljs tests...

;; ======================================================================
;; Testing with Phantom:

[doo] Command to run script: [phantomjs /var/folders/7m/8846n6kn08v2cw2m09hdfs540000gn/T/phantom7645804787916866875.js /var/folders/7m/8846n6kn08v2cw2m09hdfs540000gn/T/phantom-shim5992225626840496523.js /Users/jeff/.boot/cache/tmp/Users/jeff/spark/cljs-test-problem/81w/oz0m8x/cljs_test/generated_test_suite.js]

Testing sparkfund.ui-test

FAIL in (plus-one) (:)
deliberately failing case
expected: (= 5 (ui/plus-one 1))
  actual: (not (= 5 2))

Ran 1 tests containing 2 assertions.
1 failures, 0 errors.

...(failure stack trace snipped)...
```

## Chrome/Firefox

Running the tests in Chrome/Firefox (via Karma) does not work.  A Chrome window briefly appears,
but then immediately disappears before any tests can be executed:

```console
$ node --version                                                                                                                    ✘
v6.10.0

$ npm install --global karma karma-chrome-launcher karma-firefox-launcher karma-cljs-test
...

$ boot test-in-chrome
Adding: ([adzerk/boot-cljs "1.7.228-2"] [doo "0.1.7"]) to :dependencies
Compiling ClojureScript...
• cljs_test/generated_test_suite.js
Running cljs tests...

;; ======================================================================
;; Testing with Chrome:

[doo] Created karma conf file: /var/folders/7m/8846n6kn08v2cw2m09hdfs540000gn/T/karma_conf4885380502268738690.js
[doo] Started karma server
25 05 2017 15:10:21.314:WARN [watcher]: All files matched by "/Users/jeff/.boot/cache/tmp/Users/jeff/spark/cljs-test-problem/86v/oz0m8x/cljs_test/generated_test_suite.out/**/*.js" were excluded or matched by prior matchers.
25 05 2017 15:10:21.317:WARN [watcher]: All files matched by "/Users/jeff/.boot/cache/tmp/Users/jeff/spark/cljs-test-problem/86v/oz0m8x/cljs_test/generated_test_suite.out/*.js" were excluded or matched by prior matchers.
[doo] Started karma run
Close Karma run
Shutdown Karma Server
```
