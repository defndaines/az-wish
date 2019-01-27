(ns az-wish.page-test
  "Tests against the az-wish.page namespace."
  (:require [clojure.test :refer [deftest testing is]]
            [clojure.java.io :as io]
            [az-wish.page :as page])
  (:import [org.jsoup Jsoup]))


;; Testing data

(def document
  (-> "twenty-three-items.html"
      io/resource
      io/file
      (Jsoup/parse "UTF-8" "https://www.amazon.com/")))

(def last-page
  (-> "last-page.html"
      io/resource
      io/file
      (Jsoup/parse "UTF-8" "https://www.amazon.com/")))


;; Tests

(deftest build-wishlist-url-test
  (testing "able to build a valid URL of a wishlist"
    (is (= "https://www.amazon.com/gp/registry/wishlist/2B071NDZWAZPX"
           (page/list-url "2B071NDZWAZPX")))

    (is (= "https://www.amazon.com/gp/registry/wishlist/R6BS0GASDTZQ"
           (page/list-url "R6BS0GASDTZQ")))))


(deftest next-link-test
  (testing "extraction of the next page of links from a document"
    (is (= "https://www.amazon.com/hz/wishlist/ls/2B071NDZWAZPX?filter=DEFAULT&viewType=list&lek=af7b7cde-af06-4482-a0a5-f9e73fe44ae7&sort=default&type=wishlist&ajax=true"
           (page/next-link document))))

  (testing "last page returns `nil` when there is nothing more to grab"
    (is (nil? (page/next-link last-page)))))


(deftest pull-items
  (testing "each item can be separated out"
    (is (= 23
           (count (page/pull-items document))))

    (is (= 21
          (count (page/pull-items last-page))))))
