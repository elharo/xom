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

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.NodeFactory;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.Serializer;

/**
 * <p>
 *   Demonstrates a custom <code>NodeFactory</code> that converts 
 *   <code>rddl:resource</code> elements to XHTML tables. 
 *   This is inspired by Example 8-11 in
 *   <cite>Processing XML with Java</cite>.
 *   In brief, it demonstrates that major modifications 
 *   may have to take place in <code>endElement</code> but can still
 *   be effectively streamed.
 * </p>
 * 
 * <p>
 *  <code>rddl:resource</code> elements are replaced by a simple table. 
 *  The various attributes of the resource are mapped to different 
 *  parts of the table. In particular, a <code>rddl:resource</code>
 *  like this:
 * </p>
 *
 * <pre><code>&lt;rddl:resource
 *        id="note-xlink2rdf"
 *       xlink:title="W3C NOTE XLink2RDF"
 *       xlink:role="http://www.w3.org/TR/html4/"
 *       xlink:arcrole="http://www.rddl.org/purposes#reference"
 *       xlink:href="http://www.w3.org/TR/xlink2rdf/"
 *       >
 *     &lt;li>
 *       &lt;a href="http://www.w3.org/TR/xlink2rdf/">
 *        W3C Note Harvesting RDF Statements from XLinks
 *      &lt;/a>
 *    &lt;/li>
 *&lt;/rddl:resource></code></pre>
 *
 * <p>will turn into an XHTML table that looks like this:</p>
 *
 *<pre><code>&lt;table id="note-xlink2rdf">
 * &lt;caption>W3C NOTE XLink2RDF&lt;/caption>
 * &lt;tr>&lt;td>Role: &lt;/td>&lt;td>http://www.w3.org/TR/html4/&lt;/td>&lt;/tr>
 * &lt;tr>&lt;td>Arcrole: &lt;/td>&lt;td>http://www.rddl.org/purposes#reference&lt;/td>&lt;/tr>
 * &lt;tr>&lt;td>Href: &lt;/td>&lt;td>&lt;a href="http://www.w3.org/TR/xlink2rdf/">
 *  http://www.w3.org/TR/xlink2rdf/&lt;/a>&lt;/td>&lt;/tr>
 * &lt;tr>
 *   &lt;td colspan="2">
 *     &lt;li>
 *       &lt;a href="http://www.w3.org/TR/xlink2rdf/">
 *         W3C Note Harvesting RDF Statements from XLinks
 *       &lt;/a>
 *     &lt;/li>
 *   &lt;/td>
 * &lt;/tr>
 *&lt;/table></code></pre>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0
 */

public class RDDLToTable extends NodeFactory {

    public final static String RDDL_NAMESPACE 
      = "http://www.rddl.org/";
    public final static String XHTML_NAMESPACE 
      = "http://www.w3.org/1999/xhtml";
    public final static String XLINK_NAMESPACE 
      = "http://www.w3.org/1999/xlink";


    public static void main(String[] args) {

        if (args.length <= 0) {
          System.out.println("Usage: java nu.xom.samples.RDDLToTable URL");
          return;
        }
        
        try {
            Builder parser = new Builder(new RDDLToTable());
            Document doc = parser.build(args[0]);
            Serializer out = new Serializer(System.out);
            out.write(doc);
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
    
    public Nodes finishMakingElement(Element element) {
        
        element.removeNamespaceDeclaration("rddl");
        element.removeNamespaceDeclaration("xlink");
        Element result = element;
        if (RDDL_NAMESPACE.equals(element.getNamespaceURI())
          && "resource".equals(element.getLocalName())) {
            Element table = new Element("table", XHTML_NAMESPACE);
            // move the content
            Element tr = new Element("tr", XHTML_NAMESPACE);
            Element td = new Element("td", XHTML_NAMESPACE);
            td.addAttribute(new Attribute("colspan", "2"));
            tr.appendChild(td);
            while (element.getChildCount() > 0) {
                Node child = element.removeChild(0);
                td.appendChild(child);   
            }
            table.appendChild(tr);
            
            Attribute href = element.getAttribute("role", XLINK_NAMESPACE);
            if (href != null) {
                element.removeAttribute(href);
                Element trhref = new Element("tr", XHTML_NAMESPACE);
                Element tdhref1 = new Element("td", XHTML_NAMESPACE);
                Element tdhref2 = new Element("td", XHTML_NAMESPACE);
                tdhref1.appendChild("href: ");
                tdhref2.appendChild(href.getValue());
                trhref.appendChild(tdhref1);
                trhref.appendChild(tdhref2);
                table.insertChild(trhref, 0); 
            }
            
            Attribute arcrole = element.getAttribute("role", XLINK_NAMESPACE);
            if (arcrole != null) {
                element.removeAttribute(arcrole);
                Element trarcrole = new Element("tr", XHTML_NAMESPACE);
                Element tdarcrole1 = new Element("td", XHTML_NAMESPACE);
                Element tdarcrole2 = new Element("td", XHTML_NAMESPACE);
                tdarcrole1.appendChild("arcrole: ");
                tdarcrole2.appendChild(arcrole.getValue());
                trarcrole.appendChild(tdarcrole1);
                trarcrole.appendChild(tdarcrole2);
                table.insertChild(trarcrole, 0); 
            }


            Attribute role = element.getAttribute("role", XLINK_NAMESPACE);
            if (role != null) {
                element.removeAttribute(role);
                Element trrole = new Element("tr", XHTML_NAMESPACE);
                Element tdrole1 = new Element("td", XHTML_NAMESPACE);
                Element tdrole2 = new Element("td", XHTML_NAMESPACE);
                tdrole1.appendChild("role: ");
                tdrole2.appendChild(role.getValue());
                trrole.appendChild(tdrole1);
                trrole.appendChild(tdrole2);
                table.insertChild(trrole, 0); 
            }
                       
            Attribute id = element.getAttribute("id");
            if (id != null) {
                element.removeAttribute(id);
                Element caption = new Element("caption", XHTML_NAMESPACE);
                caption.appendChild(id.getValue());
                table.insertChild(caption, 0); 
            }    
            result = table;
        }      
        return new Nodes(result); 
    }

    public Nodes makeDocType(String rootElementName, 
      String publicID, String systemID) {
        return new Nodes(new DocType("html", 
          "PUBLIC \"-//W3C//DTD XHTML Basic 1.0//EN\"",
          "http://www.w3.org/TR/xhtml-basic/xhtml-basic10.dtd"));    
    }

}
