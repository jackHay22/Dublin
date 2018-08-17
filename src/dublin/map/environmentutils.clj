(ns dublin.map.environmentutils
  (:require [dublin.config :as config]
            [clojure.java.io :as io])
  (:gen-class))

(import java.awt.Image)
(import java.awt.AlphaComposite)
(import java.awt.image.RescaleOp)
(import java.awt.image.BufferedImage)
(import java.awt.geom.AffineTransform)
(import java.awt.image.AffineTransformOp)

(defn load-image
  "load an image from resources"
  [path]
  (javax.imageio.ImageIO/read (clojure.java.io/resource path)))

(defn draw-image
  "draw an image to the gr object"
  [gr img x y]
  (try
    (.drawImage gr img x y nil)
    (catch Exception e
      (println "Dublin => Failed to render image:" img "\n" (.getMessage e)))))
