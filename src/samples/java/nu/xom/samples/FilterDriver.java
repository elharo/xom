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
import nu.xom.NodeFactory;
import nu.xom.Serializer;

/**
 * @author Elliotte Rusty Harold
 * @version 1.0
 *
 */
public class FilterDriver {


    /**
      * <p>
      * The driver method for the various <code>NodeFactory</code>
      * based filters. 
      * </p>
      *
      * @param args <code>args[0]</code> contains the fully qualified 
      *     class name of the ,codeNodeFactory</code> to use. 
      * @param args <code>args[1]</code> contains the URL or  
      *      file name of the first document to be processed. 
      */
    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println(
              "Usage: java nu.xom.samples.FilterDriver filterclass URL"
            );   
            return;
        }        
        
        try {
            Builder builder = new Builder((NodeFactory) Class.forName(args[0]).newInstance());
            Serializer outputter = new Serializer(System.out, "ISO-8859-1");
            Document input = builder.build(args[1]);
            outputter.write(input);
        }
        catch (ClassNotFoundException ex) {
            System.err.println("Could not find filter " + args[0]);
        }
        catch (Exception ex) {
            System.err.println(ex);
            ex.printStackTrace();
        }
  
    }
}
