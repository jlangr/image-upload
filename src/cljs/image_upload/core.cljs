(ns image-upload.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))

(defn ^js/XMLHttpRequest upload-file
  "Upload a file with XMLHttpRequest."
  [url ^js/File file
   {:keys [on-load
           on-progress
           post-params]
    :or {on-load identity
         on-progress identity
         post-params {}}}]
  (let [form-data (doto (js/FormData.)
                    (.append "file" file))
        xhr (doto (js/XMLHttpRequest.)
              (.open "POST" url true))]
    (doseq [[k v] post-params]
      (.append form-data (name k) v))
    (set! (.-onload xhr) on-load)
    (set! (.-onprogress (.-upload xhr)) on-progress)
    (doto xhr
      (.send form-data))))

#_(aget e "target" "files" 0)

(def img-id "uploaded-image")
(def preview-src (atom ""))

(defn load-image [file-added-event]
  (let [file (first (array-seq (.. file-added-event -target -files)))
        file-reader (js/FileReader.)]
    (set! (.-onload file-reader)
          (fn [file-load-event]
            (reset! preview-src (-> file-load-event .-target .-result))
            (let [img (.getElementById js/document img-id)]
              (set! (.-onload img)
                    (fn [image-load]
                      (.log js/console "dimensions:" (.-width img) "x" (.-height img))) ))))
    (.readAsDataURL file-reader file)))

;; Rename button text: http://stackoverflow.com/questions/1163667/how-to-rename-html-browse-button-of-an-input-type-file

(defn home-page []
  [:div [:h2 "image-upload"]
   [:input {:type "file" :on-change load-image}]
   [:img {:id img-id :src @preview-src}]])

(defn current-page []
  [:div [(session/get :current-page)]])

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!)
  (accountant/dispatch-current!)
  (mount-root))
