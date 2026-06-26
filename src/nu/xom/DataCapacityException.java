package nu.xom;

/**
 * Thrown when the size of data provided to a XOM node exceeds 
 * the internal capacity limits of the XOM tree structure, 
 * typically to prevent memory exhaustion attacks.
 */
public class DataCapacityException extends XMLException {

  DataCapacityException(String message, Throwable cause) {
    super(message, cause);
  }
}
