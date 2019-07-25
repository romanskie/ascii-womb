(ns image-to-asci-art.core
  (:require [clojure.java.io :as io])
  (:import
    [java.lang Math]
    [java.awt Image Color Robot]
    [java.awt.image RenderedImage BufferedImageOp]
    [javax.imageio ImageIO ImageWriter ImageWriteParam IIOImage]
    [javax.imageio.stream FileImageOutputStream]))

(def ^:private pixel-mapping " .-+*wGHM#&%")

(def image-source (ImageIO/read (io/resource "me.jpg")))

(def image-width (int (.getWidth image-source)))

(def image-height (int (.getHeight image-source)))

(defn get-colors [^java.awt.image.BufferedImage img [x y] [w h]]
  (for [x (range x (+ x w))
        y (range y (+ y h))]
    (let [c (Color. (.getRGB img x y))]
      [(.getRed c) (.getGreen c) (.getBlue c)])))

(defn calc-brightness [color]
  (let [[red green blue] color]
    (Math/sqrt
      (+
       (* red red 0.241)
       (* green green 0.691)
       (* blue blue 0.068)))))

;;var idx = brightness / 255 * (_pixels.Length - 1);
(defn to-pixel-idx [arg] (/ (int arg) (* 255 (- (count pixel-mapping) 1))))

(def pixel-color-values (get-colors image-source [0 0] [image-width image-height]))

(def pixel-brightness-values (map #(calc-brightness %) pixel-color-values))

(def pixel-idx-values (map #(to-pixel-idx %) pixel-brightness-values))

(defn -main [& args] (print pixel-color-values ))

