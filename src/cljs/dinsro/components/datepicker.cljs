(ns dinsro.components.datepicker
  (:require [reagent.core :as r]
            [taoensso.timbre :as timbre]))

(defn mount-component
  [comp]
  (let [opts (r/props comp)
        e (r/dom-node comp)
        instance (js/bulmaCalendar. e (clj->js opts))]
    (when-let [on-select (:on-select opts)]
      (.on instance "select"
           #(let [value (.toISOString (js/Date. (.value (.-data %))))]
              (on-select value))))))

(defn update-component
  [comp]
  #_(mount-component comp))

(defn datepicker-inner
  []
  (r/create-class
   {:component-did-mount mount-component
    :component-did-update update-component
    :reagent-render (fn [comp] [:input.input])}))

(defn datepicker-outer
  [config]
  [datepicker-inner config])

(defn datepicker
  [props]
  (let [config (merge {:todayButton false
                       :minuteSteps 1
                       :type "datetime"
                       :validateLabel "OK"
                       :showClearButton false
                       :showHeader false}
                      props)]
    [datepicker-outer config]))
