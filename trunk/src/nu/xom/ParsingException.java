// Copyright 2002-2004 Elliotte Rusty Harold
// 
// This library is free software; you can redistribute 
// it and/or modify it under the terms of version 2.1 of 
// the GNU Lesser General Public License as published by  
// the Free Software Foundation.
// 
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
// GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General 
// Public License along with this library; if not, write to the 
// Free Software Foundation, Inc., 59 Temple Place, Suite 330, 
// Boston, MA  02111-1307  USA
// 
// You can contact Elliotte Rusty Harold by sending e-mail to
// elharo@metalab.unc.edu. Please include the word "XOM" in the
// subject line. The XOM home page is temporarily located at
// http://www.cafeconleche.org/XOM/  but will eventually move
// to http://www.xom.nu/

package nu.xom;

/**
 * <p>
 *  The generic superclass for all the
 *  checked exceptions thrown in XOM. The general
 *  principle followed is that anything that could
 *  plausibly be detected by testing such as 
 *  using spaces in an element name is a runtime exception.
 *  Exceptions that depend on environmental conditions,
 *  such as might occur when parsing an external file,
 *  are checked exceptions, because these depend on variable input,
 *  and thus problems may not all be detected during testing.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0a5
 *
 */
public class ParsingException extends Exception {

    
    private Throwable cause;
    private int lineNumber = -1;
    private int columnNumber = -1;
    private String uri;

    
    /**
     * <p>
     * Creates a new <code>ParsingException</code> with a detail message
     * and an underlying root cause.
     * </p>
     * 
     * @param message a string indicating the specific problem
     * @param ex the original cause of this exception
     */
    public ParsingException(String message, Throwable ex) {
        super(message);
        this.initCause(ex);
    }

    
    /**
     * <p>
     * Creates a new <code>ParsingException</code> with a detail message
     * and an underlying root cause.
     * </p>
     * 
     * @param message a string indicating the specific problem
     * @param uri the URI of the document that caused this exception
     * @param ex the original cause of this exception
     */
    public ParsingException(String message, String uri, Throwable ex) {
        super(message);
        this.uri = uri;
        this.initCause(ex);
    }

    
    /**
     * <p>
     * Creates a new <code>ParsingException</code> with a detail message
     * and line and column numbers.
     * </p>
     * 
     * @param message a string indicating the specific problem
     * @param lineNumber the approximate line number 
     *     where the problem occurs
     * @param columnNumber the approximate column number 
     *     where the problem occurs
     */
    public ParsingException(String message, 
      int lineNumber, int columnNumber) {
        this(message, null, lineNumber, columnNumber, null);
    }

    
    /**
     * <p>
     * Creates a new <code>ParsingException</code> with a detail message
     * and line and column numbers.
     * </p>
     * 
     * @param message a string indicating the specific problem
     * @param uri the URI of the document that caused this exception
     * @param lineNumber the approximate line number 
     *     where the problem occurs
     * @param columnNumber the approximate column number 
     *     where the problem occurs
     */
    public ParsingException(String message, String uri, 
      int lineNumber, int columnNumber) {
        this(message, uri, lineNumber, columnNumber, null);
    }

    
    /**
     * <p>
     * Creates a new <code>ParsingException</code> with a detail 
     * message, line and column numbers, and an underlying exception.
     * </p>
     * 
     * @param message a string indicating the specific problem
     * @param uri the URI of the document that caused this exception
     * @param lineNumber the approximate line number 
     *     where the problem occurs
     * @param columnNumber the approximate column number 
     *     where the problem occurs
     * @param ex the original cause of this exception
     */
    public ParsingException(String message, String uri, int lineNumber,
      int columnNumber, Throwable ex) {
        super(message);
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
        this.uri = uri;
        this.initCause(ex);
    }

    
    /**
     * <p>
     * Creates a new <code>ParsingException</code> with a detail 
     * message, line and column numbers, and an underlying exception.
     * </p>
     * 
     * @param message a string indicating the specific problem
     * @param message a string indicating the specific problem
     * @param lineNumber the approximate line number 
     *     where the problem occurs
     * @param columnNumber the approximate column number 
     *     where the problem occurs
     * @param ex the original cause of this exception
     */
    public ParsingException(String message, int lineNumber,
      int columnNumber, Throwable ex) {
        super(message);
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
        this.initCause(ex);
    }

    
    /**
     * <p>
     * Creates a new <code>ParsingException</code> with a detail message.
     * </p>
     * 
     * @param message a string indicating the specific problem
     */
    public ParsingException(String message) {
        super(message);
    }
    
    
    /**
     * <p>
     * Returns the approximate row number of the construct that
     * caused this exception. If the row number is not known,
     * -1 is returned.
     * </p>
     * 
     * @return row number where the exception occurred
     */
    public int getLineNumber() {
        return this.lineNumber;  
    }

    /**
     * <p>
     * Returns the approximate column number of the construct that
     * caused this exception. If the column number is not known,
     * -1 is returned.
     * </p>
     * 
     * @return column number where the exception occurred
     */
    public int getColumnNumber() {
        return this.columnNumber;  
    }


    /**
     * <p>
     * Returns the system ID (generally a URL) of the document that
     * caused this exception. If this is not known, for instance 
     * because the document was parsed from a raw input stream or from
     * a string, it returns null. 
     * </p>
     * 
     * @return the URI of the document that caused this exception
     */
    public String getURI() {
        return this.uri;  
    }


    // null is insufficient for determining unset cause.
    // The cause may be set to null which may not then be reset.
    private boolean causeSet = false;

    /**
     * <p>
     * Sets the root cause of this exception. This may 
     * only be called once. Subsequent calls throw an 
     * <code>IllegalStateException</code>.
     * </p>
     * 
     * <p>
     * This method is unnecessary in Java 1.4 where it could easily be
     * inherited from the superclass. However, including it here
     * allows this  method to be used in Java 1.3 and earlier.
     * </p>
     *
     * @param cause the root cause of this exception
     * 
     * @return this <code>XMLException</code>
     * 
     * @throws IllegalArgumentException if the cause is this exception
     *   (An exception cannot be its own cause.)
     * @throws IllegalStateException if this method is called twice
     */
    public Throwable initCause(Throwable cause) {
        
        if (causeSet) {
            throw new IllegalStateException("Can't overwrite cause");
        } 
        else if (cause == this) {
            throw new IllegalArgumentException("Self-causation not permitted"); 
        }
        else this.cause = cause;
        causeSet = true;
        return this;
        
    }

    
    /**
     * <p>
     * Returns the underlying exception that caused this exception.
     * </p>
     * 
     * @return the root exception that caused this exception 
     *     to be thrown
     */
    public Throwable getCause() {
        return this.cause;  
    }

    
    /**
     * <p>
     *   Returns a string suitable for display to the developer
     *   summarizing what went wrong where.
     * </p>
     * 
     * @return an exception message suitable for display to a developer
     */
    public String toString() {
        return super.toString() + " at line " 
          + lineNumber + ", column " + columnNumber + ".";    
    }

}