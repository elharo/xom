// Copyright 2003 Elliotte Rusty Harold
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

package nu.xom.tests;

import nu.xom.Comment;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.Text;

/**
 * <p>
 *  Various basic tests for the <code>Nodes</code> class.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d23
 *
 */
public class NodesTest extends XOMTestCase {

    public void testNoArgsConstructor() {
        Nodes nodes = new Nodes();
        assertEquals(0, nodes.size());   
    }

    public void testOneArgConstructor() {
        Element test = new Element("test");
        Nodes nodes = new Nodes(test);
        assertEquals(1, nodes.size()); 
        Element stored = (Element) nodes.get(0);
        assertEquals(test, stored);  
    }
    
    public void testIndexOutofBoundsException() {
        Nodes nodes = new Nodes();
        try {
            Node stored = nodes.get(0);
            fail("Didn't throw IndexOutOfBoundsException for empty list");
        }
        catch (IndexOutOfBoundsException success) {
            assertNotNull(success.getMessage());   
        }  
        
        nodes.append(new Comment("data"));
        try {
            Node stored = nodes.get(-1);
            fail("Didn't throw IndexOutOfBoundsException for -1");
        }
        catch (IndexOutOfBoundsException success) {
            assertNotNull(success.getMessage());   
        }  
        try {
            Node stored = nodes.get(1);
            fail("Didn't throw IndexOutOfBoundsException for fencepost");
        }
        catch (IndexOutOfBoundsException success) {
            assertNotNull(success.getMessage());   
        }  
        
        
    }

    public void testAppendAndGet() {
        Nodes nodes = new Nodes();
        int length = 10;
        for (int i = 0; i < length; i++) {
            nodes.append(new Text(String.valueOf(i)));   
        }
        assertEquals(length, nodes.size());
        for (int i = 0; i < length; i++) {
            assertEquals(String.valueOf(i), nodes.get(i).getValue());   
        }     
    }

}
