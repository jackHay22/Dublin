(ns dublin.menu.manager
  (:require [dublin.config :as config]
            [dublin.menu.utils :as utils])
  (:gen-class))

(defn init-menu
  "initialize menu preset"
  [preset]
  (update-in preset [:paralax-layers]
    #(map (fn [layer]
            (update-in layer [:image] utils/load-image)) %)))

(defn menu-update
  "update menu state"
  [menu-state]
  (update-in menu-state [:paralax-layers]
    #(map (fn [layer]
            (update-in layer [:x]
                (fn [x] (mod (+ x (:dx layer))
                             (.getWidth (:image layer))))))
          %)))

(defn menu-draw
  "draw menu state"
  [gr menu-state]
  (doall (map #(% gr)
  (concat
    (map (fn [layer]
              #(utils/draw-paralax-layer % (:image layer) (:x layer)))
          (:paralax-layers menu-state))
    (map (fn [option y]
              #(utils/draw-menu-option % option config/MENU-GUTTER y))
          (:menu-options menu-state)
          (range config/MENU-GUTTER config/WINDOW-HEIGHT
                 config/MENU-OPTION-SPACING))))))

(defn menu-keypressed
  "respond to key press"
  [key state]
  state
  )

(defn menu-keyreleased
  "respond to key press"
  [key state]
  state
  )
