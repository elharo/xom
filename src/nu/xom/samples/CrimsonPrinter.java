/* Copyright 2002-2004 Elliotte Rusty Harold
   
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

import java.io.IOException;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;


/**
 * <p>
 * Demonstrates specifying the SAX parser class to use.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.1b4
 *
 */
public class CrimsonPrinter {

    public static void main(String[] args) {
  
        if (args.length <= 0) {
          System.out.println("Usage: java nu.xom.samples.CrimsonPrinter URL");
          return;
        }
        
        try {
          XMLReader crimson = XMLReaderFactory.createXMLReader("org.apache.crimson.parser.XMLReaderImpl");
          Builder parser = new Builder(crimson);
          Document doc = parser.build(args[0]);
          System.out.println(doc.toXML());
        }
        catch (ParsingException ex) {
          ex.printStackTrace();
          System.out.println(args[0] + " is not well-formed.");
          System.out.println(ex.getMessage());
          System.out.println(" in line " + ex.getLineNumber() + " at column " + ex.getColumnNumber() );
          ex.getCause().printStackTrace();
        }
        catch (IOException ex) { 
          System.out.println(
           "Due to an IOException, the parser could not print " 
           + args[0]
          ); 
        }
        catch (SAXException e) {
            System.err.println("Could not find the Crimson parser in the classpath.");
        }
  
    }

}