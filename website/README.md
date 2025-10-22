# XOM Website Documentation Build

This directory contains the source files for the XOM website documentation.

## Build System

The documentation build uses XOM itself for all XML processing tasks:

- **XOMXIncluder** - Processes XInclude directives to merge tutorial chapters
- **XOMTransformer** - Performs XSLT transformations using Saxon-HE

## Dependencies

The build requires:

1. **XOM library** - The main XOM jar file (downloaded from Maven Central)
2. **Saxon-HE** - Modern open-source XSLT processor (replaces Saxon 6.5.5)
3. **Xerces** - XML parser from `../lib/`
4. **DocBook XSL** - Stylesheets from `../lib2/docbook-xsl-1.79.2/`

## Building Documentation

To build all documentation:

```bash
ant dist
```

To build specific targets:

```bash
ant tutorial    # Build the tutorial
ant faq         # Build the FAQ
ant samples     # Build the samples documentation
```

## How It Works

1. XML source files are processed with XOMXIncluder to resolve XInclude directives
2. The resulting documents are transformed using XOMTransformer with DocBook XSL stylesheets
3. Saxon-HE is used as the XSLT 1.0 processor (configured via javax.xml.transform.TransformerFactory)
4. A custom entity resolver (`html-entities.dtd`) provides common HTML and Greek entities offline

## Migration from Saxon 6.5.5

This build system replaced the original Saxon 6.5.5 dependency with:

- XOM's built-in XSLT support (via `nu.xom.xslt.XSLTransform`)
- Saxon-HE 9.9+ as the underlying XSLT processor
- Custom utility classes for command-line transformation

The key advantage is that Saxon-HE is actively maintained and works with modern Java versions, while maintaining compatibility with XSLT 1.0 stylesheets like DocBook.
