/* Copyright 2002-2004 Elliotte Rusty Harold
   
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
   subject line. The XOM home page is located at http://www.xom.nu/
*/

package nu.xom.samples;


import java.io.IOException;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ParsingException;


/**
 * 
 * <p>
 * Demonstrates recursive descent through a document
 * using the <code>Node</code> class.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0
 *
 */
public class NodeLister {

    
  public static void main(String[] args) {
  
    if (args.length == 0) {
      System.out.println("Usage: java nu.xom.samples.NodeLister URL");
      return;
    } 
      
    Builder builder = new Builder();
     
    try {
      Document doc = builder.build(args[0]);
      Element root = doc.getRootElement();
      listChildren(root, 0);      
    }
    // indicates a well-formedness error
    catch (ParsingException ex) { 
      System.out.println(args[0] + " is not well-formed.");
      System.out.println(ex.getMessage());
    }  
    catch (IOException ex) { 
      System.out.println(ex);
    }  
  
  }
  
  
  public static void listChildren(Node current, int depth) {
   
    printSpaces(depth);
    String name = "";
    if (current instanceof Element) {
        Element temp = (Element) current;
        name = ": " + temp.getQualifiedName();   
    }
    System.out.println(current.getClass().getName() + name);
    for (int i = 0; i < current.getChildCount(); i++) {
      listChildren(current.getChild(i), depth+1);
    }
    
  }
  
  
  private static void printSpaces(int n) {
    
    for (int i = 0; i < n; i++) {
      System.out.print(' '); 
    }
    
  }

}
