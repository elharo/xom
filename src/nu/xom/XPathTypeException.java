/* Copyright 2005 Elliotte Rusty Harold
   
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
 * Indicates that an XPath query returned a non-node-set.
 * </p>
 *
 * @author Elliotte Rusty Harold
 * @version 1.2.4
 *
 */
public class XPathTypeException extends XPathException {
    
    private static final long serialVersionUID = 1247817719683497718L;
    
    private final Object returnValue;


    XPathTypeException(Object returnValue) {
        // Irrelevant empty message because we override getMessage() below
        super("");
        this.returnValue = returnValue;
    }

    
    /**
     * <p>
     * This method usually returns the actual object returned by the
     * query. However, XOM makes no guarantees about the type of this
     * object.
     * </p>
     *
     * @return the query result
     */
    public Object getReturnValue() {
        return this.returnValue;
    }

    public String getMessage() {
        String xpath = getXPath();
        String type = returnValue.getClass().getName();
        if (xpath == null) {
            return "XPath expression returned a " + type + " instead of a node-set.";
        } else {
            return "XPath expression " + xpath + " returned a " + type + " instead of a node-set.";
        }
    }
    
    
}
