/* Copyright 2002-2005 Elliotte Rusty Harold
   
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

import java.util.List;
import java.util.Map;

import org.jaxen.JaxenException;
import org.jaxen.NamespaceContext;
import org.jaxen.XPath;

/**
 *
 * <p>
 *  The generic superclass for all the contents 
 *  of an XML document. There are exactly seven kinds of 
 *  nodes in XOM:
 * </p>
 * 
 * <ul>
 *   <li><code>Element</code></li>
 *   <li><code>Document</code></li>
 *   <li><code>Text</code></li>
 *   <li><code>Comment</code></li>
 *   <li><code>Attribute</code></li>
 *   <li><code>ProcessingInstruction</code></li>
 *   <li><code>DocType</code></li>
 * </ul>
 * 
 * <p>
 *   Every instance of <code>Node</code> is an  
 *   instance of one of these seven classes
 *   (including, possibly, one of their subclasses).
 * </p>
 * 
 * 
 * @author Elliotte Rusty Harold
 * @version 1.1d2
 *
 */
public abstract class Node {
    
    
    private ParentNode parent = null;
    
    /**
     * <p>
     * Creates a new <code>Node</code> object.
     * Can only be invoked by other members of
     * the <code>nu.xom</code> package.
     * </p>
     */
    Node() {}

     
    /**
     * <p>
     * Returns the XPath 1.0 string-value of this node.
     * </p>
     * 
     * @return the XPath 1.0 string-value of this node
     */
    public abstract String getValue();
    
    
    /**
     * 
     * <p>
     * Returns the document that contains this node,
     * or null if this node is not currently part of a document.
     * Each node belongs to no more than one document at a time.
     * If this node is a <code>Document</code>, then it returns 
     * this node.
     * </p>
     * 
     * @return the document this node is a part of
     */
    public final Document getDocument() {
        Node parent = this;
        while (parent != null && !(parent.isDocument())) {
            parent = parent.getParent();
        }
        return (Document) parent;
    }
    
    
    /**
     * 
     * <p>
     * Returns the base URI of this node as specified by 
     * <a href="http://www.w3.org/TR/xmlbase/" target="_top">XML 
     * Base</a>, or the empty string if this is not known. In most  
     * cases, this is the URL against which relative URLs in this node 
     * should be resolved.
     * </p>
     * 
     * <p>
     *  The base URI of a non-parent node is the base URI of the 
     *  element containing the node. The base URI of a document
     *  node is the URI from which the document was parsed,
     *  or which was set by calling <code>setBaseURI</code> on
     *  on the document. 
     * </p>
     *
     * <p>
     * The base URI of an element is determined as follows:
     * </p>
     * 
     * <ul>
     *   <li>
     *     If the element has an <code>xml:base</code> attribute,
     *     then the value of that attribute is 
     *     converted from an IRI to a URI, absolutized if possible,
     *     and returned.
     *   </li>
     *   <li>
     *      Otherwise, if any ancestor element of the element loaded
     *      from the same entity has an <code>xml:base</code> 
     *      attribute, then the value of that attribute from the
     *      nearest such ancestor is converted from an IRI to a URI, 
     *      absolutized if possible, and returned. 
     *      <em><code>xml:base</code> attributes from other entities are
     *      not considered.</em>
     *    </li>
     *    <li>
     *      Otherwise, if <code>setBaseURI()</code> has been invoked on 
     *      this element, then the URI most recently passed to that method 
     *      is absolutized if possible and returned.
     *    </li>
     *    <li>
     *      Otherwise, if the element comes from an externally
     *      parsed entity or the document entity, and the 
     *      original base URI has not been changed by invoking 
     *      <code>setBaseURI()</code>, then the URI of that entity is
     *      returned.
     *    </li>
     *    <li>
     *      Otherwise, (the element was created by a constructor
     *      rather then being parsed from an existing document), the
     *      base URI of the nearest ancestor that does have a base URI
     *      is returned. If no ancestors have a base URI, then the
     *      empty string is returned.
     *    </li>
     * </ul>
     * 
     * <p>
     *  Absolutization takes place as specified by the 
     *  <a target="_top" href="http://www.w3.org/TR/xmlbase/">XML 
     *  Base specification</a>. However, it is not always possible to
     *  absolutize a relative URI, in which case the empty string will 
     *  be returned. 
     * </p> 
     * 
     * @return the base URI of this node 
     */
    public String getBaseURI() {
        if (parent == null) return "";
        return parent.getBaseURI();
    }


