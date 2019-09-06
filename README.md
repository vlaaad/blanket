# Blanket [![Clojars project](https://img.shields.io/clojars/v/com.github.vlaaad/blanket.svg)](https://clojars.org/com.github.vlaaad/blanket)

*Soft* encapsulation for Clojure data structures

# Rationale

There is value in hiding data even when it's immutable: drawing a line between
public API and implementation details. You might want to create a library (or 
module in an application) that allows it's users to create some entity and then 
pass it back to your library. You want to be able to change this library in the 
future without breaking your users' code, so you write in your documentation 
that shape of your entity is internal and subject to change, hoping that people 
will read and remember it... And this is where Blanket steps in: it shows users 
of your library — at the REPL — that part of what you give them is 
implementation details.

Blanket is extremely lightweight and non-intrusive: it changes covered objects' 
metadata to only affect printing, so your covered hash-map is still the same 
hash-map, and your covered vector is still the same vector, with all equality 
semantics etc. retained. It does not really prevent any access, just helps your 
users to get better understanding of your library's API contracts (hence *soft* 
encapsulation).

# Install

Using git dependency:
```edn
com.github.vlaaad/blanket {:git/url "https://github.com/vlaaad/blanket"
                           :sha "3f54f3cfec43f9de67cda56cc2ec1d88d7936951"}
```

Using maven:
```edn
com.github.vlaaad/blanket {:mvn/verion "1.1.0"}
```

# Usage
```clojure
(ns service
  (:require [blanket.core :as blanket]))

;; mark data structure as namespace-local, optionally set how it's printed
(def async-config
  (blanket/cover {:async true} :print-method (blanket/print-as 'async-config)))

;; in same namespace, you'll still see it
(prn async-config) 
=> {:async true}
;; in other namespace, you wont see it
(in-ns 'service-user)
(clojure.core/prn service/async-config) 
=> #impl[service async-config]
(in-ns 'service)

(defn make [opts]
  ;; Since `make` can be called from other namespace, we should specify 
  ;; encapsulation namespace directly (*ns* will be different)
  (blanket/cover {:opts opts} :ns 'service))

(in-ns 'service-user)
(clojure.core/prn (service/make {:some :opts})) ;; => #impl[service 0x7c6442c2]
(in-ns 'service)

;; here goes remaining API...
(defn perform-request [service config] ,,,)
```
