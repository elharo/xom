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
 * Demonstrates the creation of a simple document
 * with basic nested element structure
 * and the serialization of that document in a
 * specified encoding.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0
 *
 */
public class FibonacciLatin1 {

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
            Serializer serializer 
             = new Serializer(System.out, "ISO-8859-1");
            serializer.write(doc);
        }  
        catch (IOException ex) {
            System.err.println("This shouldn't happen for Latin-1!");
        }

  }

}