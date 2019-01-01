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
   subject line. The XOM home page is located at http://www.xom.nu/
*/

package nu.xom.samples;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

/**
 * 
 * <p>
 * Demonstrates building a structured XML document,
 * from flat, tabular data. A different version of this 
 * example was originally developed for Chapter 4 of 
 * <cite><a target="_top" 
 * href="http://www.cafeconleche.org/books/xmljava/">Processing 
 * XML with Java</a></cite>.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.3.1
 *
 */
public class AttributesXMLBudget {

  public static void convert(List<Map<String, String>> data, OutputStream out) 
   throws IOException {
      
    Element budget = new Element("Budget");
    Document doc = new Document(budget);
          
    Iterator<Map<String, String>> records = data.iterator();
    while (records.hasNext()) {
      Element lineItem = new Element("LineItem");
      Map<String, String> record = records.next();

      // write the attributes
      setYear(lineItem, "AgencyCode", record);
      setYear(lineItem, "AgencyName", record);
      setYear(lineItem, "BureauCode", record);
      setYear(lineItem, "BureauName", record);
      setYear(lineItem, "AccountCode", record);
      setYear(lineItem, "AccountName", record);
      setYear(lineItem, "TreasuryAgencyCode", record);
      setYear(lineItem, "SubfunctionCode", record);
      setYear(lineItem, "SubfunctionTitle", record);
      setYear(lineItem, "BEACategory", record);
      setYear(lineItem, "BudgetIndicator", record);
      setAmount(lineItem, "1976", record);
      Element amount = new Element("Amount");
      amount.addAttribute(new Attribute("year", "TransitionalQuarter"));    
      amount.appendChild((String) record.get("TransitionalQuarter"));
      for (int year=1977; year <= 2006; year++) {
        setAmount(lineItem, String.valueOf(year), record);
      }
    }

    Serializer serializer = new Serializer(out, "UTF-8");
    serializer.write(doc);
    serializer.flush();
        
  } 

  // Just a couple of private methods to factor out repeated code 
  private static void setYear(Element element, String name, 
   Map<String, String> record) {
    element.addAttribute(new Attribute(name, record.get(name)));       
  }

  private static void setAmount(Element element, String year, 
   Map<String, String> record) {
    Element amount = new Element("Amount");
    amount.addAttribute(new Attribute("year", String.valueOf(year)));    
    amount.appendChild(record.get("Y" + year));
    element.appendChild(amount);
  }

  public static void main(String[] args) {
  
    try {
        
      if (args.length < 1) {
        System.out.println(
         "Usage: nu.xom.samples.AttributesXMLBudget infile outfile"
        );
        return;
      }
      
      InputStream in = new FileInputStream(args[0]); 
      OutputStream out; 
      if (args.length < 2) {
        out = System.out;
      }
      else {
        out = new FileOutputStream(args[1]); 
      }

      List<Map<String, String>> results = BudgetData.parse(in);
      convert(results, out);
    }
    catch (IOException ex) {
      System.err.println(ex);       
    }
  
  }

}
