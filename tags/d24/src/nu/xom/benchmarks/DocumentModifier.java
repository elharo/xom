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

package nu.xom.benchmarks;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ParentNode;
import nu.xom.Serializer;
import nu.xom.Text;
import nu.xom.ParsingException;


/**
 * 
 * <p>
 * Based on Dennis Sosnoski's benchmarks:
 * </p>
 * 
 * <blockquote>
 * This test looks at the time required to systematically 
 * modify the constructed document representation. It walks 
 * the representation, deleting all isolated whitespace content
 * and wrapping each non-whitespace content string with a new,
 * added, element. It also adds an attribute to each element of
 * the original document that contained non-whitespace content. 
 * This test is intended to represent the performance of the 
 * document models across a range of modifications to the 
 * documents. As with the walk times, the modify times are 
 * considerably faster than the parse times. As a result, 
 * the parse times are going to be more important for applications
 * that make only a single pass through each parsed document.
 * </blockquote>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d23
 *
 */
class DocumentModifier {

    public static void main(String[] args) {
     
        if (args.length <= 0) {
          System.out.println(
            "Usage: java nu.xom.benchmarks.DocumentModifier URL"
          );
          return; 
        }
         
        DocumentModifier iterator = new DocumentModifier();
        Builder parser = new Builder();
        try {    
            // Separate out the basic I/O by parsing document,
            // and then reserializing into a byte array.
            // This caches the and removes any dependence on the DTD.
            Document doc = parser.build(args[0]);
            DocType type = doc.getDocType();
            if (type != null) {
                doc.removeChild(type);   
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Serializer serializer = new Serializer(out);
            serializer.write(doc);
            serializer.flush();
            out.close();
            byte[] data = out.toByteArray();
             
            warmup(parser, iterator, data, 5);
            InputStream raw = new BufferedInputStream(
              new ByteArrayInputStream(data)
            );    
            
            // Try to avoid garrbage collection pauses     
            System.gc(); System.gc(); System.gc();
            
            long prebuild = System.currentTimeMillis();
          
            // Read the entire document into memory
            Document document = parser.build(raw); 
            long postbuild = System.currentTimeMillis();
            
            System.out.println((postbuild - prebuild) 
              + "ms to build the document");

            long prewalk = System.currentTimeMillis();
            performTask(iterator, document);
            long postwalk = System.currentTimeMillis();
            
            System.out.println((postwalk - prewalk) 
              + "ms to modify the document");
            
        }
        catch (IOException ex) { 
            System.out.println(ex); 
        }
        catch (ParsingException ex) { 
            System.out.println(ex); 
        }
  
    } // end main
    
    private static void warmup(Builder builder, 
      DocumentModifier iterator, byte[] data, int numPasses)
      throws IOException, ParsingException {
          
        InputStream in = new BufferedInputStream(
          new ByteArrayInputStream(data));
        Document doc = builder.build(in);  
        for (int i = 0; i < numPasses; i++) {
            performTask(iterator, doc);
        }
    }

    private static void performTask(DocumentModifier iterator, Document document)
      throws IOException { 
        iterator.followNode(document); 
    }

    // note use of recursion
    public void followNode(Node node) throws IOException {
    
        // Chances are most of the time is spent in the instanceof test
        if (node instanceof Text) {
            if (node.getValue().trim().length() == 0) {
                node.detach();
            }
            else {
                Element dummy = new Element("dummy");
                ParentNode parent = node.getParent();
                parent.insertChild(dummy, parent.indexOf(node));
                node.detach();
                dummy.appendChild(node);
            }
            return;
        }
        else if (node instanceof Element){
            Element element = (Element) node;
            element.addAttribute(new Attribute("class", "original"));
            for (int i = 0; i < node.getChildCount(); i++) {
                followNode(node.getChild(i));
            }
        }
        else {
            for (int i = 0; i < node.getChildCount(); i++) {
                followNode(node.getChild(i));
            }
        }
    
    }

}