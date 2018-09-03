(ns dublin.map.environmentutils
  (:require [dublin.config :as config]
            [clojure.java.io :as io])
  (:gen-class))

(defn init-current-mapset
  "load the current mapset in the environment given load fn set"
  [env-state loaders]
  (update-in env-state [:mapsets (:current env-state)]
    (fn [mapset]
      (reduce #(update-in %1 [%2] (%2 loaders))
         mapset (keys loaders)))))

(defn unload-mapset-to-preset
  "unload the indexed mapset to the config preset (free up memory)"
  [env-state config-env-source index]
  (assoc-in env-state [:mapsets index]
      (nth (:mapsets config-env-source) index)))

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

(defn position-to-tile-index
  "convert a position x,y to tilemap indices"
  [pos]
  (map #(int (/ % config/TILE-DIM)) pos))

(defn get-tile-at
  "return the tile at indices"
  [mapset-state layer & indices]
  (reduce nth
      (:map (nth (:map-layers mapset-state) layer)) indices))

(defn check-key-context
  "check area around player if key pressed"
  [key state]
  (let [current-mapset (nth (:mapsets state) (:current state))
        player (:player current-mapset)
        tile-x (+ (:x player) (:width player))
        ;indices (position-to-tile-index)
        ; (get-tile-at current-mapset
        ;         (:layer-index player) (first indices) (second indices))
        tilemarkers (:tile-markers current-mapset)]
        key
        ))

(defn check-links
  "if key is linked key, make-mapset-move
  check for viable maplinks within a proximity of the player
  if any found, check the mapset they index to and get the corresponding link
  then update the environment map index and player location"
  [env-state key loaders config-src]
  (if (= key config/MAP-LINK-CONTROLLER)
      (let [current-mapset-index (:current env-state)
            current-mapset (nth (:mapsets env-state) current-mapset-index)
            current-player (:player current-mapset)]
            (reduce (fn [orig-env current-mapset-link]
                    ;check player proximity to current link
                    (if (> config/LINK-PROXIMITY
                           (get-distance (:x current-player) (:y current-player)
                                         (:px current-mapset-link) (:py current-mapset-link)))
                        ;if viable, transform into destination reduce
                        (reduced
                          (reduce
                            (fn [orig-env-nested target-mapset-link]
                                (if (= (:current env-state) (:set-index target-mapset-link))
                                    (reduced
                                      (unload-mapset-to-preset
                                        (make-player-location-update
                                          (init-current-mapset
                                            (assoc orig-env-nested :current (:set-index current-mapset-link))
                                          loaders) (:px target-mapset-link) (:py target-mapset-link))
                                          config-src current-mapset-index))
                                orig-env-nested))
                              orig-env (:maplinks (nth (:mapsets env-state)
                                      (:set-index current-mapset-link)))))
                            orig-env)) env-state (:maplinks current-mapset)))
          env-state))
