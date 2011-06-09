;;; Copyright © 2011 Dave Lambert

(ns powerloom.low
  "Low-level interface to Java PowerLoom."

  (:refer-clojure :exclude [load])
  (:use powerloom.util)
  (:import [edu.isi.powerloom PLI]
	   [edu.isi.powerloom.logic Logic]))

(declare s-change-module)

(defn get-current-module []
  (s-change-module nil))

(defn get-module
  ([name] (get-module name nil))
  ([name environment] (PLI/getModule name environment)))

(defn initialize []
  (PLI/initialize)
  (s-change-module "PL-USER")
  nil)

(defn load [filename]
  (PLI/load filename nil))

;;; In Common Lisp, retrieve returns a query iterator.  This is more
;;; informative than the PlIterator returned from PLI/retrieve.  So
;;; our retrieve returns the query iterator from Logic/retrieve.
(defn retrieve
  ([query] (retrieve query nil nil))
  ([query module] (retrieve query module nil))
  ([query module environment] (with-ambiance [module environment]
				(Logic/retrieve query))))

(defn s-assert-proposition [proposition]
  (PLI/sAssertProposition proposition nil nil))

(defn s-change-module [modulename]
  (PLI/sChangeModule modulename nil))
