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

import java.util.ArrayList;
import java.util.List;


/**
 * 
 * <p>
 * The generic superclass for nodes that have children. 
 * Not counting subclasses, there are exactly two such classes in XOM:
 * </p>
 * 
 * <ul>
 *   <li><code>Document</code></li>
 *   <li><code>Element</code></li>
 * </ul>
 * 
 * <p>
 * This class provides methods to add and remove child nodes.
 * </p>
 * 
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0b7
 *
 */
public abstract class ParentNode extends Node {

    List   children; 
    String actualBaseURI;

    /**
     * <p>
     * Creates a new <code>ParentNode</code> object.
     * Can only be invoked by other members of
     * the <code>nu.xom</code> package.
     * </p>
     * 
     */
    ParentNode() {}


    /**
     * <p>
     * Returns the number of child nodes this node contains.
     * This is always greater than or equal to 0.
     * </p>
     * 
     * @return the number of children of this node
     */
    public int getChildCount() {
        if (children == null) return 0;
        return children.size(); 
    }

    
    /**
     * <p>
     * Inserts a child node at the specified position.
     * The child node previously at that position (if any) 
     * and all subsequent child nodes are moved up by one.
     * That is, when inserting a node at 2, the old node at 2
     * is moved to 3, the old child at 3 is moved to 4, and so 
     * forth. Inserting at position 0 makes the child the first 
     * child of this node. Inserting at the position 
     * <code>getChildCount()</code> makes the child the 
     * last child of the node.
     * </p>
     * 
     * <p>
     * All the other methods that add a node to the tree ultimately
     * invoke this method.
     * </p>
     * 
     * @param position where to insert the child
     * @param child the node to insert
     * 
     * @throws IllegalAddException if this node cannot have a child of
     *     the argument's type
     * @throws MultipleParentException if <code>child</code> already 
     *     has a parent
     * @throws NullPointerException if <code>child</code> is null
     * @throws IndexOutOfBoundsException if the position is negative or 
     *     greater than the number of children of this node
     */
    public void insertChild(Node child, int position) {
        _insertChild(child, position);
    }
    
    
    // because this method is called from Document constructor and
    // constructors should not call overridable methods
    final void _insertChild(Node child, int position) {
        insertionAllowed(child, position);
        fastInsertChild(child, position);
    }


    void fastInsertChild(Node child, int position) {
        if (children == null) children = new ArrayList(1);
        children.add(position, child);
        child.setParent(this);
    }


    abstract void insertionAllowed(Node child, int position);

    
    /**
     * <p>
     * Appends a node to the children of this node.
     * </p>
     * 
     * @param child node to append to this node
     * 
     * @throws IllegalAddException if this node cannot have children 
     *     of this type
     * @throws MultipleParentException if child already has a parent
     * @throws NullPointerException if <code>child</code> is null
     * 
     */
    public void appendChild(Node child) {
        insertChild(child, getChildCount());
    }

    
    /**
     *<p>
     * Returns the child of this node at the specified position.
     * Indexes begin at 0 and count up to one less than the number
     * of children in this node.
     * </p>
     * 
     * @param position index of the node to return
     * 
     * @return the node at the requested position
     * 
     * @throws IndexOutOfBoundsException if the index is negative or
     *     greater than or equal to the number of children of this node
     */
    public Node getChild(int position) {
        
        if (children == null) {
            throw new IndexOutOfBoundsException(
              "This node has no children"
            );
        }
        return (Node) children.get(position); 
        
    }
    
    
    // private int lastPosition = -1;
    
    /**
     *<p>
     * Returns the position of a node within the children of this
     * node. This is a number between 0 and one less than the number of
     * children of this node. It returns -1 if <code>child</code>
     * does not have this node as a parent.
     * </p>
     * 
     * <p>
     * This method does a linear search through the node's children. 
     * On average, it executes in O(N) where N is the number of 
     * children of the node.
     * </p>
     * 
     * @param child the node whose position is desired
     * 
     * @return the position of the argument node among 
     *     the children of this node
     */
    public int indexOf(Node child) {
        
        if (children == null) return -1;
        
        // Programs tend to iterate through in order so we store the 
        // last index returned and check the one immediately after it
        //  first; before searching the list from the beginning. 
        /* lastPosition++;
        if (lastPosition != children.size()) {
            if (child == children.get(lastPosition)) {
              return lastPosition;
            }
            else lastPosition = -1;
        }
        lastPosition = children.indexOf(child);
        return lastPosition; */
        return children.indexOf(child);
        
    }

    
    /**
     * <p>
     * Removes the child of this node at the specified position.
     * Indexes begin at 0 and count up to one less than the number
     * of children in this node.
     * </p>
     * 
     * @param position index of the node to remove
     *
     * @return the node which was removed
     * 
     * @throws IndexOutOfBoundsException if the index is negative or
     *     greater than or equal to the number of children of this node
     */
    public Node removeChild(int position) {
        
        if (children == null) {
            throw new IndexOutOfBoundsException(
              "This node has no children"
            );
        }
        Node removed = (Node) children.get(position);
        // fill in actual base URI
        // This way does add base URIs to elements created in memory
        if (removed.isElement()) fillInBaseURI((Element) removed);
        children.remove(position);
        removed.setParent(null);
                
        return removed;  
        
    }

    
    void fillInBaseURI(Element removed) {

        ParentNode parent = removed;
        String actualBaseURI = "";
        while (parent != null && actualBaseURI.equals("")) {
            actualBaseURI = parent.getActualBaseURI();
            parent = parent.getParent();
        }
        removed.setActualBaseURI(actualBaseURI);
        
    }


