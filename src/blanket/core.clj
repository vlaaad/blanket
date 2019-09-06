(ns blanket.core
  (:import [clojure.lang IObj]
           [java.io Writer]))

(defn- default-print-method [x ^Writer writer]
  (.write writer (format "0x%x" (System/identityHashCode x))))

(defn print-as [x]
  (fn [_ ^Writer writer]
    (print-method x writer)))

(defmethod print-method ::hidden [x ^Writer writer]
  (let [meta (meta x)
        ns (::ns meta)
        x (vary-meta x dissoc :type ::ns ::print-method)]
    (if (= *ns* ns)
      (print-method x writer)
      (doto writer
        (.write "#impl[")
        (.write (str ns))
        (.write " ")
        (->> ((::print-method meta) x))
        (.write "]")))))

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
