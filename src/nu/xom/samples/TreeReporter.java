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

package nu.xom.samples;

import java.io.IOException;

import nu.xom.Builder;
import nu.xom.Node;
import nu.xom.ParsingException;


/**
 * 
 * <p>
 * Demonstrates recursive descent through a document.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d22
 *
 */
public class TreeReporter {

    public static void main(String[] args) {
     
        if (args.length <= 0) {
          System.out.println(
            "Usage: java nu.xom.samples.TreeReporter URL"
          );
          return; 
        }
         
        TreeReporter iterator = new TreeReporter();
        try {
          Builder parser = new Builder();
          
          // Read the entire document into memory
          Node document = parser.build(args[0]); 
          
          // Process it starting at the root
          iterator.followNode(document);
    
        }
        catch (IOException ex) { 
          System.out.println(ex); 
        }
        catch (ParsingException ex) { 
          System.out.println(ex); 
        }
  
    } // end main

    private PropertyPrinter printer = new PropertyPrinter();
  
  // note use of recursion
    public void followNode(Node node) throws IOException {
    
        printer.writeNode(node);
        for (int i = 0; i < node.getChildCount(); i++) {
            followNode(node.getChild(i));
        }
    
  }

}