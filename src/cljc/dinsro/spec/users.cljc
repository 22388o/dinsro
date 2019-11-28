(ns dinsro.spec.users
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.specs :as ds]
            [orchestra.core :refer [defn-spec]]
            [taoensso.timbre :as timbre]))

(s/def ::name string?)
(s/def ::email (s/with-gen #(and % (re-matches #".+@.+\..+" %)) (fn [] ds/email-gen)))
(s/def ::password string?)
(s/def ::password-hash string?)
(s/def ::params (s/keys :req [::name ::email ::password]))
(s/def ::item (s/keys :req [::name ::email ::password-hash]))

(def schema
  [
   ;; {:db/ident       ::id
   ;;  :db/valueType   :db.type/long
   ;;  :db/cardinality :db.cardinality/one}
   {:db/ident       ::name
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident       ::password-hash
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident       ::email
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/unique      :db.unique/identity}])

(comment
  (gen/generate (s/gen ::params))
  (gen/generate (s/gen ::item))

  )
