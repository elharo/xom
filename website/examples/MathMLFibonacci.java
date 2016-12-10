import nu.xom.*;
import java.math.BigInteger;
import java.io.IOException;

public class MathMLFibonacci {

  public static void main(String[] args) {
   
      BigInteger low  = BigInteger.ONE;
      BigInteger high = BigInteger.ONE;      

      String namespace = "http://www.w3.org/1998/Math/MathML";
      Element root = new Element("mathml:math", namespace);  
      for (int i = 1; i <= 10; i++) {
        Element mrow = new Element("mathml:mrow", namespace);
        Element mi = new Element("mathml:mi", namespace);
        Element mo = new Element("mathml:mo", namespace);
        Element mn = new Element("mathml:mn", namespace);
        mrow.appendChild(mi);
        mrow.appendChild(mo);
        mrow.appendChild(mn);
        root.appendChild(mrow);
        mi.appendChild("f(" + i + ")");
        mo.appendChild("=");
        mn.appendChild(low.toString());
        
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
