(ns dinsro.views.index-categories
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.create-category :as u.f.create-category]
   [dinsro.ui.index-categories :as u.index-categories]
   [taoensso.timbre :as timbre]))

(defsc IndexCategoriesPage
  [_this {:keys [button-data categories form-data]}]
  {:query [{:button-data (comp/get-query u.buttons/ShowFormButton)}
           {:form-data (comp/get-query u.f.create-category/CreateCategoryForm)}
           {:categories (comp/get-query u.index-categories/IndexCategories)}]
   :ident (fn [] [:page/id ::page])
   :initial-state {:button-data {}
                   :form-data {}
                   :categories {}}
   :route-segment ["categories"]}
  (dom/section
   :.section
   (dom/div
    :.container
    (dom/div
     :.content
     (dom/div
      :.box
      (dom/h1
       (tr [:index-categories "Index Categories"])
       (u.buttons/ui-show-form-button button-data))
      (u.f.create-category/ui-create-category-form form-data)
      (dom/hr)
      (u.index-categories/ui-index-categories categories))))))

(def ui-page (comp/factory IndexCategoriesPage))
