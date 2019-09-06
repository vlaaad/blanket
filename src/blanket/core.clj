(ns blanket.core
  (:import [clojure.lang IObj]
           [java.io Writer]))

(defn- default-print-method [x ^Writer writer]
  (doto writer
    (.write "#impl-in[")
    (.write (str (::ns (meta x))))
    (.write " ")
    (.write (format "0x%x" (System/identityHashCode x)))
    (.write "]")))

(defmethod print-method ::hidden [x ^Writer writer]
  (let [meta (meta x)
        ns (::ns meta)]
    (if (= *ns* ns)
      (print-method (vary-meta x dissoc :type ::ns) writer)
      ((::print-method meta) x writer))))

(defn cover
  "Hides data structure internals from printing in other namespaces"
  [x & {:keys [print-method ns]
        :or {print-method default-print-method
             ns *ns*}}]
  (if (instance? IObj x)
    (vary-meta x assoc :type ::hidden ::ns (the-ns ns) ::print-method print-method)
    x))

(defn reveal
  "Reveals previously hidden data structure internals"
  [x]
  (if (instance? IObj x)
    (vary-meta x dissoc :type ::ns ::print-method)
    x))
