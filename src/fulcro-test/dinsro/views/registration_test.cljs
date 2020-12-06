(ns dinsro.views.registration-test
  (:require
   [dinsro.views.registration :as v.registration]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

(ws/defcard RegistrationPage
  {::wsm/card-height 11
   ::wsm/card-width 2}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root v.registration/RegistrationPage
    ::ct.fulcro3/wrap-root? false}))