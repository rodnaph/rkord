
(ns rkord.web
  (:require [compojure.core :refer [routes GET POST]]
            [compojure.handler :refer [api]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [org.httpkit.server :refer [run-server]]
            [langohr.core :as rmq]
            [langohr.channel :as lch]
            [langohr.queue :as lq]
            [langohr.consumers :as lc]
            [langohr.basic :as lb]
            [clojurewerkz.elastisch.native :as es]
            [clojurewerkz.elastisch.native.index :as esi]
            [clojurewerkz.elastisch.native.document :as esd]))

(def queue-exchange "")
(def queue-name "rkord")

(defn- phpunit [ch req]
  (let [content (-> req :params :file :tempfile (slurp))]
    (lb/publish ch queue-exchange queue-name content
                :type "phpunit"
                :content-type "text/xml")
    {:status 200
     :body (str "Got: " content)}))

(defn- queue-handler [esconn ch req ^bytes payload]
  (let [doc {:content "foobar"}]
    (esd/create esconn "rkord" "phpunit" doc))
  (println "Message received"))

(defn start []
  (let [; rabbitmq

        qconn (rmq/connect {:host "192.168.33.99"
                           :username "rkord"
                           :password "blank"})
        ch (lch/open qconn)

        ; elasticsearch

        esconn (es/connect [["192.168.33.99" 9300]]
                           {"cluster.name" "elasticsearch"})]

    ; elasticsearch

    (try
      (esi/create esconn "rkord")
      (catch Exception e
        ; ignore
        ))

    ; rabbitmq

    (lq/declare ch queue-name)
    (lc/subscribe ch queue-name (partial queue-handler esconn))

    ; webserver

    (let [all (routes
                (GET "/" [] "Hello!")
                (-> (POST "/phpunit/:id" [] (partial phpunit ch))
                    (wrap-keyword-params)
                    (wrap-multipart-params)))
          app (api all)]
      (run-server app {:port 8989}))))

