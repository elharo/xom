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
   subject line. The XOM home page is located at http://www.xom.nu/
*/

package nu.xom.samples;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Serializer;
import nu.xom.xinclude.XIncluder;

/**
 * @author Elliotte Rusty Harold
 * @version 1.0
 *
 */
public class XIncludeDriver {


    /**
      * <p>
      * The driver method for the XIncluder program.
      * </p>
      *
      * @param args <code>args[0]</code> contains the URL or  
      *      file name of the first document to be processed. 
      */
    public static void main(String[] args) {
  
        Builder builder = new Builder();
        try {
            Serializer outputter = new Serializer(System.out, "ISO-8859-1");
            Document input = builder.build(args[0]);
            XIncluder.resolveInPlace(input);
            outputter.write(input);
        }
        catch (Exception ex) {
            System.err.println(ex);
            ex.printStackTrace();
        }
  
    }
}
