(ns ascii-womb.util
  (:require [clojure.java.io :as io]
            [clojure.string :as cstr]
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

(defn- get-brightness-mapping [brightness ascii-mapping]
  (let [dividend (-> ascii-mapping
                     (count)
                     (- 1)
                     (* 255))]
    (-> dividend
        (/ 100)
        (/ brightness))))

(defn- get-ascii-mapping-idx [ascii-mapping idx]
  (- (count ascii-mapping) (int (Math/round idx)) 1))

(defn- resize-img [^BufferedImage bufferd-img width height]
  (let [img-scale (.getScaledInstance bufferd-img width height (Image/SCALE_SMOOTH))
        resized-img (BufferedImage. width height (BufferedImage/TYPE_INT_ARGB))
        g2d (.createGraphics resized-img)]
    (doto g2d
      (.drawImage img-scale 0 0 nil)
      (.dispose))
    resized-img))

(defn- scale [input-size scaling-factor]
  (double (/ (* input-size scaling-factor) 200)))

(defn- write-output-to-file [filepath output]
  (with-open [w (io/writer filepath :append true)]
    (.write w (apply str output))))

(defn- get-img-src-ending [src]
  (let [end (count src)
        start (- end 4)]
    (cstr/upper-case (subs src start end))))

(defn- is-valid-img-src [src]
  (let [valid-src #{".JPG" ".JPE" ".BMP"  ".GIF" ".PNG"}
        ending (-> src
                   (cstr/upper-case)
                   (get-img-src-ending))]
    (contains? valid-src ending)))

(defn arg-to-img-src [arg]
  (if (is-valid-img-src arg)
    (try
      (let [file (io/as-file arg)]
        (ImageIO/read file))
      (catch java.io.IOException ex (.getMessage ex)))
    (throw (Exception. (str "Provided image filetype not supported.")))))

(defn scale-img [^BufferedImage bufferd-img scaling-factor]
  (let [h (get-img-height bufferd-img)
        w (get-img-width bufferd-img)
        scaled-h (-> scaling-factor
                     (* 0.5)
                     (scale h))
        scaled-w (scale w scaling-factor)
        resized-img (resize-img bufferd-img scaled-w scaled-h)]
    resized-img))

(defn write-img [^BufferedImage bufferd-img ascii-mapping filepath]
  (let [w (get-img-width bufferd-img)
        h (get-img-height bufferd-img)
        w-range (range 0 w)
        h-range (range 0 h)
        output (reduce (fn [lines y]
                         (let [line (map #(let [color (Color. (.getRGB bufferd-img % y))
                                                rgb-values [(.getRed color) (.getGreen color) (.getBlue color)]
                                                brightness-value (calc-pixel-brightness rgb-values)
                                                brightness-mapping (get-brightness-mapping brightness-value ascii-mapping)
                                                ascii-mapping-idx (get-ascii-mapping-idx ascii-mapping brightness-mapping)
                                                ascii-sign (nth ascii-mapping ascii-mapping-idx)] ascii-sign) w-range)]
                           (conj lines (s/join (concat line '("\n"))))))
                       [] h-range)]
    (write-output-to-file filepath output)))
