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

import java.util.HashMap;
import java.util.Map;

import org.jaxen.NamespaceContext;

/**
 *
 * <p>
 *  Provides namespace prefix bindings for use in an XPath expression.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.1d2
 *
 */
public class XPathContext {

    
    Map namespaces = new HashMap();
    
    
    String getURI(String prefix) {
        return (String) namespaces.get(prefix);
    }   
    
    
    public XPathContext(String prefix, String uri) {
        namespaces.put(prefix, uri);
    }
    
    
    private XPathContext() {}

    
    public void addNamespace(String prefix, String uri) {
        namespaces.put(prefix, uri);        
    }

    
    public static void makeNamespaceContext(Element element) {
        
        XPathContext context = new XPathContext();
        for (int i = 0; i < element.getNamespaceDeclarationCount(); i++) {
            String prefix = element.getNamespacePrefix(i);
            if (!"".equals(prefix)) {
                context.addNamespace(prefix, element.getNamespaceURI(prefix));
            }
        }
        // now add ancestors????
        
    }
    
    
    NamespaceContext getJaxenContext() {
        return new JaxenNamespaceContext();
    }
    
    
    private class JaxenNamespaceContext implements NamespaceContext {
        
        public String translateNamespacePrefixToUri(String prefix) {
            return (String) namespaces.get(prefix);
        }
        
    }

    
}
