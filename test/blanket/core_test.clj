(ns blanket.core-test
  (:require [clojure.test :refer [deftest is]]
            [blanket.core :as blanket]
            [clojure.spec.test.alpha :as stest]
            [clojure.spec.alpha :as s]))

(deftest hides-data-from-printing-in-other-namespaces
  (in-ns 'blanket.core-test.a)
  (let [x @(def x (blanket/cover {:a 1}))]
    (in-ns 'blanket.core-test.b)
    (is (re-matches #"#impl-in\[blanket.core-test.a 0x[0-9a-f]+\]" (pr-str x)))))

(deftest does-not-hide-from-printing-in-same-namespace
  (in-ns 'blanket.core-test.a)
  (let [x @(def x (blanket/cover {:a 1}))]
    (is (= "{:a 1}" (pr-str x)))))

(deftest reveal-shows-hidden-data-structures
  (in-ns 'blanket.core-test.a)
  (let [x @(def x (blanket/cover {:a 1}))]
    (in-ns 'blanket.core-test.b)
    (is (= "{:a 1}" (pr-str (blanket/reveal x))))))

(deftest hide-reveal-cancel-each-other
  (is (= {} (stest/abbrev-result
              (stest/check-fn
                (comp blanket/reveal blanket/cover)
                (s/fspec :args (s/cat :in any?)
                         :fn #(let [ret (:ret %)
                                    in (-> % :args :in)]
                                (and (= ret in)
                                     (= (into {} [(meta in)])
                                        (into {} [(meta ret)]))))))))))
