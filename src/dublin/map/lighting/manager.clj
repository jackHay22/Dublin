(ns dublin.map.lighting.manager
  (:require [dublin.config :as config])
  (:gen-class))

(import java.awt.geom.Point2D)
(import java.awt.RadialGradientPaint)
(import java.awt.Graphics2D)
(import java.awt.AlphaComposite)
(import java.awt.image.BufferedImage)
(import java.awt.MultipleGradientPaint)
(import java.awt.Color)

(defn render-lighting-from-preset
  "render lighting at player xy from config/LightingPreset"
  [gr px py preset]
  (let [win-width config/WINDOW-WIDTH
        win-height config/WINDOW-HEIGHT
        lighting-layer (BufferedImage. win-width win-height BufferedImage/TYPE_INT_ARGB)
        g2d (cast Graphics2D (.createGraphics lighting-layer))]
        (do
          (.setPaint g2d (RadialGradientPaint.
                          (float px) (float py)
                          (float (:radius preset)) (float-array [0.1 1.0])
                          (into-array Color [(Color. 0.0 0.0 0.0 0.0) (:color preset)])))
          (.setComposite g2d (AlphaComposite/getInstance AlphaComposite/SRC_OVER 0.95))
          (.fillRect g2d 0 0 win-width win-height)
          (.drawImage gr lighting-layer 0 0 win-width win-height nil)
          (.dispose g2d))))
