(ns dinsro.ui.currencies
  (:require
   [com.fulcrologic.fulcro.application :as app]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
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
  [_this {::m.currencies/keys [code name] :as _props}]
  {}
  (dom/div {}
    (dom/p {} "Name: " name)
    (dom/p {} "Code: " code)))

(defsc ShowCurrencyPage
  [_this {::m.currencies/keys [id name]}]
  {:ident         (fn [] [::m.currencies/id (new-uuid id)])
   :query         [::m.currencies/id
                   ::m.currencies/name]
   :initial-state {::m.currencies/id   nil
                   ::m.currencies/name ""}
   :route-segment ["currencies" :id]
   :will-enter
   (fn [app {id :id}]
     (let [ident [::m.currencies/id (new-uuid id)]]
       (if (-> (app/current-state app) (get-in ident) ::m.currencies/name)
         (dr/route-immediate ident)
         (let [options  {:post-mutation        `dr/target-ready
                         :post-mutation-params {:target ident}}
               callback #(df/load app ident ShowCurrencyPage options)]
           (dr/route-deferred ident callback)))))}
  (dom/div {}
    (dom/h1 {} "Show Currency")
    (dom/p {} (str "Name: " name))))

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
    (fn [_this name {::m.currencies/keys [id]}]
      (u.links/ui-currency-link {::m.currencies/id id ::m.currencies/name name}))}
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
