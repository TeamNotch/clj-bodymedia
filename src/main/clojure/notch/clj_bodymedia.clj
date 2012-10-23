(ns notch.clj-bodymedia
  (:use clojure.set)
  (:use clojure.tools.logging)
  (:require [clojure.data.json :as json])
  (:require [oauth.client :as oauth] :reload)
  (:require [clj-http.client :as http])
  (:require [clojure.string :as str])
  (:require [clojure.java.io :as io]))


(do
  (def properties (-> (clojure.java.io/resource "bodymedia.properties.clj")
                    (clojure.java.io/reader)
                    (java.io.PushbackReader.)
                    (read)))

  (def ^{:dynamic true} *consumer*
    (oauth/make-consumer (:client_id properties)
      (:client_secret properties)
      "https://api.bodymedia.com/oauth/request_token"
      (str "https://api.bodymedia.com/oauth/access_token")
      (str "https://api.bodymedia.com/oauth/authorize?api_key=" (:client_id properties))
      :hmac-sha1 ))

  (def api_uri "http://api.bodymedia.com/v2/json"))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;Authentication Calls
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn get-request-token
  "Get request token for oauth1 start"
  ([callback_url]
    (let [result (oauth/request-token *consumer* callback_url)]
      (when (and (contains? result :oauth_token )
              (contains? result :oauth_token_secret ))
        result
        ))))

(defn get-auth-uri
  "Send the user to this URL for first part of OAuth"
  ([request_token callback_url]
    (let [uri (oauth/user-approval-uri *consumer* (:oauth_token request_token))]
      (str (subs uri 0 (.lastIndexOf uri "?"))
        "&" (subs uri (+ (.lastIndexOf uri "?") 1) )
        "&oauth_callback=" (java.net.URLEncoder/encode callback_url ))
    )))

(defn get-access-token
  "Get the final access token for OAuth.
  Returns the token as a map if successful
  Returns nil otherwise"
  [request_token]
  (let [result (oauth/access-token *consumer* request_token)]
    (when (and (contains? result :oauth_token)
            (contains? result :oauth_token_secret)
            (contains? result :xoauth_token_expiration_time))
      result
      )
    )
  )

(defn access-token-expired?
  "Is the token expiration past the current time?"
  [access_token]
  (< (* 1000 (Long/parseLong (:xoauth_token_expiration_time access_token)))
     (System/currentTimeMillis)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;HTTP Helper Calls
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn http-get [access_token path params]
  (let [uri (str api_uri path)
        ;;Add the api_key param, required by Mashery/BM
        params (merge params {:api_key (:key *consumer*)})
        query_params (merge
                        ;;Oauth Credentials
                        (oauth/credentials *consumer*
                              (:oauth_token access_token)
                              (:oauth_token_secret access_token)
                              :GET
                              uri
                              params)
                        params)]
    (debug "GET: " uri " " query_params)
    (-> (http/get uri {:query-params query_params})
    :body
    json/read-json)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;API Calls
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn get-user-info
  "Get basic user profile information"
  [access_token]
  (http-get access_token "/user/info" {}))

(defn get-user-last-sync
  "Get the last time the user synced their armband"
  [access_token]
  (http-get access_token "/user/sync/last" {}))

(defn get-weight-measurements
  "List the user's weights over time"
  [access_token]
  (-> (http-get access_token "/measurement/WEIGHT" {})
    :weight))

(defn get-personal-records
  "List the user's personal bests"
  [access_token]
  (http-get access_token "/notification/record" {}))

(defn get-current-preferences
  "List the user's current preferences"
  [access_token]
  (-> (http-get access_token "/preference/current" {})
    :preference))

(defn get-burn-days
  "Lists the user's caloric burn, per day, for the date range"
  [access_token start_date stop_date]
  (-> (http-get access_token (str "/burn/day/intensity/" start_date "/" stop_date ) {})
    :days))

(defn get-sleep-days
  "Lists the user's sleep, per day, for the date range"
  [access_token start_date stop_date]
  (-> (http-get access_token (str "/sleep/day/" start_date "/" stop_date ) {})
    :days))

(defn get-step-days
  "Lists the user's steps, per day, for the date range"
  [access_token start_date stop_date]
  (-> (http-get access_token (str "/step/day/" start_date "/" stop_date ) {})
    :days))

(defn get-summaries
  "Lists the user's summaries for the date range"
  [access_token start_date stop_date]
  (-> (http-get access_token (str "/summary/day/" start_date "/" stop_date ) {})
    identity))
