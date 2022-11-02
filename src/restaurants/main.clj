(ns restaurants.main
  (:require [clojure.test :refer [deftest is run-tests testing]]
            [clojure.string :as str])
  (:import [java.time LocalDate]))

(defprotocol Reviewable
  (add-review [this] "method for assoc'ing a review to a business")
  (update-aggregate [this] "updates the aggregate rating for the business based on new reviews"))

(defrecord Restaurant [name price menu aggregate])

(defrecord MovieTheater [name movies-showing])

(extend-protocol Reviewable
  Restaurant
  (add-review [this] (str "I had " (rand-nth (seq (:menu this)))))
  (update-aggregate [this] (+ (:aggregate this) (rand-int 5)))
  MovieTheater
  (add-review [this] (str "The following movies are playing: " (str/join ", " (:movies-showing this)))))
  
(def north-dumpling (->Restaurant "North Dumpling" "$" ["pork and chive dumplings"] 5.0))

(def film-forum (->MovieTheater "Film Forum" ["Apocalypse Now" "Last Year at Marienbad"]))

(deftest update-aggregate-test
  (testing "that the protocol implementation adds a random int between 0 and 4 to the aggregate")
  (is (complement zero?) (:aggregate (update-aggregate north-dumpling))))

(deftest assert-film-forum-reviewable
  (testing "that the Film Forum is a Reviewable"
    (is (satisfies? Reviewable film-forum))))

(run-tests)
