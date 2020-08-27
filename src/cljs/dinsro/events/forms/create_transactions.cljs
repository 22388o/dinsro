(ns dinsro.events.forms.create-transaction
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.spec.actions.transactions :as s.a.transactions]
   [dinsro.spec.events.forms.create-transaction :as s.e.f.create-transaction]
   [dinsro.store :as st]))

(s/def ::form-data-db (s/keys :req [::s.e.f.create-transaction/account-id
                                    ::s.e.f.create-transaction/date
                                    ::s.e.f.create-transaction/description
                                    ::s.e.f.create-transaction/value]))
(s/def ::form-data-event (s/cat :kw keyword?))
(s/def ::form-data ::s.a.transactions/create-params-valid)
(def form-data ::form-data)

(defn form-data-sub
  [{:keys [::s.e.f.create-transaction/account-id
           ::s.e.f.create-transaction/date
           ::s.e.f.create-transaction/description
           ::s.e.f.create-transaction/value]}
   _]
  {:account-id  (int account-id)
   :value       (.parseFloat js/Number value)
   :description description
   :date        date})

(s/fdef form-data-sub
  :args (s/cat :db ::form-data-db
               :event ::form-data-event)
  :ret ::form-data)

(defn init-handlers!
  [store]
  (doto store
    (st/reg-basic-sub ::shown?)
    (st/reg-set-event ::shown?)
    (st/reg-basic-sub ::s.e.f.create-transaction/account-id)
    (st/reg-set-event ::s.e.f.create-transaction/account-id)
    (st/reg-basic-sub ::s.e.f.create-transaction/date)
    (st/reg-set-event ::s.e.f.create-transaction/date)
    (st/reg-basic-sub ::s.e.f.create-transaction/description)
    (st/reg-set-event ::s.e.f.create-transaction/description)
    (st/reg-basic-sub ::s.e.f.create-transaction/value)
    (st/reg-set-event ::s.e.f.create-transaction/value)
    (st/reg-sub ::form-data form-data-sub))
  store)
