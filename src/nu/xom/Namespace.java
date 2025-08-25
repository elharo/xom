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
 * Represents a namespace in scope. It is used by XOM's
 * XPath implementation for the namespace axis. However, it is not
 * really part of the XOM data model. Namespace objects are only
 * created as needed when evaluating XPath. While a namespace node has
 * a parent element (which may be null), that element does not know
 * about these namespace nodes and cannot remove them. (This is an
 * inconsistency in the XPath data model, and is shared with attributes
 * which also have parents but are not children.)
 * </p>
 *
 * @author Elliotte Rusty Harold
 * @version 1.3.0
 */
public final class Namespace extends Node {
    
    private final String prefix;
    private final String uri;
    
    /**
     * Namespace URI specified for <code>xml</code> prefix.
     */
    public final static String XML_NAMESPACE 
      = "http://www.w3.org/XML/1998/namespace";

    
    /**
     * <p>
     * Create a new namespace node.
     * </p>
     *
     * @param prefix the prefix for the namespace; may be the empty
     *     string or a non-colonized name
     * @param URI the namespace URI
     * @param parent the element that possesses this namespace node
     * @throws IllegalNameException if
     *  <ul>
     *      <li>The prefix is <code>xmlns</code>.</li>
     *      <li>The prefix is not the empty string, and the URI is 
     *          null or the empty string.</li>
     * </ul>
     * @throws MalformedURIException if <code>URI</code> is
     *     not an RFC 3986 URI reference
     * @throws NamespaceConflictException if
     *  <ul>
     *      <li>The prefix is the empty string, and the URI is 
     *          null or the empty string.</li>
     *      <li>The prefix is <code>xml</code>, and the URI is not
     *          <code>http://www.w3.org/XML/1998/namespace</code>.</li>
     *      <li>The prefix is not <code>xml</code>, and the URI is
     *          <code>http://www.w3.org/XML/1998/namespace</code>.</li>
     * </ul>
     */
    public Namespace(String prefix, String URI, Element parent) {
        
        if (prefix == null) prefix = "";
        else if ("xmlns".equals(prefix)) {
            throw new IllegalNameException(
              "The xmlns prefix may not be bound to a URI.");
        }
        else if ("xml".equals(prefix)) {
            if (! XML_NAMESPACE.equals(URI) ) {
                throw new NamespaceConflictException(
                  "The prefix xml can only be bound to the URI "
                  + "http://www.w3.org/XML/1998/namespace");
            }
        }
        
        if (prefix.length() != 0) Verifier.checkNCName(prefix);
        
        if (URI == null) URI = "";
        else if (URI.equals(XML_NAMESPACE)) {
            if (! "xml".equals(prefix)) {
                throw new NamespaceConflictException(
                  "The URI http://www.w3.org/XML/1998/namespace can "
                  + "only be bound to the prefix xml");    
            }
        }
        
        if (URI.length() == 0) { // faster than "".equals(uri)
            if (prefix.length() != 0) {
                throw new NamespaceConflictException(
                 "Prefixed elements must have namespace URIs."
                );  
            }
        }
        else Verifier.checkAbsoluteURIReference(URI);
        
        this.prefix = prefix;
        this.uri = URI;
        super.setParent(parent);
    }
    
    
    /**
     * <p>
     * Returns the namespace prefix, or the empty string if this node
     * is the default namespace.
     * </p>
     *
     * @return the namespace prefix
     */
    public String getPrefix() {
        return prefix;
    }


    /**
     * <p>
     * Returns the namespace URI.
     * </p>
     *
     * @return the namespace URI
     */
    public String getValue() {
        return uri;
    }


     /**
      * <p>
      * Throws <code>IndexOutOfBoundsException</code> because
      * namespaces do not have children.
      * </p>
      *
      * @param position the index of the child node to return
      * @return never returns because document type declarations do not
     *     have children. Always throws an exception.
      * @throws IndexOutOfBoundsException because document type declarations
     *     do not have children
      */
    public Node getChild(int position) {
        throw new IndexOutOfBoundsException(
          "Namespaces do not have children");
    }


    /**
     * <p>
     * Returns 0 because namespaces do not have
     * children.
     * </p>
     *
     * @return zero
     */
    public int getChildCount() {
        return 0;
    }


    /**
     * <p>
     *   Returns a copy of this namespace which has
     *   the same prefix and URI, but no parent.
     * </p>
     *
     * @return a copy of this <code>Namespace</code>
     *     that is not part of a document
     */
    public Namespace copy() {
        return new Namespace(prefix, uri, null);
    }


    /**
     * <p>
     * Removes this namespace node from its parent.
     * </p>
     *
     * @see nu.xom.Node#detach()
     */
    public void detach() {
        super.setParent(null);
    }
    
    
    /**
     * <p>
     *  Returns a string containing the actual XML
     *  form of the namespace declaration represented
     *  by this object. For example,
     *  <code>xmlns:pre="http://www.example.org/"</code>.
     * </p>
     *
     * @return a <code>String</code> containing
     *      an XML namespace declaration
     */
    public String toXML() {
        String colon = prefix.equals("") ? "" : ":";
        return "xmlns" + colon + prefix + "=\"" + uri + "\"";
    }
    
    
    /**
     * <p>
     * Returns a string form of the
     * <code>Namespace</code> suitable for debugging
     * and diagnosis. It deliberately does not return
     * an actual XML namespace declaration.
     * </p>
     *
     * @return a string representation of this object
     */
    public String toString() {
        return "[Namespace: " + this.toXML() + "]";
    }
    
    
}
