/* Copyright 2002, 2003, 2018 Elliotte Rusty Harold
   
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

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.xslt.XSLTransform;

/**
 * @author Elliotte Rusty Harold
 * @version 1.3.0
 *
 */
public class Transformer {

    /**
      * <p>
      * The driver method for the Transformer program.
      * </p>
      *
      * @param args <code>args[0]</code> contains the URL or  
      *      filename of the document to be transformed. 
      *      <code>args[1]</code> contains the URL or  
      *      filename of the stylesheet. 
      */
    public static void main(String[] args) {
        
       if (args.length < 2) {
           System.err.println(
             "Usage: java nu.xom.samples.Transformer document stylesheet"
           );
           return;   
       }
  
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
            
            for (Node node : output) {
                System.out.print(node.toXML());                
            } 
            System.out.println();
        }
        catch (Exception ex) {
            System.err.println(ex);
            ex.printStackTrace();
        }
  
    }

}
