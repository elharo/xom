/* Copyright 2002-2005 Elliotte Rusty Harold
   
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
 *   This class represents an XML comment such as 
 *   <code>&lt;-- This is a comment--></code>. 
 *   A comment node cannot have any child nodes.
 *   It can be a child of an <code>Element</code> 
 *   or a <code>Document</code>.
 *   It has essentially no internal substructure.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.3.0
 * 
 */
public class Comment extends Node {

    
    private String data;

    /**
     * <p>
     * Creates a new <code>Comment</code> object from string data. 
     * The data is checked for legality according to XML 1.0 rules. 
     * Illegal characters such as the form feed and null are not
     * allowed. Furthermore, the two hyphen string "--" is not allowed;
     * and the last character of the comment must not be a hyphen.
     * </p>
     * 
     * @param data the initial text of the comment
     */
    public Comment(String data) {
        _setValue(data);  
    }

    
    /**
     * <p>
     * Creates a new comment that's a copy of its argument.
     * The copy has the same data but no parent node.
     * </p>
     * 
     * @param comment the comment to copy
     */
    public Comment(Comment comment) {
        this.data = comment.data;  
    }
    
    
    private Comment() {}
    
    static Comment build(String data) {
        Comment result = new Comment();
        result.data = data;
        return result;
    }

    
    /**
     * <p>
     * Returns the value of this comment as defined by XPath 1.0. 
     * The XPath string-value of a comment node is the string 
     * content of the node, not including the initial  
     * <code>&lt;--</code> and closing <code>--&gt;</code>.
     * </p>
     * 
     * @return the content of the comment
     */
    public final String getValue() {
        return data;
    }

    
    /**
     * <p>
     * Sets the content of this <code>Comment</code> object 
     * to the specified string.
     * This string is checked for legality according to XML 1.0 rules. 
     * Characters that can be serialized such as &lt; and &amp;  
     * are allowed. However, illegal characters such as the form feed  
     * and unmatched halves of surrogate pairs are not allowed.
     * Furthermore, the string may not contain a double hyphen 
     * (<code>--</code>) and may not end with a hyphen.
     * </p>
     * 
     * @param data the text to install in the comment
     */
    public void setValue(String data) {
        _setValue(data);
    }


    private void _setValue(String data) {
        
        if (data == null) data = "";
        else {
            Verifier.checkPCDATA(data);
            
            if (data.indexOf("--") != -1) {
                IllegalDataException ex = new IllegalDataException(
                 "Comment data contains a double hyphen (--).");
                ex.setData(data);
                throw ex;
            }
    
            if (data.indexOf('\r') != -1) {
                IllegalDataException ex = new IllegalDataException(
                 "Comment data cannot contain carriage returns.");
                ex.setData(data);
                throw ex;
            }
    
            if (data.endsWith("-")) {
                IllegalDataException ex = new IllegalDataException(
                 "Comment data ends with a hyphen.");
                ex.setData(data);
                throw ex;
            } 
            
        } 
        this.data = data;
        
    }


    /**
     * <p>
     * Throws <code>IndexOutOfBoundsException</code> because 
     * comments do not have children.
     * </p>
     * 
     * @return never returns because comments do not have children;
     *     Always throws an exception.
     * 
     * @param position the index of the child node to return
     * 
     * @throws IndexOutOfBoundsException because comments 
     *     do not have children
     */
    public final Node getChild(int position) {
        throw new IndexOutOfBoundsException(
          "LeafNodes do not have children");        
    }

    
    /**
     * <p>
     * Returns 0 because comments do not have children.
     * </p>
     * 
     * @return zero
     */
    public final int getChildCount() {
        return 0;   
    }
    
    
    /**
     * <p>
     *   Returns a deep copy of this <code>Comment</code> object 
     *   which contains the same text, but does not have any parent.
     *   Thus, it can be inserted into a different document.
     * </p>
     *
     * @return a deep copy of this <code>Comment</code> 
     *     that is not part of a document
     * 
     */
    public Comment copy()  {
        return new Comment(data);
    }

    
    /**
     * <p>
     *   Returns a <code>String</code> containing the actual XML
     *  form of the comment;
     *   for example, <code>&lt;--This is a comment--&gt;</code>. 
     * </p>
     * 
     * @return a <code>String</code> containing a well-formed 
     *     XML comment
     */
    public final String toXML() {
    	StringBuilder result = new StringBuilder("<!--");
        result.append(data);
        result.append("-->");
        return result.toString();
    }


    /**
     * <p>
     *   Returns a string form of the comment suitable for debugging
     *   and diagnosis. It deliberately does not return an actual 
     *   XML comment. 
     * </p>
     * 
     * @return a representation of the <code>Comment</code> 
     *     as a <code>String</code>
     */
    public final String toString() {
        
        String value = getValue();
        if (value.length() <= 40) {
            return "[" + getClass().getName() + ": " 
              + Text.escapeLineBreaksAndTruncate(value) + "]";
        }
        
        return "[" + getClass().getName() + ": " 
          + Text.escapeLineBreaksAndTruncate(value.substring(0, 35)) + "...]";
        
    }

    
    boolean isComment() {
        return true;   
    } 
    
    
}
