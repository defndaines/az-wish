(ns az-wish.core
  (:require [net.cgrand.enlive-html :as html]))

(defn- az-link [rel-uri]
  (str "http://www.amazon.com" rel-uri))

(defn- list-url [id]
  (str (az-link "/gp/registry/wishlist/") id))

(defn- fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn- items [dom]
  (html/select dom [:div.a-fixed-left-grid :h5 :a]))

(defn- item-title [link]
  (clojure.string/trim (html/text link)))

(defn- item-link [link]
  (str (az-link (re-find #"^[^?]*" (:href (:attrs link))))))

(defn- item-pair [link]
  {:link (item-link link) :title (item-title link)})

(defn- next-link [dom]
  ; Written this way to avoid Util.java:221 RuntimeException for Unmatched delimiter: )
  (az-link (((first ((first (html/select dom [:#wishlistPagination :li.a-last])) :content)) :attrs) :href)))

(defn wishlist [id]
  (letfn [(get-links [links] (map item-pair links))]
    (loop [url (list-url id)
           coll '()]
      (if (nil? url)
        coll
        (let [resource (fetch-url url)
              links (items resource)]
          (recur (try (next-link resource) (catch ClassCastException _ nil))
                 (concat coll (get-links links))))))))
