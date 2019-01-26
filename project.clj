(defproject az-wish "0.1.0-SNAPSHOT"
  :description "Utility for grabbing an amazon.com wishlist."
  :url "https://github.com/defndaines/az-wish"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/tools.cli "0.4.1"]
                 [org.jsoup/jsoup "1.11.3"]
                 [enlive "1.1.6"]
                 [clj-time "0.15.1"]]
  :profiles {:test {:resource-paths ["test-resources"]}}
  :main az-wish.core
  :aot [az-wish.core])
