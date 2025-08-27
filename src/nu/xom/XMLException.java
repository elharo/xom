/* Copyright 2002-2005 Elliotte Rusty Harold
   
   This library is free software; you can redistribute it and/or modify
   it under the terms of version 2.1 of the GNU Lesser General Public 
   License as published by the Free Software Foundation.
   
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
   GNU Lesser General Public License for more details.
   
   You should have received a copy of the GNU Lesser General Public
   License along with this library. If not, see
   <https://www.gnu.org/licenses/>.
   
   You can contact Elliotte Rusty Harold by sending e-mail to
   elharo@ibiblio.org. Please include the word "XOM" in the
   subject line. The XOM home page is located at https://xom.nu/
*/


package nu.xom;

/**
 * <p>
 *  The generic superclass for most runtime exceptions thrown in 
 *  <code>nu.xom</code>. The general principle followed is that 
 *  anything that can normally be detected by testing such as 
 *  using spaces in an element name is a runtime exception.
 *  Exceptions that depend on environmental conditions,
 *  such as might occur when parsing an external file,
 *  are checked exceptions, because these depend on variable input,
 *  and thus all problems may not be detected during testing.
 * </p>
 *
 * @author Elliotte Rusty Harold
 * @version 1.1b3
 */
public class XMLException extends RuntimeException {


    private static final long serialVersionUID = -4497254051626978523L;
    
    private Throwable cause;

    
    /**
     * <p>
     * Creates a new <code>XMLException</code> 
     * with the specified detail message
     * and an underlying root cause.
     * </p>
     *
     * @param message information about the cause of the exception
     * @param cause the nested exception that caused this exception
     */
    public XMLException(String message, Throwable cause) {
        super(message);
        this.initCause(cause);
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
     *  or null if there was no original exception.
     * </p>
     *
     * @return the root cause of this exception
     */
    public Throwable getCause() {
        return this.cause;  
    }

    
    // null is insufficient for detecting an uninitialized cause.
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
     * @return this <code>XMLException</code>
     * @throws IllegalArgumentException if the cause is this exception
     *   (An exception cannot be its own cause.)
     * @throws IllegalStateException if this method is called twice
     */
    public final Throwable initCause(Throwable cause) {
        
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
