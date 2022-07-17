(ns dinsro.helm.specter
  (:require
   #?(:clj [clj-yaml.core :as yaml])
   #?(:cljs [dinsro.yaml :as yaml])))

(defn ->node-config
  [options]
  (let [{:keys [name alias rpcuser rpcpassword port host]} options]
    {:name          name
     :alias         alias
     :autodetect    false
     :datadir       ""
     :user          rpcuser
     :password      rpcpassword
     :port          port
     :host          host
     :protocol      "http"
     :external_node true
     :fullpath      (str "/data/.specter/nodes/" name ".json")}))

(defn merge-defaults
  [options]
  (let [{:keys [name alias rpcuser rpcpassword port host]
         :or
         {name        "foo"
          alias       "bar"
          rpcuser     "rpcuser"
          rpcpassword "rpcpassword"
          port        18443
          host        (str "lnd." name)}} options]

    {:name        name
     :alias       alias
     :rpcuser     rpcuser
     :rpcpassword rpcpassword
     :port        port
     :host        host}))

(defn ->values
  [{:keys [name] :as options}]
  (let [options (merge-defaults options)]
    {:image        {:tag "v1.7.2"}
     :ingress      {:hosts [{:host  (str "specter." name ".localhost")
                             :paths [{:path "/"}]}]}
     :persistence  {:storageClassName "local-path"}
     :walletConfig (prn-str (->node-config options))}))

(defn ->values-yaml
  [options]
  (yaml/generate-string (->values (merge-defaults options))))
