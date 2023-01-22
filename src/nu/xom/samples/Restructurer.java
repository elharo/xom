/* Copyright 2002-2004, 2018 Elliotte Rusty Harold
   
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

import nu.xom.Builder;
import nu.xom.Comment;
import nu.xom.Document;
import nu.xom.Node;
import nu.xom.ParentNode;
import nu.xom.ParsingException;
import nu.xom.ProcessingInstruction;

/**
 * <p>
 *   Demonstrates moving nodes from one part of the tree
 *   to a different part.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.3.0
 *
 */
public class Restructurer {

    
    // Since this methods only operates on its argument and 
    // does not interact with any fields in the class, it's 
    // plausibly made static.
    public static void processNode(Node current) {  
    
        if (current instanceof Comment 
          || current instanceof ProcessingInstruction) {       
            Document document = current.getDocument();
            ParentNode root = document.getRootElement();
            current.detach();
            document.insertChild(current, document.indexOf(root));      
        }
        else {
            for (int i = 0; i < current.getChildCount(); i++) {
                processNode(current.getChild(i));
            }
        }
    
    }

    
    public static void main(String[] args) {
     
        if (args.length <= 0) {
            System.out.println(
              "Usage: java nu.xom.samples.RestructureDriver URL"
            );
            return;
        }
        String url = args[0];
    
        try {
            // Find a parser
            Builder parser = new Builder();
      
            // Read the document
            Document document = parser.build(url); 
     
            // Modify the document
            Restructurer.processNode(document.getRootElement());
      
            // Write it out again
            System.out.println(document.toXML());
      
        }
        catch (ParsingException ex) {
            System.out.println(url + " is not well-formed.");
        }
        catch (IOException ex) { 
            System.out.println(
             "Due to an IOException, the parser could not read " + url
            );
        }
   
    }

}


