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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 * <p>
 *  A serializer outputs a <code>Document</code> object
 *  in a specific encoding
 *  using various options for controlling white space, indenting,  
 *  line breaking, and base URIs. However, in general these do affect
 *  the document's infoset. In particular, if you set either the 
 *  maximum line length or the indent size to a positive value, 
 *  then the serializer will not respect input white space. It 
 *  may trim leading and trailing space, condense runs of white 
 *  space to a single space, convert carriage  returns and line 
 *  feeds to spaces, add extra space where none was present before, 
 *  and otherwise muck with the document's white space. 
 *  The defaults, however, preserve all significant white 
 *  space including ignorable white space, to the maximum
 *  extent possible.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d22
 *
 */
public class Serializer {

    private TextWriter escaper;
    private boolean preserveBaseURI = false;

    /**
     * <p>
     * Create a new serializer that uses the UTF-8 encoding.
     * </p>
     * 
     * @param out the output stream to write the document on
     * 
     * @throws NullPointerException if out is null
     */
    public Serializer(OutputStream out) {
        if (out == null) {
            throw new NullPointerException("Null OutputStream");
        } 
        try {
            Writer writer = new OutputStreamWriter(out, "UTF-8");
            writer = new BufferedWriter(writer);
            escaper = TextWriterFactory.getTextWriter(writer, "UTF-8");
        }
        catch (UnsupportedEncodingException ex) {
            throw new XMLException(
              "The VM is broken. It does not understand UTF-8.");
        }
    }
    
    /**
     * <p>
     * Create a new serializer that uses a specified encoding.
     * The encoding must be recognized by the Java virtual machine.
     * Currently the following encodings are recognized by XOM:
     * </p>
     * 
     * <ul>
     *   <li>UTF-8</li>
     *   <li>UTF-16</li>
     *   <li>UTF-16BE</li>
     *   <li>UTF-16LE</li>
     *   <li>ISO-10646-UCS-2</li>
     *   <li>ISO-8859-1</li>
     *   <li>ISO-8859-2</li>
     *   <li>ISO-8859-3</li>
     *   <li>ISO-8859-4</li>
     *   <li>ISO-8859-5</li>
     *   <li>ISO-8859-6</li>
     *   <li>ISO-8859-7</li>
     *   <li>ISO-8859-8</li>
     *   <li>ISO-8859-9</li>
     *   <li>ISO-8859-10</li>
     *   <li>ISO-8859-13</li>
     *   <li>ISO-8859-14</li>
     *   <li>ISO-8859-15</li>
     *   <li>ISO-8859-16</li>
     *   <li>IBM037 (a.k.a. CP037, EBCDIC-CP-US, EBCDIC-CP-CA, 
     *         EBCDIC-CP-WA, EBCDIC-CP-NL, and CSIBM037)</li>
     * </ul>
     * 
     * <p>
     *   More will be added in the future. You can use 
     *   encodings not in this list as long as the local virtual
     *   machine supports them. However, characters may unnecessarily
     *   be output as character references. Conversely, not all  
     *   versions of Java support all of these encodings. If you 
     *   attempt to use an encoding that the local Java virtual 
     *   machine does not support, the constructor will throw an 
     *   <code>UnsupportedEncodingException</code>.
     * </p>
     * 
     * @param out the output stream to write the document on
     * @param encoding the character encoding for the serialization
     * 
     * @throws NullPointerException if <code>out</code> 
     *     or <code>encoding</code> is null
     * @throws UnsupportedEncodingException if the VM does not 
     *     support the requested encoding
     *  
     */
    public Serializer(OutputStream out, String encoding)
      throws UnsupportedEncodingException {
        if (out == null) {
            throw new NullPointerException("Null OutputStream");
        } 
        if (encoding == null) {
            throw new NullPointerException("Null encoding");
        } 
        
        Writer writer;  
        // Java's Cp037 encoding is broken, so we have to
        // provide our own.   
        if (encoding.equals("IBM037")
                      || encoding.equals("CP037")
                      || encoding.equals("EBCDIC-CP-US")
                      || encoding.equals("EBCDIC-CP-CA")
                      || encoding.equals("EBCDIC-CP-WA")
                      || encoding.equals("EBCDIC-CP-NL")
                      || encoding.equals("CSIBM037")) {
                          
            writer = new EBCDICWriter(out);
        }
        else if (encoding.equals("UTF-16") 
          || encoding.equals("ISO-10646-UCS-2")) {
           // For compatibility with Java 1.2 and earlier
           writer = new OutputStreamWriter(out, "UnicodeBig");  
        }
        else writer = new OutputStreamWriter(out, encoding);
        writer = new BufferedWriter(writer);
        escaper = TextWriterFactory.getTextWriter(writer, encoding);
    }

    
    /**
     * <p>
     * Serializes a document onto the output 
     * stream using the current options.
     * </p>
     * 
     * @param doc the <code>Document</code> to serialize.
     * 
     * @throws IOException if the underlying <code>OutputStream</code>
     *      encounters an I/O error
     */
    public void write(Document doc) throws IOException {
        escaper.reset();
        // The OutputStreamWriter automatically inserts
        // the byte order mark if necessary.
        escaper.writeMarkup("<?xml version=\"1.0\" encoding=\"");
        escaper.writeMarkup(escaper.getEncoding());
        escaper.writeMarkup("\"?>");
        escaper.breakLine();
        for (int i = 0; i < doc.getChildCount(); i++) {
            write(doc.getChild(i)); 
            escaper.breakLine();
        }       
        escaper.flush();
    }
    
