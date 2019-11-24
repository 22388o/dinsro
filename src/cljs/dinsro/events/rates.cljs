(ns dinsro.events.rates
  (:require [ajax.core :as ajax]
            [clojure.spec.alpha :as s]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.spec.rates :as s.rates]
            [dinsro.specs :as ds]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [taoensso.timbre :as timbre]))

(rf/reg-sub ::items (fn [db _] (get db ::items [])))
(s/def ::items (s/coll-of ::s.rates/item))

(rf/reg-sub
 ::item
 :<- [::items]
 (fn [items [_ id]]
   (first (filter #(= (:id %) id) items))))

(kf/reg-event-fx
 ::do-submit-success
 (fn-traced [_ _]
   (timbre/info "submit success")))

(kf/reg-event-fx
 ::do-submit-failed
 (fn-traced [_ _]
   (timbre/info "submit failed")))

(defn-spec do-submit (s/keys)
  [{:keys [db]} any?
   [data] any?]
  {:db (assoc db ::do-submit-loading true)
   :http-xhrio
   {:method          :post
    :uri             (kf/path-for [:api-index-rates])
    :params          data
    :format          (ajax/json-request-format)
    :response-format (ajax/json-response-format {:keywords? true})
    :on-success      [::do-submit-success]
    :on-failure      [::do-submit-failed]}})

(kf/reg-event-fx
 ::do-submit do-submit
 #_(fn-traced
   [{:keys [db]} [data]]
   {:db (assoc db ::do-submit-loading true)
    :http-xhrio
    {:method          :post
     :uri             (kf/path-for [:api-index-rates])
     :params          data
     :format          (ajax/json-request-format)
     :response-format (ajax/json-response-format {:keywords? true})
     :on-success      [::do-submit-success]
     :on-failure      [::do-submit-failed]}}))

(kf/reg-event-db
 ::do-fetch-index-success
 (fn-traced [db [{:keys [items]}]]
   (assoc db ::items items)))

(kf/reg-event-fx
 ::do-fetch-index-failed
 (fn-traced [_ _]
   (timbre/info "fetch records failed")))

(kf/reg-event-fx
 ::do-fetch-index
 (fn-traced [{:keys [db]} _]
   {:db (assoc db ::do-fetch-index-loading true)
    :http-xhrio
    {:method          :get
     :uri             (kf/path-for [:api-index-rates])
     :response-format (ajax/json-response-format {:keywords? true})
     :on-success      [::do-fetch-index-success]
     :on-failure      [::do-fetch-index-failed]}}))

;; Delete

(s/def ::do-delete-record-cofx (s/keys))
(s/def ::do-delete-record-event (s/cat ))
(s/def ::do-delete-record-success-cofx (s/keys))
(s/def ::do-delete-record-failed-cofx (s/keys))

(defn-spec do-delete-record-success (s/keys)
  [cofx ::do-delete-record-success-cofx _ any?]
  {:dispatch [::do-fetch-index]})

(defn-spec do-delete-record-failed (s/keys)
  [cofx ::do-delete-record-failed-cofx _ any?]
  (timbre/error "Delete record failed")
  {:dispatch [::do-fetch-index]})

(defn-spec do-delete-record (s/keys)
  [cofx ::do-delete-cofx _ any?]
  {:http-xhrio
   {:uri             (kf/path-for [:api-show-rate {:id id}])
    :method          :delete
    :format          (ajax/json-request-format)
    :response-format (ajax/json-response-format {:keywords? true})
    :on-success      [::do-delete-record-success]
    :on-failure      [::do-delete-record-failed]}})

(kf/reg-event-fx ::do-delete-record-failed  do-delete-record-failed)
(kf/reg-event-fx ::do-delete-record-success do-delete-record-success)
(kf/reg-event-fx ::do-delete-record         do-delete-record)
