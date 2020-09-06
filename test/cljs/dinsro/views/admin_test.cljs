(ns dinsro.views.admin-test
  (:require
   [devcards.core :refer-macros [defcard-rg]]
   [dinsro.cards :as cards]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.views.admin :as v.admin]))

(cards/header "Admin View" [])

(let [store (mock-store)
      match nil]
  (defcard-rg load-buttons
    (fn []
      [error-boundary
       [v.admin/load-buttons store]]))

  (defcard-rg users-section
    (fn []
      [error-boundary
       [v.admin/users-section store]]))

  (defcard-rg page
    (fn []
      [error-boundary
       [v.admin/page store match]])))
