(ns dublin.map.tilemap.object
  (:require [dublin.config :as config]
            [dublin.map.tilemap.utils :as utils])
  (:gen-class))

(def family-marker "_")

(defmacro defobject
  "Macro for defining object functions"
  [name comment [self-arg] operation]
  (list 'def (symbol (str name family-marker))
          (list 'fn '[self-arg]
            (list 'fn '[mapset-state]
               (list 'merge 'self-arg
                  (list operation 'mapset-state))))))

(defn resolve-function-keyword
  "resolve action to qualified function name"
  [function-keyword]
  (ns-resolve *ns*
    (symbol (str "dublin.map.tilemap.object/"
      (name function-keyword) family-marker))))

;object definitions (note: objects responsible for shutting off)

(defobject tap
  "bar tap that animates when invoked
  2 frames for opening animation,
  5 second delay on open frame
  2 frames for closing animation
  return to closed
  self:
    :images :width :action (self) :controller :frame :operating?"
  [self]
  (fn [mapset-state]

    ;return object state changes
    {}
    ))

(defobject door
  "a door that animates when close to player
  to indicate a maplink"
  [self]
  (fn [mapset-state]

    ;return object state changes
    {}
    ))

(defn update-objects-from-state
  "update all objects given state"
  [objects mapset-state]
  (map #((:action %) mapset-state) objects))
