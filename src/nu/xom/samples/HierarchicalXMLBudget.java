/* Copyright 2002, 2003, 2019 Elliotte Rusty Harold
   
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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import nu.xom.Document;
import nu.xom.Serializer;


/**
 * 
 * <p>
 * Demonstrates building a structured XML document,
 * from flat, tabular data. A different version of this 
 * example was originally developed for Chapter 4 of 
 * <cite><a target="_top"
 * href="https://www.cafeconleche.org/books/xmljava/">Processing 
 * XML with Java</a></cite>.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.3.1
 *
 */
public class HierarchicalXMLBudget {

  public static void convert(List<Map<String, String>> budgetData, String year, 
   OutputStream out) throws IOException { 
     
    Budget budget = new Budget(year);
    for (Map<String, String> lineItem : budgetData) {
      budget.add(lineItem);
    }

    Document doc = new Document(budget.getXML());
    Serializer sout = new Serializer(out, "UTF-8");
    sout.write(doc); 
    sout.flush();
        
  }

  public static void main(String[] args) {
  
    try {
        
      if (args.length < 2) {
        System.out.println(
         "Usage: nu.xom.samples.HierarchicalXMLBudget year infile outfile");
        return;
      }
      
      // simple error checking on the year value
      try {
        if (!args[0].equals("TransitionalQuarter")) {
          Integer.parseInt(args[0]);
        }
      }
      catch (NumberFormatException ex) {
        System.out.println(
         "Usage: HierarchicalXMLBudget year infile outfile");
        return;        
      }
      
      InputStream in = new FileInputStream(args[1]); 
      OutputStream out; 
      if (args.length < 3) {
        out = System.out;
      }
      else {
        out = new FileOutputStream(args[2]); 
      }

      List<Map<String, String>> results = BudgetData.parse(in);
      convert(results, args[0], out);
    }
    catch (IOException e) {
      System.err.println(e);       
    }
  
  }

}