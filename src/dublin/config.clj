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

(def LINK-PROXIMITY TILE-DIM)

; Environment definition groups map locations and defines player
(defrecord Environment [current mapsets player])

; Mapset defines a single location in the environment, its tilemap layers,
; the associated image -> tileset, any image -> objects (interactive or animated), entities
; within that map, and links to other maps in the environment
(defrecord MapSet [map-layers map-tileset map-objects entities maplinks])

; A maplink defines a "door" between mapsets in the environment and is defined
; by a proximity tile index and the index of the connected mapset
(defrecord MapLink [px py set-index])

; A layer defines a tilemap layer within a mapset. This includes the mapfile -> 2D map array,
; the paralax offset from 1, the current position x/y and render location x/y, the width and height
; (tile-dimension x number of tiles)
(defrecord Layer [map offset position-x position-y start-draw-x start-draw-y width height])

; A tileset defines the tiles associated with a mapset and includes an image -> images and a tile dimension
(defrecord TileSet [images dim])

; A map object is an animated or interactive object in a mapset and is defined by an image -> images,
; an image dimension, the keyword (symbol for function name), the keybinding for toggling action, and
; the state of the object
(defrecord MapObject [images dim action controller frame operating?])

; An entity set defines a list of movement bindings, the index of the current movement, and the entity's
; coordinates
(defrecord EntitySet [movements current-movement-index x y])

; Movement bindings define an entity movement, the image -> images, the number of frames, the key that toggles
; the movement (or key release), the delay between frames, and the change in x and change in y for each update
; cycle
(defrecord MovementBinding [images total-frames key-bind frame-delay dx dy]) ;idle: on-release binding

(def main-player
      (EntitySet.
        (list
          (MovementBinding. "entities/jack_walk_r.png" 15 :right )) 0 128 220))

(def tap (MapObject. "objects/tap.png" TILE-DIM :tap :p 0 false))

(def underdog
      (MapSet.
        (list
          (Layer. "maps/underdog_int_layer_0.txt" 0.90 0 0 0 0 0 0)
          (Layer. "maps/underdog_int_layer_1.txt" 0.95 0 0 0 0 0 0)
          (Layer. "maps/underdog_int_layer_2.txt" 1 0 0 0 0 0 0)
          (Layer. "maps/underdog_int_layer_3.txt" 1.1 0 0 0 0 0 0))
        (TileSet. "tiles/underdog_int.png" TILE-DIM)
        (list tap)
        (list )
        (list )))

(def dublin (Environment. 0 (vector underdog) main-player))
