(ns dublin.state.mainstate
  (:require [dublin.config :as config]
            [dublin.map.environmentmanager :as environmentmanager])
  (:gen-class))


(defn main-init
  "takes environment definition, loads,
  returns long-running state"
  [] (environmentmanager/environment-init config/dublin))

(defn main-update
  "update the environment"
  [state]
  (environmentmanager/environment-update state))

(defn main-draw
  "draw the environment"
  [gr state]
  (environmentmanager/environment-draw gr state))

(defn main-keypressed
  "respond to key event"
  [key state]
  (environmentmanager/environment-keypressed key state))

(defn main-keyreleased
  "respond to key release event"
  [key state]
  (environmentmanager/environment-keyreleased key state))
