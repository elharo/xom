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
 * <p>
 *   Demonstrates the removal of elements and
 *   their content from a document.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.3.0
 *
 */
public class XHTMLPurifier {

     public final static String XHTML_NAMESPACE 
       = "http://www.w3.org/1999/xhtml";

    public static void main(String[] args) {

        if (args.length <= 0) {
          System.out.println("Usage: java nu.xom.samples.XHTMLPurifier URL");
          return;
        }
        
        try {
            Builder parser = new Builder();
            Document doc = parser.build(args[0]);
            Element root = doc.getRootElement();
            if (root.getNamespaceURI().equals(XHTML_NAMESPACE)) {
                strip(root);
            }
            else {
                System.out.println(args[0] 
                  + " does not appear to be an XHTML document");
                return;   
            }
          

            System.out.println(doc.toXML());
        }
        catch (ParsingException ex) {
          System.out.println(args[0] + " is not well-formed.");
          System.out.println(ex.getMessage());
        }
        catch (IOException ex) { 
          System.out.println(
           "Due to an IOException, the parser could not read " 
           + args[0]
          ); 
        }      
        
    }
    
    public static void strip(Element element) {
        
       if (element.getNamespaceURI().equals(XHTML_NAMESPACE)) {
            
            // Strip out non XHTML attributes
            for (int i = 0; i < element.getAttributeCount(); i++) {
               Attribute attribute = element.getAttribute(i);
               
               if (!"".equals(attribute.getNamespaceURI())) {
                  if (!"xml".equals(attribute.getNamespacePrefix())) {
                       attribute.detach();
                  }
               } 
            }
            
            // Strip out additional namespaces
            for (int i = 0; i < element.getNamespaceDeclarationCount(); i++) {
                String prefix = element.getNamespacePrefix(i);
                element.removeNamespaceDeclaration(prefix);  
            }
            
            for (Element child : element.getChildElements()) {
                strip(child);
            }     
            
        }
        else {
            element.detach();     
        }       
        
    } 
    
}