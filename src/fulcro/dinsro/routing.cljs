(ns dinsro.routing
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.views.admin :as v.admin]
   [dinsro.views.home :as v.home]
   [dinsro.views.index-accounts :as v.index-accounts]
   [dinsro.views.index-categories :as v.index-categories]
   [dinsro.views.index-currencies :as v.index-currencies]
   [dinsro.views.index-rates :as v.index-rates]
   [dinsro.views.index-rate-sources :as v.index-rate-sources]
   [dinsro.views.index-transactions :as v.index-transactions]
   [dinsro.views.index-users :as v.index-users]
   [dinsro.views.login :as v.login]
   [taoensso.timbre :as timbre]))

(defrouter RootRouter
  [_this props]
  {:router-targets [v.admin/AdminPage

                    v.home/HomePage
                    v.index-accounts/IndexAccountsPage
                    v.index-categories/IndexCategoriesPage
                    v.index-currencies/IndexCurrenciesPage
                    v.index-rates/IndexRatesPage
                    v.index-rate-sources/IndexRateSourcesPage
                    v.index-transactions/IndexTransactionsPage
                    v.index-users/IndexUsersPage

                    v.login/LoginPage]}
  (do (timbre/spy :info props)
      (dom/div "No route selected")))

(def ui-root-router (comp/factory RootRouter))
