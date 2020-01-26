/* Copyright 2002, 2003 2006 Elliotte Rusty Harold
   
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
   subject line. The XOM home page is located at https://xom.nu/
*/

package nu.xom.samples;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;


/**
 * <p>
 *   Demonstrates recursive descent through an XML document,
 *   and the getter methods of the <code>Element</code> 
 *   and <code>Attribute</code> classes.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.2b2
 *
 */
public class PropertyPrinter {

    private Writer out;
    
    public PropertyPrinter(Writer out) {
      if (out == null) {
      throw new NullPointerException("Writer must be non-null.");
      }
      this.out = out;
    }
    
    public PropertyPrinter() {
      this(new OutputStreamWriter(System.out));
    }
    
    private int nodeCount = 0;
    
    public void writeNode(Node node) throws IOException {
      
        if (node == null) {
            throw new NullPointerException("Node must be non-null.");
        }
        if (node instanceof Document) { 
            // starting a new document, reset the node count
            nodeCount = 1; 
        }
      
        String type      = node.getClass().getName(); // never null
        String value     = node.getValue();
        
        String name      = null; 
        String localName = null;
        String uri       = null;
        String prefix    = null;
        /* This is the one example I found where
         * it seemed like it might make sense to
         * move the namespace methods into Node or another
         * common interface. However, this is a pretty 
         * artificial example, so I decided not to.
         */
        if (node instanceof Element) {
            Element element = (Element) node;
            name = element.getQualifiedName();
            localName = element.getLocalName();
            uri = element.getNamespaceURI();
            prefix = element.getNamespacePrefix();
        }
        else if (node instanceof Attribute) {
            Attribute attribute = (Attribute) node;
            name      = attribute.getQualifiedName();
            localName = attribute.getLocalName();
            uri       = attribute.getNamespaceURI();
            prefix    = attribute.getNamespacePrefix();
        }

      
        StringBuffer result = new StringBuffer();
        result.append("Node " + nodeCount + ":\r\n");
        result.append("  Type: " + type + "\r\n");
        if (name != null) {
            result.append("  Name: " + name + "\r\n");
        }
        if (localName != null) {
            result.append("  Local Name: " + localName + "\r\n");
        }
        if (prefix != null) {
            result.append("  Prefix: " + prefix + "\r\n");
        }
        if (uri != null) {
            result.append("  Namespace URI: " + uri + "\r\n");
        }
        if (value != null) {
            result.append("  Value: " + value + "\r\n");
        }
      
        out.write(result.toString());
        out.write("\r\n");
        out.flush();
      
        nodeCount++;
      
    }

}
