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
public final class XPathContext {

    
    Map namespaces = new HashMap();
    
    
    String getURI(String prefix) {
        return (String) namespaces.get(prefix);
    }   
    
    
    /**
     * <p>
     * Creates a new XPath context that bonds the prefix to the
     * URI.
     * </p>
     * 
     * @param prefix the prefix to bind
     * @param uri the namespace URI the prefix is bound to
     */
    public XPathContext(String prefix, String uri) {
        namespaces.put(prefix, uri);
    }
    
    
    private XPathContext() {}

    
    /**
     * <p>
     * Binds the specified prefix to the specified namespace URI. 
     * If the prefix is already bound in this context, ????.
     * </p>
     * 
     * @param prefix the prefix to bind
     * @param uri the namespace URI the prefix is bound to
     */
    public void addNamespace(String prefix, String uri) {
        namespaces.put(prefix, uri);        
    }

    
    // should this be a Node rather than an Element????
    /**
     * <p>
     * Creates a new XPath context that contains all the namespace
     * bindings <em>in scope</em> on the element. Changing 
     * the prefixes in scope on the element after the context
     * is returned does not change the context.
     * </p>
     * 
     * @param element the element whose namespace bindings are copied
     * 
     * @return all the namespace prefix mappings 
     *     in scope on the element
     */
    public static XPathContext makeNamespaceContext(Element element) {
        
        XPathContext context = new XPathContext();
        context.namespaces = element.getNamespacePrefixesInScope();
        return context;
        
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
