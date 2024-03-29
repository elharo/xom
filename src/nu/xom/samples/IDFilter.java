/* Copyright 2002-2004 Elliotte Rusty Harold
   
   This library is free software; you can redistribute it and/or modify
   it under the terms of version 2.1 of the GNU Lesser General Public 
   License as published by the Free Software Foundation.
   
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
   GNU Lesser General Public License for more details.
   
   You should have received a copy of the GNU Lesser General Public
   License along with this library. If not, see
   <https://www.gnu.org/licenses/>.
   
   You can contact Elliotte Rusty Harold by sending e-mail to
   elharo@ibiblio.org. Please include the word "XOM" in the
   subject line. The XOM home page is located at https://xom.nu/
*/

package nu.xom.samples;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.NodeFactory;
import nu.xom.Nodes;

/**
 * <p>
 *   Demonstrates a custom <code>NodeFactory</code> that adds 
 *   ID attributes to all elements that don't already have one. 
 *   This is inspired by Example 8-12 in
 *   <cite>Processing XML with Java</cite>.
 *   In brief, it demonstrates that major modifications 
 *   may have to take place in <code>endElement</code> but can still
 *   be effectively streamed.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0
 *
 */
public class IDFilter extends NodeFactory {

    private int counter = 0;
    
    public Document startMakingDocument() {
        counter = 0;
        return super.startMakingDocument();
    }

    public Nodes finishMakingElement(Element element) {
        Attribute id = element.getAttribute("id");
        if (id == null) {
            id = new Attribute("id", "p" + counter);
            element.addAttribute(id);   
        }  
        counter++;
        return new Nodes(element);
    }

}
