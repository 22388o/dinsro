(ns dinsro.actions.authentication-test
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test :refer :all]
            [dinsro.actions.authentication :as actions.authentication]
            [dinsro.config :as config]
            [dinsro.db.core :as db]
            [dinsro.handler :as handler]
            [dinsro.specs :as ds]
            [dinsro.model.user :as model.user]
            [luminus-migrations.core :as migrations]
            [mount.core :as mount]
            [ring.mock.request :as mock]
            [ring.util.http-status :as status]
            [taoensso.timbre :as timbre]))

(def url-root "/api/v1")

(use-fixtures
  :once
  (fn [f]
    (mount/start #'config/env #'db/*db*)
    (mount/start #'config/env #'handler/app-routes)
    (migrations/migrate ["migrate"] (select-keys config/env [:database-url]))
    (f)))

(deftest check-auth
  (testing "successful"
    (db/delete-users!)
    (let [{:keys [email password] :as user-params} (gen/generate (s/gen ::ds/register-request))
          user (model.user/create-user! user-params)
          response (actions.authentication/check-auth email password)]
      (is (= response true)))))

(deftest authenticate-test
  (let [{:keys [email password] :as user-params} (gen/generate (s/gen ::ds/register-request))]
   (testing "successful"
      (db/delete-users!)
      (let [user (model.user/create-user! user-params)

            body {:email email :password password}
            path (str url-root "/authenticate")
            request (-> (mock/request :post path) (mock/json-body body))
            response ((handler/app) request)]
        (is (= (:status response) status/ok))))
   (testing "failure"
     (db/delete-users!)
     (let [user (model.user/create-user! user-params)
           body {:email email :password (str password "x")}
           path (str url-root "/authenticate")
           request (-> (mock/request :post path) (mock/json-body body))
           response ((handler/app) request)]
       (is (= (:status response) status/unauthorized))))))

(deftest register-test
  (let [path (str url-root "/register")]
    (testing "successful"
      (db/delete-users!)
      (let [params (gen/generate (s/gen ::ds/register-request))
            request (-> (mock/request :post path) (mock/json-body params))
            response ((handler/app) request)]
        (is (= (:status response) status/ok))))
    (testing "invalid params"
      (let [params {}
            request (-> (mock/request :post path) (mock/json-body params))
            response ((handler/app) request)]
        (is (= (:status response) status/bad-request))))))
