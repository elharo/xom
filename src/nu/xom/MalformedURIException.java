// Copyright 2002-2004 Elliotte Rusty Harold
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
 *  A <code>MalformedURIException</code> signals that a string
 *  intended for use as a URI reference (typically a namespace
 *  name or a base URI) is not a syntactically correct URI
 *  according to 
 *  <a href="http://www.ietf.org/rfc/rfc2396.txt">RFC 2396</a>.
 * </p>

 * @author Elliotte Rusty Harold
 * @version 1.0d23
 * 
 *
 */
public class MalformedURIException extends IllegalDataException {

    
    /**
     * <p>
     * Creates a new <code>MalformedURIException</code> 
     * with a detail message.
     * </p>
     * 
     * @param message indicates the specific problem
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
     * @param message indicates the specific problem
     * @param cause the original cause of this exception
     */
    public MalformedURIException(String message, Throwable cause) {
        super(message, cause);
    }

    
}
