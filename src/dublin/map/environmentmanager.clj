(ns dublin.map.environmentmanager
  (:require [dublin.config :as config]
            [dublin.map.environmentutils :as utils]
            [dublin.map.entity.manager :as entity-manager]
            [dublin.map.tilemap.manager :as tilemap-manager])
  (:gen-class))

(defn environment-init
  "take environment preset and perform loads
  map-preset is list of map sets"
  [map-preset]
  (update-in map-preset [:mapsets]
      (fn [mapsets]
        (doall (map
          #(update-in
            (update-in
              (update-in % [:map-layers]
                                (fn [map-layers] (tilemap-manager/load-maps map-layers)))
                            [:map-tileset]
                                (fn [map-tileset] (tilemap-manager/load-tileset map-tileset)))
                            [:map-objects]
                                (fn [map-objects] (tilemap-manager/load-map-objects map-objects)))
        mapsets)))))

(defn environment-update
  "take state and perform updates"
  [state]
  ;TODO: entity code and objects
  (update-in state [:mapsets (:current state)]
    #(tilemap-manager/update-map-resource-set % 64 64)
  ))

(defn environment-draw
  "draw environment state"
  [gr state]
  (let [mapset-to-draw (nth (:mapsets state) (:current state))]
    ;TODO: temporary; integrate entities
    (doall (map #(tilemap-manager/draw-map-layer gr %) mapset-to-draw))
  )
  )

(defn environment-keypressed
  [key state]
  ;if object key, pass location of player to map manager
  )

(defn environment-keyreleased
  [key state])
