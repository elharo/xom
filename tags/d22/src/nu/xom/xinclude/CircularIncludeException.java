// Copyright 2002, 2003 Elliotte Rusty Harold
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

package nu.xom.xinclude;

/**
 * <p>
 * A <code>CircularIncludeException</code> is thrown when
 * an included document attempts to include itself or 
 * one of its ancestor documents.
 * </p>
 *
 * @author Elliotte Rusty Harold
 * @version 1.0d22
 */
public class CircularIncludeException extends XIncludeException {

    /**
     * <p>
     * Constructs a <code>CircularIncludeException</code> 
     * with <code>null</code>
     * as its error detail message.
     * </p>
     */
    public CircularIncludeException() {}

    /**
     * <p>
     * Constructs a <code>CircularIncludeException</code> with  
     * the specified detail message. The error message string 
     * <code>message</code> can be retrieved by the 
     * <code>{@link java.lang.Throwable#getMessage}</code>
     * method of class <code>java.lang.Throwable</code>.
     * </p>
     * 
     * @param message the detail message
     */
    public CircularIncludeException(String message) {
        super(message);
    }

    /**
     * <p>
     * Creates a new <code>CircularIncludeException</code> with a detail 
     * message, line and column numbers, and a URI of the document
     * that caused the exception.
     * </p>
     * 
     * @param message indicates the specific problem
     * @param uri the URI of the document that caused this exception
     */
    public CircularIncludeException(String message, String uri) {
        super(message, uri);
    }

}