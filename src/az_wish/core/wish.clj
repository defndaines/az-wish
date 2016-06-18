;;; Extracts data from Amazon wishlist pages.
(ns az-wish.core.wish
  (:require [net.cgrand.enlive-html :as html]
            [clojure.spec :as s]))

(defn ^:private az-link [rel-uri]
  (str "https://www.amazon.com" rel-uri))

(defn ^:private list-url
  "Get the wishlist URL given a wishlist ID, such as 2B071NDZWAZPX."
  [id]
  (str (az-link "/gp/registry/wishlist/") id))

(defn ^:private fetch-url
  "Get the (string) URL as an enlive HTML resource (lazy)."
  [url]
  (html/html-resource (java.net.URL. url)))

(defn ^:private item-nodes
  "Scrapes the item sub-nodes from a wishlist HTML resource."
  [dom]
  (html/select dom [(html/attr-contains :id "item_")]))

(defn ^:private item-title [item]
  (let [title (first (html/select item [:h5 :a]))]
    (clojure.string/trim (html/text title))))

(defn ^:private item-link [item]
  (let [title (first (html/select item [:h5 :a]))]
    (str (az-link (re-find #"^[^?]*" (:href (:attrs title)))))))

(defn ^:private item-price [item]
  (if-let [price-section (first (html/select item [:div.price-section :span]))]
    (clojure.string/trim (html/text price-section))))

(defn ^:private item-availability [item]
  (if-let [avail (html/text (first (html/select item [:span.itemAvailMessage])))]
    avail))

; Do I want to convert the availability into a contant? How best to track limited quantity?
;;     (cond
;;      (= "In Stock." avail) :in-stock
;;      (= "Currently unavailable." avail) :unavailable
;;      (re-matches #".*will be released.*" avail) :pre-order
;;      (re-matches #"^Only .*" avail) :limited-supply
;;      :else avail)))

(defn ^:private item-comment [item]
  (html/text (first (html/select item [:span.g-comment-quote]))))

(defn ^:private item-hash [link]
  {:title (item-title link)
   :link (item-link link)
   :price (item-price link)
   :availability (item-availability link)
   :comment (item-comment link)})

(defn ^:private next-link
  "Get the (string) URL of the next page of a wish list. nil if there are no further pages."
  [dom]
  ; Single page wishlists will not have the element at all.
  (if-let [a-last (first (html/select dom [:#wishlistPagination :li.a-last]))]
    ; The last page will not have a link, just text.
    (if-let [nxt (-> a-last :content first :attrs)]
      (az-link (:href nxt)))))

(defn ^:private ->items
  "Converts extracted link nodes into item hash-maps."
  [links]
  (map item-hash links))

(defn wishlist
  "Given a wishlist ID, retrieve data on all items in the wishlist.
  Does not follow item links. Should only make an HTTPS call for each page of
  the wishlist."
  [id]
  (loop [url (list-url id)
         coll '()]
    (if (nil? url)
      coll
      (let [resource (fetch-url url)
            links (item-nodes resource)]
        (recur (next-link resource)
               (concat coll (->items links)))))))
