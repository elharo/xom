/* Copyright 2002-2005 Elliotte Rusty Harold
   
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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Locale;

/**
 * <p>
 *  Outputs a <code>Document</code> object in a specific encoding using
 *  various options for controlling white space, normalization,
 *  indenting, line breaking, and base URIs. However, in general these 
 *  options do affect the document's infoset. In particular, if you set 
 *  either the maximum line length or the indent size to a positive  
 *  value, then the serializer will not respect input white space. It 
 *  may trim leading and trailing space, condense runs of white 
 *  space to a single space, convert carriage  returns and line 
 *  feeds to spaces, add extra space where none was present before, 
 *  and otherwise muck with the document's white space. 
 *  The defaults, however, preserve all significant white space
 *  including ignorable white space and boundary white space.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0
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
     * @throws NullPointerException if <code>out</code> is null
     */
    public Serializer(OutputStream out) {
        
        if (out == null) {
            throw new NullPointerException("Null OutputStream");
        } 
        try {
            Writer writer = new OutputStreamWriter(out, "UTF8");
            writer = new BufferedWriter(writer);
            escaper = TextWriterFactory.getTextWriter(writer, "UTF-8");
        }
        catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(
              "The VM is broken. It does not understand UTF-8.");
        }
        
    }
    
    
    /**
     * <p>
     * Create a new serializer that uses the specified encoding.
     * The encoding must be recognized by the Java virtual machine. If 
     * you attempt to use an encoding that the local Java virtual 
     * machine does not support, the constructor will throw an 
     * <code>UnsupportedEncodingException</code>.
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
     *   <li>ISO-8859-11 (a.k.a. TIS-620)</li>
     *   <li>ISO-8859-13</li>
     *   <li>ISO-8859-14</li>
     *   <li>ISO-8859-15</li>
     *   <li>ISO-8859-16</li>
     *   <li>IBM037 (a.k.a. CP037, EBCDIC-CP-US, EBCDIC-CP-CA, 
     *         EBCDIC-CP-WA, EBCDIC-CP-NL, and CSIBM037)</li>
     *   <li>GB18030</li>
     * </ul>
     * 
     * <p>
     * You can use encodings not in this list if the virtual
     * machine supports them. However, they may be
     * significantly slower than the encodings in this list.
     * </p>
     * 
     * <p>
     * I've noticed Java has significant bugs in its handling of some
     * of these encodings. In some cases such as 0x80 in Big5, XOM
     * will escape a character that should not need to be escaped
     * because Java can't output that character in the specified 
     * encoding, even though the output character set does contain it.
     * :-(
     * </p>
     * 
     * @param out the output stream to write the document on
     * @param encoding the character encoding for the serialization

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
        
        this.setOutputStream(out, encoding);
        
    }
    
    
    /**
     * <p>
     * Flushes the previous output stream and 
     * redirects further output to the new output stream.
     * </p>
     * 
     * 
     * @param out the output stream to write the document on

     * @throws NullPointerException if <code>out</code> is null
     * @throws IOException if the previous output stream 
     *     encounters an I/O error when flushed
     *  
     */
    public void setOutputStream(OutputStream out) 
      throws IOException {
        
        // flush any data onto the old output stream
        this.flush();
        int maxLength = getMaxLength();
        int indent = this.getIndent();
        String lineSeparator = getLineSeparator();
        boolean nfc = getUnicodeNormalizationFormC(); 
        String encoding = escaper.getEncoding();
        setOutputStream(out, encoding);   
        setIndent(indent);
        setMaxLength(maxLength);
        setUnicodeNormalizationFormC(nfc);
        setLineSeparator(lineSeparator); 
        
    }

    
    private void setOutputStream(OutputStream out, String encoding)
        throws UnsupportedEncodingException {
        
        Writer writer;  
        String encodingUpperCase = encoding.toUpperCase(Locale.ENGLISH);
        // Java's Cp037 encoding is broken, so we have to
        // provide our own.
        if (encodingUpperCase.equals("IBM037")
          || encodingUpperCase.equals("CP037")
          || encodingUpperCase.equals("EBCDIC-CP-US")
          || encodingUpperCase.equals("EBCDIC-CP-CA")
          || encodingUpperCase.equals("EBCDIC-CP-WA")
          || encodingUpperCase.equals("EBCDIC-CP-NL")
          || encodingUpperCase.equals("CSIBM037")) {
            writer = new EBCDICWriter(out);
        }
        else if (encodingUpperCase.equals("UTF-16") 
          || encodingUpperCase.equals("ISO-10646-UCS-2")) {
           // For compatibility with Java 1.2 and earlier
           writer = new OutputStreamWriter(out, "UnicodeBig");  
        }
        else if (encodingUpperCase.equals("ISO-8859-11") 
          || encodingUpperCase.equals("TIS-620")) {
           // Java doesn't recognize the name ISO-8859-11 and 
           // Java 1.3 and earlier don't recognize TIS-620
           writer = new OutputStreamWriter(out, "TIS620");  
        }
        else writer = new OutputStreamWriter(out, encoding);
        writer = new BufferedWriter(writer);
        this.escaper = TextWriterFactory.getTextWriter(writer, encoding);
        
    }

    
    /**
     * <p>
     * Serializes a document onto the output 
     * stream using the current options.
     * </p>
     * 
     * @param doc the <code>Document</code> to serialize
     * 
     * @throws IOException if the underlying output stream
     *      encounters an I/O error
     * @throws NullPointerException if <code>doc</code> is null
     * @throws UnavailableCharacterException if the document contains 
     *     an unescapable character (e.g. in an element name) that is 
     *     not available in the current encoding
     */
    public void write(Document doc) throws IOException {
        
        escaper.reset();
        // The OutputStreamWriter automatically inserts
        // the byte order mark if necessary.
        writeXMLDeclaration();
        int childCount = doc.getChildCount();
        for (int i = 0; i < childCount; i++) {
            writeChild(doc.getChild(i)); 
            
            // Might want to remove this line break in a 
            // non-XML serializer where it's not guaranteed to be 
            // OK to add extra line breaks in the prolog
            escaper.breakLine();
        }       
        escaper.flush();
        
    }


    /**
     * <p>
     * Writes the XML declaration onto the output stream,
     * followed by a line break.
     * </p>
     * 
     * @throws IOException if the underlying output stream
     *      encounters an I/O error
     */
    protected void writeXMLDeclaration() throws IOException {
        
        escaper.writeMarkup("<?xml version=\"1.0\" encoding=\"");
        escaper.writeMarkup(escaper.getEncoding());
        escaper.writeMarkup("\"?>");
        escaper.breakLine();
        
    }
    
    
    /**
     * <p>
     * Serializes an element onto the output stream using the current
     * options. The result is guaranteed to be well-formed. If 
     * <code>element</code> does not have a parent element, the output  
     * will also be namespace well-formed.
     * </p>
     * 
     * <p>
     *   If the element is empty, this method invokes 
     *   <code>writeEmptyElementTag</code>. If the element is not 
     *   empty, then: 
     * </p>
     * 
     * <ol>
     *   <li>It calls <code>writeStartTag</code></li>
     *   <li>It passes each of the element's children to 
     *       <code>writeChild</code> in order.</li>
     *   <li>It calls <code>writeEndTag</code></li>
     * </ol>
     * 
     * <p>
     *   It may break lines or add white space if the serializer has
     *   been configured to indent or use a maximum line length.
     * </p>
     * 
     * @param element the <code>Element</code> to serialize
     * 
     * @throws IOException if the underlying output stream
     *     encounters an I/O error
     * @throws UnavailableCharacterException if the element name   
     *     contains a character that is not available in the 
     *     current encoding
     */
    protected void write(Element element) throws IOException {

        if (escaper.isIndenting() 
          && !escaper.isPreserveSpace() 
          && !escaper.justBroke()) {
            escaper.breakLine();
        }
        
        // workaround for case where only children are empty text nodes
        boolean hasRealChildren = false;
        int childCount = element.getChildCount();
        for (int i = 0; i < childCount; i++) {
            Node child = element.getChild(i);
            if (child.isText()) {
                Text t = (Text) child;
                if (t.isEmpty()) continue;
            }
            hasRealChildren = true;
            break;
        }
        
        if (hasRealChildren) {
            writeStartTag(element);
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
            for (int i = 0; i < childCount; i++) {
                writeChild(element.getChild(i)); 
            }
            escaper.decrementIndent();
            if (escaper.getIndent() > 0 && !escaper.isPreserveSpace()) {
                if (hasNonTextChildren(element)) {
                    escaper.breakLine();
                }
            }
            writeEndTag(element);
            
            // restore parent value
            if  (newXMLSpaceValue != null) {
                escaper.setPreserveSpace(wasPreservingWhiteSpace);
            }
                        
        }
        else {
            writeEmptyElementTag(element);   
        }
        escaper.flush();
        
    }

    
    private boolean hasNonTextChildren(Element element) {
        
        int childCount = element.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (! element.getChild(i).isText()) return true;  
        }
        return false;
        
    }


    // writeEndTag should not normally throw UnavailableCharacterException 
    // because that would already have been thrown for the
    // corresponding start-tag.
    /**
     * <p>
     *   Writes the end-tag for an element in the form
     *   <code>&lt;/<i>name</i>&gt;</code>.
     * </p>
     * 
     * @param element the element whose end-tag is written
     * 
     * @throws IOException if the underlying output stream
     *     encounters an I/O error
     */
    protected void writeEndTag(Element element) throws IOException {
        escaper.writeMarkup("</");
        escaper.writeMarkup(element.getQualifiedName());
        escaper.writeMarkup(">");
    }

    
    /**
     * 
     * <p>
     *  Writes the start-tag for the element including
     *  all its namespace declarations and attributes.
     * </p>
     * 
     * <p>
     *   The <code>writeAttributes</code> method is called to write
     *   all the non-namespace-declaration attributes. 
     *   The <code>writeNamespaceDeclarations</code> method
     *   is called to write all the namespace declaration attributes.
     * </p>
     * 
     * @param element the element whose start-tag is written
     * 
     * @throws IOException if the underlying output stream
     *     encounters an I/O error
     * @throws UnavailableCharacterException if the name of the element
     *     or the name of any of its attributes contains a character  
     *     that is not available in the current encoding
     */
    protected void writeStartTag(Element element) throws IOException {
        writeTagBeginning(element);
        escaper.writeMarkup('>');
    }

    
    /**
     * 
     * <p>
     *  Writes an empty-element tag for the element 
     *  including all its namespace declarations and attributes.
     * </p>
     * 
     * <p>
     *   The <code>writeAttributes</code> method is called to write
     *   all the non-namespace-declaration attributes. 
     *   The <code>writeNamespaceDeclarations</code> method
     *   is called to write all the namespace declaration attributes.
     * </p>
     * 
     * <p>
     *   If subclasses don't wish empty-element tags to be used,
     *   they can override this method to simply invoke 
     *   <code>writeStartTag</code> followed by 
     *   <code>writeEndTag</code>.
     * </p>
     * 
     * @param element the element whose empty-element tag is written
     * 
     * @throws IOException if the underlying output stream
     *     encounters an I/O error
     * @throws UnavailableCharacterException if the name of the element or the name of
     *     any of its attributes contains a character that is not 
     *     available in the current encoding
     */
    protected void writeEmptyElementTag(Element element) 
      throws IOException {
        writeTagBeginning(element);
        escaper.writeMarkup("/>");
    }

    
    // This just extracts the commonality between writeStartTag  
    // and writeEmptyElementTag
    private void writeTagBeginning(Element element) 
      throws IOException {
        escaper.writeMarkup('<');
        escaper.writeMarkup(element.getQualifiedName());
        writeAttributes(element);           
        writeNamespaceDeclarations(element);
    }


    /**
     * <p>
     *   Writes all the attributes of the specified
     *   element onto the output stream, one at a time, separated
     *   by white space. If preserveBaseURI is true, and it is
     *   necessary to add an <code>xml:base</code> attribute
     *   to the element in order to preserve the base URI, then 
     *   that attribute is also written here.
     *   Each individual attribute is written by invoking
     *   <code>write(Attribute)</code>.
     * </p>
     * 
     * @param element the <code>Element</code> whose attributes are 
     *     written
     * @throws IOException if the underlying output stream
     *     encounters an I/O error
     * @throws UnavailableCharacterException if the name of any of
     *     the element's attributes contains a character that is not 
     *     available in the current encoding
     */
    protected void writeAttributes(Element element)
      throws IOException {
          
        // check to see if we need an xml:base attribute
        if (preserveBaseURI) {
            ParentNode parent = element.getParent();
            if (element.getAttribute("base", 
              "http://www.w3.org/XML/1998/namespace") == null) {
                String baseValue = element.getBaseURI();
                if (parent == null 
                  || parent.isDocument()
                  || !element.getBaseURI()
                       .equals(parent.getBaseURI())) {
                       
                    escaper.writeMarkup(' ');
                    Attribute baseAttribute = new Attribute(
                      "xml:base", 
                      "http://www.w3.org/XML/1998/namespace", 
                      baseValue);
                    write(baseAttribute);
                }
            }
        }
        
        int attributeCount = element.getAttributeCount();
        for (int i = 0; i < attributeCount; i++) {
            Attribute attribute = element.getAttribute(i);
            escaper.writeMarkup(' ');
            write(attribute);
        }  
    }

    
    /**
     * <p>
     *   Writes all the namespace declaration
     *   attributes of the specified element onto the output stream,
     *   one at a time, separated by white space. Each individual 
     *   declaration is written by invoking 
     *   <code>writeNamespaceDeclaration</code>.
     * </p>
     * 
     * @param element the <code>Element</code> whose attributes are 
     *     written
     * @throws IOException if the underlying output stream
     *     encounters an I/O error
     * @throws UnavailableCharacterException if any of the element's namespace prefixes
     *     contains a character that is not available in the current 
     *     encoding
     */
    protected void writeNamespaceDeclarations(Element element)
      throws IOException {
        
        ParentNode parent = element.getParent();
        int count = element.getNamespaceDeclarationCount();
        for (int i = 0; i < count; i++) {
            String additionalPrefix = element.getNamespacePrefix(i);
            String uri = element.getNamespaceURI(additionalPrefix);
            if (parent.isElement()) {
               Element parentElement = (Element) parent;   
               if (uri.equals(
                 parentElement.getNamespaceURI(additionalPrefix))) {
                   continue;
               } 
            }
            else if (uri.equals("")) {
                continue; // no need to say xmlns=""   
            }
            
            escaper.writeMarkup(' ');
            writeNamespaceDeclaration(additionalPrefix, uri);
        } 
    }


    /**
     * <p>
     *   Writes a namespace declaration in the form
     *   <code>xmlns:<i>prefix</i>="<i>uri</i>"</code> or 
     *   <code>xmlns="<i>uri</i>"</code>. It does not write
     *   the spaces on either side of the namespace declaration.
     *   These are written by <code>writeNamespaceDeclarations</code>.
     * </p>
     * 
     * @param prefix the namespace prefix; the empty string for the
     *     default namespace
     * @param uri the namespace URI
     * 
     * @throws IOException if the underlying output stream
     *     encounters an I/O error
     * @throws UnavailableCharacterException if the namespace prefix contains a 
     *     character that is not available in the current encoding
     */
    protected void writeNamespaceDeclaration(String prefix, String uri)
      throws IOException {
        
        if ("".equals(prefix)) {
            escaper.writeMarkup("xmlns"); 
        }
        else {
            escaper.writeMarkup("xmlns:"); 
            escaper.writeMarkup(prefix); 
        } 
        escaper.writeMarkup("=\""); 
        escaper.writePCDATA(uri);   
        escaper.writeMarkup('\"');
        
    }

    
    /**
     * <p>
     *   Writes an attribute in the form 
     *   <code><i>name</i>="<i>value</i>"</code>.
     *   Characters in the attribute value are escaped as necessary.
     * </p>
     * 
     * @param attribute the <code>Attribute</code> to write
     * 
     * @throws IOException if the underlying output stream
     *     encounters an I/O error
     * @throws UnavailableCharacterException if the attribute name contains a character 
     *     that is not available in the current encoding
     * 
     */
    protected void write(Attribute attribute) throws IOException {
        escaper.writeMarkup(attribute.getQualifiedName());
        escaper.writeMarkup("=\"");
        escaper.writeAttributeValue(attribute.getValue());
        escaper.writeMarkup('\"');  
    }
    
    
    /**
     * <p>
     * Writes a comment onto the output stream using the current 
     * options. Since character and entity references are not resolved
     * in comments, comments can only be serialized when all
     * characters they contain are available in the current 
     * encoding.
     * </p>
     * 
     * @param comment the <code>Comment</code> to serialize
     * 
     * @throws IOException if the underlying output stream 
     *     encounters an I/O error
     * @throws UnavailableCharacterException if the comment contains a 
     *     character that is not available in the current encoding
     */
    protected void write(Comment comment) throws IOException {
        if (escaper.isIndenting()) escaper.breakLine();
        escaper.writeMarkup("<!--");
        escaper.writeMarkup(comment.getValue());
        escaper.writeMarkup("-->");
    }
    
    
    /**
     * <p>
     * Writes a processing instruction
     * onto the output stream using the current options.
     * Since character and entity references are not resolved
     * in processing instructions, processing instructions
     * can only be serialized when all
     * characters they contain are available in the current 
     * encoding.
     * </p>
     * 
     * @param instruction the <code>ProcessingInstruction</code> 
     *     to serialize
     * 
     * @throws IOException if the underlying output stream
     *     encounters an I/O error
     * @throws UnavailableCharacterException if the comment contains a 
     *     character that is not available in the current encoding
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
     * Writes a <code>Text</code> object
     * onto the output stream using the current options.
     * Reserved characters such as &lt;, &gt; and "
     * are escaped using the standard entity references 
     * such as <code>&amp;lt;</code>, <code>&amp;gt;</code>, 
     * and <code>&amp;quot;</code>.
     * </p>
     * 
     * <p>
     * Characters which cannot be encoded in the current character set
     * (for example, &Omega; in ISO-8859-1) are encoded using 
     * character references. 
     * </p> 
     * 
     * @param text the <code>Text</code> to serialize
     * 
     * @throws IOException if the underlying output stream
     *     encounters an I/O error
     */
    protected void write(Text text) throws IOException {
        
        // XXX Is there a shortcut that takes advantage of the
        // data being stored in UTF-8 here? perhaps even if only
        // when serializing to UTF-8?
        String value = text.getValue();
        if (text.isCDATASection() 
          && value.indexOf("]]>") == -1) {
            if (!(escaper instanceof UnicodeWriter)) {
                int length = value.length();
                for (int i = 0; i < length; i++) {
                   if (escaper.needsEscaping(value.charAt(i))) {
                        // can't use CDATA section
                        escaper.writePCDATA(value);
                        return;   
                   }   
                }
            }
            escaper.writeMarkup("<![CDATA[");
            escaper.writeMarkup(value);
            escaper.writeMarkup("]]>");
        }
        // is this boundary whitespace we can ignore?
        else if (isBoundaryWhitespace(text)) {
            return; // without writing node
        }
        else {
            escaper.writePCDATA(value);
        }
        
    }  
    
    
    private boolean isBoundaryWhitespace(Text text) {
        
        if (getIndent() <= 0) return false;
        
        // XXX check this without getValue
        if (!"".equals(text.getValue().trim())) return false;
        ParentNode parent = text.getParent();
        
        int position = parent.indexOf(text);
        
        if (position == 0 && parent.getChildCount() == 1) return false;
        Node previous = null;
        Node next = null;
        if (position != 0) previous = parent.getChild(position-1);
        if (position != parent.getChildCount()-1) {
            next = parent.getChild(position+1);
        }
        if (previous == null || !previous.isText()) {
            if (next == null || !next.isText()) {
                return true;
            }
        }
        
        return false;
        
    }

    
    /**
     * <p>
     * Writes a <code>DocType</code> object
     * onto the output stream using the current options.
     * </p>
     * 
     * @param doctype the document type declaration to serialize
     * 
     * @throws IOException if the underlying 
     *     output stream encounters an I/O error
     * @throws UnavailableCharacterException if the document type   
     *     declaration contains a character that is not available 
     *     in the current encoding
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
            escaper.setInDocType(true);
            escaper.writeMarkup(internalDTDSubset); 
            escaper.setInDocType(false);
            escaper.writeMarkup("]"); 
        }

        escaper.writeMarkup(">");
        
    }   

    
    /**
     * <p>
     * Writes a child node onto the output stream using the  
     * current options. It is invoked when walking the tree to
     * serialize the entire document. It is not called, and indeed
     * should not be called, for either the <code>Document</code> 
     * node or for attributes. 
     * </p>
     * 
     * @param node the <code>Node</code> to serialize
     * 
     * @throws IOException if the underlying output stream
     *     encounters an I/O error
     * @throws XMLException if an <code>Attribute</code> or a 
     *     <code>Document</code> is passed to this method
     */
    protected void writeChild(Node node) throws IOException {
        
        if (node.isElement()) {
            write((Element) node);
        }
        else if (node.isText()) {
            write((Text) node);
        }
        else if (node.isComment()) {
            write((Comment) node);
        }
        else if (node.isProcessingInstruction()) {
            write((ProcessingInstruction) node);
        }
        else if (node.isDocType()) {
            write((DocType) node);
        }
        else {
            throw new XMLException("Cannot write a " + 
              node.getClass().getName() + 
              " from the writeChildNode() method");
        }
        
    }
 
    
    /** <p>
     * Writes a string onto the underlying output stream.
     * Non-ASCII characters that are not available in the
     * current character set are encoded with numeric character
     * references. The three reserved characters &lt;, &gt;, and &amp; 
     * are escaped using the standard entity references 
     * <code>&amp;lt;</code>, <code>&amp;gt;</code>, 
     * and <code>&amp;amp;</code>.
     * Double and single quotes are not escaped.
     * </p> 
     * 
     * @param text the parsed character data to serialize
     * 
     * @throws IOException if the underlying output stream 
     *     encounters an I/O error
     */
    protected final void writeEscaped(String text) throws IOException {
        escaper.writePCDATA(text);
    }   
 
    /** <p>
     *   Writes a string onto the underlying output stream.
     *   Non-ASCII characters that are not available in the
     *   current character set are escaped using hexadecimal numeric
     *   character references. Carriage returns, line feeds, and tabs
     *   are also escaped using hexadecimal numeric character 
     *   references in order to ensure their preservation on a round
     *   trip. The four reserved characters &lt;, &gt;, &amp;,  
     *   and &quot; are escaped using the standard entity references 
     *   <code>&amp;lt;</code>, <code>&amp;gt;</code>, 
     *   <code>&amp;amp;</code>, and <code>&amp;quot;</code>. 
     *   The single quote is not escaped. 
     * </p> 
     * 
     * @param value the attribute value to serialize
     * 
     * @throws IOException if the underlying output stream 
     *     encounters an I/O error
     */
    protected final void writeAttributeValue(String value)
      throws IOException {
        escaper.writeAttributeValue(value);
    }   
 
    
    /** <p>
     *   Writes a string onto the underlying output stream.
     *   without escaping any characters.
     *   Non-ASCII characters that are not available in the
     *   current character set cause an <code>IOException</code>.
     * </p> 
     * 
     * @param text the <code>String</code> to serialize
     * 
     * @throws IOException if the underlying output stream
     *     encounters an I/O error or <code>text</code> contains 
     *     characters not available in the current character set
     */
    protected final void writeRaw(String text) throws IOException {
        escaper.writeMarkup(text);
    }   
 
    
    /** <p>
     *   Writes the current line break string
     *   onto the underlying output stream and indents
     *   as specified by the current level and the indent property.
     * </p> 
     * 
     * @throws IOException if the underlying output stream 
     *     encounters an I/O error
     */
    protected final void breakLine() throws IOException {
        escaper.breakLine();
    }   
    
    
    /**
     * <p>
     * Flushes the data onto the output stream.
     * It is not enough to flush the output stream.
     * You must flush the serializer object itself because it
     * uses some internal buffering.
     * The serializer will flush the underlying output stream.
     * </p>
     * 
     * @throws IOException  if the underlying  
     *     output stream encounters an I/O error
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
     * Returns the string used as a line separator.
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
     * Sets the line separator. This can only be one of the 
     * three strings <code>"\n"</code>, <code>"\r"</code>, 
     * or <code>"\r\n"</code>. All other values are forbidden.
     * If this method is invoked, then 
     * line separators in the character data will be changed to this
     * string. Line separators in attribute values will be changed
     * to the hexadecimal numeric character references corresponding
     * to this string.
     * </p>
     * 
     * <p>
     *  The default line separator is <code>"\r\n"</code>. However, 
     *  line separators in character data and attribute values are not 
     *  changed to this string, unless this method is called first.
     * </p>
     * 
     * @param lineSeparator the line separator to set
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
     * @return the preferred maximum line length.
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
     * The default value for maximum line length is 0, which is  
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
     * @param maxLength the preferred maximum line length
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
     * Determines whether this serializer inserts
     * extra <code>xml:base</code> attributes to attempt to 
     * preserve base URI information from the document.
     * The default is false, do not preserve base URI information.
     * <code>xml:base</code> attributes that have been explicitly
     * added to an element are always output. This property only  
     * determines whether or not extra <code>xml:base</code> 
     * attributes are added.
     * </p>
     * 
     * @param preserve true if <code>xml:base</code> 
     *     attributes should be added as necessary
     *     to preserve base URI information 
     */
    public void setPreserveBaseURI(boolean preserve) {
        this.preserveBaseURI = preserve;
    }
    
    
    /**
     * <p>
     *   Returns the name of the character encoding used by 
     *   this serializer.
     * </p>
     * 
     * @return the encoding used for the output document
     */
    public String getEncoding() {
        return escaper.getEncoding();   
    }
    
    /**
     * <p>
     *   If true, this property indicates serialization will
     *   perform Unicode normalization on all data using normalization
     *   form C (NFC). Performing Unicode normalization may change the
     *   document's infoset. The default is false; do not normalize.
     * </p>
     * 
     * <p>
     *   The implementation used is IBM's <a target="_top" href=
     *   "http://oss.software.ibm.com/icu4j/index.html">International
     *   Components for Unicode <i>for Java</i> (ICU4J) 2.6</a>. 
     *   This version is based on Unicode 4.0. 
     * </p>
     * 
     * <p>
     *   This feature has not yet been benchmarked or optimized.
     *   It may result in substantially slower code. 
     * </p>
     * 
     * <p>
     *   If all your data is in the first 256 code points of Unicode
     *   (i.e. the ISO-8859-1, Latin-1 character set), then it's 
     *   already in normalization form C and normalizing won't change
     *   anything.
     * </p>
     * 
     * @param normalize true if normalization is performed; 
     *     false if it isn't
     */
    public void setUnicodeNormalizationFormC(boolean normalize) {
        escaper.setNFC(normalize);   
    }

    
    /**
     * <p>
     *   Indicates whether serialization will
     *   perform Unicode normalization on all data using normalization
     *   form C (NFC). The default is false; do not normalize.
     * </p>
     * 
     * @return true if this serializer performs Unicode 
     *     normalization; false if it doesn't
     */
    public boolean getUnicodeNormalizationFormC() {
        return escaper.getNFC();   
    }
    
    
    /**
     * <p>
     *   Returns the current column number of the output stream. This 
     *   method useful for subclasses that implement their own pretty
     *   printing strategies by inserting white space and line breaks 
     *   at appropriate points.
     * </p>
     * 
     * <p>
     *   Columns are counted based on Unicode characters, not Java
     *   chars. A surrogate pair counts as one character in this 
     *   context, not two. However, a character followed by a 
     *   combining character (e.g. e followed by combining accent
     *   acute) counts as two characters. This latter choice
     *   (treating combining characters like regular characters)
     *   is under review, and may change in the future if it's not
     *   too big a performance hit.
     * </p>
     * 
     * @return the current column number
     */
    protected final int getColumnNumber() {
        return escaper.getColumnNumber();
    }
    
}