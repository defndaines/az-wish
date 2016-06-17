(ns az-wish.core.item
  (:require [net.cgrand.enlive-html :as html]))

(defn ^:private item-url [id]
  (str "http://www.amazon.com/dp/" id "/"))

(defn ^:private fetch-url [url]
  (html/html-resource (java.net.URL. url)))

;; 193398838X ... http://www.amazon.com/dp/193398838X/

(def dom (fetch-url (item-url "193398838X")))

;; The price when currently being sold by Amazon.
(-> (html/select dom [:span.offer-price]) first :content first)
;; Sold by different party.
(-> (html/select dom [:span#priceblock_ourprice]) first :content first)

;; Availability:
(-> (html/select dom [:div#availability :span]) first :content first clojure.string/trim)
;; "In Stock."
;; "This title will be released on MMMM dd, yyyy."
;; "Only 2 left in stock."
;; "Temporarily out of stock."
