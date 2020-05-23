(defproject dinsro "0.1.0-SNAPSHOT"

  :description "FIXME: write description"
  :url "https://github.com/duck1123/dinsro"

  :dependencies [[buddy "2.0.0"]
                 [ch.qos.logback/logback-classic "1.2.3"]
                 [cljsjs/highcharts "7.0.3-0"]
                 [com.cemerick/url "0.1.1"]
                 [com.google.guava/guava "29.0-jre"]
                 [com.smxemail/re-frame-cookie-fx "0.0.2"]
                 [com.smxemail/re-frame-document-fx "0.0.1-SNAPSHOT"]
                 [com.taoensso/tempura "1.2.1"]
                 [com.taoensso/timbre "4.10.0"]
                 [cprop "0.1.17"]
                 [day8.re-frame/http-fx "0.1.6"
                  :exclusions [com.cognitect/transit-cljs]]
                 [devcards "0.2.6"
                  :exclusions [args4j]]
                 [expound "0.8.4"]
                 [fentontravers/transit-websocket-client "0.4.11"
                  :exclusions [args4j
                               cljsjs/highlight]]
                 [http.async.client "1.3.1"]
                 [http-kit "2.4.0-alpha6"]
                 [io.grpc/grpc-api "1.23.0"]
                 [io.grpc/grpc-core "1.23.0"
                  :exclusions [com.google.errorprone/error_prone_annotations io.grpc/grpc-api]]
                 [io.replikativ/datahike "0.2.1"]
                 [kee-frame "0.4.0"
                  :exclusions [args4j
                               cljs-ajax
                               com.google.errorprone/error_prone_annotations
                               instaparse
                               metosin/reitit-core
                               mvxcvi/arrangement
                               org.clojure/core.async]]
                 [luminus-http-kit "0.1.6"]
                 [luminus-transit "0.1.2"
                  :exclusions [com.cognitect/transit-cljs]]
                 [luminus/ring-ttl-session "0.3.3"]
                 [manifold "0.1.8"]
                 [metosin/reitit "0.4.2"
                  :exclusions [borkdude/edamame
                               com.fasterxml.jackson.core/jackson-annotations
                               com.fasterxml.jackson.core/jackson-databind
                               mvxcvi/puget]]
                 [metosin/ring-http-response "0.9.1"
                  :exclusions [joda-time
                              clj-time]]
                 [mvxcvi/whidbey "2.2.0"]
                 [mount "0.1.16"]
                 [mvxcvi/puget "1.2.1"]
                 [nrepl "0.7.0"]
                 [orchestra "2019.02.06-1"]
                 [org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.764" :scope "provided"]
                 [org.clojure/core.rrb-vector "0.1.1"]
                 [org.clojure/test.check "1.0.0"]
                 [org.clojure/tools.cli "1.0.194"]
                 [org.clojure/tools.logging "1.1.0"]
                 [org.lightningj/lightningj "0.9.0-Beta-rc1"  :exclusions [io.grpc/grpc-core]]
                 [org.glassfish/javax.json "1.1.4"]

                 [org.webjars.npm/bulma "0.8.2"]
                 [org.webjars.npm/bulma-calendar "6.0.7"
                  :exclusions [org.webjars.npm/bulma
                               org.webjars.npm/date-and-time
                               org.webjars.npm/date-fns]]
                 [org.webjars.npm/date-and-time "0.6.3"]
                 [org.webjars.npm/date-fns "2.9.0"]
                 [org.webjars.npm/bulma-extensions "6.2.7"
                  :exclusions [org.webjars.npm/bulma]]
                 [org.webjars.npm/material-icons "0.3.1"]
                 [org.webjars/webjars-locator "0.40"
                  :exclusions [org.slf4j/slf4j-api]]
                 [re-frame "0.12.0"]
                 [reframe-utils "0.2.2"
                  :exclusions [args4j
                               com.cognitect/transit-cljs]]
                 [reagent "0.10.0"
                  :exclusions [cljsjs/react
                               cljsjs/react-dom]]
                 [ring-webjars "0.2.0"
                  :exclusions [clj-time
                               joda-time]]
                 [ring/ring-core "1.8.1"]
                 [ring/ring-defaults "0.3.2"
                  :exclusions [clj-time]]
                 [selmer "1.12.24"]
                 [tick "0.4.21-alpha"]
                 [time-specs "0.1.0-SNAPSHOT"]]

  :min-lein-version "2.0.0"

  :source-paths ["src/clj" "src/cljs" "src/cljc"]
  :test-paths ["test/clj"]
  :resource-paths ["resources" "target/cljsbuild"]
  :target-path "target/%s/"
  :main ^:skip-aot dinsro.core

  :plugins [[lein-ancient "0.6.15"]
            [lein-cljsbuild "1.1.5"]
            [jonase/eastwood "0.3.5"]
            [mvxcvi/whidbey "2.2.0"]
            [lein-doo "0.1.11"]
            [lein-kibit "0.1.6"]
            [cider/cider-nrepl "0.25.0-SNAPSHOT"]]

  :clean-targets
  ^{:protect false} [:target-path
                     [:cljsbuild :builds :app :compiler :output-dir]
                     [:cljsbuild :builds :app :compiler :output-to]]

  :figwheel {:http-server-root "public"
             :server-logfile "log/figwheel-logfile.log"
             :nrepl-port 7002
             :css-dirs ["resources/public/css"]
             :nrepl-middleware [cider.piggieback/wrap-cljs-repl
                                cider.nrepl/cider-middleware
                                refactor-nrepl.middleware/wrap-refactor]
             ;; :main dinsro.core
             :ring-handler dinsro.handler/app-routes
             }

  :middleware [whidbey.plugin/repl-pprint]

  :profiles
  {:uberjar {:omit-source true
             :prep-tasks ["compile" ["cljsbuild" "once" "min"]]
             :dependencies [[day8.re-frame/tracing-stubs "0.5.3"]]
             :cljsbuild
             {:builds {:min {:source-paths ["src/cljc"
                                            "src/cljs"
                                            "env/prod/cljs"
                                            "env/prod/cljc"]
                             :compiler
                             {:output-dir    "target/cljsbuild/public/js"
                              :output-to     "target/cljsbuild/public/js/app.js"
                              :source-map    "target/cljsbuild/public/js/app.js.map"
                              :optimizations :advanced
                              :infer-externs true
                              :pretty-print  false
                              :closure-warnings
                              {:externs-validation :off :non-standard-jsdoc :off}
                              :externs       ["react/externs/react.js"]}}}}
             :aot :all
             :uberjar-name "dinsro.jar"
             :source-paths ["env/prod/clj" "env/prod/cljc"]
             :resource-paths ["env/prod/resources"]}

   :dev           [:project/dev :profiles/dev]
   :test          [:project/dev :project/test :profiles/test]
   :devcards      [:project/devcards :test]

   :project/dev {:jvm-opts ["-Dconf=dev-config.edn"
                            "-Dclojure.spec.check-asserts=true"
                            "-Dlogback.configurationFile=resources/logback.xml"]
                 :dependencies [[binaryage/devtools "1.0.0"]
                                [cider/piggieback "0.5.0"]
                                [day8.re-frame/re-frame-10x "0.6.5"]
                                [day8.re-frame/test "0.1.5"]
                                [day8.re-frame/tracing "0.5.5"]
                                [doo "0.1.11"]
                                [figwheel-sidecar "0.5.20" :exclusions [args4j clj-time]]
                                [pjstadig/humane-test-output "0.10.0"]
                                [prone "2020-01-17"]
                                [ring/ring-devel "1.8.1"]
                                [ring/ring-mock "0.4.0"]]

                 :plugins      [[lein-doo "0.1.11"]
                                [lein-figwheel "0.5.20"]
                                [org.clojure/tools.namespace "0.3.0-alpha4"
                                 :exclusions [org.clojure/tools.reader]]
                                [refactor-nrepl "2.4.0"
                                 :exclusions [org.clojure/clojure]]]

                 :cljsbuild
                  {:builds
                   {:app      {:source-paths ["src/cljs"
                                              "src/cljc"
                                              "env/dev/cljs"
                                              "test/cljs"
                                              "test/cljc"]
                               :figwheel     {:on-jsload "dinsro.core/mount-components"}
                               :compiler     {:main            "dinsro.app"
                                              :asset-path      "/js/out"
                                              :output-to       "target/cljsbuild/public/js/app.js"
                                              :output-dir      "target/cljsbuild/public/js/out"
                                              :source-map      true
                                              :optimizations   :none
                                              :closure-defines
                                              {"re_frame.trace.trace_enabled_QMARK_"        true
                                               "day8.re_frame.tracing.trace_enabled_QMARK_" true}
                                              :preloads        [day8.re-frame-10x.preload]
                                              :pretty-print    true}}

                    }}

                 :doo {:build "test"
                       :alias {:default [:chrome]}}
                 :source-paths ["env/dev/clj"]
                 :resource-paths ["env/dev/resources"]
                 :repl-options   {:init-ns user}
                 :injections     [(require 'pjstadig.humane-test-output)
                                  (pjstadig.humane-test-output/activate!)]}
   :project/devcards {:figwheel {:server-port 3450
                                 :nrepl-port 7003}
                      :cljsbuild
                      {:builds
                       {:devcards {:source-paths ["src/cljs" "src/cljc" "env/dev/cljs" "test/cljs" "test/cljc"]
                                   :figwheel     {:devcards true}
                                   :compiler     {:main "starter.doo"
                                                  :asset-path "/js/devcards_out"
                                                  :output-to "target/cljsbuild/public/js/devcards.js"
                                                  :output-dir "target/cljsbuild/public/js/devcards_out"
                                                  :source-map-timestamp true
                                                  :optimizations :none
                                                  ;; :pretty-print true
                                                  }}}}}

   :project/test
   {:jvm-opts ["-Dconf=test-config.edn"]
    :resource-paths ["env/test/resources"]
    :cljsbuild
    {:builds {:test {:source-paths ["src/cljc" "src/cljs" "test/cljs" "test/cljc"]
                     :compiler
                     {:output-to     "target/test.js"
                      :main          "starter.doo"
                      :optimizations :none}}}}}
   :profiles/dev {}
   :profiles/test {}})
