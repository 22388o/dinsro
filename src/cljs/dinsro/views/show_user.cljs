(ns dinsro.views.show-user
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.components :as c]
            [dinsro.components.show-user :refer [show-user]]
            [dinsro.events.accounts :as e.accounts]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.users :as e.users]
            [dinsro.specs :as ds]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(s/def ::init-page-cofx (s/keys))
(s/def ::init-page-event (s/keys))
(s/def ::init-page-response (s/keys))

(defn-spec init-page ::init-page-response
  [cofx ::init-page-cofx
   event ::init-page-event]
  (timbre/spy :info cofx)
  (timbre/spy :info event)
  (let [[{:keys [id]}] event]
    {:dispatch [::e.users/do-fetch-record id]}))

(kf/reg-event-fx ::init-page init-page)

(kf/reg-controller
 ::page-controller
 {:params (c/filter-param-page :show-user-page)
  :start  [::init-page]})

(s/def :show-user-view/id          pos-int?)
(s/def :show-user-view/path-params (s/keys :req-un [:show-user-view/id]))
(s/def ::view-map                  (s/keys :req-un [:show-user-view/path-params]))

(def strings
  {:header "Show User"})

(defn l
  [keyword]
  (get strings keyword (str "MISSING STRING: " keyword)))

(defn-spec page vector?
  [match ::view-map]
  (let [{{:keys [id]} :path-params} (timbre/spy :info match)
        state @(rf/subscribe [::e.users/do-fetch-record-state])]
    [:section.section>div.container>div.content
     [:h1 (l :header)]
     [:button.button {:on-click #(rf/dispatch [::e.users/do-fetch-record id])}
      (str "Load User: " state)]
     (condp = state
       :invalid [:p "invalid"]
       :failed [:p "Failed"]
       :loaded
        (if-let [user @(rf/subscribe [::e.users/item (int id)])]
          (let [user-id (:db/id user)
                accounts @(rf/subscribe [::e.accounts/items-by-user user-id])]
            [:<>
             [:button.button {:on-click #(rf/dispatch [::e.accounts/do-fetch-index])}
              "Load Accounts: " @(rf/subscribe [::e.accounts/do-fetch-index-state])]
             [:button.button {:on-click #(rf/dispatch [::e.currencies/do-fetch-index])}
              "Load Currencies: " @(rf/subscribe [::e.currencies/do-fetch-index-state])]
             [show-user user (timbre/spy :info accounts)]])
          [:p "User not found"])
        [:p "unknown state"])]))
