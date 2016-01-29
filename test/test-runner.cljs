(ns test-runner
  (:require [cljs.test :refer-macros [run-all-tests]]) )

(enable-console-print!)

(defn ^:export run []
  (run-all-tests #"image-upload.*-test"))
