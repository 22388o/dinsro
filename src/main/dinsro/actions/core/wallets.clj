(ns dinsro.actions.core.wallets
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.client.bitcoin :as c.bitcoin]
   [dinsro.client.bitcoin-s :as c.bitcoin-s]
   [dinsro.model.core.nodes :as m.core-nodes]
   [dinsro.model.core.wallets :as m.wallets]
   [dinsro.model.core.words :as m.words]
   [dinsro.queries.core.blocks :as q.core-block]
   [dinsro.queries.core.nodes :as q.core-nodes]
   [dinsro.queries.core.wallets :as q.wallets]
   [dinsro.queries.core.words :as q.words]
   [lambdaisland.glogc :as log])
  (:import
   org.bitcoins.core.hd.BIP32Path
   org.bitcoins.core.hd.HDPurpose
   org.bitcoins.core.crypto.MnemonicCode
   org.bitcoins.core.crypto.BIP39Seed
   scala.collection.immutable.Vector))

(defn parse-descriptor
  [descriptor]
  (let [pattern #"(?<type>.*)\(\[(?<fingerprint>.*?)/(?<path>.*)\](?<key>.*?)/(?<keypath>.*)\)#(?<checksum>.*)"
        matcher (re-matcher pattern  descriptor)]
    (when (.matches matcher)
      {:type        (.group matcher "type")
       :fingerprint (.group matcher "fingerprint")
       :path        (.group matcher "path")
       :key         (.group matcher "key")
       :keypath     (.group matcher "keypath")
       :checksum    (.group matcher "checksum")})))

(defn create!
  [{::m.wallets/keys      [name user]
    {node ::m.core-nodes/id} ::m.wallets/node}]
  (let [props {::m.wallets/name name
               ::m.wallets/user user
               ::m.wallets/node node}]
    (log/info :wallet/create {:props props})
    (q.wallets/create-record props)))

(>defn calculate-derivation
  [wallet]
  [::m.wallets/item => any?]
  (let [{::m.wallets/keys [seed]} wallet]
    (MnemonicCode/fromWords (c.bitcoin-s/create-vector seed))))

(defn ->bip39-seed
  [wallet]
  (let [code (calculate-derivation wallet)
        password (BIP39Seed/EMPTY_PASSWORD)]
    (BIP39Seed/fromMnemonic code password)))

(defn ->priv-key
  [wallet]
  (let [seed (->bip39-seed wallet)]
    (c.bitcoin-s/get-xpriv seed 84 "regtest")))

(defn get-word-list
  [wallet-id]
  (let [ids (q.words/find-by-wallet wallet-id)]
    (->> ids
         (map q.words/read-record)
         (sort-by ::m.words/position)
         (mapv
          (fn [word]
            (log/info :word-list/item {:word word})
            (::m.words/word word))))))

(defn update-words!
  [wallet-id words]
  (log/info :words/updating {:wallet-id wallet-id :words words})
  (let [old-ids (q.words/find-by-wallet wallet-id)]
    (doseq [id old-ids]
      (q.words/delete! id))
    (let [response (doall
                    (map-indexed
                     (fn [i word]
                       (let [props {::m.words/word     word
                                    ::m.words/position (inc i)
                                    ::m.words/wallet   wallet-id}
                             word-id (q.words/create-record props)]
                         (q.words/read-record word-id)))
                     words))]
      (log/info :words/update-finished {:response response})
      response)))

(defn get-mnemonic
  [wallet-id]
  (c.bitcoin-s/words->mnemonic (get-word-list wallet-id)))

(defn get-xpriv
  [wallet-id]
  (let [;; wallet (q.wallets/read-record wallet-id)
        mnemonic (get-mnemonic wallet-id)]
    (c.bitcoin-s/get-xpriv (BIP39Seed/fromMnemonic mnemonic (BIP39Seed/EMPTY_PASSWORD)) 84 "regtest")))

(defn get-wif
  [wallet-id]
  (c.bitcoin-s/->wif (.key (get-xpriv wallet-id))))

