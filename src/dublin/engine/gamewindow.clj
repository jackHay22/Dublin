(ns dublin.engine.gamewindow
  (:gen-class)
  (:require [dublin.config :as config]
            [dublin.engine.gsmanager :as state]))

(import java.awt.event.KeyListener)
(import java.awt.event.KeyEvent)
(import java.awt.image.BufferedImage)
(import javax.swing.JPanel)
(import javax.swing.JFrame)
(import java.awt.Graphics2D)
(import java.awt.Graphics)
(import java.awt.Dimension)

(def control-keys
      {java.awt.event.KeyEvent/VK_UP    :up
       java.awt.event.KeyEvent/VK_DOWN  :down
       java.awt.event.KeyEvent/VK_LEFT  :left
       java.awt.event.KeyEvent/VK_RIGHT :right
       java.awt.event.KeyEvent/VK_SPACE :space
       java.awt.event.KeyEvent/VK_ENTER :enter
       java.awt.event.KeyEvent/VK_SHIFT :shift
       java.awt.event.KeyEvent/VK_W     :up
       java.awt.event.KeyEvent/VK_S     :down
       java.awt.event.KeyEvent/VK_A     :left
       java.awt.event.KeyEvent/VK_D     :right
       java.awt.event.KeyEvent/VK_O     :open
       java.awt.event.KeyEvent/VK_I     :interact})

(def system-thread (atom nil))
(def sleep-ticks-per-second 1000)

(defn graphical-panel
  "-extends JPanel, implements Runnable and KeyListener-"
  [w h s td]
  (let [base-image (BufferedImage. w h BufferedImage/TYPE_INT_ARGB)
        g (cast Graphics2D (.createGraphics base-image))
        window-width (* w s)
        window-height (* h s)]
     (proxy [JPanel Runnable KeyListener] []
            (addNotify []
              (do (proxy-super addNotify)
                  (if (= @system-thread nil)
                      (reset! system-thread (.start (Thread. this))))))
            (keyPressed [e]
              (state/keypressed (control-keys (.getKeyCode e))))
            (keyReleased [e]
              (state/keyreleased (control-keys (.getKeyCode e))))
            (keyTyped [e])
            (paintComponent [^Graphics panel-graphics]
              (proxy-super paintComponent panel-graphics)
              (state/state-draw g)
              (.drawImage panel-graphics base-image 0 0 window-width window-height nil))
            (run [] (loop []
                      (let [render-start (System/nanoTime)]
                      (do (state/state-update)
                          (.repaint this)
                          (Thread/sleep td)))
                    (recur))))))

(defn start-window
  "start JFrame and add JPanel extension as content"
  [title width height scale framerate]
  (let [target-delay (/ sleep-ticks-per-second framerate)
        panel (graphical-panel width height scale target-delay)
        window (JFrame. title)]
        (doto panel
          (.setPreferredSize (Dimension. (* width scale) (* height scale)))
          (.setFocusable true)
          (.requestFocus)
          (.addKeyListener panel))
        (doto window
          (.setContentPane panel)
          (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)
          (.setResizable false)
          (.pack)
          (.setVisible true)
          (.validate)
          (.repaint))))
