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
 * <p>
 *   This class represents an XML comment such as 
 *   <code>&lt;-- This is a comment--></code>. 
 *   It cannot have any child nodes.
 *   It can be a child of an <code>Element</code> 
 *   or a <code>Document</code>.
 *   It has essentially no internal substructure.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d21
 * 
 */
public class Comment extends LeafNode {

    private String data;

    /**
     * <p>
     * Creates a new <code>Comment</code> object from string data. 
     * The data is checked for legality according to XML 1.0 rules. 
     * Characters like the form feed and null are not allowed. 
     * Furthermore, the two hyphen string "--" is not allowed 
     * and the last character of the comment may not be a hyphen.
     * </p>
     * 
     * @param data The initial text of the object
     */
    public Comment(String data) {
        setValue(data);  
    }

    /**
     * <p>
     * Creates a new comment that's a <code>Comment</code> 
     * object of its argument.
     * The copy has the same data but no parent node.
     * </p>
     * @param comment The comment to copy
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
     * Sets the content of this <code>Comment</code> object 
     * to the specified string.
     * This string is checked for legality according to XML 1.0 rules. 
     * Characters that can be serialized such as &lt; and &amp;  
     * are allowed. However, characters like the form feed and 
     * unmatched halves of surrogate pairs are not allowed.
     * Furthermore, the string may not contain a double hyphen 
     * (<code>--</code>) and may not end with a hyphen.
     * </p>
     * 
     * @param data the text to install in the object
     */
    public final void setValue(String data) {
        if (data == null) data = "";
        else {
            Verifier.checkCharacterData(data);
            if (data.indexOf("--") != -1) {
                throw new IllegalDataException(
                 "Comment data contains a double hyphen (--).");
            }
    
            if (data.indexOf('\r') != -1) {
                throw new IllegalDataException(
                 "Comment data cannot contain carriage returns.");
            }
    
            if (data.startsWith("-")) {
                throw new IllegalDataException(
                 "Comment data starts with a hyphen.");
            }
    
            if (data.endsWith("-")) {
                throw new IllegalDataException(
                 "Comment data ends with a hyphen.");
            }              
        }
        checkValue(data);
        // Is <!----> a legal comment? Yes it is. 
        this.data = data;
    }


    /**
     * <p>
     * Subclasses can override this method to perform additional 
     * checks. However, this can only be used to add checks, not 
     * remove them. All text in comments must be potentially 
     * well-formed when serialized. 
     * </p>
     * 
     * @param data the text to check
     * 
     * @throws XMLException if the data does not satisfy
     *     the local constraints
     */
    protected void checkValue(String data) {}

    /**
     * <p>
     * Returns the value of this comment as defined by XPath 1.0. 
     * The XPath string-value of a comment node is the string 
     * content of the node, not including the initial  
     * <code>&lt;--</code> and closing <code>--&gt;</code>.
     * </p>
     * 
     * @return the content of the comment
     * @see nu.xom.Node#getValue()
     */
    public final String getValue() {
        return data;
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
    public Node copy()  {
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
     * 
     * @see nu.xom.Node#toXML()
     */
    public final String toXML() {
        StringBuffer result = new StringBuffer("<!--");
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
     * 
     * @see java.lang.Object#toString()
     */
    public final String toString() {
        String value = getValue();
        if (value.length() <= 40) {
            return "[" + getClass().getName() + ": " + value + "]";
        }
        
        return "[" + getClass().getName() + ": " 
          + value.substring(0, 35) + "...]";
    }

    boolean isComment() {
        return true;   
    } 
    
}
