(ns image-to-asci-art.util
  (:require [clojure.java.io :as io]
            [clojure.string :as s])
  (:import
   [java.lang Math]
   [java.awt Image Color]
   [java.awt.image BufferedImage]
   [javax.imageio ImageIO]))


(def sample "hello")

