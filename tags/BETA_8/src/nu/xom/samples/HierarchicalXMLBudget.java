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
   subject line. The XOM home page is temporarily located at
   http://www.cafeconleche.org/XOM/  but will eventually move
   to http://www.xom.nu/  */

package nu.xom.samples;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
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
 * <cite><a href="http://www.cafeconleche.org/books/xmljava/">Processing 
 * XML with Java</a></cite>.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d18
 *
 */
public class HierarchicalXMLBudget {

  public static void convert(List budgetData, String year, 
   OutputStream out) throws IOException { 
     
    Budget budget = new Budget(year);
    Iterator records = budgetData.iterator();
    while (records.hasNext()) {
      Map lineItem = (Map) records.next();
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

      List results = BudgetData.parse(in);
      convert(results, args[0], out);
    }
    catch (IOException e) {
      System.err.println(e);       
    }
  
  }

}