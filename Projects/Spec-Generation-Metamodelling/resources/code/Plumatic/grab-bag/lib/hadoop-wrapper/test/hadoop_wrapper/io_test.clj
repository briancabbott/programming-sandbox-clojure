(ns hadoop-wrapper.io-test
  (:use plumbing.test)
  (:require [hadoop-wrapper.io :refer :all]
            [clojure.test :refer :all]))

(deftest sequence-file-reader-test
  (let [uncompressed (byte-array [83 69 81 6 25 111 114 103 46 97 112 97 99 104 101 46 104 97 100 111 111 112 46 105 111 46 84 101 120 116 33 111 114 103 46 97 112 97 99 104 101 46 104 97 100 111 111 112 46 105 111 46 76 111 110 103 87 114 105 116 97 98 108 101 0 0 0 0 0 0 -82 25 39 98 -125 -23 62 31 -77 36 90 -8 50 -1 -54 81 0 0 0 12 0 0 0 4 3 97 110 100 0 0 0 0 0 0 1 44 0 0 0 13 0 0 0 5 4 104 97 116 101 0 0 0 0 0 0 0 100 0 0 0 13 0 0 0 5 4 108 111 118 101 0 0 0 0 0 0 0 -56 0 0 0 14 0 0 0 6 5 112 101 97 99 101 0 0 0 0 0 0 0 100 0 0 0 12 0 0 0 4 3 119 97 114 0 0 0 0 0 0 0 -56])
        compressed (byte-array [83 69 81 6 25 111 114 103 46 97 112 97 99 104 101 46 104 97 100 111 111 112 46 105 111 46 84 101 120 116 33 111 114 103 46 97 112 97 99 104 101 46 104 97 100 111 111 112 46 105 111 46 76 111 110 103 87 114 105 116 97 98 108 101 1 1 42 111 114 103 46 97 112 97 99 104 101 46 104 97 100 111 111 112 46 105 111 46 99 111 109 112 114 101 115 115 46 68 101 102 97 117 108 116 67 111 100 101 99 0 0 0 0 120 3 -19 32 113 -116 -72 112 43 -118 -49 -119 63 65 -127 75 -1 -1 -1 -1 120 3 -19 32 113 -116 -72 112 43 -118 -49 -119 63 65 -127 75 5 13 120 -100 99 97 101 101 99 1 0 0 76 0 25 32 120 -100 99 78 -52 75 97 -55 72 44 73 101 -55 -55 47 75 101 45 72 77 76 78 101 46 79 44 2 0 94 36 7 -25 11 120 -100 -29 -32 0 2 0 0 125 0 41 21 120 -100 99 96 0 1 70 29 6 8 72 -127 -46 39 -48 -7 0 33 78 2 -122])
        result [["and" 300] ["hate" 100] ["love" 200] ["peace" 100] ["war" 200]]]
    (is-= result (sequence-file-reader compressed))
    (is-= result (sequence-file-reader uncompressed))))