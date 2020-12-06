(ns dinsro.specs.events.forms.registration
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]))

(def default-name "Bob")
(def default-email "bob@example.com")
(def default-password "hunter2")
(def default-error-message "")

(s/def ::name string?)
(def name ::name)

(s/def ::email string?)
(def email ::email)

(s/def ::password string?)
(def password ::password)

(s/def ::confirm-password string?)
(def confirm-password ::confirm-password)

(s/def ::error-message string?)
(def error-message ::error-message)