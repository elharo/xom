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
 * Indicates an attempt to assign a name that is not a legal XML name.
 * For example, this might be a name that begins with a digit
 * such as <code>7pins</code> or a name that contains an asterisk such
 * as <code>pt*</code>. In some contexts, this also includes names that
 * are not legal non-colonized names as defined by <cite>Namespaces in
 * XML</cite>.
 * </p>
 *
 * @author Elliotte Rusty Harold
 * @version 1.1b3
 */
public class IllegalNameException extends IllegalDataException {

    
    private static final long serialVersionUID = -5050380625643506613L;


    /**
     * <p>
     * Creates a new <code>IllegalNameException</code>
     * with a detail message.
     * </p>
     *
     * @param message a string indicating the specific problem
     */
    public IllegalNameException(String message) {
        super(message);
    }

    
    /**
     * <p>
     * Creates a new <code>IllegalNameException</code>
     * with a detail message and an underlying root cause.
     * </p>
     *
     * @param message a string indicating the specific problem
     * @param cause the original cause of this exception
     */
    public IllegalNameException(String message, Throwable cause) {
        super(message, cause);
    }


}
