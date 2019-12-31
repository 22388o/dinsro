(ns dinsro.spec.events.categories
  (:require [clojure.spec.alpha :as s]
            [dinsro.spec.categories :as s.categories]
            [taoensso.timbre :as timbre]))

(s/def ::item (s/nilable ::s.categories/item))
(s/def ::items (s/coll-of ::s.categories/item))

;; Index

(s/def ::do-fetch-index-state keyword?)

(s/def ::do-fetch-index-cofx (s/keys))
(s/def ::do-fetch-index-event vector?)
(s/def ::do-fetch-index-response (s/keys))

(s/def ::do-fetch-index-failed-cofx (s/keys))
(s/def ::do-fetch-index-failed-event vector?)
(s/def ::do-fetch-index-failed-response (s/keys))
