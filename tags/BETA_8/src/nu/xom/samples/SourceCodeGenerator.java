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
   elharo@metalab.unc.edu. Please include the word "XOM" in the
   subject line. The XOM home page is temporarily located at
   http://www.cafeconleche.org/XOM/  but will eventually move
   to http://www.xom.nu/  */

package nu.xom.samples;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Comment;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ProcessingInstruction;
import nu.xom.Text;

/**
 * 
 * <p>
 *   This program essentially serializes a XOM <code>Node</code>
 *   object into the Java statements necessary to build the 
 *   <code>Node</code> using XOM. This may be useful for building
 *   unit tests. 
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d22
 *
 */
public class SourceCodeGenerator {

    /**
      * <p>
      * The driver method for the SourceCodeGenerator program.
      * </p>
      *
      * @param args <code>args[0]</code> contains the URL 
      *     of the document to be processed. 
      */
    public static void main(String[] args) {
  
        Builder builder = new Builder();
        try {
            Document input = builder.build(args[0]);
            generateClass(input);
        }
        catch (Exception ex) {
            System.err.println(ex);
            ex.printStackTrace();
        }
  
    }
    
    private static int elementCount = 1;
    
    public static void generateClass(Document doc) {
        System.out.println("import nu.xom.*;");
        System.out.println();
        System.out.println();
        System.out.println("public class CodeMaker {");
        System.out.println();
        System.out.println("  public static void main(String[] args) throws Exception {");
        generateDoc(doc);
        System.out.println("    Serializer serializer = new Serializer(System.out);");
        System.out.println("    serializer.write(doc);");
        System.out.println("  }");
        System.out.println();
        System.out.println("}");
    }
    
    public static void generateSource(Node node, String parent) {
        
        if (node instanceof Element) {
            Element element = (Element) node;
            String name = "element" + elementCount;
            System.out.println("    Element " + name + " = " 
             + "new Element(\"" + element.getQualifiedName()
             + "\", \"" + element.getNamespaceURI() + "\");");
            if ("doc".equals(parent)) {
                System.out.println("    doc.setRootElement(" + name + ");");
            }
            else {
                System.out.println("    " + parent + ".appendChild(" + name + ");");
            }  
            
            for (int i = 0; i < element.getAttributeCount(); i++) {
                Attribute a = element.getAttribute(i);
                System.out.println("    " + name + ".addAttribute(new Attribute(\""  
                  + a.getQualifiedName() + "\", \"" 
                  + a.getNamespaceURI() + "\", \""
                  + a.getValue() + "\"));");
            }
              
            for (int i = 0; i < element.getNamespaceDeclarationCount(); i++) {
                String prefix = element.getNamespacePrefix(i);
                System.out.println("    " + name + ".addNamespaceDeclaration(\""  
                  + prefix + "\", \"" 
                  + element.getNamespaceURI(prefix) + "\");");
            }
            
            elementCount++;
            for (int i = 0; i < element.getChildCount(); i++) {
                generateSource(element.getChild(i), name);
            }
              
             
        }
        else if (node instanceof ProcessingInstruction) {
            ProcessingInstruction pi = (ProcessingInstruction) node;
            System.out.println("    pi = new ProcessingInstruction(\"" +
              pi.getTarget() + "\", \"" + javaEscape(pi.getValue()) + "\");"); 
            System.out.println("    " + parent + ".appendChild(pi);");    
        }
        else if (node instanceof Comment) {
            Comment comment = (Comment) node;
            System.out.println("    comment = new Comment(\""
             + javaEscape(comment.getValue()) + "\");"); 
            System.out.println("    " + parent + ".appendChild(comment);");    
        }
        else if (node instanceof Text) {
            Text text = (Text) node;
            System.out.println("    text = new Text(\""
             + javaEscape(text.getValue()) + "\");"); 
            System.out.println("    " + parent + ".appendChild(text);");    
        }
        else if (node instanceof DocType) {
            DocType doctype = (DocType) node;
            String publicID = doctype.getPublicID();
            String systemID = doctype.getSystemID();
            System.out.println("    DocType doctype = new DocType(\""
                + doctype.getRootElementName() + "\");"); 
            if (systemID != null) {
                System.out.println("    doctype.setSystemID(\""
                 + systemID + "\");"); 
            }
            if (publicID != null) {
                System.out.println("    doctype.setPublicID(\""
                 + publicID + "\");"); 
            }
            System.out.println("    doc.setDocType(doctype);"); 
            
          
        }
        
    }
    
    private static String javaEscape(String text) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
                case '"':
                    result.append("\\\"");   
                    break;
                case '\n':
                    result.append("\\n");   
                    break;
                case '\r':
                    result.append("\\r");   
                    break;
                case '\t':
                    result.append("\\t");   
                    break;
                case '\\':
                    result.append("\\\\");   
                    break;
                default:
                    result.append(c);
            }
            
        }
        return result.toString();  
    }

    public static void generateDoc(Document doc) {
        
        System.out.println("    Comment comment;");   
        System.out.println("    Text text;");   
        System.out.println("    ProcessingInstruction pi;"); 
        System.out.println("    Document doc = new Document(new Element(\"root\"));");   
        for (int i = 0; i < doc.getChildCount(); i++) {
            generateSource(doc.getChild(i), "doc");   
        }
        
    }

}
