# Contributing to XOM

## Importing into Eclipse

Before importing into Eclipse, first compile the bundled Jaxen XPath library that XOM
depends on. This requires [Apache Ant](https://ant.apache.org/) to be installed:

```
ant compile-jaxen
```

This creates the compiled Jaxen classes in `build/jaxen-classes/`.

Next, create a new Java project in Eclipse pointing at the XOM checkout directory:

1. Choose **File > New > Java Project**
2. Uncheck **Use default location**, click **Browse…**, and select the XOM checkout directory
3. Click **Next**, then open the **Libraries** tab
4. Click **Add External JARs…** and add these JARs from the `lib/` directory:
   - `xercesImpl-2.12.2.jar`
   - `xml-apis-1.4.01.jar`
   - `junit-4.13.2.jar`
5. Click **Add Class Folder…** and add `build/jaxen-classes`
6. Click **Finish**
