(ns image-to-asci-art.core
  (:require [clojure.java.io :as io]
            [clojure.string :as s])
  (:import
   [java.lang Math]
   [java.awt Image Color]
   [java.awt.image BufferedImage]
   [javax.imageio ImageIO]))

(def pixel-mapping " .-+*wGHM#&%")

(def output-path
  "Example.txt")

(def image-source
  (ImageIO/read (io/resource "me.jpg")))

(def image-width
  (int (.getWidth image-source)))

(def image-height
  (int (.getHeight image-source)))

(defn- calc-brightness [color]
  (let [[red green blue] color]
    (Math/sqrt
     (+
      (* red red 0.241)
      (* green green 0.691)
      (* blue blue 0.068)))))

(defn- get-index [pixel-mapping idx]
  (- (count pixel-mapping) (int (Math/round idx)) 1))

(defn- to-pixel-idx [brightness]
  (let [dividend (* 255 (- (count pixel-mapping) 1))]
    (/ brightness (/ dividend 100))))

;;private static BufferedImage resize(BufferedImage img, int height, int width) {
;;  Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
;;  BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
;;  Graphics2D g2d = resized.createGraphics();
;;  g2d.drawImage(tmp, 0, 0, null);
;;  g2d.dispose();
;;  return resized;
;; }

(defn resize-image [img width height]
  (let [tmp (.getScaledInstance img width height (Image/SCALE_SMOOTH))
        resized (new BufferedImage width height (BufferedImage/TYPE_INT_ARGB))
        g2d (.createGraphics resized)]
    (doto g2d
        (.drawImage tmp 0 0 nil)
        (.dispose))
    resized))

(defn- write-to-file [filepath output]
  (with-open [w (io/writer filepath :append true)]
    (.write w (apply str output))))

(defn- write-image [^BufferedImage org-img
                    [w h]]
  (let [
        w (int (/ image-width 25))
        h (int (/ image-height 45))
        img (resize-image org-img w h)
        width-range (range 0 w)
        height-range (range 0 h)
        res (reduce (fn [lines y]
                      (let [line (map #(let [c (Color. (.getRGB img % y))
                                             rgb [(.getRed c) (.getGreen c) (.getBlue c)]
                                             brightness (calc-brightness rgb)
                                             idx (to-pixel-idx brightness)
                                             mapping-index (get-index pixel-mapping idx)
                                             asci-sign (nth pixel-mapping mapping-index)] asci-sign) width-range)]
                        (conj lines (s/join (concat line '("\n"))))))
                    [] height-range)]
    (write-to-file output-path res)))

(defn -main [& args] (write-image image-source [image-width image-height]))
