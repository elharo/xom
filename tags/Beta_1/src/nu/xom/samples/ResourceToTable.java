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

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.ParentNode;
import nu.xom.ParsingException;

/**
 * <p>
 *   Demonstrates replacing elements in a document
 *   with different elements that contain the same content.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d22
 *
 */
public class ResourceToTable {

    public final static String XHTML_NAMESPACE 
      = "http://www.w3.org/1999/xhtml";
    public final static String RDDL_NAMESPACE 
       = "http://www.rddl.org/";
    public final static String XLINK_NAMESPACE 
      = "http://www.w3.org/1999/xlink";

    public static void main(String[] args) {

        if (args.length <= 0) {
          System.out.println("Usage: java nu.xom.samples.ResourceToTable URL");
          return;
        }
        
        try {
            Builder parser = new Builder();
            Document doc = parser.build(args[0]);
            Element root = doc.getRootElement();
            if (root.getNamespaceURI().equals(XHTML_NAMESPACE)) {
                convert(root);
            }
            else {
                System.out.println(args[0] + " does not appear to be an XHTML document");
                return;   
            }
            System.out.println(doc.toXML());
        }
        catch (ParsingException ex) {
          System.out.println(args[0] + " is not well-formed.");
        }
        catch (IOException ex) { 
          System.out.println(
           "Due to an IOException, the parser could not read " 
           + args[0]
          ); 
        }      
        
    }
    
    public static void convert(Element element) {
        
       if (element.getNamespaceURI().equals(RDDL_NAMESPACE)) {
            
            Element table = new Element("table", XHTML_NAMESPACE);
            
/* <rddl:resource
        id="note-xlink2rdf"
        xlink:title="W3C NOTE XLink2RDF"
        xlink:role="http://www.w3.org/TR/html4/"
        xlink:arcrole="http://www.rddl.org/purposes#reference"
        xlink:href="http://www.w3.org/TR/xlink2rdf/"
        >
      <li>
        <a href="http://www.w3.org/TR/xlink2rdf/">
          W3C Note Harvesting RDF Statements from XLinks
        </a>
      </li>
</rddl:resource>

will turn into an XHTML table that looks like this:

<table id="note-xlink2rdf">
  <caption>W3C NOTE XLink2RDF</caption>
  <tr><td>Role: </td><td>http://www.w3.org/TR/html4/</td></tr>
  <tr><td>Arcrole: </td><td>http://www.rddl.org/purposes#reference</td></tr>
  <tr><td>Href: </td><td><a href="http://www.w3.org/TR/xlink2rdf/">
   http://www.w3.org/TR/xlink2rdf/</a></td></tr>
  <tr>
    <td colspan="2">
      <li>
        <a href="http://www.w3.org/TR/xlink2rdf/">
          W3C Note Harvesting RDF Statements from XLinks
        </a>
      </li>
    </td>
  </tr>
</table> */

            Attribute role = element.getAttribute("role", XLINK_NAMESPACE);            
            if (role != null) {
                Element tr = new Element("tr", XHTML_NAMESPACE);
                Element td1 = new Element("td", XHTML_NAMESPACE);
                tr.appendChild(td1);
                td1.appendChild("Role: ");
                Element td2 = new Element("td", XHTML_NAMESPACE);
                tr.appendChild(td2);
                td2.appendChild(role.getValue());
                table.insertChild(tr, 0);
            }    
            Attribute arcrole = element.getAttribute("arcrole", XLINK_NAMESPACE);            
            if (arcrole != null) {
                Element tr = new Element("tr", XHTML_NAMESPACE);
                Element td1 = new Element("td", XHTML_NAMESPACE);
                tr.appendChild(td1);
                td1.appendChild("Arcrole: ");
                Element td2 = new Element("td", XHTML_NAMESPACE);
                tr.appendChild(td2);
                td2.appendChild(arcrole.getValue());
                table.insertChild(tr, 0);
            }    
            Attribute href = element.getAttribute("href", XLINK_NAMESPACE);            
            if (href != null) {
                Element tr = new Element("tr", XHTML_NAMESPACE);
                Element td1 = new Element("td", XHTML_NAMESPACE);
                tr.appendChild(td1);
                td1.appendChild("Href: ");
                Element td2 = new Element("td", XHTML_NAMESPACE);
                tr.appendChild(td2);
                Element a = new Element("a", XHTML_NAMESPACE);
                a.appendChild(href.getValue());
                td2.appendChild(a);
                a.addAttribute(new Attribute("href", href.getValue()));
                table.insertChild(tr, 0);
            }    
            Attribute title = element.getAttribute("title", XLINK_NAMESPACE);            
            if (title != null) {
                Element caption = new Element("caption", XHTML_NAMESPACE);
                caption.appendChild(title.getValue());
                table.insertChild(caption, 0);
            }    
              
              
            // Move children into a td  
            Element tr = new Element("tr", XHTML_NAMESPACE);
            Element td = new Element("td", XHTML_NAMESPACE);
            td.addAttribute(new Attribute("colspan", "2"));
            tr.appendChild(td);
            while (element.getChildCount() > 0) {
                Node child = element.getChild(0);
                child.detach();
                td.appendChild(child);
                if (child instanceof Element) convert((Element) child);
            }        
            table.appendChild(tr);  
            
            ParentNode parent = element.getParent();
            parent.replaceChild(element, table);
                
        }
        else {
            // Strip out additional namespaces
            for (int i = 0; i < element.getNamespaceDeclarationCount(); i++) {
                String prefix = element.getNamespacePrefix(i);
                element.removeNamespaceDeclaration(prefix);  
            }

            Elements elements = element.getChildElements();      
            for (int i = 0; i < elements.size(); i++) {
              convert(elements.get(i));
            }
                
        }            
        
    } 
    
}