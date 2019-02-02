# az-wish

A Clojure library designed to grab [amazon.com](https://www.amazon.com/)
wishlists.


## Usage

*Work in Progress*

You can run the code from a command line with `lein` installed:
```shell
lein run -w 2B071NDZWAZPX > wishlist-$(date +%Y-%m-%d).edn
```

It will iterate through all pages of the wishlist to grab all items,
normalizing them into a data structure something like this:

```clojure
({:name "Preacher 2016 Season 03"
  :id "B07FDKWTXR"
  :price 29.99
  :used-price 20.84
  :format "Blu-ray"
  :state :available}
 {:name "Dungeons and Dragons Art and Arcana: A Visual History"
  :id "0399580948"
  :price 34.00
  :used-price 26.97
  :format "Hardcover"
  :state :available}
```

## Future Work

I plan to plug this into a process to periodically check for price changes so
that I can notify when an item drops below a particular threshold. That
threshold may be a percentage or perhaps compared to a best-price determined
from historical data. This'll need to be stored in a database to track prices
over time.

I'll also need to adjust for purchases, so that items that disappear from the
list are no longer tracked. But, I've noticed a few items go missing from my
list over the years, so this could also highlight when an item has become
permanently unavailable.

Another feature is to watch for items that have been unavailable suddenly
coming back onto the market.


## License

Copyright © 2014-2019 Michael S. Daines

Distributed under the Eclipse Public License either version 1.0 or (at your
option) any later version.
