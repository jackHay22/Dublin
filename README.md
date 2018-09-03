# Dublin

A Walk through the city.

## Build
```./build```

### Options
- ```--release``` Creates a zip archive from the build and adds to Docker web server image

## Development Mode
- To utilize the repl to reload code after changes, do the following:
  - ```lein repl```
- In the repl, load the initial game state:
  - ```clojure
    (gsm/init-gsm)
    ```
- Start the window:
  - ```clojure
    (engine/start-window config/WINDOW-TITLE config/WINDOW-WIDTH
        config/WINDOW-HEIGHT config/SCALE-FACTOR config/FRAMERATE)
    ```
- To reload code after configuration changes (Note: this applies to map updates rather than code updates):
  - ```clojure
    (gsm/init-gsm)
    ```
## About

Copyright © 2018 Jack Hay
