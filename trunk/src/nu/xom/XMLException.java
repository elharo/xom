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


package nu.xom;

/**
 * <p>
 *  This is the generic superclass for all the
 *  runtime exceptions thrown in XOM. The general
 *  principle followed is that anything that could
 *  plausibly be detected by testing such as 
 *  using spaces in an element name is a runtime exception.
 *  Exceptions that depend on environmental conditions,
 *  such as might occur when parsing an external file,
 *  are checked exceptions, because these depend on variable input,
 *  and thus all problems may not be detected during testing.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d14
 *
 */
public class XMLException extends RuntimeException {

    private Throwable cause;

    /**
     * Creates a new <code>XMLException</code>.
     */
    public XMLException() {
        super();
    }

    /**
     * <p>
     * Creates a new <code>XMLException</code> 
     * with the specified detail message
     * and an underlying root cause.
     *  </p>
     *
     * @param message information about the cause of the exception
     * @param ex the nested exception that caused this exception
     */
    public XMLException(String message, Throwable ex) {
        super(message);
        this.initCause(ex);
    }
    
    /**
     * <p>
     *   Creates a new <code>XMLException</code> with 
     *   the specified detail message.
     *  </p>
     *
     * @param message information about the cause of the exception
     */
    public XMLException(String message) {
        super(message);
    }
    
    
    /**
     * <p>
     *  Return the original cause that led to this exception,
     *   or null if there was no original exception.
     *  </p>
     *
     * @return the root cause of this exception
     */
    public Throwable getCause() {
        return this.cause;  
    }


    /**
     * <p>
     * Sets the root cause of this exception. This may 
     * only be called once. Subsequent calls throw an 
     * <code>IllegalStateException</code>.
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
