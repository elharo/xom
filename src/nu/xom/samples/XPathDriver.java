/* Copyright 2005 Elliotte Rusty Harold
   
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
   subject line. The XOM home page is located at http://www.xom.nu/
*/

package nu.xom.samples;

import java.io.IOException;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.XPathException;

/**
 * @author Elliotte Rusty Harold
 * @version 1.1a2
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
            Nodes result = input.query(args[1]);
            for (int i = 0; i < result.size(); i++) {
                // ???? add a wrap option like Saxon
                // or based on the XQuery serialization format?
                System.out.println(result.get(i).toXML());
            }
        }
        catch (XPathException ex) {
            System.err.println("XPath error: " + ex.getMessage());
            return;
        }
        catch (IOException ex) {
            System.err.println("Could not read from " + args[0]);
            System.err.println(ex.getMessage());
            return;
        }
        catch (ParsingException ex) {
            System.err.println(args[0] + " is malformed.");
            System.err.println(ex.getMessage());
            return;
        }
  
    }
}
