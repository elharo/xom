// Copyright 2003, 2004 Elliotte Rusty Harold
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

package nu.xom.xinclude;

/**
 * <p>
 * <code>XPointerException</code> is a checked exception
 * that indicates an error as defined by the XPoiner specification.
 * </p>
 *
 * @author Elliotte Rusty Harold
 * @version 1.0d25
 */
class XPointerException extends Exception {

    private Throwable cause = null;

    /**
     * <p>
     * Constructs an <code>XPointerException</code> with the 
     * specified detail message.
     * </p>
     *
     * @param message the detail message
     */
    XPointerException(String message) {
        super(message);
    }

    /**
     * <p>
     * Constructs an <code>XPointerException</code> with the 
     * specified detail message and root cause.
     * </p>
     * 
     * @param message the detail message
     * @param cause the initial exception which caused this 
     *     <code>XPointerException</code>
     */
    XPointerException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }

    /**
     * <p>
     * When an <code>IOException</code>,  
     * <code>MalformedURLException</code>, or other generic  
     * exception is thrown while processing an XML document
     * for XPointer, it is customarily replaced
     * by some form of <code>XPointerSyntaxException</code>.  
     * This method allows you to retrieve the original exception.
     * It returns null if no such exception caused this 
     * <code>XPointerSyntaxException</code>.
     *</p>
     * 
     * @return the underlying exception which 
     *     caused the XPointerSyntaxException to be thrown
     */
    public Throwable getCause() {
        return this.cause;  
    }

    // null is insufficient for detecting an unset cause.
    // The cause may be set to null whicn may not then be reset.
    private boolean causeSet = false;

    /**
     * <p>
     * When an <code>IOException</code>,  
     * <code>MalformedURLException</code>, or other generic exception 
     * is thrown while processing an XML document
     * for XPointers, it is customarily replaced
     * by some form of <code>XPointerException</code>.  
     * This method allows you to store the original exception.
     * </p>
     *
     * @param cause the root cause of this exception
     * 
     * @return this <code>XPointerException</code>
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
