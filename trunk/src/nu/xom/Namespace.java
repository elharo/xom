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

import nu.xom.Element;

/**
 * <p>
 * Represents a namespace in scope. It is used by XOM's 
 * XPath implementation for the namespace axis. However, it is not 
 * really part of the XOM data model. Namespace objects are only
 * created as needed when evaluating XPath.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.1d5
 *
 */
public final class Namespace extends Node {
    
    private String prefix;
    private String uri;
    
    /**
     * Namespace URI specified for <code>xml</code> prefix
     */
    public final static String XML_NAMESPACE 
      = "http://www.w3.org/XML/1998/namespace";

    
    Namespace(String prefix, String uri, Element parent) {
        this.prefix = prefix;
        this.uri = uri;
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
     * @return never returns because document type declarations do not 
     *     have children. Always throws an exception.
     * 
     * @param position the index of the child node to return
     * 
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
     * @return a deep copy of this <code>Namespace</code> 
     *     that is not part of a document
     */
    public Node copy() {
        return new Namespace(prefix, uri, null);
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
    
    
    boolean isNamespace() {
        return true;
    }
    
    
}