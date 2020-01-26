/* Copyright 2003 Elliotte Rusty Harold
   
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
   elharo@ibiblio.org. Please include the word "XOM" in the
   subject line. The XOM home page is located at https://xom.nu/
*/

package nu.xom.samples;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import nu.xom.Builder;
import nu.xom.Comment;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.ProcessingInstruction;
import nu.xom.Serializer;
import nu.xom.Text;

/**
 * <p>
 *  This <code>Serializer</code> subclass outputs raw, unescaped text
 *  from the text nodes, but no markup of any kind. In essence, it
 *  converts XML into plain text.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0
 *
 */
public class TextSerializer extends Serializer {

    /**
     * <p>
     * Create a new text serializer that uses the UTF-8 encoding.
     * </p>
     * 
     * @param out the output stream to write the document on
     * 
     * @throws NullPointerException if out is null
     */
    public TextSerializer(OutputStream out) {
        super(out);
    }
    
    /**
     * <p>
     * Create a new serializer that uses a specified encoding.
     * The encoding must be recognized by the Java virtual machine.
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
    public TextSerializer(OutputStream out, String encoding)
      throws UnsupportedEncodingException {
        super(out, encoding);
    }
    
    protected void writeStartTag(Element element) {}
    protected void writeEmptyElementTag(Element element) {}
    protected void writeEndTag(Element element) {}
    protected void writeXMLDeclaration() {}
    protected void write(Comment comment) {}
    protected void write(ProcessingInstruction instruction) {}
    protected void write(DocType doctype) {}

    // Here we use writeRaw because we don't want characters like &
    // and < to be escaped. If they can't be written in the specified
    // encoding, an exception is thrown.
    protected void writeText(Text text) throws IOException {
        writeRaw(text.getValue()); 
    }

    public static void main(String[] args) {
      
        if (args.length <= 0) {
          System.out.println(
            "Usage: java nu.xom.samples.TextSerializer URL");
          return;
        }
        
        try {
            Builder parser = new Builder();
            Document doc = parser.build(args[0]);
            Serializer serializer = new TextSerializer(System.out);
            serializer.write(doc);
        }
        catch (ParsingException ex) {
            System.out.println(args[0] + " is not well-formed.");
            System.out.println(" at line " + ex.getLineNumber() 
              + ", column " + ex.getColumnNumber());
            System.out.println(ex.getMessage());
        }
        catch (IOException ex) { 
          System.out.println(
           "Due to an IOException, the serialization of " 
           + args[0] + " could not be completed."
          ); 
        }
      
    }

}
