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
   subject line. The XOM home page is located at http://www.xom.nu/
*/

package nu.xom.samples;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import nu.xom.Serializer;

/**
 * <p>
 * Demonstrates communication with a SOAP
 * server via the creation of an XML document,
 * transmission of that document over the network,
 * and reception and parsing of the server's response
 * document.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0
 *
 */
public class FibonacciSOAPClient {
  
    public final static String defaultServer 
      = "http://www.elharo.com/fibonacci/SOAP";
    public final static String SOAP_ACTION 
      = "http://www.example.com/fibonacci";
      
    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println(
              "Usage: java nu.xom.samples.FibonacciSOAPClient index serverURL"
            ); 
            return;
        }
    
        String index = args[0];
        
        String server;
        if (args.length <= 1) server = defaultServer;
        else server = args[1];
          
        Document request = buildRequest(index);
        
        try {
            URL u = new URL(server);
            URLConnection uc = u.openConnection();
            HttpURLConnection connection = (HttpURLConnection) uc;
            connection.setDoOutput(true);
            connection.setDoInput(true); 
            connection.setRequestMethod("POST");
            connection.setRequestProperty("SOAPAction", SOAP_ACTION);
         
            OutputStream out = connection.getOutputStream();
            Serializer serializer = new Serializer(out, "US-ASCII");
            serializer.write(request);
            serializer.flush();
              
            InputStream in = connection.getInputStream();
              
            Builder parser = new Builder();
            Document response = parser.build(in);
            in.close();
            out.close();
            connection.disconnect();

        /* This is the response we expect:
         * <?xml version="1.0"?>
<SOAP-ENV:Envelope
 xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" />
  <SOAP-ENV:Body>
    <Fibonacci_Numbers 
      xmlns="http://namespaces.cafeconleche.org/xmljava/ch3/">
      <fibonacci index="1">1</fibonacci>
      <fibonacci index="2">1</fibonacci>
      <fibonacci index="3">2</fibonacci>
      <fibonacci index="4">3</fibonacci>
      <fibonacci index="5">5</fibonacci>
      <fibonacci index="6">8</fibonacci>
      <fibonacci index="7">13</fibonacci>
      <fibonacci index="8">21</fibonacci>
      <fibonacci index="9">34</fibonacci>
      <fibonacci index="10">55</fibonacci>
    </Fibonacci_Numbers>
  </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
         * 
         */

            Element responseEnvelope = response.getRootElement();          
            Element responseBody = responseEnvelope.getFirstChildElement("Body", 
             "http://schemas.xmlsoap.org/soap/envelope/");
             
            // Check for fault
            Element fault = responseBody.getFirstChildElement(
             "Fault", "http://schemas.xmlsoap.org/soap/envelope/");
            if (fault == null) { // no fault
                Element responseNumbers = responseBody.getFirstChildElement(
                 "Fibonacci_Numbers", "http://namespaces.cafeconleche.org/xmljava/ch3/");
                Elements results = responseNumbers.getChildElements("fibonacci", 
                 "http://namespaces.cafeconleche.org/xmljava/ch3/");
                for (int i = 0; i < results.size(); i++) {
                    System.out.println(results.get(i).getValue());    
                }
            }
            else { 
                handleFault(fault);
            }   
          
        }
        catch (ParsingException ex) {
          System.err.println("Server sent malformed output"); 
          System.err.println(ex); 
        }
        catch (NullPointerException ex) {
            System.err.println(
              "Server sent invalid output without the expected content."
            ); 
            System.err.println(ex); 
        }
        catch (IOException ex) {
            System.err.println(ex); 
            ex.printStackTrace();
        }
  
    }

    private static void handleFault(Element fault) {
        
        Element faultcode = fault.getFirstChildElement("faultcode");
        Element faultstring = fault.getFirstChildElement("faultstring");
        Element faultactor = fault.getFirstChildElement("faultactor");
        Element detail = fault.getFirstChildElement("detail");
        
        String error = "Fault: \n";
        if (faultcode != null) {
            error += "Fault code: " + faultcode.getValue() + "\n";   
        }
        if (faultstring != null) {
            error += "Fault string: " + faultstring.getValue() + "\n";   
        }
        if (faultactor != null) {
            error += "Fault actor: " + faultactor.getValue() + "\n";   
        }
        if (detail != null) {
            error += "Details: " + detail.getValue() + "\n";   
        }
        
    }

    public static Document buildRequest(String index) {
        
        String SOAPNamespace = "http://schemas.xmlsoap.org/soap/envelope/";
        Element envelope = new Element("SOAP-ENV:Envelope",
         SOAPNamespace);
        Element body = new Element("SOAP-ENV:Body",
         SOAPNamespace);
        Element calculateFibonacci = new Element("calculateFibonacci",
         "http://namespaces.cafeconleche.org/xmljava/ch3/");
        calculateFibonacci.appendChild(index);
        calculateFibonacci.addNamespaceDeclaration("xsi", 
         "http://www.w3.org/2001/XMLSchema-instance");
        Attribute type = new Attribute("type", "xsi:positiveInteger");
        calculateFibonacci.addAttribute(type);
        
        envelope.appendChild(body);
        body.appendChild(calculateFibonacci);
        
        Document doc = new Document(envelope);
        return doc;
        
    }  

}