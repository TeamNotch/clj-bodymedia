(ns notch.test-clj-bodymedia
  (:use clojure.set)
  (:use clojure.tools.logging)
  (:require [clojure.data.json :as json])
  (:require [clj-http.client :as http])
  (:require [clojure.java.io :as io])
  (:use clojure.test)
  (:use notch.clj-bodymedia :reload)

  )

(def access_token (:test_access_token properties))

(def access_token_year_2000 {:xoauth_token_expiration_time "946713600"} )
(def access_token_year_3000 {:xoauth_token_expiration_time "32503708800"} )



;(try
;  (def request_token (get-request-token "http://localhost"))
;  (catch Exception e (error e)))
;
;(get-auth-uri request_token "http://localhost")
;
;(try
;  (def access_token (get-access-token request_token))
;  (catch Exception e (error e)))
;
;(get-user-info access_token )

(deftest test-basics

  (is (true? (access-token-expired? access_token_year_2000 )))
  (is (false? (access-token-expired? access_token_year_3000 )))

  ;;Test some basic calls
  (is (= "20110722" (:registrationDate (get-user-info access_token))))
  (is (= [{:measure "POUND", :value "200.0", :startDate "20110722", :endDate "20110801"} {:measure "POUND", :value "198.0", :startDate "20110801", :endDate "20110817"} {:measure "POUND", :value "195.0", :startDate "20110817", :endDate "20110912"} {:measure "POUND", :value "192.0", :startDate "20110912", :endDate "20110914"} {:measure "POUND", :value "195.0", :startDate "20110914"}]
        (get-weight-measurements access_token)))
  (is (= [{:date "20110722", :totalCalories 3780, :intensity {:averageMets 1.74, :moderate {:totalCalories 469, :totalMinutes 72}, :vigorous {:totalCalories 1025, :totalMinutes 80}, :sedentary {:totalCalories 2286, :totalMinutes 1288}}, :estimatedCalories 0, :predictedCalories 0} {:date "20110723", :totalCalories 3722, :intensity {:averageMets 1.71, :moderate {:totalCalories 1287, :totalMinutes 192}, :vigorous {:totalCalories 169, :totalMinutes 17}, :sedentary {:totalCalories 2266, :totalMinutes 1231}}, :estimatedCalories 0, :predictedCalories 0} {:date "20110724", :totalCalories 4770, :intensity {:averageMets 2.19, :moderate {:totalCalories 1838, :totalMinutes 268}, :vigorous {:totalCalories 996, :totalMinutes 96}, :sedentary {:totalCalories 1936, :totalMinutes 1076}}, :estimatedCalories 0, :predictedCalories 0} {:date "20110725", :totalCalories 4340, :intensity {:averageMets 1.99, :moderate {:totalCalories 867, :totalMinutes 132}, :vigorous {:totalCalories 1196, :totalMinutes 90}, :sedentary {:totalCalories 2277, :totalMinutes 1218}}, :estimatedCalories 0, :predictedCalories 0} {:date "20110726", :totalCalories 3293, :intensity {:averageMets 1.51, :moderate {:totalCalories 853, :totalMinutes 130}, :vigorous {:totalCalories 48, :totalMinutes 5}, :sedentary {:totalCalories 2392, :totalMinutes 1305}}, :estimatedCalories 0, :predictedCalories 0} {:date "20110727", :totalCalories 4408, :intensity {:averageMets 2.02, :moderate {:totalCalories 817, :totalMinutes 123}, :vigorous {:totalCalories 1366, :totalMinutes 101}, :sedentary {:totalCalories 2225, :totalMinutes 1216}}, :estimatedCalories 0, :predictedCalories 0} {:date "20110728", :totalCalories 4353, :intensity {:averageMets 2.0, :moderate {:totalCalories 977, :totalMinutes 151}, :vigorous {:totalCalories 1082, :totalMinutes 84}, :sedentary {:totalCalories 2294, :totalMinutes 1205}}, :estimatedCalories 0, :predictedCalories 0} {:date "20110729", :totalCalories 3977, :intensity {:averageMets 1.83, :moderate {:totalCalories 598, :totalMinutes 91}, :vigorous {:totalCalories 1053, :totalMinutes 77}, :sedentary {:totalCalories 2326, :totalMinutes 1272}}, :estimatedCalories 0, :predictedCalories 0} {:date "20110730", :totalCalories 3887, :intensity {:averageMets 1.79, :moderate {:totalCalories 1234, :totalMinutes 180}, :vigorous {:totalCalories 525, :totalMinutes 54}, :sedentary {:totalCalories 2128, :totalMinutes 1206}}, :estimatedCalories 0, :predictedCalories 0} {:date "20110731", :totalCalories 4108, :intensity {:averageMets 1.89, :moderate {:totalCalories 1680, :totalMinutes 241}, :vigorous {:totalCalories 297, :totalMinutes 30}, :sedentary {:totalCalories 2131, :totalMinutes 1169}}, :estimatedCalories 0, :predictedCalories 0} {:date "20110801", :totalCalories 3632, :intensity {:averageMets 1.68, :moderate {:totalCalories 1077, :totalMinutes 165}, :vigorous {:totalCalories 213, :totalMinutes 16}, :sedentary {:totalCalories 2342, :totalMinutes 1259}}, :estimatedCalories 0, :predictedCalories 0}]
        (get-burn-days access_token "20110722" "20110801")))

  )

;(run-tests 'notch.test-clj-bodymedia)
