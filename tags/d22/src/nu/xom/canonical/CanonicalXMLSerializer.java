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
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d22
 *
 */
public class CanonicalXMLSerializer extends Serializer {

    private boolean withComments;

    /**
     * <p>
     *   Creates a <code>Serializer</code> that outputs a 
     *   canonical XML document with comments.
     * </p>
     * 
     * @param out the <code>OutputStream</code> the document
     *     is written onto
     */
    public CanonicalXMLSerializer(OutputStream out) {
        this(out, true);
    }

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
    public CanonicalXMLSerializer(
      OutputStream out, boolean withComments) {
        super(out);
        setLineSeparator("\n");
        this.withComments = withComments;
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
            write(child); 
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
            write(child);
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
    protected final void write(Element element) throws IOException {
        
        writeStartTag(element, false);
        // children
        for (int i = 0; i < element.getChildCount(); i++) {
            write(element.getChild(i)); 
        }
        writeEndTag(element);
    } 

    protected void writeStartTag(Element element, boolean isEmpty) throws IOException {
        writeRaw("<");
        writeRaw(element.getQualifiedName());
        
        // Namespace
        String prefix = element.getNamespacePrefix();
        
        ParentNode parent = element.getParent();
        String parentURI = "";
        if (parent instanceof Element) {
            parentURI = ((Element) parent).getNamespaceURI(prefix);
        } 
        
        Element parentElement = null;
        if (parent instanceof Element) {
            parentElement = (Element) parent; 
        } 
        
        for (int i = 0; 
             i < element.getNamespaceDeclarationCount(); 
             i++) {
            String additionalPrefix = element.getNamespacePrefix(i);
            if ("xml".equals(additionalPrefix)) continue;
            String uri = element.getNamespaceURI(additionalPrefix);
            if (parentElement != null) {
               if (uri.equals(
                 parentElement.getNamespaceURI(additionalPrefix))) {
                   continue; 
               }
            }
            else if (uri.equals("")) {
                continue; // no need to say xmlns=""   
            }
            
            writeRaw(" ");
            writeNamespaceDeclaration(additionalPrefix, uri);
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
            for (int i = 0; i < data.length; i++) {
                if (data[i] == ' ') {
                    if (i != 0 && data[i-1] != ' ') {
                        result.append(data[i]);   
                    }
                }
                else if (data[i] == '\t') {
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
    
    private static boolean isSpace(char c) {
       return c == ' ' || c == '\t' || c == '\r' || c == '\n';   
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
     *  <p>
     *   Does nothing because canonical XML does not
     *   include XML declarations.
     * </p>
     * 
     * @see nu.xom.Serializer#writeXMLDeclaration()
     */
    protected void writeXMLDeclaration() {};
    
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


    /**
     * <p>
     * Returns 0 because canonical XML does not adjust white space.
     * </p>
     * 
     * @return 0
     */
    public final int getIndent() {
        return 0;
    }


    /**
     * <p>
     * This has no effect because the canonical XML algorithm
     * controls all white space.
     * </p>
     * 
     * @param indent the number of spaces to indent each 
     *     successive level in the hierarchy
     * @throws IllegalArgumentException if <code>indent</code> is anything 
     *     other than 0
     * 
     */
    public final void setIndent(int indent) {
        if (indent != 0) {
            throw new IllegalArgumentException(
              "Canonical XML does not allow indenting."
            );
        }
    }

    /**
     * <p>
     * Returns the <code>String</code> used as a line separator.
     * This is always <code>"\n"</code> for canonical XML.
     * </p>
     * 
     * @return \n
     */
    public final String getLineSeparator() {
        return "\n";
    }

    /**
     * <p>
     * Sets the lineSeparator. This can only be  <code>"\n"</code>.
     * All other values are forbidden.
     * Line separators in the input text may be converted to a line
     * feed, but are never inserted or deleted.
     * </p>
     * 
     * @param lineSeparator the character(s) used to break lines
     * @throws IllegalArgumentException if <code>lineSeparator</code>
     *     is not \n
     */
    public final void setLineSeparator(String lineSeparator) {
        if (!("\n".equals(lineSeparator))) {
            throw new IllegalArgumentException(
              "Canonical XML requires that the line separator be \n."
            );
        }
        super.setLineSeparator("\n");  
    }

    /**
     * <p>
     * Returns -1 indicating that there is no maximum
     * line length in canonical XML.
     * </p>
     * 
     * @return -1
     */
    public final int getMaxLength() {
        return -1;
    }

    /**
     * <p>
     * Sets the suggested maximum line length for this serializer.
     * Because canonical XML sets all the rules for white space
     * handling, this method does nothing.
     * </p>
     * 
     * @param maxLength the suggested maximum line length
     * @throws IllegalArgumentException if <code>maxLength</code>
     *     greater than 0
     */
    public final void setMaxLength(int maxLength) {
        if (maxLength > 0) {
            throw new IllegalArgumentException(
              "Canonical XML does not allow line wrapping."
            );
        }
    }

    /**
     * <p>
     * Returns false because canonicalization 
     * does not preserve the base URI.
     * </p>
     * 
     * @return false
     */
    public final boolean getPreserveBaseURI() {
        return false;
    }

    /**
     * <p>
     * Determines whether this <code>Serializer</code> inserts
     * extra <code>xml:base</code> attributes to attempt to 
     * preserve base URI information from the document.
     * For canonical XML, this value can only be false,
     * do not preserve base URI information.
     * <code>xml:base</code> attributes that are 
     * part of the document's infoset are still output. 
     * </p>
     * 
     * @param preserve argument is ignored. It is present only for
     *     compatibility with the superclass 
     * @throws IllegalArgumentException if <code>preserve</code>
     *     is true
     */
    public final void setPreserveBaseURI(boolean preserve) {
        if (preserve) {
            throw new IllegalArgumentException(
              "Canonical XML does not preserve the base URI."
            );
        }
    }

    /**
     * <p>
     *   If true, this property indicates serialization will
     *   perform Unicode normalization on all data using normalization
     *   form C (NFC). Because Unicode normalization and XML
     *   canonicalization are incompatible, attempting to set this
     *   feature to true throws an 
     *   <code>IllegalArgumentException</code>.
     * </p>
     * 
     * <p>
     *   This feature is not yet implemented. 
     * </p>
     * 
     * @param normalize true if normalization is performed; 
     *     false if it isn't
     * @throws IllegalArgumentException if <code>normalize</code>
     *     is true
     */
    public final void setUnicodeNormalizationFormC(boolean normalize) {
        if (normalize) {
            throw new IllegalArgumentException(
              "Normalization and canonicalization are incompatible."
            ); 
        }  
    }

    
    /**
     * <p>
     *   A canonicalizing serializer never performs Unicode 
     *   normalization. The method always returns false.
     * </p>
     * 
     * @return false XML canonicalization does not include Unicode
     *     normalization.
     */
    public final boolean getUnicodeNormalizationFormC() {
        return false;   
    }

}