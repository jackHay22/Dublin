(ns dublin.map.environmentmanager
  (:require [dublin.config :as config]
            [dublin.map.environmentutils :as utils]
            [dublin.map.entity.manager :as entity-manager]
            [dublin.map.tilemap.manager :as tilemap-manager])
  (:gen-class))

(def temp-x (atom 128))
(def temp-y (atom 220))

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
                (update-in % [:map-layers]
                                (fn [map-layers] (tilemap-manager/load-maps map-layers)))
                             [:map-tileset]
                                (fn [map-tileset] (tilemap-manager/load-tileset map-tileset)))
                             [:map-objects]
                                (fn [map-objects] (tilemap-manager/load-map-objects map-objects)))
                             [:entities]
                                (fn [entities] (entity-manager/load-entity-resource-sets entities)))
        mapsets))))))

(defn environment-update
  "take state and perform updates"
  [state]
  ;TODO: entity code and objects
  ;if changing mapset, move player to new mapset and dissoc from old
  (update-in state [:mapsets (:current state)]
    #(tilemap-manager/update-map-resource-set % @temp-x @temp-y)
  ))

(defn environment-draw
  "draw environment state"
  [gr state]
  (let [mapset-to-draw (nth (:mapsets state) (:current state))
        tileset (:map-tileset mapset-to-draw)
        object-images '() ;(map #(nth (:images %) (:frame %)) (:map-objects mapset-to-draw))
        ]
    ;TODO: temporary; integrate entities
    (doall (map
              #(tilemap-manager/draw-map-layer gr % tileset object-images)
            (:map-layers mapset-to-draw))
  )))

(defn environment-keypressed
  [key state]
  (let [x @temp-x y @temp-y]
  ;if object key, pass location of player to map manager
    (cond (= key :right)
      (reset! temp-x (+ x 5))
          (= key :left)
      (reset! temp-x (- x 5))
      )
      state
  ))

(defn environment-keyreleased
  [key state]
  state
  )
