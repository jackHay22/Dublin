(ns dublin.map.environmentmanager
  (:require [dublin.config :as config]
            [dublin.map.environmentutils :as utils]
            [dublin.map.entity.manager :as entity-manager]
            [dublin.map.tilemap.manager :as tilemap-manager]
            [dublin.map.lighting.manager :as lighting-manager])
  (:gen-class))

(def loaders
  {:map-layers tilemap-manager/load-maps
   :map-tileset tilemap-manager/load-tileset
   :map-objects tilemap-manager/load-map-objects
   :entities entity-manager/load-entity-resource-sets
   :player entity-manager/load-entity-resource})

(defn environment-init
  "take environment preset and perform loads
  map-preset is list of map sets"
  [map-preset]
  (update-in map-preset [:mapsets]
    (fn [mapsets]
       (into [] (doall (map
         (fn [mapset]
            (reduce #(update-in %1 [%2] (%2 loaders))
                mapset (list :map-layers :map-tileset :map-objects :entities :player)))
        mapsets))))))

(defn environment-update
  "take state and perform updates"
  [state]
  ;TODO: entity code and objects
  ;if changing mapset, move player to new mapset and dissoc from old
  (update-in state [:mapsets (:current state)]
    #(tilemap-manager/update-map-resource-set
        (entity-manager/update-entity-resource-set %))))

(defn environment-draw
  "draw environment state"
  [gr state]
  (let [mapset-to-draw (nth (:mapsets state) (:current state))
        tileset (:map-tileset mapset-to-draw)
        object-images '() ;(map #(nth (:images %) (:frame %)) (:map-objects mapset-to-draw))
        all-entities (cons (:player mapset-to-draw) (:entites mapset-to-draw))
        lighting-layer (:lighting mapset-to-draw)
        ]
    ;(lighting-manager/render-lighting-from-preset gr x y preset)
    (doall (map
              #(do
                ;lighting?
                  (tilemap-manager/draw-map-layer gr %1 tileset object-images)
                  (if (= (:layer lighting-layer) %2)
                      (lighting-manager/render-lighting-from-preset gr 125 80 lighting-layer)) ;TODO
                  (doall
                    (map (fn [e]
                            (if (= (:layer-index e) %2)
                              (entity-manager/draw-entity gr e (:position-x %1) (:position-y %1))))
                         all-entities)))
          (:map-layers mapset-to-draw) (range))
  )))

(defn environment-keypressed
  [key state]
  (update-in state [:mapsets (:current state) :player]
      #(entity-manager/entity-key-update % key)))

(defn environment-keyreleased
  [key state]
  (update-in state [:mapsets (:current state) :player]
      #(entity-manager/entity-key-update %
          (utils/get-release-keyword key))))
