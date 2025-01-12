(ns dinsro.components.nrepl
  (:require
   [dinsro.components.config :as config]
   [mount.core :as mount]
   [nrepl.server :as nrepl]
   [taoensso.timbre :as log]))

(defn nrepl-handler []
  (require 'cider.nrepl)
  (ns-resolve 'cider.nrepl 'cider-nrepl-handler))

(defn start
  "Start a network repl for debugging on specified port followed by
  an optional parameters map. The :bind, :transport-fn, :handler,
  :ack-port and :greeting-fn will be forwarded to
  clojure.tools.nrepl.server/start-server as they are."
  [{:keys [port bind transport-fn handler ack-port greeting-fn]}]
  (try
    (log/info "starting nREPL server on port" port)
    (nrepl/start-server :port port
                        :bind bind
                        :transport-fn transport-fn
                        :handler handler
                        :ack-port ack-port
                        :greeting-fn greeting-fn)

    (catch Throwable t
      (log/error t "failed to start nREPL")
      (throw t))))

(defn stop [server]
  (nrepl/stop-server server)
  (log/info "nREPL server stopped"))

(mount/defstate ^{:on-reload :noop} repl-server
  :start
  (when (config/config :nrepl-port)
    (let [bind (or (config/config :nrepl-bind) "0.0.0.0")
          port (config/config :nrepl-port)]
      (log/infof "Starting nrepl server: %s:%s" bind port)
      (start {:bind    bind
              :handler (nrepl-handler)
              :port    port})))
  :stop
  (when repl-server
    (stop repl-server)))
