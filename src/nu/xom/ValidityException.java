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
 *  A <code>ValidityException</code> is thrown to
 *  report a validity error in a document being parsed.
 *  These are not thrown by default, unless you specifically 
 *  request that the <code>Builder</code> validate.
 * </p>

 * @author Elliotte Rusty Harold
 * @version 1.0d21
 *
 */
public class ValidityException extends ParseException {

    /**
     * <p>
     *   Creates a new ValidityException.
     *  <p>
     */
    public ValidityException() {
        super();
    }

    /**
     * <p>
     * Creates a new <code>ValidityException</code> 
     * with a detail message and an underlying root cause.
     * </p>
     * 
     * @param message indicates the specific problem
     * @param cause the original cause of this exception
     */
    public ValidityException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * <p>
     * Creates a new <code>ValidityException</code> 
     * with a detail message and line and column numbers.
     * </p>
     * 
     * @param message indicates the specific problem
     * @param lineNumber the approximate line number 
     *     where the problem occurs
     * @param columnNumber the approximate column number
     *     where the problem occurs
     */
    public ValidityException(
        String message,
        int lineNumber,
        int columnNumber) {
        super(message, lineNumber, columnNumber);
    }

    /**
     * <p>
     * Creates a new <code>ValidityException</code> 
     * with a detail message, line and column numbers, 
     * and an underlying exception.
     * </p>
     * 
     * @param message indicates the specific problem
     * @param lineNumber the approximate line number 
     *     where the problem occurs
     * @param columnNumber the approximate column number 
     *     where the problem occurs
     * @param cause the original cause of this exception
     */
    public ValidityException(
        String message,
        int lineNumber,
        int columnNumber,
        Throwable cause) {
        super(message, lineNumber, columnNumber, cause);
    }

    /**
     * <p>
     * Creates a new <code>ValidityException</code> 
     * with a detail message.
     * </p>
     * 
     * @param message indicates the specific problem
     */
    public ValidityException(String message) {
        super(message);
    }

}
