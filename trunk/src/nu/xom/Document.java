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
 *  <p>
 * The <code>Document</code> class represents
 * a complete XML document including its root element,
 * prolog, and epilog.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0b4
 * 
 */
public class Document extends ParentNode {

    /**
     * <p>
     * Creates a new <code>Document</code> object with the
     * specified root element.
     * </p>
     * 
     * @param root the root element of this document
     * 
     * @throws NullPointerException if <code>root</code> is null
     * @throws MultipleParentException if <code>root</code> already 
     *     has a parent
     */
    public Document(Element root) {
        _insertChild(root, 0);
    }

    
    /**
     * <p>
     * Creates a copy of this document.
     * </p>
     * 
     * @param doc the document to copy
     * 
     * @throws NullPointerException if <code>doc</code> is null
     */
    public Document(Document doc) {

      insertChild(doc.getRootElement().copy(), 0);
      for (int i = 0; i < doc.getChildCount(); i++) {
          Node child = doc.getChild(i);
          if (!(child.isElement())) {
              this.insertChild(child.copy(), i);
          }
      }
      this.actualBaseURI = doc.actualBaseURI;

    }


    final void insertionAllowed(Node child, int position) {
        
        if (child == null) {
            throw new NullPointerException(
             "Tried to insert a null child in the document");
        }
        else if (child.getParent() != null) {
            throw new MultipleParentException("Child already has a parent.");
        }
        else if (child.isComment() || child.isProcessingInstruction()) {
            return;
        }
        else if (child.isDocType()) {
            if (position <= getRootPosition()) {
                DocType oldDocType = getDocType(); 
                if (oldDocType != null) {
                    throw new IllegalAddException(
                      "Tried to insert a second DOCTYPE"
                    );   
                }
                return;
            }
            else {
                throw new IllegalAddException(
                  "Cannot add a document type declaration "
                  + "after the root element"
                );               
            }
        }
        else if (child.isElement()) {
            if (getChildCount() == 0) return;
            else {
                throw new IllegalAddException(
                  "Cannot add a second root element to a Document."
                );
            }
        }
        else {
            throw new IllegalAddException("Cannot add a "
             + child.getClass().getName() + " to a Document.");
        }

    }
    

    private int getRootPosition() {
        
        // This looks like an infinite loop but it isn't
        // because all documents have root elements
        for (int i = 0; ; i++) {
             Node child = getChild(i);
             if (child.isElement()) {
                return i;
             }
         }
        
    }
    
    
    /**
     * <p>
     * Returns this document's document type declaration, 
     * or null if it doesn't have one.
     * </p>
     * 
     * @return the document type declaration
     * 
     * @see #setDocType
     *
     */
    public final DocType getDocType() {
        
        for (int i = 0; i < getChildCount(); i++) {
             Node child = getChild(i);
             if (child.isDocType()) {
                return (DocType) child;
             }
         }
         return null;
         
    }

    
    /**
     * <p>
     * Sets this document's document type declaration.
     * If this document already has a document type declaration,
     * then it's inserted at that position. Otherwise, it's inserted
     * at the beginning of the document.
     * </p>
     * 
     * @param doctype the document type declaration
     * 
     * @throws MultipleParentException if <code>doctype</code> belongs 
     *      to another document
     * @throws NullPointerException if <code>doctype</code> is null
     * 
     */
    public void setDocType(DocType doctype) {
        
        DocType oldDocType = getDocType();
        if (doctype == null) {
            throw new NullPointerException("Null DocType");
        }
        else if (doctype == oldDocType) return; 
        else if (doctype.getParent() != null) {
            throw new MultipleParentException("DocType belongs to another document");
        }
        
        if (oldDocType == null) insertChild(doctype, 0);
        else {
            int position = indexOf(oldDocType);
            children.remove(position);
            children.add(position, doctype);
            oldDocType.setParent(null);
            doctype.setParent(this);
        }
        
    }


