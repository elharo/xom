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

package nu.xom.converters;

import nu.xom.Attribute;
import nu.xom.Comment;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParentNode;
import nu.xom.ProcessingInstruction;
import nu.xom.Text;
import nu.xom.XMLException;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
// Many DOM interfaces such as Element and Document
// have name conflicts with XOM classes.
// Thus they cannot be imported, and this class
// must use their fully package qualified names.


/**
 * <p>
 * Converts XOM <code>Document</code> objects to and from DOM
 * <code>Document</code> objects. This class can also
 * convert many DOM node objects into the corresponding 
 * XOM node objects. However, the reverse is not possible because
 * DOM objects cannot live outside their containing 
 * <code>Document</code>
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d25
 *
 */
public class DOMConverter {

    
    // prevent instantiation
    private DOMConverter() {}
    
    
    /**
     * <p>
     * DOM violates the namespaces specification by mapping  
     * the <code>xmlns</code> prefix to the namespace URI
     * <code>http://www.w3.org/2000/xmlns/</code>.
     * </p>
     */
    private final static String XMLNS_NAMESPACE 
      = "http://www.w3.org/2000/xmlns/";

    
    /**
     * <p>
     * Translates a DOM <code>org.w3c.dom.Document</code> object 
     * into an equivalent <code>nu.xom.Document</code> object.
     * The original DOM document is not changed.
     * Some DOM <code>Document</code> objects cannot 
     * be serialized as namespace well-formed XML, and  
     * thus cannot be converted to XOM.
     * </p>
     * 
     * @param domDocument the DOM document to translate
     * @return a XOM document
     * 
     * @throws XMLException if the DOM document is not a well-formed 
     *     XML document
     */
    public static Document convert(org.w3c.dom.Document domDocument) {
        
        org.w3c.dom.Element domRoot = domDocument.getDocumentElement();
        Element xomRoot = convert(domRoot);
        Document xomDocument = new Document(xomRoot);
        
        org.w3c.dom.Node current = domDocument.getFirstChild();
        
        // prolog
        for (int position = 0; 
             current.getNodeType() != org.w3c.dom.Node.ELEMENT_NODE; 
             position++, current = current.getNextSibling()) {
            xomDocument.insertChild(convert(current), position);
        }
        // root element       
        current = current.getNextSibling();
        
        // epilog
        while (current != null) {
            xomDocument.appendChild(convert(current));
            current = current.getNextSibling();   
        }       
                       
        return xomDocument;
    }

    
    /**
     * <p>
     * Translates a DOM <code>org.w3c.dom.DocumentFragment</code>  
     * object into an equivalent <code>nu.xom.Nodes</code> object.
     * The original DOM document fragment is not changed.
     * Some DOM <code>DocumentFragment</code> objects cannot 
     * be serialized as namespace well-balanced XML, and  
     * thus cannot be converted to XOM.
     * </p>
     * 
     * @param fragment the DOM document fragment to translate
     * 
     * @return a <code>Nodes</code> containing the converted 
     *     fragment members
     * 
     * @throws XMLException if the DOM object is not a well-balanced 
     *     XML fragment
     */
    public static Nodes convert(DocumentFragment fragment) {
        
        Nodes result = new Nodes();  
        NodeList children = fragment.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            result.append(convert(children.item(i)));
        }
     
