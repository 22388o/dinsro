(ns dinsro.queries.rate-sources
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [crux.api :as crux]
   [dinsro.components.crux :as c.crux]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.specs]
   [dinsro.utils :as utils]
   [taoensso.timbre :as log]))

(def attributes-list
  '[:db/id
    ::m.rate-sources/id
    ::m.rate-sources/name])
(def record-limit 1000)

(def find-dbid-by-id-query
  '[:find ?dbid
    :where [?dbid ::m.rate-sources/id _]])

(def find-eid-by-id-query
  '[:find  ?eid
    :in    $ ?id
    :where [?eid ::m.rate-sources/id ?id]])

(def find-id-by-eid-query
  '[:find  ?id
    :in    $ ?eid
    :where [?eid ::m.rate-sources/id ?id]])

(>defn find-eid-by-id
  [id]
  [::m.rate-sources/id => :db/id]
  (let [db (c.crux/main-db)]
    (ffirst (crux/q db find-eid-by-id-query id))))

(>defn find-id-by-eid
  [eid]
  [:db/id => ::m.rate-sources/id]
  (let [db (c.crux/main-db)]
    (ffirst (crux/q db find-id-by-eid-query eid))))

(>defn create-record
  [params]
  [::m.rate-sources/params => :db/id]
  (let [node   (c.crux/main-node)
        id     (utils/uuid)
        params (assoc params ::m.rate-sources/id id)
        params (assoc params :crux.db/id id)]
    (crux/await-tx node (crux/submit-tx node [[:crux.tx/put params]]))
    id))

(>defn read-record
  [id]
  [:db/id => (? ::m.rate-sources/item)]
  (let [db     (c.crux/main-db)
        record (crux/pull db '[*] id)]
    (when (get record ::m.rate-sources/name)
      (-> record
          (dissoc :db/id)))))

(>defn index-ids
  []
  [=> (s/coll-of :db/id)]
  (let [db (c.crux/main-db)]
    (map first (crux/q db '{:find  [?e]
                            :where [[?e ::m.rate-sources/name _]]}))))

(>defn index-records
  []
  [=> (s/coll-of ::m.rate-sources/item)]
  (map read-record (index-ids)))

(defn index-records-by-currency
  [currency-id]
  (let [db    (c.crux/main-db)
        query '{:find  [?id ?currency-id]
                :keys  [db/id name]
                :in    [$ ?currency-id]
                :where [[?id ::m.rate-sources/currency ?currency-id]]}]
    (->> (crux/q db query currency-id)
         (map :db/id)
         (map read-record)
         (take record-limit))))

(>defn delete-record
  [id]
  [:db/id => any?]
  (let [node (c.crux/main-node)]
    (crux/await-tx node (crux/submit-tx node [[:crux.tx/delete id]]))))

(>defn delete-all
  []
  [=> nil?]
  (doseq [id (index-ids)]
    (delete-record id)))

(comment
  (index-ids)
  (index-records)
  (index-records-by-currency 408231720)
  (delete-all))
