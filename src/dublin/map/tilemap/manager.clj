(ns dublin.map.tilemap.manager
  (:require [dublin.config :as config]
            [dublin.map.tilemap.utils :as utils]
            [dublin.map.tilemap.object :as objects])
  (:gen-class))

(defn load-maps
  "take list of map layers, return loaded layers and offsets
  :map, :offset"
  [map-layers]
  (doall (map (fn [layer]
          (let [map-loaded (doall (update-in layer [:map]
                                    (fn [map-path] (utils/load-map-file map-path))))]
                (assoc map-loaded :width
                          (* config/TILE-DIM (count (first (:map map-loaded))))
                                   :height
                          (* config/TILE-DIM (count (:map map-loaded))))))
            map-layers)))

(defn load-tileset
  "take tileset :path :dim, return images and dim"
  [tileset]
  (update-in tileset [:images]
    #(utils/load-master-image-set % (:dim tileset))))

(defn load-map-objects
  "take map objects :image and :action and load"
  [objects]
  (doall (map
      (fn [obj]
        (reduce #(update-in %1 [(first %2)] (second %2))
            obj (map vector (list :images :action)
                            (list #(utils/load-master-image-set % (:dim obj))
                                  objects/resolve-function-keyword))))
         objects)))

(defn update-map-resource-set
  "update all layers in set"
  [mapset-state]
  (let [player (:player mapset-state)]
    (update-in
      (update-in mapset-state
        [:map-layers]
            (fn [layers]
                (map #(utils/set-map-layer-position % (:x player) (:y player))
                layers)))
        [:map-objects]
            (fn [objects]
                (map (fn [o] (objects/action-update o)) objects)))))

(defn draw-map-layer
  "draw single map layer from set"
  [gr mapset-layer all-images]
  (let [map-array (:map mapset-layer)
        position-x (:position-x mapset-layer)
        position-y (:position-y mapset-layer)
        start-draw-x (:start-draw-x mapset-layer)
        start-draw-y (:start-draw-y mapset-layer)
        map-tiles-across (/ (:width mapset-layer) config/TILE-DIM)
        map-tiles-down (/ (:height mapset-layer) config/TILE-DIM)]
    (doseq
      [x (range start-draw-x (+ start-draw-x (inc config/TILES-ACROSS)))
       y (range start-draw-y (+ start-draw-y (inc config/TILES-DOWN)))]

       (if (and (>= x 0) (>= y 0) (> map-tiles-across x) (> map-tiles-down y))
            (let [image-index (nth (nth map-array y) x)]
                  (if (not (= image-index -1))
                      (utils/draw-tile gr (nth all-images image-index)
                          (+ position-x (* x config/TILE-DIM))
                          (+ position-y (* y config/TILE-DIM)))))))))

(defn invoke-object-at
  "invoke a map object"
  [x y key mapset-state]
  (let [action-loc (reduce
                    #(if (= key (:controller %2))
                        (reduced %1) (inc %1)) 0 (:map-objects mapset-state))]
        (update-in mapset-state [:map-objects action-loc] #(objects/action-invoke %))))