    /**
     * 
     * <p>
     * Returns the node that contains this node,
     * or null if this node does not have a parent.
     * </p>
     * 
     * @return the element or document that most immediately
     *     contains this node
     */
    public final ParentNode getParent() {
        return this.parent; 
    }
    
    
    final void setParent(ParentNode parent) {
        this.parent = parent;   
    }
    
    
    /**
     * <p>
     * Removes this node from its parent so that it can be added 
     * to a different parent node or document. This method does nothing
     * if the node does not have a parent.
     * </p>
     * 
     * @throws XMLException if the parent refuses to detach this node
     */
    public void detach() {

        if (parent == null) return;
        else if (this.isAttribute()) {
            Element element = (Element) parent;
            element.removeAttribute((Attribute) this);
        }
        else {
            parent.removeChild(this);
        }

    }


    /**
     * <p>
     *  Returns the child of this node at the specified position.
     * </p>
     * 
     * @param position the index of the child node to return
     * 
     * @return the position<sup>th</sup> child node of this node
     * 
     * @throws IndexOutOfBoundsException if this node does not have children
     */
    public abstract Node getChild(int position);

    
    /**
     * <p>
     * Returns the number of children of this node.
     * This is always non-negative (greater than or equal to zero).
     * </p>
     * 
     * @return the number of children of this node
     */
    public abstract int getChildCount();

    
    /**
     * <p>
     * Returns a deep copy of this node with no parent,
     * that can be added to the current document or a different one.
     * </p>
     * 
     * <p>
     * Per Bloch, the <code>Cloneable</code>
     * interface is just a mess and should 
     * be avoided. However, I do not follow his suggestion of a copy
     * constructor exclusively because it is useful to be able to
     * copy a node without knowing its more specific type.
     * Ken Arnold agrees with this. It's more effective for 
     * subclasses that can return an instance of the subclass.
     * </p> 
     * 
     * @return a copy of this node without a parent
     */
    public abstract Node copy();        

    
    /**
     * <p>
     * Returns the actual XML form of this node, such as might be
     * copied and pasted from the original document. However, this 
     * does not preserve semantically insignificant details such as
     * white space inside tags or the use of empty-element tags vs.
     * start-tag end-tag pairs.
     * </p>
     * 
     * @return an XML representation of this node
     */
    public abstract String toXML(); 
    
    
    /**
     * <p>
     * Tests for node identity. That is, two 
     * <code>Node</code> objects are equal
     * if and only if they are the same object.
     * </p>
     * 
     * @param o the object compared for equality to this node
     * 
     * @return true if <code>o</code> is this node; false otherwise
     * 
     * @see java.lang.Object#equals(Object)
     */
    public final boolean equals(Object o) {
        return this == o; 
    }       

    
    /**
     * <p>
     * Returns a unique identifier for this node.
     * The value returned is the same as returned by 
     * <code>super.hashCode()</code>
     * because nodes use identity semantics. 
     * </p>
     * 
     * @return a probably unique identifier for this node
     * 
     * @see java.lang.Object#hashCode()
     */
    public final int hashCode() {
        return super.hashCode();    
    }
    
    
    /**
     * <p>
     * ????. This node is the context node.
     * </p>
     * 
     * <p>
     * No variables are bound. 
     * </p>
     * 
     * @param xpath the XPath location path to search for
     * @param namespaces a collection of namespace prefix bindings used in the 
     *     XPath expression
     * 
     * @return a list of all matched nodes; possibly empty
     */
    public Nodes query(String xpath, Map namespaces) {
        
        try {
            XPath xp = new JaxenConnector(xpath);
            xp.setNamespaceContext(new MapNamespaceContext(namespaces));
            List results = xp.selectNodes(this);
            return new Nodes(results);
        }
        catch (JaxenException ex) {
            throw new XPathException("XPath error???? " + ex.getMessage(), ex);
        }
        
    }

    
    /**
     * <p>
     * ????. This node is the context node.
     * </p>
     * 
     * <p>
     * No variables are bound. No namespace prefixes are bound.
     * </p>
     * 
     * @param xpath the XPath location path to search for
     * 
     * @return a list of all matched nodes; possibly empty
     * @throws XPathException if the query is syntactically incorrect
     */
    public Nodes query(String xpath) {
        
        try {
            XPath xp = new JaxenConnector(xpath);
            List results = xp.selectNodes(this);
            return new Nodes(results);
        }
        catch (JaxenException ex) {
            throw new XPathException("XPath error???? " + ex.getMessage(), ex);
        }
        
    }
    
    
    private static class MapNamespaceContext implements NamespaceContext {

        private Map context;
        
        MapNamespaceContext(Map context) {
            this.context = context;
        }
        
        
        public String translateNamespacePrefixToUri(String prefix) {
            return (String) context.get(prefix);
        }
        
    }

    
    // Methods to replace instanceof tests to improve performance
    boolean isElement() {
        return false;   
    }
            
    boolean isText() {
        return false;   
    }
            
    boolean isComment() {
        return false;   
    }
            
    boolean isProcessingInstruction() {
        return false;   
    }
            
    boolean isAttribute() {
        return false;   
    }
            
    boolean isDocument() {
        return false;   
    }
            
    boolean isDocType() {
        return false;   
    }
  
    
}
