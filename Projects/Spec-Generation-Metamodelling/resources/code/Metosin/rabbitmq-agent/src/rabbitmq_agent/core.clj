(ns rabbitmq-agent.core
  (:require [langohr.http :as lhttp]
            [clojure.set :refer [rename-keys]]
            [clojure.pprint :refer [pprint]]
            [clojure.tools.logging :as log])
  (:import (com.newrelic.metrics.publish Agent Runner)
           (java.io File))
  (:gen-class))

(def guid "com.pivotal.newrelic.plugin.rabbitmq")
(def version "1.0.5")

(defn ->queue-data [queue-map]
  (-> queue-map
      (rename-keys {:messages_ready :ready, :messages_unacknowledged :unacked})
      (select-keys [:name :vhost :ready :unacked])))

(defn ->queues-data [queue-maps]
  (let [queue-data (map ->queue-data queue-maps)]
    {:queue-data queue-data
     :total-ready (reduce + (map :ready queue-data))
     :total-unacked (reduce + (map :unacked queue-data))}))

(defn rabbitmq-metrics []
  (->queues-data (lhttp/list-queues)))

(defn newrelic-agent [guid version agent-name debug]
  (proxy [Agent] [guid version]
    (pollCycle []
      (let [{:keys [queue-data total-unacked total-ready] :as metrics} (rabbitmq-metrics)]
        (when debug
          (log/debug "Debug enabled, printing metrics")
          (pprint metrics))
        (.reportMetric this "Queued Messages/Unacknowledged" "messages" total-unacked)
        (.reportMetric this "Queued Messages/Ready" "messages" total-ready)
        (doseq [{:keys [vhost ready unacked name]} queue-data]
          (.reportMetric this (str "Queue" vhost name "/Messages/Ready") "message" ready)
          (.reportMetric this (str "Queue" vhost name "/Messages/Total") "message" (+ ready unacked)))))
    (getAgentName [] agent-name)))

(defn create-agent [{:keys [agent-name url user pass debug] :as config}]
  (log/info "Creating Agent with following config:")
  (pprint config)
  (log/info "Langohr connection to the RabbitMQ management")
  (lhttp/connect! url user pass)
  (newrelic-agent guid version agent-name debug))

(defn create-license-key [{:keys [license-key]}]
  (let [license-json (format "{\"license_key\": \"%s\"}" license-key)]
    (log/infof "creating newrelic.json file")
    (spit "config/newrelic.json" license-json)))

(defn read-config [path]
  (let [file (File. path)]
    (if (.exists file)
      (read-string (slurp file))
      (throw (RuntimeException. "config/plugin.edn not found")))))

(defn -main [& _]
  (let [config (read-config "config/plugin.edn")
        _ (create-license-key config)
        agent (create-agent config)
        runner (doto (Runner.) (.add agent))]
    (.setupAndRun runner)))
