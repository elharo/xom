// Copyright 2002, 2003 Elliotte Rusty Harold
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

package nu.xom.xslt;

/**
 * <p><code>XSLException</code>
 *   is thrown when an XSL stylesheet fails to compile
 *   or an XSL transform fails.
 * </p>
` * 
 * @author Elliotte Rusty Harold
 * @version 1.0d23
 *
 */
public class XSLException extends Exception {

    private Throwable cause;

    /**
     * <p>
     * Creates a new <code>XSLException</code> with the specified 
     * detail message and an underlying root cause.
     * </p>
     * 
     * @param message information about the cause of the exception
     * @param cause the nested exception that caused this exception
     */
    public XSLException(String message, Throwable cause) {
        super(message);
        this.initCause(cause);
    }
    
    /**
     * <p>
     * Creates a new XSLException with the specified detail message.
     * </p>
     * 
     * @param message information about the cause of the exception
     */
    public XSLException(String message) {
        super(message);
    }

    // null is insufficient for detecting an unset cause.
    // The cause may be set to null whicn may not then be reset.
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
     * @return this <code>XSLException</code>
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
            throw new IllegalArgumentException(
              "Self-causation not permitted"); 
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
     * @return the initial exception that caused this exception 
     *     to be thrown
     */
    public Throwable getCause() {
        return this.cause;  
    }

}
