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


/**
 * <p>
 * Represents an XML document type declaration such as 
 * <code>&lt;!DOCTYPE book SYSTEM "docbookx.dtd"></code>.
 * Note that this is not the same thing as a document
 * type <em>definition</em> (DTD). XOM does not currently model 
 * the DTD. The document type declaration contains or points to 
 * the DTD, but it is not the DTD.
 * </p>
 * 
 * <p>
 * A <code>DocType</code> object does not have any child 
 * nodes. It can be a child of a <code>Document</code>.
 * </p>
 * 
 * <p>
 * Each <code>DocType</code> object has four <Code>String</code> 
 * properties, some of which may be null:
 * </p>
 * 
 * <ul>
 *   <li>The declared name of the root element (which 
 * does not necessarily match the actual root element name
 *  in an invalid document)</li>
 *   <li>The public identifier (which may be null)</li>
 *   <li>The system identifier (which may be null)</li>
 *   <li>The internal DTD subset (which may be null)</li>
 * 
 * </ul>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.1d1
 * 
 */
public class DocType extends Node {

    
    private String rootName;
    private String systemID;
    private String publicID;
    private String internalDTDSubset = "";

    
    /**
     * <p>
     * Creates a new document type declaration with a public ID
     * and a system ID. It has the general form 
     * <code>&lt;!DOCTYPE rootElementName PUBLIC 
     * "publicID" "systemID"&gt;</code>.
     * </p>
     * 
     * @param rootElementName the name specified for the root element
     * @param publicID the public ID of the external DTD subset
     * @param systemID the URL of the external DTD subset
     * 
     * @throws IllegalNameException  if <code>rootElementName</code> 
     *     is not a legal XML 1.0 name
     * @throws IllegalDataException if <code>publicID</code> is not a  
     *     legal XML 1.0 public identifier
     * @throws MalformedURIException if the system ID is not a 
     *     syntactically correct URI, or if it contains a fragment
     *     identifier 
     */
    public DocType(
      String rootElementName, String publicID, String systemID) {
        _setRootElementName(rootElementName);    
        _setSystemID(systemID);
        _setPublicID(publicID);
    }

    
    /**
     * <p>
     * Creates a new document type declaration with a system ID
     * but no public ID. It has the general form 
     * <code>&lt;!DOCTYPE rootElementName SYSTEM "systemID"&gt;</code>.
     * </p>
     * 
     * @param rootElementName the name specified for the root element
     * @param systemID the URL of the external DTD subset
     * 
     * @throws IllegalNameException if the rootElementName is not 
     *     a legal XML 1.0 name
     * @throws MalformedURIException if the system ID is not a 
     *     syntactically correct URI, or if it contains a fragment
     *     identifier 
     */
    public DocType(String rootElementName, String systemID) {
        this(rootElementName, null, systemID);  
    }

    
    /**
     * <p>
     * Creates a new document type declaration with 
     * no public or system ID. It has the general form 
     * <code>&lt;!DOCTYPE rootElementName&gt;</code>.
     * </p>
     * 
     * @param rootElementName the name specified for the root element
     * 
     * @throws IllegalNameException if the rootElementName is not 
     *      a legal XML 1.0 name
     */
    public DocType(String rootElementName) {
        this(rootElementName, null, null);    
    }

    
    /**
     * <p>
     * Creates a new <code>DocType</code> that's a copy of its 
     * argument. The copy has the same data but no parent document.
     * </p>
     * 
     * @param doctype the <code>DocType</code> to copy
     */
    public DocType(DocType doctype) {
        this.internalDTDSubset = doctype.internalDTDSubset;
        this.publicID = doctype.publicID;
        this.systemID = doctype.systemID;
        this.rootName = doctype.rootName;    
    }
    
    
    private DocType() {}
    
    static DocType build(
      String rootElementName, String publicID, String systemID) {
        DocType result = new DocType();
        result.publicID = publicID;
        result.systemID = systemID;
        result.rootName = rootElementName;
        return result;
    }


    /**
     * <p>
     * Returns the name the document type declaration specifies 
     * for the root element. In an invalid document, this may 
     * not be the same as the actual root element name.
     * </p>
     * 
     * @return the declared name of the root element
     */
    public final String getRootElementName() {
        return rootName;
    }

    
    /**
     * <p>
     * Sets the name the document type declaration specifies 
     * for the root element. In an invalid document, this may 
     * not be the same as the actual root element name.
     * </p>
     * 
     * @param name the root element name given by
     *     the document type declaration
     * 
     * @throws IllegalNameException if the root element name is not 
     *     a legal XML 1.0 name
     */
    public void setRootElementName(String name) {
        _setRootElementName(name);
    }

    
    private void _setRootElementName(String name) {
        Verifier.checkXMLName(name);
        this.rootName = name;
    }


