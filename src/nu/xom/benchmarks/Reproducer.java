/* Copyright 2002-2006 Elliotte Rusty Harold
   
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

package nu.xom.benchmarks;

import java.io.IOException;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Comment;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ParsingException;
import nu.xom.ProcessingInstruction;
import nu.xom.Text;

/**
 * 
 * <p>
 * Benchmarks building a tree in memory by copying an existing document
 * without using copy. Thus everything is reverified so that 
 * constructors and <code>Verifier</code> are hit heavily. 
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.2d1
 *
 */
class Reproducer {

    public static void main(String[] args) {
     
        if (args.length <= 0) {
          System.out.println(
            "Usage: java nu.xom.benchmarks.Reproducer URL"
          );
          return; 
        }
         
        Reproducer iterator = new Reproducer();
        Builder parser = new Builder();
        try {
            Document document = parser.build(args[0]);

            // warmup Hotspot
            bench(iterator, document);
            
            long ms = 0;
            int repeat = 100;
            for (int i = 1; i < repeat; i++) {
                ms += bench(iterator, document);
            }
            System.out.println(ms/(double) repeat + "ms to build tree on average");

        }
        catch (IOException ex) { 
          System.out.println(ex); 
        }
        catch (ParsingException ex) { 
          System.out.println(ex); 
        }
  
    }

    private static long bench(Reproducer iterator, Document document) throws IOException {

        long prewalk = System.currentTimeMillis();         
        // Process it starting at the root
        iterator.copy(document);
        long postwalk = System.currentTimeMillis();
        return postwalk - prewalk;
    }

    private Document copy(Document doc)
      throws IOException {

        Element originalRoot = doc.getRootElement();
        Element root = copy(originalRoot);
        Document copy = new Document(root);
        copy.setBaseURI(doc.getBaseURI());
        for (int i = 0; i < doc.getChildCount(); i++) {
            Node child = doc.getChild(i);
            if (child == originalRoot) continue;
            Node node = copy(child);
            copy.insertChild(node, i);
        }
        return copy;
        
    }

    
    private Element copy(Element original) {

        Element copy = new Element(original.getQualifiedName(), 
          original.getNamespaceURI());
        for (int i = original.getAttributeCount()-1; i >= 0; i--) {
            Attribute att = original.getAttribute(i);
            copy.addAttribute(copy(att));
        }
        // Weird; need to find just the additional namespaces????
        /* for (int i = original.getNamespaceDeclarationCount()-1; i >= 0; i--) {
             copy.addNamespaceDeclaration(original.);
        } */
        for (int i = 0; i < original.getChildCount(); i++) {
            Node node = copy(original.getChild(i));
            copy.appendChild(node);
        }
        return copy;
        
    }

    
    private Node copy(Node node) {

        if (node instanceof Text) {
            return copy((Text) node);
        }
        else if (node instanceof Element) {
            return copy((Element) node);
        }
        else if (node instanceof Comment) {
            return copy((Comment) node);
        }
        else if (node instanceof ProcessingInstruction) {
            return copy((ProcessingInstruction) node);
        }
        else if (node instanceof DocType) {
            return copy((DocType) node);
        }
        return null;
        
    }

    
    private Node copy(Text text) {
        return new Text(text.getValue());
    }

    
    private Node copy(Comment comment) {
        return new Comment(comment.getValue());
    }

    
    private Node copy(ProcessingInstruction pi) {
        return new ProcessingInstruction(pi.getTarget(), pi.getValue());
    }

    
    private Node copy(DocType doctype) {
        return new DocType(
          doctype.getRootElementName(), 
          doctype.getPublicID(), 
          doctype.getSystemID());
    }

    
    private Attribute copy(Attribute original) {
        
        Attribute copy = new Attribute(original.getQualifiedName(), 
          original.getNamespaceURI(), 
          original.getValue(), 
          original.getType());
        return copy;
        
    }


}