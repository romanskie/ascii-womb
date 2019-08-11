(ns asci-womb.core
  (:gen-class)
  (:require [clojure.java.io :as io]
            [asci-womb.util :as util])
  (:import
   [java.lang Math]
   [java.awt Image Color]
   [java.awt.image BufferedImage]
   [javax.imageio ImageIO]))

(defonce asci-mapping
  " .-+*wGHM#&%")

(defonce image-scale-factor
  20)

(defonce output-path
  "output.txt")

(defonce img-source
  (ImageIO/read (io/resource "smiley.jpg")))

(defn -main [& args]
  (util/write-img
   (util/scale-img img-source image-scale-factor)
   asci-mapping output-path))
