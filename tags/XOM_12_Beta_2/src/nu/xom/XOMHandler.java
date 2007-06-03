/* Copyright 2002-2006 Elliotte Rusty Harold
   
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

import java.util.ArrayList;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.Locator;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

/**
 * @author Elliotte Rusty Harold
 * @version 1.2b1
 *
 */
class XOMHandler 
  implements ContentHandler, LexicalHandler, DeclHandler, DTDHandler {

    protected Document     document;
    protected String       documentBaseURI;
    
    // parent is never null. It is the node we're adding children 
    // to. current corresponds to the most recent startElement()
    // method and may be null if we've skipped it (makeElement
    // returned null.) If we didn't skip it, then parent and
    // current should be the same node.
    protected ParentNode   parent;
    protected ParentNode   current;
    protected ArrayList    parents;
    protected boolean      inProlog;
    protected boolean      inDTD;
    protected int          position; // current number of items in prolog
    protected Locator      locator; 
    protected DocType      doctype;
    protected StringBuffer internalDTDSubset;
    protected NodeFactory  factory;
              boolean      usingCrimson = false;
    
    
    XOMHandler(NodeFactory factory) {
        this.factory = factory; 
    }   
    
    
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    
    Document getDocument() {
        return document;
    }


    // See http://www.servlets.com/archive/servlet/ReadMsg?msgId=554071&listName=jdom-interest
    // This method is called to avoid leaking document sized memory
    // when a Builder is not imediately reused
    void freeMemory() {
        document = null;
        parent = null;
        current = null;
        parents = null;
        locator = null;
        doctype = null;
        internalDTDSubset = null;
    }

    
    public void startDocument() {
        
        inDTD = false;
        document = factory.startMakingDocument();
        parent = document;
        current = document;
        parents = new ArrayList();
        parents.add(document);
        inProlog = true;
        position = 0;
        textString = null;
        doctype = null;
        if (locator != null) {
            documentBaseURI = locator.getSystemId();
            // According to the XML spec, 
            // "It is an error for a fragment identifier 
            // (beginning with a # character) to be part of a system identifier"
            // but some parsers including Xerces seem to get this wrong, so we'll 
            document.setBaseURI(documentBaseURI);
        }
        buffer = null;
        
    }
  
    
    public void endDocument() {
        factory.finishMakingDocument(document);
        parents.remove(parents.size()-1);
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
        parents.add(element);
        
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
            // XXX pull out length
            
            // XXX we've got a pretty good guess at how many attributes there
            // will be here; we should ensureCapacity up to that length
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
                    int numberChildren = 0;
                    for (int j=0; j < nodes.size(); j++) {
                        Node node = nodes.get(j);
                        if (node.isAttribute()) {
                            factory.addAttribute(element, (Attribute) node);
                        }
                        else {
                            factory.insertChild(element, node, numberChildren++);   
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
                    if (!namespaceName.equals(currentValue) && ! namespacePrefix.equals(element.getNamespacePrefix())) {
                        element.addNamespaceDeclaration(
                          namespacePrefix, namespaceName);
                    }              
                }   
                else if (qName.equals("xmlns")) {               
                    String namespaceName = attributes.getValue(i);
                    String namespacePrefix = "";
                    String currentValue 
                      = element.getNamespaceURI(namespacePrefix); 
                    if (!namespaceName.equals(currentValue) && ! "".equals(element.getNamespacePrefix())) {
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
        current = (ParentNode) parents.remove(parents.size()-1);
        flushText();
        
        if (current != null) {
            parent = current.getParent();
            Nodes result = factory.finishMakingElement((Element) current);
            
            // Optimization for default case where result only contains current
            if (result.size() != 1 || result.get(0) != current) {            
                if (!parent.isDocument()) {
                    // allow factories to detach the element itself in
                    // finishMakingElement
                    int childCount = parent.getChildCount();
                    try {
                        parent.removeChild(childCount - 1);
                    }
                    catch (IndexOutOfBoundsException ex) {
                        throw new XMLException(
                          "Factory detached element in finishMakingElement()", 
                          ex);
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
    
    
    static Attribute.Type convertStringToType(String saxType) {
    
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
  
    
    protected String textString = null;
    protected StringBuffer buffer = null;
  
    public void characters(char[] text, int start, int length) {
        
        if (length <= 0) return;
        if (textString == null) textString = new String(text, start, length);
        else {
            if (buffer == null) buffer = new StringBuffer(textString);
            buffer.append(text, start, length);
        }
        if (finishedCDATA) inCDATA = false;
        
    }
 
    
    // accumulate all text that's in the buffer into a text node
    private void flushText() {
        
        if (buffer != null) {
            textString = buffer.toString();
            buffer = null;
        }
        
        if (textString != null) {
            Nodes result;
            if (!inCDATA) {
                result = factory.makeText(textString);
            }
            else {
                result = factory.makeCDATASection(textString);
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
            textString = null;
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
        if (inDTD && !inInternalSubset()) return;
        Nodes result = factory.makeProcessingInstruction(target, data);
        
        for (int i = 0; i < result.size(); i++) {
            Node node = result.get(i);
            if (!inDTD) {
                if (inProlog) {
                    parent.insertChild(node, position);
                    position++;
                }
                else {
                    if (node.isAttribute()) {
                        ((Element) parent).addAttribute((Attribute) node);
                    }
                    else parent.appendChild(node);
                }
            }
            else {
                if (node.isProcessingInstruction() || node.isComment()) {
                    internalDTDSubset.append("  ");            
                    internalDTDSubset.append(node.toXML());            
                    internalDTDSubset.append("\n");            
                }
                else {
                    throw new XMLException("Factory tried to put a " 
                      + node.getClass().getName() 
                      + " in the internal DTD subset");   
                }
            }            
        }

    }


    // XOM handles this with attribute values; not prefix mappings
    public void startPrefixMapping(String prefix, String uri) {}
    public void endPrefixMapping(String prefix) {}

    public void skippedEntity(String name) {
        
        // Xerces 2.7 now calls this method in the DTD 
        // for parameter entities it doesn't resolve. We can ignore these.
        if (name.startsWith("%")) return;
        flushText();
        throw new XMLException("Could not resolve entity " + name);
        
    }
    
    
    // LexicalHandler events
    public void startDTD(String rootName, String publicID, 
      String systemID) {
        
        inDTD = true;
        Nodes result = factory.makeDocType(rootName, publicID, systemID);
        for (int i = 0; i < result.size(); i++) {
            Node node = result.get(i);
            document.insertChild(node, position);
            position++;
            if (node.isDocType()) {
                DocType doctype = (DocType) node;
                internalDTDSubset = new StringBuffer(); 
                this.doctype = doctype;
            }
        }
        
    }
     
    
    public void endDTD() {
        
        inDTD = false;
        if (doctype != null) {
            doctype.fastSetInternalDTDSubset(internalDTDSubset.toString());
        }
        
    }

    
    protected boolean inExternalSubset = false;

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
    
    
    protected boolean inCDATA = false;
    protected boolean finishedCDATA = false;
    
    public void startCDATA() {
        if (textString == null) inCDATA = true;
        finishedCDATA = false;
    }
    
    
    public void endCDATA() {
        finishedCDATA = true;
    }

    
    public void comment(char[] text, int start, int length) {
        
        if (!inDTD) flushText();
        if (inDTD && !inInternalSubset()) return;

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
                    internalDTDSubset.append("  ");            
                    internalDTDSubset.append(node.toXML());            
                    internalDTDSubset.append("\n");            
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
        
        if (inInternalSubset() && doctype != null) {
            internalDTDSubset.append("  <!ELEMENT ");
            internalDTDSubset.append(name); 
            internalDTDSubset.append(' '); 
            internalDTDSubset.append(model); 
            // workaround for Crimson bug
            if (model.indexOf("#PCDATA") > 0 && model.indexOf('|') > 0) {
                if (model.endsWith(")")) {
                    internalDTDSubset.append('*');  
                }
            }
            internalDTDSubset.append(">\n"); 
        }
        
    }
  
    
    // This method only behaves properly when called from the DeclHandler 
    // and DTDHandler callbacks; i.e. from inside the DTD;
    // It is not intended for use anywhere in the document.
    protected boolean inInternalSubset() {

        if (!usingCrimson && !inExternalSubset) return true;
        String currentURI = locator.getSystemId();
        if (currentURI == this.documentBaseURI) return true;
        if (currentURI.equals(this.documentBaseURI)) return true;
        return false;
        
    }


    public void attributeDecl(String elementName, 
      String attributeName, String type, String mode, 
      String defaultValue)  {
    
        // workaround for Crimson bug
        if (type.startsWith("NOTATION ")) {
            if (type.indexOf('(') == -1 && ! type.endsWith(")")) {
                type = "NOTATION (" + type.substring("NOTATION ".length()) + ")";
            }
        }
        
        if (inInternalSubset() && doctype != null) {
            internalDTDSubset.append("  <!ATTLIST ");
            internalDTDSubset.append(elementName);
            internalDTDSubset.append(' ');
            internalDTDSubset.append(attributeName);
            internalDTDSubset.append(' ');
            internalDTDSubset.append(type);
            if (mode != null) {
            internalDTDSubset.append(' ');
                internalDTDSubset.append(mode);
            }
            if (defaultValue != null) {
                internalDTDSubset.append(' ');
                internalDTDSubset.append('"');
                internalDTDSubset.append(
                  escapeReservedCharactersInDefaultAttributeValues(defaultValue)
                );
                internalDTDSubset.append('\"');         
            }
            internalDTDSubset.append(">\n");   
        }
        
    }
  
    
    public void internalEntityDecl(String name, 
       String value) {   
        
        if (inInternalSubset() && doctype != null) {
            internalDTDSubset.append("  <!ENTITY ");
            if (name.startsWith("%")) {
                internalDTDSubset.append("% "); 
                internalDTDSubset.append(name.substring(1));
            }
            else {
                internalDTDSubset.append(name); 
            }
            internalDTDSubset.append(" \""); 
            internalDTDSubset.append(escapeReservedCharactersInDeclarations(value)); 
            internalDTDSubset.append("\">\n"); 
        }
        
    }
  
    
    public void externalEntityDecl(String name, 
       String publicID, String systemID) {

        if (inInternalSubset() && doctype != null) {
            internalDTDSubset.append("  <!ENTITY ");
            if (name.startsWith("%")) { 
                internalDTDSubset.append("% ");
                internalDTDSubset.append(name.substring(1));
            }
            else {
                internalDTDSubset.append(name);
            }
               
            if (locator != null && URIUtil.isAbsolute(systemID)) {
                String documentURL = locator.getSystemId();
                // work around Crimson style file:/root URLs
                if (documentURL != null) {
                    if (documentURL.startsWith("file:/") && !documentURL.startsWith("file:///")) {
                        documentURL = "file://" + documentURL.substring(5); 
                    }
                    if (systemID.startsWith("file:/") && !systemID.startsWith("file:///")) {
                        systemID = "file://" + systemID.substring(5); 
                    }
                    systemID = URIUtil.relativize(documentURL, systemID);
                }
            }

            if (publicID != null) { 
                internalDTDSubset.append(" PUBLIC \""); 
                internalDTDSubset.append(publicID); 
                internalDTDSubset.append("\" \""); 
                internalDTDSubset.append(systemID);       
            }
            else {
                // need to escape system ID???? could it contain an ampersand?
                internalDTDSubset.append(" SYSTEM \""); 
                internalDTDSubset.append(systemID); 
            }
            internalDTDSubset.append("\">\n");
            
        }
        
    }
    
    
    public void notationDecl(String name, String publicID, 
      String systemID) {
        
        if (systemID != null) {
            systemID = escapeReservedCharactersInDeclarations(systemID);
        }
        
        if (inInternalSubset() && doctype != null) {
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
            internalDTDSubset.append(">\n"); 
        }        
        
    }
   
    
    public void unparsedEntityDecl(String name, String publicID, 
     String systemID, String notationName) {
        
        // escapable characters????
        if (inInternalSubset() && doctype != null) {
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
            internalDTDSubset.append(">\n"); 
        }
        
    }
    
    
    private static String escapeReservedCharactersInDeclarations(String s) {
        
        int length = s.length();
        StringBuffer result = new StringBuffer(length);
        for (int i = 0; i < length; i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\r': 
                    result.append("&#x0D;");
                    break;
                case 14:
                    // placeholder for table lookup
                    break;
                case 15:
                    // placeholder for table lookup
                    break;
                case 16 :
                    // placeholder for table lookup
                    break;
                case 17:
                    // placeholder for table lookup
                    break;
                case 18:
                    // placeholder for table lookup
                    break;
                case 19:
                    // placeholder for table lookup
                    break;
                case 20:
                    // placeholder for table lookup
                    break;
                case 21:
                    // placeholder for table lookup
                    break;
                case 22:
                    // placeholder for table lookup
                    break;
                case 23:
                    // placeholder for table lookup
                    break;
                case 24:
                    // placeholder for table lookup
                    break;
                case 25:
                    // placeholder for table lookup
                    break;
                case 26:
                    // placeholder for table lookup
                    break;
                case 27:
                    // placeholder for table lookup
                    break;
                case 28:
                    // placeholder for table lookup
                    break;
                case 29:
                    // placeholder for table lookup
                    break;
                case 30:
                    // placeholder for table lookup
                    break;
                case 31:
                    // placeholder for table lookup
                    break;
                case ' ':
                    result.append(' ');
                    break;
                case '!':
                    result.append('!');
                    break;
                case '\"':
                    result.append("&#x22;");
                    break;
                case '#':
                    result.append('#');
                    break;
                case '$':
                    result.append('$');
                    break;
                case '%':
                    result.append("&#x25;");
                    break;
                case '&':
                    result.append("&#x26;");
                    break;
                default:
                    result.append(c);
            }
        }
        
        return result.toString();
        
    }

    
    private static String escapeReservedCharactersInDefaultAttributeValues(String s) {
        
        int length = s.length();
        StringBuffer result = new StringBuffer(length);
        for (int i = 0; i < length; i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\r': 
                    result.append("&#x0D;");
                    break;
                case 14:
                    // placeholder for table lookup
                    break;
                case 15:
                    // placeholder for table lookup
                    break;
                case 16 :
                    // placeholder for table lookup
                    break;
                case 17:
                    // placeholder for table lookup
                    break;
                case 18:
                    // placeholder for table lookup
                    break;
                case 19:
                    // placeholder for table lookup
                    break;
                case 20:
                    // placeholder for table lookup
                    break;
                case 21:
                    // placeholder for table lookup
                    break;
                case 22:
                    // placeholder for table lookup
                    break;
                case 23:
                    // placeholder for table lookup
                    break;
                case 24:
                    // placeholder for table lookup
                    break;
                case 25:
                    // placeholder for table lookup
                    break;
                case 26:
                    // placeholder for table lookup
                    break;
                case 27:
                    // placeholder for table lookup
                    break;
                case 28:
                    // placeholder for table lookup
                    break;
                case 29:
                    // placeholder for table lookup
                    break;
                case 30:
                    // placeholder for table lookup
                    break;
                case 31:
                    // placeholder for table lookup
                    break;
                case ' ':
                    result.append(' ');
                    break;
                case '!':
                    result.append('!');
                    break;
                case '\"':
                    result.append("&quot;");
                    break;
                case '#':
                    result.append('#');
                    break;
                case '$':
                    result.append('$');
                    break;
                case '%':
                    result.append("&#x25;");
                    break;
                case '&':
                    result.append("&amp;");
                    break;
                case '\'':
                    result.append('\'');
                    break;
                case '(':
                    result.append('(');
                    break;
                case ')':
                    result.append(')');
                    break;
                case '*':
                    result.append('*');
                    break;
                case '+':
                    result.append('+');
                    break;
                case ',':
                    result.append(',');
                    break;
                case '-':
                    result.append('-');
                    break;
                case '.':
                    result.append('.');
                    break;
                case '/':
                    result.append('/');
                    break;
                case '0':
                    result.append('0');
                    break;
                case '1':
                    result.append('1');
                    break;
                case '2':
                    result.append('2');
                    break;
                case '3':
                    result.append('3');
                    break;
                case '4':
                    result.append('4');
                    break;
                case '5':
                    result.append('5');
                    break;
                case '6':
                    result.append('6');
                    break;
                case '7':
                    result.append('7');
                    break;
                case '8':
                    result.append('8');
                    break;
                case '9':
                    result.append('9');
                    break;
                case ':':
                    result.append(':');
                    break;
                case ';':
                    result.append(';');
                    break;
                case '<': 
                    result.append("&lt;");
                    break;
                default:
                    result.append(c);
            }
        }
        
        return result.toString();
        
    }

    
}