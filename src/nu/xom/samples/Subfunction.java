/* Copyright 2002, 2003 Elliotte Rusty Harold
   
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

import nu.xom.Element;


/**
 * 
 * <p>
 * Demonstrates building a structured XML document,
 * from flat, tabular data. A different version of this 
 * example was originally developed for Chapter 4 of 
 * <cite><a  target="_top"
 * href="https://www.cafeconleche.org/books/xmljava/">Processing 
 * XML with Java</a></cite>.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0
 *
 */
public class Subfunction {
 
  private String code;
  private String title;
  private long   amount;
    
  public Subfunction(String title, String code, long amount) {
        
    this.title  = title;
    this.code   = code;
    this.amount = amount;
    
  }
  
  public Element getXML() {
        
    Element subfunction = new Element("Subfunction");
    Element name = new Element("Name");
    Element code = new Element("Code");
    Element amount = new Element("Amount");
    name.appendChild(this.title);
    code.appendChild(this.code);
    amount.appendChild(String.valueOf(this.amount));
    return subfunction;
    
  } 
               
}