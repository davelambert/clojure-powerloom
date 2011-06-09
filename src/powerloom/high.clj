;;; Copyright © 2011 Dave Lambert

(ns powerloom.high
  "High level, Clojure-ish interface to PowerLoom.

Where the low-level interface uses the native Java structures from
STELLA and PowerLoom, this interface presents more Clojure-ish data
structures like hashmaps and lazy sequences."

  (:refer-clojure :exclude [load])
  (:use [powerloom.util])
  (:import [edu.isi.powerloom PLI])
  (:require [powerloom.low :as low]))

(defn assert-proposition [proposition]
  (from-pl-iterator
   (low/s-assert-proposition (str proposition))))

(defn change-module [modulename]
  (.moduleName (low/s-change-module modulename)))

(defn initialize []
  (low/initialize))

(defn load [filename]
  (low/load filename))

(defn retrieve [query]
  (from-pl-iterator
   (PLI/sRetrieve (apply str (interpose " " (map str query))) nil nil)))
