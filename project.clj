
(defproject rkord "0.1.0-SNAPSHOT"
  :description "Recorder for consuming code stats"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [compojure "1.1.8"]
                 [ring "1.2.2"]
                 [rodnaph/confo "0.7.0"]
                 [http-kit "2.1.8"]
                 [clojurewerkz/elastisch "2.0.0-rc1"]
                 [com.novemberain/langohr "2.9.0"]]
  :main rkord.core)

