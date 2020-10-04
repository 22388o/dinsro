(ns dinsro.components.forms-test
  (:require
   [dinsro.cards :as cards :include-macros true]
   [dinsro.components.forms.add-currency-rate-test]
   [dinsro.components.forms.add-user-account-test]
   [dinsro.components.forms.add-user-category-test]
   [dinsro.components.forms.add-user-transaction-test]
   [dinsro.components.forms.admin-create-account-test]
   [dinsro.components.forms.create-account-test]
   [dinsro.components.forms.create-rate-test]
   [dinsro.components.forms.create-transaction-test]
   [dinsro.components.forms.registration-test]
   [dinsro.components.forms.settings-test]
   [taoensso.timbre :as timbre]))

(cards/header
 'dinsro.components.forms-test
 "Form Components" [])