    /**
     * <p>
     * Removes the specified child of this node.
     * </p>
     * 
     * @param child child node to remove
     *
     * @return the node which was removed
     * 
     * @throws NoSuchChildException if <code>child</code> is 
     *     not in fact a child of this node
     */
    public Node removeChild(Node child) {
        
        if (children == null) {
            throw new NoSuchChildException(
              "Child does not belong to this node"
            );
        }
        // This next line is a hotspot
        int position = children.indexOf(child);
        if (position == -1) {
            throw new NoSuchChildException(
              "Child does not belong to this node"
            );
        }
        if (child.isElement()) fillInBaseURI((Element) child);
        children.remove(position);
        
        child.setParent(null);

        return child;
        
    }

    
    /**
     * <p>
     * Replaces an existing child with a new child node.
     * If <code>oldChild</code> is not a child of this node, 
     * then a <code>NoSuchChildException</code> is thrown. 
     * </p>
     * 
     * @param oldChild the node removed from the tree
     * @param newChild the node inserted into the tree
     * 
     * @throws MultipleParentException if <code>newChild</code> already
     *     has a parent
     * @throws NoSuchChildException if <code>oldChild</code> 
     *     is not a child of this node
     * @throws NullPointerException if either argument is null
     * @throws IllegalAddException if this node cannot have children 
     *     of the type of <code>newChild</code>
     */
    public void replaceChild(Node oldChild, Node newChild) {
        
        if (oldChild == null) {
            throw new NullPointerException(
              "Tried to replace null child"
            );
        } 
        if (newChild == null) {
            throw new NullPointerException(
              "Tried to replace child with null"
            );
        } 
        if (children == null) {
            throw new NoSuchChildException(
              "Reference node is not a child of this node."
            );
        }
        int position = children.indexOf(oldChild);
        if (position == -1)  {
            throw new NoSuchChildException(
              "Reference node is not a child of this node."
            );   
        }
        
        if (oldChild == newChild) return;
        
        insertionAllowed(newChild, position);
        removeChild(position);
        insertChild(newChild, position);
        
    }

    
    /**
     * 
     * <p>
     * Sets the URI against which relative URIs in this node will be 
     * resolved. Generally, it's only necessary to set this property if
     * it's different from a node's parent's base URI, as it may
     * be in a document assembled from multiple entities
     * or by XInclude.
     * </p>
     * 
     * <p>
     * Relative URIs are not allowed here. Base URIs must be absolute.
     * However, the base URI may be set to null or the empty string
     * to indicate that the node has no explicit base URI. In this 
     * case, it inherits the base URI of its parent node, if any.
     * </p>
     * 
     * <p>
     * URIs with fragment identifiers are also not allowed. The value 
     * passed to this method must be a pure URI, not a URI reference.
     * </p>
     * 
     * <p>
     * You can also add an <code>xml:base</code> attribute to 
     * an element in the same way you'd add any other namespaced
     * attribute to an element. If an element's base URI 
     * conflicts with its <code>xml:base</code> attribute,
     * then the value found in the <code>xml:base</code> attribute
     * is used. 
     * </p>
     * 
     * <p>
     * If the base URI is null or the empty string and there is 
     * no <code>xml:base</code> attribute, then the base URI is 
     * determined by the nearest ancestor node which does have a  
     * base URI. Moving such a node from one location to another 
     * can change its base URI.
     * </p>
     * 
     * @param URI the new base URI for this node
     *
     * @throws MalformedURIException if <code>URI</code> is 
     *     not a legal RFC 2396 absolute URI
     */
    public abstract void setBaseURI(String URI);


    String getActualBaseURI() {
        if (actualBaseURI == null) return "";
        return actualBaseURI;     
    }


    void setActualBaseURI(String uri) {
        if (uri == null) uri = "";
        if (!"".equals(uri)) Verifier.checkAbsoluteURI(uri);
        actualBaseURI = uri;     
    }


    final String findActualBaseURI() {
        
        ParentNode current = this;
        while (true) {
            String actualBase = current.getActualBaseURI();
            ParentNode parent = current.getParent();
    
            if (parent == null) return actualBase;
               
            if ("".equals(actualBase)) {
                current = parent;
                continue;  
            }
               
            // The parent is loaded from a different entity.
            // Therefore just return the actual base.
            return actualBase;
        }
        
    }

    
}
