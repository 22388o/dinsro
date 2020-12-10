(ns dinsro.ui.index-transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defsc IndexTransactionLine
  [_this {::m.transactions/keys [date description account-id]}]
  {:query [::m.transactions/id
           ::m.transactions/date
           ::m.transactions/description
           ::m.transactions/account-id]
   :ident ::m.transactions/id
   :initial-state (fn [{::m.transactions/keys [id]}] {::m.transactions/id id})
   :css [[:.card {:margin-bottom "5px"}]]}
  (dom/div
   :.card
   (dom/div
    :.card-content
    (dom/div
     :.level.is-mobile
     (dom/div
      :.level-left
      (dom/div
       :.level-item
       (dom/p description))))
    (dom/div
     :.level.is-mobile
     (dom/div
      :.level-left
      (dom/div
       :.level-item
       date))
     (dom/div
      :.level-right
      (dom/div
       :.level-item
       account-id))))
   (dom/footer
    :.card-footer
    (dom/a
     :.button.card-footer-item
     {:onClick (fn [_] (timbre/info "delete"))}
     (tr [:delete])))))

(def ui-index-transaction-line
  (comp/factory IndexTransactionLine {:keyfn ::m.transactions/id}))

(defsc IndexTransactions
  [_this {::keys [transactions]}]
  {:query [{::transactions (comp/get-query IndexTransactionLine)}]
   :initial-state
   (fn [_]
     (let [ids [1]]
       {::transactions
        (map #(comp/get-initial-state IndexTransactionLine {::m.transactions/id %}) ids)}))}
  (if (seq transactions)
    (dom/div
     (map ui-index-transaction-line transactions))
    (dom/p "no items")))

(def ui-index-transactions
  (comp/factory IndexTransactions))
