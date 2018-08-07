(ns dublin.map.tilemap.utils
  (:require [dublin.config :as config]
            [clojure.java.io :as io]
            [dublin.map.environmentutils :as env-utils])
  (:gen-class))

(import java.awt.geom.Point2D)
(import java.awt.Color)
(import java.awt.RadialGradientPaint)
(import java.awt.Graphics2D)
(import java.awt.AlphaComposite)
(import java.awt.image.BufferedImage)
(import java.awt.MultipleGradientPaint)

(defn load-map-file
    "resource path, list of keywords for storing the game map as a list of maps (i.e. '(:image :sound)
    or '(:image :sound :height :blocked?))
    returns: map of fields and :draw.  result is wrapped with :map, :tiles-across, :tiles-down"
    [path]
    (with-open [reader (clojure.java.io/reader (io/resource path))]
        (map (fn [line]
                    (map #(Integer. %) (clojure.string/split line #",")))
        (clojure.string/split (clojure.string/join "\n" (line-seq reader)) #"\n"))))

(defn load-master-image-set
    "split master image into list of images"
    [path block-dim]
    (let [master-image (env-utils/load-image path)
          master-width (.getWidth master-image)
          master-height (.getHeight master-image)]
          (flatten (doall
            (map (fn [y]
              (doall
                (map (fn [x]
                  (.getSubimage master-image x y block-dim block-dim))
                (range 0 master-width block-dim))))
            (range 0 master-height block-dim))))))

(defn set-map-layer-position
  "set map position (one layer)"
  [map-layer player-x player-y]
  (let [bounds-correct #(cond
                          (< %1 %2) %2
                          (> %1 0) 0
                          :in-range %1)
        depth-offset (:offset map-layer)
        position-x (bounds-correct
                    (- (/ config/WINDOW-WIDTH 2) player-x)
                    (- config/WINDOW-WIDTH (:width map-layer)))
        position-y (bounds-correct
                    (- (/ config/WINDOW-HEIGHT 2) player-y)
                    (- config/WINDOW-HEIGHT (:height map-layer)))]
        ;TODO: factor in depth-offset
        (assoc map-layer
          :position-x position-x
          :position-y position-y
          :start-draw-x
          (int (/ (- position-x) config/TILE-DIM))
          :start-draw-y
          (int (/ (- position-y) config/TILE-DIM)))))

(defn draw-tile [gr img x y]
  (env-utils/draw-image gr img x y))

; (def layer-1-lighting-opacity 200)
; (def layer-2-lighting-opacity 100)
;
; (def layer-1-lighting-radius (float 1000.0))
; (def layer-2-lighting-radius (float 800.0))
;
; (defn layer-1-rgb
;   [x y] {:color (Color. 0 0 0 layer-1-lighting-opacity) :radius layer-1-lighting-radius})
;
; (defn layer-2-rgb
;   [x y] {:color (Color. 164 154 135 layer-2-lighting-opacity) :radius layer-1-lighting-radius})
;
; (def lighting-preset
;   {:l0 #(layer-1-rgb %1 %2)
;    :l1 #(layer-2-rgb %1 %2)})
;
; (defn get-lighting-profile
;   "returns color given a tile and a layer"
;   [layer x y] ;x and y are map relative player positions
;   ((layer preset/lighting-preset) x y))

; (defn render-lighting
;   "render lighting at point"
;   [gr x y layer]
;   (let [win-width @config/WINDOW-RESOURCE-WIDTH
;         win-height @config/WINDOW-RESOURCE-HEIGHT
;         lighting-layer (BufferedImage. win-width win-height BufferedImage/TYPE_INT_ARGB)
;         g2d (cast Graphics2D (.createGraphics lighting-layer))
;         dist (float-array [0.1 1.0])
;         lighting-profile (get-lighting-profile layer x y)
;         radial-color (into-array Color [(Color. 0.0 0.0 0.0 0.0) (:color lighting-profile)])
;         gradient (RadialGradientPaint. (float x) (float y) (:radius lighting-profile) dist radial-color)]
;         (do
;           (.setPaint g2d gradient)
;           (.setComposite g2d (AlphaComposite/getInstance AlphaComposite/SRC_OVER 0.95))
;           (.fillRect g2d 0 0 win-width win-height)
;           (.drawImage gr lighting-layer 0 0 win-width win-height nil)
;           (.dispose g2d))))
