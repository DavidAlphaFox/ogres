(ns ogres.app.core
  (:require [ogres.app.const            :refer [PATH]]
            [ogres.app.layout           :refer [layout]]
            [ogres.app.provider.events  :as events]
            [ogres.app.provider.portal  :as portal]
            [ogres.app.provider.release :as release]
            [ogres.app.provider.state   :as state]
            [ogres.app.provider.storage :as storage]
            [ogres.app.provider.window  :as window]
            [ogres.app.render           :refer [error-boundary]]
            [ogres.app.session          :as session]
            [ogres.app.shortcut         :as shortcut]
            [ogres.app.provider.image   :as image]
            [uix.core :refer [defui $ strict-mode use-effect]]
            [uix.dom :refer [create-root render-root]]))

(defui ^:private stylesheet [props]
  (let [{:keys [name]} props]
    (use-effect
     (fn []
       (let [element (js/document.createElement "link")]
         (set! (.-href element) (str PATH "/" name))
         (set! (.-rel element) "stylesheet")
         (.. js/document -head (appendChild element)))) [name])) nil)

(defui ^:private app []
  ($ strict-mode
    ($ stylesheet {:name "reset.css"})
    ($ stylesheet {:name "ogres.app.css"})
    ($ events/provider
      ($ state/provider
        ($ storage/provider
          ($ release/provider
            ($ image/provider
              ($ portal/provider
                ($ :<>
                  ($ storage/handlers)
                  ($ window/provider)
                  ($ shortcut/handlers)
                  ($ session/handlers)
                  ($ error-boundary
                    ($ layout)))))))))))

(defn ^:export main []
  (let [elem (.querySelector js/document "#root")
        root (create-root elem)]
    (render-root ($ app) root)))
