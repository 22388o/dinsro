(ns dinsro.ui.forms.add-currency-rate
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [taoensso.timbre :as timbre]))

(defsc AddCurrencyRate
  [_this _props]
  (dom/div
   (u.buttons/ui-close-button #_close-button)

   "Add Currency Rate"))
