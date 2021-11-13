(ns dinsro.components.parser
  (:require
   [com.fulcrologic.rad.attributes :as attr]
   [com.fulcrologic.rad.blob :as blob]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.pathom :as pathom]
   [com.fulcrologic.rad.type-support.date-time :as dt]
   [com.wsscode.pathom.connect :as pc]
   [com.wsscode.pathom.core :as p]
   [dinsro.components.auto-resolvers :refer [automatic-resolvers]]
   [dinsro.components.blob-store :as bs]
   [dinsro.components.config :refer [config]]
   [dinsro.components.xtdb :refer [xtdb-nodes]]
   [dinsro.components.delete-middleware :as delete]
   [dinsro.components.save-middleware :as save]
   [dinsro.model :refer [all-attributes]]
   [dinsro.model.navlink :as m.navlink]
   [dinsro.mutations.core-nodes :as mu.core-nodes]
   [dinsro.mutations.ln-nodes :as mu.ln-nodes]
   [dinsro.mutations.rate-sources :as mu.rate-sources]
   [dinsro.mutations.session :as mu.session]
   [dinsro.mutations.settings :as mu.settings]
   [mount.core :refer [defstate]]
   [roterski.fulcro.rad.database-adapters.xtdb :as xt]
   [taoensso.timbre :as log]))

(def default-timezone "America/Detroit")

(pc/defresolver index-explorer [{::pc/keys [indexes]} _]
  {::pc/input  #{:com.wsscode.pathom.viz.index-explorer/id}
   ::pc/output [:com.wsscode.pathom.viz.index-explorer/index]}
  {:com.wsscode.pathom.viz.index-explorer/index
   (p/transduce-maps
    (remove (comp #{::pc/resolve ::pc/mutate} key))
    indexes)})

(defstate parser
  :start
  (let [database-key :main]
    (pathom/new-parser
     config
     [(attr/pathom-plugin all-attributes)
      (form/pathom-plugin save/middleware delete/middleware)
      (xt/pathom-plugin (fn [_env] {:production (database-key xtdb-nodes)}))
      (blob/pathom-plugin bs/temporary-blob-store {:files         bs/file-blob-store
                                                   :avatar-images bs/image-blob-store})
      {::p/wrap-parser
       (fn transform-parser-out-plugin-external [wrapped-parser]
         (fn transform-parser-out-plugin-internal [env tx]
           ;; TASK: This should be taken from account-based setting
           (dt/with-timezone default-timezone
             (if (and (map? env) (seq tx))
               (wrapped-parser env tx)
               {}))))}]
     [automatic-resolvers
      form/resolvers
      (blob/resolvers all-attributes)
      m.navlink/resolvers
      mu.core-nodes/resolvers
      mu.ln-nodes/resolvers
      mu.rate-sources/resolvers
      mu.session/resolvers
      mu.settings/resolvers
      index-explorer])))
