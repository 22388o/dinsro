(ns dinsro.components.user)

(defn index-users
  [users]
  [:div
   ;; [list-user-accounts {:id id}]
   [:ul
    (for [user users]
      ^{:key user}
      [:li
       [:p (get user "id") " - " (get user "name")]])]])