(defn roll!
  [props]
  (log/info :roll/started {:props props})
  (let [wallet-id (::m.wallets/id props)
        words     (c.bitcoin-s/create-mnemonic-words)
        response  (update-words! wallet-id words)
        wif (get-wif wallet-id)
        wallet (q.wallets/read-record wallet-id)
        ;; props (assoc wallet ::m.wallets/key wif)
        props {::m.wallets/key wif}]
    (q.wallets/update! wallet-id props)
    (log/info :roll/finished {:response response})
    (merge wallet {::m.wallets/words response})))

(comment
  (def descriptor "wpkh([7c6cf2c1/84h/1h/0h]tpubDDV8TbjuWeytsM7mAwTTkwVqWvmZ6TpMj1qQ8xNmNe6fZcZPwf1nDocKoYSF4vjM1XAoVdie8avWzE8hTpt8pgsCosTdAjnweSy7bR1kAwc/0/*)#8phlkw5l")

  {:type        "wpkh"
   :fingerprint "7c6cf2c1"
   :path        "84h/1h/0h"
   :key         "tpubDDV8TbjuWeytsM7mAwTTkwVqWvmZ6TpMj1qQ8xNmNe6fZcZPwf1nDocKoYSF4vjM1XAoVdie8avWzE8hTpt8pgsCosTdAjnweSy7bR1kAwc"
   :keypath     "0/*"
   :checksum    "8phlkw5l"}

  (q.core-nodes/index-records)
  (q.core-block/index-records)
  (def node-name "bitcoin-alice")
  (def node (q.core-nodes/read-record (q.core-nodes/find-id-by-name node-name)))
  node

  (def client (m.core-nodes/get-client node ""))
  client

  (c.bitcoin/add-node client "bitcoin.bitcoin-bob")
  (c.bitcoin/get-peer-info client)
  (c.bitcoin/generate-to-address client "bcrt1q69zq0gn5cuflasuu8redktssdqxyxg8h6mh53j")

  (c.bitcoin-s/create-mnemonic-words)

  (roll! {})

  (q.wallets/index-ids)
  (tap> (q.wallets/index-records))
  (q.wallets/index-records)
  (def wallet (first (q.wallets/index-records)))
  (def wallet-id (::m.wallets/id wallet))

  (roll! {::m.wallets/id wallet-id})

  (c.bitcoin-s/->wif (.key (get-xpriv wallet-id)))
  (get-wif wallet-id)

  (get-word-list wallet-id)

  (c.bitcoin-s/words->mnemonic (get-word-list wallet-id))

  (get-mnemonic wallet-id)

  (update-words! wallet-id (c.bitcoin-s/create-mnemonic-words))
  (->priv-key wallet)

  (->bip39-seed wallet)

  (.words (calculate-derivation (first (q.wallets/index-records))))

  (tap> (seq (.getDeclaredMethods (.getClass (calculate-derivation (first (q.wallets/index-records)))))))

  (tap> (seq (.getDeclaredMethods BIP39Seed)))

  (def xpriv (c.bitcoin-s/get-xpriv
              (BIP39Seed/fromMnemonic
               (calculate-derivation (first (q.wallets/index-records)))
               (BIP39Seed/EMPTY_PASSWORD))
              84 "regtest"))

  (tap> (map
         (fn [m]
           {:str (str m)
            :name (.getName m)
            ;; :return (str (.getReturnTypes m))
            })
         (vec (.getDeclaredMethods (class xpriv)))))
  xpriv

  (.key xpriv)
  (.hex (.chainCode xpriv))

  (.fingerprint xpriv)

  (BIP39Seed/EMPTY_PASSWORD)

  (parse-descriptor descriptor)

  (MnemonicCode.)

  (def account-path (BIP32Path/fromString "m/84'/0'/0'"))
  account-path

  (def purpose (HDPurpose. 84))
  purpose

  (vec (.getDeclaredMethods (class purpose)))

  (let [builder (Vector/newBuilder)]
    (.addOne builder 1)
    (.addOne builder 2)
    (.addOne builder 3)
    (.result builder))

  (tap> (seq (.getDeclaredMethods (.getClass (Vector/newBuilder)))))

  (Vector/fill 1 2 3 4 5)

  nil)