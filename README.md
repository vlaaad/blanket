# Blanket

*Soft* encapsulation for Clojure data structures

# Rationale

There is value in hiding data even when it's immutable: drawing a line between
public api and implementation details. You might want to create a library that
allows it's users to create some entity and then pass it back to your library. 
You want to be able to change this library in the future without breaking your 
users' code, so you write in your documentation that shape of your entity is 
internal and subject to change. Blanket has the same purpose: it shows users of 
your library — at the REPL — that part of what you give them is implementation 
details.

Blanket is extremely lightweight and non-intrusive: it changes covered objects' 
metadata to only affect printing, so your covered hash-map is still same map, 
and your covered vector is still same vector, with all equality semantics etc.
retained. It does not really prevent any access, just helps your users to get 
better understanding of your library's contract (hence *soft* encapsulation).

# Install

Using git dependency:
```edn
com.github.vlaaad/blanket {:git/url "https://github.com/vlaaad/blanket"
                         :sha "b766c610dac0ac40fc71864f5535a392757f1d3c"}
```

Using maven:
```edn
com.github.vlaaad/blanket {:mvn/verion "1.0.0"}
```

# Usage
```clojure
(ns service
  (:require [blanket.core :as blanket]))

;; mark data structure as namespace-local
(def async-config
  (blanket/cover {:async true}))

;; in same namespace, you'll still see it
(prn async-config) ;; => {:async true}

(def sync-config
  (blanket/cover {:async false}))

(defn make [opts]
  ;; Since `make` can be called from other namespace, we should specify 
  ;; encapsulation namespace directly (*ns* will be different)
  (blanket/cover {:opts opts} :ns 'service))

(defn perform-request [service config] ,,,)

(ns service-user
  (:require service))

;; create a service without seeing implementation details
(prn (service/make {:some :opts})) ;; => #impl-in[service 0x7c6442c2]
(prn service/async-config) ;; => #impl-in[service 0x6de0f580]

(service/perform-request (service/make {:some :opts}) service/async-config)
```
