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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Element;


/**
 * 
 * <p>
 * Demonstrates the building of a structured XML document,
 * from flat, tabular data. A different version of this 
 * example was originally developed for Chapter 4 of 
 * <cite><a href="http://www.cafeconleche.org/books/xmljava/">Processing 
 * XML with Java</a></cite>.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d18
 *
 */
public class Budget {

  private List   agencies = new ArrayList();
  private String year;
  
  public Budget(String year) {
    this.year = year;
  }
  
  // not thread safe
  public void add(Agency agency) {
    if (!agencies.contains(agency)) agencies.add(agency);     
  }

  public void add(Map lineItem) { 
           
    String agencyName = (String) lineItem.get("AgencyName");
    String agencyCode = (String) lineItem.get("AgencyCode");
    String treasuryAgencyCode 
     = (String) lineItem.get("TreasuryAgencyCode");
    Agency agency = Agency.getInstance(agencyName, agencyCode, 
     treasuryAgencyCode, year);
    this.add(agency);
    
    String bureauName = (String) lineItem.get("BureauName");
    String bureauCode = (String) lineItem.get("BureauCode");
    Bureau bureau = Bureau.getInstance(bureauName, bureauCode, 
     agencyCode, year);
    agency.add(bureau);
    
    // Names and codes of two accounts in different bureaus 
    // can be the same
    String accountName = (String) lineItem.get("AccountName");
    String accountCode = (String) lineItem.get("AccountCode");
    String category    = (String) lineItem.get("BEACategory");
    Account account = Account.getInstance(accountName,  
     accountCode, category, bureauCode, agencyCode, year);
    bureau.add(account);
    
    // Names and codes of two subfunctions in different accounts 
    // can be the same
    String subfunctionTitle = (String) lineItem.get("SubfunctionTitle");
    String subfunctionCode
     = (String) lineItem.get("SubfunctionCode");
    String yearKey = year;
    if (!yearKey.equals("TransitionalQuarter")) {
      yearKey = "Y" + year;
    }
    long amount
     = 1000L * Long.parseLong((String) lineItem.get(yearKey));
    Subfunction subfunction = new Subfunction(subfunctionTitle,
     subfunctionCode, amount);
    account.add(subfunction);
        
  } 

  public Element getXML() {
        
    Element budget = new Element("Budget");
    budget.addAttribute(new Attribute("year", String.valueOf(year)));
    Iterator iterator = agencies.iterator();
    while (iterator.hasNext()) {
      Agency agency = (Agency) iterator.next();
      budget.appendChild(agency.getXML());
    }
     return budget;
    
  }

}