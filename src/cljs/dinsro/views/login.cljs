(ns dinsro.views.login
  (:require [ajax.core :as ajax]
            [clojure.string :as string]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [taoensso.timbre :as timbre]))

(rf/reg-sub ::email ::email)
(rf/reg-sub ::password ::password)

(rf/reg-sub :state :state)

(rf/reg-sub
 :login-disabled?
 (fn [db _] (rf/subscribe [:state]))
 (fn [state _] (not= state :ready)))

(rf/reg-sub
 :no-email
 (fn [db _] (rf/subscribe [::email]))
 (fn [email _] (string/blank? email)))

(rf/reg-event-db
 :change-email
 (fn-traced [db [_ email]]
   (assoc db ::email email)))

(rf/reg-event-db
 :change-password
 (fn-traced [db [_ password]]
   (assoc db ::password password)))

(rf/reg-event-db
 :login-no-email
 (fn-traced [db [_ _]]
   (assoc db :no-email true)))

(rf/reg-event-fx
 :login-failed
 (fn [{:keys [db]} event]
   {:db (-> db
            (assoc :login-failed true)
            (assoc :loading false))}))

(rf/reg-event-fx
 :submit-login
 (fn [_ [_ data]]
   {:http-xhrio
    {:method :post
     :uri "/api/v1/authenticate"
     :params data
     :timeout 8000
     :format          (ajax/json-request-format)
     :response-format (ajax/json-response-format {:keywords? true})
     :on-success [:login-succeeded]
     :on-failure [:login-failed]}}))

(rf/reg-event-fx
 :login-click
 (fn-traced [{:keys [db]} _]
   (let [email (::email db)
         password (::password db)]
     {:db (assoc db :loading true)
      :dispatch [:submit-login {:email email :password password}]})))

(defn page []
  [:div.section
   [:div.container
    [:form.is-centered
     [c/email-input "Email" ::email :change-email]
     [c/password-input "Password" ::password :change-password]
     [:div.field
      [:div.control
       [:a.button.is-primary
        {:on-click (fn [e] (rf/dispatch [:login-click]))}
        "Login"]]]]]])
