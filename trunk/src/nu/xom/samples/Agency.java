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
 * <cite><a  target="_top"
 * href="http://www.cafeconleche.org/books/xmljava/">Processing 
 * XML with Java</a></cite>.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0
 *
 */
public class Agency {
 
  private String code;
  private String name;
  private String treasuryCode;
  private String year;
  
  private List   bureaus = new ArrayList();
  
  private static Map instances = new HashMap();

  // A private constructor so instantiators 
  // have to use the factory method
  private Agency(String name, String code, String treasuryCode, 
     String year) {
        
    this.name = name;
    this.code = code;
    this.treasuryCode = treasuryCode;
    this.year = year;
    
  }
  
  public static Agency getInstance(String name, String code, 
     String treasuryCode, String year) {
        
    // Agencies can be uniquely identified by code alone
    String key = code+" "+year;
    Agency agency = (Agency) instances.get(key);
    if (agency == null) {
      agency = new Agency(name, code, treasuryCode, year);
      instances.put(key, agency);
    }
    
    return agency;
        
  }
  
  public void add(Bureau b) {
    if (!bureaus.contains(b)) {
        bureaus.add(b);
    }
  }
  
  public Element getXML() {
        
    Element agency = new Element("Agency");
    Element name = new Element("Name");
    Element code = new Element("Code");
    Element treasuryAgencyCode = new Element("TreasuryAgencyCode");
    name.appendChild(this.name);
    code.appendChild(this.code);
    treasuryAgencyCode.appendChild(treasuryCode);
    agency.appendChild(name);
    agency.appendChild(code);
    agency.appendChild(treasuryAgencyCode);
    
    Iterator iterator = bureaus.iterator();
    while (iterator.hasNext()) {
      Bureau bureau = (Bureau) iterator.next();
      agency.appendChild(bureau.getXML());
    }
    return agency;
    
  }  
           
}