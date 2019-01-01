/* Copyright 2002-2006 Elliotte Rusty Harold
   
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
   subject line. The XOM home page is located at http://www.xom.nu/
*/

package nu.xom.tests;

import nu.xom.Element;
import nu.xom.IllegalCharacterDataException;
import nu.xom.Text;

/**
 * 
 * <p>
 *  Basic tests for the <code>Text</code> class.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.3.1
 *
 */
public class TextTest extends XOMTestCase {

    
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

        Text t = new Text("name");
        
        // Things that shouldn't cause an exception
        for (int i = 0; i < legal.length; i++) {
            t.setValue(legal[i]);   
            assertEquals(legal[i], t.getValue());
        }
        
        t.setValue(null);
        assertEquals("", t.getValue());
        
        try {
            t.setValue("test \u0000 test ");
            fail("Should raise an IllegalCharacterDataException");
        }
        catch (IllegalCharacterDataException success) {
            assertEquals("test \u0000 test ", success.getData());
            assertNotNull(success.getMessage());
        }

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

        Text t = new Text("name");
        
        // Things that shouldn't cause an exception
        for (int i = 0; i < easyCases.length; i++) {
            t.setValue(easyCases[i]);   
            assertEquals(easyCases[i], t.toXML());
        }
        
        t.setValue("<>");
        assertEquals("&lt;&gt;", t.toXML());
        t.setValue("&amp;");
        assertEquals("&amp;amp;", t.toXML());
        t.setValue("]]>");
        assertEquals("]]&gt;", t.toXML());
        t.setValue("\r");
        assertEquals("&#x0D;", t.toXML());
        
    }

    
    public void testPunctuationCharactersInToXML() {
        
        String data = "=,.!@#$%^*()_-\"'[]{}+/?;:`|\\";
        Text t = new Text(data);
        assertEquals(data, t.toXML());
        
    }

    
    public void testEquals() {
        
        Text c1 = new Text("test");
        Text c2 = new Text("test");
        Text c3 = new Text("skjlchsakdjh");

        assertEquals(c1, c1);
        assertEquals(c1.hashCode(), c1.hashCode());
        assertFalse(c1.equals(c2));
        assertFalse(c1.equals(c3));
        
    }

    
    public void testCopy() {
        
        Text c1 = new Text("test");
        Text c2 = c1.copy();

        assertEquals(c1.getValue(), c2.getValue());
        assertEquals(c1, c2);
        assertFalse(c1.equals(c2));
        assertNull(c2.getParent());

    }


    public void testCopyisNotACDATASection() {
        
        Text c1 = new Text("test");
        Text c2 = c1.copy();
        assertEquals(Text.class, c2.getClass());

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
          fail("Should raise an IllegalCharacterDataException");
        }
        catch (IllegalCharacterDataException success) {
            assertNotNull(success.getMessage());
            assertEquals("test: \uD8F5\uDBF0  ", success.getData());
        }

        // Two high-halves
        try {
            new Text("test: \uD8F5\uD8F5  ");
            fail("Should raise an IllegalCharacterDataException");
        }
        catch (IllegalCharacterDataException success) {
            assertEquals("test: \uD8F5\uD8F5  ", success.getData());
            assertNotNull(success.getMessage());
        }

        // One high-half
        try {
            new Text("test: \uD8F5  ");
            fail("Should raise an IllegalCharacterDataException");
        }
        catch (IllegalCharacterDataException success) {
            assertNotNull(success.getMessage());
            assertEquals("test: \uD8F5  ", success.getData());
        }

        // One low half
        try {
            new Text("test: \uDF80  ");
            fail("Should raise an IllegalCharacterDataException");
        }
        catch (IllegalCharacterDataException success) {
            assertNotNull(success.getMessage());
            assertEquals("test: \uDF80  ", success.getData());
        }

        // Low half before high half
        try {
            new Text("test: \uDCF5\uD8F5  ");
            fail("Should raise an IllegalCharacterDataException");
        }
        catch (IllegalCharacterDataException success) {
            assertEquals("test: \uDCF5\uD8F5  ", success.getData());
            assertNotNull(success.getMessage());
        }

    }

    
    public void testNonBMPText() {
        
        StringBuffer sb = new StringBuffer(2);
        for (char high = '\uD800'; high <= '\uDB7F'; high++) {
            for (char low = '\uDC00'; low <= '\uDFFF'; low++) {
                sb.setLength(0);
                sb.append(high);
                sb.append(low);
                String s = sb.toString();
                Text t = new Text(s);
                assertEquals(s, t.getValue());
            }
        }
        
    }
    
    
    public void testEndOfBMP() {
        
        try {
            new Text("\uFFFE");
            fail("allowed FFFE");
        }
        catch (IllegalCharacterDataException success) {
            assertEquals("\uFFFE", success.getData());
            assertNotNull(success.getMessage());
        }
        
        try {
            new Text("\uFFFF");
            fail("allowed FFFF");
        }
        catch (IllegalCharacterDataException success) {
            assertEquals("\uFFFF", success.getData());
            assertNotNull(success.getMessage());
        }
        
    }

    
    public void testLeafNode() {

        Text c1 = new Text("data");
        assertEquals(0, c1.getChildCount());
        try {
            c1.getChild(0);
            fail("Didn't throw IndexOutofBoundsException");
        }
        catch (IndexOutOfBoundsException success) {
            // success   
        }
        
        assertNull(c1.getParent());

        Element element = new Element("test");
        element.appendChild(c1);
        assertEquals(element, c1.getParent());
        assertEquals(c1, element.getChild(0));

        element.removeChild(c1);
        assertEquals(0, element.getChildCount());

    }

    
    public void testToStringWithLineFeed() {
        
        Text t = new Text("content\ncontent");
        assertEquals("[nu.xom.Text: content\\ncontent]", t.toString());          
        
    }


    public void testToStringWithCarriageReturn() {
        
        Text t = new Text("content\rcontent");
        assertEquals("[nu.xom.Text: content\\rcontent]", t.toString());          
        
    }


    public void testToStringWithCarriageReturnLinefeed() {
        
        Text t = new Text("content\r\ncontent");
        assertEquals("[nu.xom.Text: content\\r\\ncontent]", t.toString());          
        
    }


    public void testToStringWithTab() {
        
        Text t = new Text("content\tcontent");
        assertEquals("[nu.xom.Text: content\\tcontent]", t.toString());          
        
    }


    public void testToString() {
        
        Text t = new Text("content");
        assertEquals("[nu.xom.Text: content]", t.toString());          
        
        t.setValue("012345678901234567890123456789012345678901234567890123456789");
        assertEquals(
          "[nu.xom.Text: 01234567890123456789012345678901234...]", 
          t.toString()
        );          
        
    }

    
    // Make sure carriage returns are escaped properly by toXML()
    public void testCarriageReturnInText() {
        Text text = new Text("data\rdata");
        String xml = text.toXML();
        assertEquals("data&#x0D;data", xml);   
    }
    
    
    public void testHighSurrogateWithNoLowSurrogate() {
        
        String data = String.valueOf((char) 0xD800);
        try {
            new Text(data);
            fail("Allowed single high surrogate in text node");
        }
        catch (IllegalCharacterDataException success) {
            assertEquals(data, success.getData());
            assertNotNull(success.getMessage());
        }
        
    }


}
