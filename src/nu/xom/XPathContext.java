/* Copyright 2005, 2006, 2012, 2019 Elliotte Rusty Harold
   
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

import java.util.HashMap;
import java.util.Map;

import org.jaxen.NamespaceContext;

/**
 * <p>
 * Provides namespace prefix bindings for use in an XPath expression.
 * </p>
 *
 * @author Elliotte Rusty Harold
 * @version 1.3.1
 */
public final class XPathContext {

    
    private Map<String, String> namespaces = new HashMap<String, String>();
    
    
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
     * @throws NamespaceConflictException if the prefix is
     *     <code>xml</code> and the URI is not
     *     <code>http://www.w3.org/XML/1998/namespace</code> or the
     *     prefix is the empty string
     * @throws NullPointerException if the prefix is null
     */
    public void addNamespace(String prefix, String uri) {
        
        if ("xml".equals(prefix) 
          && !Namespace.XML_NAMESPACE.equals(uri)) {
            throw new NamespaceConflictException(
              "Wrong namespace URI for xml prefix: " + uri);
        }
        if ("".equals(uri)) uri = null;
        if (prefix == null) {
            throw new NullPointerException("Prefixes used in XPath expressions cannot be null");
        }
        else if ("".equals(prefix)){
            throw new NamespaceConflictException(
              "XPath expressions do not use the default namespace");
        }
        Verifier.checkNCName(prefix);
        
        // should there be a separate remove method????
        if (uri == null) {
            namespaces.remove(prefix);
        }
        else {
            namespaces.put(prefix, uri);
        }
        
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
            return lookup(prefix);
        }
        
    }

    /**
     * Returns the namespace URI associated with a specified prefix in
     * this context. It returns null if this prefix is not bound
     * to a namespace in this context. The prefix is not checked
     * for validity. If you pass in a prefix that is not an NCName,
     * this method simply returns null.
     *
     * @param prefix the prefix to look up
     * @return the namespace URI associated with the specified prefix in
     *          this context or null
     */
    public String lookup(String prefix) {
        if ("".equals(prefix)) return null;
        return namespaces.get(prefix);
    }

    
}
