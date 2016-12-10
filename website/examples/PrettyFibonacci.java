import nu.xom.*;

import java.io.IOException;
import java.math.BigInteger;

public class PrettyFibonacci {

  public static void main(String[] args) {
   
    BigInteger low  = BigInteger.ONE;
    BigInteger high = BigInteger.ONE;      

    Element root = new Element("Fibonacci_Numbers");  
    for (int i = 1; i <= 10; i++) {
        Element fibonacci = new Element("fibonacci");
        fibonacci.appendChild(low.toString());
        root.appendChild(fibonacci);
        
        BigInteger temp = high;
        high = high.add(low);
        low = temp;
    }
    Document doc = new Document(root);
      
    try {
      Serializer serializer = new Serializer(System.out, "ISO-8859-1");
      serializer.setIndent(4);
      serializer.setMaxLength(64);
      serializer.write(doc);  
    }
    catch (IOException ex) {
       System.err.println(ex); 
    }  
    
  }
  
}
