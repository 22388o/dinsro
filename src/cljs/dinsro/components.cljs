(ns dinsro.components
  (:require [ajax.core :as ajax]
            [markdown.core :refer [md->html]]
            [reagent.core :as r]))

(defn about-page []
  [:div.container
   [:div.row
    [:div.col-md-12
     [:img {:src "/img/warning_clojure.png"}]]]])

(defn home-page [docs]
  [:div.container
   (when docs
     [:div.row>div.col-sm-12
      [:div {:dangerouslySetInnerHTML
             {:__html (md->html docs)}}]])])
