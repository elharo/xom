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
 * 
 * <p>
 * A <code>NamespaceException</code> indicates some violation of the 
 * rules of Namespaces in XML. All namespace exceptions are not
 * violations of pure XML 1.0 without namespaces. For example, 
 * trying to set the name of an element to "98degrees" throws an 
 * <code>IllegalNameException</code> because it violates XML 1.0 
 * with or without namespaces. However, setting the same element's 
 * name to <code>test:test:degrees</code> throws a 
 * <code>NamespaceException</code> because 
 * <code>test:test:degrees</code> is a legal name in XML 1.0 but not a 
 * legal name in XML 1.0 + namespaces.
 * </p>
 * 
 * <p>
 * The <code>xml</code> prefix is not treated specially on attributes  
 * like <code>xml:base</code> and <code>xml:space</code>.
 * If used, these attributes must be specified 
 * like any other attribute in a namespace.
 * </p> 
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d23
 *
 */
public class NamespaceException extends XMLException {

    /**
     * <p>
     * Creates a new <code>NamespaceException</code> 
     * with a detail message.
     * </p>
     * 
     * @param message indicates the specific problem
     */
    public NamespaceException(String message) {
        super(message);
    }

    /**
     * <p>
     * Creates a new <code>NamespaceException</code> 
     * with a detail message and an underlying root cause.
     * </p>
     * 
     * @param message indicates the specific problem
     * @param cause the original cause of this exception
     */
    public NamespaceException(String message, Throwable cause) {
        super(message, cause);
    }


}
