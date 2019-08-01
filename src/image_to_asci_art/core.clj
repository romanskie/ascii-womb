(ns image-to-asci-art.core
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [image-to-asci-art.util :as util])
  (:import
   [java.lang Math]
   [java.awt Image Color]
   [java.awt.image BufferedImage]
   [javax.imageio ImageIO]))

(def pixel-mapping " .-+*wGHM#&%")

(prn util/sample)

(defn- write-to-file [filepath output]
  (with-open [w (io/writer filepath :append true)]
    (.write w (apply str output))))

(def output-path
  "Example.txt")

(def img-source
  (ImageIO/read (io/resource "me.jpg")))

(defn- get-img-width [^BufferedImage bufferd-img]
  (int (.getWidth bufferd-img)))

(defn- get-img-height [^BufferedImage bufferd-img]
  (int (.getHeight bufferd-img)))

(defn- calc-pixel-brightness [pixel-color]
  (let [[red green blue] pixel-color]
    (Math/sqrt
     (+
      (* red red 0.241)
      (* green green 0.691)
      (* blue blue 0.068)))))

(defn- get-mapping-index [pixel-mapping idx]
  (- (count pixel-mapping) (int (Math/round idx)) 1))

(defn- to-pixel-idx [brightness]
  (let [dividend (* 255 (- (count pixel-mapping) 1))]
    (/ brightness (/ dividend 100))))

(defn- resize-img [^BufferedImage bufferd-img width height]
  (let [img-scale (.getScaledInstance bufferd-img width height (Image/SCALE_SMOOTH))
        resized-img (new BufferedImage width height (BufferedImage/TYPE_INT_ARGB))
        g2d (.createGraphics resized-img)]
    (doto g2d
      (.drawImage img-scale 0 0 nil)
      (.dispose))
    resized-img))

(defn- scale-img [^BufferedImage bufferd-img scale-factor]
  (let [scale (fn [input factor] (int (/ input factor)))
        h (get-img-height bufferd-img)
        w (get-img-width bufferd-img)
        scaled-h (scale h scale-factor)
        scaled-w (scale w (int (* scale-factor 0.5)))
        resized-img (resize-img bufferd-img scaled-w scaled-h)]
    resized-img))

(defn- write-img [^BufferedImage bufferd-img]
  (let [w (get-img-width bufferd-img)
        h (get-img-height bufferd-img)
        w-range (range 0 w)
        h-range (range 0 h)
        res (reduce (fn [lines y]
                      (let [line (map #(let [c (Color. (.getRGB bufferd-img % y))
                                             rgb [(.getRed c) (.getGreen c) (.getBlue c)]
                                             brightness (calc-pixel-brightness rgb)
                                             idx (to-pixel-idx brightness)
                                             mapping-index (get-mapping-index pixel-mapping idx)
                                             asci-sign (nth pixel-mapping mapping-index)] asci-sign) w-range)]
                        (conj lines (s/join (concat line '("\n"))))))
                    [] h-range)]
    (write-to-file output-path res)))

(defn -main [& args] (write-img (scale-img img-source 50)))
