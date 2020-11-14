(ns dinsro.ui.account-transactions
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events.forms.add-account-transaction :as e.f.add-account-transaction]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.add-account-transaction :as u.f.add-account-transaction]
   [dinsro.ui.index-transactions :as u.index-transactions]
   [taoensso.timbre :as timbre]))

(defn section
  "List all transactions associated with an account"
  [store account-id items]
  (let [items (or items [])]
    [:div.box
     [:h2
      (tr [:transactions])
      [u.buttons/show-form-button store ::e.f.add-account-transaction/shown?]]
     [u.f.add-account-transaction/form store account-id]
     [:hr]
     [u.index-transactions/index-transactions store items]]))

(s/fdef section
  :args (s/cat :store #(instance? st/Store %)
               :account-id :db/id
               :items (s/coll-of ::m.transactions/item))
  :ret vector?)
