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

import java.util.Iterator;
import java.util.List;

import org.jaxen.JaxenException;
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
     * Returns the root of the subtree in which this node is found,
     * whether that's a document or an element.
     * </p>
     * 
     * @return the document this node is a part of
     */
    final Node getRoot() {
        
        Node parent = this.getParent();
        if (parent == null) {
            return this;
        }
        while (parent.getParent() != null) {
            parent = parent.getParent();
        }
        return parent;
        
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
    
    // ???? how does document order work with
    // nodes selected from multiple documents
    // document(url1) | document(url2) ?
    
    /**
     * <p>
     * Returns the nodes selected by the XPath expression in the 
     * context of this node in document order as defined in XSLT. 
     * All namespace prefixes used in the 
     * expression should be bound to namespace URIs by the 
     * second argument. 
     * </p>
     * 
     * <p>
     * Note that XPath expressions operate on the XPath data model,
     * not the XOM data model. XPath counts all adjacent 
     * <code>Text</code> objects as a single text node, and does not
     * consider empty <code>Text</code> objects. For instance, an 
     * element that has exactly three text children in XOM, will
     * have exactly one text child in XPath, whose value is the 
     * concatenation of all three XOM <code>Text</code> objects. 
     * </p>
     * 
     * <p>
     * You can use XPath expressions that use the namespace axis.
     * However, namespace nodes are never returned. If an XPath 
     * expression only selects namespace nodes, then this method will
     * return an empty list.
     * </p>
     * 
     * <p>
     * No variables are bound. 
     * </p>
     * 
     * <p>
     * The context position is ????. The context size is ????. 
     * </p>
     * 
     * @param xpath the XPath expression to evaluate
     * @param namespaces a collection of namespace prefix bindings used in the 
     *     XPath expression
     * 
     * @return a list of all matched nodes; possibly empty
     * 
     * @throws XPathException if there's a syntax error in the 
     *     expression, the query returns something other than
     *     a node-set, or the query returns a node-set containing a
     *     namespace node
     * 
     */
    public final Nodes query(String xpath, XPathContext namespaces) {
        
        if (this.isDocType()) {
            throw new XPathException("Can't use XPath on a DocType");
        }
        DocumentFragment frag = null;
        
        Node root = getRoot();
        if (! root.isDocument()) {
            frag = new DocumentFragment();
            frag.appendChild(root);
        }
        
        try {
            XPath xp = new JaxenConnector(xpath);
            if (namespaces != null) {
                xp.setNamespaceContext(namespaces.getJaxenContext());
            }
            List results = xp.selectNodes(this);
            Iterator iterator = results.iterator();
            while (iterator.hasNext()) {
                Object o = iterator.next();
                if (o instanceof JaxenNavigator.XPathNamespaceNode) {
                    throw new XPathException("XPath expression " 
                      + xpath + "returned namespace nodes, which XOM "
                      + "does not support.");
                }
                else if (o instanceof DocumentFragment) {
                    iterator.remove(); // throw exception????
                }
                else if (!(o instanceof Node)) {
                    throw new XPathException("XPath expression " 
                      + xpath + " did not return a node-set.");
                }
            }
            
            return sortResults(results);
        }
        catch (XPathException ex) {
            ex.setXPath(xpath);
            throw ex;
        }
        catch (JaxenException ex) {
            XPathException xpe = new XPathException("XPath error: " + ex.getMessage(), ex);
            xpe.setXPath(xpath);
            throw xpe;
        }
        catch (RuntimeException ex) {
            XPathException xpe = new XPathException("XPath error: " + ex.getMessage(), ex);
            xpe.setXPath(xpath);
            throw xpe;
        }
        finally {
            if (frag != null) frag.removeChild(0);
        }
        
    }


    // recursively descend through document; in document
    // order, and add results as they are found
    private Nodes sortResults(List in) {

        Node root = this.getRoot();
        if (in.size() > 1 && root instanceof ParentNode) {
            Nodes out = new Nodes();
            process(in, out, (ParentNode) root);
            return out;
        }
        else {
            return new Nodes(in);
        }
    }


    private static void process(List in, Nodes out, ParentNode parent) {

        if (in.isEmpty()) return;
        if (in.contains(parent)) {
            out.append(parent);
            in.remove(parent);
            if (in.isEmpty()) return;
        }
        
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            Node child = parent.getChild(i);
            if (child.isElement()) {
                Element element = (Element) child;
                for (int a = 0; a < element.getAttributeCount(); a++) {
                    Attribute att = element.getAttribute(a);
                    if (in.contains(att)) {
                        out.append(att);
                        in.remove(att);
                        if (in.isEmpty()) return;
                    }
                }
                process(in, out, element);
            }
            else {
                if (in.contains(child)) {
                    out.append(child);
                    in.remove(child);
                    if (in.isEmpty()) return;
                }
            }
        }
        
    }


    private static void process(List in, Nodes out, Text text) {

        Iterator iterator = in.iterator();
        while (iterator.hasNext()) {
            Object next = iterator.next();
            if (next instanceof JaxenNavigator.XOMList) {
                List l = (List) next;
                if (l.contains(text)) {
                    out.append(text);
                    l.remove(text);
                    if (l.isEmpty()) in.remove(l);
                    return;
                }
            }
        }
        
    }


    /**
     * <p>
     * Returns the nodes selected by the XPath expression in the 
     * context of this node in document order as defined by XSLT. 
     * This XPath expression must not contain
     * any namespace prefixes.
     * </p>
     * 
     * <p>
     * No variables are bound. No namespace prefixes are bound.
     * </p>
     * 
     * @param xpath the XPath expression to evaluate
     * 
     * @return a list of all matched nodes; possibly empty
     * 
     * @throws XPathException if there's a syntax error in the 
     *     expression; or the query returns something other than
     *     a node-set
     */
    public final Nodes query(String xpath) {
        return query(xpath, null);
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
