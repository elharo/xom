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
   subject line. The XOM home page is located at http://www.xom.nu/
*/


package nu.xom;

/**
 * @author Elliotte Rusty Harold
 * @version 1.0
 *
 */
class NonVerifyingHandler extends XOMHandler {

    NonVerifyingHandler(NodeFactory factory) {
        super(factory); 
    } 
  
    
    public void startElement(String namespaceURI, String localName, 
      String qualifiedName, org.xml.sax.Attributes attributes) {
        
        flushText();
        Element element = Element.build(qualifiedName, namespaceURI);
        if (parent == document) { // root
            document.setRootElement(element);
            inProlog = false;
        }
        
        current = element;
        // Need to push this, even if it's null 
        parents.push(element);
        
        if (parent != document) { 
            // a.k.a. parent not instanceof Document
            parent.fastInsertChild(element, parent.getChildCount());
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
        int length = attributes.getLength();
        
        // We've got a pretty good guess at how many attributes there
        // will be here; we could ensureCapacity up to that length.
        // However, that might waste memory because we wouldn't use 
        // the ones for namespace declarations. We could always 
        // trimToSize when we're done, but it's probably not worth
        // the effort.
        for (int i = 0; i < length; i++) {
            String qName = attributes.getQName(i);
            if (qName.startsWith("xmlns:") || qName.equals("xmlns")) {               
                continue;               
            }             
            else {
                String namespace = attributes.getURI(i);
                String value = attributes.getValue(i);
                Attribute attribute = Attribute.build(
                  qName, 
                  namespace, 
                  value, 
                  convertStringToType(attributes.getType(i))
                );
                element.fastAddAttribute(attribute);
            }
        }

        // Attach the namespaces
        for (int i = 0; i < length; i++) {
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
    
    
    public void endElement(
      String namespaceURI, String localName, String qualifiedName) {
        
        // If we're immediately inside a skipped element
        // we need to reset current to null, not to the parent
        current = (ParentNode) parents.pop();
        flushText();
        
        parent = current.getParent();
        
        if (parent.isDocument()) { // root element
            Document doc = (Document) parent;
            boolean beforeRoot = true;
            doc.setRootElement((Element) current);
            beforeRoot = false;
        }
        
    }
 
    
    // accumulate all text that's in the buffer into a text node
    protected void flushText() {
        
        if (buffer.length() > 0) {
            Text result;
            if (!inCDATA) {
                result = Text.build(buffer.toString());
            }
            else {
                result = CDATASection.build(buffer.toString());
            }
            parent.fastInsertChild(result, parent.getChildCount());
            buffer = new StringBuffer();
        }
        inCDATA = false;
        finishedCDATA = false;
        
    }
  
    
    public void processingInstruction(String target, String data) {
        
        if (!inDTD) flushText();
        if (inExternalSubset) return;
        ProcessingInstruction result = ProcessingInstruction.build(target, data);
        
        if (!inDTD) {
            if (inProlog) {
                parent.fastInsertChild(result, position);
                position++;
            }
            else {
                parent.fastInsertChild(result, parent.getChildCount());
            }
        }
        else {
            internalDTDSubset.append("  ");            
            internalDTDSubset.append(result.toXML());            
            internalDTDSubset.append("\n"); 
        }  

    }
    
    
    // LexicalHandler events
    public void startDTD(String rootName, String publicID, 
      String systemID) {
        
        inDTD = true;
        DocType doctype = DocType.build(rootName, publicID, systemID);
        document.fastInsertChild(doctype, position);
        position++;
        internalDTDSubset = new StringBuffer(); 
        this.doctype = doctype;
        
    }
    
    
    public void comment(char[] text, int start, int length) {
        
        if (!inDTD) flushText();
        if (inExternalSubset) return;

        Comment result = Comment.build(new String(text, start, length));
        ;
        if (!inDTD) {
            if (inProlog) {
                parent.insertChild(result, position);
                position++;
            }
            else {
                parent.fastInsertChild(result, parent.getChildCount());
            }
        }
        else {
            internalDTDSubset.append("  ");            
            internalDTDSubset.append(result.toXML());            
            internalDTDSubset.append("\n");            
        }            

    }    

}