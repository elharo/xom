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
 * Indicates that an attribute with a certain name and/or namespace 
 * does not exist. This is normally thrown after an attempt to remove
 * such a non-existent attribute.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0
 * @see Element#removeAttribute(Attribute)
 *
 */
public class NoSuchAttributeException extends XMLException {


    private static final long serialVersionUID = -7472517723464699452L;


    /**
     * <p>
     * Creates a new <code>NoSuchAttributeException</code> 
     * with a detail message.
     * </p>
     * 
     * @param message explains the reason for the exception
     */
    public NoSuchAttributeException(String message) {
        super(message);
    }

    
    /**
     * <p>
     * Creates a new <code>NoSuchAttributeException</code> 
     * with the specified detail message
     * and an underlying root cause.
     * </p>
     * 
     * @param message explains the reason for the exception
     * @param cause the nested exception that caused this exception
     */
    public NoSuchAttributeException(String message, Throwable cause) {
        super(message);
        this.initCause(cause);
    }

    
}