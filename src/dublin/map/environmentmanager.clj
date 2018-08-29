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
  "load the current mapset within the environment preset"
  [environment-preset]
  (utils/init-current-mapset environment-preset loaders))

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
        tileset-images (:images (:map-tileset mapset-to-draw)) ;TODO: add objects
        ;(map #(nth (:images %) (:frame %)) (:map-objects mapset-to-draw))
        lighting-preset-list (:lighting-objects mapset-to-draw)
        player (:player mapset-to-draw)
        renderable-objects (concat (take (count (:map-layers mapset-to-draw))
                                      (iterate (fn [p] (update-in p [0] inc))
                                                (vector 0 #(tilemap-manager/draw-map-layer gr
                                                            % tileset-images))))
                                   (map (fn [entity]
                                             (list (:layer-index entity) #(entity-manager/draw-entity gr % entity)))
                                                  (cons player (:entites mapset-to-draw)))
                                   (map (fn [light-preset]
                                            (list (:layer-index light-preset)
                                                  #(lighting-manager/render-lighting-from-preset gr % light-preset)))
                                        lighting-preset-list))]
    (doall (map
              (fn [map-layer layer-index]
                (doall
                  (map #(if (= layer-index (first %))
                            ((second %) map-layer)) renderable-objects)))
              (:map-layers mapset-to-draw) (range)))))

(defn environment-keypressed
  [key state]
  (utils/check-links
    (update-in state [:mapsets (:current state) :player]
        #(entity-manager/entity-key-update % key)) key loaders))

(defn environment-keyreleased
  [key state]
  (update-in state [:mapsets (:current state) :player]
      #(entity-manager/entity-key-update %
          (utils/get-release-keyword key))))
