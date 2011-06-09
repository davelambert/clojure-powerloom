;;; Copyright © 2011 Dave Lambert

(ns powerloom.test.high
  (:use clojure.test)
  (:require [powerloom.high :as hi]))

(hi/initialize)

(deftest test-high-level
  (testing "loading files"
    (is (= nil (hi/load "test/plm/clojure-testing.plm"))))
  (testing "changing modules"
    (is (= "CLOJURE-TESTING" (hi/change-module "CLOJURE-TESTING")))
    (is (= "PL-USER" (hi/change-module "PL-USER")))
    (is (= "CLOJURE-TESTING" (hi/change-module "CLOJURE-TESTING"))))
  (testing "querying"
    (is (= (hi/retrieve '(all (?p ?n) (and (project ?p) (project-name ?p ?n))))
	   '((CLOJURE-POWERLOOM "Clojure PowerLoom")
	     (CLOJURE "Clojure"))))
    (is (= (hi/retrieve '(1 (?p ?pl) (and (project ?p) (project-language ?p ?pl))))
	   '((CLOJURE-POWERLOOM CLOJURE))))
    (is (= (hi/retrieve '(all (?p) (language-project clojure ?p)))
	   '((CLOJURE) (CLOJURE-POWERLOOM)))))
  (testing "asserting propositions"
    (is (= (hi/assert-proposition '(AND (LANGUAGE POWERLOOM)
					(LANGUAGE-NAME POWERLOOM "PowerLoom")))
     	   '((LANGUAGE POWERLOOM) (LANGUAGE-NAME POWERLOOM "PowerLoom"))))
    (is (= (hi/assert-proposition '(PROJECT-LANGUAGE CLOJURE-POWERLOOM POWERLOOM))
	   '((PROJECT-LANGUAGE CLOJURE-POWERLOOM POWERLOOM))))
    (is (= (hi/retrieve '(all (?p ?l) (PROJECT-LANGUAGE ?p ?l)))
	   '((CLOJURE-POWERLOOM POWERLOOM) (CLOJURE-POWERLOOM CLOJURE)
	     (CLOJURE CLOJURE))))))
