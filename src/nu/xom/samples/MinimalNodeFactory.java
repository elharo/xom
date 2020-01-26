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
   subject line. The XOM home page is located at https://xom.nu/
*/

package nu.xom.samples;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.NodeFactory;
import nu.xom.Nodes;
/**
 * <p>
 * Demonstrates a <code>NodeFactory</code> that builds the minimum 
 * structures possible, just the document and the root element.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0
 * 
 */
public class MinimalNodeFactory extends NodeFactory {

    private Nodes empty = new Nodes();

    public Nodes makeComment(String data) {
        return empty;  
    }    

    public Nodes makeText(String data) {
        return empty;  
    }    

    public Element makeRootElement(String name, String namespace) {
        return new Element(name, namespace); 
    }
    
    public Element startMakingElement(String name, String namespace) {
        return null;    
    }

    public Nodes makeAttribute(String name, String namespace, 
      String value, Attribute.Type type) {
        return empty;
    }

    public Nodes makeDocType(String rootElementName, 
      String publicID, String systemID) {
        return empty;    
    }

    public Nodes makeProcessingInstruction(
      String target, String data) {
        return empty; 
    }    

}