    /**
     * <p>
     * Returns the complete internal DTD subset.
     * White space may not be preserved completely accurately,
     * but all declarations should be in place. 
     * </p>
     * 
     * @return the internal DTD subset
     */
    public final String getInternalDTDSubset() {
        return internalDTDSubset;
    }

    
    /**
     * <p>
     * Sets the internal DTD subset; that is the part of the DTD 
     * between <code>[</code> and <code>]</code>. Changing the 
     * internal DTD subset does not affect the instance document. 
     * That is, default attribute values and attribute types 
     * specified in the new internal DTD subset are not applied to the
     * corresponding elements in the instance document. Furthermore,
     * there's no guarantee that the instance document is or is not
     * valid with respect to the declarations in the new internal 
     * DTD subset.
     * </p>
     * 
     * @param subset the internal DTD subset
     * 
     * @throws IllegalDataException if subset is not 
     *     a legal XML 1.0 internal DTD subset
     * 
     * @since 1.1
     */
    public final void setInternalDTDSubset(String subset) {
        
        if (subset != null && subset.length() > 0) {
            Verifier.checkInternalDTDSubset(subset);
            fastSetInternalDTDSubset(subset);
        }
        else {
            this.internalDTDSubset = "";              
        }
        
    }

    
    final void fastSetInternalDTDSubset(String internalSubset) {
        this.internalDTDSubset = internalSubset;   
    }

    
    /**
     * <p>
     * Returns the public ID of the external DTD subset. 
     * This is null if there is no external DTD subset
     * or if it does not have a public identifier.
     * </p>
     * 
     * @return the public ID of the external DTD subset.
     */
    public final String getPublicID() { 
        return publicID;
    }

    
    /**
     * <p>
     * Sets the public ID for the external DTD subset.
     * This can only be set after a system ID has been set, 
     * because XML requires that all document type declarations 
     * with public IDs have system IDs. Passing null removes 
     * the public ID. 
     * </p>
     * 
     * @param id the public identifier of the external DTD subset
     * 
     * @throws IllegalDataException if the public ID does not satisfy
     *      the rules for public IDs in XML 1.0
     * @throws WellformednessException if no system ID has been set
     */
    public void setPublicID(String id) {  
        _setPublicID(id);     
    }   

    
    private void _setPublicID(String id) {
        
        if (systemID == null && id != null) {
            throw new WellformednessException(
              "Cannot have a public ID without a system ID"
            );   
        }
        
        if (id != null) {
            int length = id.length();
            if (length != 0) {
                if (Verifier.isXMLSpaceCharacter(id.charAt(0))) {
                    throw new IllegalDataException("Initial white space "
                      + "in public IDs is not round trippable.");           
                }
                if (Verifier.isXMLSpaceCharacter(id.charAt(length - 1))) {
                    throw new IllegalDataException("Trailing white space " 
                      + "in public IDs is not round trippable.");           
                }
                
                for (int i = 0; i < length; i++) {
                    char c = id.charAt(i);
                    if (!isXMLPublicIDCharacter(c)) {
                        throw new IllegalDataException("The character 0x"   
                          + Integer.toHexString(c) 
                          + " is not allowed in public IDs");
                    }
                    if (c == ' ' && id.charAt(i-1) == ' ') {
                        throw new IllegalDataException("Multiple consecutive "
                          + "spaces in public IDs are not round trippable.");  
                    }
                }  
            }
        }
        this.publicID = id;
        
    }


    /**
     * <p>
     * Returns the system ID of the external DTD subset. 
     * This is a URL. It is null if there is no external DTD subset.
     * </p>
     * 
     * @return the URL for the external DTD subset.
     */
    public final String getSystemID() { 
        return systemID;
    }

    
    /**
     * <p>
     * Sets the system ID for the external DTD subset.
     * This must be a a relative or absolute  URI with no fragment
     * identifier. Passing null removes the system ID, but only if  
     * the public ID has been removed first. Otherwise,
     * passing null causes a <code>WellformednessException</code>.
     * </p>
     * 
     * @param id the URL of the external DTD subset
     * 
     * @throws MalformedURIException if the system ID is not a 
     *     syntactically correct URI, or if it contains a fragment
     *     identifier 
     * @throws WellformednessException if the public ID is non-null 
     *     and you attempt to remove the system ID
     */
    public void setSystemID(String id) {
        _setSystemID(id);
    }

    
    private void _setSystemID(String id) {
        
        if (id == null && publicID != null) {
            throw new WellformednessException(
             "Cannot remove system ID without removing public ID first"
            );   
        }

        if (id != null) {
            
            Verifier.checkURIReference(id);
            
            if (id.indexOf('#') != -1) {
                MalformedURIException ex = new MalformedURIException(
                 "System literals cannot contain fragment identifiers"
                );
                ex.setData(id);
                throw ex;
            }
        }
        
        this.systemID = id;
        
    }


    /**
     * <p>
     * Returns the empty string. XPath 1.0 does not define a value 
     * for document type declarations.
     * </p>
     * 
     * @return an empty string
     */
    public final String getValue() {
        return "";
    }


