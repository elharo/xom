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

import java.io.IOException;

import nu.xom.Builder;
import nu.xom.ParsingException;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * <p>
 * Demonstrates schema validation by setting SAX properties
 * on an <code>XMLReader</code>
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d22
 *
 */
public class SchemaValidator {

  public static void main(String[] args) {
  
    if (args.length <= 0) {
      System.out.println(
        "Usage: java nu.xom.samples.SchemaValidator URL");
      return;
    }
    
    try {      
      XMLReader xerces = XMLReaderFactory.createXMLReader(
       "org.apache.xerces.parsers.SAXParser"); 
      xerces.setFeature(
        "http://apache.org/xml/features/validation/schema",
         true
      );                         

      Builder parser = new Builder(xerces, true);
      parser.build(args[0]);
      System.out.println(args[0] + " is schema valid.");
    }
    catch (SAXException ex) {
      System.out.println("Could not load Xerces.");
      System.out.println(ex.getMessage());
    }
    catch (ParsingException ex) {
      System.out.println(args[0] + " is not schema valid.");
      System.out.println(ex.getMessage());
      ex.printStackTrace();
      System.out.println(" at line " + ex.getLineNumber() 
        + ", column " + ex.getColumnNumber());
    }
    catch (IOException ex) { 
      System.out.println(
       "Due to an IOException, Xerces could not check " 
       + args[0]
      ); 
      ex.printStackTrace();
    }
  
  }

}