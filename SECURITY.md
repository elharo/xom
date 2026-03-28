# Security Policy

## Reporting a Vulnerability

Please report security vulnerabilities by opening an issue in the
[GitHub issue tracker](https://github.com/elharo/xom/issues) or by
emailing the maintainer directly.

## Known Security Considerations

### Billion Laughs / XML Bomb (Exponential Entity Expansion)

XOM delegates XML parsing to an underlying SAX parser, typically Apache Xerces.
By default the parser may be vulnerable to the
[billion laughs attack](https://en.wikipedia.org/wiki/Billion_laughs_attack),
a denial-of-service attack that exploits recursive XML entity definitions to
exhaust available memory.

See the [XOM FAQ](https://xom.nu/faq.xhtml#billion-laughs) for instructions on how to
configure XOM's underlying parser to protect against this attack.
