// Copyright 2002-2004 Elliotte Rusty Harold
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
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;

import javax.servlet.ServletException;
import javax.servlet.SingleThreadModel;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.WellformednessException;


/**
 * <p>
 * Demonstrates a servlet that receives and
 * responds to SOAP requests.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0b3
 *
 */
public class FibonacciXOMSOAPServlet extends HttpServlet 
  implements SingleThreadModel {

    // Fault codes   
    public final static String MALFORMED_REQUEST_DOCUMENT 
      = "MalformedRequest";
    public final static String INVALID_REQUEST_DOCUMENT 
      = "InvalidRequest";
    public final static String INDEX_MISSING 
      = "IndexMissing";
    public final static String NON_POSITIVE_INDEX 
      = "NonPositiveIndex";
    public final static String BAD_INTEGER_FORMAT
      = "BadIntegerFormat";
    public final static String UNEXPECTED_PROBLEM
      = "UnexpectedProblem";    
    
    private transient Builder parser;
  
    // Load a parser, transformer, and implementation
    public void init() throws ServletException {  
  
        try {
          this.parser = new Builder();
        }
        catch (Exception ex) { 
          throw new ServletException(
           "Could not locate a XOM parser", ex); 
        }

    } 
  
  
    public void doPost(HttpServletRequest servletRequest,
      HttpServletResponse servletResponse)
      throws ServletException, IOException {
    
        servletResponse.setContentType("application/soap+xml; charset=UTF-8");               
        OutputStreamWriter out = new OutputStreamWriter(
          servletResponse.getOutputStream(), "UTF-8");
        InputStream in = servletRequest.getInputStream();
    
        Document request;
        Document response;
        String generations ="here";
        try {
            request = parser.build(in);
       
/* <?xml version="1.0"?>
<SOAP-ENV:Envelope
 xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"
 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" >
  <SOAP-ENV:Body>
    <calculateFibonacci 
      xmlns="http://namespaces.cafeconleche.org/xmljava/ch3/"
      type="xsi:positiveInteger">10</calculateFibonacci>
  </SOAP-ENV:Body>
</SOAP-ENV:Envelope> */       
       
            generations = request.getValue().trim();
            int numberOfGenerations = Integer.parseInt(generations);
            BigInteger result = calculateFibonacci(numberOfGenerations);
            response = makeResponseDocument(result);
        }
        catch (WellformednessException ex) {  
            response = makeFaultDocument(MALFORMED_REQUEST_DOCUMENT, 
              ex.getMessage());
        }
        catch (NullPointerException ex) {  
            response = makeFaultDocument(INDEX_MISSING, 
              ex.getMessage());
        }
        catch (NumberFormatException ex) {  
            response = makeFaultDocument(BAD_INTEGER_FORMAT, 
              generations + ex.getMessage());
        }
        catch (IndexOutOfBoundsException ex) {  
            response = makeFaultDocument(NON_POSITIVE_INDEX, 
              ex.getMessage());
        }
        catch (Exception ex) {  
            response = makeFaultDocument(UNEXPECTED_PROBLEM, 
              ex.getMessage());
        }
    
        // Transform onto the OutputStream
        try {
            out.write(response.toXML());
            servletResponse.flushBuffer();
            out.flush();
        }
        catch (IOException ex) {
            // If we get an exception at this point, it's too late to
            // switch over to a SOAP fault
            throw new ServletException(ex); 
        }
        finally {
            in.close();
            out.close();
        }
    
    }

    
    // The details of the formats and namespace URIs are likely to
    // change when SOAP 1.2 is released.
    public Document makeResponseDocument(BigInteger result) {
    
        Element envelope = new Element("SOAP-ENV:Envelope",
          "http://schemas.xmlsoap.org/soap/envelope/");
        Document response = new Document(envelope); 
        Element body = new Element("SOAP-ENV:Body", 
          "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.appendChild(body);
     
        Element Fibonacci_Numbers = new Element("Fibonacci_Numbers",  
         "http://namespaces.cafeconleche.org/xmljava/ch3/");
        body.appendChild(Fibonacci_Numbers);
    
        Element fibonacci = new Element("fibonacci",
         "http://namespaces.cafeconleche.org/xmljava/ch3/");
        Fibonacci_Numbers.appendChild(fibonacci);
        fibonacci.appendChild(result.toString());
    
        return response;
   
    }
  
    
    public Document makeFaultDocument(String code, String message){
     
        Element envelope = new Element("SOAP-ENV:Envelope",
          "http://schemas.xmlsoap.org/soap/envelope/");
        Document faultDoc = new Document(envelope);
    
        Element body = new Element("SOAP-ENV:Body", 
          "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.appendChild(body);
     
        Element fault = new Element("Fault", 
         "http://schemas.xmlsoap.org/soap/envelope/");
        body.appendChild(fault);
    
        Element faultCode = new Element("faultcode");
        fault.appendChild(faultCode);
    
        Element faultString = new Element("faultstring");
        fault.appendChild(faultString);
    
        faultCode.appendChild(code);
    
        faultString.appendChild(message);
        
        return faultDoc;
       
    } 
  
    
    public static BigInteger calculateFibonacci(int generations) 
      throws IndexOutOfBoundsException {
    
        if (generations < 1) {
            throw new IndexOutOfBoundsException(
              "Fibonacci numbers are not defined for " + generations 
              + "or any other number less than one.");
        }
        BigInteger low  = BigInteger.ONE;
        BigInteger high = BigInteger.ONE;      
        for (int i = 2; i <= generations; i++) {
            BigInteger temp = high;
            high = high.add(low);
            low = temp;
        }
        return low;   
        
    }

}
