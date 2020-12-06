(ns dinsro.ui.admin-index-categories-test
  (:require
   [dinsro.sample :as sample]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.admin-index-categories :as u.admin-index-categories]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as timbre]))

(ws/defcard AdminIndexCategories
  {::wsm/card-height 10
   ::wsm/card-width 3}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.admin-index-categories/AdminIndexCategories
    ::ct.fulcro3/initial-state
    (fn [] {:categories (map sample/category-map [1 2])})
    ::ct.fulcro3/wrap-root? false}))