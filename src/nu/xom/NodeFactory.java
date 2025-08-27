/* Copyright 2002-2005, 2019 Elliotte Rusty Harold
   
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
 * Builders use a <code>NodeFactory</code> object
 * to construct each <code>Node</code> object (<code>Element</code>,
 * <code>Text</code>, <code>Attribute</code>, etc.) they add to the
 * tree. The default implementation simply calls the relevant 
 * constructor, stuffs the resulting <code>Node</code> object in a 
 * length one <code>Nodes</code> object, and returns it. 
 * </p>
 *
 * <p>
 * Subclassing this class allows builders to produce
 * instance of subclasses (for example,
 * <code>HTMLElement</code>) instead of the
 * base classes.
 * </p>
 *
 * <p>
 * Subclasses can also filter content while building.
 * For example, namespaces could be added to or changed 
 * on all elements. Comments could be deleted. Processing
 * instructions can be changed into elements. An 
 * <code>xinclude:include</code> element could be replaced
 * with the content it references. All such changes must be 
 * consistent with the usual rules of well-formedness. For 
 * example, the <code>makeDocType()</code> method should not 
 * return a list containing two <code>DocType</code> objects
 * because an XML document can have at most one document type
 * declaration. Nor should it return a list containing an element,
 * because an element cannot appear in a document prolog. However,
 * it could return a list containing any number of comments and
 * processing instructions, and not more than one <code>DocType</code> 
 * object.
 * </p>
 *
 * @author Elliotte Rusty Harold
 * @version 1.3.1
 */
public class NodeFactory {

    /**
     * <p>
     * Constructs a new node factory.
     * </p>
     */
    public NodeFactory() {}
    
    /**
     * <p>
     * Creates a new element in the specified namespace 
     * with the specified name. The builder calls this
     * method to make the root element of the document.
     * </p>
     *
     * <p>
     * Subclasses may change the name, namespace, content, or other 
     * characteristics of the element returned.
     * The default implementation merely calls 
     * <code>startMakingElement</code>. However, when subclassing, it 
     * is often useful to be able to easily distinguish between the  
     * root element and a non-root element because the root element   
     * cannot be detached. Therefore, subclasses must not return null  
     * from this method. Doing so will cause a 
     * <code>NullPointerException</code>.
     * </p>
     *
     * @param name the qualified name of the element
     * @param namespace the namespace URI of the element
     * @return the new root element
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
     * too is filtered. To remove an entire element, return an empty
     * <code>Nodes</code> object from the 
     * <code>finishMakingElement()</code> method.
     * </p>
     *
     * @param name the qualified name of the element
     * @param namespace the namespace URI of the element
     * @return the new element
     */
    public Element startMakingElement(String name, String namespace) {
        return new Element(name, namespace);    
    }
   
    
    /**
     * <p>
     * Signals the end of an element. This method should return 
     * the <code>Nodes</code> to be added to the tree.
     * They need not contain the <code>Element</code> that 
     * was passed to this method, though most often they will.
     * By default the <code>Nodes</code> returned contain
     * only the built element. However, subclasses may return
     * a list containing any number of nodes, all of which will be 
     * added to the tree at the current position in the order given by 
     * the list (subject to the usual well-formedness constraints, of 
     * course. For instance, the list should not contain a 
     * <code>DocType</code> object unless the element is the root 
     * element, and the document does not already have a 
     * <code>DocType</code>). All of the nodes returned must be 
     * parentless. If this method returns an empty list,
     * then the element (including all its contents) is not included 
     * in the finished document.
     * </p>
     *
     * <p>
     * To process an element at a time, override this method in a 
     * subclass so that it functions as a callback. When you're done
     * processing the <code>Element</code>, return an empty list so  
     * that it will be removed from the tree and garbage collected.
     * Be careful not to return an empty list for the root element 
     * though. That is, when the element passed to this method is the 
     * root element, the list returned must contain exactly one 
     * <code>Element</code> object. The simplest way to check this 
     * is testing if <code>element.getParent() instanceof 
     * Document</code>.
     * </p>
     *
     * <p>
     *  Do not detach <code>element</code> or any of its ancestors 
     *  while inside this method. Doing so can royally muck up the 
     *  build.
     * </p>
     *
     * @param element the finished <code>Element</code>
     * @return the nodes to be added to the tree
     */
    public Nodes finishMakingElement(Element element) {
        return new Nodes(element);
    }


