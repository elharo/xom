/* Copyright 2002-2004, 2019 Elliotte Rusty Harold
   
   This library is free software; you can redistribute it and/or modify
   it under the terms of version 2.1 of the GNU Lesser General Public 
   License as published by the Free Software Foundation.
   
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
   GNU Lesser General Public License for more details.
   
   You should have received a copy of the GNU Lesser General Public
   License along with this library. If not, see
   <https://www.gnu.org/licenses/>.
   
   You can contact Elliotte Rusty Harold by sending e-mail to
   elharo@ibiblio.org. Please include the word "XOM" in the
   subject line. The XOM home page is located at https://xom.nu/
*/

package nu.xom.samples;

import java.io.IOException;
import java.util.Stack;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.NodeFactory;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.Serializer;

/**
 * <p>
 *   Demonstrates a custom <code>NodeFactory</code> that strips out 
 *   all non-XHTML elements.  It’s easy enough to drop out any elements 
 *   that are not in the XHTML namespace. However, in the case of SVG, 
 *   MathML and most other applications you’ll want to remove the  
 *   content of these elements as well. I’ll assume that the namespace 
 *   for text is the same as the namespace of the parent element. 
 *   (This is not at all clear from the namespaces specification, 
 *   but it makes sense in many cases.) To track the nearest namespace 
 *   for non-elements, makeElement() will push the element’s namespace 
 *   onto a stack and endElement() will pop it off. Peeking at the top 
 *   of the stack tells you what namespace the nearest element uses.
 *   This is modeled after Example 8-9 in
 *   <cite>Processing XML with Java</cite>.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.3.1
 * 
 */

public class StreamingXHTMLPurifier extends NodeFactory {

    private Stack<String> namespaces = new Stack<String>(); 
    private Nodes empty = new Nodes();   
    public final static String XHTML_NAMESPACE 
      = "http://www.w3.org/1999/xhtml";

    // We need text nodes only inside XHTML    
    public Nodes makeText(String data) {
        if (inXHTML()) return super.makeText(data);
        return empty;  
    } 

    public Nodes makeComment(String data) {
        if (inXHTML()) return super.makeComment(data);
        return empty;  
    }    

    
    private boolean inXHTML() {
        if (namespaces.isEmpty()) return true; // document prolog
        String currentNamespace = (String) (namespaces.peek());
        if (XHTML_NAMESPACE.equals(currentNamespace)) return true;
        return false;
    }   

    public Element startMakingElement(String name, String namespace) {
        
        namespaces.push(namespace);
        if (XHTML_NAMESPACE.equals(namespace)) {
            return super.startMakingElement(name, namespace);   
        }
        return null;
    }
    
    public Nodes finishMakingElement(Element element) {
        namespaces.pop(); 
        int namespaceCount = element.getNamespaceDeclarationCount();
        for (int i = 0; i < namespaceCount; i++) {
            String prefix = element.getNamespacePrefix(i);
            element.removeNamespaceDeclaration(prefix);
            if (element.getNamespaceDeclarationCount() < namespaceCount) {
                i--;
                namespaceCount--;   
            }
        }
        return new Nodes(element);      
    }

    public Nodes makeDocType(String rootElementName, 
      String publicID, String systemID) {
        return new Nodes(new DocType("html", 
          "PUBLIC \"-//W3C//DTD XHTML Basic 1.0//EN\"",
          "http://www.w3.org/TR/xhtml-basic/xhtml-basic10.dtd"));    
    }

    public Nodes makeProcessingInstruction(
      String target, String data) {
        if (inXHTML()) {
            return super.makeProcessingInstruction(target, data);   
        }
        return empty; 
    }  

    public Nodes makeAttribute(String name, String URI, 
      String value, Attribute.Type type) {
        if ("".equals(URI) 
          || "http://www.w3.org/XML/1998/namespace".equals(URI)) {
            return super.makeAttribute(name, URI, value, type);
        }
        return empty;
    }

    public static void main(String[] args) {
  
        if (args.length == 0) {
            System.out.println(
              "Usage: java nu.xom.samples.StreamingXHTMLPurifier URL"
            ); 
            return;
        } 
      
        StreamingXHTMLPurifier factory = new StreamingXHTMLPurifier();
        Builder builder = new Builder(factory);
     
        try {
            Document doc = builder.build(args[0]);
            Serializer serializer = new Serializer(System.out);
            serializer.write(doc);
        }
        // indicates a well-formedness error
        catch (ParsingException ex) { 
            System.out.println(args[0] + " is not well-formed.");
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }  
        catch (IOException ex) { 
            System.out.println(ex);
        }  
  
    }

}
