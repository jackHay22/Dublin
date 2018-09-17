(ns dublin.menu.utils
  (:require [dublin.config :as config]
            [clojure.java.io :as io])
  (:gen-class))

(defn load-image
  [path]
  (javax.imageio.ImageIO/read (clojure.java.io/resource path)))

(defn draw-paralax-layer
  "draw an image to the gr object"
  [gr image x]
  (try
    (do
      (.drawImage gr image (int x) 0 nil)
      (.drawImage gr image (int (- x (.getWidth image))) 0 nil))
    (catch Exception e
      (println "Dublin => Failed to render image:" image "\n" (.getMessage e)))))

(defn draw-menu-option
  "draw text at x y"
  [gr option x y]
  ;(doto gr
    ;(.setFont config/MENU-FONT)
    ; (.setColor (if (:selected option)
    ;                 config/MENU-COLOR-SELECTED
    ;                 config/MENU-COLOR-DESELECTED))
  ;TODO: drawString is unacceptably slow @ first call
   ;(.drawString (:text option) x y)
  ;)
  )
