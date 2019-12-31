(ns dinsro.spec.events.forms.add-user-transaction
  (:require [clojure.spec.alpha :as s]
            [dinsro.spec.actions.transactions :as s.a.transactions]))

(s/def ::shown? boolean?)
(def shown? ::shown?)

(s/def ::currency-id string?)
(def currency-id ::currency-id)

(s/def ::date string?)
(def date ::date)

(s/def ::value string?)
(def value ::value)

(s/def ::form-data-input
  (s/cat :value ::value))
(s/def ::form-data-output ::s.a.transactions/create-params-valid)
