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

import java.io.*;
import nu.xom.Document;
import nu.xom.Builder;
import nu.xom.ParsingException;

/**
 * @author Elliotte Rusty Harold
 * @version 1.0d22
 *
 */
public class ROT13File {

  public static void main(String[] args) {

    if (args.length <= 0) {
      System.out.println("Usage: java nu.xom.samples.ROT13File filename");
      return;
    }
    
    File file = new File(args[0]);
    
    try {
      Builder parser = new Builder();
      
      // Read the document
      Document document = parser.build(file); 
      
      // Modify the document
      ROT13XML.encode(document);

      // Write it out again
      System.out.println(document.toXML());

    }
    catch (IOException ex) { 
      System.out.println(
      "Due to an IOException, the parser could not encode " + file
      ); 
    }
    catch (ParsingException ex) { 
      System.out.println(ex); 
      ex.printStackTrace(); 
    }
     
  } // end main
  
}
