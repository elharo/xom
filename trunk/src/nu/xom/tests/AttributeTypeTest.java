/* Copyright 2003, 2006 Elliotte Rusty Harold
   
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
   elharo@metalab.unc.edu. Please include the word "XOM" in the
   subject line. The XOM home page is located at http://www.xom.nu/
*/
package nu.xom.tests;

import nu.xom.Attribute;
import nu.xom.Text;

/**
 * <p>
 *   Tests for the <code>Attribute.Type</code> inner class.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.2d1
 *
 */
public class AttributeTypeTest extends XOMTestCase {

    
    public AttributeTypeTest(String name) {
        super(name);   
    }

    
    public void testEquals() {
        
        assertEquals(Attribute.Type.CDATA,       Attribute.Type.CDATA);
        assertEquals(Attribute.Type.ID,          Attribute.Type.ID);
        assertEquals(Attribute.Type.IDREF,       Attribute.Type.IDREF);
        assertEquals(Attribute.Type.IDREFS,      Attribute.Type.IDREFS);
        assertEquals(Attribute.Type.UNDECLARED,  Attribute.Type.UNDECLARED);
        assertEquals(Attribute.Type.NMTOKEN,     Attribute.Type.NMTOKEN);
        assertEquals(Attribute.Type.NMTOKENS,    Attribute.Type.NMTOKENS);
        assertEquals(Attribute.Type.NOTATION,    Attribute.Type.NOTATION);
        assertEquals(Attribute.Type.ENTITY,      Attribute.Type.ENTITY);
        assertEquals(Attribute.Type.ENTITIES,    Attribute.Type.ENTITIES);
        assertNotSame(Attribute.Type.ENTITIES,   Attribute.Type.ENTITY);
        assertNotSame(Attribute.Type.CDATA,      Attribute.Type.ID);
        assertNotSame(Attribute.Type.ID,         Attribute.Type.IDREF);
        assertNotSame(Attribute.Type.ID,         Attribute.Type.IDREFS);
        assertNotSame(Attribute.Type.ID,         Attribute.Type.NMTOKEN);
        assertNotSame(Attribute.Type.ID,         Attribute.Type.NMTOKENS);
        assertNotSame(Attribute.Type.UNDECLARED, Attribute.Type.CDATA);
        assertNotSame(Attribute.Type.NMTOKEN,    Attribute.Type.CDATA);
        
        assertFalse(Attribute.Type.CDATA.equals(new Integer(1)));
        assertFalse(Attribute.Type.CDATA.equals(new Text("data")));
        
    }

    
    public void testToString() {
        
        assertNotNull(Attribute.Type.CDATA.toString());
        assertNotNull(Attribute.Type.ID.toString());
        assertNotNull(Attribute.Type.IDREF.toString());
        assertNotNull(Attribute.Type.IDREFS.toString());
        assertNotNull(Attribute.Type.UNDECLARED.toString());
        assertNotNull(Attribute.Type.NMTOKEN.toString());
        assertNotNull(Attribute.Type.NMTOKENS.toString());
        assertNotNull(Attribute.Type.NOTATION.toString());
        assertNotNull(Attribute.Type.ENTITY.toString());
        assertNotNull(Attribute.Type.ENTITIES.toString());

        assertTrue(Attribute.Type.CDATA.toString().length() > 10);
        assertTrue(Attribute.Type.ID.toString().length() > 10);
        assertTrue(Attribute.Type.IDREF.toString().length() > 10);
        assertTrue(Attribute.Type.IDREFS.toString().length() > 10);
        assertTrue(Attribute.Type.UNDECLARED.toString().length() > 10);
        assertTrue(Attribute.Type.NMTOKEN.toString().length() > 10);
        assertTrue(Attribute.Type.NMTOKENS.toString().length() > 10);
        assertTrue(Attribute.Type.NOTATION.toString().length() > 10);
        assertTrue(Attribute.Type.ENTITY.toString().length() > 10);
        assertTrue(Attribute.Type.ENTITIES.toString().length() > 10);

        assertTrue(Attribute.Type.CDATA.toString().startsWith("[Attribute.Type"));
        assertTrue(Attribute.Type.ID.toString().startsWith("[Attribute.Type"));
        assertTrue(Attribute.Type.IDREF.toString().startsWith("[Attribute.Type"));
        assertTrue(Attribute.Type.IDREFS.toString().startsWith("[Attribute.Type"));
        assertTrue(Attribute.Type.UNDECLARED.toString().startsWith("[Attribute.Type"));
        assertTrue(Attribute.Type.NMTOKEN.toString().startsWith("[Attribute.Type"));
        assertTrue(Attribute.Type.NMTOKENS.toString().startsWith("[Attribute.Type"));
        assertTrue(Attribute.Type.NOTATION.toString().startsWith("[Attribute.Type"));
        assertTrue(Attribute.Type.ENTITY.toString().startsWith("[Attribute.Type"));
        assertTrue(Attribute.Type.ENTITIES.toString().startsWith("[Attribute.Type"));

        assertTrue(Attribute.Type.CDATA.toString().endsWith("]"));
        assertTrue(Attribute.Type.ID.toString().endsWith("]"));
        assertTrue(Attribute.Type.IDREF.toString().endsWith("]"));
        assertTrue(Attribute.Type.IDREFS.toString().endsWith("]"));
        assertTrue(Attribute.Type.UNDECLARED.toString().endsWith("]"));
        assertTrue(Attribute.Type.NMTOKEN.toString().endsWith("]"));
        assertTrue(Attribute.Type.NMTOKENS.toString().endsWith("]"));
        assertTrue(Attribute.Type.NOTATION.toString().endsWith("]"));
        assertTrue(Attribute.Type.ENTITY.toString().endsWith("]"));
        assertTrue(Attribute.Type.ENTITIES.toString().endsWith("]"));

    }

    
    public void testGetName() {
        
        assertEquals("ENUMERATION", Attribute.Type.ENUMERATION.getName());
        assertEquals("NOTATION", Attribute.Type.NOTATION.getName());
        assertEquals("ENTITY", Attribute.Type.ENTITY.getName());
        assertEquals("ENTITIES", Attribute.Type.ENTITIES.getName());
        assertEquals("CDATA", Attribute.Type.CDATA.getName());
        assertEquals("ID", Attribute.Type.ID.getName());
        assertEquals("IDREF", Attribute.Type.IDREF.getName());
        assertEquals("IDREFS", Attribute.Type.IDREFS.getName());
        assertEquals("NMTOKEN", Attribute.Type.NMTOKEN.getName());
        assertEquals("NMTOKENS", Attribute.Type.NMTOKENS.getName());
    }

    
}
