(ns az-wish.core
  (:require [net.cgrand.enlive-html :as html]
            [clojure.tools.cli :refer [parse-opts]])
  (:gen-class :main true))

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
  ; Single page wishlists will not have the element at all.
  (if-let [a-last (first (html/select dom [:#wishlistPagination :li.a-last]))]
    ; The last page will not have a link, just text.
    (if-let [nxt (-> a-last :content first :attrs)]
      (az-link (:href nxt)))))

(defn wishlist [id]
  (letfn [(get-links [links] (map item-pair links))]
    (loop [url (list-url id)
           coll '()]
      (if (nil? url)
        coll
        (let [resource (fetch-url url)
              links (items resource)]
          (recur (next-link resource)
                 (concat coll (get-links links))))))))

(def cli-options
  [["-w" "--wishlist" "ID of the wishlist to grab data and links for."]])

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:wishlist options) (let [id (first arguments)]
                            (print (pr-str (wishlist id)))))))
