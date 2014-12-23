(ns az-wish.core.wish
  (:require [net.cgrand.enlive-html :as html]))

(defn- az-link [rel-uri]
  (str "http://www.amazon.com" rel-uri))

(defn- list-url [id]
  (str (az-link "/gp/registry/wishlist/") id))

(defn- fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn- items [dom]
  (html/select dom [(html/attr-contains :id "item_")]))

(defn- item-title [item]
  (let [title (first (html/select item [:h5 :a]))]
    (clojure.string/trim (html/text title))))

(defn- item-link [item]
  (let [title (first (html/select item [:h5 :a]))]
    (str (az-link (re-find #"^[^?]*" (:href (:attrs title)))))))

(defn- item-price [item]
  (if-let [price-section (first (html/select item [:div.price-section :span]))]
    (clojure.string/trim (html/text price-section))))

(defn- item-availability [item]
  (if-let [avail (html/text (first (html/select item [:span.itemAvailMessage])))]
    avail))

; Do I want to convert the availability into a contant? How best to track limited quantity?
;;     (cond
;;      (= "In Stock." avail) :in-stock
;;      (= "Currently unavailable." avail) :unavailable
;;      (re-matches #".*will be released.*" avail) :pre-order
;;      (re-matches #"^Only .*" avail) :limited-supply
;;      :else avail)))

(defn- item-comment [item]
  (html/text (first (html/select item [:span.g-comment-quote]))))

(defn- item-hash [link]
  {:title (item-title link)
   :link (item-link link)
   :price (item-price link)
   :availability (item-availability link)
   :comment (item-comment link)})

(defn- next-link [dom]
  ; Single page wishlists will not have the element at all.
  (if-let [a-last (first (html/select dom [:#wishlistPagination :li.a-last]))]
    ; The last page will not have a link, just text.
    (if-let [nxt (-> a-last :content first :attrs)]
      (az-link (:href nxt)))))

(defn- get-links [links]
  (map item-hash links))

(defn wishlist [id]
  (loop [url (list-url id)
         coll '()]
    (if (nil? url)
      coll
      (let [resource (fetch-url url)
            links (items resource)]
        (recur (next-link resource)
               (concat coll (get-links links)))))))
