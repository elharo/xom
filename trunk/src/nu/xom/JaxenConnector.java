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

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.jaxen.BaseXPath;
import org.jaxen.FunctionContext;
import org.jaxen.JaxenException;
import org.jaxen.XPathFunctionContext;


/**
 * 
 * @author Elliotte Rusty Harold
 * @version 1.1.1b1
 *
 */
class JaxenConnector extends BaseXPath {

    
    private static final long serialVersionUID = 9025734269448515308L;
    
    private static FunctionContext functionContext = new XPathFunctionContext(false);

    
    JaxenConnector(String expression) throws JaxenException {
        super(expression, new JaxenNavigator());
        // possible thread-safety issue????
        this.setFunctionContext(functionContext);
    }

    
    public List selectNodes(Object expression) throws JaxenException {
        
        List result = super.selectNodes(expression);
        // Text objects are returned wrapped in a List.
        // We need to unwrap them here.
        ListIterator iterator = result.listIterator();
        while (iterator.hasNext()) {
            Object next = iterator.next();
            if (next instanceof List) {
                List list = (List) next;
                // replace the list with the first item in the list
                iterator.set(list.get(0));
                // insert any subsequent Text objects into the list
                if (list.size() > 1) {
                    Iterator texts = list.listIterator(1);
                    while (texts.hasNext()) {
                        iterator.add(texts.next());
                    }
                }
            }
        } 
        return result;
        
    }

    
} 

