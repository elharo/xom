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
   subject line. The XOM home page is temporarily located at
   http://www.cafeconleche.org/XOM/  but will eventually move
   to http://www.xom.nu/  */

package nu.xom.samples;

import java.io.IOException;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

/**
 * 
 * <p>
 * Test what happens when an element with no namespace is
 * a child of an element in a default namespace
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0b7
 *
 */
public class HelloNamespaces {

    public static void main(String[] args) {
   
        Element root = new Element("root", "http://www.xom.nu");    
        root.appendChild(new Element("NoNamespace"));
        Document doc = new Document(root);
    
        try {
            Serializer serializer = new Serializer(System.out, "ISO-8859-1");
            serializer.write(doc);
            serializer.flush();
        }
        catch (IOException ex) { 
            System.out.println(
              "Due to an IOException, the parser could not check " 
              + args[0]
            ); 
        }
    
    }

}