// Copyright 2003 Elliotte Rusty Harold
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

package nu.xom.samples;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.ProcessingInstruction;
import nu.xom.Serializer;

/**
 * <p>
 *   This class writes XML with a maximum line length,
 *   but only breaks lines inside tags. It does
 *   not change a document's infoset. 
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d22
 *
 */
public class WrappingSerializer extends Serializer {

    public WrappingSerializer(OutputStream out) {
        super(out);
    }

    public WrappingSerializer(OutputStream out, String encoding) 
      throws UnsupportedEncodingException {
        super(out, encoding);
    }
    
    private int maxLength;
    
    /**
     * <p>
     * Returns the preferred maximum line length.
     * </p>
     * 
     * @return the maximum line length.
     */
    public int getMaxLength() {
        return this.maxLength;
    }

    /**
     * <p>
     * Sets the suggested maximum line length for this serializer.
     * Setting this to 0 indicates that no automatic wrapping is to be
     * performed. When a line approaches this length, the serializer 
     * begins looking for opportunities to break the line. 
     * It will only break inside a tag, at places that do not
     * affect the infoset, such as between attribute values or
     * before the closing <code>></code>. In some circumstances the 
     * serializer may not be able to break the line before the maximum
     * length is reached. In this case,
     *  the serializer will exceed the maximum line length.
     * </p>
     * 
     * <p>
     * The default value for max line length is 0, which is  
     * interpreted as no maximum line length. 
     * Setting this to a negative value just sets it to 0. 
     * </p>
     * 
     * @param maxLength the suggested maximum line length
     */
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    protected void writeStartTag(Element element) 
      throws IOException {
        writeRaw("<");
        writeRaw(element.getQualifiedName());
        writeAttributes(element);           
        writeNamespaceDeclarations(element);
        if (needsBreak()) breakLine();
        writeRaw(">");
    }

    protected void writeEmptyElementTag(Element element) 
      throws IOException {
        writeRaw("<");
        writeRaw(element.getQualifiedName());
        writeAttributes(element);           
        writeNamespaceDeclarations(element);
        if (needsBreak()) breakLine();
        writeRaw("/>");
    }

    public void writeEndTag(Element element) throws IOException {
        writeRaw("<");
        writeRaw(element.getQualifiedName());
        if (needsBreak()) breakLine();
        writeRaw("/>");
    }

    /**
     * <p>
     *   This method writes an attribute in the form 
     *   <code><i>name</i>="<i>value</i>"</code>.
     *   Characters in the attribute value are escaped as necessary.
     * </p>
     * 
     * @param attribute the <code>Attribute</code> to write
     * 
     * @throws IOException if the underlying <code>OutputStream</code>
     *     encounters an I/O error
     */
    protected void write(Attribute attribute) throws IOException {
        
        String name = attribute.getQualifiedName();
        if (maxLength <= this.getColumnNumber() + name.length()) {
            breakLine();   
        }
        writeRaw(name);
        if (this.getColumnNumber() == maxLength) {
            breakLine();   
        }
        writeRaw("=");
        String value = attribute.getValue();
        if (maxLength < value.length() + 2) {
            breakLine();   
        }
        writeRaw("\""); 
        writeAttributeValue(attribute.getValue());
        writeRaw("\"");  
        
    }

    /**
     * <p>
     *   This writes a namespace declaration in the form
     *   <code>xmlns:<i>prefix</i>="<i>uri</i>"</code> or 
     *   <code>xmlns="<i>uri</i>"</code>.
     * </p>
     * 
     * @param prefix the namespace prefix; the empty string for the
     *     default namespace
     * @param uri the namespace URI
     * 
     * @throws IOException if the underlying <code>OutputStream</code>
     *     encounters an I/O error
     */
    protected void writeNamespaceDeclaration(String prefix, String uri)
      throws IOException {
          
        String name;
        if ("".equals(prefix)) {
            name = "xmlns"; 
        }
        else {
            name = "xmlns:" + prefix; 
        } 
        if (this.maxLength < this.getColumnNumber() + name.length()) {
            breakLine();   
        }
        writeRaw(name);

        if (this.getColumnNumber() == maxLength) {
            breakLine();   
        }
        writeRaw("=");
        
        if (this.maxLength < this.getColumnNumber() + uri.length() + 2) {
            breakLine();   
        }
        writeRaw("\"");
        writeEscaped(uri);   
        writeRaw("\"");
    }

