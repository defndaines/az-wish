(ns az-wish.core
  (:require [az-wish.core.wish :as wish]
            [clojure.tools.cli :refer [parse-opts]])
  (:gen-class :main true))

(def cli-options
  [["-w" "--wishlist" "ID of the wishlist to grab data and links for."]])

(defn -main [& args]
  (let [{:keys [options arguments]} (parse-opts args cli-options)]
    (cond
      (:wishlist options) (let [id (first arguments)]
                            (print (pr-str (wish/wishlist id)))))))
