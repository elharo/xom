import nu.xom.*;
import java.math.BigInteger;

public class ValidFibonacci {

  public static void main(String[] args) {
   
      BigInteger low  = BigInteger.ONE;
      BigInteger high = BigInteger.ONE;      
      
      Element root = new Element("Fibonacci_Numbers");  
      for (int i = 1; i <= 10; i++) {
        Element fibonacci = new Element("fibonacci");
        fibonacci.appendChild(low.toString());
        Attribute index = new Attribute("index", String.valueOf(i));
        fibonacci.addAttribute(index);
        root.appendChild(fibonacci);
        
        BigInteger temp = high;
        high = high.add(low);
        low = temp;
      }
      Document doc = new Document(root);
      DocType doctype = new DocType("Fibonacci_Numbers", "fibonacci.dtd");
      doc.insertChild(doctype, 0);
      System.out.println(doc.toXML());  

  }

}
