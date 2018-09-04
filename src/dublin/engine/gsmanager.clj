(ns dublin.engine.gsmanager
  (:require [dublin.config :as config]
            [dublin.map.environmentmanager :as environmentmanager]
            [dublin.menu.manager :as menumanager])
  (:gen-class))

(defrecord GameState [draw-handler update-handler
                      key-press-handler key-release-handler
                      init-handler pipeline-ref])

(defn new-state-pipeline [] (atom nil))

(def current-game-state (atom config/STARTING-STATE))

(def STATES
  [(GameState. menumanager/menu-draw
               menumanager/menu-update
               menumanager/menu-keypressed
               menumanager/menu-keyreleased
               #(menumanager/init-menu config/main-menu)
               (new-state-pipeline))
   (GameState. environmentmanager/environment-draw
               environmentmanager/environment-update
               environmentmanager/environment-keypressed
               environmentmanager/environment-keyreleased
               #(environmentmanager/environment-init config/dublin)
               (new-state-pipeline))
                ])

(defn init-gsm
  "perform resource loads"
  []
  (let [state-record (nth STATES @current-game-state)]
    (do (reset! (:pipeline-ref state-record)
                ((:init-handler state-record))))))

(defn state-draw
  "draw current state"
  [gr]
  (let [state-record (nth STATES @current-game-state)]
     ((:draw-handler state-record)
          gr @(:pipeline-ref state-record))))

(defn state-update
  "Update and Draw the current game state"
  []
  (let [state-record (nth STATES @current-game-state)]
      (reset! (:pipeline-ref state-record)
              ((:update-handler state-record)
                @(:pipeline-ref state-record)))))

(defn keypressed
    "respond to keypress event"
    [key]
    (let [state-record (nth STATES @current-game-state)]
          (reset! (:pipeline-ref state-record)
                ((:key-press-handler state-record)
                    key @(:pipeline-ref state-record)))))

(defn keyreleased
    "respond to keyrelease event"
    [key]
    (let [state-record (nth STATES @current-game-state)]
          (reset! (:pipeline-ref state-record)
                ((:key-release-handler state-record)
                    key @(:pipeline-ref state-record)))))
