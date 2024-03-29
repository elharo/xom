/* Copyright 2002, 2003, 2018 Elliotte Rusty Harold
   
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
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;


/**
 * <p>
 * Demonstrates walking the element hierarchy of 
 * an XML document.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.3.0
 *
 */
public class ElementLister {

  public static void main(String[] args) {
  
    if (args.length == 0) {
      System.out.println("Usage: java nu.xom.samples.ElementLister URL"); 
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
  
  
  public static void listChildren(Element current, int depth) {
   
    printSpaces(depth);
    System.out.println(current.getQualifiedName());
    for (Element child : current.getChildElements()) {
      listChildren(child, depth+1);
    }
    
  }
  
  private static void printSpaces(int n) {
    
    for (int i = 0; i < n; i++) {
      System.out.print(' '); 
    }
    
  }

}
