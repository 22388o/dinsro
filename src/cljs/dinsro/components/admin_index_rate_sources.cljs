(ns dinsro.components.admin-index-rate-sources
  (:require [clojure.spec.alpha :as s]
            [devcards.core :refer-macros [defcard-rg]]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.debug :as c.debug]
            [dinsro.components.links :as c.links]
            [dinsro.events.rate-sources :as e.rate-sources]
            [dinsro.spec.rate-sources :as s.rate-sources]
            [dinsro.specs :as ds]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [reagent.core :as r]
            [re-frame.core :as rf]))

(defn-spec index-line vector?
  [item ::s.rate-sources/item]
  (let [id (:db/id item)
        name (::s.rate-sources/name item)
        url (::s.rate-sources/url item)
        currency-id (get-in item [::s.rate-sources/currency :db/id])]
    [:tr
     [:td id]
     [:td name]
     [:td url]
     [:td [c.links/currency-link currency-id]]
     [:td
      [:button.button
       {:on-click #(rf/dispatch [::e.rate-sources/do-run-source id])}
       "Run"]
      [c.buttons/delete-rate item]]]))

(defn rate-sources-table
  [items]
  (if-not (seq items)
    [:p (tr [:no-rate-sources])]
    [:table.table
     [:thead>tr
      [:th (tr [:id])]
      [:th (tr [:name])]
      [:th (tr [:url])]
      [:th (tr [:currency])]
      [:th (tr [:actions])]]
     (into
      [:tbody]
      (for [item items] ^{:key (:db/id item)} [index-line item]))]))

(defn section-inner
  [items]
  [:div.box
   [:h2.title.is-2 (tr [:rate-sources])]
   [:<>
    [c.debug/debug-box @items]
    (rate-sources-table @items)]])

(defn-spec section vector?
  []
  (let [items (rf/subscribe [::e.rate-sources/items])]
    [section-inner items]))

(defcard-rg form
  "**Admin Index Rate Sources**"
  (fn []
    [:div.box
     [section]])
  {})
