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

package nu.xom.tests;

import nu.xom.Text;


/**
 * <p>
 * 
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d18
 *
 */
public class LeafNodeTest extends XOMTestCase {

    public LeafNodeTest(String name) {
        super(name);
    }
    
    private Text parent;
    
    protected void setUp() {
        parent = new Text("parent");
    }
    
    public void testHasChildren() {      
        assertTrue(!parent.hasChildren());       
    } 

    public void testAppendChild() {
        assertTrue(!parent.hasChildren());
    } 

    public void testGetChild() {

        try {
            parent.getChild(0);
            fail("No index exception");   
        }
        catch (IndexOutOfBoundsException success) {
            // success
        }

    }  

    public void testGetChildCount() {

        assertEquals(0, parent.getChildCount());

    }  

}
