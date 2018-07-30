import java.math.BigInteger;
import nu.xom.*;

public class FibonacciXML {

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
      System.out.println(doc.toXML());  

  }

}
