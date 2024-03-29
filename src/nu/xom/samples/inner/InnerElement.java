/* Copyright 2005 Elliotte Rusty Harold
   
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

package nu.xom.samples.inner;

import java.io.IOException;

import nu.xom.*;

public class InnerElement extends Element {
    
    
    private static ThreadLocal<Builder> builders = new ThreadLocal<Builder>() {
        
         protected synchronized Builder initialValue() {
             return new Builder(new InnerFactory());
         }
         
     };
    
    
    public InnerElement(String name) {
        super(name);
    }

    
    public InnerElement(String namespace, String name) {
        super(namespace, name);
    }

    
    public InnerElement(Element element) {
        super(element);
    }

    
    public String getInnerXML() {
        
    	StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getChildCount(); i++) {
            sb.append(getChild(i).toXML());
        }
        return sb.toString();
        
    }

    
    public void setInnerXML(String xml) throws ParsingException {

        xml = "<fakeRoot>"
          + xml + "</fakeRoot>";
        Document doc;
        try {
            doc = builders.get().build(xml, null);
        }
        catch (IOException ex) {
            throw new ParsingException(ex.getMessage(), ex);
        }
        this.removeChildren();
        Nodes children = doc.getRootElement().removeChildren();
        for (Node child : children) {
            this.appendChild(child);
        }
        
    }

    
    public Element copy() {
        return new InnerElement(this);
    }
    
   
}
