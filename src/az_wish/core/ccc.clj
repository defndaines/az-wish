(ns az-wish.core.ccc
  (:require [net.cgrand.enlive-html :as html]
            [clj-time.format :as fmt]))

;; TODO: Duplicate, worth extracting?
(defn- fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(def ccc-format (fmt/formatter "MMM dd, yyyy"))

(defn- to-date [date]
  (fmt/unparse (fmt/formatters :date) (fmt/parse ccc-format date)))

(defn lowest [id]
  (let [url (str "http://camelcamelcamel.com/product/" id)
        data (html/select (fetch-url url) [:table.product_pane :tr.lowest_price :td])]
    {:price (-> data second :content first)
     :date (-> (nth data 2) :content first to-date)}))