    // 1 == preserve all
    // 0 == inherit
    // -1 == default
    private int isWhiteSpaceSignificant(ParentNode node) {
     
        if (node == null) return -1;
        if (node.isDocument()) return -1;
        Element element = (Element) node;
        String xmlSpace = element.getAttributeValue(
          "space", "http://www.w3.org/XML/1998/namespace"
        );

        if ("preserve".equals(xmlSpace)) return 1;
        else if ("default".equals(xmlSpace)) return 0;
        else {
            return isWhiteSpaceSignificant(element.getParent());
        }
 
    }
   
    
    
    /**
     * <p>
     * Serializes an element onto the output stream using the current
     * options. The result is guaranteed to be well-formed. If 
     * <code>element</code> does not have a parent element, it  
     * will also be namespace well-formed.
     * </p>
     * 
     * @param element the <code>Element</code> to serialize.
     * 
     * @throws IOException if the underlying <code>OutputStream</code>
     *     encounters an I/O error
     */
    protected void write(Element element) throws IOException {

        if (escaper.isIndenting() && !escaper.isPreserveSpace()) {
            escaper.breakLine();
        }
        escaper.writeMarkup("<");
        escaper.writeMarkup(element.getQualifiedName());
        
        // Namespace
        String prefix = element.getNamespacePrefix();

        ParentNode parent = element.getParent();
        String parentURI = "";
        if (parent.isElement()) {
            parentURI = ((Element) parent).getNamespaceURI(prefix);
        } 
        
        // check to see if we need an xml:base attribute
        if (preserveBaseURI) {
            if (element.getAttribute("base", 
             "http://www.w3.org/XML/1998/namespace") == null) {
                ParentNode elemParent = element.getParent();
                String baseValue = element.getBaseURI();
                if (baseValue != null) {
                    if (elemParent == null 
                      || elemParent.isDocument()
                      || !element.getBaseURI()
                           .equals(elemParent.getBaseURI())) {
                           
                        escaper.writeMarkup(' ');
                        escaper.writeMarkup("xml:base=\"");
                        escaper.writeAttributeValue(baseValue);
                        escaper.writeMarkup("\"");
                    }
                }
            }
        }
        
        for (int i = 0; i < element.getAttributeCount(); i++) {
            Attribute attribute = element.getAttribute(i);
            escaper.writeMarkup(' ');
            escaper.writeMarkup(attribute.getQualifiedName());
            escaper.writeMarkup("=\"");
            escaper.writeAttributeValue(attribute.getValue());
            escaper.writeMarkup("\"");  
        }       
        
        // Namespaces
        ParentNode parentNode = element.getParent();
        for (int i = 0; 
             i < element.getNamespaceDeclarationCount(); 
             i++) {
            String additionalPrefix = element.getNamespacePrefix(i);
            if ("xml".equals(additionalPrefix)) continue;
            String uri = element.getNamespaceURI(additionalPrefix);
            if (parentNode.isElement()) {
               Element parentElement = (Element) parentNode;   
               if (uri.equals(
                 parentElement.getNamespaceURI(additionalPrefix))) {
                      continue;
               } 
            }
            else if (uri.equals("")) {
                continue; // no need to say xmlns=""   
            }
            
            if ("".equals(additionalPrefix)) {
                escaper.writeMarkup(" xmlns"); 
            }
            else {
                escaper.writeMarkup(" xmlns:"); 
                escaper.writeMarkup(additionalPrefix); 
            } 
            escaper.writeMarkup("=\""); 
            escaper.writePCDATA(uri);   
            escaper.writeMarkup('\"');
        } 
        
        if (element.hasChildren()) {
            escaper.writeMarkup('>');
            // adjust for xml:space
            boolean wasPreservingWhiteSpace = escaper.isPreserveSpace();
            String newXMLSpaceValue = element.getAttributeValue(
              "space", "http://www.w3.org/XML/1998/namespace");
            if  (newXMLSpaceValue != null) {
                if ("preserve".equals(newXMLSpaceValue)){
                    escaper.setPreserveSpace(true);
                }
                else if ("default".equals(newXMLSpaceValue)){
                    escaper.setPreserveSpace(false);
                }
            }
            
            escaper.incrementIndent();
            // children
            for (int i = 0; i < element.getChildCount(); i++) {
                write(element.getChild(i)); 
            }
            escaper.decrementIndent();
            if (escaper.getIndent() > 0 && !escaper.isPreserveSpace()) {
                Node firstChild = element.getChild(0);
                if (!(firstChild.isText()) 
                  || firstChild.getValue().trim().equals("")) {
                     escaper.breakLine();
                }
            }
            escaper.writeMarkup("</");
            escaper.writeMarkup(element.getQualifiedName());
            escaper.writeMarkup(">");
            
            // readjust for xml:space
            /*  if (changingWhiteSpace) {
                escaper.setIndent(oldIndent);
                escaper.setMaxLength(oldMaxLength);
            }*/
            // restore parent value
            if  (newXMLSpaceValue != null) {
                escaper.setPreserveSpace(wasPreservingWhiteSpace);
            }
                        
        }
        else {
            escaper.writeMarkup("/>");   
        }
        escaper.flush();
        
    }
    
