(ns dublin.map.entity.manager
  (:require [dublin.config :as config]
            [dublin.map.entity.behavior :as behavior]
            [dublin.map.entity.utils :as utils])
  (:gen-class))

(defn load-entity-resource-sets
  "load entity set"
  [entity-resources-list]
  (doall (map
            #(update-in % [:movements]
              (fn [movements]
                  (map (fn [move-set]
                        (update-in move-set [:images]
                          (fn [image-path]
                              (utils/load-movement-sheet
                                  image-path (:total-frames move-set)))))
                   movements)))
         entity-resources-list)))

(defn update-entity-resource-set
  "update all entities in set"
  [entity-state]
  ;(mod current movement frame delay | current frame cycles)
  )

(defn draw-entity
  "draw a single entity"
  [gr entity]
  (if (= (:current-frame-cycles entity) 0)
      (utils/draw-entity-frame
        gr (nth
            (:images
              (nth (:movements entity)
                (:current-movement-index entity)))
          (:current-frame-index entity)) 0 0 ; x y
  )))
