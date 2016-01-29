(ns image-upload.prod
  (:require [image-upload.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
