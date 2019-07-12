(ns image-to-asci-art.core
  (:require [clojure.java.io :as io])
  (:import
    [java.lang Math]
    [java.awt Image Color Robot]
    [java.awt.image RenderedImage BufferedImageOp]
    [javax.imageio ImageIO ImageWriter ImageWriteParam IIOImage]
    [javax.imageio.stream FileImageOutputStream]))

(def image-data (ImageIO/read (io/resource "me.jpg")))

(def image-width (int (.getWidth image-data)))

(def image-height (int (.getHeight image-data)))

(defn get-pixels [^java.awt.image.BufferedImage img [x y] [w h]]
  (for [x (range x (+ x w))
        y (range y (+ y h))]
    (let [c (Color. (.getRGB img x y))]
      [(.getRed c) (.getGreen c) (.getBlue c)])))

(defn calculate-brightness [color]
  (let [[red green blue] color]
    (Math/sqrt
      (+
       (* red red 0.241)
       (* green green 0.691)
       (* blue blue 0.068)))))

(def pixels (get-pixels image-data [0 0] [image-width image-height]))

(def pixel-brightness (map #(calculate-brightness %) pixels))

(defn -main [& args] (prn pixel-brightness))
