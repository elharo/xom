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

import java.io.IOException;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ParentNode;
import nu.xom.Text;
import nu.xom.ParseException;


/**
 * 
 * <p>
 * based on Sosnoski's benchmarks:
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
         
        TreeWalker iterator = new TreeWalker();
        try {
            
            long prebuild = System.currentTimeMillis();
            Builder parser = new Builder();
          
            // Read the entire document into memory
            Node document = parser.build(args[0]); 
            long postbuild = System.currentTimeMillis();
            
            System.out.println((postbuild - prebuild) + "ms to build the document");


            long prewalk = System.currentTimeMillis();
            
            // Process it starting at the root
            iterator.followNode(document);
            long postwalk = System.currentTimeMillis();
            
            System.out.println((postwalk - prewalk) + "ms to walk tree");
            
        }
        catch (IOException e) { 
          System.out.println(e); 
        }
        catch (ParseException e) { 
          System.out.println(e); 
        }
  
    } // end main

  // note use of recursion
    public void followNode(Node node) throws IOException {
    
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