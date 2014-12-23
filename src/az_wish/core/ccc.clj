(ns az-wish.core.ccc
  (:require [net.cgrand.enlive-html :as html]))

(defn- fetch-url [url]
  (html/html-resource (java.net.URL. url)))

;; http://camelcamelcamel.com/product/1941222226 ... redirects to ...
;; http://camelcamelcamel.com/Mastering-Clojure-Macros-Cleaner-Smarter/product/1941222226

(def direct (fetch-url "http://camelcamelcamel.com/product/1941222226"))

(def az-lowest (second (html/select direct [:table.product_pane :tr.lowest_price :td])))
;; And the next td is the date of that price

(def price (-> az-lowest :content first))
