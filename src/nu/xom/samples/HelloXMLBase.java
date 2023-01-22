/* Copyright 2002-2004 Elliotte Rusty Harold
   
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
import nu.xom.Comment;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

/**
 * 
 * <p>
 *   Demonstrates interaction of actual base URI with 
 *   <code>xml:base</code> attributes.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0
 *
 */
public class HelloXMLBase {

  public static void main(String[] args) {
   
    Document doc;
    String base1 = "http://www.base1.com";
    String base2 = "http://www.base2.com";
    String base3 = "base3.html";

    Element root = new Element("root");
    doc = new Document(root);
    doc.setBaseURI(base1);
    Element child = new Element("child");
    root.appendChild(child);
    child.setBaseURI(base2);
    child.appendChild(new Comment("here I am"));
    
    Element child2 = new Element("child2");
    root.appendChild(child2);
 
    Element child3 = new Element("child3");
    root.appendChild(child3);
    child3.addAttribute(new Attribute("xml:base", 
      "http://www.w3.org/XML/1998/namespace", base2));
 
    Element child4 = new Element("child4");
    root.appendChild(child4);
    child4.addAttribute(new Attribute("xml:base", 
      "http://www.w3.org/XML/1998/namespace", base3));
    
    try {
        Serializer serializer 
          = new Serializer(System.out, "ISO-8859-1");
        serializer.setPreserveBaseURI(true);
        serializer.write(doc);
        serializer.flush();
        serializer.setPreserveBaseURI(false);
        serializer.write(doc);
        serializer.flush();
    }
    catch (IOException ex) { 
        // shouldn't happen on System.out
        ex.printStackTrace();
    }
    
  }

}
