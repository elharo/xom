# Security Policy

## Reporting Security Issues

If it's a real issue you've personally discovered and can explain, feel free to drop me an email.

If it's some security tool logging a warning, that is 95% likely not to be a security issue but rather a bug in the tool. You can file that here after you have investigated if you are willing to vouch that it is a true security issue, but be aware that these tools are rarely correct when analyzing XOM.

## What is **NOT** a Security Bug in XOM

1. Anything in your dependency tree whose source code is not in this repo. You control your classpath. XOM doesn't. If you don't like what's in the classpath, change it.
2. Properly implementing XML 1.0 according to the specification. This includes loading external DTDs and processing the internal DTD subset.
3. Properly implementing Namespaces in XML according to the specification.
4. Properly implementing XPath 1.0 and XSLT 1.0 according to the specifications.
5. Being able to load a URL from Java code.

## Probably Not Security Bugs in XOM

* Problems that only appear when your code (not XOM's) accepts untrusted, unvalidated user input

## Defending Against XML Denial-of-Service Attacks

XOM itself does not add defenses against XML entity expansion attacks such as the
[billion laughs attack](https://en.wikipedia.org/wiki/Billion_laughs_attack), but you can
configure the underlying parser to limit entity expansion. See the
[FAQ](https://xom.nu/faq.xhtml#billionlaughs) for details.

## Possible Security Bugs in XOM

If you can find one, none are currently known to exist:

* XML documents or XPath expressions that cause infinite loops in the parser.
