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
 * @version 1.1d6
 *
 */
public final class XPathContext {

    
    Map namespaces = new HashMap();
    
    
    /**
     * <p>
     * Creates a new XPath context that binds the specified prefix to 
     * the specified URI. The <code>xml</code> 
     * prefix is also bound to the URI 
     * <code>http://www.w3.org/XML/1998/namespace</code>.
     * </p>
     * 
     * @param prefix the prefix to bind
     * @param uri the namespace URI the prefix is bound to
     */
    public XPathContext(String prefix, String uri) {
        this();
        addNamespace(prefix, uri);
    }
    
    
    /**
     * <p>
     * Creates a new XPath context that binds the <code>xml</code> 
     * prefix to the URI 
     * <code>http://www.w3.org/XML/1998/namespace</code>.
     * </p>
     * 
     * @param prefix the prefix to bind
     * @param uri the namespace URI the prefix is bound to
     */
    public XPathContext() {
        addNamespace("xml", Namespace.XML_NAMESPACE);
    }

    
    /**
     * <p>
     * Binds the specified prefix to the specified namespace URI. 
     * If the prefix is already bound in this context, the new URI
     * replaces the old URI. Binding a prefix to null removes the
     * declaration. The binding of the <code>xml</code> prefix
     * may not be changed.
     * </p>
     * 
     * @param prefix the prefix to bind
     * @param uri the namespace URI the prefix is bound to
     * 
     * @throws NamesapceConflictException if the prefix is 
     *     <code>xml</code> and the URI is not
     *     <code>http://www.w3.org/XML/1998/namespace</code>
     * 
     */
    public void addNamespace(String prefix, String uri) {
        
        if ("xml".equals(prefix) 
          && !Namespace.XML_NAMESPACE.equals(uri)) {
            throw new NamespaceConflictException(
              "Wrong namespace URI for xml prefix: " + uri);
        }
        if ("".equals(uri)) uri = null;
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
