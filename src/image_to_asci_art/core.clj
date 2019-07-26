(ns image-to-asci-art.core
  (:require [clojure.java.io :as io])
  (:import
   [java.lang Math]
   [java.awt Image Color Robot]
   [java.awt.image RenderedImage BufferedImageOp]
   [javax.imageio ImageIO ImageWriter ImageWriteParam IIOImage]
   [javax.imageio.stream FileImageOutputStream]))

(def ^:private pixel-mapping " .-+*wGHM#&%")

(def image-source
  (ImageIO/read (io/resource "smiley.jpg")))

(def image-width
  (int (.getWidth image-source)))

(def image-height
  (int (.getHeight image-source)))

(defn calc-brightness [color]
  (let [[red green blue] color]
    (Math/sqrt
     (+
      (* red red 0.241)
      (* green green 0.691)
      (* blue blue 0.068)))))

;;var idx = brightness / 255 * (_pixels.Length - 1);

(defn- get-index [pixel-mapping idx]
  (- (count pixel-mapping) (int (Math/round idx)) 1))

(defn- to-pixel-idx [brightness]
  (let [dividend (* 255 (- (count pixel-mapping) 1))]
    (/ brightness (/ dividend 100))))

(defn- get-colors [^java.awt.image.BufferedImage img
                   [x y]
                   [w h]]
  ;;(with-open [output (clojure.java.io/writer "Example.txt")]
  (doseq [y (range y (+ y h))]
    (do
      (let [res
            (map #(let [c (Color. (.getRGB img % y))
                        rgb [(.getRed c) (.getGreen c) (.getBlue c)]
                        brightness (calc-brightness rgb)
                        idx (to-pixel-idx brightness)
                        mapping-index (get-index pixel-mapping idx)
                        asci-sign (nth pixel-mapping mapping-index)]
                    asci-sign) (range x (+ x w)))]
        (spit "Example.txt" (str (apply str res) "\n") :append true)))))

(defn -main [& args] (get-colors image-source [0 0] [image-width image-height]))
