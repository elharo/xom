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
 * Subclassing this class allows builders to produce
 * instance of subclasses (for example,
 * <code>HTMLElement</code>) instead of the
 * base classes.
 * </p>
 * 
 * <p>
 *   Subclasses can also filter content while building.
 *   For example, namespaces could be added to or changed 
 *   on all elements. Comments could be deleted. All such 
 *   changes must be consistent with the usual rules of 
 *   well-formedness.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d19
 * 
 */
public class NodeFactory {

    // suppose you want to change comments to Text or PI to
    // Element? Should some of these methods just return a Node,
    // or even a NodeList instead????
    // would need to get rid of fastAdd, but that might be doable
    // now that insertChild no longer uses instanceof
    // might also prevent sax filters from adding wrong thing
    // in wrong place

    /**
     * <p>
     * Creates a new <code>Element</code> in the specified namespace 
     * with the specified name. The <code>Builder</code> calls this
     * method to make the root element of the document.
     * </p>
     * 
     * <p>
     * Subclasses may change the name, namespace, content, or other 
     * characteristics of the <code>Element</code> returned.
     * The default implementation merely calls 
     * <code>makeElement</code>. However, when subclassing it is often
     * useful to be able to easily distinguish between the root element
     * and a non-root element because the root element cannot be 
     * detached. Therefore, subclasses must not return null from this 
     * method. Doing so will cause a <code>NullPointerException</code>.
     * </p>
     * 
     * @param name the qualified name of the element
     * @param namespace the namespace URI of the element
     * 
     * @return the new root <code>Element</code>
     */
    public Element makeRootElement(String name, String namespace) {
        return startMakingElement(name, namespace);    
    }

    /**
     * <p>
     * Creates a new <code>Element</code> in the specified namespace 
     * with the specified name.
     * </p>
     * 
     * <p>
     * Subclasses may change the name, namespace, content, or other 
     * characteristics of the <code>Element</code> returned.
     * Subclasses may return null to indicate the 
     * <code>Element</code> should not be created.
     * However, doing so will only remove the element's start-tag and
     * end-tag from the result tree. Any content inside the element  
     * will be attached to the element's parent by default, unless it
     * too is filtered. To remove an entire element, invoke
     * <code>element.detach()</code> from the <code>endElement()</code>
     * method.
     * </p>
     * 
     * @param name the qualified name of the element
     * @param namespace the namespace URI of the element
     * 
     * @return the new <code>Element</code>
     */
    public Element startMakingElement(String name, String namespace) {
        return new Element(name, namespace);    
    }
    
    // check that you can't use this method to bypass
    // add's checks on multiple parentage
    
    /**
     * <p>
     * The <code>Builder</code> calls this method to signal the 
     * end of an element. This method should return the
     * <code>Element</code> to be added to the tree.
     * This need not be the same <code>Element</code> that 
     * was passed to this method, though most often it will be.
     * The default implementation returns the built element.
     * If this method returns null, then the element
     * (including all its contents) is not included in the
     * finished document.
     * </p>
     * 
     * <p>
     * To process an element at a time, override this method in a 
     * subclass so that it functions as a callback. When you're done
     * processing the <code>Element</code>, return null so that it 
     * will be removed from the tree and garbage collected.
     * </p>
     * 
     * <p>
     *  Do not detach <code>element</code> or any of its ancestors 
     *  while inside this method. Doing so can royally muck up the 
     *  build.
     * </p>
     * 
     * @param element the finished <code>Element</code>
     * 
     * @return the element to be added to the tree
     * 
     */
    protected Element finishMakingElement(Element element) {
        return element;
    }

    /**
     * <p>
     * The <code>Builder</code> calls this method to signal the end 
     * of a document. The default implementation of this method  
     * does nothing.
     * </p>
     * 
     * @param document the completed <code>Document</code>
     */
    protected void endDocument(Document document) {}

