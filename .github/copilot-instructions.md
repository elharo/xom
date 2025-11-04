# GitHub Copilot Instructions for XOM

## Project Overview

XOM (XML Object Model) is a tree-based API for processing XML with Java. It emphasizes correctness, simplicity, and performance, in that order. XOM is designed to be:
- **Correct**: Only accepts and creates namespace well-formed XML documents
- **Simple**: Easy to learn with a shallow learning curve
- **Dual streaming/tree-based**: Individual nodes can be processed while document is being built
- **Memory efficient**: Supports filtering and incremental processing of large documents

XOM is an LGPL-licensed library that has been stable since version 1.0 and maintains backward compatibility.

## Java Version Requirements

**IMPORTANT: Do NOT change the Java source and target from 1.6**

XOM is required to maintain compatibility with Java 1.6 or later as documented in README.txt. This is a fundamental requirement of the library to ensure it can run on older Java environments.

### Build Configuration Rules

1. **Never modify** the `source="1.6"` and `target="1.6"` settings in build.xml
2. **Do not suggest** upgrading Java compilation target beyond 1.6
3. **Preserve compatibility** with Java 1.6 language features and APIs

### Current Configuration

The build.xml file contains two javac tasks that must maintain Java 1.6 compatibility:

```xml
<!-- Main compilation target around line 193-194 -->
<javac ... target="1.6" source="1.6" ... />

<!-- Jaxen compilation target around line 749-750 -->
<javac ... target="1.6" source="1.6" ... />
```

### What You Can Do

- Suggest code improvements that are compatible with Java 1.6
- Recommend libraries and APIs available in Java 1.6
- Help with XML processing, XSLT, XPath, and other XOM-related functionality
- Assist with unit tests and documentation

### What You Should NOT Do

- Suggest upgrading to newer Java versions (7, 8, 11, 17, etc.)
- Recommend APIs or language features introduced after Java 1.6
- Propose changes to the Java compilation targets in build.xml
- Suggest using modern Java syntax (try-with-resources, lambda expressions, etc.)

This ensures XOM continues to serve users who need Java 1.6 compatibility for their applications.

## Build System

XOM uses Apache Ant as its build system. The main build file is `build.xml` at the repository root.

### Common Ant Targets

- `ant compile` - Compile the source code (both core XOM and Jaxen)
- `ant test` - Run JUnit tests using command line interface
- `ant jar` - Create xom.jar
- `ant samples` - Create xom-samples.jar
- `ant javadoc` - Generate API documentation
- `ant clean` - Remove build files
- `ant help` - Display available targets

### Testing

- Tests are located in `src/nu/xom/tests/`
- Use `ant test` to run the full test suite
- Tests use JUnit and are comprehensive - they exercise edge cases and XML specification compliance
- The test suite is rigorous and helps ensure XML correctness
- Test results are placed in the `testresults/` directory

### Building from Scratch

```bash
ant clean
ant compile
ant test
```

## Project Structure

```
src/nu/xom/
├── [Core classes]       # Main XOM API (Element, Document, Attribute, etc.)
├── xinclude/            # XInclude implementation
├── xslt/                # XSLT support
├── canonical/           # Canonical XML support
├── converters/          # Converters for DOM, SAX, etc.
├── samples/             # Sample applications demonstrating XOM usage
├── tests/               # JUnit test suite
├── benchmarks/          # Performance benchmarks
└── tools/               # Development tools
```

### Key Packages

- `nu.xom` - Core API classes (Element, Document, Attribute, Node, etc.)
- `nu.xom.xinclude` - XInclude 1.0 implementation
- `nu.xom.xslt` - XSLT 1.0 transformation support
- `nu.xom.canonical` - Canonical XML and Exclusive Canonical XML
- `nu.xom.converters` - Converters between XOM and other APIs (DOM, SAX)
- `nu.xom.samples` - Example applications
- `nu.xom.tests` - Comprehensive test suite

## Dependencies

### Runtime Dependencies (in lib/)

- `xercesImpl-2.12.2.jar` - Apache Xerces XML parser (required)
- `xml-apis-1.4.01.jar` - XML APIs (required)

### Build/Test Dependencies

- `junit.jar` - JUnit testing framework (in lib/)
- `jaxen-1.1.6-src.zip` - XPath engine source (bundled and compiled as part of build)
- Various additional libraries in `lib2/` for specific features:
  - `servlet-api-2.5.jar` - For servlet samples
  - `tagsoup-1.2.1.jar` - For HTML parsing
  - `jarjar-1.0.jar` - For repackaging dependencies

### Parser Requirements

XOM requires a SAX2-compliant parser. Xerces 2.6.1+ is the recommended and tested parser. Earlier versions and other parsers may have bugs that prevent proper XOM operation.

## Code Style and Conventions

### General Guidelines

- **Correctness first**: XOM prioritizes XML specification compliance above all else
- **No null returns**: XOM methods typically throw exceptions rather than returning null
- **Immutable where possible**: Many XOM objects are immutable or have restricted mutability
- **Clear error messages**: Exceptions should provide detailed, actionable error messages
- **Namespace awareness**: All XOM code must properly handle XML namespaces

### Testing Guidelines

- All new features must have comprehensive tests
- Tests should cover edge cases and error conditions
- Tests should verify XML specification compliance
- Follow existing test patterns in `src/nu/xom/tests/`

### Documentation

- Public APIs must have complete JavaDoc
- Include `@param`, `@return`, `@throws` for all methods
- Provide code examples in JavaDoc where helpful
- Document XML specification references where applicable

## Common Development Workflows

### Adding a New Feature

1. Write tests first (TDD approach is used)
2. Implement the feature in the appropriate package
3. Ensure all existing tests still pass
4. Add JavaDoc documentation
5. Run `ant test` to verify
6. Consider XML specification compliance

### Fixing a Bug

1. Add a test that reproduces the bug
2. Fix the bug in the source
3. Verify the new test passes
4. Ensure no existing tests break
5. Run full test suite with `ant test`

### Working with XML Specifications

XOM implements several XML specifications:
- XML 1.0/1.1
- Namespaces in XML
- XPath 1.0
- XSLT 1.0
- XInclude 1.0
- xml:id, xml:base
- Canonical XML
- Exclusive Canonical XML

When working with these features, always consult the relevant W3C specifications for correctness.

## Important Notes

### Character Encoding

XOM handles character encoding carefully. Be aware of:
- UTF-8 is the default encoding
- XOM properly handles all Unicode characters
- Encoding declarations in XML documents are respected

### Performance Considerations

- XOM supports streaming to handle large documents
- Use `NodeFactory` for filtering during parsing
- Consider memory implications when building large trees
- Benchmarks are available in `src/nu/xom/benchmarks/`

### Error Handling

- XOM uses checked exceptions extensively
- Common exceptions: `ParsingException`, `ValidityException`, `XMLException`
- Exceptions are specific and informative
- Don't catch exceptions unless you can handle them meaningfully

## CI/CD

The repository uses GitHub Actions for continuous integration:
- Workflow: `.github/workflows/ci.yml`
- Tests run on Java 8 and Java 11 (though code targets Java 1.6)
- All tests must pass before merging PRs

## Release Process

See `RELEASING.md` for detailed release instructions. Key points:
- Version numbers are in `build.xml`, `README.md`, `README.txt`
- Releases are published to Maven Central
- Website is hosted on Google App Engine