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
   elharo@metalab.unc.edu. Please include the word "XOM" in the
   subject line. The XOM home page is located at http://www.xom.nu/
*/

package nu.xom.samples;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nu.xom.Element;


/**
 * 
 * <p>
 * Demonstrates building a structured XML document,
 * from flat, tabular data. A different version of this 
 * example was originally developed for Chapter 4 of 
 * <cite><a 
 * href="http://www.cafeconleche.org/books/xmljava/">Processing 
 * XML with Java</a></cite>.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0
 *
 */
public class Bureau {
 
  // Agency code plus bureau code uniquely identify a bureau  
  // Bureau code alone is definitely not sufficient
  private String code;
  private String name;
  private String year;
  private String agencyCode;
  
  private List   accounts = new ArrayList();
  
  private static Map instances = new HashMap();

  // Use a private constructor so instantiators 
  // have to use the factory method
  private Bureau(String name, String code, String agencyCode, 
    String year) {
        
    this.name = name;
    this.code = code;
    this.agencyCode = agencyCode;
    this.year = year;
    
  }
  
  public static Bureau getInstance(String name, String code, 
   String agencyCode, String year) {
        
    String key = agencyCode+" "+code+" "+year;
    Bureau bureau = (Bureau) instances.get(key);
    if (bureau == null) {
      bureau = new Bureau(name, code, agencyCode, year);
      instances.put(key, bureau);
    }
    
    return bureau;
        
  }
  
  public void add(Account account) {
    if (!accounts.contains(account)) accounts.add(account);     
  }
  
  public Element getXML() {
        
    Element bureau = new Element("Bureau");
    Element name = new Element("Name");
    Element code = new Element("Code");

    name.appendChild(this.name);
    code.appendChild(this.code);
    bureau.appendChild(name);
    bureau.appendChild(code);
    
    Iterator iterator = accounts.iterator();
    while (iterator.hasNext()) {
      Account account = (Account) iterator.next();
      bureau.appendChild(account.getXML());
    }
    return bureau;
    
  }
          
}