(ns dinsro.ui
  (:require
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.rate-sources :as e.rate-sources]
   [dinsro.events.users :as e.users]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [reframe-utils.core :as rfu]
   [taoensso.timbre :as timbre]))

(def strings {})

(defn l
  [keyword]
  (get strings keyword (str "Missing string: " keyword)))
