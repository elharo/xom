/* Copyright 2005 Elliotte Rusty Harold
   
   This library is free software; you can redistribute it and/or modify
   it under the terms of version 2.1 of the GNU Lesser General Public 
   License as published by the Free Software Foundation.
   
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
   GNU Lesser General Public License for more details.
   
   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the 
   Free Software Foundation, Inc., 59 Temple Place, Suite 330, 
   Boston, MA 02111-1307  USA
   
   You can contact Elliotte Rusty Harold by sending e-mail to
   elharo@metalab.unc.edu. Please include the word "XOM" in the
   subject line. The XOM home page is located at http://www.xom.nu/
*/
package nu.xom;

/**
 * <p>
 * Indicates that an XPath query returned a non-node-set.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.1a2
 * 
 */
public class XPathTypeException extends XPathException {
    
    
    private Object returnValue;

    
    XPathTypeException(String message) {
        super(message);
    }


    XPathTypeException(String message, Object returnValue) {
        super(message);
        this.returnValue = returnValue;
    }

    
    /**
     * <p>
     * This method probably returns the actual object returned by the 
     * query. However, XOM makes no guarantees about the type of this 
     * object. 
     * </p>
     * 
     * @return the query result
     */
    public Object getReturnValue() {
        return this.returnValue;
    }

    
}
