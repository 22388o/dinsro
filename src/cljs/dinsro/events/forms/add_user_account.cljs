(ns dinsro.events.forms.add-user-account
  (:require [clojure.spec.alpha :as s]
            [dinsro.spec.events.forms.add-user-account :as s.e.f.add-user-account]
            [dinsro.specs :as ds]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]))

(rfu/reg-basic-sub ::s.e.f.add-user-account/shown?)
(rfu/reg-set-event ::s.e.f.add-user-account/shown?)

(defn toggle
  [cofx event]
  (let [{:keys [db]} cofx]
    {:db (update db ::s.e.f.add-user-account/shown? not)}))

(kf/reg-event-fx ::s.e.f.add-user-account/toggle toggle)

(def default-name "Offshore")

(rfu/reg-basic-sub ::s.e.f.add-user-account/name)
(rfu/reg-set-event ::s.e.f.add-user-account/name)

(rfu/reg-basic-sub ::s.e.f.add-user-account/initial-value)
(rfu/reg-set-event ::s.e.f.add-user-account/initial-value)

(rfu/reg-basic-sub ::s.e.f.add-user-account/currency-id)
(rfu/reg-set-event ::s.e.f.add-user-account/currency-id)

(rfu/reg-basic-sub ::s.e.f.add-user-account/user-id)
(rfu/reg-set-event ::s.e.f.add-user-account/user-id)

(defn-spec create-form-data (s/keys)
  [[name initial-value currency-id user-id] ::s.e.f.add-user-account/form-bindings _ any?]
  {:name          name
   :currency-id   (int currency-id)
   :user-id       (int user-id)
   :initial-value (.parseFloat js/Number initial-value)})

(rf/reg-sub
 ::form-data
 :<- [::s.e.f.add-user-account/name]
 :<- [::s.e.f.add-user-account/initial-value]
 :<- [::s.e.f.add-user-account/currency-id]
 :<- [::s.e.f.add-user-account/user-id]
 create-form-data)
