/* Copyright 2004, 2019 Elliotte Rusty Harold
   
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
import java.util.Stack;

import nu.xom.Attribute;
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
 *   This class converts an XML document into XOM source code that
 *   creates the same XML document. It's often useful for
 *   building self-contained unit tests.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.3.1
 *
 */
public class SourceCodeSerializer extends Serializer {

    public SourceCodeSerializer(OutputStream out) {
        super(out);
    }

    public SourceCodeSerializer(OutputStream out, String encoding) 
      throws UnsupportedEncodingException {
        super(out, encoding);
    }
    
    
    private Stack<String> parents = new Stack<String>();

    
    public void write(Document doc) throws IOException {
        parents.push("doc");
        Element root = doc.getRootElement();
                
        write(root);
        
        // prolog
        for (int i = 0; i < doc.indexOf(root); i++) {
            writeChild(doc.getChild(i)); 
        }  
        
        //epilog
        for (int i = doc.indexOf(root) + 1; i < doc.getChildCount(); i++) {
            writeChild(doc.getChild(i)); 
        }       
        
        flush();
        
    }
    
    private int count = 1;
    
    protected void writeStartTag(Element element) 
      throws IOException {
        
        String name = "e" + count;
        writeRaw("Element " + name + " = new Element(\"" 
           + element.getQualifiedName() + "\", \"" + element.getNamespaceURI() + "\");");
        breakLine();
        if (count == 1) {
            writeRaw("Document doc = new Document(e1);");
        }
        else {
            writeRaw(parents.peek() + ".appendChild(" + name + ");");
        }
        breakLine();
        parents.push(name);
        writeAttributes(element);           
        writeNamespaceDeclarations(element);
        count++;
        
    }

    protected void writeEndTag(Element element) throws IOException {
        parents.pop();
    }


    protected void writeEmptyElementTag(Element element) throws IOException {
        writeStartTag(element);
        writeEndTag(element);
    }

    protected void writeAttributes(Element element)
      throws IOException {
        
        for (int i = 0; i < element.getAttributeCount(); i++) {
            Attribute attribute = element.getAttribute(i);
            write(attribute);
        }  
        
    }
    

    protected void write(Attribute attribute) throws IOException {
        
        String parent = (String) parents.peek();
        writeRaw(parent + ".addAttribute(new Attribute(\"" + attribute.getQualifiedName() + "\", "
          + "\"" + attribute.getNamespaceURI() + "\", \"" 
          + escapeText(attribute.getValue()) + "\"));");
        breakLine(); 
        
    }
    
    private static String escapeText(String s) {
    	StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\n': 
                    sb.append("\\n");
                    break;
                case '\r': 
                    sb.append("\\r");
                    break;
                case '"': 
                    sb.append("\\\"");
                    break;
                case '\t': 
                    sb.append("\\t");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }


    protected void writeNamespaceDeclarations(Element element)
      throws IOException {
        // We need to write only the additional namespace declarations
        prefix: for (int i = 0; i < element.getNamespaceDeclarationCount(); i++) {
            String prefix = element.getNamespacePrefix(i);
            if (prefix.equals(element.getNamespacePrefix())) continue;
            for (int a = 0; a < element.getAttributeCount(); a++) {
                if (prefix.equals(element.getAttribute(a).getNamespacePrefix())) {
                    continue prefix;
                }
            }
            String parent = (String) parents.peek();
            writeRaw(parent + ".addNamespaceDeclaration(\"" +
              prefix + "\", \"" + element.getNamespaceURI(prefix) + "\");");
            breakLine();
        }

    }


    protected void write(ProcessingInstruction instruction) 
      throws IOException {
        String parent = (String) parents.peek(); 
        if (parent.equals("doc")) {
            Document doc = instruction.getDocument();
            int root = doc.indexOf(instruction);
            writeRaw(parent + ".insertChild(new ProcessingInstruction(\"" + instruction.getTarget() 
                + "\", \"" + escapeText(instruction.getValue()) + "\"), " + root + ");");            
        }
        else {
            writeRaw(parent 
                + ".appendChild(new ProcessingInstruction(\"" 
                + instruction.getTarget() 
                + "\", \"" + escapeText(instruction.getValue()) 
                + "\"));");
        }
        breakLine();
    }


    protected void write(DocType doctype) throws IOException {
        writeRaw("DocType doctype = new DocType(\"" 
                + doctype.getRootElementName() + "\", \""
                + doctype.getPublicID() + "\", \"" 
                + doctype.getSystemID() + 
                        "\");");
        breakLine();
        Document doc = doctype.getDocument();
        int root = doc.indexOf(doc.getRootElement());
        writeRaw("doc.insertChild(doctype, " + root + ");");
        breakLine();

    } 
    
    
    protected void write(Comment comment) throws IOException {
        String parent = (String) parents.peek();
        if (parent.equals("doc")) {
            Document doc = comment.getDocument();
            int root = doc.indexOf(comment);
            writeRaw(parent + ".insertChild(new Comment(\"" 
              + escapeText(comment.getValue()) + "\"), " 
              + root + ");");            
        }
        else {
            writeRaw(parent + ".appendChild(new Comment(\"" 
              + escapeText(comment.getValue()) + "\");");
        }
        breakLine();
    }
    
    
    protected void write(Text text) throws IOException {
        String parent = (String) parents.peek(); 
        writeRaw(parent + ".appendChild(new Text(\"" + escapeText(text.getValue()) + "\"));");
        breakLine();
    }
    
    
    public static void main(String[] args) {
  
        if (args.length <= 0) {
          System.out.println("Usage: java nu.xom.samples.SourceCodeSerializer URL");
          return;
        }
        
        try {
          Builder parser = new Builder();
          Document doc = parser.build(args[0]);
          Serializer serializer = new SourceCodeSerializer(System.out, "ISO-8859-1");
          serializer.write(doc);
          serializer.flush();
        }
        catch (ParsingException ex) {
          System.out.println(args[0] + " is not well-formed.");
          System.out.println(ex.getMessage());
        }
        catch (IOException ex) { 
          System.out.println(
           "Due to an IOException, the parser could not read " 
           + args[0]
          ); 
        }
  
    }

}
