# Contributing to XOM

## Bug Reports

Bug reports are welcome through the [GitHub issue tracker](https://github.com/elharo/xom/issues).

When filing a bug, please include:
- A code fragment or JUnit test that demonstrates the problem
- The Java version and operating system you are running

All fixes are made on the latest release. Patches are not released for earlier versions without a support contract.

## Feature Requests and Pull Requests

**Please be aware: XOM is a very stable, mature library. New features are only very occasionally considered.**

XOM has been stable since version 1.0 and intentionally has a narrow scope. The vast majority of proposed changes and pull requests are not accepted. Before investing time in a pull request, open an issue to discuss your proposed change.

Changes most likely to be considered:
- Bug fixes with a clear test case demonstrating the defect
- Fixes for security vulnerabilities
- Improvements to documentation or error messages

Changes that are unlikely to be accepted:
- New features or API additions
- Performance "optimizations" (unless they come with really convincing evidence of actual performance problems and improvements)

## Setting Up in IntelliJ IDEA

XOM uses Apache Ant as its build system. IntelliJ IDEA does not include Ant support by default, so you must install the **Ant** plugin first:

1. Open **File → Settings** (on macOS: **IntelliJ IDEA → Settings**).
2. Go to **Plugins**, search for **Ant**, and install the **Ant** plugin by JetBrains.
3. Restart IntelliJ IDEA when prompted.

Once the plugin is installed, import the project:

1. Open IntelliJ IDEA and choose **File → Open**.
2. Navigate to the root of the XOM repository and click **OK**.
3. Open the **Ant** tool window (**View → Tool Windows → Ant**).
4. Click the **+** button in the Ant tool window and add the `build.xml` file from the repository root.
5. Use the Ant tool window to run targets such as `compile`, `test`, or `jar`.

To run tests from the command line instead, use `ant test` at the repository root.

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
