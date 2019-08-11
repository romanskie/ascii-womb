(ns ascii-womb.core
  (:gen-class)
  (:require [clojure.java.io :as io]
            [ascii-womb.util :as util])
  (:import
   [java.lang Math]
   [java.awt Image Color]
   [java.awt.image BufferedImage]
   [javax.imageio ImageIO]))

(defonce ascii-mapping
  " .-+*wGHM#&%")

(defonce image-scale-factor 40)

(defonce output-path
  "output.txt")

(defonce img-source
  (ImageIO/read (io/resource "couchant-wombat.jpg")))

(defn -main [& args]
  (util/write-img
   (util/scale-img img-source image-scale-factor)
   ascii-mapping output-path))
