(ns dublin.core
  (:require [dublin.engine.gamewindow :as engine]
            [dublin.config :as config])
  (:gen-class))

(defn -main
  "entrypoint"
  [& args]
  (do
    (System/setProperty "sun.java2d.opengl" "true")
    ;(gsm/init-gsm gsm/LEVEL-STATE)
    (engine/start-window config/WINDOW-TITLE config/WINDOW-WIDTH
            config/WINDOW-HEIGHT config/SCALE-FACTOR config/FRAMERATE)
    ;(gsm/start-pipeline-autosave gsm/LEVEL-STATE)
    ))
