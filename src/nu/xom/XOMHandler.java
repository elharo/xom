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

import java.util.Stack;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.Locator;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

/**
 * @author Elliotte Rusty Harold
 * @version 1.0d22
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
        if (factory == null) {
            throw new NullPointerException("Factory cannot be null");
        }
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
            flushText();
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
            
            // Attach the attributes and namespaces
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
                else {
                    String namespace = attributes.getURI(i);
                    String value = attributes.getValue(i);
                    Attribute attribute = factory.makeAttribute(
                      qName, 
                      namespace, 
                      value, 
                      convertStringToType(attributes.getType(i))
                    );
                    if (attribute != null) element.fastAdd(attribute);
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
        
        if (current != null) {
            flushText();
            ParentNode temp = current.getParent();
            Element result = factory.finishMakingElement((Element) current);
            if (result != current) {
               temp.removeChild(temp.getChildCount() - 1);
               if (result != null) {
                   temp.appendChild(result);
               }   
            }
            parent = temp;
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
            Text data;
            if (!isIgnorable &&!inCDATA) {
                data = factory.makeText(buffer.toString());
            }
            else if (inCDATA) {
                data = factory.makeCDATASection(buffer.toString());
            }
            else data = factory.makeWhiteSpaceInElementContent(
              buffer.toString()
            );
            if (data != null) parent.appendChild(data);
            buffer = new StringBuffer();
        }
        isIgnorable = false;
        inCDATA = false;
        finishedCDATA = false;
    }
  
    private boolean isIgnorable = false;
  
    public void ignorableWhitespace(
      char[] text, int start, int length) {
        characters(text, start, length);
        isIgnorable = true;
    }
  
    public void processingInstruction(String target, String data) {
        ProcessingInstruction instruction 
         = factory.makeProcessingInstruction(target, data);
        if (instruction != null) {
            if (!inDTD) {
                flushText();
                if (inProlog) {
                    parent.insertChild(instruction, position);
                    position++;
                }
                else {
                    parent.appendChild(instruction);
                }
            }
            else if (!inExternalSubset) {
                internalDTDSubset.append(instruction.toXML());            
                internalDTDSubset.append('\n');            
            }
        }
    }


    // I handle this with attribute values; not prefix mappings
    public void startPrefixMapping(String prefix, String uri) {}
    public void endPrefixMapping(String prefix) {}

    public void skippedEntity(String name) {
        throw new XMLException("Could not resolve entity " + name);                        
    }
    
    // LexicalHandler events
    
    private boolean inDTD = false;

    public void startDTD(String rootName, String publicID, 
      String systemID) {
        inDTD = true;
        DocType doctype = factory.makeDocType(rootName, publicID, systemID);
        if (doctype != null) {
            internalDTDSubset = new StringBuffer(); 
            document.insertChild(doctype, position);
            position++;
            this.doctype = doctype;
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
        
        if (inDTD && inExternalSubset) return;
        Comment comment 
          = factory.makeComment(new String(text, start, length));
        if (comment != null) {
            if (!inDTD) {
                flushText();
                if (inProlog) {
                    parent.insertChild(comment, position);
                    position++;
                }
                else {
                    parent.appendChild(comment);
                }
            }
            else if (inDTD) {
                internalDTDSubset.append(comment.toXML());            
                internalDTDSubset.append('\n');            
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