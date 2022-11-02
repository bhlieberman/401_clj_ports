(ns day-three
  (:import [java.io FileNotFoundException])
  (:require [clojure.java.io :as io]
            [clojure.test :refer [deftest is testing run-tests]]
            [clojure.set :refer [difference]]))

(def forecast [[66, 64, 58, 65, 71, 57, 60],
               [57, 65, 65, 70, 72, 65, 51],
               [55, 54, 60, 53, 59, 57, 61],
               [65, 56, 55, 52, 55, 62, 57]])

(defn weather [forecast]
  (let [fc (set (mapcat sort forecast))
        min (apply min fc) max (apply max fc)
        all (set (range min (inc max)))]
    (difference all fc)))

(def words ["bush" "bush" "bush" "bush" "bush" "shrub" "shrub" "shrub"])

(defn tally [words]
  (->> words
       (partition-by identity)
       (map (juxt first count))
       (sort-by second >)))

(defn linter [f]
  (with-open [fs (io/reader f)]
    (->> fs
         line-seq
         (map-indexed (fn [i l]
                        (cond
                          (or (.endsWith l "{") (.contains l "}")) nil
                          (not (.endsWith l ";")) (str "Missing semicolon on line" (inc i)))))
         (into []))
    (catch FileNotFoundException)))

(deftest tally-test
  (testing "That the function returns the sorted seq of voted words"
    (is (= (ffirst (tally words)) "bush"))))

(deftest weather-test
  (testing "The weather function outputs temperature values
            not seen in the period of the forecast"
    (is (= (weather forecast) #{69 68 67 63}))))

(deftest linter-test
  (testing "That the semicolon usage of the files is correct")
  (is (nil? (first (linter "./test.txt"))))
  (is (nil? (last (linter "./test2.txt"))))
  (is (instance? FileNotFoundException (linter "./test3.txt"))))

(run-tests)