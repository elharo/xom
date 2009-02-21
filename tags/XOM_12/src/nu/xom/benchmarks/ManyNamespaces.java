

package nu.xom.benchmarks;

import java.io.*;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.canonical.*;
 
class ManyNamespaces {
    
    public static void main(String[] args) throws Exception {
                
        Builder builder = new Builder();
        Document dataDoc = builder.build("data/manynamespaces.xml");
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Canonicalizer c = new Canonicalizer(out);
        long pre = System.currentTimeMillis(); 
        for (int i = 0; i < 1; i++) {
            c.write(dataDoc);
        }
        out.close();
        long post = System.currentTimeMillis();
        System.out.println(out.toByteArray().length);
        System.out.println((post - pre)/1000.0 + "s to canonicalize document");
          
    }

}
