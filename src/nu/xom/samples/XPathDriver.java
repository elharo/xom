/* Copyright 2005, 2018 Elliotte Rusty Harold
   
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

import java.io.IOException;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Node;
import nu.xom.ParsingException;
import nu.xom.XPathException;
import nu.xom.XPathTypeException;

/**
 * @author Elliotte Rusty Harold
 * @version 1.3.0
 *
 */
public class XPathDriver {


    /**
      * <p>
      * A simple driver program for XPath.
      * </p>
      *
      * @param args <code>args[0]</code> contains the URL or  
      *      file name of the first document to be processed. 
      *      <code>args[1]</code> contains the XPath expression  
      *      to apply. 
      */
    public static void main(String[] args) {
  
        Builder builder = new Builder();
        
        if (args.length < 2) {
            System.out.println("Usage: java nu.xom.samples.XPathDriver URL xpath");
            return;
        }
        
        try {
            Document input = builder.build(args[0]);
            for (Node result : input.query(args[1])) {
                // ???? add a wrap option like Saxon
                // or based on the XQuery serialization format?
                System.out.println(result.toXML());
            }
        }
        catch (XPathTypeException ex) {
            System.err.println(ex.getReturnValue());
        }
        catch (XPathException ex) {
            System.err.println("XPath error: " + ex.getMessage());
        }
        catch (IOException ex) {
            System.err.println("Could not read from " + args[0]);
            System.err.println(ex.getMessage());
        }
        catch (ParsingException ex) {
            System.err.println(args[0] + " is malformed.");
            System.err.println(ex.getMessage());
        }
  
    }
}
