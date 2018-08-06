(ns dublin.state.gsmanager
  (:require
            [dublin.config :as config])
  (:gen-class))

(defrecord GameState [draw-handler update-handler
                      key-press-handler key-release-handler init-handler pipeline-ref])

(defn new-state-pipeline [] (atom nil))

(def LOAD-STATE 0)


(def current-game-state (atom LOAD-STATE))

(def STATES
  [
  ; (GameState. #(loadstate/draw-load %1 %2)
  ;               #(loadstate/update-load %)
  ;               #(loadstate/keypressed-load %1 %2)
  ;               #(loadstate/keyreleased-load %1 %2)
  ;               #(loadstate/init-load)
  ;               (new-state-pipeline))
                ])

(defn start-pipeline-autosave
  "start the autosaver with a reference to l1 state"
  [state-to-save]
  ;TODO: start only when level data loaded
  ;(save/start-autosaver (:pipeline-ref (nth STATES state-to-save)))
  )

(defn init-gsm
  "perform resource loads"
  [starting-state]
  ; (let [state-record (nth STATES starting-state)]
  ; (do
  ;   (reset! current-game-state starting-state)
  ;   (reset! (:pipeline-ref state-record) ((:init-handler state-record)))))
    )

(defn state-draw
  "draw current state"
  [gr]
  ; (let [state-record (nth STATES @current-game-state)]
  ;   ((:draw-handler state-record) gr @(:pipeline-ref state-record)))
    )

(defn state-update
  "Update and Draw the current game state"
  []
  ; (let [state-record (nth STATES @current-game-state)
  ;       state-transform ((:update-handler state-record) @(:pipeline-ref state-record))]
  ;     (if (= state-transform nil)
  ;         (swap! current-game-state inc)
  ;         (reset! (:pipeline-ref state-record) state-transform)))
          )

(defn keypressed
    "respond to keypress event"
    [key]
    ; (let [state-record (nth STATES @current-game-state)]
    ;   (reset! (:pipeline-ref state-record)
    ;       ((:key-press-handler state-record) key @(:pipeline-ref state-record))))
          )

(defn keyreleased
    "respond to keyrelease event"
    [key]
    ; (let [state-record (nth STATES @current-game-state)]
    ;   (reset! (:pipeline-ref state-record)
    ;       ((:key-release-handler state-record) key @(:pipeline-ref state-record))))
          )
