(ns dinsro.ui.currencies
  (:require
   [com.fulcrologic.fulcro.application :as app]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.currencies :as j.currencies]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.ui.accounts :as u.accounts]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.rates :as u.rates]))

(form/defsc-form NewCurrencyForm [_this _props]
  {fo/id           m.currencies/id
   fo/attributes   [m.currencies/name
                    m.currencies/code]
   ;; fo/cancel-route ["admin"]
   fo/route-prefix "new-currency"
   fo/title        "New Currency"})

(def override-form false)

(defsc ShowCurrency
  [this {::m.currencies/keys [code name] :as props}]
  {}
  (dom/div {}
    (dom/p {} "Name: " name)))

(defsc ShowCurrencyPage
  [this {::m.currencies/keys [id name]
         :as                 props}]
  {:ident         (fn []
                    (log/spy :info props)
                    [::m.currencies/id (log/spy :info "ident id" id)])
   :query         [::m.currencies/id
                   ::m.currencies/name]
   :initial-state {::m.currencies/id nil
                   ::m.currencies/name ""}
   :route-segment ["currencies" :id]
   :will-enter
   (fn [app {id :id}]
     (let [ident [::m.currencies/id (log/spy :info id)]]
       (if (log/spy :info (-> (app/current-state app) (get-in (log/spy :info ident)) ::m.currencies/name))
         (do
           (log/info "immediate")
           (dr/route-immediate ident))
         (do
           (log/info "deferred")
           (dr/route-deferred
            [::m.currencies/id id]
            #(df/load app [::m.currencies/id id] ShowCurrencyPage
                      {:post-mutation `dr/target-ready
                       :post-mutation-params
                       {:target [::m.currencies/id id]}}))))
       #_ident))}
  (dom/div {}
    (dom/h1 {} "Show Currency")
    (log/spy :info props)))

(def ui-show-currency (comp/factory ShowCurrency))

(form/defsc-form CurrencyForm [this props]
  {fo/id           m.currencies/id
   fo/attributes   [m.currencies/name
                    m.currencies/code
                    j.currencies/accounts
                    j.currencies/sources
                    j.currencies/current-rate]
   fo/field-styles {::m.currencies/accounts     :link-list
                    ::m.currencies/sources      :link-list
                    ::m.currencies/transactions :link-list}
   fo/cancel-route ["currencies"]
   fo/route-prefix "currency"
   fo/subforms     {::m.currencies/accounts     {fo/ui u.links/AccountLinkForm}
                    ::m.currencies/current-rate {fo/ui u.rates/RateSubForm}
                    ::m.currencies/sources      {fo/ui u.links/RateSourceLinkForm}
                    ::m.currencies/transactions {fo/ui u.links/TransactionLinkForm}}
   fo/title        "Currency"}
  (if override-form
    (form/render-layout this props)
    (dom/div {}
      (dom/h1 "Currency")
      (form/render-layout this props)
      (u.accounts/ui-accounts-sub-report {}))))

(form/defsc-form AdminCurrencyForm [_this _props]
  {fo/id           m.currencies/id
   fo/attributes   [m.currencies/name
                    m.currencies/code]
   fo/cancel-route ["admin"]
   fo/route-prefix "admin/currency"
   fo/title        "Currency"})

(def new-button
  {:label  "New"
   :type   :button
   :action #(form/create! % NewCurrencyForm)})

(report/defsc-report CurrenciesReport
  [_this _props]
  {ro/column-formatters
   {::m.currencies/name
    (fn [this name {::m.currencies/keys [id]}]
      (u.links/ui-currency-link {::m.currencies/id id ::m.currencies/name name})
      #_(dom/a {:onClick #(form/edit! this CurrencyForm id)} name))}
   ro/columns          [m.currencies/name
                        m.currencies/code]
   ro/controls         {::new new-button}
   ro/route            "currencies"
   ro/row-actions      []
   ro/row-pk           m.currencies/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.currencies/index
   ro/title            "Currencies Report"})

(report/defsc-report AdminIndexCurrenciesReport
  [_this _props]
  {ro/columns          [m.currencies/name m.currencies/code]
   ro/controls         {::new {:label  "New Currency"
                               :type   :button
                               :action #(form/create! % AdminCurrencyForm)}}
   ro/source-attribute ::m.currencies/index
   ro/title            "Currencies"
   ro/row-pk           m.currencies/id
   ro/run-on-mount?    true})
