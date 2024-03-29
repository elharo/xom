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

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;


/**
 * 
 * <p>
 * Demonstrates recursive descent through a document,
 * and reading and setting of attributes on elements.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.3.0
 *
 */
public class IDTagger {

  private static int id = 1;

  public static void processElement(Element element) {
    
    if (element.getAttribute("ID") == null) {
      element.addAttribute(new Attribute("ID", "_" + id));
      id = id + 1; 
    }
    
    // recursion
    for (Element child : element.getChildElements()) {
      processElement(child);   
    }
    
  }

  public static void main(String[] args) {
     
    Builder builder = new Builder();
    
    for (int i = 0; i < args.length; i++) {
        
      try {
        // Read the entire document into memory
        Document document = builder.build(args[i]); 
       
        processElement(document.getRootElement());
        
        System.out.println(document.toXML());         
      }
      catch (ParsingException ex) {
        System.err.println(ex);
        continue; 
      }
      catch (IOException ex) {
        System.err.println(ex);
        continue; 
      }
      
    }
  
  } // end main

}