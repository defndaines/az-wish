(ns az-wish.item-test
  "Tests against the az-wish.item namespace."
  (:require [clojure.test :refer [deftest testing is]]
            [clojure.java.io :as io]
            [az-wish.item :as item])
  (:import [org.jsoup Jsoup]))


;; Testing data

(def wishlist
  (-> "twenty-three-items.html"
      io/resource
      io/file
      (Jsoup/parse "UTF-8" "https://www.amazon.com/")
      (.select "div[id~=^item_]")))

(def test-item (first wishlist))

(def pre-order-item (nth wishlist 2))

(def blu-ray-item (nth wishlist 3))

(def not-in-stock-item (nth wishlist 4))


;; Tests

(deftest pull-name-test
  (testing "pulling a name from a wishlist item"
    (is (= "Dungeons and Dragons Art and Arcana: A Visual History"
           (item/pull-name test-item))))

  (testing "no items in the full wishlist come back with an empty name"
    (is (not-any? empty? (map item/pull-name wishlist)))))


(deftest pull-id-test
  (testing "pulling an ID from a wishlist item"
    (is (= "0399580948"
           (item/pull-id test-item))))

  (testing "no items in the full wishlist come back with an empty ID"
    (is (not-any? empty? (map item/pull-id wishlist)))))


(deftest parse-price-test
  (testing "handles empty price by returning nil"
    (is (nil? (item/parse-price ""))))

  (testing "parses USD currency strings to doubles"
    (is (= 14.36 (item/parse-price "14.36")))))


(deftest pull-price-test
  (testing "pulling a price from a wishlist item"
    (is (= 34.00
           (item/pull-price test-item))))

  (testing "not all items in a wishlist will have a current price"
    (is (= [34.00 26.97 17.29 17.99   nil   nil   nil 28.00 32.00 14.95 29.95
            14.95 10.37 19.29  7.89  8.95 13.96 16.75 12.88 21.12 14.36  7.95
            nil]
           (map item/pull-price wishlist)))))


(deftest pull-used-price-test
  (testing "pulling a used price from a wishlist item"
    (is (= 29.42
           (item/pull-used-price test-item))))

  (testing "not all items in a wishlist will have a current price"
    (is (= [29.42 22.49   nil 12.99  9.10 634.59  2.40 17.10 26.60  6.84  8.11
             5.49  9.51  9.98  5.26  3.49   1.99  1.37  6.00  6.92  1.93   nil
             3.00]
           (map item/pull-used-price wishlist)))))


(deftest pull-release-date-test
  (testing "only pre-sale items will have a release date"
    (is (= "" (item/pull-release-date test-item))))

  (testing "report release dates as Strings for now"
    (is (= "February 12, 2019"
           (item/pull-release-date pre-order-item)))))


(deftest pull-state-test
  (testing "items available through Amazon"
    (is (= :available (item/pull-state test-item))))

  (testing "pre-order items have their own state"
    (is (= :pre-order (item/pull-state pre-order-item))))

  (testing "items that are unavailable through Amazon or maybe anywhere are
           categorized as `:other`"
    (is (= :other (item/pull-state not-in-stock-item))))

  (testing "all items parse to a state"
    (is (= #{:available :pre-order :other}
           (set (map item/pull-state wishlist))))))


(deftest pull-by-line-test
  (testing "grabbing the by line when it is available"
    (is (= "Michael Witwer, Kyle Newman"
           (item/pull-by-line test-item))))

  (testing "if undefined, comes back as empty String"
    (is (empty? (item/pull-by-line pre-order-item))))

  (testing "movies may report the stars of the film"
    (is (= "Regina Hall, Andrew Bujalski"
           (item/pull-by-line blu-ray-item)))))


(deftest pull-format-test
  (testing "parsing the format of an item from the by-line"
    (is (= "Hardcover"
           (item/pull-format test-item)))
    (is (= "Blu-ray"
           (item/pull-format blu-ray-item))))

  (testing "not all items will have a format, so will parse to empty strings"
    (is (= {"" 2
            "Blu-ray" 1
            "Hardcover" 3
            "Kindle Edition" 1
            "Mass Market Paperback" 1
            "Paperback" 15}
           (frequencies (map item/pull-format wishlist))))))


(deftest pull-details-test
  (testing "aggregate values from an item into a map of properties"
    (is (= {:id "0399580948"
            :name "Dungeons and Dragons Art and Arcana: A Visual History"
            :price 34.00
            :used-price 29.42
            :format "Hardcover"
            :state :available}
           (item/pull-details test-item)))

    (is (= {:id "B07KZHV8Y1"
            :name "Shoplifters"
            :price 17.29
            :used-price nil
            :format ""
            :state :pre-order}
           (item/pull-details pre-order-item)))

    (is (= {:id "B07GRV89VM"
            :name "Support The Girls"
            :price 17.99
            :used-price 12.99
            :format "Blu-ray"
            :state :available}
           (item/pull-details blu-ray-item)))

    (is (= {:id "1506700489"
            :name "Usagi Yojimbo Volume 30: Thieves and Spies"
            :price nil
            :used-price 9.10
            :format "Paperback"
            :state :other}
           (item/pull-details not-in-stock-item)))))
