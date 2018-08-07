(ns dublin.state.mainstate
  (:require [dublin.config :as config]
            [dublin.map.environmentmanager :as environmentmanager])
  (:gen-class))


(defn main-init
  "takes environment definition, loads,
  returns long-running state"
  []
  (environmentmanager/environment-init config/dublin))

(defn main-update
  [state]
  (environmentmanager/environment-update state))

(defn main-draw
  [gr state]
  (environmentmanager/environment-draw gr state))

(defn main-keypressed
  [key]
  ;TODO
)

(defn main-keyreleased
  [key]
  ;TODO
)
