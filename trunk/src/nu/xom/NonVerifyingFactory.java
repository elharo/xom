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
 * This class bypasses most of the usual verification checks on
 * input. The assumption is that the SAX parser has already done this.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d22
 *
 */
class NonVerifyingFactory extends NodeFactory {


    /**
     * <p>
     * Creates a new <code>Element</code> in no namespace with
     * the specified name.
     * </p>
     * 
     * @param name the local name of the element
     * 
     * @return the new <code>Element</code>
     */
    public Element makeElement(String name) {
        return Element.build(name, "");   
    }

    /**
     * <p>
     * Creates a new <code>Element</code> in the specified namespace 
     * with the specified name.
     * </p>
     * 
     * @param name the qualified name of the element
     * @param namespace the namespace URI
     * 
     * @return the new <code>Element</code>
     */
    public Element startMakingElement(String name, String namespace) {
        return Element.build(name, namespace);   
    }

    /**
     * <p>
     * Creates a new <code>Attribute</code> in the specified namespace 
     * with the specified name and type.
     * </p>
     * 
     * @param name the prefixed name of the attribute
     * @param URI the namespace URI
     * @param value the attribute value
     * @param type the attribute type
     * 
     * @return the new <code>Attribute</code>
     */
    public Attribute makeAttribute(String name, String URI, 
      String value, Attribute.Type type) {
        return Attribute.build(name, URI, value, type);
    }

    /**
     * <p>
     * Creates a new <code>Comment</code>.
     * </p>
     * 
     * @param data the complete text content of the comment
     * 
     * @return the new <code>Comment</code>
     */
    public Comment makeComment(String data) {
        return Comment.build(data);   
    }

    /**
     * <p>
     * Creates a new <code>DocType</code> with a root element name,
     * a system ID, and a public ID.
     * </p>
     * 
     * @param rootElementName the declared, qualified name 
     *   for the root element
     * @param publicID the public ID of the external DTD subset
     * @param systemID the URL of the external DTD subset
     * 
     * @return the new <code>DocType</code>
     */
    public DocType makeDocType(String rootElementName, 
      String publicID, String systemID) {
        return DocType.build(rootElementName, publicID, systemID);    
    }

    /**
     * <p>
     * Creates a new <code>DocType</code> with a root element name,
     * a system ID, and a null public ID.
     * </p>
     * 
     * @param rootElementName the declared, qualified name 
     *   for the root element
     * @param systemID the URL of the external DTD subset
     * 
     * @return the new <code>DocType</code>
     */
    public DocType makeDocType(String rootElementName,
      String systemID) {
        return DocType.build(rootElementName, null, systemID);    
    }

    /**
     * <p>
     * Creates a new <code>DocType</code> with a root element name
     * but no public or system ID.
     * </p>
     * 
     * @param rootElementName the declared, qualified name 
     *   for the root element
     * 
     * @return the new <code>DocType</code>
     */
    public DocType makeDocType(String rootElementName) {
        return DocType.build(rootElementName, null, null);    
    }

    /**
     * <p>
     * Creates a new <code>Text</code> node.
     * </p>
     * 
     * @param data the complete text content of the node
     * 
     * @return the new <code>Text</code>
     */
    public Text makeText(String data) {
        return Text.build(data);  
    }

    /**
     * <p>
     * Creates a new <code>CDATASection</code> node.
     * </p>
     * 
     * @param data the complete text content of the node
     * 
     * @return the new <code>Text</code>
     */
    Text makeCDATASection(String data) {
        return CDATASection.build(data);  
    }

    /**
     * <p>
     * Creates a new <code>ProcessingInstruction</code> with
     * the specified target and data.
     * </p>
     * 
     * @param target the target of the processing instruction
     * @param data the data of the processing instruction
     * 
     * @return the new <code>ProcessingInstruction</code>
     */
    public ProcessingInstruction makeProcessingInstruction(
      String target, String data) {
        return ProcessingInstruction.build(target, data); 
    }

}
