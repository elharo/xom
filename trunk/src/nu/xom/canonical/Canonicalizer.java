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

package nu.xom.canonical;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;

import nu.xom.Attribute;
import nu.xom.Comment;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ParentNode;
import nu.xom.ProcessingInstruction;
import nu.xom.Serializer;
import nu.xom.Text;

/**
 * <p>
 *   This class writes XML in the format specified by <a 
 *   href="http://www.w3.org/TR/2001/REC-xml-c14n-20010315">Canonical
 *   XML Version 1.0</a>.
 * </p>
 *
 * <p>
 *   Only complete documents can be canonicalized.
 *   Document subset canonicalization is not yet supported.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d25
 *
 */
public class Canonicalizer {

    private boolean withComments;
    private Serializer serializer;
    
    private static Comparator comparator = new AttributeComparator();
    
    private static class AttributeComparator implements Comparator {
        
        public int compare(Object o1, Object o2) {
            Attribute a1 = (Attribute) o1;   
            Attribute a2 = (Attribute) o2;   
            
            String namespace1 = a1.getNamespaceURI();
            String namespace2 = a2.getNamespaceURI();
            if (namespace1.equals(namespace2)) { 
                return a1.getLocalName().compareTo(a2.getLocalName());             
            }
            else if (namespace1.equals("")) {
                 return -1;   
            }
            else if (namespace2.equals("")) {
                 return 1;   
            }
            else { // compare namespace URIs
                return namespace1.compareTo(namespace2);               
            }
            
        }

    }
    
    
    /**
     * <p>
     *   Creates a <code>Canonicalizer</code> that outputs a 
     *   canonical XML document with comments.
     * </p>
     * 
     * @param out the <code>OutputStream</code> the document
     *     is written onto
     */
    public Canonicalizer(OutputStream out) {
        this(out, true);
    }

    
    /**
     * <p>
     *   Creates a <code>Canonicalizer</code> that outputs a 
     *   canonical XML document with or without comments.
     * </p>
     * 
     * @param out the <code>OutputStream</code> the document
     *     is written onto
     * @param withComments true if comments should be included 
     *     in the output, false otherwise
     */
    public Canonicalizer(
      OutputStream out, boolean withComments) {
        this.serializer = new CanonicalXMLSerializer(out);
        serializer.setLineSeparator("\n");
        this.withComments = withComments;
    }


    private class CanonicalXMLSerializer extends Serializer {