    /**
     * <p>
     * Returns this document's root element.
     * This is guaranteed to be non-null.
     * </p>
     * 
     * @return the root element
     */
    public final Element getRootElement() {
        
        // This looks like an infinite loop but it isn't because
        // all documents have root elements.
        for (int i = 0; ; i++) {
             Node child = getChild(i);
             if (child.isElement()) {
                return (Element) child;
             }
         }
        
    }

    
    /**
     * <p>
     * Replaces the current root element with a different root element.
     * </p>
     * 
     * @param root the new root element
     * 
     * @throws MultipleParentException if root has a parent
     * @throws NullPointerException if root is null
     */
    public void setRootElement(Element root) {
        
        Element oldRoot = this.getRootElement(); 
        if (root == oldRoot) return;
        else if (root == null) {
            throw new NullPointerException("Root element cannot be null");
        }
        else if (root.getParent() != null) {
            throw new MultipleParentException(root.getQualifiedName()
              + " already has a parent");
        }
        
        fillInBaseURI(oldRoot);
        int index = indexOf(oldRoot);
        
        oldRoot.setParent(null);
        children.remove(index);
        children.add(index, root);
        root.setParent(this);
        
    }
    
    
    /**
     * <p>
     * Sets the URI from which this document was loaded, and
     * against which relative URLs in this document will be resolved.
     * Setting the base URI to null or the empty string removes any
     * existing base URI.
     * </p>
     * 
     * @param URI the base URI of this document 
     * 
     * @throws MalformedURIException if <code>URI</code> is 
     *     not a legal absolute URI
     */
    public void setBaseURI(String URI) { 
        setActualBaseURI(URI);       
    }
    
    
    /**
     * <p>
     *   Returns the absolute URI from which this document was loaded.
     *   This method returns the empty string if the base URI is not 
     *   known; for instance if the document was created in memory with
     *   a constructor rather than by parsing an existing document.
     * </p>
     * 
     * @return the base URI of this document 
     */
    public final String getBaseURI() {       
        return getActualBaseURI();
    }

    
    /**
     * <p>
     * Removes the child of this document at the specified position.
     * Indexes begin at 0 and count up to one less than the number
     * of children of this document. The root element cannot be 
     * removed. Instead, use <code>setRootElement</code> to replace
     * the existing root element with a different element.
     * </p>
     * 
     * @param position index of the node to remove
     * 
     * @return the node which was removed
     * 
     * @throws IndexOutOfBoundsException if the index is negative or 
     *    greater than the number of children of this document - 1
     * @throws WellformednessException if the index points 
     *     to the root element
     */
    public Node removeChild(int position) {
        
        if (position == getRootPosition()) {
            throw new WellformednessException(
              "Cannot remove the root element"
            );
        }
        return super.removeChild(position);
        
    }

    
    /**
     * <p>
     * Removes the specified child from this document.
     * The root element cannot be removed.
     * Instead, use <code>setRootElement</code> to replace the
     * existing root element with a different element.
     * </p>
     * 
     * @param child node to remove
     * 
     * @return the node which was removed
     * 
     * @throws NoSuchChildException if the node is not a
     *   child of this node
     * @throws WellformednessException if child is the root element
     */
    public Node removeChild(Node child) {
        
        if (child == getRootElement()) {
            throw new WellformednessException(
              "Cannot remove the root element");
        }
        return super.removeChild(child);
        
    }

    
    /**
     * <p>
     * Replaces an existing child with a new child node.
     * If <code>oldChild</code> is not a child of this node, 
     * then a <code>NoSuchChildException</code> is thrown. 
     * The root element can only be replaced by another element.
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
     * @throws IllegalAddException if <code>newChild</code> is an
     *     attribute or a text node
     * @throws WellformednessException if <code>newChild</code> 
     *     <code>oldChild</code> is an element and 
     *     <code>newChild</code> is not
     */
    public void replaceChild(Node oldChild, Node newChild) {
          
        if (oldChild == getRootElement() 
          && newChild != null && newChild.isElement()) {
            setRootElement((Element) newChild);
        } 
        else if (oldChild == getDocType() 
          && newChild != null && newChild.isDocType()) {
            setDocType((DocType) newChild);
        }
        else {
            super.replaceChild(oldChild, newChild);
        }
        
    }


    /**
     * <p>
     * Returns the value of the document as defined by XPath 1.0.
     * This is the same as the value of the root element, which 
     * is the complete PCDATA content of the root element, without 
     * any tags, comments, or processing instructions after all 
     * entity and character references have been resolved.
     * </p>
     * 
     * @return  value of the root element of this document
     * 
     */
    public final String getValue() {
        return getRootElement().getValue();
    }

    
    /**
     * <p>
     * Returns the actual complete, well-formed XML document as a 
     * <code>String</code>. Significant white space is preserved. 
     * Insignificant white space in tags, the prolog, the epilog, 
     * and the internal DTD subset is not preserved.
     * Entity and character references are not preserved. 
     * The entire document is contained in this one string.
     * </p>
     * 
     * @return a string containing this entire XML document
     */
    public final String toXML() {
    
        StringBuffer result = new StringBuffer();

        // XML declaration
        result.append("<?xml version=\"1.0\"?>\n");
        
        // children
        for (int i = 0; i < getChildCount(); i++) {
            result.append(getChild(i).toXML());
            result.append("\n");  
        }
        
        return result.toString();
        
    }

    
    /**
     * <p>
     * Returns a complete copy of this document.
     * </p>
     * 
     * @return a deep copy of this <code>Document</code> object
     */
    public Node copy() {
        return new Document(this);
    }

    
    boolean isDocument() {
        return true;   
    }

    
    /**
     * <p>
     * Returns a string representation of this document suitable 
     * for debugging and diagnosis. This is <em>not</em>
     * the XML representation of this document.
     * </p>
     * 
     * @return a non-XML string representation of this document
     */
    public final String toString() {
        return "[" + getClass().getName() + ": " 
          + getRootElement().getQualifiedName() + "]"; 
    }

    
}