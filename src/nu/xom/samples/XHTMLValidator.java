/* Copyright 2002, 2003 Elliotte Rusty Harold
   
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

package nu.xom.samples;

import java.io.IOException;

import nu.xom.Builder;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;

/**
 * <p>
 *   Demonstrates the use of the <code>DocType</code> class
 *   by validating XHTML.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0
 *
 */
public class XHTMLValidator {

  public static void main(String[] args) {
    
    for (int i = 0; i < args.length; i++) {
      validate(args[i]);
    }   
    
  }

  private static Builder builder = new Builder(true);
                         /* turn on validation ^^^^ */
  
  // not thread safe
  public static void validate(String source) {
        
      Document document;
      try {
        document = builder.build(source); 
      }
      catch (ParsingException ex) {  
        System.out.println(source 
         + " is invalid XML, and thus not XHTML."); 
        return; 
      }
      catch (IOException ex) {  
        System.out.println("Could not read: " + source); 
        return; 
      }
      
      // If we get this far, then the document is valid XML.
      // Check to see whether the document is actually XHTML 
      boolean valid = true;       
      DocType doctype = document.getDocType();
    
      if (doctype == null) {
        System.out.println("No DOCTYPE");
        valid = false;
      }
      else {
        // verify the DOCTYPE
        String name     = doctype.getRootElementName();
        String publicID = doctype.getPublicID();
      
        if (!name.equals("html")) {
          System.out.println(
           "Incorrect root element name " + name);
          valid = false;
        }
    
        if (publicID == null
         || (!publicID.equals("-//W3C//DTD XHTML 1.0 Strict//EN")
           && !publicID.equals(
            "-//W3C//DTD XHTML 1.0 Transitional//EN")
           && !publicID.equals(
            "-//W3C//DTD XHTML 1.0 Frameset//EN"))) {
          valid = false;
          System.out.println(source 
           + " does not seem to use an XHTML 1.0 DTD");
        }
      }
    
    
      // Check the namespace on the root element
      Element root = document.getRootElement();
      String uri = root.getNamespaceURI();
      String prefix = root.getNamespacePrefix();
      if (!uri.equals("http://www.w3.org/1999/xhtml")) {
        valid = false;
        System.out.println(source 
         + " does not properly declare the"
         + " http://www.w3.org/1999/xhtml namespace"
         + " on the root element");        
      }
      if (!prefix.equals("")) {
        valid = false;
        System.out.println(source 
         + " does not use the empty prefix for XHTML");        
      }
      
      if (valid) System.out.println(source + " is valid XHTML.");
    
  }

}
