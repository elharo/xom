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

Once the plugin is installed, open the project:

1. Open IntelliJ IDEA and choose **File → Open**.
2. Navigate to the root of the XOM repository and click **OK**.
3. If IntelliJ shows a dialog asking how to open the project (for example, offering "Eclipse project" or "Maven project"), click **Cancel**. XOM uses Ant, not Eclipse or Maven.
4. Open the **Ant** tool window (**View → Tool Windows → Ant**).
5. Click the **+** button in the Ant tool window and add the `build.xml` file from the repository root.
6. Use the Ant tool window to run targets such as `compile`, `test`, or `jar`.

To run tests from the command line instead, use `ant test` at the repository root.

## Setting Up in Eclipse

Before importing into Eclipse, run `ant compile` from the repository root at least once. This downloads the Ivy-managed dependencies and compiles the bundled Jaxen library that XOM requires.

Once you have done that, import the project:

1. Open Eclipse and choose **File → Import**.
2. Select **Java → Java Project from Existing Ant Buildfile** and click **Next**.
3. Click **Browse** next to the **Ant buildfile** field, navigate to `build.xml` at the root of the XOM repository, and click **Open**.
4. In the **Javac declarations** list, select the first `javac` entry (the one for the main XOM sources), then click **Finish**.
5. To run Ant targets such as `compile`, `test`, or `jar`, right-click `build.xml` in the Package Explorer and select **Run As → Ant Build...**.

To run tests within Eclipse, right-click on any test class in `src/nu/xom/tests/` and select **Run As → JUnit Test**.
