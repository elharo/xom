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

package nu.xom.tests;

import nu.xom.Text;


/**
 * <p>
 * 
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0
 *
 */
public class LeafNodeTest extends XOMTestCase {

    
    public LeafNodeTest(String name) {
        super(name);
    }
    
    
    private Text leaf;
    
    
    protected void setUp() {
        leaf = new Text("parent");
    }
    
    
    public void testHasChildren() {      
        assertEquals(0, leaf.getChildCount());       
    } 

    
    public void testGetChild() {

        try {
            leaf.getChild(0);
            fail("No index exception");   
        }
        catch (IndexOutOfBoundsException success) {
            // success
        }

    }  

    
    public void testGetChildCount() {
        assertEquals(0, leaf.getChildCount());
    }  

}
