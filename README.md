# commons-view ![build status](https://shredzone.org/badge/commons-view.svg) ![maven central](https://maven-badges.herokuapp.com/maven-central/org.shredzone.commons/commons-view/badge.svg)

_commons-view_ is a micro framework for parsing URLs, finding the appropriate view handlers, and for creating links to views.

Basically it is comparable to Spring's MVC, but allows some more features like URL wildcards and link generation.

This software is part of the Shredzone Commons.

## Features

* Map views to URLs just by using annotations
* Use placeholders within URLs
* Pass in URL placeholders and request parameters as method parameters, type conversion included
* Add Spring converters and e.g. pass in an entity just by its ID
* Generate a link just by passing in its parameter values
* Requires Java 8 (up to v1.0: Java 6)

## Documentation

See the [online documentation](https://shredzone.org/maven/commons-view/) for examples.

## Contribute

* Fork the [Source code at GitHub](https://github.com/shred/commons-view). Feel free to send pull requests.
* Found a bug? [File a bug report!](https://github.com/shred/commons-view/issues)

## License

_commons-view_ is open source software. The source code is distributed under the terms of [GNU Lesser General Public License Version 3](http://www.gnu.org/licenses/lgpl-3.0.html).
