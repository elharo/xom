/* Copyright 2024 Elliotte Rusty Harold
   
   This library is free software; you can redistribute it and/or modify
   it under the terms of version 2.1 of the GNU Lesser General Public 
   License as published by the Free Software Foundation.
   
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
   GNU Lesser General Public License for more details.
   
   You can contact Elliotte Rusty Harold by sending e-mail to
   elharo@ibiblio.org. Please include the word "XOM" in the
   subject line. The XOM home page is located at https://xom.nu/
*/

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.xslt.XSLTransform;

/**
 * Command-line XSLT transformer using XOM.
 * Replacement for Saxon's command-line interface for documentation builds.
 * 
 * @author Elliotte Rusty Harold
 * @version 1.3.9
 */
public class XOMTransformer {

    /**
     * <p>
     * The driver method for the XOMTransformer program.
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
              "Usage: java XOMTransformer document stylesheet"
            );
            System.exit(1);   
        }
  
        Builder builder = new Builder();
        try {
            Document doc = builder.build(args[0]);
            Document stylesheet = builder.build(args[1]);
            XSLTransform transform = new XSLTransform(stylesheet);           
            
            Nodes output = transform.transform(doc);
            
            for (Node node : output) {
                System.out.print(node.toXML());                
            } 
        }
        catch (Exception ex) {
            System.err.println(ex);
            ex.printStackTrace();
            System.exit(1);
        }
  
    }

}
