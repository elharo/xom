/* Copyright 2003-2005 Elliotte Rusty Harold
   
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
 * Thrown when no XPointer part identifies a subresource. This  
 * corresponds to <a target="_top" 
 * href="https://www.w3.org/TR/xptr-framework/#scheme">section 3.3 
 * of the XPointer Framework</a> which states, 
 * "If no pointer part identifies subresources, it is an error."
 * </p>
 *
 * @author Elliotte Rusty Harold
 * @version 1.3.9
 */
class XPointerResourceException extends XPointerException {

    
    private static final long serialVersionUID = -3854144696916677840L;

    /**
     * <p>
     * Constructs an <code>XPointerResourceException</code> with the 
     * specified detail message. 
     * </p>
     * 
     * @param message a string indicating the specific problem
     */
    XPointerResourceException(String message) {
        super(message);
    }

    
}
