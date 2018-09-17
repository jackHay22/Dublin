(ns dublin.config (:gen-class))

(import java.awt.Color)
(import java.awt.Font)

(def VERSION "0.3.2")

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

(def MENU-STATE 0)
(def MAIN-STATE 1)
(def STARTING-STATE MENU-STATE)

(def MENU-GUTTER 30)
(def MENU-OPTION-SPACING 20)

; Environment definition groups map locations
(defrecord Environment [current mapsets minigames])

; Mapset defines a single location in the environment, its tilemap layers,
; the associated image -> tileset, any image -> objects (interactive or animated),
; the player (only defined when map is current), entities
; within that map, links to other maps in the environment, and LightingPreset
(defrecord MapSet [map-layers tile-markers map-tileset map-objects player entities maplinks lighting-objects])

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
(defrecord MapObject [images width action controller frame operating?])

; An entity set defines a list of movement bindings, the index of the current movement, the current frame of the movement,
; the number of times that frame has been displayed, and the entity's coordinates plus layer
(defrecord EntitySet [movements current-movement-index current-frame-index current-frame-cycles x y width height layer-index])

; Movement bindings define an entity movement, the image -> images, the number of frames, the key that toggles
; the movement (or key release), the delay between frames, and the change in x and change in y for each update
; cycle
(defrecord MovementBinding [images total-frames key-bind frame-delay dx dy]) ;idle: on-release binding

; defines a mapset specific lighting preset with color-> radial gradient, radius and layer to draw on, x, y center
(defrecord LightingPreset [color radius layer-index x y])

; defines a tile index - label mapping
(defrecord TileMarker [index marker])

; defines a minigame
(defrecord Minigame [])

; defines a menu option binding operation
(defrecord OptionBinding [text selected gs-update-operation])

; defines the main menu with options list and paralax list
(defrecord Menu [menu-options paralax-layers])

; defines a paralax layer
(defrecord ParalaxLayer [image x dx])

(def main-player
      (EntitySet.
        (list
          (MovementBinding. "entities/jack_idle_r.png" 1 :right-release 10 0 0)
          ;:stairs
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
        ;tile markers
        (list (TileMarker. 3 :stair))
        (TileSet. "tiles/underdog_int.png" TILE-DIM)
        ;objects
        (list tap)
        ;player
        main-player
        ;entities
        (list)
        ;links
        (list
            (MapLink. 128 286 3)
            (MapLink. 300 286 2)
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
      ;tile markers
      (list)
      (TileSet. "tiles/frognerseteren_station.png" TILE-DIM)
      (list)
      main-player
      (list)
      (list
          (MapLink. 128 190 1)
          )
      (list
        ;(LightingPreset. (Color. 0 0 0 80) 600 1 460 0)
        )))

(def trinity-range
  (MapSet.
    (list
      (Layer. "maps/dublin/trinity_range_layer_0.txt" 0.86 0 0 0 0 0 0)
      (Layer. "maps/dublin/trinity_range_layer_1.txt" 0.90 0 0 0 0 0 0)
      (Layer. "maps/dublin/trinity_range_layer_2.txt" 1 0 0 0 0 0 0)
      (Layer. "maps/dublin/trinity_range_layer_3.txt" 1.1 0 0 0 0 0 0))
      ;tile markers
      (list)
      (TileSet. "tiles/trinity_range.png" TILE-DIM)
      (list)
      main-player
      (list)
      (list
          (MapLink. 70 158 1))
      (list
        (LightingPreset. (Color. 0 0 0 100) 400 0 100 0)
        (LightingPreset. (Color. 0 0 0 100) 400 3 200 0))))

(def matosinhos-beach
  (MapSet.
    (list
      (Layer. "maps/portugal/matosinhos_layer_0.txt" 0.4 0 0 0 0 0 0)
      (Layer. "maps/portugal/matosinhos_layer_1.txt" 0.4 0 0 0 0 0 0)
      (Layer. "maps/portugal/matosinhos_layer_2.txt" 0.8 0 0 0 0 0 0)
      (Layer. "maps/portugal/matosinhos_layer_3.txt" 1 0 0 0 0 0 0)
      (Layer. "maps/portugal/matosinhos_layer_4.txt" 1.1 0 0 0 0 0 0))
      ;tile markers
      (list)
      (TileSet. "tiles/matosinhos.png" TILE-DIM)
      (list)
      (update-in main-player [:layer-index] inc)
      (list)
      (list
          (MapLink. 128 154 1))
      (list)))

(def dublin
  (Environment. 1
    (vector frognerseteren-station underdog trinity-range matosinhos-beach)
    (vector )))

(def MENU-COLOR-SELECTED (Color. 255 61 46))
(def MENU-COLOR-DESELECTED (Color. 157 189 198))

(def main-menu
  (Menu.
    (list
      (OptionBinding. "Start" true #(constantly 1))
      (OptionBinding. "About" false #(constantly 2))
      (OptionBinding. "Exit" false #(System/exit 0)))
    (list
      (ParalaxLayer. "menu/dublin_paralax_0.png" 0 0.8)
      (ParalaxLayer. "menu/dublin_paralax_1.png" 0 0.6)
      (ParalaxLayer. "menu/dublin_paralax_2.png" 0 0.4)
      (ParalaxLayer. "menu/dublin_paralax_3.png" 0 0.2)
      (ParalaxLayer. "menu/title.png" 0 0)
      (ParalaxLayer. "menu/dublin_paralax_4.png" 0 -0.2)
      (ParalaxLayer. "menu/dublin_paralax_5.png" 0 -0.6))))
