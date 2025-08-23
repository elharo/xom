/* Simple XSLT transformer using JDK built-in processor */
package nu.xom.samples;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.xslt.XSLTransform;

public class SimpleTransformer {

    public static void main(String[] args) {
        
       if (args.length < 2) {
           System.err.println(
             "Usage: java nu.xom.samples.SimpleTransformer document stylesheet"
           );
           return;   
       }
  
       // Use the built-in JDK XSLT processor
       System.setProperty(
          "javax.xml.transform.TransformerFactory", 
          "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
     
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