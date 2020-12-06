(ns dinsro.ui.index-accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.semantic-ui.elements.button.ui-button :refer [ui-button]]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.sample :as sample]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defsc IndexAccountLine
  [_this {:keys [name user currency initial-value] :as props}]
  {:query [::m.accounts/id :name :user :currency :initial-value]
   :initial-state {:name "name"
                   :user "bob"
                   :currency "sats"
                   :initial-value 23}}
  (timbre/spy :info props)
  (dom/tr
   (dom/td name)
   (dom/td user)
   (dom/td currency)
   (dom/td initial-value)
   (dom/td
    (ui-button {:content (tr [:delete])}))))

(def ui-index-account-line
  (comp/factory IndexAccountLine {:keyfn ::m.accounts/id}))

(defsc IndexAccounts
  [_this {:keys [accounts]}]
  {:query [:accounts]
   :initial-state {:accounts (vals sample/account-map)}}
  (dom/div
   (dom/table
    :.table
    (dom/thead
     (dom/tr
      (dom/th (tr [:name]))
      (dom/th (tr [:user-label]))
      (dom/th (tr [:currency-label]))
      (dom/th (tr [:initial-value-label]))
      (dom/th (tr [:buttons]))))
    (dom/tbody
     (map ui-index-account-line accounts)))))