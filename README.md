grails-geoip [![Build Status](https://api.travis-ci.org/donbeave/grails-geoip.png?branch=master)](https://travis-ci.org/donbeave/grails-geoip)
============

Grails GeoIP Plugin (based on [MaxMind][maxmind] library)

Summary
-------

This plugin facilitates grails integration with the opensource GeoIP framework offered by [MaxMind][maxmind].
Using its straightforward API one can find out the country, area, city, geographical coordinates and 
others based on an IP.

Installation
------------

Add the following plugin definition to your BuildConfig:
```
…
plugins {
…
	compile ':geoip:0.3.2'
…
}
```

Then run script to download and install free GeoLite database:
```
grails install-geo-data
```

Usage
-----

The plugin adds a dynamic method to your controllers to determine location of client is accessing the app:

**isInCountry(String countryCode)** (where `countryCode` is [ISO 3166 Country Code][country-codes]) will be true if the client use IP from this country.

```groovy
def list() {
	...
	if (isInCountry('US')) {
		render(view: 'usa')
	} else {
		render(view: 'default')
	}
}
```

Additionally, you can run code conditionally, with access to the current `com.maxmind.geoip.Location` instance, with this method:

withLocation(Closure closure)
```groovy
def list() {
	...
	def view = 'list'
	withLocation { location ->
		view = "list_${location.countryCode}"
	}
	render(view: view, model: [list: listInstance])
}
```

Copyright and license
---------------------

Copyright 2009-2013 Radu Andrei Tanasa, Alexey Zhokhov under the [GNU Lesser General Public License, version 2.1](LICENSE). Supported by [Polusharie][polusharie].

This product includes GeoLite data created by MaxMind, available from
[www.maxmind.com][maxmind].

[maxmind]: http://www.maxmind.com
[country-codes]: http://dev.maxmind.com/geoip/legacy/codes/iso3166/
[polusharie]: http://www.polusharie.com
