# Testing XOM

## JUnit 4 with Legacy Support

XOM uses JUnit 4.13.2 with legacy runner support. This allows existing JUnit 3-style tests to work without modifications while also enabling the use of JUnit 4 features.

## Running Tests

To run all tests:
```bash
ant test
```

To run a specific test class:
```bash
java -classpath build/classes:lib/junit.jar:lib/hamcrest-core-1.3.jar:lib/xercesImpl.jar:lib/xml-apis.jar:build/jaxen-classes junit.textui.TestRunner nu.xom.tests.AttributeTest
```

## Skipping Network Tests

Some tests require external network connections. In CI environments where network access is limited, you can use JUnit 4's `org.junit.Assume` to conditionally skip these tests.

### Example

```java
import static org.junit.Assume.assumeTrue;

public class MyTest extends XOMTestCase {
    
    public MyTest(String name) {
        super(name);
    }
    
    public void testNetworkOperation() {
        // Skip this test if SKIP_NETWORK_TESTS environment variable is set
        assumeTrue("Skipping network test", 
                   System.getenv("SKIP_NETWORK_TESTS") == null);
        
        // Test code that requires network access
        // ...
    }
}
```

To skip network tests when running via ant:
```bash
export SKIP_NETWORK_TESTS=true
ant test
```

## Dependencies

- **junit.jar** (JUnit 4.13.2) - Testing framework
- **hamcrest-core-1.3.jar** - Matcher library required by JUnit 4

Both are only needed for testing and not required for normal XOM operation.
