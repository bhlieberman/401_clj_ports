(ns quotes.quotes
  (:require [clojure.test :refer [deftest is run-test]]
            [clojure.java.io :as io]
            [portal.api :as p])
  (:import [java.net.http HttpClient HttpRequest
            HttpResponse$BodyHandlers]
           [java.net URI]
           [java.io FileNotFoundException]))

(def sys-data {:client (HttpClient/newHttpClient)
               :api-url "https://ron-swanson-quotes.herokuapp.com/v2/quotes"
               :fallback "resources/quotes.json"})

(def result (atom []))

(defn fallback-quote
  "When API calls fail, this function will return a substitute response"
  []
  (with-open [rdr (io/reader "resources/quotes.json")]
    (rand-nth (line-seq rdr))))

(defn- build-req [url]
  (let [uri (URI. url)
        req (HttpRequest/newBuilder uri)]
    (.build req)))

(defn send-req [req]
  (let [handler (HttpResponse$BodyHandlers/ofString)]
    (try (.send (:client sys-data) req handler)
         (catch Exception _
           (fallback-quote)))))

(defn read-resp [resp]
  (.body resp))

(defn on-threads [req]
  (doto (Thread. (fn [] (->> (send-req req)
                             read-resp
                             (swap! result conj))))
    .start))

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

(comment
  (dotimes [_ 5] (-> sys-data :api-url build-req on-threads))
  (tap> @result)
  (reset! result []))
