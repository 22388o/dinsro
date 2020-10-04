(ns dinsro.spec.actions-test
  (:require
   [dinsro.cards :as cards :include-macros true]
   [dinsro.spec.actions.accounts-test]
   [dinsro.spec.actions.categories-test]
   [dinsro.spec.actions.rate-sources-test]
   [dinsro.spec.actions.rates-test]
   [taoensso.timbre :as timbre]))

(cards/header
 'dinsro.spec.actions-test
 "Actions Specs" [])