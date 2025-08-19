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
 * Indicates that a string
 * intended for use as a URI or URI reference (typically a namespace
 * name or a base URI) is not syntactically correct  according to
 * <a href="https://www.ietf.org/rfc/rfc3986.txt"
 * target="_top">RFC 3986</a>.
 * </p>
 *
 * @author Elliotte Rusty Harold
 * @version 1.3.9
 *
 */
public class MalformedURIException extends IllegalDataException {


    private static final long serialVersionUID = 6510452627371611735L;


    /**
     * <p>
     * Creates a new <code>MalformedURIException</code>
     * with a detail message.
     * </p>
     *
     * @param message a string indicating the specific problem
     */
    public MalformedURIException(String message) {
        super(message);
    }

    
    /**
     * <p>
     * Creates a new <code>MalformedURIException</code>
     * with a detail message and an underlying root cause.
     * </p>
     *
     * @param message a string indicating the specific problem
     * @param cause the original cause of this exception
     */
    public MalformedURIException(String message, Throwable cause) {
        super(message, cause);
    }

    
}
