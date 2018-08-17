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

(defn max-reduce
    "reduce a complex list to max val with transforms"
    [complex-list & transforms]
    (reduce max
      (reduce
        (fn [current-list t]
          (if (map? current-list)
              (t current-list)
              (map t current-list)))
      complex-list transforms)))


(defn get-bounding-coordinates
  "take entity and return set of bounds coordinates"
  [entity]
  (map vector
    (take 4
      (cycle (list (:x entity) (+ (:x entity) (:width entity)))))
    (mapcat
      (fn [n] (repeat 2 (+ n (:y entity)))) (list 0 (:height entity)))))
