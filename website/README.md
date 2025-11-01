# XOM Website Documentation Build

This directory contains the source files for the XOM website documentation.

## Build System

The documentation build uses XOM itself for all XML processing tasks:

- **nu.xom.samples.Transformer** - Performs XSLT transformations using XOM

## Dependencies

The build requires:

1. **XOM library** - Built class files from `../build/classes` and `../build/jaxen-classes`
2. **Xerces** - XML parser from `../lib/`
3. **DocBook XSL** - Stylesheets from `../lib2/docbook-xsl-1.79.2/`

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

1. XML source files are processed with `nu.xom.samples.XIncludeDriver` to resolve XInclude directives
2. The resulting documents are transformed using `nu.xom.samples.Transformer` with DocBook XSL stylesheets
3. XOM uses the default XSLT processor available in the JDK