        /**
         * <p>
         *   Creates a <code>Serializer</code> that outputs a 
         *   canonical XML document with or without comments.
         * </p>
         * 
         * @param out the <code>OutputStream</code> the document
         *     is written onto
         * @param withComments true if comments should be included 
         *     in the output, false otherwise
         */
        CanonicalXMLSerializer(OutputStream out) {
            super(out);
            setLineSeparator("\n");
        }

        
        /**
         * <p>
         * Serializes a document onto the output 
         * stream using the canonical XML algorithm.
         * </p>
         * 
         * @param doc the <code>Document</code> to serialize
         * 
         * @throws IOException if the underlying <code>OutputStream</code>
         *      encounters an I/O error
         */
         public final void write(Document doc) throws IOException {
            
            int position = 0;        
            while (true) {
                Node child = doc.getChild(position);
                writeChild(child); 
                position++;
                if (child instanceof ProcessingInstruction) breakLine();
                else if (child instanceof Comment && withComments) {
                    breakLine();
                }
                else if (child instanceof Element) break;
            }       
            
            for (int i = position; i < doc.getChildCount(); i++) {
                Node child = doc.getChild(i);
                if (child instanceof ProcessingInstruction) breakLine();
                else if (child instanceof Comment && withComments) {
                    breakLine();
                }
                writeChild(child);
            }
            
            flush();
        }  
     
         
        /**
         * <p>
         * Serializes an element onto the output stream using the canonical
         * XML algorithm.  The result is guaranteed to be well-formed. 
         * If <code>element</code> does not have a parent element, it will
         * also be namespace well-formed.
         * </p>
         * 
         * @param element the <code>Element</code> to serialize
         * 
         * @throws IOException if the underlying <code>OutputStream</code>
         *     encounters an I/O error
         */
        protected final void write(Element element) 
          throws IOException {

            // treat empty elements differently to avoid an
            // instance of test
            if (element.getChildCount() == 0) {
                writeStartTag(element, false);
                writeEndTag(element);                
            }
            else {
                Node current = element;
                boolean end = false;
                int index = -1;
                while (true) {                   
                    if (!end && current.getChildCount() > 0) {
                       writeStartTag((Element) current, false);
                       current = current.getChild(0);
                       index = 0;
                    }
                    else {
                        if (end) {
                            writeEndTag((Element) current);
                            if (current == element) break;
                        }
                        else {
                            writeChild(current);
                        }
                        end = false;
                        ParentNode parent = current.getParent();
                        if (parent.getChildCount() - 1 == index) {
                            current = parent;
                            if (current != element) {
                                parent = current.getParent();
                                index = parent.indexOf(current);
                            }
                            end = true;
                        }
                        else {
                            index++;
                            current = parent.getChild(index);
                        }
                    }
                }   
            }
            
        } 
    
        
        protected void writeStartTag(Element element, boolean isEmpty) 
          throws IOException {
            writeRaw("<");
            writeRaw(element.getQualifiedName());
            
            ParentNode parent = element.getParent();
            
            Element parentElement = null;
            if (parent instanceof Element) {
                parentElement = (Element) parent; 
            } 
            
            for (int i = 0; 
                 i < element.getNamespaceDeclarationCount(); 
                 i++) {
                String prefix = element.getNamespacePrefix(i);
                String uri = element.getNamespaceURI(prefix);
                if (parentElement != null) {
                   if (uri.equals(
                     parentElement.getNamespaceURI(prefix))) {
                       continue; 
                   }
                }
                else if (uri.equals("")) {
                    continue; // no need to say xmlns=""   
                }
                
                writeRaw(" ");
                writeNamespaceDeclaration(prefix, uri);
            } 
            
            Attribute[] sorted = sortAttributes(element);        
            for (int i = 0; i < sorted.length; i++) {
                writeRaw(" ");
                write(sorted[i]);
            }       
            
            writeRaw(">");
        } 
    
        
        protected void write(Attribute attribute) throws IOException {
            writeRaw(attribute.getQualifiedName());
            writeRaw("=\"");
            writeRaw(prepareAttributeValue(attribute));
            writeRaw("\"");
        }
        
        
        protected void writeEndTag(Element element) throws IOException {
            writeRaw("</");
            writeRaw(element.getQualifiedName());
            writeRaw(">");
        }    
        
        
        private Attribute[] sortAttributes(Element element) {
    
            Attribute[] result 
              = new Attribute[element.getAttributeCount()];
            for (int i = 0; i < element.getAttributeCount(); i++) {
                result[i] = element.getAttribute(i); 
            }
            Arrays.sort(result, comparator);       
            
            return result;        
            
        }
    
        
        private String prepareAttributeValue(Attribute attribute) {
    
            String value = attribute.getValue();
            StringBuffer result = new StringBuffer(value.length());
    
            if (attribute.getType().equals(Attribute.Type.CDATA)
              || attribute.getType().equals(Attribute.Type.UNDECLARED)) {
                char[] data = value.toCharArray();
                for (int i = 0; i < data.length; i++) {
                    char c = data[i];
                    if (c == '\t') {
                        result.append("&#x9;");
                    }
                    else if (c == '\n') {
                        result.append("&#xA;");
                    }
                    else if (c == '\r') {
                        result.append("&#xD;");
                    }
                    else if (c == '\"') {
                        result.append("&quot;");
                    }
                    else if (c == '&') {
                        result.append("&amp;");
                    }
                    else if (c == '<') {
                        result.append("&lt;");
                    }
                    else { 
                        result.append(c);   
                    }
                }
            }
            else {
                // According to the spec, "Whitespace character references
                // other than &#x20; are not affected by attribute value 
                // normalization. For parsed documents, the parser will  
                // still replace these with the actual character. I am 
                // going to assume that if one is found here, that the 
                // user meant to put it there; and so we will escape it 
                // with a character reference
                char[] data = value.toCharArray();
                boolean seenFirstNonSpace = false;
                for (int i = 0; i < data.length; i++) {
                    if (data[i] == ' ') {
                        if (i != data.length-1 && data[i+1] != ' ' && seenFirstNonSpace) {
                             result.append(data[i]); 
                        }
                        continue;
                    } 
                    seenFirstNonSpace = true;
                    if (data[i] == '\t') {
                        result.append("&#x9;");
                    }
                    else if (data[i] == '\n') {
                        result.append("&#xA;");
                    }
                    else if (data[i] == '\r') {
                        result.append("&#xD;");
                    }
                    else if (data[i] == '\"') {
                        result.append("&quot;");
                    }
                    else if (data[i] == '&') {
                        result.append("&amp;");
                    }
                    else if (data[i] == '<') {
                        result.append("&lt;");
                    }
                    else {
                        result.append(data[i]);
                    }
                }
            }
    
            return result.toString();    
            
        }
        
        
        /**
         * <p>
         * Serializes a <code>Text</code> object
         * onto the output stream using the UTF-8 encoding.
         * The reserved characters &lt;, &gt;, and &amp;
         * are escaped using the standard entity references such as
         * <code>&amp;lt;</code>, <code>&amp;gt;</code>, 
         * and <code>&amp;amp;</code>.
         * </p>
         * 
         * @param text the <code>Text</code> to serialize
         * 
         * @throws IOException  if the underlying <code>OutputStream</code>
         *     encounters an I/O error
         */
        protected final void write(Text text) throws IOException {
            String input = text.getValue();
            StringBuffer result = new StringBuffer(input.length());
            for (int i = 0; i < input.length(); i++) {
                char c = input.charAt(i);
                if (c == '\r') {
                    result.append("&#xD;");
                }
                else if (c == '&') {
                    result.append("&amp;");
                }
                else if (c == '<') {
                    result.append("&lt;");
                }
                else if (c == '>') {
                    result.append("&gt;");
                }
                else { 
                    result.append(c);   
                }            
            }
            writeRaw(result.toString());
        }   
    
        
        /**
         * <p>
         * Serializes a <code>Comment</code> object
         * onto the output stream if and only if this
         * serializer is configured to produce canonical XML
         * with comments.
         * </p>
         * 
         * @param comment the <code>Comment</code> to serialize
         * 
         * @throws IOException if the underlying <code>OutputStream</code>
         *     encounters an I/O error
         */
        protected final void write(Comment comment) throws IOException {
            if (withComments) super.write(comment);
        }
        
        
        /**
         * <p>
         * Does nothing because canonical XML does not include
         * document type declarations.
         * </p>
         * 
         * @param doctype the document type declaration to serialize
         */
        protected final void write(DocType doctype) {
            // DocType is not serialized in canonical XML
        } 
       
        
    }

    
    /**
     * <p>
     * Serializes a document onto the output 
     * stream using the canonical XML algorithm.
     * </p>
     * 
     * @param doc the <code>Document</code> to serialize
     * 
     * @throws IOException if the underlying <code>OutputStream</code>
     *      encounters an I/O error
     */
    public final void write(Document doc) throws IOException {  
        serializer.write(doc);        
        serializer.flush();
    }  
 
    
}