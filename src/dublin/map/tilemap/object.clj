(ns dublin.map.tilemap.object
  (:require [dublin.config :as config]
            [dublin.map.tilemap.utils :as utils])
  (:gen-class))

(def family-marker "_")

(defmacro defobject
  "Macro for defining object functions"
  [name comment operation]
  (list 'def (symbol (str name family-marker))
        (list 'fn '[context]
              (list operation (list 'assoc 'context :operating? true)))))

(defn resolve-function-keyword
  "resolve action to qualified function name"
  [function-keyword]
  (ns-resolve *ns*
    (symbol (str "dublin.map.tilemap.object/"
      (name function-keyword) family-marker))))

(defn action-invoke
  "take object set and operate"
  [mapobject]
  (if (not (:operating? mapobject))
      ((:action mapobject) mapobject)))

(defn action-update
  "update operating object"
  [mapobject]
  (if (:operating? mapobject)
      ((:action mapobject) mapobject)))

;object definitions (note: objects responsible for shutting off)

(defobject tap
  "bar tap that animates when invoked
  2 frames for opening animation,
  5 second delay on open frame
  2 frames for closing animation
  return to closed
  :images :dim :action (self) :controller :frame :operating?"
  (fn [self-state]
    ))
