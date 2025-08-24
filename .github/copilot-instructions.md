# GitHub Copilot Instructions for XOM

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