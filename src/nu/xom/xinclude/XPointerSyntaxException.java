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
 * <code>XPointerException</code> is the generic superclass
 * for all checked exceptions that may be thrown as a result
 * of a violation of the XPointer grammar.
 * </p>
 *
 * @author Elliotte Rusty Harold
 * @version 1.0a1
 */
class XPointerSyntaxException extends XPointerException {

    /**
     * <p>
     * Constructs an <code>XPointerSyntaxException</code> with the  
     * specified detail message.
     * </p>
     * 
     * @param message the detail message.
     */
    public XPointerSyntaxException(String message) {
        super(message);
    }
    
    /**
     * <p>
     * Constructs an <code>XPointerSyntaxException</code> with the 
     * specified detail message and initial cause. 
     * </p>
     *
     * @param message the detail message
     * @param cause the initial exception which caused this 
     *     <code>XPointerSyntaxException</code>
     */
    public XPointerSyntaxException(String message, Throwable cause) {
        super(message, cause);
    }

}
