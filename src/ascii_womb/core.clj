(ns ascii-womb.core
  (:gen-class)
  (:require
   [ascii-womb.util :as util])
  (:import
   [java.lang Math]
   [java.awt Image Color]
   [java.awt.image BufferedImage]
   [javax.imageio ImageIO]))

(defonce ascii-mapping
  " .-+*wGHM#&%")

(def img-scaling-factor 40)

(def default-output-path "output/asci-art.txt")

(defn write-output [arg]
  (let [img-src (util/arg-to-img-src arg)
        scaled-src (-> img-src
                       (util/scale-img img-scaling-factor))]
    (util/write-img scaled-src ascii-mapping default-output-path)))

(defn -main [& args] ; & creates a list of var-args
  (if (empty? args)
    (throw (Exception. "No args provided. Please prove a path to your image!"))
    (let [img-src (first args)]
      write-output img-src)))
