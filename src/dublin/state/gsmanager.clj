(ns dublin.state.gsmanager
  (:require
            [dublin.config :as config]
            [dublin.state.mainstate :as mainstate])
  (:gen-class))

(defrecord GameState [draw-handler update-handler
                      key-press-handler key-release-handler init-handler pipeline-ref])

(defn new-state-pipeline [] (atom nil))

(def MAIN-STATE 0)

(def current-game-state (atom MAIN-STATE))

(def STATES
  [
  (GameState. #(mainstate/main-draw %1 %2)
              #(mainstate/main-update %)
              #(mainstate/main-keypressed %1 %2)
              #(mainstate/main-keyreleased %1 %2)
              #(mainstate/main-init)
              (new-state-pipeline))
                ])

(defn init-gsm
  "perform resource loads"
  []
  (let [state-record (nth STATES @current-game-state)]
    (do
      (reset! (:pipeline-ref state-record)
              ((:init-handler state-record))))))

(defn state-draw
  "draw current state"
  [gr]
  (let [state-record (nth STATES @current-game-state)]
     ((:draw-handler state-record) gr @(:pipeline-ref state-record))))

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
    ;TODO
    ;   (reset! (:pipeline-ref state-record)
    ;       ((:key-press-handler state-record) key @(:pipeline-ref state-record))))
          ))

(defn keyreleased
    "respond to keyrelease event"
    [key]
    (let [state-record (nth STATES @current-game-state)]
    ;   (reset! (:pipeline-ref state-record)
    ;       ((:key-release-handler state-record) key @(:pipeline-ref state-record))))
          ))
