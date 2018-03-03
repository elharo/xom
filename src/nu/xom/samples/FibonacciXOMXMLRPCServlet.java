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
   elharo@ibiblio.org. Please include the word "XOM" in the
   subject line. The XOM home page is located at http://www.xom.nu/
*/

package nu.xom.samples;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

import javax.servlet.ServletException;
import javax.servlet.SingleThreadModel;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;
import nu.xom.XMLException;

/**
 * <p>
 * Demonstrates a servlet that receives and
 * responds to XML-RPC requests.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0
 *
 */
public class FibonacciXOMXMLRPCServlet extends HttpServlet 
 implements SingleThreadModel {

  // Fault codes   
  public final static int MALFORMED_REQUEST_DOCUMENT = 1;
  public final static int INVALID_REQUEST_DOCUMENT   = 2;
  public final static int INDEX_MISSING              = 3;
  public final static int NON_POSITIVE_INDEX         = 4;
  public final static int BAD_INTEGER_FORMAT         = 5;
  public final static int UNEXPECTED_PROBLEM         = 255;
   
  private transient Builder parser;
  
  // Load a parser, transformer, and implementation
  public void init() throws ServletException {  
  
    try {
      this.parser = new Builder();
    }
    catch (Exception ex) { 
      throw new ServletException(
       "Could not locate a JAXP parser", ex); 
    }
    
  }   
  
  // Respond to an XML-RPC request
  public void doPost(HttpServletRequest servletRequest,
   HttpServletResponse servletResponse)
   throws ServletException, IOException {
    
    servletResponse.setContentType("application/xml; charset=UTF-8");               
    OutputStream out = servletResponse.getOutputStream();
    InputStream in  = servletRequest.getInputStream();

    Document request;
    Document response;
    try {
        request = parser.build(in);
        Element methodCall = request.getRootElement();
        Element params = methodCall.getFirstChildElement("params");
        String generations = params.getValue().trim();
        int numberOfGenerations = Integer.parseInt(generations);
        BigInteger result = calculateFibonacci(numberOfGenerations);
        response = makeResponseDocument(result);
    }
    catch (XMLException ex) {  
      response = makeFaultDocument(MALFORMED_REQUEST_DOCUMENT, ex.getMessage());
    }
    catch (NullPointerException ex) {  
      response = makeFaultDocument(INDEX_MISSING, ex.getMessage());
    }
    catch (NumberFormatException ex) {  
      response = makeFaultDocument(BAD_INTEGER_FORMAT, ex.getMessage());
    }
    catch (Exception ex) {  
      response = makeFaultDocument(UNEXPECTED_PROBLEM, ex.getMessage());
    }
    
    // Transform onto the OutputStream
    try {
      Serializer output = new Serializer(out, "US-ASCII");
      output.write(response);
      servletResponse.flushBuffer();
      out.flush(); 
    }
    catch (Exception ex) {
      // If we get an exception at this point, it's too late to
      // switch over to an XML-RPC fault.
      throw new ServletException(ex); 
    }
    
  }
  
  // If performance is an issue, this could be pre-built in the
  // init() method and then cached. You'd just change one text 
  // node each time.  This would only work in a SingleThreadModel 
  // servlet.
  public Document makeResponseDocument(BigInteger result) {
    
    Element methodResponse = new Element("methodResponse");
    Element params         = new Element("params");
    Element param          = new Element("param");
    Element value          = new Element("value");
    Element doubleElement  = new Element("double");
 
    methodResponse.appendChild(params);
    params.appendChild(param);
    param.appendChild(value);
    value.appendChild(doubleElement);
    doubleElement.appendChild(result.toString());

    return new Document(methodResponse);
   
  }
  
  public Document makeFaultDocument(int faultCode, String faultString) {
    
    Element methodResponse = new Element("methodResponse");
    
    Element fault         = new Element("fault");
    Element value         = new Element("value");
    Element struct        = new Element("struct");
    Element memberCode    = new Element("member");
    Element nameCode      = new Element("name");
    Element valueCode     = new Element("value");
    Element intCode       = new Element("int");
    Element memberString  = new Element("member");
    Element valueString   = new Element("value");
    Element stringString  = new Element("string");

    methodResponse.appendChild(fault);
    fault.appendChild(value);
    value.appendChild(struct);
    struct.appendChild(memberCode);
    struct.appendChild(memberString);
    memberCode.appendChild(nameCode);
    memberCode.appendChild(valueCode);
    memberString.appendChild("name");
    memberString.appendChild(valueString);
    nameCode.appendChild("faultCode");
    valueCode.appendChild(intCode);
    valueString.appendChild(stringString);
    intCode.appendChild(String.valueOf(faultCode));
    stringString.appendChild(faultString);

    Document faultDoc = new Document(methodResponse);
    
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
