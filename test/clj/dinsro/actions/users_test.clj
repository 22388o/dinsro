(ns dinsro.actions.users-test
  (:require
   [clojure.test :refer [are deftest is use-fixtures]]
   [datahike.api :as d]
   [datahike.config :refer [uri->config]]
   [dinsro.actions.users :as a.users]
   [dinsro.config :as config]
   [dinsro.db :as db]
   [dinsro.mocks :as mocks]
   [dinsro.model.users :as m.users]
   [dinsro.spec :as ds]
   [dinsro.spec.users :as s.users]
   [mount.core :as mount]
   [ring.mock.request :as mock]
   [ring.util.http-status :as status]))

(def uri "datahike:file:///tmp/file-example2")

(use-fixtures
  :each
  (fn [f]
    (mount/start #'config/env #'db/*conn*)
    (d/delete-database uri)
    (when-not (d/database-exists? (uri->config uri))
      (d/create-database uri))
    (with-redefs [db/*conn* (d/connect uri)]
      (d/transact db/*conn* s.users/schema)
      (f))))

(deftest create-record-response-test
  (let [params (ds/gen-key ::s.users/input-params-valid)
        response (a.users/create-handler {:params params})]
    (is (= (:status response) status/ok))))

(deftest index-handler-empty
  (let [path "/users"
        request (mock/request :get path)
        response (a.users/index-handler request)]
    (is (= (:status response) status/ok))))

(deftest index-handler-with-records
  (mocks/mock-user)
  (let [request {}
        response (a.users/index-handler request)]
    (is (= (:status response) status/ok))
    (is (= 1 (count (:body response))))))

(deftest read-handler-success
  (let [params (ds/gen-key ::s.users/params)
        id (m.users/create-record params)
        request {:path-params {:id (str id)}}
        response (a.users/read-handler request)]
    (is (= status/ok (:status response))
        "Should return an ok status")
    (are [key] (= (get params key) (get-in response [:body key]))
      :id :email)))

(deftest read-handler-not-found
  (let [id (ds/gen-key ::ds/id)
        request {:path-params {:id (str id)}}
        response (a.users/read-handler request)]
    (is (= (:status response) status/not-found)
        "Should return a not-found response")))