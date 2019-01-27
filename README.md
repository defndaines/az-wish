# az-wish

A Clojure library designed to grab [amazon.com](https://www.amazon.com/)
wishlists.


## Usage

Currently a work in progress. You can run the code in a REPL to grab the
sequence.

```shell
lein run -w 2B071NDZWAZPX > wishlist.edn
```

It will iterate through all pages of the wishlist to grab all items, with
titles and links.


## License

Copyright © 2014-2019 Michael S. Daines

Distributed under the Eclipse Public License either version 1.0 or (at your
option) any later version.
