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
import nu.xom.IllegalTargetException;
import nu.xom.ProcessingInstruction;

/**
 * @author Elliotte Rusty Harold
 * @version 1.0d18
 *
 */
public class ProcessingInstructionTest extends XOMTestCase {

    public ProcessingInstructionTest() {
        super("Processing Instruction tests");
    }

    public ProcessingInstructionTest(String name) {
        super(name);
    }
    
    private ProcessingInstruction c1;
    
    protected void setUp() {
        c1 = new ProcessingInstruction("test", "test");  
    }

    public void testToXML() {
        assertEquals("<?test test?>", c1.toXML());
    }

    public void testToString() {
        assertEquals(
          "[nu.xom.ProcessingInstruction: target=\"test\"; data=\"test\"]", 
          c1.toString());
    }

    public void testConstructor() {

        assertEquals("test", c1.getValue());
        assertEquals("test", c1.getTarget());

        try {
          new ProcessingInstruction("test:test", "test");
          fail("Processing instruction targets cannot contain colons");
        }
        catch (IllegalTargetException success) {}
        
        try {
          new ProcessingInstruction("", "test");
          fail("Processing instruction targets cannot be empty");
        }
        catch (IllegalTargetException success) {}
        
        try {
           new ProcessingInstruction(null, "test");
           fail("Processing instruction targets cannot be empty");
        }
        catch (IllegalTargetException success) {}
        
        try {
           new ProcessingInstruction("12345", "test");
           fail("Processing instruction targets must be NCNames");
        }
        catch (IllegalTargetException success) {}
        
        // test empty data allowed
        new ProcessingInstruction("test", "");
        
        // what should happen with null data????

    }

    public void testSetter() {

        try {
          c1.setValue("kjsahdj ?>");
          fail("Should raise an IllegalDataException");
        }
        catch (IllegalDataException success) {}
        try {
          c1.setValue("?>");
          fail("Should raise an IllegalDataException");
        }
        catch (IllegalDataException success) {}
        try {
          c1.setValue("kjsahdj ?> skhskjlhd");
          fail("Should raise an IllegalDataException");
        }
        catch (IllegalDataException success) {}

        // These should all work
        String[] testData = {"<html></html>",
          "name=value",
          "name='value'",
          "name=\"value\"",
          "salkdhsalkjhdkjsadhkj sadhsajkdh",
            "<?", "? >", " -- "
        };
        for (int i = 0; i < testData.length; i++) {
          c1.setValue(testData[i]);
          assertEquals(testData[i], c1.getValue());
        }

     }

    public void testNames() {
        assertEquals("test", c1.getTarget());
     }


    public void testEquals() {
        ProcessingInstruction c1 
          = new ProcessingInstruction("test", "afaf");
        ProcessingInstruction c2
          = new ProcessingInstruction("test", "afaf");
        ProcessingInstruction c3 
          = new ProcessingInstruction("tegggst", "afaf");
        ProcessingInstruction c4
          = new ProcessingInstruction("test", "1234");

        assertEquals(c1, c1);
        assertEquals(c1.hashCode(), c1.hashCode());
        assertTrue(!c1.equals(c2));
        assertTrue(!c1.equals(c3));
        assertTrue(!c3.equals(c4));
        assertTrue(!c2.equals(c4));
        assertTrue(!c2.equals(c3));
    }

    public void testCopy() {
        Element test = new Element("test");
        test.appendChild(c1);
        ProcessingInstruction c2 = (ProcessingInstruction) c1.copy();

        assertEquals(c1, c2);
        assertEquals(c1.getValue(), c2.getValue());
        assertTrue(!c1.equals(c2));
        assertNull(c2.getParent());
    }

    // Check passing in a string with broken surrogate pairs
    // and with correct surrogate pairs
    public void testSurrogates() {

        String goodString = "test: \uD8F5\uDF80  ";
        c1.setValue(goodString);
        assertEquals(goodString, c1.getValue());
        // need to add broken surrogates????
        
    }

    public void testLeafNode() {

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

}
