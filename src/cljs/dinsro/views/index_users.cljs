(ns dinsro.views.index-users
  (:require [ajax.core :as ajax]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components.index-users :refer [index-users]]
            [dinsro.events.users :as e.users]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [taoensso.timbre :as timbre]))

(kf/reg-event-fx
 ::init-page
 (fn-traced [{:keys [db]} _]
   {:db (-> db
            (assoc :failed false)
            (assoc ::e.users/users [])
            (assoc ::loading false))}))

(kf/reg-controller
 ::page-controller
 {:params #(when (= (get-in % [:data :name]) ::page) true)
  :start [::init-page]})

(defn page
  []
  (let [users @(rf/subscribe [::e.users/users])]
    [:section.section>div.container>div.content
     [:h1 "Users Page"]
     [index-users users]]))
