(ns dublin.map.environmentutils
  (:require [dublin.config :as config]
            [clojure.java.io :as io])
  (:gen-class))

(import java.awt.Image)
(import java.awt.AlphaComposite)
(import java.awt.image.RescaleOp)
(import java.awt.image.BufferedImage)
(import java.awt.geom.AffineTransform)
(import java.awt.image.AffineTransformOp)

(defn init-current-mapset
  [env-state loaders]
  (update-in env-state [:mapsets (:current env-state)]
    (fn [mapset]
      (reduce #(update-in %1 [%2] (%2 loaders))
         mapset (list :map-layers :map-tileset :map-objects :entities :player)))))

(defn load-image
  "load an image from resources"
  [path]
  (javax.imageio.ImageIO/read (clojure.java.io/resource path)))

(defn draw-image
  "draw an image to the gr object"
  [gr img x y]
  (try
    (.drawImage gr img x y nil)
    (catch Exception e
      (println "Dublin => Failed to render image:" img "\n" (.getMessage e)))))

(defn get-release-keyword
  [key] (keyword (subs (str key "-release") 1)))

(def square (fn [x] (* x x)))

(defn get-distance
  "get the distance between two points"
  [x1 y1 x2 y2]
  (Math/sqrt (+ (square (- x2 x1)) (square (- y2 y1)))))

(defn complex-attrib-reduce
  "reduce a complex list to max val with transforms"
  [complex-list attrib transforms]
  (reduce attrib
    (reduce
      (fn [current-list t]
        (if (map? current-list)
            (t current-list)
            (map t current-list)))
    complex-list transforms)))

(defn make-player-location-update
  "take env-state and update player location in current"
  [env-state new-x new-y]
  (assoc-in
    (assoc-in env-state [:mapsets (:current env-state) :player :x] new-x)
                        [:mapsets (:current env-state) :player :y] new-y))

; (defn match-links
;   [viable-links env-state]
;   (let [linked-targets ])
;   )

(defn check-links
  "if key is linked key, make-mapset-move"
  [env-state key loaders]
  (if (= key config/MAP-LINK-CONTROLLER)
      (let [current-mapset (nth (:mapsets env-state) (:current env-state))
            current-player (:player current-mapset)
            viable-links (filter
                            #(> config/LINK-PROXIMITY
                                (get-distance (:x current-player) (:y current-player)
                                              (:px %) (:py %))) (:maplinks current-mapset))]
            (if (not (empty? viable-links))
                (let [dest-link (first
                                  (:maplinks (nth (:mapsets env-state)
                                                  (:set-index (first viable-links)))))]
                (make-player-location-update
                  (init-current-mapset
                    (assoc env-state :current (:set-index (first viable-links)))
                  loaders) (:px dest-link) (:py dest-link)))
                env-state))
        env-state))
