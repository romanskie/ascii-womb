(ns asci-womb.util
  (:require [clojure.java.io :as io]
            [clojure.string :as s])
  (:import
   [java.lang Math]
   [java.awt Image Color]
   [java.awt.image BufferedImage]
   [javax.imageio ImageIO]))

(defn- get-img-width [^BufferedImage bufferd-img]
  (double (.getWidth bufferd-img)))

(defn- get-img-height [^BufferedImage bufferd-img]
  (double (.getHeight bufferd-img)))

(defn- calc-pixel-brightness [rgb-values]
  (let [[red green blue] rgb-values]
    (Math/sqrt
     (+
      (* red red 0.241)
      (* green green 0.691)
      (* blue blue 0.068)))))

(defn- get-brightness-mapping [brightness asci-mapping]
  (let [dividend (* 255 (- (count asci-mapping) 1))]
    (/ brightness (/ dividend 100))))

(defn- get-asci-mapping-idx [asci-mapping idx]
  (- (count asci-mapping) (int (Math/round idx)) 1))

(defn- resize-img [^BufferedImage bufferd-img width height]
  (let [img-scale (.getScaledInstance bufferd-img width height (Image/SCALE_SMOOTH))
        resized-img (BufferedImage. width height (BufferedImage/TYPE_INT_ARGB))
        g2d (.createGraphics resized-img)]
    (doto g2d
      (.drawImage img-scale 0 0 nil)
      (.dispose))
    resized-img))

(defn- scale [input-size scale-factor]
  (double (/ (* input-size scale-factor) 100)))

(defn- write-to-file [output-path output]
  (with-open [w (io/writer output-path :append true)]
    (.write w (apply str output))))

(defn scale-img [^BufferedImage bufferd-img scale-factor]
  (let [h (get-img-height bufferd-img)
        w (get-img-width bufferd-img)
        scaled-h (scale h (* scale-factor 0.5))
        scaled-w (scale w scale-factor)
        resized-img (resize-img bufferd-img scaled-w scaled-h)]
    resized-img))

(defn write-img [^BufferedImage bufferd-img asci-mapping output-path]
  (let [w (get-img-width bufferd-img)
        h (get-img-height bufferd-img)
        w-range (range 0 w)
        h-range (range 0 h)
        res (reduce (fn [lines y]
                      (let [line (map #(let [color (Color. (.getRGB bufferd-img % y))
                                             rgb-values [(.getRed color) (.getGreen color) (.getBlue color)]
                                             brightness-value (calc-pixel-brightness rgb-values)
                                             brightness-mapping (get-brightness-mapping brightness-value asci-mapping)
                                             asci-mapping-idx (get-asci-mapping-idx asci-mapping brightness-mapping)
                                             asci-sign (nth asci-mapping asci-mapping-idx)] asci-sign) w-range)]
                        (conj lines (s/join (concat line '("\n"))))))
                    [] h-range)]
    (write-to-file output-path res)))
