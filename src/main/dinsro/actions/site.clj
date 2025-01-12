(ns dinsro.actions.site
  (:require
   [clojure.java.io :as io]
   [clojure.edn :as edn]
   [clojure.string :as string]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.queries.core.nodes :as q.c.nodes]
   [dinsro.queries.core.blocks :as q.c.blocks]
   [dinsro.queries.core.tx :as q.c.tx]
   [ring.util.codec :as codec]))

(defn load-edn
  "Load edn from an io/reader source (filename or io/resource)."
  [source]
  (try
    (with-open [r (io/reader source)]
      (edn/read (java.io.PushbackReader. r)))
    (catch java.io.IOException _e
      #_(printf "Couldn't open '%s': %s\n" source (.getMessage e))
      nil)
    (catch RuntimeException _e
      #_(printf "Error parsing edn file '%s': %s\n" source (.getMessage e))
      nil)))

(defn parse-lnurl
  [url]
  (let [[_ host port args] (re-matches #"^lndconnect://(?<host>[^:]+):(?<port>[\d]+)\?(?<args>.*)$" url)
        {macaroon "macaroon"
         cert     "cert"}  (codec/form-decode args)]
    {:host host :port port :macaroon macaroon :cert cert}))

(defn get-lnuris
  [site-config]
  (first (get-in site-config [:seeds :core-nodes :lnd-uris])))

(defn get-node-config
  [site-config]
  (first (get-in site-config [:seeds :core-nodes :nodes])))

(defn create-core-nodes
  [site-config]
  (let [{:keys [name host port rpcuser rpcpass]} (get-node-config site-config)]
    (q.c.nodes/create-record
     {::m.c.nodes/name    name
      ::m.c.nodes/host    host
      ::m.c.nodes/port    port
      ::m.c.nodes/rpcuser rpcuser
      ::m.c.nodes/rpcpass rpcpass})))

(comment
  (edn/read-string (slurp (io/file "site.edn")))

  (slurp (io/file "site.edn"))

  (tap> (first (get-in (load-edn "site.edn") [:seeds :core-nodes :lnd-uris])))

  (def ln-url (first (get-in (load-edn "site.edn") [:seeds :core-nodes :lnd-uris])))
  ln-url
  (tap> ln-url)

  (get-lnuris (load-edn "site.edn"))

  (first (get-in (load-edn "site.edn") [:seeds :core-nodes :nodes]))

  (q.c.nodes/index-records)

  (doseq [id (q.c.nodes/index-ids)]
    (q.c.nodes/delete! id))

  (doseq [id (q.c.blocks/index-ids)]
    (q.c.blocks/delete id))

  (doseq [id (q.c.tx/index-ids)]
    (q.c.tx/delete id))

  (create-core-nodes (load-edn "site.edn"))

  (tap> (parse-lnurl ln-url))

  (first (string/split ln-url #"\?"))
  (tap> (codec/form-decode (second (string/split ln-url #"\?"))))

  nil)
