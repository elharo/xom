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

package nu.xom.xinclude;

/**
 * <p>
 * Indicates that an <code>xinclude:include</code> element has neither 
 * an <code>href</code> attribute nor an <code>xpointer</code> 
 * attribute.
 * </p>
 *
 * @author Elliotte Rusty Harold
 * @version 1.1b3
 */
public class NoIncludeLocationException extends XIncludeException {


    private static final long serialVersionUID = -2839892819780144596L;


    /**
     * <p>
     * Constructs a <code>NoIncludeLocationException</code> with the 
     * specified message.
     * </p>
     *
     * @param message a string indicating the specific problem
     */
    public NoIncludeLocationException(String message) {
       super(message);
    }

    
    /**
     * <p>
     * Constructs a <code>NoIncludeLocationException</code> with the 
     * specified detail message and root cause. 
     * </p>
     * 
     * @param message a string indicating the specific problem
     * @param cause the initial exception that caused this 
     *     <code>NoIncludeLocationException</code>
     */
    public NoIncludeLocationException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }

    
    /**
     * <p>
     * Creates a new <code>NoIncludeLocationException</code> with a  
     * detail message, line and column numbers, and a URI of the 
     * document that caused the exception.
     * </p>
     * 
     * @param message a string indicating the specific problem
     * @param uri the URI of the document that caused this exception
     */
    public NoIncludeLocationException(String message, String uri) {
        super(message, uri);
    }
    
    
}