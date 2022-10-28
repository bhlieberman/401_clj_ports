(ns day-one
  (:require [clojure.test :refer [deftest is run-tests testing]])
  (:import [java.time LocalDateTime]
           [java.time.format DateTimeFormatter]))

(defn pluralize [s n]
  (cond
    (= n 1) (str "I own " n " " s ".")
    (or (> n 1) (= n 0)) (str "I own " n " " s "s.")))

(defn flip-n-heads [n]
  (loop [heads 0
         flips 0]
    (cond
      (= heads n) (str "It took " flips " flips to get " n " heads in a row")
      (< heads n) (if (>= (Math/random) 0.5)
                    (recur (inc heads) (inc flips))
                    (recur 0 (inc flips))))))

(defn clock []
  (let [pat (DateTimeFormatter/ofPattern "HH:mm:ss")]
    (loop [time ""
           current (.format (LocalDateTime/now) pat)]
      (if (not= time current)
        (do (println time)
            (recur current current))
        (recur current (.format (LocalDateTime/now) pat))))))

(deftest pluralize-test
  (testing "That strings are formatted conditionally given certain values of n"
    (is (= (pluralize "cat" 3) "I own 3 cats."))))

(deftest flip-n-heads-test
  (testing "That the loop terminates after n flips in a row turn up heads"
    (is (string? (flip-n-heads 10)))))

(deftest clock-test
  (testing "That java.time LocalDateTime functions are working as expected"
    (is (= LocalDateTime (type (clock))))))

(run-tests)
