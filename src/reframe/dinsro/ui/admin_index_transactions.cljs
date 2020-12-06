(ns dinsro.ui.admin-index-transactions
  (:require
   [dinsro.events.transactions :as e.transactions]
   [dinsro.store :as st]))

(defn index-transactions
  [_items]
  [:div])

(defn section
  [store]
  [:div.box
   [:h1 "Index Transactions"]
   (let [items @(st/subscribe store [::e.transactions/items])]
     (if (seq items)
       [index-transactions store items]
       [:p "No data"]))])