(ns dinsro.ui.forms.add-account-transaction
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.mutations :as fm]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as log]))

(defsc AddAccountTransactionForm
  [this {::keys [description name submit-button]}]
  {:ident         (fn [] [:component/id ::form])
   :initial-state {::description   ""
                   ::name          ""
                   ::submit-button {}}
   :query         [::description
                   ::name
                   ::submit-button]}
  (dom/form :.ui.form
    (bulma/field
     (bulma/control
      (u.inputs/ui-text-input
       {:label (tr [:name]) :value name})))
    (bulma/field
     (bulma/control
      (u.inputs/ui-text-input
       {:label (tr [:description]) :value description}
       {:onChange #(fm/set-string! this ::name :event %)})))
    (bulma/field
     (bulma/control
      (u.inputs/ui-primary-button submit-button)))))

(def ui-add-account-transaction-form
  (comp/factory AddAccountTransactionForm))
