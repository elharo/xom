/* Copyright 2002, 2003 Elliotte Rusty Harold
   
   This library is free software; you can redistribute it and/or modify
   it under the terms of version 2.1 of the GNU Lesser General Public 
   License as published by the Free Software Foundation.
   
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
   GNU Lesser General Public License for more details.
   
   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the 
   Free Software Foundation, Inc., 59 Temple Place, Suite 330, 
   Boston, MA 02111-1307  USA
   
   You can contact Elliotte Rusty Harold by sending e-mail to
   elharo@metalab.unc.edu. Please include the word "XOM" in the
   subject line. The XOM home page is temporarily located at
   http://www.cafeconleche.org/XOM/  but will eventually move
   to http://www.xom.nu/  */

package nu.xom.samples;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

/**
 * <p>
 * Demonstrates the creation of a simple document
 * in memory and saving it to a file.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d18
 *
 */
public class FibonacciFile {

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
            OutputStream out = new FileOutputStream("fibonacci.xml");
            out = new BufferedOutputStream(out);
            Serializer serializer 
             = new Serializer(out, "ISO-8859-1");
            serializer.write(doc);
        }  
        catch (IOException ex) {
            System.err.println("This shouldn't happen for Latin-1!");
        }

  }

}