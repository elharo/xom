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
 * Signals an attempt to set a namespace in a way that conflicts with  
 * an existing namespace; for instance, adding an attribute to
 * an element that has the same prefix as the element but maps it
 * to a different URI. 
 * </p>
 *
 * @author Elliotte Rusty Harold
 * @version 1.1b3
 */
public class NamespaceConflictException 
  extends WellformednessException {


    private static final long serialVersionUID = -3527557666747617537L;


    /**
     * <p>
     * Creates a new <code>NamespaceConflictException</code> 
     * with a detail message.
     * </p>
     *
     * @param message a string indicating the specific problem
     */
    public NamespaceConflictException(String message) {
        super(message);
    }

    
    /**
     * <p>
     * Creates a new <code>NamespaceConflictException</code> 
     * with a detail message and an underlying root cause.
     * </p>
     *
     * @param message a string indicating the specific problem
     * @param cause the original cause of this exception
     */
    public NamespaceConflictException(
      String message, Throwable cause) {
        super(message, cause);
    }


}
