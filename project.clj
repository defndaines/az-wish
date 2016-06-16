(defproject az-wish "0.1.0-SNAPSHOT"
  :description "Utility for grabbing an amazon.com wishlist."
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha7"]
                 [org.clojure/tools.cli "0.3.5"]
                 [enlive "1.1.6"]
                 [clj-time "0.12.0"]]
  :main az-wish.core
  :aot [az-wish.core])
