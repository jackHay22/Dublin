(ns dublin.map.entity.manager
  (:require [dublin.config :as config]
            [dublin.map.entity.behavior :as behavior]
            [dublin.map.entity.utils :as utils])
  (:gen-class))

(defn load-entity-resource
  "load a single entity"
  [entity-resource]
  (update-in entity-resource [:movements]
    (fn [movements]
        (doall (map (fn [move-set]
              (update-in move-set [:images]
                (fn [image-path]
                    (utils/load-movement-sheet
                        image-path (:total-frames move-set)))))
         movements)))))

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
                  [:x] + (:dx current-movement))
                  [:y] + (:dy current-movement))))]
  (update-in
    (update-in mapset-state [:entities] (fn [entity-list]
      (map
        make-entity-frame-update
        entity-list))) [:player] make-entity-frame-update)))

(defn draw-entity
  "draw a single entity"
  [gr entity]
  (utils/draw-entity-frame
     gr (nth
         (:images
           (nth (:movements entity)
             (:current-movement-index entity)))
         (:current-frame-index entity)) (:x entity) (:y entity)))

(defn entity-key-update
  "update player based on key input"
  [entity key]
  (let [new-animation-index (reduce #(if (= key (:key-bind (first %2))) (reduced (first %1)) %1) false
                                (map vector (:movements entity) (range)))]
    (if new-animation-index
      (assoc entity
        :current-frame-cycles 0
        :current-frame-index 0
        :current-movement-index new-animation-index) entity)))
