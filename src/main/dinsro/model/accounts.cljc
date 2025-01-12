(ns dinsro.model.accounts
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.authorization :as auth]
   [com.fulcrologic.rad.report :as report]
   [com.wsscode.pathom.connect :as pc]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.users :as m.users]
   [dinsro.specs]))

(comment ::auth/_ ::pc/_)

(s/def ::ident (s/tuple keyword? ::id))

(>defn ident
  [id]
  [::id => ::ident]
  [::id id])

(>defn ident-item
  [{::keys [id]}]
  [::item => ::ident]
  (ident id))

(s/def ::id uuid?)
(defattr id ::id :uuid
  {ao/identity? true
   ao/schema    :production})

(s/def ::name string?)
(defattr name ::name :string
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::initial-value (s/or :double double?
                             :zero zero?
                             :number number?))

(defattr initial-value ::initial-value :double
  {ao/identities #{::id}
   ao/schema     :production})

(s/def ::currency ::m.currencies/id)
(defattr currency ::currency :ref
  {ao/cardinality      :one
   ao/required?        true
   ao/identities       #{::id}
   ao/schema           :production
   ao/target           ::m.currencies/id
   ::report/column-EQL {::currency [::m.currencies/id ::m.currencies/name]}})

(s/def ::source (s/or :id ::m.rate-sources/id
                      :nil nil?))
(defattr source ::source :ref
  {ao/cardinality :one
   ao/required?   true
   ao/identities  #{::id}
   ao/schema      :production
   ao/target      ::m.rate-sources/id})

(s/def ::user ::m.users/id)
(defattr user ::user :ref
  {ao/cardinality      :one
   ao/required?        true
   ao/identities       #{::id}
   ao/schema           :production
   ao/target           ::m.users/id
   ::report/column-EQL {::user [::m.users/id ::m.users/name]}})

(s/def ::required-params
  (s/keys :req [::name
                ::initial-value]))

(def required-params
  "Required params for accounts"
  ::required-params)
(s/def ::params (s/keys :req [::currency ::initial-value ::name ::user]
                        :opt [::source]))
(s/def ::item (s/keys :req [::id ::currency ::initial-value ::name ::user]
                      :opt [::source]))

(defn idents
  [ids]
  (mapv (fn [id] {::id id}) ids))

(def attributes [currency id initial-value name source user])

#?(:clj (def resolvers []))
