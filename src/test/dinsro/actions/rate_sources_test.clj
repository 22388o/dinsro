(ns dinsro.actions.rate-sources-test
  (:require
   [clojure.test :refer [deftest is use-fixtures]]
   [dinsro.actions.rate-sources :as a.rate-sources]
   [dinsro.mocks :as mocks]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.queries.rate-sources :as q.rate-sources]
   [dinsro.specs :as ds]
   [dinsro.specs.actions.rate-sources :as s.a.rate-sources]
   [dinsro.test-helpers :refer [start-db]]
   [ring.util.http-status :as status]
   [taoensso.timbre :as timbre]))

(def uri "datahike:file:///tmp/file-example2")

(use-fixtures
  :each
  (fn [f]
    (start-db f [m.currencies/schema m.rate-sources/schema])))

(deftest prepare-record
  (let [currency-id 1
        name "Default Rate Source"
        url "http://example.com/"
        params {:name name
                :url url
                :currency-id currency-id}
        expected {::m.rate-sources/name name
                  ::m.rate-sources/url url
                  ::m.rate-sources/currency {:db/id currency-id}}
        response (a.rate-sources/prepare-record params)]
    (is (= expected response))))

(deftest index-handler
  (let [request {}
        response (a.rate-sources/index-handler request)]
    (is (= (:status response) status/ok))
    (let [body (:body response)
          items (:items body)]
      (is (= [] items)))
    #_(is (= true response))))

(deftest create-handler-valid
  (let [request (ds/gen-key ::s.a.rate-sources/create-request-valid)
        response (a.rate-sources/create-handler request)]
    (is (= status/ok (:status response)))
    (let [id (get-in response [:body :item :db/id])]
      (is (not (nil? ident?)))
      (let [created-record (q.rate-sources/read-record id)]
        (is (not (nil? created-record))
            "record can be read")
        (is (= (:name request) (::m.rate-sources/name response)))))))

(deftest create-handler-invalid
  (let [params {}
        request {:params params}
        response (a.rate-sources/create-handler request)]
    (is (= status/bad-request (:status response))
        "should signal a bad request")))

(deftest read-handler
  (let [rate (mocks/mock-rate-source)
        id (:db/id rate)
        request {:path-params {:id (str id)}}
        response (a.rate-sources/read-handler request)]
    (is (= status/ok (:status response)))))