    /**
     * <p>
     * Serializes a <code>Comment</code> object
     * onto the output stream using the current options.
     * </p>
     * 
     * <p>
     *   Since character and entity references are not resolved
     *   in comments, comments can only be serialized when all
     *   characters they contain are available in the current 
     *   encoding.
     * </p>
     * 
     * @param comment the <code>Comment</code> to serialize.
     * 
     * @throws IOException if the underlying <code>OutputStream</code> 
     *     encounters an I/O error
     */
    protected void write(Comment comment) throws IOException {
        if (escaper.isIndenting()) escaper.breakLine();
        escaper.writeMarkup("<!--");
        escaper.writeMarkup(comment.getValue());
        escaper.writeMarkup("-->");
    }
    
    /**
     * <p>
     * Serializes a <code>ProcessingInstruction</code> object
     * onto the output stream using the current options.
     * </p>
     * 
     * <p>
     *   Since character and entity references are not resolved
     *   in processing instructions, processing instructions
     *   can only be serialized when all
     *   characters they contain are available in the current 
     *   encoding.
     * </p>
     * 
     * @param instruction the <code>ProcessingInstruction</code> 
     *     to serialize.
     * 
     * @throws IOException  if the underlying <code>OutputStream</code>
     *     encounters an I/O error
     */
    protected void write(ProcessingInstruction instruction) 
      throws IOException {
        if (escaper.isIndenting()) escaper.breakLine();
        escaper.writeMarkup("<?");
        escaper.writeMarkup(instruction.getTarget());
        String value = instruction.getValue();
        // for canonical XML, only output a space after the target
        // if there is a value
        if (!"".equals(value)) {
            escaper.writeMarkup(' ');
            escaper.writeMarkup(value);
        }
        escaper.writeMarkup("?>");      
    }
    
