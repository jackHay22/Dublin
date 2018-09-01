(ns dublin.config (:gen-class))

(import java.awt.Color)

(def VERSION "0.3.1")

(def WINDOW-TITLE "Dublin")

(def WINDOW-WIDTH 256)
(def WINDOW-HEIGHT 160)
(def SCALE-FACTOR 4.5)

(def FRAMERATE 60)

(def TILE-DIM 32)

(def TILES-ACROSS (/ WINDOW-WIDTH TILE-DIM))
(def TILES-DOWN (/ WINDOW-HEIGHT TILE-DIM))

(def LINK-PROXIMITY TILE-DIM)

(def GRAVITY-CONSTANT 0.5)

(def MAP-LINK-CONTROLLER :open)

; Environment definition groups map locations
(defrecord Environment [current mapsets minigames])

; Mapset defines a single location in the environment, its tilemap layers,
; the associated image -> tileset, any image -> objects (interactive or animated),
; the player (only defined when map is current), entities
; within that map, links to other maps in the environment, and LightingPreset
(defrecord MapSet [map-layers map-tileset map-objects player entities maplinks lighting-objects])

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

; An entity set defines a list of movement bindings, the index of the current movement, the current frame of the movement,
; the number of times that frame has been displayed, and the entity's coordinates plus layer
(defrecord EntitySet [movements current-movement-index current-frame-index current-frame-cycles x y width height layer-index])

; Movement bindings define an entity movement, the image -> images, the number of frames, the key that toggles
; the movement (or key release), the delay between frames, and the change in x and change in y for each update
; cycle
(defrecord MovementBinding [images total-frames key-bind frame-delay dx dy]) ;idle: on-release binding

; defines a mapset specific lighting preset with color-> radial gradient, radius and layer to draw on, x, y center
(defrecord LightingPreset [color radius layer-index x y])

; defines a minigame
(defrecord Minigame [])

(def main-player
      (EntitySet.
        (list
          (MovementBinding. "entities/jack_idle_r.png" 1 :right-release 10 0 0)
          (MovementBinding. "entities/jack_walk_r.png" 15 :right 7 0.6 0)
          (MovementBinding. "entities/jack_walk_l.png" 15 :left 7 -0.6 0)) 0 0 0 130 280 0 0 2)) ;underdog: 128 288 ;station 128 190

(def tap (MapObject. "objects/tap.png" TILE-DIM :tap :p 0 false))

(def underdog
      (MapSet.
        (list
          (Layer. "maps/dublin/underdog_int_layer_0.txt" 0.85 0 0 0 0 0 0)
          (Layer. "maps/dublin/underdog_int_layer_1.txt" 0.95 0 0 0 0 0 0)
          (Layer. "maps/dublin/underdog_int_layer_2.txt" 1 0 0 0 0 0 0)
          (Layer. "maps/dublin/underdog_int_layer_3.txt" 1.1 0 0 0 0 0 0))
        (TileSet. "tiles/underdog_int.png" TILE-DIM)
        ;objects
        (list tap)
        ;player
        main-player
        ;entities
        (list)
        ;links
        (list
            (MapLink. 128 288 0)
            )
        ;lighting
        (list
          ; (LightingPreset. (Color. 0 0 0 80) 200 1 160 160)
          ; (LightingPreset. (Color. 0 0 0 40) 300 1 280 160)
          ; (LightingPreset. (Color. 0 0 0 40) 300 1 340 160)
          ; (LightingPreset. (Color. 0 0 0 40) 300 1 400 160)
          )))

(def frognerseteren-station
  (MapSet.
    (list
      (Layer. "maps/oslo/frognerseteren_station_layer_0.txt" 0 0 0 0 0 0 0)
      (Layer. "maps/oslo/frognerseteren_station_layer_1.txt" 0.95 0 0 0 0 0 0)
      (Layer. "maps/oslo/frognerseteren_station_layer_2.txt" 1 0 0 0 0 0 0)
      (Layer. "maps/oslo/frognerseteren_station_layer_3.txt" 1.1 0 0 0 0 0 0)
      (Layer. "maps/oslo/frognerseteren_station_layer_4.txt" 1.12 0 0 0 0 0 0))
      (TileSet. "tiles/frognerseteren_station.png" TILE-DIM)
      (list)
      main-player
      (list)
      (list
          (MapLink. 128 190 1)
          )
      (list)))

(def dublin
  (Environment. 1
    (vector frognerseteren-station underdog)
    (vector )))
