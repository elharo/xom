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

import nu.xom.XMLException;

/**
 * <p><code>XSLException</code>
 *   is thrown when an XSL stylesheet fails to compile
 *   or an XSL transform fails.
 * </p>
` * 
 * @author Elliotte Rusty Harold
 * @version 1.0d16
 *
 */
public class XSLException extends XMLException {

    private Throwable cause;

    /**
     * <p>
     * Creates a new XSLException.
     * </p>
     */
    public XSLException() {
        super();
    }

    /**
     * <p>
     * Creates a new XSLException with the specified detail message
     * and an underlying root cause.
     * </p>
     * 
     * @param message information about the cause of the exception
     * @param ex the nested exception that caused this exception
     */
    public XSLException(String message, Throwable ex) {
        super(message);
        this.initCause(ex);
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
    
    
    /**
     * <p>
     * Return the original cause that led to this exception,
     * or null if there was no original exception.
     * </p>
     * 
     * @return the root cause of this exception
     */
    public Throwable getCause() {
        return this.cause;  
    }


    /**
     * <p>
     * Sets the root cause of this exception. 
     * This may only be called once. Subsequent
     * calls throw an <code>IllegalStateException</code>.
     * </p>
     * 
     * @param cause the root cause of this exception
     * 
     * @return the root cause of this exception
     * 
     * @throws IllegalStateException if this method is called twice
     */
    public Throwable initCause(Throwable cause) {
        if (this.cause == null) this.cause = cause; 
        else throw new IllegalStateException("Cannot reset the cause");
        return this;
    }

}