    /**
     * <p>
     * Serializes a <code>Text</code> object
     * onto the output stream using the current options.
     * Reserved characters such as &lt;, &gt; and "
     * are escaped using the standard entity references 
     * such as <code>&amp;lt;</code>, <code>&amp;gt;</code>, 
     * and <code>&amp;quot;</code>.
     * </p>
     * 
     * <p>
     *   Characters which cannot be encoded in the current character set
     *   (for example, &Omega; in ISO-8859-1) are encoded using 
     *   character references.
     * </p>
     * 
     * <p>
     *   Unsupported character sets encode all non-ASCII characters.
     *   Supported character sets currently include:
     * </p>
     * 
     * <ul>
     *   <li>Unicode in its various encodings</li>
     *   <li>Latin-1 (ISO-8859-1)</li>
     *   <li>Latin-2 (ISO-8859-2)</li>
     *   <li>Latin-3 (ISO-8859-3)</li>
     *   <li>Latin-4 (ISO-8859-4)</li>
     *   <li>ISO-8859-5 (Cyrillic)</li>
     *   <li>ISO-8859-6 (Arabic)</li>
     *   <li>ISO-8859-7 (Greek)</li>
     *   <li>ISO-8859-8 (Hebrew)</li>
     *   <li>ISO-8859-9 (Latin-5)</li>
     *   <li>ISO-8859-10 (Latin-6)</li>
     *   <li>ISO-8859-11 Thai</li>
     *   <li>ISO-8859-13 (Latin-7)</li>
     *   <li>ISO-8859-14 (Latin-8)</li>
     *   <li>ISO-8859-15 (Latin-9)</li>
     *   <li>ISO-8859-16 (Latin-10)</li>
     * </ul>
     * 
     * <p>
     *   Non-ASCII characters from other character sets 
     *   will probably be hexadecimally escaped.
     *   even when they don't need to be. 
     *   More standard character sets will be added in the future.
     *   This will not require any changes to the public API.
     * </p> 
     * 
     * @param text the <code>Text</code> to serialize.
     * 
     * @throws IOException  if the underlying <code>OutputStream</code>
     *     encounters an I/O error
     */
    protected void write(Text text) throws IOException {
        String value = text.getValue();
        if (text.isCDATASection()) {
           for (int i = 0; i < value.length(); i++) {
               if (escaper.needsEscaping(value.charAt(i))) {
                    // can't use CDATA section
                    escaper.writePCDATA(value);
                    return;   
               }   
           }
           escaper.writeMarkup("<![CDATA[");
           escaper.writeMarkup(value);
           escaper.writeMarkup("]]>");
        }
        else {
            escaper.writePCDATA(value);
        }
    }   

    
    /**
     * <p>
     * Serializes a <code>DocType</code> object
     * onto the output stream using the current options.
     * </p>
     * 
     * @param doctype the document type declaration to serialize
     * 
     * @throws IOException if the underlying 
     *     <code>OutputStream</code> encounters an I/O error
     */
    protected void write(DocType doctype) throws IOException {
        escaper.writeMarkup("<!DOCTYPE ");
        escaper.writeMarkup(doctype.getRootElementName());
        if (doctype.getPublicID() != null) {
          escaper.writeMarkup(" PUBLIC \"" + doctype.getPublicID() 
           + "\" \"" + doctype.getSystemID() + "\"");
        } 
        else if (doctype.getSystemID() != null) {
          escaper.writeMarkup(
            " SYSTEM \"" + doctype.getSystemID() + "\"");
        } 
        
        String internalDTDSubset = doctype.getInternalDTDSubset();
        if (!internalDTDSubset.equals("")) {
            escaper.writeMarkup(" [");    
            escaper.breakLine();
            escaper.writeMarkup(internalDTDSubset); 
            escaper.breakLine();  
            escaper.writeMarkup("]"); 
        }

        escaper.writeMarkup(">");

    }   

    /**
     * <p>
     * Serializes a node
     * onto the output stream using the current options.
     * </p>
     * 
     * @param node the <code>Node</code> to serialize.
     * 
     * @throws IOException if the underlying <code>OutputStream</code>
     *     encounters an I/O error
     */
    protected void write(Node node) throws IOException {
        
        if (node.isText()) {
            write((Text) node);
        }
        else if (node.isElement()) {
            write((Element) node);
        }
        else if (node.isComment()) {
            write((Comment) node);
        }
        else if (node.isProcessingInstruction()) {
            write((ProcessingInstruction) node);
        }
        else if (node.isDocument()) {
            write((Document) node);
        }
        else if (node.isDocType()) {
            write((DocType) node);
        }
        else {
            throw new XMLException(
              "Serializer cannot directly serialize a " 
              + node.getClass()
            );    
        }
        
    }
 