        return result;
        
    }

    
    private static Node convert(org.w3c.dom.Node node) {
        
        int type = node.getNodeType();
        switch (type) {
            case org.w3c.dom.Node.ELEMENT_NODE:
                return convert((org.w3c.dom.Element) node);   
            case org.w3c.dom.Node.COMMENT_NODE:
                return convert((org.w3c.dom.Comment) node);   
            case org.w3c.dom.Node.DOCUMENT_TYPE_NODE:
                return convert((org.w3c.dom.DocumentType) node);   
            case org.w3c.dom.Node.TEXT_NODE:
                return convert((org.w3c.dom.Text) node);
            case org.w3c.dom.Node.CDATA_SECTION_NODE:
                return convert((org.w3c.dom.Text) node);
            case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
                return convert((org.w3c.dom.ProcessingInstruction) node);
            default:   
                throw new XMLException(
                  "Unexpected DOM node type: " + type);
        }
        
    }

    
    /**
     * <p>
     * Translates a DOM <code>org.w3c.dom.Comment</code> object 
     * into an equivalent <code>nu.xom.Comment</code> object.
     * The original DOM object is not changed.
     * Some DOM <code>Comment</code> objects cannot 
     * be serialized as well-formed XML, and  
     * thus cannot be converted to XOM.
     * </p>
     * 
     * @param comment the DOM comment to translate
     * @return a XOM comment
     * 
     * @throws XMLException if the DOM comment is not a well-formed 
     *     XML comment
     */
    public static Comment convert(org.w3c.dom.Comment comment) {       
        return new Comment(comment.getNodeValue());
    }

    /**
     * <p>
     * Translates a DOM <code>org.w3c.dom.Text</code> object 
     * into an equivalent <code>nu.xom.Text</code>.
     * This method will also convert <code>org.w3c.dom.CDATA</code>
     * objects. The original DOM object is not changed.
     * Some DOM <code>Text</code> objects cannot 
     * be serialized as well-formed XML, and  
     * thus cannot be converted to XOM.
     * </p>
     * 
     * @param text the DOM text to translate
     * @return a XOM text
     * 
     * @throws XMLException if the DOM text is not a well-formed 
     *     XML text
     */
    public static Text convert(org.w3c.dom.Text text) {       
        return new Text(text.getNodeValue());       
    }

    
    /**
     * <p>
     * Translates a DOM <code>org.w3c.dom.Attr</code> object 
     * into an equivalent <code>nu.xom.Attribute</code> object.
     * The original DOM object is not changed.
     * Some DOM <code>Attr</code> objects cannot 
     * be serialized as well-formed XML, and  
     * thus cannot be converted to XOM. Furthermore, DOM uses 
     * <code>Attr</code> objects to represent namespace declarations.
     * XOM does not. Converting an <code>Attr</code> object that
     * represents an <code>xmlns</code> or 
     * <code>xmlns:<i>prefix</i></code> attribute will cause an 
     * exception.
     * </p>
     * 
     * @param attribute the DOM <code>Attr</code> to translate
     * @return the equivalent XOM <code>Attribute</code>
     * 
     * @throws XMLException if the DOM <code>Attr</code>  
     *     is a namespace declaration or is not a well-formed 
     *     XML attribute
     */
    public static Attribute convert(Attr attribute) {       
        String name = attribute.getName();
        String uri = attribute.getNamespaceURI();
        if (uri == null) uri = "";
        return new Attribute(name, uri, attribute.getNodeValue());       
    }


    /**
     * <p>
     * Translates a DOM <code>org.w3c.dom.ProcessingInstruction</code> 
     * object into an equivalent 
     * <code>nu.xom.ProcessingInstruction</code> object.
     * The original DOM object is not changed.
     * Some DOM <code>ProcessingInstruction</code> objects cannot 
     * be serialized as well-formed XML, and  
     * thus cannot be converted to XOM.
     * </p>
     * 
     * @param pi the DOM <code>ProcessingInstruction</code> to 
     *    convert
     * @return a XOM <code>ProcessingInstruction</code>
     * 
     * @throws XMLException if the DOM <code>ProcessingInstruction</code> 
     *     is not a well-formed XML processing instruction
     */
    public static ProcessingInstruction convert(
        org.w3c.dom.ProcessingInstruction pi) {
        return new ProcessingInstruction(
          pi.getTarget(), pi.getNodeValue());
    }

    
    /**
     * <p>
     * Translates a DOM <code>org.w3c.dom.DocumentType</code> 
     * object into an equivalent <code>nu.xom.DocType</code> object.
     * The original DOM object is not changed. The internal DTD subset
     * is not converted, but the root element name, system identifier,
     * and public identifier are.  Some DOM <code>DocumentType</code> 
     * objects cannot  be serialized as well-formed XML, and  
     * thus cannot be converted to XOM.
     * </p>
     * 
     * @param doctype the DOM <code>DocumentType</code> to convert
     * @return the equivalent XOM <code>DocType</code>
     * 
     * @throws XMLException if the DOM <code>DocumentType</code> 
     *     is not a well-formed XML document type declaration
     */
    public static DocType convert(org.w3c.dom.DocumentType doctype) {

        DocType result =
            new DocType(
                doctype.getName(),
                doctype.getPublicId(),
                doctype.getSystemId());

        return result;

    }

    
    /**
     * <p>
     * Translates a DOM <code>org.w3c.dom.Element</code> 
     * object into an equivalent <code>nu.xom.Element</code> object.
     * The original DOM object is not changed. Some DOM 
     * <code>Element</code> objects cannot be serialized as
     * namespace well-formed XML, and thus cannot be converted to XOM.
     * </p>
     * 
     * @param element the DOM <code>Element</code> to convert
     * @return the equivalent XOM <code>Element</code>
     * 
     * @throws XMLException if the DOM <code>Element</code> 
     *     is not a well-formed XML element
     */
    public static Element convert(org.w3c.dom.Element element) {
        
        org.w3c.dom.Node current = element;
        Element result = makeElement(element);
        ParentNode parent = result;
        boolean backtracking = false;
        while (current != null) {
            if (current.hasChildNodes() && !backtracking) {
                current = current.getFirstChild();
                backtracking = false;
            }
            else if (current == element) {
                break;   
            }
            else if (current.getNextSibling() != null) {
                current = current.getNextSibling();
                backtracking = false;
            }
            else {
                current = current.getParentNode();
                backtracking = true;
                parent = parent.getParent();
                continue;
            }
            
            int type = current.getNodeType();
            if (type == org.w3c.dom.Node.ELEMENT_NODE) {
                Element child = makeElement((org.w3c.dom.Element) current);
                parent.appendChild(child);
                parent = child;
            }
            else {
                Node child = convert(current);
                parent.appendChild(child);
            }
            
        }
        
        return result;  
        
    }
 
    
    private static Element makeElement(org.w3c.dom.Element element) {
        String namespaceURI = element.getNamespaceURI();
        String tagName = element.getTagName();
        Element result = new Element(tagName, namespaceURI);
        
        // fill element's attributes and additional namespace declarations
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            org.w3c.dom.Attr attribute = (org.w3c.dom.Attr) attributes.item(i);
            String name = attribute.getName();
            String uri = attribute.getNamespaceURI();
            String value = attribute.getValue();
            if (uri == null) uri = "";
            if (uri.equals(XMLNS_NAMESPACE)) { 
                if (name.equals("xmlns")) continue;
                String prefix = name.substring(name.indexOf(':') + 1);
                String currentURI = result.getNamespaceURI(prefix);
                if (!value.equals(currentURI)) {
                    result.addNamespaceDeclaration(prefix, value); 
                }
            }
            else { 
                result.addAttribute(new Attribute(name, uri, value));
            }
        }
        return result;
    }


    /**
     * <p>
     * Translates a XOM <code>nu.xom.Document</code> object
     * into an equivalent <code>org.w3c.dom.Document</code> 
     * object. The original XOM document is not changed.
     * Since DOM2 internal subsets are read-only,
     * the internal DTD subset is not converted. 
     * All other aspects of the document should be  
     * translated without a problem.
     * </p>
     * 
     * @param document the XOM document to translate
     * @param impl the specific DOM implementation into which this
     *     document will be converted 
     * 
     * @return a DOM document
     */
    public static org.w3c.dom.Document convert(Document document, 
      DOMImplementation impl) {

        Element root = document.getRootElement();
        String rootName = root.getQualifiedName();
        String rootNamespace = root.getNamespaceURI();
        DocType doctype = document.getDocType();     
        DocumentType domDOCTYPE = null;
        if (doctype != null) {
            domDOCTYPE = impl.createDocumentType(rootName, 
            doctype.getPublicID(), doctype.getSystemID());   
        }      
        
        org.w3c.dom.Document domDoc 
         = impl.createDocument(rootNamespace, rootName, domDOCTYPE);
        org.w3c.dom.Element domRoot = domDoc.getDocumentElement();
        
        boolean beforeRoot = true;
        for (int i = 0; i < document.getChildCount(); i++) {
            Node original = document.getChild(i);
            // Need to test positioning of doctype
            if (original instanceof DocType) continue;
            else if (original instanceof Element) {
                convert((Element) original, domDoc);
                beforeRoot = false;   
            }
            else {
                org.w3c.dom.Node domNode = convert(original, domDoc);
                if (beforeRoot) domDoc.insertBefore(domNode, domRoot);
                else domDoc.appendChild(domNode);
            }
        } 
        
        return domDoc;
    }

    
    private static org.w3c.dom.Node convert(
      Node node, org.w3c.dom.Document document) {
        
        if (node instanceof Text) {
            return convert((Text) node, document);
        }
        else if (node instanceof Element) {
            return convert((Element) node, document);
        }
        else if (node instanceof Comment) {
            return convert((Comment) node, document);
        }
        else if (node instanceof ProcessingInstruction) {
            return convert((ProcessingInstruction) node, document);
        }
        else {
            throw new XMLException(
              "Unexpected node type: " + node.getClass().getName()
            );   
        }
         
    }

    
    private static org.w3c.dom.Comment convert(
      Comment comment, org.w3c.dom.Document document) {
        return document.createComment(comment.getValue());   
    }

    
    private static org.w3c.dom.Text convert(
      Text text, org.w3c.dom.Document document) {
        return document.createTextNode(text.getValue());        
    }

    
    private static org.w3c.dom.ProcessingInstruction convert(
      ProcessingInstruction pi, org.w3c.dom.Document document) {
        return document.createProcessingInstruction(
          pi.getTarget(), pi.getValue());        
    }

    
    private static org.w3c.dom.Element convert(
      Element element, org.w3c.dom.Document document) {
        
        org.w3c.dom.Element result;
        String namespace = element.getNamespaceURI();   
         
        if (element.getParent() instanceof Document) {
            result = document.getDocumentElement();   
        } 
        else if (namespace.equals("")) {
            result = document.createElement(
              element.getQualifiedName());
        }
        else {
            result = document.createElementNS(
              namespace, element.getQualifiedName());
        }
                
        for (int i = 0; i < element.getAttributeCount(); i++) {
            Attribute attribute = element.getAttribute(i);
            String attns = attribute.getNamespaceURI();
            Attr attr;
            if (attns.equals("")) {
                attr = document.createAttribute(attribute.getLocalName());
                result.setAttributeNode(attr);
            }
            else {
                attr = document.createAttributeNS(
                  attns, attribute.getQualifiedName()
                );
                result.setAttributeNodeNS(attr);
            }
            attr.setValue(attribute.getValue());
        }
        
        for (int i = 0; i < element.getNamespaceDeclarationCount(); i++) {
            String additionalPrefix = element.getNamespacePrefix(i);
            String uri = element.getNamespaceURI(additionalPrefix);

            ParentNode parentNode = element.getParent();
            if (parentNode instanceof Element) {
               Element parentElement = (Element) parentNode;   
               if (uri.equals(
                 parentElement.getNamespaceURI(additionalPrefix))) {
                    continue; 
               }
            }
            else if (uri.equals("")) { //parent is Document or null
                continue; // no need to say xmlns=""   
            }

            if ("".equals(additionalPrefix)) {
                Attr attr = document.createAttributeNS(
                  XMLNS_NAMESPACE, "xmlns"
                );
                result.setAttributeNodeNS(attr);
                attr.setValue(uri);                
            }
            else {
                Attr attr = document.createAttributeNS(
                  XMLNS_NAMESPACE, 
                  "xmlns:" + additionalPrefix
                );
                result.setAttributeNodeNS(attr);
                attr.setValue(uri);
            }
        }
        
        
        // children ???? remove the recursion
        for (int i = 0; i < element.getChildCount(); i++) {
            result.appendChild(convert(element.getChild(i), document)); 
        }
        
        return result;  
        
    }

    
}
