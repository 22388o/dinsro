(ns dinsro.mutations.core.tx
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm :refer [defmutation]])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.core.tx :as a.core-tx])
   [dinsro.model.core.blocks :as m.core-block]
   [dinsro.model.core.nodes :as m.core-nodes]
   [dinsro.model.core.tx :as m.core-tx]
   #?(:clj [dinsro.queries.core.nodes :as q.core-nodes])
   #?(:clj [dinsro.queries.core.tx :as q.core-tx])
   [lambdaisland.glogc :as log]))

(comment
  ::m.core-block/_
  ::m.core-nodes/_
  ::m.core-tx/_
  ::pc/_)

(defsc FetchResponse
  [_ _]
  {:initial-state {::m.core-tx/item {}}
   :query [:status
           ::m.core-tx/item]})

(defsc SearchResponse
  [_ _]
  {:initial-state {:status :initial
                   :tx-id nil
                   :node nil
                   :tx nil
                   ::m.core-tx/item {}}
   :query         [:status
                   :tx-id
                   :node
                   :tx
                   ::m.core-tx/item]})

#?(:clj
   (pc/defmutation fetch!
     [_env props]
     {::pc/params #{::m.core-tx/id}
      ::pc/output [:status]}
     (a.core-tx/fetch! props))

   :cljs
   (defmutation fetch! [_props]
     (action [_env] true)
     (remote [env]
       (fm/returning env FetchResponse))))

#?(:clj
   (defn do-search!
     [props]
     (log/info :tx/searching {:props props})
     (let [{tx-id   ::m.core-tx/tx-id
            node-id ::m.core-tx/node} props]
       (log/info :search/started {:tx-id tx-id :node-id node-id})
       (let [result (a.core-tx/search! props)]
         (log/info :search/result {:result result})
         (if result
           {:status  :passed
            :tx  result
            :tx-id   tx-id
            :node-id node-id}
           {:status  :failed
            :tx  result
            :tx-id   tx-id
            :node-id node-id})))))

#?(:clj
   (pc/defmutation search!
     [_env props]
     {::pc/params #{::m.core-tx/id}
      ::pc/output [:status]}
     (do-search! props))

   :cljs
   (defmutation search! [_props]
     (action [_env] true)
     (error-action [env]
       (log/info :search/error {:env env}))
     (ok-action [{:keys [result]
                  :as env}]
       (let [{:keys [body]} result
             data (get body `search!)]
         (log/info :search/completed (merge
                                      {:body body
                                       :data data
                                       :result result}
                                      (when false {:env env})))))

     (remote [env]
       (fm/returning env SearchResponse))))

#?(:clj (def resolvers [fetch! search!]))

#?(:clj
   (comment
     (def node-alice (q.core-nodes/read-record (q.core-nodes/find-id-by-name "bitcoin-alice")))
     (def node-bob (q.core-nodes/read-record (q.core-nodes/find-id-by-name "bitcoin-bob")))
     node-alice
     (def tx-id2 (::m.core-tx/tx-id (first (q.core-tx/index-records))))

     (q.core-nodes/index-ids)

     (do-search! {::m.core-tx/tx-id "foo" ::m.core-tx/node (::m.core-nodes/id node-alice)})
     (tap> (do-search! {::m.core-tx/tx-id tx-id2 ::m.core-tx/node (::m.core-nodes/id node-alice)}))

     nil))