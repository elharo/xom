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

package nu.xom.xinclude;

/**
 * <p>
 * A <code>NoIncludeLocationException</code> is thrown when
 * an <code>xinclude:include</code> element has neither an 
 * <code>href</code> nor an <code>xpointer</code> attribute.
 * </p>
 *
 * @author Elliotte Rusty Harold
 * @version 1.0d23
 */
public class NoIncludeLocationException extends XIncludeException {

    
    /**
     * <p>
     * Constructs a <code>NoIncludeLocationException</code> with the 
     * specified message. The message string <code>message</code> 
     * can be retrieved later by the 
     * <code>{@link java.lang.Throwable#getMessage}</code>
     * method of class <code>java.lang.Throwable</code>.
     * </p>
     *
     * @param message indicates the specific problem
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
     * @param message indicates the specific problem
     * @param cause the initial exception which caused this 
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
     * @param message indicates the specific problem
     * @param uri the URI of the document that caused this exception
     */
    public NoIncludeLocationException(String message, String uri) {
        super(message, uri);
    }
    
    
}