(ns dublin.map.entity.utils
  (:require [dublin.config :as config]
            [dublin.map.environmentutils :as env-utils])
  (:gen-class))

(defn load-movement-sheet
  "take path and number of frames and load"
  [path frame-total]
  (let [master-image (env-utils/load-image path)
        master-width (.getWidth master-image)
        sub-width (/ master-width frame-total)
        master-height (.getHeight master-image)]
        (doall (map #(.getSubimage master-image % 0 sub-width master-height)
                    (range 0 master-width sub-width)))))

(defn draw-entity-frame
  [gr img x y]
  (env-utils/draw-image gr img x y))
