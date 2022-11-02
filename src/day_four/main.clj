(ns day-four.main
  (:require [clojure.java.io :as io])
  (:import [java.io File]
           [java.awt Color]
           [javax.imageio ImageIO])
  (:gen-class))

(defn invert-bits [i]
  (bit-and-not 0xff i))

(defn grayscale [cs]
  (let [r (.getRed cs)
        g (.getGreen cs)
        b (.getBlue cs)]
    (/ (reduce + [r g b]) 3)))

(defmulti transform "defines the transformation type" class)
(defmethod transform Integer [b] (invert-bits b))
(defmethod transform Color [b] (grayscale b))

(defn bm [f-in f-out tf]
  (let [in (ImageIO/read (File. f-in))]
    (doseq [w (range (.getWidth in))
            h (range (.getHeight in))]
      (as-> (.getRGB in w h) color
        (->> (if tf (Color. color) color)
             transform
             (.setRGB in w h))))
    (ImageIO/write in "bmp" (File. f-out))))

(defn -main [& args]
  (bm (first args) (second args) (first (nthrest args 3))))
