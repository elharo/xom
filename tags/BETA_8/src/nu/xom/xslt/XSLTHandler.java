/* Copyright 2002-2004 Elliotte Rusty Harold
   
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
   subject line. The XOM home page is temporarily located at
   http://www.cafeconleche.org/XOM/  but will eventually move
   to http://www.xom.nu/  */

package nu.xom.xslt;

import java.util.Stack;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.NamespaceConflictException;
import nu.xom.Node;
import nu.xom.NodeFactory;
import nu.xom.Nodes;
import nu.xom.ParentNode;
import nu.xom.XMLException;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

/**
 * <p> 
 * As currently designed this class is non-public and never
 * reused. A new XSLTHandler is used for each call to transform().
 * Therefore we do not actually need to reset. This is important
 * because some XSLT processors call startDocument() and 
 * endDocument() and some don't, especially when the output
 * of a transform is a document fragment.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0b7
 *
 */
class XSLTHandler 
  implements ContentHandler, LexicalHandler {

    private Nodes        result;
    private Stack        parents;
    private NodeFactory  factory;
    private StringBuffer buffer;
    
    
    XSLTHandler(NodeFactory factory) {
        this.factory = factory; 
        result   = new Nodes();
        parents  = new Stack();
        buffer   = new StringBuffer();
    }   
    
    
    Nodes getResult() {
        flushText(); // to handle case where there's no endDocument
        return result;
    }
    
    
    public void setDocumentLocator(Locator locator) {}
    public void startDocument() {}
    public void endDocument() {}
  
    private Element current;
    
    public void startElement(String namespaceURI, String localName, 
     String qualifiedName, Attributes attributes) {
        
        flushText();
        
        // mix namespaceDeclarations into attributes
        for (int i = 0; i < attributes.getLength(); i++) {
            namespaceDeclarations.addAttribute(
              attributes.getURI(i),
              attributes.getLocalName(i),
              attributes.getQName(i),
              attributes.getType(i),
              attributes.getValue(i)
            );
        }
        attributes = namespaceDeclarations;
        
        Element element 
          = factory.startMakingElement(qualifiedName, namespaceURI);
        
        if (parents.isEmpty()) {
            // won't append until finishMakingElement()
            current = element; 
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
            
            Nodes nodes = factory.makeAttribute(
              attributeName, 
              namespace, 
              value, 
              Attribute.Type.UNDECLARED
            ); 
            for (int j=0; j < nodes.size(); j++) {
                Node node = nodes.get(j);
                if (node instanceof Attribute) {
                    Attribute attribute = (Attribute) node;
                    while (true) {
                        try {
                            element.addAttribute(attribute);
                            break;
                        }
                        catch (NamespaceConflictException ex) {
                            // According to section 7.1.3 of XSLT spec we 
                            // need to remap the prefix here; ideally the
                            // XSLT processor should do this but many don't
                            // for instance, see 
                            // http://nagoya.apache.org/bugzilla/show_bug.cgi?id=5389
                            attribute.setNamespace(
                              "p"+attribute.getNamespacePrefix(), 
                              attribute.getNamespaceURI()
                            );
                        }
                    }
                }
                else {
                    element.appendChild(node);   
                }
            }   
        }
        
        // Attach any additional namespaces
        for (int i = 0; i < attributes.getLength(); i++) {
            String qName = attributes.getQName(i);
            if (qName.startsWith("xmlns:")) {               
                String namespaceName = attributes.getValue(i);
                String namespacePrefix = qName.substring(6);
                String currentValue
                   = element.getNamespaceURI(namespacePrefix); 
                if (!namespaceName.equals(currentValue)) {
                    try {
                        element.addNamespaceDeclaration(
                          namespacePrefix, namespaceName);
                    }
                    catch (NamespaceConflictException ex) {
                        // skip it; see attribset40 test case;
                        // This should only happen if an attribute's
                        // namespace conflicts with the element's 
                        // namespace; in which case we already remapped
                        // the prefix when adding the attribute
                    }
                }              
            }   
            else if (qName.equals("xmlns")) {               
                String namespaceName = attributes.getValue(i);
                if (namespaceName == null) { // Work around a Xalan bug
                    namespaceName = "";
                }
                String namespacePrefix = "";
                String currentValue 
                  = element.getNamespaceURI(namespacePrefix); 
                if (!namespaceName.equals(currentValue)) {
                    try {
                        element.addNamespaceDeclaration(namespacePrefix, 
                         namespaceName);
                    }
                    catch (NamespaceConflictException ex) {
                       // work around Bug 27937 in Xalan
                       // http://nagoya.apache.org/bugzilla/show_bug.cgi?id=27937
                       // Xalan sometimes use the XML namespace 
                       // http://www.w3.org/XML/1998/namespace where it
                       // should use the empty string
                       if ("http://www.w3.org/XML/1998/namespace".equals(namespaceName)
                         && "".equals(namespacePrefix)) {
                            element.addNamespaceDeclaration("", "");                           
                       }
                    }
                } 
            }             
        }  
        
        // reset namespaceDeclarations
        namespaceDeclarations = new AttributesImpl();
        
    }
  
    
    public void endElement(String namespaceURI, String localName, 
      String qualifiedName) {
        
        flushText();
        Element element = (Element) parents.pop();
        if (parents.isEmpty()) {
            Nodes nodes = factory.finishMakingElement(current);
            for (int i = 0; i < nodes.size(); i++) {
                result.append(nodes.get(i));
            }
            current = null;
        }
        else {
            Nodes nodes = factory.finishMakingElement(element);
            ParentNode parent = element.getParent();
            element.detach();
            for (int i = 0; i < nodes.size(); i++) {
                Node node = nodes.get(i);
                if (node instanceof Attribute) {
                    ((Element) parent).addAttribute((Attribute) node);
                }
                else {
                    parent.appendChild(node);
                }
            }
        }

    }
  
    
    public void characters(char[] text, int start, int length) {
        buffer.append(text, start, length); 
    }
 
    
    // accumulate all text that's in the buffer into a text node
    private void flushText() {
        if (buffer.length() > 0) {
            Nodes text = factory.makeText(buffer.toString());
            addToResultTree(text);
            buffer = new StringBuffer();
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
        else if ("javax.xml.transform.disable-output-escaping".equals(target)
          || "javax.xml.transform.enable-output-escaping".equals(target)) { 
            // Xalan workaround
            return;   
        }
        
        flushText();
        // Xalan fails to split the ?> before passing such data to 
        // this method, so we have to do it
        int position = data.indexOf("?>");
        while (position != -1) {
            data = data.substring(0, position) + "? >" + data.substring(position+2);
            position = data.indexOf("?>");
        }
        Nodes nodes = factory.makeProcessingInstruction(target, data);
        addToResultTree(nodes);

    }

    
    private void addToResultTree(Nodes nodes) {
        
        if (parents.isEmpty()) {
            for (int i = 0; i < nodes.size(); i++) {
                result.append(nodes.get(i));          
            }            
        }
        else {
            ParentNode parent = (ParentNode) parents.peek();
            for (int i = 0; i < nodes.size(); i++) {
                Node node = nodes.get(i);
                if (node instanceof Attribute) {
                    ((Element) parent).addAttribute((Attribute) node);
                }
                else {
                    parent.appendChild(node);
                }            
            }
        }
        
    }

    
    public void endPrefixMapping(String prefix) {}
    
    
    private AttributesImpl namespaceDeclarations = new AttributesImpl();
    
    public void startPrefixMapping(String prefix, String uri) {
        
       if ("".equals(prefix)) {
           namespaceDeclarations.addAttribute("", "xmlns", "xmlns", "CDATA", uri);
       }
       else {
           namespaceDeclarations.addAttribute("", "xmlns:" + prefix, "xmlns:" + prefix, "CDATA", uri);           
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
        
        String data = new String(text, start, length);
        // Xalan should add spaces as necessary to split up double hyphens
        // in comments but it doesn't
        int position = data.indexOf("--");
        while (position != -1) {
            data = data.substring(0, position) + "- -" + data.substring(position+2);
            position = data.indexOf("--");
        }
        if (data.endsWith("-")) data += ' ';
        
        addToResultTree(factory.makeComment(data));
        
    } 
     
    
}