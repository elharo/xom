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

package nu.xom;

/**
 * <p>
 *  An <code>IllegalNameException</code> indicates an attempt to
 *  assign a name that is not a legal XML name.
 *  For example, this might be a name that begins with a digit 
 * such as <code>7pins</code> or a name that contains an asterisk such as 
 * <code>pt*</code>. In some contexts, this also includes names that are
 * not legal non-colonized names as defined by <cite>Namespaces in XML</cite>.
 * </p>

 * @author Elliotte Rusty Harold
 * @version 1.0d7
 * 
 *
 */

public class IllegalNameException extends WellformednessException {


    /**
     * <p>
     * Creates a new <code>IllegalNameException</code>.
     * </p>
     */
    public IllegalNameException() {
        super();
    }

    /**
     * <p>
     * Creates a new <code>IllegalNameException</code> with a detail message.
     * </p>
     * 
     * @param message indicates the specific problem
     */
    public IllegalNameException(String message) {
        super(message);
    }

    /**
     * <p>
     * Creates a new <code>IllegalNameException</code> with a detail message
     * and an underlying root cause.
     * </p>
     * 
     * @param message indicates the specific problem
     * @param ex the original cause of this exception
     */
    public IllegalNameException(String message, Throwable ex) {
        super(message, ex);
    }

}