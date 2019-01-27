(ns az-wish.core
  (:require [az-wish.item :as item]
            [az-wish.page :as page]
            [clojure.tools.cli :refer [parse-opts]])
  (:gen-class :main true))

(def cli-options
  [["-w" "--wishlist" "ID of the wishlist to grab data and links for."]])


(defn wishlist
  "Given a wishlist ID, retrieve data on all items in the wishlist. Does not
  follow item links. Should only make an HTTPS call for each page of the
  wishlist."
  [id]
  (loop [url (page/list-url id)
         acc []]
    (if (nil? url)
      acc
      (let [document (page/retrieve-url url)
            items (page/pull-items document)]
        (recur (page/next-link document)
               (concat acc (mapv item/pull-details items)))))))


(defn -main [& args]
  (let [{:keys [options arguments]} (parse-opts args cli-options)]
    (cond
      (:wishlist options) (let [id (first arguments)]
                            (print (pr-str (wishlist id)))))))
