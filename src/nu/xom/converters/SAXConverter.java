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
import nu.xom.ParentNode;
import nu.xom.ProcessingInstruction;
import nu.xom.Text;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

/**
 * 
 * <p>
 * This class walks a XOM tree, while firing events into a
 * SAX2 <code>ContentHandler</code>.
 * </p>
  * 
 * @author Elliotte Rusty Harold
 * @version 1.0a1
 * 
 */
public class SAXConverter {

    
    private ContentHandler contentHandler;
    private LexicalHandler lexicalHandler;

    
    /**
     * <p>
     * Creates a new <code>SAXConverter</code>.
     * </p>
     * 
     * @param handler SAX2 <code>ContentHandler</code> 
     *     that receives the data
     * 
     * @throws NullPointerException if handler is null
     * 
     */
    public SAXConverter(ContentHandler handler) {
        setContentHandler(handler);
    }

    
    /**
     * <p>
     * Set the <code>ContentHandler</code> for this converter.
     * </p>
     * 
     * @param handler SAX2 <code>ContentHandler</code> that 
     *     receives the data
     * 
     * @throws NullPointerException if handler is null
     * 
     */
    public void setContentHandler(ContentHandler handler) {
        if (handler == null) {
            throw new NullPointerException(
              "ContentHandler must be non-null."
            );
        }
        this.contentHandler = handler;
    }

    
    /**
     * <p>
     * Returns the <code>ContentHandler</code>.
     * </p>
     * 
     * @return SAX2 <code>ContentHandler</code> that receives the data
     */
    public ContentHandler getContentHandler() {
        return this.contentHandler;
    }