    private boolean needsBreak() {
        if (maxLength > 0) {
            return this.maxLength - this.getColumnNumber() <= 10;  
        }
        return false;
    }

    /**
     * <p>
     * Serializes a <code>ProcessingInstruction</code> object
     * onto the output stream. Line breaks may be inserted 
     * following the target.
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
        writeRaw("<?");
        writeRaw(instruction.getTarget());
        String value = instruction.getValue();
        if (maxLength < getColumnNumber() + value.length() + 3) {
            breakLine();
        }
        else {
            writeRaw(" ");   
        }
        writeRaw(value);
        writeRaw("?>");      
    }

    /**
     * <p>
     * Serializes a <code>DocType</code> object
     * onto the output stream.
     * </p>
     * 
     * @param doctype the document type declaration to serialize
     * 
     * @throws IOException if the underlying 
     *     <code>OutputStream</code> encounters an I/O error
     */
    protected void write(DocType doctype) throws IOException {
        writeRaw("<!DOCTYPE");
        String rootElementName = doctype.getRootElementName();
        if (maxLength < getColumnNumber() + rootElementName.length() + 1) {
            breakLine();
        }
        else {
            writeRaw(" ");   
        }
        writeRaw(rootElementName);
        
        String publicID = doctype.getPublicID();
        String systemID = doctype.getSystemID();
        if (publicID != null) {
            if (maxLength < getColumnNumber() + 6) {
                breakLine();  
            }
            else {
                writeRaw(" ");   
            }
            writeRaw("PUBLIC"); 
              
            if (maxLength < getColumnNumber() + publicID.length() + 2) {
                breakLine();  
            }
            else {
                writeRaw(" ");   
            }
            writeRaw("\"");   
            writeRaw(publicID);
            writeRaw("\"");   
            
            if (maxLength < getColumnNumber() + systemID.length() + 2) {
                breakLine();  
            }
            else {
                writeRaw(" ");   
            }
            writeRaw("\"");   
            writeRaw(systemID);
            writeRaw("\"");   
        } 
        else if (systemID != null) {
            if (maxLength < getColumnNumber() + 6) {
                breakLine();  
            }
            else {
                writeRaw(" ");   
            }
            writeRaw("SYSTEM");    
            
            if (maxLength < getColumnNumber() + systemID.length() + 2) {
                breakLine();  
            }
            else {
                writeRaw(" ");   
            }
            writeRaw("\"");   
            writeRaw(systemID);
            writeRaw("\"");   
        } 
        
        String internalDTDSubset = doctype.getInternalDTDSubset();
        if (!internalDTDSubset.equals("")) {
            if (maxLength < getColumnNumber() + 2) {
                breakLine();  
            }
            else writeRaw(" ");
            writeRaw("[");    
            breakLine();
            writeRaw(internalDTDSubset); 
            breakLine();  
            writeRaw("]"); 
        }

        if (maxLength < getColumnNumber() + 1) {
            breakLine();  
        }
        writeRaw(">");

    } 
    
    public static void main(String[] args) {
  
        if (args.length <= 0) {
          System.out.println("Usage: java nu.xom.samples.WrappingSerializer URL");
          return;
        }
        
        try {
          Builder parser = new Builder();
          Document doc = parser.build(args[0]);
          Serializer serializer = new WrappingSerializer(System.out, "ISO-8859-1");
          serializer.setIndent(4);
          serializer.setMaxLength(24);
          serializer.setPreserveBaseURI(true);
          serializer.write(doc);
          serializer.flush();
        }
        catch (ParsingException ex) {
          System.out.println(args[0] + " is not well-formed.");
          System.out.println(ex.getMessage());
        }
        catch (IOException ex) { 
          System.out.println(
           "Due to an IOException, the parser could not check " 
           + args[0]
          ); 
        }
  
    }

}