    /**
     * <p>
     * Creates a new <code>Attribute</code> in the specified namespace 
     * with the specified name and type.
     * </p>
     * 
     * <p>
     * Subclasses may change the name, namespace, value, type, or other
     * characteristics of the <code>Attribute</code> returned.
     * Subclasses may return null to indicate the 
     * <code>Attribute</code> should not be created.
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
        return new Attribute(name, URI, value, type);
    }

    /**
     * <p>
     * Creates a new <code>Comment</code>.
     * </p>
     * 
     * <p>
     * Subclasses may change the content or other 
     * characteristics of the <code>Comment</code> 
     * returned. Subclasses may return null to indicate  
     * the <code>Comment</code> should not be created.
     * The <code>Comment</code> returned must not
     * have a parent.
     * </p>
     * 
     * @param data the complete text content of the comment
     * 
     * @return the new <code>Comment</code>
     */
    public Comment makeComment(String data) {
        return new Comment(data);   
    }

    /**
     * <p>
     * Creates a new <code>DocType</code> with a root element name,
     * a system ID, and a public ID.
     * </p>
     * 
     * <p>
     * Subclasses may change the root element name, public ID, 
     * system ID, or other characteristics of the <code>DocType</code> 
     * returned. Subclasses may return null to indicate the 
     * <code>DocType</code> should not be created.
     * The <code>DocType</code> returned must not
     * have a parent.
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
        return new DocType(rootElementName, publicID, systemID);    
    }

    /**
     * <p>
     * Creates a new <code>Document</code> with 
     * the specified root element.
     * </p>
     * 
     * <p>
     * Subclasses may change the root element, content, 
     * or other characteristics of the <code>Document</code> 
     * returned. However, this method must not return null
     * or the builder will throw a <code>NullPointerException</code>.
     * </p>
     * 
     * @param root the root <code>Element</code>
     * 
     * @return the newly created <code>Document</code>
     */
    public Document makeDocument(Element root) {
        return new Document(root);  
    }

    /**
     * <p>
     * Creates a new <code>Text</code> node.
     * </p>
     * 
     * <p>
     * Subclasses may change the content, 
     * or other characteristics of the <code>Text</code> 
     * returned. Subclasses may return null to indicate  
     * the <code>Text</code> object should not be created.
     * The <code>Text</code> returned must not have a parent.
     * </p>
     * 
     * @param data the complete text content of the node
     * 
     * @return the new <code>Text</code>
     */
    public Text makeText(String data) {
        return new Text(data);  
    }

    /**
     * <p>
     * Creates a new <code>Text</code> node.
     * The <code>Builder</code> calls this method
     * to create text nodes composed of white space
     * in places where the DTD says only child elements may appear.
     * SAX calls this ignorable white space, and the XML specification
     * calls this white space in element content.
     * </p>
     * 
     * <p>
     * The default implementation of this method merely delegates
     * to the <code>makeText</code> method. However, a subclass might
     * eliminate white space in element content from the constructed
     * tree by overriding this method so it always returns null.  
     * </p>
     * 
     * @param data the complete text content of the node
     * 
     * @return the new <code>Text</code>
     */
    public Text makeWhiteSpaceInElementContent(String data) {
        return makeText(data);  
    }

    /**
     * <p>
     * Creates a new <code>ProcessingInstruction</code> with
     * the specified target and data.
     * </p>
     * 
     * <p>
     * Subclasses may change the target, data, or other 
     * characteristics of the <code>ProcessingInstruction</code>
     * returned. Subclasses may return null to indicate the 
     * <code>ProcessingInstruction</code> should not be created.
     * The <code>ProcessingInstruction</code> returned must not
     * have a parent.
     * </p>
     * 
     * @param target the target of the processing instruction
     * @param data the data of the processing instruction
     * 
     * @return the new <code>ProcessingInstruction</code>
     */
    public ProcessingInstruction makeProcessingInstruction(
      String target, String data) {
        return new ProcessingInstruction(target, data); 
    }

}