    /**
     * <p>
     * Sets the optional <code>LexicalHandler</code> for this 
     * converter. The only lexical events the converter supplies 
     * are comments.
     * </p>
     * 
     * @param handler the <code>LexicalHandler</code>; 
     *     may be null to turn off lexical events
     */
    public void setLexicalHandler(LexicalHandler handler) {
        this.lexicalHandler = handler;
    }

    
    /**
     * <p>
     * Returns the <code>LexicalHandler</code> for this
     * converter. This is only used for comments.
     * </p>
     * 
     * @return SAX2 <code>LexicalHandler</code> that receives 
     *     lexical events
     */
    public LexicalHandler getLexicalHandler() {
        return this.lexicalHandler;
    }
    
    
    // Not necessary to woory about parser exceptions passed to
    // fatal error because we're starting with a known good document.
    // Only excepitons that can arise are thrown by 
    // the supplied ContentHandler, and we don't want to pass those
    // to the ErrorHandler, or call endDocument if such ane exception
    // is thrown
    /**
     * <p>
     * Feed a document through this converter.
     * </p>
     * 
     * @param doc the document to pass to SAX
     * @throws SAXException if the <code>ContentHandler</code> 
     *      or <code>LexicalHandler</code> throws an exception
     */
    public void convert(Document doc) throws SAXException {
        
        contentHandler.startDocument();
        for (int i = 0; i < doc.getChildCount(); i++) {
             process(doc.getChild(i));
        }                 
        contentHandler.endDocument();
        
    }
    
    
    private void process(Node node) throws SAXException {
        
        if (node instanceof Element) {
            convertElement((Element) node);
        }
        else if (node instanceof Text) {
            String data = node.getValue();
            contentHandler.characters(
              data.toCharArray(), 0, data.length());
        }
        else if (node instanceof ProcessingInstruction) {
            ProcessingInstruction instruction 
              = (ProcessingInstruction) node;
            contentHandler.processingInstruction(
              instruction.getTarget(), instruction.getValue());
        }
        else if (node instanceof Comment && lexicalHandler != null) {
            String data = node.getValue();
            lexicalHandler.comment(
              data.toCharArray(), 0, data.length());            
        }
        else if (node instanceof DocType && lexicalHandler != null) {
            DocType type = (DocType) node;
            lexicalHandler.startDTD(type.getRootElementName(), 
              type.getPublicID(), type.getSystemID());              
            lexicalHandler.endDTD();          
        }
        
    }

    
    private void convertElement(Element element) throws SAXException {
        // start prefix mapping
        for (int i = 0; 
             i < element.getNamespaceDeclarationCount(); 
             i++) {
            String prefix = element.getNamespacePrefix(i);
            contentHandler.startPrefixMapping(prefix, 
              element.getNamespaceURI(prefix));  
        }
        ParentNode parentNode = element.getParent();
        if (parentNode instanceof Element) {
            Element parent = (Element) parentNode;
         // now handle element's prefix if not declared on ancestor
            String prefix = element.getNamespacePrefix();
            if (!element.getNamespaceURI(prefix)
              .equals(parent.getNamespaceURI(prefix))) {
                contentHandler.startPrefixMapping(prefix, 
                  element.getNamespaceURI(prefix));  
            }
            
         // Handle attributes' prefixes if not declared on ancestor
            for (int i = 0; i < element.getAttributeCount(); i++) {
                Attribute att = element.getAttribute(i);
                String attPrefix = att.getNamespacePrefix();
                if (!element.getNamespaceURI(attPrefix)
                  .equals(parent.getNamespaceURI(attPrefix))
                  && !element.getNamespacePrefix()
                  .equals(attPrefix)) {
                    contentHandler.startPrefixMapping(attPrefix, 
                      element.getNamespaceURI(attPrefix));  
                }
            }                
        }
        else { // declare all prefixes
            String prefix = element.getNamespacePrefix();
            if (!prefix.equals("")) {
                contentHandler.startPrefixMapping(prefix, 
                  element.getNamespaceURI());  
            }
            
         // Handle attributes' prefixes if not declared on ancestor
            for (int i = 0; i < element.getAttributeCount(); i++) {
                Attribute att = element.getAttribute(i);
                String attPrefix = att.getNamespacePrefix();
                if (!attPrefix.equals("") &&
                  !attPrefix.equals(element.getNamespacePrefix())){
                    contentHandler.startPrefixMapping(attPrefix, 
                      att.getNamespaceURI());  
                }
            }
            
        }
        
        
        // add attributes
        AttributesImpl saxAttributes = new AttributesImpl();
        for (int i = 0; i < element.getAttributeCount(); i++) {
            Attribute attribute = element.getAttribute(i);
            saxAttributes.addAttribute(attribute.getNamespaceURI(),
              attribute.getLocalName(),
              attribute.getQualifiedName(),
              getSAXType(attribute),
              attribute.getValue());
        }
        
        
        contentHandler.startElement(
          element.getNamespaceURI(),
          element.getLocalName(), 
          element.getQualifiedName(), 
          saxAttributes);
        for (int i = 0; i < element.getChildCount(); i++) {
            process(element.getChild(i));   
        }
        contentHandler.endElement(element.getNamespaceURI(),
          element.getLocalName(), element.getQualifiedName());
        
        // end prefix mappings
        for (int i = 0; 
             i < element.getNamespaceDeclarationCount(); 
             i++) {
            String prefix = element.getNamespacePrefix(i);
            contentHandler.endPrefixMapping(prefix);  
        }
        if (parentNode instanceof Element) {
            Element parent = (Element) parentNode;
         // Now handle element's prefix if not declared on ancestor
            String prefix = element.getNamespacePrefix();
            if (!element.getNamespaceURI(prefix)
              .equals(parent.getNamespaceURI(prefix))) {
                contentHandler.endPrefixMapping(prefix);  
            }
            
         // Handle attributes' prefixes if not declared on ancestor
            for (int i = 0; i < element.getAttributeCount(); i++) {
                Attribute att = element.getAttribute(i);
                String attPrefix = att.getNamespacePrefix();
                if (!element.getNamespaceURI(attPrefix)
                  .equals(parent.getNamespaceURI(attPrefix))
                  && !element.getNamespacePrefix().equals(
                  attPrefix)) {
                    contentHandler.endPrefixMapping(attPrefix);  
                }
            }                
        }
        else { // undeclare all prefixes
            String prefix = element.getNamespacePrefix();
            if (!prefix.equals("")) {
                contentHandler.endPrefixMapping(prefix);  
            }
            
         // Handle attributes' prefixes if not declared on ancestor
            for (int i = 0; i < element.getAttributeCount(); i++) {
                Attribute att = element.getAttribute(i);
                String attPrefix = att.getNamespacePrefix();
                if (!attPrefix.equals("") && !attPrefix
                  .equals(element.getNamespacePrefix())) {
                    contentHandler.endPrefixMapping(attPrefix);  
                }
            }
            
        }
    }
    
    
    private static String getSAXType(Attribute attribute) {

        Attribute.Type type = attribute.getType();
        if (type.equals(Attribute.Type.UNDECLARED))  return "CDATA";
        if (type.equals(Attribute.Type.CDATA))       return "CDATA";
        if (type.equals(Attribute.Type.ID))          return "ID";
        if (type.equals(Attribute.Type.IDREF))       return "IDREF";
        if (type.equals(Attribute.Type.IDREFS))      return "IDREFS";
        if (type.equals(Attribute.Type.NMTOKEN))     return "NMTOKEN";
        if (type.equals(Attribute.Type.NMTOKENS))    return "NMTOKENS";
        if (type.equals(Attribute.Type.ENTITY))      return "ENTITY";
        if (type.equals(Attribute.Type.ENTITIES))    return "ENTITIES";
        if (type.equals(Attribute.Type.NOTATION))    return "NOTATION";
        return "NMTOKEN"; // ENUMERATED
        
    }

    
}
