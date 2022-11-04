(ns quotes.quotes
  (:require [clojure.test :refer [deftest is run-test]]
            [clojure.java.io :as io])
  (:import [java.net.http HttpClient HttpRequest
            HttpResponse$BodyHandlers]
           [java.net URI]
           [java.io FileNotFoundException]))

(def sys-data {:api-url "https://ron-swanson-quotes.herokuapp.com/v2/quotes"
               :fallback "resources/quotes.json"})

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

(defn write-resp [body]
  (with-open [writer (io/writer (:fallback sys-data) :append true)]
    (.write writer body)
    (catch FileNotFoundException)))

(deftest test-write-resp-to-file
  (is (not (instance? FileNotFoundException
                  (as-> (build-req (:api-url sys-data)) req
                    (->> (send-req req)
                         read-resp
                         write-resp))))))

(deftest read-response
  (as-> (build-req (:api-url sys-data)) req
    (->> (send-req req)
         read-resp
         (is string?))))

(deftest send-request
  (is (= 200 (.statusCode (send-req (build-req (:api-url sys-data)))))))

(deftest build-request
  (is (= 2 (count (build-req (:api-url sys-data))))))

(run-test test-write-resp-to-file)
