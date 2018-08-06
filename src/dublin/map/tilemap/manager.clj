(ns dublin.map.tilemap.manager
  (:require [dublin.config :as config]
            [dublin.map.tilemap.utils :as utils]
            [dublin.map.tilemap.object :as objects])
  (:gen-class))

(defn load-maps
  "take list of map layers, return loaded layers and offsets
  :map, :offset"
  [map-layers]
  (doall
    (map (fn [layer]
          (update-in
            (update-in
              (update-in layer [:map]
                                  (fn [map-path] (utils/load-map-file map-path)))
                               [:width]
                                  (fn [map-loaded]
                                        (* config/TILE-DIM (count (first (:map map-loaded))))))
                               [:height]
                                  (fn [width-set]
                                        (* config/TILE-DIM (count (:map width-set))))))
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
        (update-in
          (update-in obj
                [:images]
                  (fn [images] (utils/load-master-image-set images (:dim obj))))
                [:action]
                  (fn [action] (objects/resolve-function-keyword action))))
         objects)))

(defn update-map-resource-set
  "update all layers in set"
  [mapset-state player-x player-y]
  (update-in
    (update-in mapset-state
        [:map-layers]
            (fn [layers]
                (map #(utils/set-map-layer-position % player-x player-y)
                layers)))
        [:map-objects]
            (fn [objects]
                (map (fn [o] (objects/action-update o)) objects))))

(defn draw-map-layer
  "draw single map layer from set"
  [gr mapset-layer]
  (let [mapset (:map mapset-layer)
        position-x (:position-x mapset-layer)
        position-y (:position-y mapset-layer)]
    (doseq
      [x (range (:start-draw-x mapset-layer) (+ config/TILES-ACROSS 1))
       y (range (:start-draw-y mapset-layer) (+ config/TILES-DOWN 1))]
       (utils/draw-tile gr (nth (nth mapset y) x)
          (+ position-x (* x config/TILE-DIM))
          (+ position-y (* y config/TILE-DIM))))))

(defn invoke-object-at
  "invoke a map object"
  [x y key mapset-state]
  (let [action-loc (reduce
                    #(if (= key (:controller %2))
                        (reduced %1) (inc %1)) 0 (:map-objects mapset-state))]
        (update-in mapset-state [:map-objects action-loc] #(objects/action-invoke %))))
