# XOM: a new XML object model

XOM™ is a new XML object model. It is an open source (LGPL), tree-based API for processing XML with Java that strives for correctness, simplicity, and performance, in that order. It includes built-in support for a number of XML technologies including Namespaces in XML, XPath 1.0, XSLT 1.0, XInclude, xml:id, xml:base, Canonical XML, and Exclusive Canonical XML. XOM documents can be converted to and from SAX and DOM.

XOM is designed to be easy to learn and easy to use. It works very straight-forwardly, and has a very shallow learning curve. Assuming you're already familiar with XML, you should be able to get up and running with XOM very quickly.

XOM is the only XML API that makes no compromises on correctness. XOM only accepts namespace well-formed XML documents, and only allows you to create namespace well-formed XML documents. (In fact, it's a little stricter than that: it actually guarantees that all documents are round-trippable and have well-defined XML infosets.) XOM manages your XML so you don't have to. With XOM, you can focus on the unique value of your application, and trust XOM to get the XML right.

XOM is fairly unique in that it is a dual streaming/tree-based API. Individual nodes in the tree can be processed while the document is still being built. The enables XOM programs to operate almost as fast as the underlying parser can supply data. You don't need to wait for the document to be completely parsed before you can start working with it.

XOM is very memory efficient. If you read an entire document into memory, XOM uses as little memory as possible. More importantly, XOM allows you to filter documents as they're built so you don't have to build the parts of the tree you aren't interested in. For instance, you can skip building text nodes that only represent boundary white space, if such white space is not significant in your application. You can even process a document piece by piece and throw away each piece when you're done with it. XOM has been used to process documents that are gigabytes in size.

The current version of XOM is 1.3.8 and is backwards compatible with 1.2, 1.1 and 1.0. You should not need to recompile any code to upgrade to 1.3.8. XOM is believed to be quite stable and robust. Future releases should be backwards compatible with the 1.0 API for the foreseeable future.

## Adding XOM to your build

XOM's Maven group ID is `xom` and its artifact ID is `xom`. To add a dependency on XOM using Maven, add this `dependency` element to your pom.xml:

```xml
<dependency>
  <groupId>xom</groupId>
  <artifactId>xom</artifactId>
  <version>1.3.8</version>
</dependency>
```

To add a dependency using Gradle:

```gradle
dependencies {
  compile 'xom:xom:1.3.8'
}
```

## Dependencies


XOM is not complete unto itself. It depends on an underlying SAX parser to read documents and feed the data into a tree structure. While theoretically any SAX2 compliant parser should work, Xerces 2.6.1 and later is the only one that I am fairly confident does work. Xerces 2.8.0 is included with the full distribution. This product includes software developed by the Apache Software Foundation (http://www.apache.org/). Piccolo 1.0.3, Crimson, GNU JAXP 1.0b1, the Oracle XML Parser for Java 9.2.0.2.0D and 9.2.0.5.0, and Xerces versions prior to 2.6.1 all have bugs that prevent them from doing what XOM needs them to do. (Note to XML parser vendors: XOM's test suite gives parsers a very thorough workout, and delves into some of the more obscure parts of the XML spec that many parsers get wrong. You could do a lot worse for testing than making sure all the XOM unit tests pass when using your parser.)

Similarly XSLT support depends on a TrAX processor (Xalan-J 2.6.0 is bundled). XInclude and XML canonicalization, however, are native.


## Learning More

If you'd like to know more about XOM, I suggest starting with the [tutorial](https://xom.nu/tutorial.xhtml). XOM also includes a large collection of small [sample programs](https://xom.nu/samples.xhtml) that demonstrate various parts of the library. If you're curious about why XOM is the way it is, or if you would like to suggest future directions for XOM, you should read the [design principles](https://xom.nu/designprinciples.xhtml) on which XOM is based. If you have a question about XOM that is not answered in the API documentation or the FAQ, you can ask it on [Stack Overflow](https://stackoverflow.com/questions/ask?tags=xom) or the [xom-interest mailing list]((https://lists.ibiblio.org/mailman/listinfo/xom-interest)). You do not need to be subscribed to post, but non-subscriber questions are moderated. (Due to increasing amounts of non-subscriber spam, it is possible non-subscriber questions are missed. If you don't get an answer, please subscribe and try again.)


## Links

- [GitHub project](https://github.com/elharo/xom)
- [Issue tracker: Report a defect or feature request](https://github.com/elharo/xom/issues/new)
- [StackOverflow: Ask "how-to" and "why-didn't-it-work" questions](https://stackoverflow.com/questions/ask?tags=xom)
- [xom-interest: For open-ended discussion](https://lists.ibiblio.org/mailman/listinfo/xom-interest)

