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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;


class JaxenConnector extends BaseXPath {

    
    JaxenConnector(String expression) throws JaxenException {
        super(expression, new JaxenNavigator());
    }

    
    public List selectNodes(Object expression) throws JaxenException {
        
        List initial = super.selectNodes(expression);
        List result = new ArrayList(initial.size());
        Iterator iterator = initial.iterator();
        while (iterator.hasNext()) {
            Object next = iterator.next();
            if (next instanceof List) {
                List l = (List) next;
                result.addAll(l);
            }
            else {
                result.add(next);
            }
        } 
        return result; 
        
    }

    
} 

