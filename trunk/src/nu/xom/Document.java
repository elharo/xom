// Copyright 2002, 2003 Elliotte Rusty Harold
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
 * @version 1.0d23
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
     */
    public Document(Element root) {
        checkRoot(root);
        super.insertChild(root, 0);
    }

    /**
     * <p>
     * Creates a copy of the <code>Document</code>.
     * </p>
     * 
     * @param doc the Document to copy
     */
    public Document(Document doc) {

      super.insertChild(doc.getRootElement().copy(), 0);
      for (int i = 0; i < doc.getChildCount(); i++) {
          Node child = doc.getChild(i);
          if (!(child.isElement())) {
              this.insertChild(child.copy(), i);
          }
      }
      this.setActualBaseURI(doc.getActualBaseURI());

    }


    /**
     * <p>
     * Inserts a child node at the specified position.
     * Inserting at position 0 makes the child the
     *  first child of this node. Inserting at the position 
     * <code>getChildCount</code> makes the child the 
     * last child of the node.
     * </p>
     * 
     * @param position where to insert the child
     * @param child the node to insert
     * 
     * @throws IllegalAddException if <code>child</code> is not a 
     *    <code>Comment</code>, <code>ProcessingInstruction</code>, 
     *    or <code>DocType</code>, or if <code>child</code> is a 
     *    <code>DocType</code> but this document already has a
     *    <code>DocType</code>
     * @throws MultipleParentException if child already has a parent
     * @throws NullPointerException if child is null
     * @throws IndexOutOfBoundsException if the position is negative 
     *     or greater than the number of children of the node.`
     */
    public final void insertChild(Node child, int position) {
        if (child.isComment() || child.isProcessingInstruction()) {
            super.insertChild(child, position);
        }
        else if (child.isDocType()) {
            if (position <= getRootPosition()) {
                DocType oldDocType = getDocType(); 
                if (oldDocType != null) {
                    throw new IllegalAddException(
                      "Tried to insert a second DOCTYPE"
                    );   
                }
                super.insertChild(child, position);
            }
            else {
                throw new IllegalAddException(
                  "Cannot add a document type declaration "
                  + "after the root element"
                );               
            }
        }
        else if (child.isElement()) {
            throw new IllegalAddException(
             "Cannot add a second root element to a Document."
            );
        }
        else {
            throw new IllegalAddException("Cannot add a "
             + child.getClass().getName() + " to a Document.");
        }

    }

    private int getRootPosition() {
        for (int i = 0; i < getChildCount(); i++) {
             Node child = getChild(i);
             if (child.isElement()) {
                return i;
             }
         }
        // It should not be possible to get here
        throw new WellformednessException("Missing root element");
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
     * @throws MultipleParentException if doctype belongs to 
     *     another document
     * @throws NullPointerException if doctype is null
     * 
     */
    public final void setDocType(DocType doctype) {
        DocType oldDocType = getDocType();
        if (oldDocType == null) {
            super.insertChild(doctype, 0);            
        } 
        else if (doctype == oldDocType) return; 
        else {
            super.insertChild(doctype, this.indexOf(oldDocType));
            oldDocType.detach();
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
        for (int i = 0; i < getChildCount(); i++) {
             Node child = getChild(i);
             if (child.isElement()) {
                return (Element) child;
             }
         }
         // It should not be possible to get here
         throw new WellformednessException("Missing root element");
    }

    /**
     * <p>
     * Replaces the current root element
     * with a different root element.
     * </p>
     * 
     * @param root the new root element
     * 
     * @throws MultipleParentException if root has a parent
     * @throws XMLException if root is not legal for this  
     *      subclass of Document
     * @throws NullPointerException if root is null
     */
    public final void setRootElement(Element root) {
        if (root == this.getRootElement()) return;
        checkRoot(root);
        super.insertChild(root, getRootPosition());
        super.removeChild(getRootPosition()+1);
    }

    /**
     * <p>
     * Subclasses can override this method to perform additional 
     * checks on the root element beyond what XML 1.0 requires.
     * For example, an <code>XHTMLDocument</code> subclass might 
     * throw an exception if the proposed root element were not 
     * an <code>html</code> element.
     * </p>
     * 
     * @param root The new root element.
     * 
     * @throws XMLException if the proposed root element 
     *     does not satisfy the local constraints
     */
    protected void checkRoot(Element root) {}

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
     *    greater than the number of children of this document - 1.
     * @throws WellformednessException if the index points 
     *     to the root element.
     */
    public final Node removeChild(int position) {
        if (position == getRootPosition()) {
            throw new WellformednessException(
              "Cannot remove the root element"
            );
        }
        return super.removeChild(position);
    }

    /**
     * <p>
     * Removes the specified child from this node.
     * It throws a <code>NoSuchChildException</code> 
     * if the node is not a child of this node.  
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
     *   child of this node.
     * @throws WellformednessException if child is the root element.
     */
    public final Node removeChild(Node child) {
        if (child == getRootElement()) {
            throw new WellformednessException(
              "Cannot remove the root element");
        }
        return super.removeChild(child);
    }

    /**
     * 
     * <p>
     * Sets the URI from which this node was loaded,
     * and against which relative URLs in this node will be resolved.
     * </p>
     * 
     * @param URI the base URI of this document 
     * 
     * @throws MalformedURIException if <code>URI</code> is 
     *     not a legal IRI
     */
    public final void setBaseURI(String URI) { 
        setActualBaseURI(URI);       
    }
    
    /**
     * <p>
     *   Returns the URI from which this document was loaded.
     * </p>
     * 
     * @return the base URI of this document 
     */
    public final String getBaseURI() {       
        return getActualBaseURI();       
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
     * @see nu.xom.Node#getValue()
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
     * @return a <code>String</code> containing this entire
     *     XML document
     * 
     * @see nu.xom.Node#toXML()
     */
    public final String toXML() {
    
        StringBuffer result = new StringBuffer();

        // XML declaration
        result.append("<?xml version=\"1.0\"?>\r\n");
        
        // children
        for (int i = 0; i < getChildCount(); i++) {
            result.append(getChild(i).toXML());
            result.append("\r\n");  
        }
        
        return result.toString();
        
    }

    
    /**
     * <p>
     * Returns a complete copy of this document.
     * </p>
     * 
     * @return a complete, deep copy of this 
     *     <code>Document</code> object
     * 
     * @see nu.xom.Node#copy()
     */
    public Node copy() {
        return new Document(this);
    }

    boolean isDocument() {
        return true;   
    }

    /**
     * <p>
     * Returns a string representation of this node suitable 
     * for debugging and diagnosis. This is <em>not</em>
     * the XML representation of this document.
     * </p>
     * 
     * @return a non-XML string representation of this 
     *     <code>Document</code> object
     * 
     * @see nu.xom.Node#toString()
     */
    public final String toString() {
        return "[" + getClass().getName() + ": " 
          + getRootElement().getQualifiedName() + "]"; 
    }

}