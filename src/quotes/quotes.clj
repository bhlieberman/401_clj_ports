(ns quotes.quotes
  (:require [clojure.test :refer [deftest is run-test]]
            [clojure.java.io :as io]
            [portal.api :as p])
  (:import [java.net.http HttpClient HttpRequest
            HttpResponse$BodyHandlers]
           [java.net URI]
           [java.io FileNotFoundException]))

(def p (p/open {:launcher :vs-code}))
(add-tap #'p/submit)

(def sys-data {:api-url "https://ron-swanson-quotes.herokuapp.com/v2/quotes"
               :fallback "resources/quotes.json"})

(defn fallback-quote []
  (with-open [rdr (io/reader "resources/quotes.json")]
    (rand-nth (line-seq rdr))))

(defn- build-req [url]
  (let [uri (URI. url)
        req (HttpRequest/newBuilder uri)]
    (.build req)))

(defn send-req [req]
  (let [client (HttpClient/newHttpClient)
        handler (HttpResponse$BodyHandlers/ofString)]
    (try (.send client req handler)
         (catch Exception _
           (fallback-quote)))))

(defn read-resp [resp]
  (.body resp))

(defn write-resp [body & {:keys [append]}]
  (with-open [writer (io/writer (:fallback sys-data) :append append)]
    (if append
      (.write writer (str ",\n" body))
      (.write writer body))))

(deftest test-write-resp-to-file
  (is (not (instance? FileNotFoundException
                      (as-> (build-req (:api-url sys-data)) req
                        (-> (send-req req)
                            read-resp
                            (write-resp {:append true})))))))

(deftest read-response
  (as-> (build-req (:api-url sys-data)) req
    (->> (send-req req)
         read-resp
         (is string?))))

(deftest send-request
  (is (= 200 (.statusCode (send-req (build-req (:api-url sys-data)))))))

(run-test test-write-resp-to-file)
