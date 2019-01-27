(ns az-wish.item
  "Extract information from a DOM element for a specific item. All functions
  in this namespace assume that an entire 'item' `org.jsoup.nodes.Element` is
  passed in.

  At the time of writing, items can be pulled from a wishlist with the
  following selector:

  `div[id~=^item_]`"
  (:require [clojure.string :as string])
  (:import [org.jsoup Jsoup]))


(defn pull-name
  "Grab the name of an item."
  [item]
  (.text (.select item "h3")))

(defn pull-id
  "Grab the unique identifier for an item. This can be used to navigate to the
  page for an item, or to look up historical information on sites like
  camelcamelcamel."
  [item]
  (-> item
      (.select "h3")
      (.select "a")
      first
      (.attr "href")
      (string/replace #"^/dp/([^/]*)\/.*" "$1")))

(defn parse-price
  "Convert a String indicating the price of an item into a `Double`. Returns
  `nil` if the value cannot be parsed."
  [price]
  (try
    (Double/parseDouble price)
    (catch NumberFormatException _ nil)))

(defn pull-price
  "Grab the current Amazon price. This does not include any prices offered by
  unaffiliated third-party sellers."
  [item]
  (-> item
      (.select "span[id~=^itemPrice_]")
      (.select "span.a-offscreen")
      (.text)
      (string/replace-first "$" "")
      parse-price))

(defn pull-used-price
  "Grab the 'Used & New' price, which is usually the cheapest price (not
  including shipping) available through third-party sellers."
  [item]
  (-> item
      (.select "span.itemUsedAndNewPrice")
      (.text)
      (string/replace "USD " "")
      (string/replace-first "$" "")
      parse-price))

(defn pull-release-date
  "Grab the release date of items that are not yet available."
  [item]
  (-> item
      (.select "span[id~=^availability]")
      (.text)
      (string/replace #".*released on (.*)\." "$1")))

(defn pull-state
  "Grab a 'state' enumeration which aligns with whether an item is available
  through Amazon, third parties, or perhaps not available at all at the
  moment."
  [item]
  (case (.text (.select item "span.a-button-inner"))
    "Add to Cart" :available
    "Pre-Order" :pre-order
    "See all buying options" :other
    :unknown))

(defn pull-by-line
  "Grab the author or actor information on items where it is available."
  [item]
  (-> item
      (.select "span[id~=^item-byline-]")
      (.text)
      (string/replace #"(?:by|Starring) (.*) \(.*" "$1")))

(defn pull-format
  "Grab the format of the item, which is a string buried in the by-line that
  distinguishes different formats an item may be available in."
  [item]
  (-> item
      (.select "span[id~=^item-byline-]")
      (.text)
      (string/replace #".*\((.*)\)" "$1")))

(defn pull-details
  "Grab the details of an item and normalize them to a map that can be used
  for comparisons and tracking."
  [item]
  {:name (pull-name item)
   :id (pull-id item)
   :price (pull-price item)
   :used-price (pull-used-price item)
   :format (pull-format item)
   :state (pull-state item)})