    /**
     * <p>
     * Throws <code>IndexOutOfBoundsException</code> because 
     * document type declarations do not have children.
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
    public final Node getChild(int position) {
        throw new IndexOutOfBoundsException(
          "LeafNodes do not have children");        
    }

    
    /**
     * <p>
     * Returns 0 because document type declarations do not have 
     * children.
     * </p>
     * 
     * @return zero
     */
    public final int getChildCount() {
        return 0;   
    }
    
    
    /**
     * <p>
     * Returns a string form of the 
     * <code>DocType</code> suitable for debugging
     * and diagnosis. It deliberately does not return 
     * an actual XML document type declaration. 
     * </p>
     * 
     * @return a string representation of this object
     */
    public final String toString() {
        return "[" + getClass().getName() + ": " + rootName + "]";
    }

    
    /**
     * <p>
     *   Returns a copy of this <code>DocType</code> 
     *   which has the same system ID, public ID, root element name,
     *   and internal DTD subset, but does not belong to a document.
     *   Thus, it can be inserted into a different document.
     * </p>
     * 
     * @return a deep copy of this <code>DocType</code> 
     *     that is not part of a document
     */
    public Node copy() {      
        return new DocType(this);
    }
    

    /**
     * <p>
     *  Returns a string containing the actual XML
     *  form of the document type declaration represented
     *   by this object. For example, 
     *  <code>&lt;!DOCTYPE book SYSTEM "docbookx.dtd"></code>. 
     * </p>
     * 
     * @return a <code>String</code> containing 
     *      an XML document type declaration
     */
    public final String toXML() { 
          
        StringBuffer result = new StringBuffer();
        result.append("<!DOCTYPE ");
        result.append(rootName);
        if (publicID != null) {
            result.append(" PUBLIC \"");
            result.append(publicID);
            result.append("\" \"");
            result.append(systemID);
            result.append('"');
        } 
        else if (systemID != null) {
            result.append(" SYSTEM \"");
            result.append(systemID);
            result.append('"');
        } 
        
        if (internalDTDSubset.length() != 0) {
            result.append(" [\n");    
            result.append(internalDTDSubset);   
            result.append(']'); 
        }
        
        result.append(">");
        return result.toString();
    }


    boolean isDocType() {
        return true;   
    } 

    
    private static boolean isXMLPublicIDCharacter(char c) {

        // PubidChar ::= #x20 | #xD | #xA | [a-zA-Z0-9] | [-'()+,./:=?;!*#@$_%]
        // but I'm deliberately not allowing carriage return and linefeed
        // because the parser normalizes them to a space. They are not
        // roundtrippable.
        switch(c) {
            case ' ': return true;
            case '!': return true;
            case '"': return false;
            case '#': return true;
            case '$': return true;
            case '%': return true;
            case '&': return false;
            case '\'': return true;
            case '(': return true;
            case ')': return true;
            case '*': return true;
            case '+': return true;
            case ',': return true;
            case '-': return true;
            case '.': return true;
            case '/': return true;
            case '0': return true;
            case '1': return true;
            case '2': return true;
            case '3': return true;
            case '4': return true;
            case '5': return true;
            case '6': return true;
            case '7': return true;
            case '8': return true;
            case '9': return true;
            case ':': return true;
            case ';': return true;
            case '<': return false;
            case '=': return true;
            case '>': return false;
            case '?': return true;
            case '@': return true;
            case 'A': return true;
            case 'B': return true;
            case 'C': return true;
            case 'D': return true;
            case 'E': return true;
            case 'F': return true;
            case 'G': return true;
            case 'H': return true;
            case 'I': return true;
            case 'J': return true;
            case 'K': return true;
            case 'L': return true;
            case 'M': return true;
            case 'N': return true;
            case 'O': return true;
            case 'P': return true;
            case 'Q': return true;
            case 'R': return true;
            case 'S': return true;
            case 'T': return true;
            case 'U': return true;
            case 'V': return true;
            case 'W': return true;
            case 'X': return true;
            case 'Y': return true;
            case 'Z': return true;
            case '[': return false;
            case '\\': return false;
            case ']': return false;
            case '^': return false;
            case '_': return true;
            case '`': return false;
            case 'a': return true;
            case 'b': return true;
            case 'c': return true;
            case 'd': return true;
            case 'e': return true;
            case 'f': return true;
            case 'g': return true;
            case 'h': return true;
            case 'i': return true;
            case 'j': return true;
            case 'k': return true;
            case 'l': return true;
            case 'm': return true;
            case 'n': return true;
            case 'o': return true;
            case 'p': return true;
            case 'q': return true;
            case 'r': return true;
            case 's': return true;
            case 't': return true;
            case 'u': return true;
            case 'v': return true;
            case 'w': return true;
            case 'x': return true;
            case 'y': return true;
            case 'z': return true;
        }

        return false;
        
    }

    
}