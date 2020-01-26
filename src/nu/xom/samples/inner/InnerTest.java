/* Copyright 2005 Elliotte Rusty Harold
   
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

package nu.xom.samples.inner;

import java.io.IOException;

import junit.framework.TestCase;
import nu.xom.*;


public class InnerTest extends TestCase {

    private Builder builder = new Builder(new InnerFactory());
    
    public void testGetInner() throws ValidityException, ParsingException, IOException {
        Document doc = builder.build("<root><a>test</a><b>test2</b></root>", null);
        InnerElement root = (InnerElement) doc.getRootElement();
        assertEquals("<a>test</a><b>test2</b>", root.getInnerXML());
    }
    
    public void testSetInner() throws ValidityException, ParsingException, IOException {
        Document doc = builder.build("<root><a>test</a><b>test2</b></root>", null);
        InnerElement root = (InnerElement) doc.getRootElement();
        root.setInnerXML("a<c/>d");
        assertEquals("a<c />d", root.getInnerXML());
    }
    
    public void testGetInnerMixed() throws ValidityException, ParsingException, IOException {
        Document doc = builder.build("<root><a>test</a>test<b>test2</b></root>", null);
        InnerElement root = (InnerElement) doc.getRootElement();
        assertEquals("<a>test</a>test<b>test2</b>", root.getInnerXML());
    }
    
    
}
