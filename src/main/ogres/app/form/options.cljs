(ns ogres.app.form.options
  (:require [ogres.app.hooks :refer [use-dispatch use-query]]
            [ogres.app.render :refer [icon]]
            [uix.core :refer [defui $]]))

(def ^:private options-lighting
  [[:revealed "Revealed" "eye-fill"]
   [:dimmed   "Obscured" "eye"]
   [:hidden   "Hidden"   "eye-slash-fill"]])

(def ^:private options-weather
  [[:none     "None"     "sun-fill"]
   [:dusk     "Dusk"     "cloud-sun-fill"]
   [:midnight "Midnight" "moon-stars-fill"]])

(def ^:private query
  [{:local/camera
    [[:camera/label :default ""]
     [:camera/draw-mode :default :select]
     {:camera/scene
      [:db/key
       [:scene/dark-mode :default false]
       [:scene/grid-size :default 70]
       [:scene/lighting :default :revealed]
       [:scene/show-grid :default true]
       [:scene/snap-grid :default false]
       [:scene/timeofday :default :none]
       {:scene/image
        [:image/checksum]}]}]}])

(defui form []
  (let [dispatch (use-dispatch)
        result   (use-query query [:db/ident :local])
        {camera :local/camera
         {scene :camera/scene
          {{checksum :image/checksum} :scene/image}
          :camera/scene}
         :local/camera} result]
    ($ :section.options
      ($ :section
        ($ :fieldset
          ($ :input
            {:type "text"
             :placeholder "Name this scene..."
             :maxLength 36
             :spellCheck "false"
             :value (:camera/label camera)
             :on-change
             (fn [event]
               (let [value (.. event -target -value)]
                 (dispatch :camera/change-label value)))})))
      ($ :section.options-grid
        ($ :header "Grid")
        ($ :form
          ($ :fieldset
            ($ :label "Show grid")
            ($ :input
              {:id "show-grid"
               :type "checkbox"
               :checked (:scene/show-grid scene)
               :on-change
               (fn [event]
                 (let [checked (.. event -target -checked)]
                   (dispatch :scene/toggle-show-grid checked)))})
            ($ :label {:for "show-grid"}
              ($ icon {:name "grid-fill" :size 16})))
          ($ :fieldset
            ($ :label "Align to grid")
            ($ :input
              {:id "align-grid"
               :type "checkbox"
               :checked (:scene/snap-grid scene)
               :on-change
               (fn [event]
                 (let [checked (.. event -target -checked)]
                   (dispatch :scene/toggle-snap-grid checked)))})
            ($ :label {:for "align-grid"}
              ($ icon {:name "bounding-box" :size 16})))
          ($ :fieldset
            ($ :label "Use dark grid")
            ($ :input
              {:id "dark-mode"
               :type "checkbox"
               :checked (:scene/dark-mode scene)
               :on-change
               (fn [event]
                 (let [checked (.. event -target -checked)]
                   (dispatch :scene/toggle-dark-mode checked)))})
            ($ :label {:for "dark-mode"}
              ($ icon {:name "lightbulb-fill" :size 16})))
          ($ :fieldset
            ($ :label "Tile size (px)")
            ($ :input
              {:type "number"
               :value (or (:scene/grid-size scene) 0)
               :placeholder "Grid size"
               :disabled (not checksum)
               :on-change #(dispatch :scene/change-grid-size (.. %1 -target -value))}))))
      ($ :section.options-effects
        ($ :header "Effects")
        ($ :form
          ($ :fieldset
            ($ :label "Visibility")
            ($ :div.options-group
              (for [[option label icon-name] options-lighting
                    :let [key (str "lighting-" (name option))]]
                ($ :fieldset {:key key}
                  ($ :input
                    {:id key
                     :type "radio"
                     :checked (= (:scene/lighting scene) option)
                     :disabled (not checksum)
                     :on-change #(dispatch :scene/change-lighting option)})
                  ($ :label {:for key :data-tooltip label}
                    ($ icon {:name icon-name :size 18}))))))
          ($ :fieldset
            ($ :label "Time of Day")
            ($ :div.options-group
              (for [[option label icon-name] options-weather
                    :let [key (str "time-of-day-" (name option))]]
                ($ :fieldset {:key key}
                  ($ :input
                    {:id key
                     :type "radio"
                     :checked (= option (:scene/timeofday scene))
                     :disabled (not checksum)
                     :on-change #(dispatch :scene/change-time-of-day option)})
                  ($ :label {:for key :data-tooltip label}
                    ($ icon {:name icon-name :size 18})))))))))))