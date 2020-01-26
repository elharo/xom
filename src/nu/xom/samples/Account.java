/* Copyright 2002, 2003, 2019 Elliotte Rusty Harold
   
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
 * @version 1.3.1
 *
 */
public class Account {
 
  // An account is uniquely identified by account code,
  // bureau code, agency code and BEA category
  private String code;
  private String name;
  private String BEACategory;
  private String bureauCode;
  private String agencyCode;
  private String year;
  
  private List<Subfunction> subfunctions = new ArrayList<Subfunction>();
  
  private static Map<String, Account> instances = new HashMap<String, Account>();

  // Use a private constructor so clients 
  // have to use the factory method
  private Account(String name, String code, String BEACategory, 
   String bureauCode, String agencyCode, String year) {
        
    this.name = name;
    this.code = code;
    this.BEACategory = BEACategory;
    this.bureauCode = bureauCode;
    this.agencyCode = agencyCode;
    this.year = year;
    
  }
  
  public static Account getInstance(String name, String code, 
   String BEACategory, String bureauCode, String agencyCode, 
   String year) {
        
    String key = code + " " + BEACategory + " " + bureauCode 
     + " " + agencyCode + " " + year;
    Account account = (Account) instances.get(key);
    if (account == null) {
      account = new Account(name, code, BEACategory, bureauCode, 
       agencyCode, year);
      instances.put(key, account);
    }
    
    return account;
        
  }
  
  public void add(Subfunction sfx) {
    if (!subfunctions.contains(sfx)) subfunctions.add(sfx);     
  }
  
  public Element getXML() {
        
    Element account = new Element("Account");
    Element name = new Element("Name");
    Element code = new Element("Code");
    Element BEACategory = new Element("BEACategory");
    name.appendChild(this.name);
    code.appendChild(this.code);
    BEACategory.appendChild(this.BEACategory);
    account.appendChild(name);
    account.appendChild(code);
    account.appendChild(BEACategory);

    Iterator<Subfunction> iterator = subfunctions.iterator();
    while (iterator.hasNext()) {
      Subfunction subfunction = iterator.next();
      account.appendChild(subfunction.getXML());
    }
    return account;
    
  }
           
}
