(ns dinsro.events.websocket
  (:require
   [cljs.core.async :refer [<! offer!]]
   [kee-frame.core :as kf]
   [re-frame.core :as rf]
   [taoensso.timbre :as timbre]
   [transit-websocket-client.core :as websocket])
  (:require-macros
   [cljs.core.async.macros :refer [go-loop]]))

(def websocket-endpoint (str "ws://" (.-host (.-location js/window))  "/ws"))

(defn create-connection
  [url fire-event]
  (timbre/infof "Connecting to endpoint: %s" url)
  (let [channel (websocket/async-websocket url)]
    (go-loop []
      (let [data (<! channel)]
        (rf/dispatch [fire-event data])
        (recur)))
    channel))

(def websocket-channel (create-connection websocket-endpoint ::receive-message))

(defn receive-message
  [_ [data]]
  {:dispatch data})

(defn send-message
  [_ [data]]
  (offer! websocket-channel data)
  {})

(kf/reg-event-fx ::receive-message receive-message)
(kf/reg-event-fx ::send-message send-message)
