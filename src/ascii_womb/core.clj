(ns ascii-womb.core
  (:gen-class)
  (:require [ascii-womb.util :as util]))

(def ascii-mapping " .-+*wGHM#&%")

(def img-scaling-factor 40)

(def default-output-path "output/asci-art.txt")

(defn write-output [arg]
  (let [img-src (util/input->img-src arg)
        scaled-src (util/scale-img img-src img-scaling-factor)]
    (util/write-img scaled-src ascii-mapping default-output-path)))

(defn -main [& args] ; & creates a list of var-args
  (if (empty? args)
    (throw (Exception. "No args provided. Please prove a path to your image!"))
    (let [img-src (first args)]
      (write-output img-src))))

