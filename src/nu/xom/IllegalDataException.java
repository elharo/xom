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
 * Indicates an attempt to
 * set some value to malformed content; for instance
 * by adding a string containing a null or a vertical tab
 * to a text node, or using white space in an element name.
 * </p>
 *
 * @author Elliotte Rusty Harold
 * @version 1.1b3
 *
 */
public class IllegalDataException extends WellformednessException {


    private static final long serialVersionUID = 5116683358318890040L;
    private String data;
    
    
    /**
     * <p>
     * Creates a new <code>IllegalDataException</code>
     * with a detail message.
     * </p>
     *
     * @param message a string indicating the specific problem
     */
    public IllegalDataException(String message) {
        super(message);
    }

    
    /**
     * <p>
     * Creates a new <code>IllegalDataException</code>
     * with a detail message and an underlying root cause.
     * </p>
     *
     * @param message a string indicating the specific problem
     * @param cause the original cause of this exception
     */
    public IllegalDataException(String message, Throwable cause) {
        super(message, cause);
    }
    
    
    /**
     * <p>
     * Stores the illegal text that caused this exception.
     * </p>
     *
     * @param data the illegal data that caused this exception
     */
    public void setData(String data) {
        this.data = data;
    }

    
    /**
     * <p>
     * Returns a string containing the actual illegal text that
     * caused this exception.
     * </p>
     *
     * @return the syntactically incorrect data that caused
     *     this exception
     */
    public String getData() {
        return data;
    }

}
