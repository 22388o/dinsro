(ns dinsro.views.index-users
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.sample :as sample]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.index-users :as u.index-users]
   [taoensso.timbre :as timbre]))

(defsc IndexUsersPage
  [_this {:keys [users]}]
  {:query [{:users (comp/get-query u.index-users/IndexUsers)}]
   :ident (fn [] [:page/id ::users])
   :initial-state (fn [_] {:users {:users/list (vals sample/user-map)}})
   :route-segment ["users"]}
  (dom/section
   :.section
   (dom/div
    :.container
    (dom/div
     :.content
     (dom/div
      :.box
      (dom/h1 (tr [:users-page "Users Page"]))
      (dom/hr)
      (u.index-users/ui-index-users users))))))

(def ui-page (comp/factory IndexUsersPage))