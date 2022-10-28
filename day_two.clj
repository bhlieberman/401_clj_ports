(ns day-two
  (:require [clojure.test :refer [deftest testing is run-tests]]))

(defn roll-dice [n]
  (loop [i 0
         acc []]
    (if (< i n)
      (recur (inc i) (conj acc (rand-nth (range 1 7))))
      acc)))

(defn contains-duplicates? [xs]
  (some true? (map #(if (> (count %) 1) true %)
                   (partition-by identity xs))))

(defn compute-avg [xs]
  (try (/ (reduce #(+ %1 %2) 0 xs) (count xs))
       (catch ArithmeticException _
         0)))

(defn xs-of-avgs [xs]
  (->> xs
       (map-indexed
        (comp second
              (fn [i x] ;; index is unused
                [i (/
                    (reduce (fn [acc x] (+ acc x)) x)
                    (count x))])))
       (apply min)))

(deftest test-roll-dice
  (testing "That the dice are rolled n times"
    (is (= 3 (count (roll-dice 3))))))

(deftest test-duplicates
  (testing "That seqs with and without duplicates return true and nil respectively"
    (is (true? (contains-duplicates? [1 1 2 3])))
    (is (nil? (contains-duplicates? [0 1 2])))))

(deftest test-compute-avg
  (testing "That average is non-zero"
    (is (< 0 (compute-avg (range 10))))
    (is (= 5 (compute-avg (range 11))))
    (is (= 0 (compute-avg [])))))

(deftest test-xs-of-avgs
  (testing "That the lowest average is outputted"
    (is (= 57 (xs-of-avgs [[66, 64, 58, 65, 71, 57, 60],
                           [57, 65, 65, 70, 72, 65, 51],
                           [55, 54, 60, 53, 59, 57, 61],
                           [65, 56, 55, 52, 55, 62, 57]])))))

(run-tests)
