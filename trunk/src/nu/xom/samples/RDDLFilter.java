/* Copyright 2002-2004 Elliotte Rusty Harold
   
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

import nu.xom.Builder;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.NodeFactory;
import nu.xom.Nodes;
import nu.xom.ParsingException;

/**
 * <p>
 *   Demonstrates using the <code>Builder</code> and a custom 
 *   <code>NodeFactory</code> to filter out start-tags and 
 *   end-tags while leaving the content intact. Specifically,
 *   we filter out all the elements in the RDDL namespace.
 * </p>
 * 
 *  @author Elliotte Rusty Harold
 *  @version 1.0d23
 * 
 */
public class RDDLFilter extends NodeFactory {

    public final static String RDDL_NAMESPACE 
       = "http://www.rddl.org/";

    public Element startMakingElement(String name, String namespace) {
        if (namespace.equals(RDDL_NAMESPACE)) return null;
        return new Element(name, namespace);    
    }

    public Nodes finishMakingElement(Element element) {
        element.removeNamespaceDeclaration("rddl");
        return new Nodes(element);    
    }

    // change the DOCTYPE to XHTML Basic DOCTYPE
    public Nodes makeDocType(String rootElementName, 
      String publicID, String systemID) {
        return new Nodes(new DocType("html", 
          "PUBLIC \"-//W3C//DTD XHTML Basic 1.0//EN\"",
          "http://www.w3.org/TR/xhtml-basic/xhtml-basic10.dtd"));    
    }

    public static void main(String[] args) {
  
        if (args.length <= 0) {
          System.out.println("Usage: java nu.xom.samples.RDDLFilter URL");
          return;
        }
        
        try {
          Builder parser = new Builder(new RDDLFilter());
          Document doc = parser.build(args[0]);
          System.out.println(doc.toXML());
        }
        catch (ParsingException ex) {
          System.out.println(args[0] + " is not well-formed.");
          System.out.println(ex.getMessage());
        }
        catch (IOException ex) { 
          System.out.println(
           "Due to an IOException, the parser could not read " 
           + args[0]
          ); 
        }
  
    }

}