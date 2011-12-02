;;; Copyright © 2011 Dave Lambert

(ns powerloom.util
  "Utility funtions used in the PowerLoom interface namespaces.

Not for public consumption."
  (:import [edu.isi.powerloom PLI]
	   [edu.isi.stella.javalib Native StellaIterator]
	   [edu.isi.powerloom.logic Logic QueryIterator]
	   [edu.isi.stella Cons InputStream InputStringStream
	    SExpressionIterator Stella Symbol]))

(defn- from-stella-object [obj]
  (read-string (edu.isi.powerloom.PLI/objectToParsableString obj)))

(defn- seqify-stella-iterator [^StellaIterator iterator]
  (loop [values '()]
    (if (.hasNext iterator)
      (recur (cons (.next iterator) values))
      (reverse values))))

(defn from-pl-iterator [iterator]
  (map (fn [v]
	 (let [n (PLI/getColumnCount v)]
	   (map (fn [column]
		  (from-stella-object (PLI/getNthValue v column nil nil)))
		(range 0 n))))
       (seqify-stella-iterator (StellaIterator. iterator))))

(defmulti stella-parse-sexp
  "Parse a Clojure sexp into a Stella sexp structure."
  (fn [sexp] (class sexp)))

(defmethod stella-parse-sexp clojure.lang.PersistentList [sexp]
  (reduce (fn [a b] (Cons/cons (stella-parse-sexp b) a))
	  Stella/NIL (reverse sexp)))

(defmethod stella-parse-sexp Boolean [sexp]
  (edu.isi.stella.BooleanWrapper/wrapBoolean sexp))

(defmethod stella-parse-sexp Double [sexp]
  (edu.isi.stella.FloatWrapper/wrapFloat sexp))

(defmethod stella-parse-sexp Float [sexp]
  (edu.isi.stella.FloatWrapper/wrapFloat sexp))

(defmethod stella-parse-sexp Integer [sexp]
  (edu.isi.stella.IntegerWrapper/wrapInteger sexp))

(defmethod stella-parse-sexp String [sexp]
  (edu.isi.stella.StringWrapper/wrapString sexp))

(defmethod stella-parse-sexp clojure.lang.Keyword [sexp]
  (Stella/internKeyword (str sexp)))

(defmethod stella-parse-sexp clojure.lang.PersistentList$EmptyList [sexp]
  Stella/NIL)

(defmethod stella-parse-sexp clojure.lang.Symbol [sexp]
  (Symbol/internSymbol (str sexp)))

;;; If there's no explicit translation, then we've got an
;;; untranslatable datatype.
(defmethod stella-parse-sexp Object [sexp]
  (throw (RuntimeException.
	  (format "Cannot convert value %s to STELLA object" sexp))))

(defn print-query-iterator-ornately [query-iterator]
  (let [baos (java.io.ByteArrayOutputStream.)]
    (QueryIterator/printQueryIteratorOrnately
     query-iterator (java.io.PrintStream. baos))
    (str baos)))

;;; Duplicate Common Lisp PowerLoom's presentation of a QueryIterator
(defmethod print-method QueryIterator [query-iterator writer]
  (.write writer (print-query-iterator-ornately query-iterator)))

(defmacro with-ambiance
  "Evaluate body in particular PowerLoom module and context."
  [[module context] & body]
  `(locking Logic/$POWERLOOM_LOCK$
     (let [old-module# (.get Stella/$MODULE$)
	   old-context# (.get Stella/$CONTEXT$)
	   new-module# (or ~module old-module#)
	   new-context# (or ~context old-context#)]
       (try
	 (Native/setSpecial Stella/$MODULE$ new-module#)
	 (Native/setSpecial Stella/$CONTEXT$ new-context#)
	 ~@body
	 (finally
	  (.set Stella/$MODULE$ old-module#)
	  (.set Stella/$CONTEXT$ old-context#))))))
