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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nu.xom.Document;
import nu.xom.Element;
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
public class FlatXMLBudget {

  public static void convert(List data, OutputStream out) 
   throws IOException {
      
    Element budget = new Element("Budget");
    Document doc = new Document(budget);
    
    Iterator records = data.iterator();
    while (records.hasNext()) {
      Element lineItem = new Element("LineItem");
      Map record = (Map) records.next();
      Set fields = record.entrySet();
      Iterator entries = fields.iterator();
      while (entries.hasNext()) {
        Map.Entry entry = (Map.Entry) entries.next();
        String name = (String) entry.getKey();
        String value = (String) entry.getValue();
        // some of the values contain ampersands and less than
        // signs that must be escaped
        Element field = new Element(name);
        field.appendChild(value); 
         lineItem.appendChild(field);
       }
       budget.appendChild(lineItem);
    }

    Serializer serializer = new Serializer(out, "UTF-8");
    serializer.write(doc);
    serializer.flush();
        
  } 

  public static void main(String[] args) {
  
    try {
        
      if (args.length < 1) {
       System.out.println("Usage: nu.xom.samples.FlatXMLBudget infile outfile");
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

      List results = BudgetData.parse(in);
      convert(results, out);
    }
    catch (IOException e) {
      System.err.println(e);       
    }
  
  }

}