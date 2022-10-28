(ns day-four
  (:import [java.io File]
           [java.awt Color]
           [javax.imageio ImageIO]))

(defn invert-bits [i]
  (bit-and-not 0xff i))

(defn grayscale [cs]
  (let [r (.getRed cs)
        g (.getGreen cs)
        b (.getBlue cs)]
    (/ (reduce + [r g b]) 3)))

(defmulti transform class)
(defmethod transform Integer [b] (invert-bits b))
(defmethod transform Color [b] (grayscale b))

(defn bm [f-in f-out tf]
  (let [in (ImageIO/read (File. f-in))]
    (doseq [w (range (.getWidth in))
            h (range (.getHeight in))]
      (case tf
        "invert" (.setRGB in w h (transform (.getRGB in w h)))
        "grayscale" (.setRGB in w h (transform (Color. (.getRGB in w h))))))
    (ImageIO/write in "bmp" (File. f-out))))
