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

import java.util.Stack;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.Locator;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

/**
 * @author Elliotte Rusty Harold
 * @version 1.0d23
 *
 */
class XOMHandler 
  implements ContentHandler, LexicalHandler, DeclHandler, DTDHandler {

    private Document     document;
    private String       documentBaseURI;
    
    // parent is never null. It is the node we're adding children 
    // to. current corresponds to the most recent startElement()
    // method and may be null if we've skipped it (makeElement
    // returned null.) If we didn't skip it, then parent and
    // current should be the same node.
    private ParentNode   parent;
    private ParentNode   current;
    private Stack        parents;
    private boolean      inProlog;
    private int          position; // current number of items in prolog
    private Locator      locator; 
    private DocType      doctype;
    private StringBuffer internalDTDSubset;
    private NodeFactory  factory;
    
    XOMHandler(NodeFactory factory) {
        this.factory = factory; 
    }   
    
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    Document getDocument() {
        return document;
    }

    public void startDocument() {
        document = factory.makeDocument();
        parent = document;
        current = document;
        parents = new Stack();
        parents.push(document);
        inProlog = true;
        position = 0;
        buffer = new StringBuffer();
        doctype = null;
        if (locator != null) {
            documentBaseURI = locator.getSystemId();
            document.setBaseURI(documentBaseURI);
        }
    }
  
    public void endDocument() {
        factory.endDocument(document);
        parents.pop();
    }
  
    public void startElement(String namespaceURI, String localName, 
      String qualifiedName, org.xml.sax.Attributes attributes) {
        
        flushText();
        Element element;
        if (parent != document) {
            element = factory.startMakingElement(qualifiedName, namespaceURI);
        }
        else {  // root
            element = factory.makeRootElement(qualifiedName, namespaceURI);
            if (element == null) { // null root; that's a no-no
                throw new NullPointerException(
                    "Factory failed to create root element."
                );   
            }
            document.setRootElement(element);
            inProlog = false;
        }
        
        current = element;
        // Need to push this, even if it's null 
        parents.push(element);
        
        if (element != null) { // wasn't filtered out
            if (parent != document) { 
                // a.k.a. parent not instanceof Document
                parent.appendChild(element);
            }
            // This is optimized for the very common case where 
            // everything in the document has the same actual base URI. 
            // It may add redundant base URIs in cases like XInclude 
            // where different parts of the document have different 
            // base URIs.
            if (locator != null) {
                 String baseURI = locator.getSystemId();
                 if (baseURI != null && !baseURI.equals(documentBaseURI)) {
                     element.setActualBaseURI(baseURI);
                 }
            } 
            
            // Attach the attributes; this must be done before the
            // namespaces are attached.           
            for (int i = 0; i < attributes.getLength(); i++) {
                String qName = attributes.getQName(i);
                if (qName.startsWith("xmlns:") || qName.equals("xmlns")) {               
                    continue;               
                }             
                else {
                    String namespace = attributes.getURI(i);
                    String value = attributes.getValue(i);
                    Nodes nodes = factory.makeAttribute(
                      qName, 
                      namespace, 
                      value, 
                      convertStringToType(attributes.getType(i))
                    );
                    for (int j=0; j < nodes.size(); j++) {
                        Node node = nodes.get(j);
                        if (node.isAttribute()) {
                            element.addAttribute((Attribute) node);
                        }
                        else {
                            element.appendChild(node);   
                        }
                    }
                }
            }

            // Attach the namespaces
            for (int i = 0; i < attributes.getLength(); i++) {
                String qName = attributes.getQName(i);
                if (qName.startsWith("xmlns:")) {               
                    String namespaceName = attributes.getValue(i);
                    String namespacePrefix = qName.substring(6);
                    String currentValue
                       = element.getNamespaceURI(namespacePrefix); 
                    if (!namespaceName.equals(currentValue)) {
                        element.addNamespaceDeclaration(
                          namespacePrefix, namespaceName);
                    }              
                }   
                else if (qName.equals("xmlns")) {               
                    String namespaceName = attributes.getValue(i);
                    String namespacePrefix = "";
                    String currentValue 
                      = element.getNamespaceURI(namespacePrefix); 
                    if (!namespaceName.equals(currentValue)) {
                        element.addNamespaceDeclaration(namespacePrefix, 
                         namespaceName);
                    }                
                }             
            }

            
            // this is the new parent
            parent = element;
        }
        
    }
    
    public void endElement(
      String namespaceURI, String localName, String qualifiedName) {
        
        // If we're immediately inside a skipped element
        // we need to reset current to null, not to the parent
        current = (ParentNode) parents.pop();
        flushText();
        
        if (current != null) {
            parent = current.getParent();
            Nodes result = factory.finishMakingElement((Element) current);
            
            // Optimization for default case where result only contains current
            if (result.size() != 1 || result.get(0) != current) {            
                if (!parent.isDocument()) {
                    parent.removeChild(parent.getChildCount() - 1);
                    for (int i=0; i < result.size(); i++) {
                        Node node = result.get(i);
                         if (node.isAttribute()) {
                             ((Element) parent).addAttribute((Attribute) node);
                         }
                         else {
                             parent.appendChild(node);   
                         }
                    }
                }
                else { // root element
                    Document doc = (Document) parent;
                    Element currentRoot = doc.getRootElement();
                    boolean beforeRoot = true;
                    for (int i=0; i < result.size(); i++) {
                        Node node = result.get(i);
                        if (node.isElement()) {
                            if (node != currentRoot) {   
                                if (!beforeRoot) {
                                    // already set root, oops
                                    throw new IllegalAddException("Factory returned multiple roots");   
                                }
                                doc.setRootElement((Element) node);
                            }
                            beforeRoot = false;
                        }
                        else if (beforeRoot) {
                            doc.insertChild(node, doc.indexOf(doc.getRootElement()));   
                        }
                        else {
                            doc.appendChild(node);   
                        }
                    }
                    if (beforeRoot) {
                        // somebody tried to replace the root element with
                        // no element at all. That's a no-no
                        throw new WellformednessException(
                          "Factory attempted to remove the root element");
                    }
                }
            }
        }
    }
    
    private static Attribute.Type convertStringToType(String saxType) {
    
        if (saxType.equals("CDATA"))    return Attribute.Type.CDATA;
        if (saxType.equals("ID"))       return Attribute.Type.ID;
        if (saxType.equals("IDREF"))    return Attribute.Type.IDREF;
        if (saxType.equals("IDREFS"))   return Attribute.Type.IDREFS;
        if (saxType.equals("NMTOKEN"))  return Attribute.Type.NMTOKEN;
        if (saxType.equals("NMTOKENS")) return Attribute.Type.NMTOKENS;
        if (saxType.equals("ENTITY"))   return Attribute.Type.ENTITY;
        if (saxType.equals("ENTITIES")) return Attribute.Type.ENTITIES;
        if (saxType.equals("NOTATION")) return Attribute.Type.NOTATION;
        
        // non-standard but some parsers use this
        if (saxType.equals("ENUMERATION")) {
            return Attribute.Type.ENUMERATION;
        } 
        if (saxType.startsWith("(")) return Attribute.Type.ENUMERATION;
    
        return Attribute.Type.UNDECLARED;
        
    }
  
    private StringBuffer buffer;
  
    public void characters(char[] text, int start, int length) {
        buffer.append(text, start, length); 
        if (finishedCDATA && length > 0) inCDATA = false;
    }
 
    // acumulate all text that's in the buffer into a text node
    private void flushText() {
        if (buffer.length() > 0) {
            Nodes result;
            if (!inCDATA) {
                result = factory.makeText(buffer.toString());
            }
            else {
                result = factory.makeCDATASection(buffer.toString());
            }
            for (int i=0; i < result.size(); i++) {
                Node node = result.get(i);
                if (node.isAttribute()) {
                    ((Element) parent).addAttribute((Attribute) node);
                }
                else {
                    parent.appendChild(node);   
                }
            }
            buffer = new StringBuffer();
        }
        inCDATA = false;
        finishedCDATA = false;
    }
  
    
    public void ignorableWhitespace(
      char[] text, int start, int length) {
        characters(text, start, length);
    }
  
    
    public void processingInstruction(String target, String data) {
        
        if (!inDTD) flushText();
        if (inExternalSubset) return;
        Nodes result = factory.makeProcessingInstruction(target, data);
        
        for (int i = 0; i < result.size(); i++) {
            Node node = result.get(i);
            if (!inDTD) {
                if (inProlog) {
                    parent.insertChild(node, position);
                    position++;
                }
                else {
                    if (node instanceof Attribute) {
                        ((Element) parent).addAttribute((Attribute) node);
                    }
                    else parent.appendChild(node);
                }
            }
            else {
                if (node.isProcessingInstruction() || node.isComment()) {
                    internalDTDSubset.append(node.toXML());            
                    internalDTDSubset.append('\n');            
                }
                else {
                    throw new XMLException("Factory tried to put a " 
                      + node.getClass().getName() 
                      + " in the internal DTD subset");   
                }
            }            
        }

    }


    // I handle this with attribute values; not prefix mappings
    public void startPrefixMapping(String prefix, String uri) {}
    public void endPrefixMapping(String prefix) {}

    public void skippedEntity(String name) {
        flushText();
        throw new XMLException("Could not resolve entity " + name);                        
    }
    
    // LexicalHandler events
    
    private boolean inDTD = false;

    public void startDTD(String rootName, String publicID, 
      String systemID) {
        inDTD = true;
        Nodes result = factory.makeDocType(rootName, publicID, systemID);
        for (int i = 0; i < result.size(); i++) {
            Node node = result.get(i);
            document.insertChild(node, position);
            position++;
            if (node.isDocType()) {
                DocType doctype =(DocType) node;
                internalDTDSubset = new StringBuffer(); 
                this.doctype = doctype;
            }
        }
     }
     
    public void endDTD() {
        inDTD = false;
        if (doctype != null) {
            doctype.setInternalDTDSubset(internalDTDSubset.toString());
        }
    }

    private boolean inExternalSubset = false;

    // We have a problem here. Xerces gets this right,
    // but Crimson and possibly other parsers don't properly
    // report these entities, or perhaps just not tag them
    // with [dtd] like they're supposed to.
    public void startEntity(String name) {
      if (name.equals("[dtd]")) inExternalSubset = true;
    }
    
    public void endEntity(String name) {
      if (name.equals("[dtd]")) inExternalSubset = false;    
    }
    
    private boolean inCDATA = false;
    private boolean finishedCDATA = false;
    
    public void startCDATA() {
        if (buffer.length() == 0) inCDATA = true;
        finishedCDATA = false;
    }
    
    public void endCDATA() {
        finishedCDATA = true;
    }

    public void comment(char[] text, int start, int length) {
        
        if (!inDTD) flushText();
        if (inExternalSubset) return;

        Nodes result = factory.makeComment(new String(text, start, length));
        
        for (int i = 0; i < result.size(); i++) {
            Node node = result.get(i);
            if (!inDTD) {
                if (inProlog) {
                    parent.insertChild(node, position);
                    position++;
                }
                else {
                    if (node instanceof Attribute) {
                        ((Element) parent).addAttribute((Attribute) node);
                    }
                    else parent.appendChild(node);
                }
            }
            else {
                if (node.isComment() || node.isProcessingInstruction()) {
                    internalDTDSubset.append(node.toXML());            
                    internalDTDSubset.append('\n');            
                }
                else {
                    throw new XMLException("Factory tried to put a " 
                      + node.getClass().getName() 
                      + " in the internal DTD subset");   
                }
            }            
        }

    }    
    
    public void elementDecl(String name, String model) {
        if (!inExternalSubset && doctype != null) {
            internalDTDSubset.append("  <!ELEMENT ");
            internalDTDSubset.append(name); 
            internalDTDSubset.append(' '); 
            internalDTDSubset.append(model); 
            internalDTDSubset.append('>'); 
        }
    }
  
    public void attributeDecl(String elementName, 
      String attributeName, String type, String mode, 
      String defaultValue)  {
    
        if (!inExternalSubset && doctype != null) {
            internalDTDSubset.append("  <!ATTLIST ");
            internalDTDSubset.append(elementName);
            internalDTDSubset.append(' ');
            internalDTDSubset.append(attributeName);
            internalDTDSubset.append(' ');
            internalDTDSubset.append(type);
            internalDTDSubset.append(' ');
            if (mode != null) {
                internalDTDSubset.append(mode);
            }
            if (defaultValue != null) {
                internalDTDSubset.append('"');
                internalDTDSubset.append(defaultValue);
                internalDTDSubset.append("\"");         
            }
            internalDTDSubset.append(">");   
        }
    }
  
    public void internalEntityDecl(String name, 
       String value) {   
        if (!inExternalSubset && doctype != null) {
            if (!name.startsWith("%")) { // ignore parameter entities
                internalDTDSubset.append("  <!ENTITY ");
                internalDTDSubset.append(name); 
                internalDTDSubset.append(" \""); 
                internalDTDSubset.append(value); 
                internalDTDSubset.append("\">"); 
            }
        }
    }
  
    public void externalEntityDecl(String name, 
       String publicID, String systemID) {
     
        if (!inExternalSubset && doctype != null) {
            if (!name.startsWith("%")) { // ignore parameter entities
                internalDTDSubset.append("  <!ENTITY ");
                if (publicID != null) { 
                    internalDTDSubset.append(name); 
                    internalDTDSubset.append(" PUBLIC \""); 
                    internalDTDSubset.append(publicID); 
                    internalDTDSubset.append("\" \""); 
                    internalDTDSubset.append(systemID);       
                }
                else {
                    internalDTDSubset.append(name); 
                    internalDTDSubset.append(" SYSTEM \""); 
                    internalDTDSubset.append(systemID); 
                }
                internalDTDSubset.append("\">"); 
            }
        }
    }
    
    public void notationDecl(String name, String publicID, 
      String systemID) {
        
        if (!inExternalSubset && doctype != null) {
            internalDTDSubset.append("  <!NOTATION ");
            internalDTDSubset.append(name); 
            if (publicID != null) {
                internalDTDSubset.append(" PUBLIC \""); 
                internalDTDSubset.append(publicID);
                internalDTDSubset.append('"'); 
                if (systemID != null) {
                    internalDTDSubset.append(" \"");                                     
                    internalDTDSubset.append(systemID);
                    internalDTDSubset.append('"');                                     
                }
            }
            else {
                internalDTDSubset.append(" SYSTEM \""); 
                internalDTDSubset.append(systemID);
                internalDTDSubset.append('"');                 
            }
            internalDTDSubset.append('>'); 
        }        
        
    }
   
    public void unparsedEntityDecl(String name, String publicID, 
     String systemID, String notationName) {
        if (!inExternalSubset && doctype != null) {
            internalDTDSubset.append("  <!ENTITY ");
            if (publicID != null) { 
                internalDTDSubset.append(name); 
                internalDTDSubset.append(" PUBLIC \""); 
                internalDTDSubset.append(publicID); 
                internalDTDSubset.append("\" \""); 
                internalDTDSubset.append(systemID); 
                internalDTDSubset.append("\" NDATA "); 
                internalDTDSubset.append(notationName);       
            }
            else {
                internalDTDSubset.append(name); 
                internalDTDSubset.append(" SYSTEM \""); 
                internalDTDSubset.append(systemID); 
                internalDTDSubset.append("\" NDATA "); 
                internalDTDSubset.append(notationName);     
            }
            internalDTDSubset.append('>'); 
        }
        
    }
 
}