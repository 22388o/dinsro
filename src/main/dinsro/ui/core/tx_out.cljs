(ns dinsro.ui.core.tx-out
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.rendering.semantic-ui.field :refer [render-field-factory]]
   [dinsro.model.core.tx-out :as m.c.tx-out]))

(defsc RefRow
  [_this {::m.c.tx-out/keys [n
                             value
                             asm
                             address
                             hex
                             type]}]
  {}
  (dom/tr {}
    (dom/td (str n))
    (dom/td (str asm))
    (dom/td (str value))
    (dom/td (str address))
    (dom/td (str hex))
    (dom/td (str type))))

(def ui-ref-row (comp/factory RefRow {:keyfn ::m.c.tx-out/id}))

(defn ref-row
  [{:keys [value]} _attribute]
  (comp/fragment
   (dom/table :.ui.table
     (dom/thead {}
       (dom/tr {}
         (dom/th {} "n")
         (dom/th {} "asm")
         (dom/th {} "value")
         (dom/th {} "address")
         (dom/th {} "hex")
         (dom/th {} "type")))
     (dom/tbody {}
       (for [tx value]
         (ui-ref-row tx))))))

(def render-ref-row (render-field-factory ref-row))

(def override-tx-out true)

(form/defsc-form CoreTxOutput
  [this props]
  {fo/id           m.c.tx-out/id
   fo/route-prefix "c.tx-in"
   fo/attributes   [m.c.tx-out/n
                    m.c.tx-out/value
                    m.c.tx-out/asm
                    m.c.tx-out/address
                    m.c.tx-out/hex
                    m.c.tx-out/type]
   fo/title        "Output"}
  (if override-tx-out
    (form/render-layout this props)
    (let [{::m.c.tx-out/keys [n value]} props]
      (dom/ul {}
        (dom/li {} "N " (str n))
        (dom/li {} "value " (str value))))))
