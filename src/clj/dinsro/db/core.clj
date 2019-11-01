(ns dinsro.db.core
  (:require [camel-snake-kebab.extras :refer [transform-keys]]
            [camel-snake-kebab.core :refer [->kebab-case-keyword]]
            [clj-time.jdbc :as jdbc]
            [conman.core :as conman]
            [datahike.api :as d]
            [dinsro.config :refer [env]]
            [hugsql.adapter :as adapter]
            [mount.core :refer [defstate]]
            [java-time.pre-java8 :as jt]
            [taoensso.timbre :as timbre]))

(def uri "datahike:file:///tmp/example")

(defstate ^:dynamic *conn*
  :start (let [uri (env :datahike-url)]
           (d/create-database uri)
           (d/connect uri))
  :stop (d/release *conn*))

#_(def conn (d/connect uri))

(defstate ^:dynamic *db*
          :start (conman/connect! {:jdbc-url (env :database-url)})
          :stop (conman/disconnect! *db*))

(defn result-one-snake->kebab
  [this result options]
  (->> (hugsql.adapter/result-one this result options)
       (transform-keys ->kebab-case-keyword)))

(defn result-many-snake->kebab
  [this result options]
  (->> (hugsql.adapter/result-many this result options)
       (map #(transform-keys ->kebab-case-keyword %))))

(defmethod hugsql.core/hugsql-result-fn :1 [sym]
  'dinsro.db.core/result-one-snake->kebab)

(defmethod hugsql.core/hugsql-result-fn :one [sym]
  'dinsro.db.core/result-one-snake->kebab)

(defmethod hugsql.core/hugsql-result-fn :* [sym]
  'dinsro.db.core/result-many-snake->kebab)

(defmethod hugsql.core/hugsql-result-fn :many [sym]
  'dinsro.db.core/result-many-snake->kebab)

(conman/bind-connection *db* "sql/queries.sql")
