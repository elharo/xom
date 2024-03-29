/* Copyright 2004, 2005 Elliotte Rusty Harold
   
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
 * Indicates that an <code>href</code> attribute contains an illegal 
 * value, such as a URI with a fragment identifier, or a syntactically
 * incorrect IRI.
 * </p>
 *
 * @author Elliotte Rusty Harold
 * @version 1.1b3
 */
public class BadHrefAttributeException extends XIncludeException {

    
    private static final long serialVersionUID = 4349654324645147403L;


    /**
     * <p>
     * Constructs a <code>BadHrefAttributeException</code> with 
     * the specified detail message. 
     * </p>
     *
     * @param message a string indicating the specific problem
     */
    public BadHrefAttributeException(String message) {
        super(message);
    }

    
    /**
     * <p>
     * Creates a new <code>BadHrefAttributeException</code> with a  
     * detail message and the URI of the document that caused 
     * the exception.
     * </p>
     * 
     * @param message a string indicating the specific problem
     * @param uri the URI of the document that caused this exception
     */
    public BadHrefAttributeException(String message, String uri) {
        super(message, uri);
    }

    
}