   /** <p>
     *   Writes a string onto the underlying <code>OutputStream</code>.
     *   Non-ASCII characters that are not available in the
     *   current character set are hexadecimally escaped.
     *   The three reserved characters &lt;, &gt;, and &amp; 
     *   are escaped using the standard entity references 
     *   <code>&amp;lt;</code>, <code>&amp;gt;</code>, 
     *   and <code>&amp;amp;</code>.
     *   Double and single quotes are not escaped.
     * </p> 
     * 
     * @param text the <code>String</code> to serialize.
     * 
     * @throws IOException if the underlying <code>OutputStream</code> 
     *     encounters an I/O error
     */
    protected final void writePCDATA(String text) throws IOException {
        escaper.writePCDATA(text);
    }   
 
   /** <p>
     *   Writes a string onto the underlying <code>OutputStream</code>.
     *   Non-ASCII characters that are not available in the
     *   current character set are escaped using hexadeicmal numeric
     *   character references.  Carriage returns, line feeds, and tabs
     *   are also escaped using hexadecimal numeric character 
     *   references in order to ensure their preservation on a round
     *   trip. The four reserved characters &lt;, &gt;, &amp;,  
     *   and &quot; are escaped using the standard entity references 
     *   <code>&amp;lt;</code>, <code>&amp;gt;</code>, 
     *   <code>&amp;amp;</code>, and <code>&amp;quot;</code>. 
     *   The single quote is not escaped. 
     * </p> 
     * 
     * @param value the <code>String</code> to serialize.
     * 
     * @throws IOException if the underlying <code>OutputStream</code> 
     *     encounters an I/O error
     */
    protected final void writeAttributeValue(String value)
      throws IOException {
        escaper.writeAttributeValue(value);
    }   
 
   /** <p>
     *   Writes a string onto the underlying <code>OutputStream</code>.
     *   without escaping any characters.
     *   Non-ASCII characters that are not available in the
     *   current character set cause an <code>IOException</code>.
     * </p> 
     * 
     * @param text the <code>String</code> to serialize.
     * 
     * @throws IOException if the underlying <code>OutputStream</code>
     *     encounters an I/O error or <code>text</code> contains 
     *     characters not available in the current character set
     */
    protected final void writeMarkup(String text) throws IOException {
        escaper.writeMarkup(text);
    }   
 
   /** <p>
     *   Writes the current line break string
     *   onto the underlying <code>OutputStream</code> and indents
     *   as specified by the current level and the indent property.
     * </p> 
     * 
     * @throws IOException if the underlying <code>OutputStream</code> 
     *     encounters an I/O error
     */
    protected final void breakLine() throws IOException {
        escaper.breakLine();
    }   
    
    /**
     * <p>
     * Flush the data onto the output stream.
     * It is not enough to flush the output stream.
     * You must flush the serializer object itself because it
     * uses some internal buffering.
     * The serializer will flush the underlying output stream.
     * </p>
     * 
     * @throws IOException  if the underlying  
     *     <code>OutputStream</code> encounters an I/O error
     */
    public void flush() throws IOException {
        escaper.flush();    
    }

    /**
     * <p>
     * Returns the number of spaces this serializer indents.
     * </p>
     * 
     * @return the number of spaces this serializer indents
     *     each successive level beyond the previous one
     */
    public int getIndent() {
        return escaper.getIndent();
    }


    /**
     * <p>
     * Sets the number of additional spaces to add to each successive
     * level in the hierarchy. Use 0 for no extra indenting. The 
     * maximum indentation is in limited to approximately half the
     * maximum line length. The serializer will not indent further 
     * than that no matter how many levels deep the hierarchy is.
     * </p>
     * 
     * <p>
     *   When this variable is set to a value greater than 0,
     *   the serializer does not preserve white space. Spaces,
     *   tabs, carriage returns, and line feeds can all be 
     *   interchanged at the serializer's discretion, and additional
     *   white space may be added before and after tags.
     *   Carriage returns, line feeds, and tabs will not be 
     *   escaped with numeric character references.
     * </p>
     * 
     * <p>
     *   Inside elements with an <code>xml:space="preserve"</code> 
     *   attribute, white space is preserved and no indenting 
     *   takes place, regardless of the setting of the indent
     *   property, unless, of course, an 
     *   <code>xml:space="default"</code> attribute overrides the
     *   <code>xml:space="preserve"</code> attribute.
     * </p>
     * 
     * <p>
     *   The default value for indent is 0; that is, the default is
     *   not to add or subtract any white space from the source
     *   document.  
     * </p>
     * 
     * @param indent the number of spaces to indent 
     *      each successive level of the hierarchy
     * 
     * @throws IllegalArgumentException if indent is less than zero
     * 
     */
    public void setIndent(int indent) {
        if (indent < 0) {
            throw new IllegalArgumentException(
              "Indent cannot be negative"
            );
        }
        escaper.setIndent(indent);
    }

