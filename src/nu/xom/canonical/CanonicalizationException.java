/* Copyright 2005 Elliotte Rusty Harold
   
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
package nu.xom.canonical;

/**
 * <p>
 * Indicates problems with canonicalization.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.1b3
 * 
 */
public class CanonicalizationException extends RuntimeException {


    private static final long serialVersionUID = 6935623053373600014L;
    
    private Throwable cause;

    
    /**
     * <p>
     * Creates a new <code>CanonicalizationException</code> 
     * with a detail message.
     * </p>
     * 
     * @param message a string indicating the specific problem
     */
    public CanonicalizationException(String message) {
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
    public final Throwable getCause() {
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
     * allows this method to be used in Java 1.3 and earlier.
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
