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

import nu.xom.Document;
import nu.xom.Element;

/**
 * 
 * <p>
 * Test what happens when an element with no namespace is
 * a child of an element in a default namespace
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0
 *
 */
public class HelloNamespaces {

    public static void main(String[] args) {
   
        Element root = new Element("root", "http://www.xom.nu");    
        root.appendChild(new Element("NoNamespace"));
        Document doc = new Document(root);
        System.out.println(doc.toXML());
    
    }

}
