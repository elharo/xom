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

import java.io.UnsupportedEncodingException;

/**
 * <p>
 *   This class represents a run of text. 
 *   CDATA sections are not treated differently than 
 *   normal text.
 * </p>

 * @author Elliotte Rusty Harold
 * @version 1.0b6
 *
 */
public class Text extends Node {

    private byte[] data;
    
    /**
     * <p>
     * This constructor creates a new <code>Text</code> object. 
     * The data is checked for  legality according to XML 1.0 rules. 
     * Characters that can be serialized by escaping them 
     * such as &lt; and &amp; are allowed. However, characters  
     * such as the form feed, null, vertical tab,
     * unmatched halves of surrogate pairs,
     * and 0xFFFE and 0xFFFF are not allowed.
     * </p>
     * 
     * @param data the initial text of the object
     *
     * @throws IllegalCharacterDataException if data contains any 
     *     characters which are illegal in well-formed XML 1.0 such as 
     *     null, vertical tab, or unmatched halves of surrogate pairs
     */
    public Text(String data) {
        _setValue(data);
    }

    
    /**
     * <p>
     * Creates a copy of the specified <code>Text</code> object.
     * </p>
     * 
     * @param text the <code>Text</code> object to copy
     */
    public Text(Text text) {
        // I'm relying here on the data array being immutable.
        // If this ever changes, e.g. by adding an append method,
        // this method needs to change too.
        this.data = text.data;
    }

    
    private Text() {}
    
    
    static Text build(String data) {
        Text result = new Text();
        try {
            result.data = data.getBytes("UTF8");   
        }
        catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(
              "Bad VM! Does not support UTF-8"
            );
        } 
        return result;
    }

    /**
     * <p>
     * Sets the content of the <code>Text</code> object 
     * to the specified data. The data is checked for 
     * legality according to XML 1.0 rules. Characters that 
     * can be serialized such as &lt; and &amp; are allowed.   
     * However, characters such as the form feed, null, 
     * vertical tab, unmatched halves of surrogate pairs,
     * and 0xFFFE and 0xFFFF are not allowed.
     * </p>
     * 
     * @param data the text to install in the object
     * 
     * @throws IllegalCharacterDataException if data contains any 
     *     characters which are illegal in well-formed XML 1.0 such as 
     *     null, vertical tab, or unmatched halves of surrogate pairs
     */
    public void setValue(String data) {
        _setValue(data);
    }

    
    private void _setValue(String data) {
        
        if (data == null) data = "";
        else Verifier.checkPCDATA(data);
        try {
            this.data = data.getBytes("UTF8");   
        }
        catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(
              "Bad VM! Does not support UTF-8"
            );
        }
        
    }

    /**
     * <p>
     * Returns the XPath 1.0 string-value of this <code>Text</code> 
     * node. The XPath string-value of a text node is the same as 
     * the text of the node.
     * </p>
     *  
     * @return The content of the node.
     */
    public final String getValue() {
        
        try {
            return new String(data, "UTF8");
        } 
        catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(
              "Bad VM! Does not support UTF-8"
            );
        }
        
    }

    
    /**
     * <p>
     * Throws <code>IndexOutOfBoundsException</code> because 
     * texts do not have children.
     * </p>
     * 
     * @return never returns because texts do not have children;
     *     always throws an exception.
     * 
     * @param position the index of the child node to return
     * 
     * @throws IndexOutOfBoundsException because texts 
     *     do not have children
     */
    public final Node getChild(int position) {
        throw new IndexOutOfBoundsException(
          "LeafNodes do not have children");        
    }

    
    /**
     * <p>
     * Returns 0 because texts do not have children.
     * </p>
     * 
     * @return zero
     */
    public final int getChildCount() {
        return 0;   
    }
    
    
    /**
     * <p>
     * Returns a deep copy of this <code>Text</code> with no parent,
     * that can be added to this document or a different one.
     * </p>
     *
     * @return a deep copy of this text node with no parent
     */
    public Node copy() {
        if (isCDATASection()) {
            return new CDATASection(this);
        }
        else {
            return new Text(this);
        }
    }

    
    /**
     * <p>
     * Returns a string containing the XML serialization of this text 
     * node.  Unlike <code>getValue</code>, this method escapes 
     * characters such as &amp; and &lt; using entity references such
     * as <code>&amp;amp;</code> and <code>&amp;lt;</code>.
     * It escapes the carriage return (\r) as <code>&amp;#x0D;</code>.
     * </p>
     * 
     * @return the string form of this text node
     */
    public final String toXML() {
        return escapeText(getValue());    
    }

    
    private static String escapeText(String s) {
        
        StringBuffer result = new StringBuffer(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '<':
                    result.append("&lt;");
                    break;
                case '>':
                    result.append("&gt;");
                    break;
                case '&':
                    result.append("&amp;");
                    break;
                case '\r':
                    result.append("&#x0D;");
                    break;
                default: 
                    result.append(c); 
            }  
        }
        return result.toString();
        
    }
    
    
    boolean isText() {
        return true;   
    } 


    /**
     * <p>
     * Returns a <code>String</code> 
     * representation of this <code>Text</code> suitable for
     * debugging and diagnosis. This is <em>not</em>
     * the XML representation of this <code>Text</code> node.
     * </p>
     * 
     * @return a non-XML string representation of this node
     */
    public final String toString() {
        
        return "[" + getClass().getName() + ": " 
          + escapeLineBreaksAndTruncate(getValue()) + "]";
          
    }
    
    
    static String escapeLineBreaksAndTruncate(String s) {
        
        int length = s.length();
        boolean tooLong = length > 40;
        if (length > 40) {
            length = 35;
            s = s.substring(0, 35);
        }
        
        StringBuffer result = new StringBuffer(length);
        for (int i = 0; i < length; i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\n': 
                    result.append("\\n");
                    break;
                case '\r': 
                    result.append("\\r");
                    break;
                case '\t': 
                    result.append("\\t");
                    break;
                default:
                    result.append(c);
            }
        }
        if (tooLong) result.append("...");
        
        return result.toString();
        
    }

    
    boolean isCDATASection() {
        return false;
    }

    
}