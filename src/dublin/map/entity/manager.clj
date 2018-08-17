(ns dublin.map.entity.manager
  (:require [dublin.config :as config]
            [dublin.map.entity.behavior :as behavior]
            [dublin.map.entity.utils :as utils])
  (:gen-class))

(defn load-entity-resource
  "load a single entity"
  [entity-resource]
  (let [entity-resource-images
        (update-in entity-resource
          [:movements]
            (fn [movements]
                (doall (map (fn [move-set]
                    (update-in move-set [:images]
                      (fn [image-path]
                          (utils/load-movement-sheet
                              image-path (:total-frames move-set)))))
               movements))))]
        (assoc entity-resource-images
          :width (utils/min-reduce
                    entity-resource-images :movements :images first #(.getWidth %))
          :height (utils/min-reduce
                    entity-resource-images :movements :images first #(.getHeight %)))))

(defn load-entity-resource-sets
  "load entity set"
  [entity-resources-list]
  (doall (map load-entity-resource entity-resources-list)))

(defn update-entity-resource-set
  "update all entities in set"
  [mapset-state]
  (let [make-entity-frame-update
          (fn [entity]
            (let [current-movement (nth
                        (:movements entity)
                        (:current-movement-index entity))
                  cycle-update (mod
                        (inc (:current-frame-cycles entity))
                        (:frame-delay current-movement))]
             (update-in
             (update-in
               (update-in
                 (assoc entity :current-frame-cycles cycle-update)
                 [:current-frame-index] #(if (= 0 cycle-update)
                                           (mod (inc %) (:total-frames current-movement)) %))
                  [:x] + (:dx current-movement)) ;TODO: apply gravity and check intersections
                  [:y] + (:dy current-movement))))]
  (update-in
    (update-in mapset-state [:entities] (fn [entity-list]
      (map
        make-entity-frame-update
        entity-list))) [:player] make-entity-frame-update)))

(defn draw-entity
  "draw a single entity"
  [gr entity map-x map-y]
  (utils/draw-entity-frame
     gr (nth
         (:images
           (nth (:movements entity)
             (:current-movement-index entity)))
         (:current-frame-index entity)) (+ (:x entity) map-x) (+ (:y entity) map-y (- (:height entity)))))

(defn entity-key-update
  "update player based on key input"
  [entity key]
  (let [new-animation-index (reduce #(if (= key (:key-bind (first %2))) (reduced (second %2)) %1)
                                          false (map vector (:movements entity) (range)))]
    (if (and new-animation-index (not (= new-animation-index (:current-movement-index entity))))
      (assoc entity
        :current-frame-cycles 0
        :current-frame-index 0
        :current-movement-index new-animation-index) entity)))
