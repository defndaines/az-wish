(defproject az-wish "0.1.0-SNAPSHOT"
  :description "Utility for grabbing an amazon.com wishlist."
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0-alpha4"]
                 [org.clojure/tools.cli "0.3.1"]
                 [enlive "1.1.5"]
                 [clj-time "0.8.0"]]
  :main az-wish.core
  :aot [az-wish.core])
