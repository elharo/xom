// Copyright 2003 Elliotte Rusty Harold
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
 * <code>XPointerResourceException</code> is a checked exception thrown
 * when no XPointer part identifies a subresource. This corresponds to 
 * <a href="http://www.w3.org/TR/xptr-framework/#scheme">section 3.3 
 * of the XPointer Framework</a> which states, 
 * "If no pointer part identifies subresources, it is an error."
 * </p>
 *
 * @author Elliotte Rusty Harold
 * @version 1.0d21
 */
class XPointerResourceException extends XPointerException {

    /**
     * <p>
     * Constructs an <code>XPointerResourceException</code> 
     * with <code>null</code> as its error detail message.
     * </p>
     */
    public XPointerResourceException() {}

    /**
     * <p>
     * Constructs an <code>XPointerResourceException</code> with the 
     * specified detail message. The error message string  
     * <code>message</code> can later be retrieved by the 
     * <code>{@link java.lang.Throwable#getMessage}</code>
     * method of class <code>java.lang.Throwable</code>.
     * </p>
     * 
     * @param message the detail message.
     */
    public XPointerResourceException(String message) {
        super(message);
    }

    /**
     * <p>
     * Constructs an <code>XPointerResourceException</code> with the 
     * specified detail message and initial cause. 
     * </p>
     *
     * @param message the detail message
     * @param cause the initial exception which caused this 
     *     <code>XPointerException</code>
     */
    public XPointerResourceException(String message, Throwable cause) {
        super(message, cause);
    }

}