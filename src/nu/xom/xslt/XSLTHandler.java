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

package nu.xom.xslt;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import nu.xom.Attribute;
import nu.xom.Comment;
import nu.xom.Element;
import nu.xom.NodeFactory;
import nu.xom.Nodes;
import nu.xom.ParentNode;
import nu.xom.ProcessingInstruction;
import nu.xom.Text;
import nu.xom.XMLException;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

/**
 * <p> 
 * As currently designed this class is non-public and never
 * reused. A new XSLTHanlder is used for each call to transform().
 * Therefore we do not actually need to reset. This is important
 * because some XSLT processors call startDocument() and 
 * endDocument() and some don't, especially when the output
 * of a transform is a document frgament.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d22
 *
 */
class XSLTHandler 
  implements ContentHandler, LexicalHandler {

    private Nodes    result;
    private Stack       parents;
    private NodeFactory factory;
    private Map         prefixes; // In scope right now
    
    XSLTHandler(NodeFactory factory) {
        if (factory == null) {
            throw new NullPointerException("Factory cannot be null");
        }
        this.factory = factory; 
        result = new Nodes();
        parents = new Stack();
        buffer = new StringBuffer();
        prefixes = new HashMap();
    }   
    
    Nodes getResult() {
        flushText(); // to handle case where there's no endDocument
        return result;
    }
    
    public void setDocumentLocator(Locator locator) {}
    public void startDocument() {}
    public void endDocument() {}
  
    public void startElement(String namespaceURI, String localName, 
     String qualifiedName, Attributes attributes) {
        
        flushText();
        Element element 
          = factory.startMakingElement(qualifiedName, namespaceURI);
        
        if (parents.isEmpty()) {
           result.append(element); 
        }
        else {
            ParentNode parent = (ParentNode) parents.peek();
            parent.appendChild(element);
        }
        parents.push(element);
        
        
        // Attach the attributes
        for (int i = 0; i < attributes.getLength(); i++) {
            String attributeName = attributes.getQName(i);
            // handle namespaces later
            if (attributeName.equals("xmlns") 
              || attributeName.startsWith("xmlns:")) {
                continue;
            }
            String namespace = attributes.getURI(i);
            String value = attributes.getValue(i);
            Attribute attribute = factory.makeAttribute(
              attributeName, 
              namespace, 
              value, 
              getType(attributes.getType(i))
            );
            element.addAttribute(attribute);    
        }

        // Attach any additional namespaces
        Iterator iterator = prefixes.keySet().iterator();
        while (iterator.hasNext()) {
            String prefix = (String) iterator.next();  
            String currentURI = element.getNamespaceURI(prefix);
            String newURI 
              = (String) ((Stack) prefixes.get(prefix)).peek(); 
            if (!newURI.equals(currentURI)) {
                if (!prefix.equals("")) {
                    element.addNamespaceDeclaration(prefix, newURI);
                }
            }
        }
    }
    
    private Attribute.Type getType(String saxType) {
    
        if (saxType.equals("CDATA")) return Attribute.Type.CDATA;
        if (saxType.equals("ID")) return Attribute.Type.ID;
        if (saxType.equals("IDREF")) return Attribute.Type.IDREF;
        if (saxType.equals("IDREFS")) return Attribute.Type.IDREFS;
        if (saxType.equals("NMTOKEN")) return Attribute.Type.NMTOKEN;
        if (saxType.equals("NMTOKENS")) return Attribute.Type.NMTOKENS;
        if (saxType.equals("ENTITY")) return Attribute.Type.ENTITY;
        if (saxType.equals("ENTITIES")) return Attribute.Type.ENTITIES;
        if (saxType.equals("NOTATION")) return Attribute.Type.NOTATION;
        // non-standard but some parsers use this
        if (saxType.equals("ENUMERATION")) {
            return Attribute.Type.ENUMERATION;
        }
        if (saxType.startsWith("(")) return Attribute.Type.ENUMERATION;
    
        return Attribute.Type.UNDECLARED;
        
    }
  
    public void endElement(String namespaceURI, String localName, 
      String qualifiedName) {
        flushText();
       // System.err.println("Closing " + qualifiedName);
        parents.pop(); 
    }
  
    private StringBuffer buffer;
  
    public void characters(char[] text, int start, int length) {
        buffer.append(text, start, length); 
    }
 
    // accumulate all text that's in the buffer into a text node
    private void flushText() {
        if (buffer.length() > 0) {
            Text data = factory.makeText(buffer.toString());
            if (parents.isEmpty()) {
                result.append(data); 
            }
            else {
                ParentNode parent = (ParentNode) parents.peek();
                parent.appendChild(data);
            }    
            buffer.setLength(0);
        }   
    }
  
    public void ignorableWhitespace(char[] text, int start, int length) {
        characters(text, start, length);
    }
  
    public void processingInstruction(String target, String data) 
      throws SAXException {

        // See http://saxon.sourceforge.net/saxon6.5.2/extensibility.html#Writing-output-filters
        // to understand why we need to work around Saxon here
        if ("saxon:warning".equals(target)) {
            throw new SAXException("continue");   
        }

        flushText();
                
        ProcessingInstruction instruction 
         = factory.makeProcessingInstruction(target, data);
        if (parents.isEmpty()) {
            result.append(instruction); 
        }
        else {
            ParentNode parent = (ParentNode) parents.peek();
            parent.appendChild(instruction);
        }

    }

    public void endPrefixMapping(String prefix) {
        Stack uris = (Stack) prefixes.get(prefix);
        uris.pop();
    }
    
    public void startPrefixMapping(String prefix, String uri) {
        if (uri == null) uri = "";
        Stack uris = (Stack) prefixes.get(prefix);
        if (uris == null) {
            uris = new Stack();
            prefixes.put(prefix, uris);
        }
        
        if (uris.isEmpty()) {
            uris.push(uri);   
        }
        else {
             String current = (String) uris.peek();
             if (!uri.equals(current)) uris.push(uri);
            // System.err.println("Prefix: " + prefix);
        }
        
    }

    public void skippedEntity(String name) {
        flushText();
        throw new XMLException("Could not resolve entity " + name);                         
    }
    
    // LexicalHandler events
    public void startCDATA() {}
    public void endCDATA() {}
    public void startDTD(String name, String publicID, String systemID) {}
    public void endDTD() {}
    public void startEntity(String name) {}
    public void endEntity(String name) {}

    public void comment(char[] text, int start, int length) {
        flushText();
        Comment comment = factory.makeComment(new String(text, start, length));
        if (parents.isEmpty()) {
            result.append(comment); 
        }
        else {
            ParentNode parent = (ParentNode) parents.peek();         
            parent.appendChild(comment);
        }
    } 
        
}