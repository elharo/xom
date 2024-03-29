/* Copyright 2002, 2003 Elliotte Rusty Harold
   
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

import nu.xom.Attribute;
import nu.xom.Element;

/**
 * <p>
 *   This class provides unit tests for the 
 *   <code>Attributes</code> class.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0
 *
 */
public class AttributesTest extends XOMTestCase {

    public AttributesTest(String name) {
        super(name);
    }

    private Element threeAttributes;
    private Element noAttributes;


    protected void setUp() {
        noAttributes = new Element("test");
        threeAttributes = new Element("test");
        threeAttributes.addAttribute(new Attribute("att1", "value1"));
        threeAttributes.addAttribute(new Attribute("att2", "value2"));
        threeAttributes.addAttribute(new Attribute("att3", "value3"));
    }

    public void testSize() {
        assertEquals(0, noAttributes.getAttributeCount());
        assertEquals(3, threeAttributes.getAttributeCount());
    }

    public void testGetOutOfBounds() {
    
        try {
            noAttributes.getAttribute(0);
            fail("Should have thrown IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException success) {
            assertNotNull(success.getMessage());  
        }
        try {
            threeAttributes.getAttribute(4);
        }
        catch (IndexOutOfBoundsException success) {
            assertNotNull(success.getMessage());   
        }
        try {
            threeAttributes.getAttribute(-1);
        }
        catch (IndexOutOfBoundsException success) {
            assertNotNull(success.getMessage());   
        }

    }

}