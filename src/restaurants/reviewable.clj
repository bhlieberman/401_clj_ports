(ns restaurants.reviewable
  (:require [clojure.test :refer [deftest testing is]]))

(defrecord MovieTheater [name movies-showing aggregate])

(defrecord Review [subject body author stars])

(defrecord Restaurant [name price menu aggregate])

(defprotocol Reviewable
  (add-review [this] "method for assoc'ing a review to a business"))

(defn update-aggregate [business stars] (update business :aggregate + stars))

(defn write-review [this rv]
  (reify Reviewable
    (add-review [_] (-> this
                        (assoc :reviews [rv])
                        (update-aggregate (:stars rv))))))

(extend-protocol Reviewable
  Restaurant
  (add-review [this] this)
  MovieTheater
  (add-review [this] this))

(def film-forum (->MovieTheater "Film Forum" ["Apocalypse Now" "Last Year at Marienbad"] 0.0))
(def north-dumpling (->Restaurant "North Dumpling" "$" ["pork and chive dumplings"] 0.0))
(def ff-review (->Review "Film Forum" "Saw Last Year at Marienbad" "Ben Lieberman" 3))
(def nd-review (->Review "North Dumpling" "Best dumplings in Chinatown" "Ben Lieberman" 5))

(deftest test-extends
  (testing "the extension of Reviewable by Restaurant and MovieTheater"
    (is (extends? Reviewable Restaurant))
    (is (extends? Reviewable MovieTheater))))

(deftest test-write-review
  (testing "that the logic of writing a review works for all data types"
    (is (= 5.0 (:aggregate (add-review (write-review north-dumpling nd-review)))))
    (is (= 3.0 (:aggregate (add-review (write-review film-forum ff-review)))))))
