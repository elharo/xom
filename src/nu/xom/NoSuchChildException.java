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
 * Indicates that a child with a certain name and/or namespace does
 * not exist. This is thrown when you attempt to remove a node from
 * a parent that does not actually have that node as a child.  
 * </p>
 *
 * @author Elliotte Rusty Harold
 * @version 1.1b3
 * @see ParentNode#removeChild(Node)
 */
public class NoSuchChildException extends XMLException {


    private static final long serialVersionUID = 1944673590646036964L;


    /**
     * <p>
     * Creates a new <code>NoSuchChildException</code> 
     * with a detail message.
     * </p>
     *
     * @param message explains the reason for the exception
     */
    public NoSuchChildException(String message) {
        super(message);
    }

    
    /**
     * <p>
     * Creates a new <code>NoSuchChildException</code> 
     * with the specified detail message
     * and an underlying root cause.
     * </p>
     *
     * @param message explains the reason for the exception
     * @param cause the nested exception that caused this exception
     */
    public NoSuchChildException(String message, Throwable cause) {
        super(message);
        this.initCause(cause);
    }

    
}
