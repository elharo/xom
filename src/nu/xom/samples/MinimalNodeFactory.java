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

import nu.xom.Attribute;
import nu.xom.Comment;
import nu.xom.DocType;
import nu.xom.Element;
import nu.xom.NodeFactory;
import nu.xom.ProcessingInstruction;
import nu.xom.Text;

/**
 * <p>
 * Demonstrates a <code>NodeFactory</code> that builds the minimum 
 * structures possible, just the document and the root element.
 * </p>
 */
public class MinimalNodeFactory extends NodeFactory {

    public Comment makeComment(String data) {
        return null;  
    }    

    public Text makeText(String data) {
        return null;  
    }    

    public Element makeRootElement(String name, String namespace) {
        return new Element(name, namespace); 
    }
    
    public Element startMakingElement(String name, String namespace) {
        Element result = new Element(name, namespace);  
        return null;    
    }

    public Attribute makeAttribute(String name, String namespace, 
      String value, Attribute.Type type) {
        return null;
    }

    public DocType makeDocType(String rootElementName, 
      String publicID, String systemID) {
        return null;    
    }

    public ProcessingInstruction makeProcessingInstruction(
      String target, String data) {
        return null; 
    }    

}
