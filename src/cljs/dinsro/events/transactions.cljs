(ns dinsro.events.transactions
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events :as e]
   [dinsro.spec :as ds]
   [dinsro.spec.events.transactions :as s.e.transactions]
   [dinsro.spec.transactions :as s.transactions]
   [kee-frame.core :as kf]
   [re-frame.core :as rf]
   [reframe-utils.core :as rfu]
   [taoensso.timbre :as timbre]
   [tick.alpha.api :as tick]))

(def example-transaction
  {:db/id 1
   ::s.transactions/value 130000
   ::s.transactions/date (tick/instant)
   ::s.transactions/currency {:db/id 53}
   ::s.transactions/account {:db/id 12}})

(s/def ::item ::s.transactions/item)

;; Item Map

(s/def ::item-map (s/map-of ::ds/id ::s.transactions/item))
(rfu/reg-basic-sub ::item-map)
(def item-map ::item-map)

;; Items

(s/def ::items (s/coll-of ::item))

(defn items-sub
  "Subscription handler: Index all items"
  [item-map _]
  (sort-by :db/id (vals item-map)))

(s/fdef items-sub
  :args (s/cat :item-map ::item-map
               :event (s/cat :kw keyword?))
  :ret ::items)

(rf/reg-sub ::items :<- [::item-map] items-sub)

;; Item

(defn item-sub
  "Subscription handler: Lookup an item from the item map by id"
  [item-map [_ id]]
  (get item-map id))

(s/fdef item-sub
  :args (s/cat :item-map ::item-map
               :event (s/cat :kw keyword? :id :db/id))
  :ret ::item)

(rf/reg-sub ::item :<- [::item-map] item-sub)

;; Items by Account

(defn items-by-account
  [items event]
  (let [[_ id] event]
    (filter #(= (get-in % [::s.transactions/account :db/id]) id) items)))

(s/fdef items-by-account
  :args (s/cat :items ::items
               :event ::s.e.transactions/items-by-account-event)
  :ret ::items)

(rf/reg-sub ::items-by-account :<- [::items] items-by-account)

;; Items by Currency

(defn items-by-currency
  [db [_ id]]
  (let [items (::items db)]
    (filter #(= (get-in % [::s.transactions/currency :db/id]) id) items)))

(s/fdef items-by-currency
  :args (s/cat :items ::items
               :event ::s.e.transactions/items-by-currency-event)
  :ret ::items)

(rf/reg-sub ::items-by-currency items-by-currency)

;; Items by User

;; FIXME: This will have to read across all linked accounts
(rf/reg-sub
 ::items-by-user
 (fn [db [_ _user-id]]
   (map val (::item-map db))))

;; Index

(s/def ::do-fetch-index-state keyword?)
(rf/reg-sub ::do-fetch-index-state (fn [db _] (get db ::do-fetch-index-state :invalid)))

(defn do-fetch-index-success
  [{:keys [db]} [{:keys [items]}]]
  (let [items (map
               (fn [item] (update item ::s.transactions/date tick/instant))
               items)]
    {:db (-> db
             (update ::item-map merge (into {} (map #(vector (:db/id %) %) items)))
             (assoc ::do-fetch-index-state :loaded))}))

(defn do-fetch-index-failed
  [{:keys [db]} _]
  {:db (assoc db ::do-fetch-index-state :failed)})

(defn do-fetch-index
  [_ _]
  {:http-xhrio
   (e/fetch-request
    [:api-index-transactions]
    [::do-fetch-index-success]
    [::do-fetch-index-failed])})

(s/fdef do-fetch-index
  :args (s/cat :cofx ::s.e.transactions/do-fetch-index-cofx
               :event ::s.e.transactions/do-fetch-index-event)
  :ret ::s.e.transactions/do-fetch-index-response)

(kf/reg-event-fx ::do-fetch-index-success do-fetch-index-success)
(kf/reg-event-fx ::do-fetch-index-failed do-fetch-index-failed)
(kf/reg-event-fx ::do-fetch-index do-fetch-index)

;; Submit

(s/def ::do-submit-state ::ds/state)
(rfu/reg-basic-sub ::do-submit-state)

(defn do-submit-success
  [{:keys [db]} _]
  {:db (assoc db ::do-submit-state :success)
   :dispatch [::do-fetch-index]})

(defn do-submit-failed
  [{:keys [db]} _]
  {:db (assoc db ::do-submit-state :failed)})

(defn do-submit
  [_ [data]]
  {:http-xhrio
   (e/post-request
    [:api-index-transactions]
    [::do-submit-success]
    [::do-submit-failed]
    data)})

(kf/reg-event-fx ::do-submit-failed  do-submit-failed)
(kf/reg-event-fx ::do-submit-success do-submit-success)
(kf/reg-event-fx ::do-submit         do-submit)

;; Delete

(defn do-delete-record-success
  [_ _]
  {:dispatch [::do-fetch-index]})

(defn do-delete-record-failed
  [_ _]
  {:dispatch [::do-fetch-index]})

(defn do-delete-record
  [_ [item]]
  (let [id (:db/id item)]
    {:http-xhrio
     (e/delete-request
      [:api-show-transaction {:id id}]
      [::do-delete-record-success]
      [::do-delete-record-failed])}))

(s/fdef do-delete-record
  :args (s/cat :cofx ::s.e.transactions/do-delete-record-cofx
               :event ::s.e.transactions/do-delete-record-event)
  :ret (s/keys))

(kf/reg-event-fx ::do-delete-record-failed  do-delete-record-failed)
(kf/reg-event-fx ::do-delete-record-success do-delete-record-success)
(kf/reg-event-fx ::do-delete-record         do-delete-record)