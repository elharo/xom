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
import nu.xom.NodeFactory;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.Serializer;

/**
 * <p>
 *   A filter that converts attributes to child elements.
 *   This does not apply to namespace declaration attributes
 *   such as <code>xmlns</code> and <code>xmlns:<i>prefix</i></code>.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d23
 */
public class AttributesToElements extends NodeFactory {
    
    private boolean maintainTypes = false;
    
    public AttributesToElements() {}

    public AttributesToElements(boolean maintainTypes) {
        this.maintainTypes = maintainTypes;   
    }

    public Nodes makeAttribute(String name, String URI, 
      String value, Attribute.Type type) {
          
        Element element = new Element(name, URI);  
        element.appendChild(value);
        if (maintainTypes 
          && !type.equals(Attribute.Type.UNDECLARED)
          && !type.equals(Attribute.Type.ENUMERATION)) {
            Attribute xsiType = new Attribute("xsi:type", 
              "http://www.w3.org/2001/XMLSchema-instance", type.getName());
            element.addAttribute(xsiType); 
        }
        return new Nodes(element);
    }

    public static void main(String[] args) {
  
        if (args.length <= 0) {
          System.out.println(
            "Usage: java nu.xom.samples.AttributesToElements URL"
          );
          return;
        }
        
        try {
          Builder parser = new Builder(new AttributesToElements());
          Document doc = parser.build(args[0]);
          Serializer.write(doc, System.out);
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
