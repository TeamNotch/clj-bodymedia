clj-bodymedia
=============

Bodymedia API Clojure Wrapper

## Usage
```clj
(ns im.a.happy.namespace
    (:use notch.clj-bodymedia))

;;First add your client id and client secret to bodymedia.properties.clj

;; Send the user to the auth URL
(def request_token (oauth/request-token consumer "http://localhost"))
(get-auth-uri request_token "http://localhost")

;;The above redirects to something like:
;;http://localhost

;;Then complete oauth by getting an access token
(def access_token (get-access-token request_token))

;;List the user's burn
(get-burn-days access_token "20110722" "20110801")
```

## License

Copyright (C) 2012 Notch, Inc

Distributed under the Eclipse Public License, the same as Clojure.
