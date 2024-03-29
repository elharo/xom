/* Copyright 2004 Elliotte Rusty Harold
   
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
 * Indicates that the <code>encoding</code> attribute 
 * is not an EncName as defined in the 
 * <a href="https://www.w3.org/TR/REC-xml/#charencoding" 
 * target="_top">XML specification, section 4.3.3, rule 81</a>.
 * </p>
 *
 * @author Elliotte Rusty Harold
 * @version 1.3.9
 */
public class BadEncodingAttributeException extends XIncludeException {

    private static final long serialVersionUID = -8856360955880988486L;


    /**
     * <p>
     * Constructs a <code>BadEncodingAttributeException</code> with 
     * the specified detail message. 
     * </p>
     *
     * @param message a string indicating the specific problem
     */
    public BadEncodingAttributeException(String message) {
        super(message);
    }

    
    /**
     * <p>
     * Creates a new <code>BadEncodingAttributeException</code> with a  
     * detail message and the URI of the document that caused 
     * the exception.
     * </p>
     * 
     * @param message a string indicating the specific problem
     * @param uri the URI of the document that caused this exception
     */
    public BadEncodingAttributeException(String message, String uri) {
        super(message, uri);
    }

    
}