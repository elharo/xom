// Copyright 2002-2004 Elliotte Rusty Harold
// 
// This library is free software; you can redistribute 
// it and/or modify it under the terms of version 2.1 of 
// the GNU Lesser General Public License as published by  
// the Free Software Foundation.
// 
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
// GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General 
// Public License along with this library; if not, write to the 
// Free Software Foundation, Inc., 59 Temple Place, Suite 330, 
// Boston, MA  02111-1307  USA
// 
// You can contact Elliotte Rusty Harold by sending e-mail to
// elharo@metalab.unc.edu. Please include the word "XOM" in the
// subject line. The XOM home page is temporarily located at
// http://www.cafeconleche.org/XOM/  but will eventually move
// to http://www.xom.nu/

package nu.xom;

/**
 *
 * <p>
 *  This is the generic superclass for all the contents 
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
 * @version 1.0a1
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
     * @return the <code>Document</code> which this node is a part of
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
     * Returns the base URI of  this node as specified by 
     * <a href="http://www.w3.org/TR/xmlbase/">XML Base</a>, 
     * or null if this is not known. In most cases, this is the URL
     * against which relative URLs in this node should be resolved.
     * </p>
     * 
     * <p>
     * Currently, the value of the base URI is determined as follows:
     * </p>
     * 
     * <ul>
     *   <li>If node has an <code>xml:base</code> attribute,
     *        then the value of that attribute is 
     *        converted from an IRI to a URI, absolutized if possible,  
     *        and returned.
     *    </li>
     *   <li>Otherwise, if any ancestor element of this node loaded from
     *        the same entity has an <code>xml:base</code> attribute,
     *        then the value of that attribute from the nearest such 
     *        ancestor is converted from an IRI to a URI, 
     *        absolutized if possible, and returned. 
     *        <code>xml:base</code> attributes from other entities are
     *        not considered.
     *    </li>
     *    <li>
     *      Otherwise, if this node was part of an externally
     *      parsed entity or the document entity, or of a node
     *      on which <code>setBaseURI()</code> was called,
     *       then the URI of that entity is returned.
     *    </li>
     * </ul>
     * 
     * <p>
     *   Absolutization takes place as specified by the XML 
     *   Base specification. However, it is not always possible to
     *   absolutize a relative URI, in which case only the relative 
     *   URI will be returned. 
     * </p> 
     * 
     * <!--
     * <p>
     *   The URIs returned are actually IRIs that can contain
     *   URI-illegal characters such as &Omega; and &eacute;.
     *   This is in keeping with the XML Base specification.
     *   However, the value returned by this method may need to be 
     *   further escaped before being used in other systems that 
     *   expect URIs, not IRIs.
     * </p> -->
     * 
     * @return the base URI of this node 
     */
    public String getBaseURI() {
        
        ParentNode parent = this.getParent();
        if (parent == null) return null;
        return parent.getBaseURI();
        
    }


    /**
     * 
     * <p>
     * Returns the node that contains this node,
     * or null if this node does not have a parent.
     * </p>
     * 
     * @return the parent <code>Element</code> 
     *         or <code>Document</code> that contains this node 
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
     * to a different parent node or document.
     * </p>
     * 
     * @throws XMLException if subclass constraints prohibit this
     *     node from being detached
     */
    public final void detach() {

        checkDetach();
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
     * Subclasses can override this method to perform additional 
     * checks beyond what XML 1.0 requires. For example, an 
     * <code>HTMLDocument</code> subclass might not allow the 
     * body element to be detached.
     * </p>
     * 
     * @throws XMLException if local constraints in a subclass do not
     *     allow this node to be detached
     */
    protected void checkDetach() {
    }


    /**
     * <p>
     *  Returns the index<sup>th</sup>
     *   child of this node.
     * </p>
     * 
     * @param position the index of the child node to return
     * 
     * @return the position<sup>th</sup> child node of this node
     * 
     * @throws IndexOutOfBoundsException if this node does not have children.
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
     * that can be added to this document or a different one.
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
     * @return a copy of this node without a parent and unattached to 
     *     any document
     */
    public abstract Node copy();        

    
    /**
     * <p>
     * Returns the actual XML form of this node, such as might be
     * copied and pasted from the original document.
     * </p>
     * 
     * @return a String containing an XML representation of this node
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

    
    // Simple methods to replace instanceof tests
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
