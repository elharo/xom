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
 * @version 1.0a1
 *
 */
class NonVerifyingFactory extends NodeFactory {

    
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
     * @return a <code>Nodes</code> containing 
     *     the new <code>Attribute</code>
     */
    public Nodes makeAttribute(String name, String URI, 
      String value, Attribute.Type type) {
        return new Nodes(Attribute.build(name, URI, value, type));
    }

    
    /**
     * <p>
     * Creates a new <code>Comment</code>.
     * </p>
     * 
     * @param data the complete text content of the comment
     * 
     * @return a <code>Nodes</code> containing 
     *     the new <code>Comment</code>
     */
    public Nodes makeComment(String data) {
        return new Nodes(Comment.build(data));   
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
     * @return a <code>Nodes</code> containing 
     *     the new <code>DocType</code>
     */
    public Nodes makeDocType(String rootElementName, 
      String publicID, String systemID) {
        return new Nodes(DocType.build(rootElementName, publicID, systemID));    
    }

    
     /**
     * <p>
     * Creates a new <code>Text</code> node.
     * </p>
     * 
     * @param data the complete text content of the node
     * 
     * @return a <code>Nodes</code> containing 
     *     the new <code>Text</code>
     */
    public Nodes makeText(String data) {
        return new Nodes(Text.build(data));  
    }

    
    /**
     * <p>
     * Creates a new <code>CDATASection</code> node.
     * </p>
     * 
     * @param data the complete text content of the node
     * 
     * @return a <code>Nodes</code> containing 
     *     the new <code>Text</code>
     */
    Nodes makeCDATASection(String data) {
        return new Nodes(CDATASection.build(data));  
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
     * @return a <code>Nodes</code> containing 
     *     the new <code>ProcessingInstruction</code>
     */
    public Nodes makeProcessingInstruction(
      String target, String data) {
        return new Nodes(ProcessingInstruction.build(target, data)); 
    }

    
    void addAttribute(Element element, Attribute attribute) {
        element.fastAddAttribute(attribute);
    }
  
    

}
