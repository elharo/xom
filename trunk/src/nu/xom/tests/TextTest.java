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

import nu.xom.Element;
import nu.xom.IllegalDataException;
import nu.xom.Text;

/**
 * 
 * <p>
 *  Basic tests for the Text class.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d22
 *
 */
public class TextTest extends XOMTestCase {

    public TextTest() {
        super("Text tests");
    }

    public TextTest(String name) {
        super(name);
    }

    public void testConstructor() {       
        Text a1 = new Text("test");
        assertEquals("test", a1.getValue());
    }

    public void testSetter() {
        
        String[] legal = {
          "Hello",
          "hello there",
          "  spaces on both ends  ",
          " quotes \" \" quotes",
          " single \'\' quotes",
          " both double and single \"\'\"\' quotes",  
          " angle brackets <  > <<<",  
          " carriage returns \r\r\r",  
          " CDATA end: ]]>",  
          " <![CDATA[ CDATA end: ]]>",  
          " &amp; ",  
          " ampersands & &&& &name; "  
        };

        Text a1 = new Text("name");
        
        // Things that shouldn't cause an exception
        for (int i = 0; i < legal.length; i++) {
            a1.setValue(legal[i]);   
            assertEquals(legal[i], a1.getValue());
        }
        
        a1.setValue(null);
        assertEquals("", a1.getValue());
        
        try {
          a1.setValue("test \u0000 test ");
          fail("Should raise an IllegalDataException");
        }
        catch (IllegalDataException success) {}


    }

    public void testToXML() {
        
        String[] easyCases = {
          "Hello",
          "hello there",
          "  spaces on both ends  ",
          " quotes \" \" quotes",
          " single \'\' quotes",
          " both double and single \"\'\"\' quotes"  
        };

        Text a1 = new Text("name");
        
        // Things that shouldn't cause an exception
        for (int i = 0; i < easyCases.length; i++) {
            a1.setValue(easyCases[i]);   
            assertEquals(easyCases[i], a1.toXML());
        }
        
        a1.setValue("<>");
        assertEquals("&lt;&gt;", a1.toXML());
        a1.setValue("&amp;");
        assertEquals("&amp;amp;", a1.toXML());
        a1.setValue("]]>");
        assertEquals("]]&gt;", a1.toXML());
        a1.setValue("\r");
        assertEquals("&#x0D;", a1.toXML());
        
    }

    public void testEquals() {
        Text c1 = new Text("test");
        Text c2 = new Text("test");
        Text c3 = new Text("skjlchsakdjh");

        assertEquals(c1, c1);
        assertEquals(c1.hashCode(), c1.hashCode());
        assertTrue(!c1.equals(c2));
        assertTrue(!c1.equals(c3));
    }

    public void testCopy() {
        Text c1 = new Text("test");
        Text c2 = (Text) c1.copy();

        assertEquals(c1.getValue(), c2.getValue());
        assertEquals(c1, c2);
        assertTrue(!c1.equals(c2));
        assertNull(c2.getParent());

    }

    // Check passing in a string with broken surrogate pairs
    // and with correct surrogate pairs
    public void testSurrogates() {

        String goodString = "test: \uD8F5\uDF80  ";
        Text c = new Text(goodString);
        assertEquals(goodString, c.getValue());

        // Two high-halves
        try {
          new Text("test: \uD8F5\uDBF0  ");
          fail("Should raise an IllegalDataException");
        }
        catch (IllegalDataException success) {}


        // Two high-halves
        try {
          new Text("test: \uD8F5\uD8F5  ");
          fail("Should raise an IllegalDataException");
        }
        catch (IllegalDataException success) {}

        // One high-half
        try {
           new Text("test: \uD8F5  ");
           fail("Should raise an IllegalDataException");
         }
         catch (IllegalDataException success) {}

        // One low half
         try {
            new Text("test: \uDF80  ");
            fail("Should raise an IllegalDataException");
          }
          catch (IllegalDataException success) {}

        // Low half before high half
         try {
            new Text("test: \uDCF5\uD8F5  ");
            fail("Should raise an IllegalDataException");
          }
          catch (IllegalDataException success) {}

    }


    public void testLeafNode() {

        Text c1 = new Text("data");
        assertEquals(0, c1.getChildCount());
        assertTrue(!c1.hasChildren());
        try {
            c1.getChild(0);
            fail("Didn't throw IndexOutofBoundsException");
        }
        catch (IndexOutOfBoundsException ex) {
            // success   
        }
        
        assertNull(c1.getParent());

        Element element = new Element("test");
        element.appendChild(c1);
        assertEquals(element, c1.getParent());
        assertEquals(c1, element.getChild(0));

        element.removeChild(c1);
        assertTrue(!element.hasChildren());

    }

    public void testToString() {
        
        Text c1 = new Text("content");
        assertEquals("[nu.xom.Text: content]", c1.toString());          
        
        c1.setValue("012345678901234567890123456789012345678901234567890123456789");
        String s = c1.toString();
        assertEquals(
          "[nu.xom.Text: 01234567890123456789012345678901234...]", 
          c1.toString()
        );          
        
    }

}
