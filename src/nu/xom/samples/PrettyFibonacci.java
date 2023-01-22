/* Copyright 2002, 2003 Elliotte Rusty Harold
   
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

import java.io.IOException;
import java.math.BigInteger;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

/**
 * <p>
 *   Demonstrates using a <code>Serializer</code> object
 *   to insert indents and line breaks in XML.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0
 *
 */
public class PrettyFibonacci {

    public static void main(String[] args) {
   
        BigInteger low  = BigInteger.ONE;
        BigInteger high = BigInteger.ONE;      

/* <mathml:math xmlns:mathml="http://www.w3.org/1998/Math/MathML">
  <mathml:mrow>
    <mathml:mi>f(1)</mathml:mi>
    <mathml:mo>=</mathml:mo>
    <mathml:mn>1</mathml:mn>
  </mathml:mrow> */
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
      
      Serializer serializer = new Serializer(System.out);
      serializer.setIndent(4);
      serializer.setMaxLength(64);
      try {
          serializer.write(doc);  
      }
      catch (IOException ex) {
        System.err.println(ex); 
      }  
    }

}