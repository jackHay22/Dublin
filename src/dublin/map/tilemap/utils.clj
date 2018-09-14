(ns dublin.map.tilemap.utils
  (:require [dublin.config :as config]
            [clojure.java.io :as io]
            [dublin.map.environmentutils :as env-utils])
  (:gen-class))

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

(defn load-flat-image-set
  "load a flat image set"
  [path width]
  (let [master-image (env-utils/load-image path)
        master-height (.getHeight master-image)]
    (doall
      (map
        (fn [x]
            (.getSubimage master-image x 0 width master-height))
        (range 0 (.getWidth master-image) width)))))

(defn set-map-layer-position
  "set map position (one layer)"
  [map-layer player-x player-y]
  (let [bounds-correct #(cond
                          (< %1 %2) %2
                          (> %1 0) 0
                          :in-range %1)
        depth-offset (:offset map-layer)
        position-x (bounds-correct
                      (* depth-offset
                        (- (/ config/WINDOW-WIDTH 2) player-x))
                    (- config/WINDOW-WIDTH (:width map-layer)))
        position-y (bounds-correct
                    (- (/ config/WINDOW-HEIGHT 2) player-y)
                    (- config/WINDOW-HEIGHT (:height map-layer)))]
        (assoc map-layer
          :position-x (int position-x)
          :position-y (int position-y)
          :start-draw-x (int (/ (- position-x) config/TILE-DIM))
          :start-draw-y (int (/ (- position-y) config/TILE-DIM)))))

(defn draw-tile [gr img x y]
  (env-utils/draw-image gr img x y))
