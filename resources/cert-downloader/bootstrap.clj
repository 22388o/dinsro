#!/usr/bin/env bb
(ns cert-downloader.bootstrap
  (:require
   [babashka.curl :as curl]
   [babashka.fs :as fs]
   [clojure.java.io :as io]))

(def default-base-path "/mnt/data")

(defn download-file!
  ([name data-path file-name]
   (download-file! name data-path file-name file-name))
  ([name data-path file-name dest-file-name]
   (try
     (let [url  (format "http://%s.%s/%s" name name file-name)
           path (format "%s/%s" data-path dest-file-name)]
       (println (str "Downloading: " url))
       (io/copy (:body (curl/get url {:as :bytes})) (io/file path)))
     (catch Exception ex
       (println (str "failed to download file: " ex))))))

(defn initialize-cert!
  [path data-path]
  (try
    (let [backup-path (str path ".bak")]
      (fs/delete-if-exists (format "%s/%s" data-path backup-path))
      (download-file! name data-path path backup-path)
      (let [src  (format "%s/%s" data-path backup-path)
            dest (format "%s/%s" data-path path)]
        (fs/delete-if-exists dest)
        (when (fs/readable? src) (fs/move src dest))))
    (catch Exception ex
      (println (str "failed to initialize: " ex)))))

(defn initialize-certs!
  [name]
  (println "Initializing certs")
  (let [base-path default-base-path
        data-path (format "%s/%s" base-path name)]
    (fs/create-dirs (format "%s" data-path))
    (let [paths ["admin.macaroon" "tls.cert"]]
      (doseq [path paths]
        (initialize-cert! path data-path)))))

(defn -main
  []
  (println "Bootstrapping cert downloader")
  (fs/create-dirs (format "%s" default-base-path))
  (let [names ["lnd-alice" "lnd-bob"]]
    (doseq [name names]
      (try
        (initialize-certs! name)
        (catch Exception ex
          (println (str "Error: " ex)))))))

;; Execute main if run as a script
(when (= *file* (System/getProperty "babashka.file")) (-main))
