(ns dublin.map.entity.utils
  (:require [dublin.config :as config]
            [dublin.map.environmentutils :as env-utils])
  (:gen-class))

(defn load-movement-sheet
  "take path and number of frames and load"
  [path frame-total]
  (let [master-image (env-utils/load-image path)
        master-width (.getWidth master-image)
        sub-width (/ master-width frame-total)
        master-height (.getHeight master-image)]
        (doall (map #(.getSubimage master-image % 0 sub-width master-height)
                    (range 0 master-width sub-width)))))

(defn draw-entity-frame
  [gr img x y]
  (env-utils/draw-image gr img (int x) (int y)))

(defn min-reduce
  [l & t] (env-utils/complex-attrib-reduce l min t))

(defn max-reduce
  [l & t] (env-utils/complex-attrib-reduce l max t))

(defn get-bounding-coordinates
  "take entity and return set of bounds coordinates"
  [entity]
  (map vector
    (take 4
      (cycle (list (:x entity) (+ (:x entity) (:width entity)))))
    (mapcat
      (fn [n] (repeat 2 (- (:y entity) n))) (list 0 (:height entity)))))

(defn position-to-tile-index
  "convert a position x,y to tilemap indices"
  [pos]
  (map #(int (/ % config/TILE-DIM)) pos))

(defn entity-map-intersection?
  "if entity intersects with non -1 tile at px,py, return true"
  [mapset-state entity]
  (let [current-map (:map (nth (:map-layers mapset-state) (:layer-index entity)))]
        (reduce #(if (not (= -1 (nth (nth current-map (second %2)) (first %2))))
                      (reduced true) false)
                false (map position-to-tile-index
                          (get-bounding-coordinates entity)))))
