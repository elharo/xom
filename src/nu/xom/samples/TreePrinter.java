/* Copyright 2002, 2003, 2018 Elliotte Rusty Harold
   
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

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;


/**
 * 
 * <p>
 * Demonstrates reading the names, namespaces, and
 * attributes of an element.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.3.0
 *
 */
public class TreePrinter {

  public static void main(String[] args) {
  
    if (args.length == 0) {
      System.out.println("Usage: java nu.xom.samples.TreePrinter URL"); 
      return;
    } 
      
    Builder builder = new Builder();
     
    try {
      Document doc = builder.build(args[0]);
      Element root = doc.getRootElement();
      listChildren(root);      
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
  

  // Print the properties of each element
  public static void inspect(Element element) {
    
    if (element.getParent() != null) {
      // Print a blank line to separate it from the previous
      // element.
      System.out.println(); 
    }
    
    String qualifiedName = element.getQualifiedName();
    System.out.println(qualifiedName + ":");
    
    String namespace = element.getNamespaceURI();
    if (!namespace.equals("")) {
      String localName = element.getLocalName();
      String uri = element.getNamespaceURI();
      String prefix = element.getNamespacePrefix();
      System.out.println("  Local name: " + localName);
      System.out.println("  Namespace URI: " + uri);
      if (!"".equals(prefix)) {
        System.out.println("  Namespace prefix: " + prefix);
      }
    }
    for (int i = 0; i < element.getAttributeCount(); i++) {
        Attribute attribute = element.getAttribute(i);
        String name = attribute.getQualifiedName();
        String value = attribute.getValue();
        System.out.println("  " + name + "=\"" + value + "\""); 
    }
    
    for (int i = 0; i < element.getNamespaceDeclarationCount(); i++) {
        String additional = element.getNamespacePrefix(i);
        String uri = element.getNamespaceURI(additional);
        System.out.println(
          "  xmlns:" + additional + "=\"" + uri + "\""); 
    }
    
  }
  
  public static void listChildren(Element current) {
   
    inspect(current);
    for (Element child : current.getChildElements()) {
      listChildren(child);
    }
    
  }

}
