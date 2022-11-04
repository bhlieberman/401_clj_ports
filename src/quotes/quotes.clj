(ns quotes.quotes
  (:require [clojure.test :refer [deftest is run-test]])
  (:import [java.net.http HttpClient HttpRequest
            HttpResponse$BodyHandlers]
           [java.net URI]))

(def sys-data {:url "https://ron-swanson-quotes.herokuapp.com/v2/quotes"})

(defn- build-req [url]
  (let [uri (URI. url)
        req (HttpRequest/newBuilder uri)]
    (.build req)))

(defn send-req [req]
  (let [client (HttpClient/newHttpClient)
        handler (HttpResponse$BodyHandlers/ofString)]
    (.send client req handler)))

(defn read-resp [resp]
  (.body resp))

(deftest read-response
  (as-> (build-req (:url sys-data)) req
    (->> (send-req req)
         read-resp
         (is string?))))

(deftest send-request
  (is (= 200 (.statusCode (send-req (build-req (:url sys-data)))))))

(deftest build-request
  (is (= 2 (count (build-req (:url sys-data))))))

(run-test read-response)