    /**
     * <p>
     * Creates a new <code>Document</code> object. 
     * The root element of this document is initially set to 
     * <code>&lt;root xmlns="https://xom.nu/fakeRoot"/></code>.
     * This is only temporary. As soon as the real root element's
     * start-tag is read, this element is replaced by the real root.
     * This fake root should never be exposed.
     * </p>
     *
     * <p>
     *  The builder calls this method at the beginning of
     *  each document, before it calls any other method in this class.
     *  Thus this is a useful place to perform per-document 
     *  initialization tasks.
     * </p>
     *
     * <p>
     *  Subclasses may change the root element, content, 
     *  or other characteristics of the document 
     *  returned. However, this method must not return null
     *  or the builder will throw a <code>ParsingException</code>.
     * </p>
     *
     * @return the newly created <code>Document</code>
     */
    public Document startMakingDocument() {
        return new Document(
          Element.build("root", "https://xom.nu/fakeRoot", "root")
        );  
    }
    
    
    /**
     * <p>
     * Signals the end of a document. The default implementation of   
     * this method does nothing. The builder does not 
     * call this  method if an exception is thrown while building 
     * a document.
     * </p>
     *
     * @param document the completed <code>Document</code>
     */
    public void finishMakingDocument(Document document) {}

    
    /**
     * <p>
     * Returns a new <code>Nodes</code> object containing an 
     * attribute in the specified namespace 
     * with the specified name and type.
     * </p>
     *
     * <p>
     * Subclasses may change the nodes returned from this method.
     * They may return a <code>Nodes</code> object containing any 
     * number of children and attributes which are appended and 
     * added to the current parent element. This <code>Nodes</code> 
     * object may not contain any <code>Document</code> objects.
     * All of the nodes returned must be parentless.
     * Subclasses may return an empty <code>Nodes</code> to indicate  
     * the attribute should not be created.
     * </p>
     *
     * @param name the prefixed name of the attribute
     * @param URI the namespace URI
     * @param value the attribute value
     * @param type the attribute type
     * @return the nodes to be added to the tree
     */
    public Nodes makeAttribute(String name, String URI, 
      String value, Attribute.Type type) {
        return new Nodes(new Attribute(name, URI, value, type));
    }

    
    /**
     * <p>
     * Returns a new <code>Nodes</code> object containing a 
     * comment with the specified text.
     * </p>
     *
     * <p>
     * Subclasses may change the content or other 
     * characteristics of the comment returned. 
     * Subclasses may change the nodes returned from this method.
     * They may return a <code>Nodes</code> object containing any 
     * number of children and attributes which are appended and 
     * added to the current parent element. This <code>Nodes</code> 
     * object should not contain any <code>Document</code> objects.
     * All of the nodes returned must be parentless.
     * Subclasses may return an empty <code>Nodes</code> to indicate  
     * the comment should not be included in the 
     * finished document.
     * </p>
     *
     * @param data the complete text content of the comment
     * @return the nodes to be added to the tree
     */
    public Nodes makeComment(String data) {
        return new Nodes(new Comment(data));   
    }

    
    /**
     * <p>
     * Returns a new <code>Nodes</code> object containing a 
     * <code>DocType</code> object with the specified root element
     * name, system ID, and public ID.
     * </p>
     *
     * <p>
     * Subclasses may change the root element name, public ID, 
     * system ID, or other characteristics of the <code>DocType</code> 
     * returned. Subclasses may change the nodes returned from this 
     * method. They may return a <code>Nodes</code> object containing  
     * any number of comments and processing instructions which are  
     * appended to the current parent node. This <code>Nodes</code> 
     * object may not contain any <code>Document</code>,
     * <code>Element</code>, <code>Attribute</code>, or 
     * <code>Text</code> objects. All of the nodes returned must be 
     * parentless. Subclasses may return an empty <code>Nodes</code> to   
     * indicate the <code>DocType</code> should not be included in the 
     * finished document.
     * </p>
     *
     * @param rootElementName the declared, qualified name 
     *   for the root element
     * @param publicID the public ID of the external DTD subset
     * @param systemID the URL of the external DTD subset
     * @return the nodes to be added to the document
     */
    public Nodes makeDocType(String rootElementName, 
      String publicID, String systemID) {
        return new Nodes(new DocType(rootElementName, publicID, systemID));    
    }


    /**
     * <p>
     * Returns a new <code>Nodes</code> object containing a 
     * text node with the specified content.
     * </p>
     *
     * <p>
     * Subclasses may change the content or other characteristics of 
     * the text returned. Subclasses may also change the nodes 
     * returned from this method. They may return a <code>Nodes</code> 
     * object containing any number of nodes which are added or 
     * appended to the current parent node. This <code>Nodes</code> 
     * object must not contain any <code>Document</code> nodes. All of 
     * the nodes returned must be parentless. Subclasses may return an 
     * empty <code>Nodes</code> to indicate the text should not be 
     * included in the finished document.
     * </p>
     *
     * @param data the complete text content of the node
     * @return the nodes to be added to the tree
     */
    public Nodes makeText(String data) {
        return new Nodes(new Text(data));  
    }

    
    /**
     * <p>
     * Returns a new <code>Nodes</code> object containing a 
     * <code>CDATASection</code> node with the specified content.
     * </p>
     *
     * @param data the complete text content of the node
     * @return the nodes to be added to the tree
     */
    Nodes makeCDATASection(String data) {
        return makeText(data);  
    } 


    /**
     * <p>
     * Returns a new <code>Nodes</code> object containing a 
     * new <code>ProcessingInstruction</code> object with
     * the specified target and data.
     * </p>
     *
     * <p>
     * Subclasses may change the target, data, or other 
     * characteristics of the <code>ProcessingInstruction</code>
     * returned. Subclasses may change the nodes returned from this 
     * method. They may return a <code>Nodes</code> object containing 
     * any  number of nodes which are added or
     * appended to the current parent node. This <code>Nodes</code> 
     * object must not contain any <code>Document</code> nodes. 
     * If the processing instruction appears in the prolog or epilog
     * of the document, then it must also not contain any 
     * <code>Element</code>, <code>Attribute</code>, or 
     * <code>Text</code> objects.
     * All of the nodes returned must be parentless. Subclasses 
     * may return an empty <code>Nodes</code> to indicate the  
     * processing instruction should not be included in the 
     * finished document.
     * </p>
     *
     * @param target the target of the processing instruction
     * @param data the data of the processing instruction
     * @return the nodes to be added to the tree
     */
    public Nodes makeProcessingInstruction(
      String target, String data) {
        return new Nodes(new ProcessingInstruction(target, data)); 
    }

    
    void addAttribute(Element element, Attribute attribute) {
        element.addAttribute(attribute);
    }
  
    
    void insertChild(Element element, Node child, int position) {
        element.insertChild(child, position);
    }
  
    
}