    /**
     * <p>
     * Returns the <code>String</code> used as a line separator.
     * This is always <code>"\n"</code>, <code>"\r"</code>, 
     * or <code>"\r\n"</code>.
     * </p>
     * 
     * @return the line separator
     */
    public String getLineSeparator() {
        return escaper.getLineSeparator();
    }

    /**
     * <p>
     * Sets the lineSeparator. This can only be one of the 
     * three strings <code>"\n"</code>, <code>"\r"</code>, 
     * or <code>"\r\n"</code>. All other values are forbidden.
     * If this method is invoked, then 
     * line separators in the character data will be changed to this
     * string. Line separators in attribute values will be changed
     * to the hexadecimal numerica character references corresponding
     * to this string.
     * </p>
     * 
     * <p>
     *  The default line separator is <code>"\r\n"</code>. However, 
     *  line separators in character data and attribute values are not 
     *  changed to this string, unless you explicitly call this method.
     * </p>
     * 
     * @param lineSeparator The lineSeparator to set
     * 
     * @throws IllegalArgumentException if you attempt to use any line
     *    separator other than <code>"\n"</code>, <code>"\r"</code>, 
     *    or <code>"\r\n"</code>.
     * 
     */
    public void setLineSeparator(String lineSeparator) {
        escaper.setLineSeparator(lineSeparator);  
    }

    /**
     * <p>
     * Returns the preferred maximum line length.
     * </p>
     * 
     * @return the maximum line length.
     */
    public int getMaxLength() {
        return escaper.getMaxLength();
    }

    /**
     * <p>
     * Sets the suggested maximum line length for this serializer.
     * Setting this to 0 indicates that no automatic wrapping is to be
     * performed. When a line approaches this length, the serializer 
     * begins looking for opportunities to break the line. Generally 
     * it will break on any ASCII white space character (tab, carriage 
     * return, linefeed, and space). In some circumstances the 
     * serializer may not be able to break the line before the maximum
     * length is reached. For instance, if an element name is longer 
     * than the maximum line length the only way to correctly 
     * serialize it is to exceed the maximum line length. In this case,
     *  the serializer will exceed the maximum line length.
     * </p>
     * 
     * <p>
     * The default value for max line length is 0, which is  
     * interpreted as no maximum line length. 
     * Setting this to a negative value just sets it to 0. 
     * </p>
     * 
     * <p>
     *   When this variable is set to a value greater than 0,
     *   the serializer does not preserve white space. Spaces,
     *   tabs, carriage returns, and line feeds can all be 
     *   interchanged at the serializer's discretion.
     *   Carriage returns, line feeds, and tabs will not be 
     *   escaped with numeric character references.
     * </p>
     * 
     * <p>
     *   Inside elements with an <code>xml:space="preserve"</code> 
     *   attribute, the maximum line length is not enforced, 
     *   regardless of the setting of the this property, unless,  
     *   of course, an <code>xml:space="default"</code> attribute 
     *   overrides the <code>xml:space="preserve"</code> attribute.
     * </p>
     * 
     * @param maxLength the suggested maximum line length
     */
    public void setMaxLength(int maxLength) {
        escaper.setMaxLength(maxLength);
    }

    /**
     * <p>
     * Returns true if this serializer preserves the original
     * base URIs by inserting extra <code>xml:base</code> attributes.
     * </p>
     * 
     * @return true if this <code>Serializer</code> inserts
     *    extra <code>xml:base</code> attributes to attempt to 
     *    preserve base URI information from the document.
     */
    public boolean getPreserveBaseURI() {
        return preserveBaseURI;
    }

    /**
     * <p>
     * Determines whether this <code>Serializer</code> inserts
     * extra <code>xml:base</code> attributes to attempt to 
     * preserve base URI information from the document.
     * The default is false, do not preserve base URI information.
     * <code>xml:base</code> attributes that are part of the document's
     * infoset are always output. This property only determines 
     * whether or not extra <code>xml:base</code> attributes are added.
     * </p>
     * 
     * @param preserve true if <code>xml:base</code> 
     *     attributes should be added as necessary
     *     to preserve base URI information 
     */
    public void preserveBaseURI(boolean preserve) {
        this.preserveBaseURI = preserve;
    }

}