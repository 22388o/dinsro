(ns dinsro.model.user
  (:require [buddy.hashers :as hashers]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [datahike.api :as d]
            [dinsro.db.core :as db]
            [dinsro.spec.users :as s.users]
            [orchestra.core :refer [defn-spec]]
            [taoensso.timbre :as timbre]))

(def attribute-list
  '[:db/id ::s.users/name ::s.users/email ::s.users/password-hash])

(def identity-attribute ::s.users/email)

(def find-by-email-query
  '[:find ?id
    :in $ ?email
    :where [?id ::s.users/email ?email]])

(defn-spec prepare-record (s/nilable ::s.users/item)
  [params ::s.users/params]
  (when-let [password (::s.users/password params)]
    (-> {::s.users/password-hash (hashers/derive password)}
        (merge params)
        (dissoc ::s.users/password))))

(defn-spec read-record (s/nilable ::s.users/item)
  [user-id :db/id]
  (d/pull @db/*conn* attribute-list user-id))

(defn-spec read-records (s/coll-of (s/nilable ::s.users/item))
  [ids (s/coll-of :db/id)]
  (d/pull-many @db/*conn* attribute-list ids))

(defn-spec find-id-by-email (s/nilable :db/id)
  [email ::s.users/email]
  (ffirst (d/q find-by-email-query @db/*conn* email)))

(defn-spec find-by-email (s/nilable ::s.users/item)
  [email ::s.users/email]
  (read-record (find-id-by-email email)))

(defn-spec create-record :db/id
  [params ::s.users/params]
  (if (nil? (find-id-by-email (::s.users/email params)))
    (let [tempid (d/tempid "user-id")
          record (prepare-record (assoc params :db/id tempid))
          response (d/transact db/*conn* {:tx-data [record]})]
      (get-in response [:tempids tempid]))
    (throw (RuntimeException. "User already exists"))))

(defn-spec index-ids (s/coll-of :db/id)
  []
  (map first (d/q '[:find ?e :where [?e ::s.users/email _]] @db/*conn*)))

(defn-spec index-records (s/coll-of ::s.users/item)
  []
  (read-records (index-ids)))

(defn-spec delete-record any?
  [user-id :db/id]
  (d/transact db/*conn* {:tx-data [[:db/retractEntity user-id]]}))

(defn-spec delete-all nil?
  []
  (doseq [id (index-ids)]
    (delete-record id)))

(defn-spec mock-record ::s.users/item
  []
  (let [params (gen/generate (s/gen ::s.users/params))
        id (create-record params)]
    (read-record id)))

(comment
  (index-ids)

  (mock-user)
  (::s.users/email (first (list-users)))

  (find-by-email (::s.users/email (first (index-records))))
  )
