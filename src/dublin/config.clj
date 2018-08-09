(ns dublin.config (:gen-class))

(def VERSION "0.1.0")

(def WINDOW-TITLE "Dublin")

(def WINDOW-WIDTH 256)
(def WINDOW-HEIGHT 160)
(def SCALE-FACTOR 4.5)

(def FRAMERATE 60)

(def TILE-DIM 32)

(def TILES-ACROSS (/ WINDOW-WIDTH TILE-DIM))
(def TILES-DOWN (/ WINDOW-HEIGHT TILE-DIM))

; Environment map structural definitions
(defrecord Environment [current mapsets])
(defrecord MapSet [map-layers map-tileset map-objects])
(defrecord Layer [map offset position-x position-y start-draw-x start-draw-y width height])
(defrecord TileSet [images dim])
(defrecord MapObject [images dim action controller frame operating?])

(defrecord EntitySet [walk-left walk-right idle-left idle-right idle-front idle-back special x y]) ;TODO

;(def player (EntitySet. "entities/player" '()))

;RESOURCES

(def tap (MapObject. "objects/tap.png" TILE-DIM :tap :p 0 false))

(def underdog
      (MapSet.  ;TODO: map links
        (list
          (Layer. "maps/underdog_int_layer_0.txt" 0.95 0 0 0 0 0 0)
          (Layer. "maps/underdog_int_layer_1.txt" 0.95 0 0 0 0 0 0)
          (Layer. "maps/underdog_int_layer_2.txt" 1 0 0 0 0 0 0)
          (Layer. "maps/underdog_int_layer_3.txt" 1.1 0 0 0 0 0 0))
        (TileSet. "tiles/underdog_int.png" TILE-DIM)
        (list tap)))

(def dublin (Environment. 0 (vector underdog)))
