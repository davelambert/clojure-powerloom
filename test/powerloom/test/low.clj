;;; Copyright © 2011 Dave Lambert

(ns powerloom.test.low
  (:use clojure.test)
  (:import [edu.isi.powerloom PLI])
  (:require [powerloom [low :as lo] [util :as util]]))

(lo/initialize)

(deftest test-util
  (testing "modules and environments"
    (is (= "PL-USER" (.moduleName (lo/s-change-module "PL-USER"))))
    (is (= "STELLA" (util/with-ambiance [(lo/get-module "STELLA" nil)  nil]
		      (.moduleName (.get edu.isi.stella.Stella/$MODULE$)))))
    (is (= "PL-USER" (.moduleName (lo/get-current-module))))))
