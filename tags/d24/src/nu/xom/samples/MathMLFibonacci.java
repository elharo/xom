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

import java.math.BigInteger;

import nu.xom.Document;
import nu.xom.Element;

/**
 * 
 * <p>
 * Demonstrates the creation and serialization of a 
 * MathML document
 * that uses namespaces and namespace prefixes.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d18
 *
 */
public class MathMLFibonacci {

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
      System.out.println(doc.toXML());  

  }

}