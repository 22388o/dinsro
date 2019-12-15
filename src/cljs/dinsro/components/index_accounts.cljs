(ns dinsro.components.index-accounts
  (:require [clojure.spec.alpha :as s]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.debug :as c.debug]
            [dinsro.components.links :as c.links]
            [dinsro.events.debug :as e.debug]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.translations :refer [tr]]
            [dinsro.views.show-account :as v.show-account]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [taoensso.timbre :as timbre]))

(defn-spec row-line vector?
  [account ::s.accounts/item]
  (let [id (:db/id account)
        name (::s.accounts/name account)
        initial-value (::s.accounts/initial-value account)
        currency-id (get-in account [::s.accounts/currency :db/id])
        user-id (get-in account [::s.accounts/user :db/id])]
    [:tr
     [:td [c.links/account-link id]]
     [:td [c.links/user-link user-id]]
     [:td [c.links/currency-link currency-id]]
     [:td initial-value]
     (c.debug/hide [:td [c.buttons/delete-account account]])]))

(defn-spec index-accounts vector?
  [accounts (s/coll-of ::s.accounts/item)]
  [:<>
   (if-not (seq accounts)
     [:div (tr [:no-accounts])]
     [:table.table
      [:thead
       [:tr
        [:th (tr [:name])]
        [:th (tr [:user-label])]
        [:th (tr [:currency-label])]
        [:th (tr [:initial-value-label])]
        (c.debug/hide [:th (tr [:buttons])])]]
      (into [:tbody]
            (for [account accounts]
              ^{:key (:db/id account)}
              (row-line account)))])])
