/* Copyright 2002-2005 Elliotte Rusty Harold
   
   This library is free software; you can redistribute it and/or modify
   it under the terms of version 2.1 of the GNU Lesser General Public 
   License as published by the Free Software Foundation.
   
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
   GNU Lesser General Public License for more details.
   
   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the 
   Free Software Foundation, Inc., 59 Temple Place, Suite 330, 
   Boston, MA 02111-1307  USA
   
   You can contact Elliotte Rusty Harold by sending e-mail to
   elharo@metalab.unc.edu. Please include the word "XOM" in the
   subject line. The XOM home page is located at http://www.xom.nu/
*/

package nu.xom.xinclude;

/**
 * <p>
 * The generic superclass for all checked exceptions that may be thrown 
 * as a result of a violation of XInclude's rules.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0
 */
public class XIncludeException extends Exception {

    
    private String uri;

    /**
     * <p>
     * Constructs an <code>XIncludeException</code> with the specified
     * detail message.
     * </p>
     * 
     * @param message a string indicating the specific problem
     */
    public XIncludeException(String message) {
        super(message);
    }

    
    /**
     * <p>
     * Constructs an <code>XIncludeException</code> with the specified 
     * detail message and initial cause. The error message string 
     * <code>message</code> can later be retrieved by the 
     * <code>{@link java.lang.Throwable#getMessage}</code>
     * method of class <code>java.lang.Throwable</code>.
     * </p>
     * 
     * @param message a string indicating the specific problem
     * @param cause   the initial cause of the exception
     */
    public XIncludeException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }

    
    /**
     * <p>
     * Creates a new <code>XIncludeException</code> with a detail 
     * message, line and column numbers, and the URI of the document
     * that caused the exception.
     * </p>
     * 
     * @param message a string indicating the specific problem
     * @param uri the URI of the document that caused this exception
     */
    public XIncludeException(String message, String uri) {
        super(message);
        this.uri = uri;
    }

    
    /**
     * <p>
     * Returns the URI of the document that caused this exception. 
     * If the URI is not known, null is returned.
     * </p>
     * 
     * @return URI of the document where the exception occurred
     */
    public String getURI() {
        return this.uri;  
    }

    
    private Throwable cause;

    
    /**
     * <p>
     * When an <code>IOException</code>,  
     * <code>MalformedURLException</code>, or other generic  
     * exception is thrown while processing an XML document
     * for XIncludes, it is customarily replaced
     * by some form of <code>XIncludeException</code>.  
     * This method allows you to retrieve the original exception.
     * It returns null if no such exception caused this 
     * <code>XIncludeException</code>.
     *</p>
     * 
     * @return the underlying exception which 
     *     caused this XIncludeException to be thrown
     */
    public Throwable getCause() {
        return this.cause;  
    }

    
    // null is insufficient for detecting an uninitialized cause.
    // The cause may be set to null which may not then be reset.
    private boolean causeSet = false;

    
    /**
     * <p>
     * When an <code>IOException</code>,  
     * <code>MalformedURLException</code>, or other generic exception 
     * is thrown while processing an XML document
     * for XIncludes, it is customarily replaced
     * by some form of <code>XIncludeException</code>.  
     * This method allows you to store the original exception.
     * </p>
     *
     * @param cause the root cause of this exception
     * 
     * @return this <code>XIncludeException</code>
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

    
}