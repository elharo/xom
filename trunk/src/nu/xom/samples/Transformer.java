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

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Nodes;
import nu.xom.xslt.XSLTransform;

/**
 * @author Elliotte Rusty Harold
 * @version 1.0d18
 *
 */
public class Transformer {

    /**
      * <p>
      * The driver method for the Transformer program.
      * </p>
      *
      * @param args <code>args[0]</code> contains the URL or  
      *      file name of the document to be transformed. 
      * <code>args[1]</code> contains the URL or  
      *      file name of the stylesheet. 
      */
    public static void main(String[] args) {
  
       System.setProperty(
          "javax.xml.transform.TransformerFactory", 
          "org.apache.xalan.processor.TransformerFactoryImpl"); 
      /* System.setProperty(
        "javax.xml.transform.TransformerFactory", 
        "com.icl.saxon.TransformerFactoryImpl"); */
     
        Builder builder = new Builder();
        try {
            Document doc = builder.build(args[0]);
            Document stylesheet = builder.build(args[1]);
            XSLTransform transform = new XSLTransform(stylesheet);           
            
            Nodes output = transform.transform(doc);
            
            for (int i = 0; i < output.size(); i++) {
                System.out.print(output.get(i).toXML());                
            } 
            System.out.println();
        }
        catch (Exception ex) {
            System.err.println(ex);
            ex.printStackTrace();
        }
  
    }

}
