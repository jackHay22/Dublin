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

(def ** (fn [x] (* x x)))

(defn get-distance
  "get the distance between two points"
  [x1 y1 x2 y2]
  (Math/sqrt (+ (** (- x2 x1)) (** (- y2 y1)))))

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

(defn reduce-first
  "take a predicate and a set and reduce to
  the first found or return nil (common pattern)"
  [pred-fn arg-set]
  (reduce #(if (pred-fn %2) (reduced %2) nil) nil arg-set))

(defn check-links
  "if key is linked key, make-mapset-move
  check for viable maplinks within a proximity of the player
  if any found, check the mapset they index to and get the corresponding link
  then update the environment map index and player location"
  [env-state key loaders]
  (if (= key config/MAP-LINK-CONTROLLER)
      (let [current-mapset (nth (:mapsets env-state) (:current env-state))
            current-player (:player current-mapset)
            viable-link (reduce-first #(> config/LINK-PROXIMITY
                                        (get-distance (:x current-player) (:y current-player)
                                                      (:px %) (:py %)))
                                      (:maplinks current-mapset))]
            (if viable-link
                (let [dest-link (reduce-first #(= (:current env-state) (:set-index %))
                                              (:maplinks (nth (:mapsets env-state)
                                                      (:set-index viable-link))))]
                      (make-player-location-update
                        (init-current-mapset
                          (assoc env-state :current (:set-index viable-link))
                        loaders) (:px dest-link) (:py dest-link)))
                env-state))
        env-state))
