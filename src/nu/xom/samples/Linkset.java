// Copyright 2002, 2003 Elliotte Rusty Harold
// 
// This library is free software; you can redistribute 
// it and/or modify it under the terms of version 2.1 of 
// the GNU Lesser General Public License as published by  
// the Free Software Foundation.
// 
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
// GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General 
// Public License along with this library; if not, write to the 
// Free Software Foundation, Inc., 59 Temple Place, Suite 330, 
// Boston, MA  02111-1307  USA
// 
// You can contact Elliotte Rusty Harold by sending e-mail to
// elharo@metalab.unc.edu. Please include the word "XOM" in the
// subject line. The XOM home page is temporarily located at
// http://www.cafeconleche.org/XOM/  but will eventually move
// to http://www.xom.nu/

package nu.xom.samples;


import java.io.IOException;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;


/**
 * 
 * <p>
 * Demonstrates extracting elements from one document
 * and inserting them into another document.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d18
 *
 */
public class Linkset {
  
  public static void main(String[] args) {
    
    String url = "http://www.slashdot.org/slashdot.rdf";
    
    try {
      Builder parser = new Builder();
      
      // Parse the document
      Document document = parser.build(url); 
      Element oldRoot = document.getRootElement();
      Element newRoot = new Element("linkset");
      Elements toplevel = oldRoot.getChildElements();
      for (int i = 0; i < toplevel.size(); i++) {
        Element element = toplevel.get(i); 
        Element link = element.getFirstChildElement("link", 
          "http://my.netscape.com/rdf/simple/0.9/");
        link.detach();
        newRoot.appendChild(link);
      }
      System.out.println(newRoot.toXML());
    }
    catch (ParsingException e) {
      System.out.println(url + " is not well-formed.");
    }
    catch (IOException e) { 
      System.out.println(
       "Due to an IOException, the parser could not read " + url
      ); 
    }
     
  } // end main

}

