(ns dublin.map.environmentmanager
  (:require [dublin.config :as config]
            [dublin.map.environmentutils :as utils]
            [dublin.map.entity.manager :as entity-manager]
            [dublin.map.tilemap.manager :as tilemap-manager]
            [dublin.map.lighting.manager :as lighting-manager])
  (:gen-class))

(defn environment-init
  "take environment preset and perform loads
  map-preset is list of map sets"
  [map-preset]
  (update-in map-preset [:mapsets]
      (fn [mapsets]
        (into [] (doall (map
         #(update-in
            (update-in
              (update-in
                (update-in
                  (update-in % [:map-layers]
                                  (fn [map-layers] (tilemap-manager/load-maps map-layers)))
                               [:map-tileset]
                                  (fn [map-tileset] (tilemap-manager/load-tileset map-tileset)))
                               [:map-objects]
                                  (fn [map-objects] (tilemap-manager/load-map-objects map-objects)))
                               [:entities]
                                  (fn [entities] (entity-manager/load-entity-resource-sets entities)))
                               [:player] entity-manager/load-entity-resource)
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
        ]
    ;(lighting-manager/render-lighting-from-preset gr x y preset)
    (doall (map
              #(do
                ;lighting?
                  (tilemap-manager/draw-map-layer gr %1 tileset object-images)
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
          (keyword (subs (str key "-release") 1)))))
