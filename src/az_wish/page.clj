(ns az-wish.page
  "Retrieve a wishlist page and parse the information into normalized data
  structures."
  (:import [org.jsoup Jsoup]
           [org.jsoup.nodes Document]))


(defn az-link
  "Prefix a relative URI to create a fully qualified URI."
  [rel-uri]
  (str "https://www.amazon.com" rel-uri))

(defn list-url
  "Build the wishlist URL given a wishlist ID, such as 2B071NDZWAZPX."
  [id]
  (str (az-link "/gp/registry/wishlist/") id))

(def user-agent "Mozilla/5.0  (Macintosh; Intel Mac OS X 10.14; rv:64.0) Gecko/20100101 Firefox/64.0")

(defn ^Document retrieve-url
  "Hit a URL and return the content as a Jsoup-parsed document."
  ([url]
   (-> url
       (Jsoup/connect)
       (.userAgent user-agent)
       (.get)))
  ([url referrer]
   (-> url
       (Jsoup/connect)
       (.userAgent user-agent)
       (.referrer referrer)
       (.get))))


(defn prefix-unless-nil
  "Only prefix a URL when there's actually a relative URL to prefix."
  [link]
  (when (seq link)
    (str (az-link link) "&ajax=true")))

(defn next-link
  "Get the (String) URL of the next page of a wish list. `nil` if there are no
  further pages."
  [document]
  (-> document
      (.select "a.wl-see-more")
      (.select "a")
      (.attr "href")
      prefix-unless-nil))


(defn pull-items
  "Grab a sequence of elements for each wishlist item from the HTML."
  [html]
  (-> html
      (.select "div[id~=^item_]")))
