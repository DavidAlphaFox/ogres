(ns ogre.tools.render.command
  (:require [uix.core.alpha :as uix]
            [datascript.core :as ds]
            [ogre.tools.render :refer [context css]]
            [ogre.tools.render.icon :refer [icon]]))

(def modes
  [[:select :cursor 20]
   [:canvas :image 18]
   [:grid :grid 21]
   [:ruler :ruler 20]
   [:circle :circle 20]
   [:rect :rect 20]
   [:cone :cone 20]
   [:line :line 20]])

(defn command [props]
  (let [{:keys [workspace dispatch store]} (uix/context context)
        {show :grid/show current :canvas/mode} workspace]
    [:div.commands
     (for [[mode name size] modes]
       [:button
        {:key mode :type "button" :class (css {:selected (= current mode)})
         :on-click #(dispatch :canvas/toggle-mode mode)}
        [icon {:name name :width size :height size}]])
     [:hr]
     [:button
      {:type "button" :title "Toggle Grid" :class (css {:selected show}) :on-click #(dispatch :grid/toggle)}
      [:div.grid]]
     [:hr]
     [:button
      {:type "button" :title "Zoom In" :on-click #(dispatch :zoom/in)}
      [icon {:name :zoom-in :width 18 :height 18}]]
     [:button
      {:type "button" :title "Zoom Out" :on-click #(dispatch :zoom/out)}
      [icon {:name :zoom-out :width 18 :height 18}]]
     [:hr]
     [:button
      {:type "button" :title "Open Player View" :on-click #(dispatch :share/open)}
      [icon {:name :window :width 18 :height 18}]]
     [:hr]
     [:a {:href "https://www.github.com/samcf/ogre.tools" :title "Project home" :target "_blank"}
      [:button {:type "button"}
       [icon {:name :github :width 20 :height 20}]]]
     [:button
      {:type "button"
       :title "Reset Local Storage"
       :on-click
       (fn []
         (.delete store)
         (.reload (.-location js/window)))}
      [icon {:name :recycle :width 20 :height 20}]]]))
