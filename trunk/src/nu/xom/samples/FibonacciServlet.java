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

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ProcessingInstruction;
import nu.xom.Serializer;


/**
 * <p>
 * Demonstrates a servlet that processes XML-RPC requests.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d18
 *
 */
public class FibonacciServlet extends HttpServlet {

    public void doGet(
      HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    
        //read the query string
        int numberOfGenerations = 10;
        String generations = request.getParameter("generations");
        try {
            numberOfGenerations = Integer.parseInt(generations);
        }
        catch (Exception e) { // NumberFormat or NullPointerException
              // use default value of 10 
        }
     
         response.setContentType("text/xml; charset=UTF-8");               
        OutputStream out = response.getOutputStream();
    
        Element root = new Element("Fibonacci_Numbers"); 
        Document doc = new Document(root);
        ProcessingInstruction stylesheet = new 
          ProcessingInstruction("xml-stylesheet", 
          "type='text/css' href='/xml/styles/fibonacci.css'");
        doc.insertChild(stylesheet, 0);
    
        BigInteger low  = BigInteger.ONE;
        BigInteger high = BigInteger.ONE;      
        for (int i = 1; i <= numberOfGenerations; i++) {
            Element fibonacci = new Element("fibonacci");
            Attribute index 
              = new Attribute("index", String.valueOf(i));
            fibonacci.addAttribute(index);
            fibonacci.appendChild(low.toString());
       
            BigInteger temp = high;
            high = high.add(low);
             low = temp;
        }
        Serializer serializer = new Serializer(out, "UTF-8");
        serializer.write(doc);
        serializer.flush();
         out.close();
    
  }

    public void doPost(
      HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
        doGet(request, response);
      }

}