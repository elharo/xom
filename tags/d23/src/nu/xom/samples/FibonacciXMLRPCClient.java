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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import nu.xom.Serializer;


/**
 * <p>
 * Demonstrates communication with an XML-RPC
 * server via the creation of a simple document,
 * transmission of that document over the network,
 * and reception and parsing of the server's response.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d23
 *
 */
public class FibonacciXMLRPCClient {
  
  private static String defaultServer 
   = "http://www.elharo.com/fibonacci/XML-RPC";
   
  public static void main(String[] args) {

    if (args.length == 0) {
      System.out.println(
       "Usage: java nu.xom.samples.FibonacciXMLRPCClient "
       + " index serverURL"
      ); 
      return;
    }
    
    String index = args[0];
    
    String server;
    if (args.length <= 1) server = defaultServer;
    else server = args[1];
    
    try {
        URL u = new URL(server);
        URLConnection uc = u.openConnection();
        HttpURLConnection connection = (HttpURLConnection) uc;
        connection.setDoOutput(true);
        connection.setDoInput(true); 
        connection.setRequestMethod("POST");
        OutputStream out = connection.getOutputStream();
      
        Element methodCall = new Element("methodCall");
        Element methodName = new Element("methodName");
        methodName.appendChild("calculateFibonacci");
        Element params = new Element("params");
        Element param = new Element("param");
        Element value = new Element("value");
        Element data = new Element("int");
        data.appendChild(index);
        methodCall.appendChild(methodName); 
        methodCall.appendChild(params); 
        params.appendChild(param);
        param.appendChild(value);
        value.appendChild(data);
        Document doc = new Document(methodCall);

        Serializer serializer = new Serializer(out, "US-ASCII");
        serializer.write(doc);
          
        InputStream in = connection.getInputStream();
          
        Builder parser = new Builder();
        Document response = parser.build(in);

        in.close();
        out.close();
        connection.disconnect();

        Element methodResponse = response.getRootElement();          
        Elements body = methodResponse.getChildElements();
        if (body.size() != 1) {
            System.err.println("XML-RPC format error");
            return;
        }
        if (body.get(0).getQualifiedName().equals("params")) { 
            Element responseParam = body.get(0).getFirstChildElement("param");
            Element responseValue 
              = responseParam.getFirstChildElement("value");
            Element responseDouble 
              = responseValue.getFirstChildElement("double");        
            System.out.println(responseDouble.getValue());
        }
        else if (body.get(0).getQualifiedName().equals("fault")) {
            handleFault(body.get(0));
        }
        else {
            System.err.println("XML-RPC Format Error");
            return;   
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
    }
  
  }

    private static void handleFault(Element fault) {
      
        Element value =  fault.getFirstChildElement("value");      
        Element struct = value.getFirstChildElement("struct");      
        Elements members = struct.getChildElements("member");      
        Element member1 =  members.get(0);      
        Element member2 =  members.get(1);      
        String code = "";
        String detail = "";
        Element name1 = member1.getFirstChildElement("name");
        Element value1 = member1.getFirstChildElement("value");
        Element name2 = member2.getFirstChildElement("name");
        Element value2 = member2.getFirstChildElement("value");
        if (name1.getValue().equals("faultCode")) {
            code = value1.getValue();   
            detail = value2.getValue();
        }
        else if (name2.getValue().equals("faultCode")) {
            code = value2.getValue();   
            detail = value1.getValue();
        }   
        else {
            throw new RuntimeException("Incorrect fault message");  
        }
        System.err.println("Fault: ");
        System.err.println("  code: " + code);
        System.err.println("  " + detail);
    }  

}