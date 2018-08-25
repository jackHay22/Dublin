(defproject dublin "0.3.0-SNAPSHOT"
  :description "A walk through Dublin"
  :dependencies [[org.clojure/clojure "1.9.0"]]
  :main ^:skip-aot dublin.core
  :jvm-opts ["-Xdock:name=Dublin"]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
