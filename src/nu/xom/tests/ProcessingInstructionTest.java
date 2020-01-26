/* Copyright 2002-2004 Elliotte Rusty Harold
   
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

package nu.xom.tests;

import nu.xom.Element;
import nu.xom.IllegalDataException;
import nu.xom.IllegalTargetException;
import nu.xom.ProcessingInstruction;

/**
 * <p>
 * Unit tests for the <code>ProcessingInstruction</code> class.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.3.0
 *
 */
public class ProcessingInstructionTest extends XOMTestCase {

    
    public ProcessingInstructionTest(String name) {
        super(name);
    }
    
    
    private ProcessingInstruction pi;
    
    
    protected void setUp() {
        pi = new ProcessingInstruction("test", "test");  
    }

    
    public void testToXML() {
        assertEquals("<?test test?>", pi.toXML());
    }

    
    public void testToString() {
        assertEquals(
          "[nu.xom.ProcessingInstruction: target=\"test\"; data=\"test\"]", 
          pi.toString());
    }

    
    public void testToStringWithLineFeed() {
        
        ProcessingInstruction p 
          = new ProcessingInstruction("test", "content\ncontent");
        assertEquals(
          "[nu.xom.ProcessingInstruction: target=\"test\"; data=\"content\\ncontent\"]", 
          p.toString()
        );          
        
    }
    
    
    public void testToStringWithLotsOfData() {
        
        ProcessingInstruction p 
          = new ProcessingInstruction("target", 
          "content content 012345678901234567890123456789012345678901234567890123456789");
        String s = p.toString();
        assertTrue(s.endsWith("...\"]"));
        
    }


    public void testConstructor() {

        assertEquals("test", pi.getValue());
        assertEquals("test", pi.getTarget());

        try {
          new ProcessingInstruction("test:test", "test");
          fail("Processing instruction targets cannot contain colons");
        }
        catch (IllegalTargetException success) {
            assertNotNull(success.getMessage());
            assertEquals("test:test", success.getData());
        }
        
        try {
          new ProcessingInstruction("", "test");
          fail("Processing instruction targets cannot be empty");
        }
        catch (IllegalTargetException success) {
            assertNotNull(success.getMessage());
            assertEquals("", success.getData());
        }
        
        try {
           new ProcessingInstruction(null, "test");
           fail("Processing instruction targets cannot be empty");
        }
        catch (IllegalTargetException success) {
            assertNotNull(success.getMessage());
            assertNull(success.getData());
        }
        
        try {
           new ProcessingInstruction("12345", "test");
           fail("Processing instruction targets must be NCNames");
        }
        catch (IllegalTargetException success) {
            assertEquals("12345", success.getData());            
        }
        
        // test empty data allowed
        pi = new ProcessingInstruction("test", "");
        assertEquals("", pi.getValue());
        assertEquals("<?test?>", pi.toXML());

    }
    

    public void testSetTarget() {

        try {
          pi.setTarget("test:test");
          fail("Processing instruction targets cannot contain colons");
        }
        catch (IllegalTargetException success) {
            assertNotNull(success.getMessage());
            assertEquals("test:test", success.getData());
        }
        
        try {
          pi.setTarget("");
          fail("Processing instruction targets cannot be empty");
        }
        catch (IllegalTargetException success) {
            assertNotNull(success.getMessage());
            assertEquals("", success.getData());
        }
        
        try {
           pi.setTarget(null);
           fail("Processing instruction targets cannot be empty");
        }
        catch (IllegalTargetException success) {
            assertNotNull(success.getMessage());
            assertNull(success.getData());
        }
        
        try {
           pi.setTarget("12345");
           fail("Processing instruction targets must be NCNames");
        }
        catch (IllegalTargetException success) {
            assertEquals("12345", success.getData());            
        }
        
        pi.setTarget("testing123");
        assertEquals("testing123", pi.getTarget());

    }    
    
    
    public void testCopyConstructor() {
        
        ProcessingInstruction instruction1 = new ProcessingInstruction("target", "data");   
        ProcessingInstruction instruction2 = new ProcessingInstruction(instruction1);
        
        assertEquals(instruction1.getTarget(), instruction2.getTarget());
        assertEquals(instruction1.getValue(), instruction2.getValue());
        assertEquals(instruction1.toXML(), instruction2.toXML());
           
    }

    
    public void testSetValue() {

        try {
          pi.setValue("kjsahdj ?>");
          fail("Should raise an IllegalDataException");
        }
        catch (IllegalDataException success) {
            assertEquals("kjsahdj ?>", success.getData());
            assertNotNull(success.getMessage());
        }
        
        try {
          pi.setValue("?>");
          fail("Should raise an IllegalDataException");
        }
        catch (IllegalDataException success) {
            assertEquals("?>", success.getData());
            assertNotNull(success.getMessage());
        }
        
        try {
          pi.setValue("kjsahdj ?> skhskjlhd");
          fail("Should raise an IllegalDataException");
        }
        catch (IllegalDataException success) {
            assertEquals("kjsahdj ?> skhskjlhd", success.getData());
            assertNotNull(success.getMessage());
        }
        
        try {
            pi.setValue(null);
            fail("Allowed null data");   
        }
        catch (IllegalDataException success) {
            assertNull(success.getData());
            assertNotNull(success.getMessage());
        }
        
        
        // These should all work
        String[] testData = {"<html></html>",
          "name=value",
          "name='value'",
          "name=\"value\"",
          "salkdhsalkjhdkjsadhkj sadhsajkdh",
            "<?", "? >", "--"
        };
        for (int i = 0; i < testData.length; i++) {
          pi.setValue(testData[i]);
          assertEquals(testData[i], pi.getValue());
        }

    }

    
    public void testNames() {
        assertEquals("test", pi.getTarget());
    }


    public void testEquals() {
        
        ProcessingInstruction pi1 
          = new ProcessingInstruction("test", "afaf");
        ProcessingInstruction pi2
          = new ProcessingInstruction("test", "afaf");
        ProcessingInstruction pi3 
          = new ProcessingInstruction("tegggst", "afaf");
        ProcessingInstruction pi4
          = new ProcessingInstruction("test", "1234");

        assertEquals(pi1, pi1);
        assertEquals(pi1.hashCode(), pi1.hashCode());
        assertTrue(!pi1.equals(pi2));
        assertTrue(!pi1.equals(pi3));
        assertTrue(!pi3.equals(pi4));
        assertTrue(!pi2.equals(pi4));
        assertTrue(!pi2.equals(pi3));
        
    }

    
    public void testCopy() {
        
        Element test = new Element("test");
        test.appendChild(pi);
        ProcessingInstruction c2 = pi.copy();

        assertEquals(pi, c2);
        assertEquals(pi.getValue(), c2.getValue());
        assertTrue(!pi.equals(c2));
        assertNull(c2.getParent());
        
    }

    
    // Check passing in a string with correct surrogate pairs
    public void testCorrectSurrogates() {
        
        String goodString = "test: \uD8F5\uDF80  ";
        pi.setValue(goodString);
        assertEquals(goodString, pi.getValue());
        
    }

    
    // Check passing in a string with broken surrogate pairs
    public void testSurrogates() {

        try {
            pi.setValue("test \uD8F5\uD8F5 test");
            fail("Allowed two high halves");
        }
        catch (IllegalDataException success) {
            assertEquals("test \uD8F5\uD8F5 test", success.getData());
            assertNotNull(success.getMessage());
        }
        
        try {
            pi.setValue("test \uDF80\uDF80 test");
            fail("Allowed two low halves");
        }
        catch (IllegalDataException success) {
            assertEquals("test \uDF80\uDF80 test", success.getData());
            assertNotNull(success.getMessage());
        }
        
        try {
            pi.setValue("test \uD8F5 \uDF80 test");
            fail("Allowed two halves split by space");
        }
        catch (IllegalDataException success) {
            assertEquals("test \uD8F5 \uDF80 test", success.getData());
            assertNotNull(success.getMessage());
        }

        try {
            pi.setValue("test \uDF80\uD8F5 test");
            fail("Allowed reversed pair");
        }
        catch (IllegalDataException success) {
            assertEquals("test \uDF80\uD8F5 test", success.getData());
            assertNotNull(success.getMessage());
        }
        
    }

    
    public void testLeafNode() {

        assertEquals(0, pi.getChildCount());
        try {
            pi.getChild(0);
            fail("Didn't throw IndexOutofBoundsException");
        }
        catch (IndexOutOfBoundsException success) {
            assertNotNull(success.getMessage()); 
        }
        
        assertNull(pi.getParent());

        Element element = new Element("test");
        element.appendChild(pi); 
        assertEquals(element, pi.getParent());
        assertEquals(pi, element.getChild(0));

        element.removeChild(pi);
        assertEquals(0, element.getChildCount());

    }

    
    // This is a problem becuase it cannot be serialized
    // since character and entity references aren't
    // recognized in comment data
    public void testCarriageReturnInProcessingInstructionData() {
        
        try {
            new ProcessingInstruction("target", "data\rdata");
            fail("Allowed carriage return in processing instruction data");
        }
        catch (IllegalDataException success) {
            assertEquals("data\rdata", success.getData());
            assertNotNull(success.getMessage());   
        }   
        
    }

    
    public void testAllowReservedCharactersInData() {
        ProcessingInstruction pi = new ProcessingInstruction("target", "<test>&amp;&greater;");
        String xml = pi.toXML();
        assertEquals("<?target <test>&amp;&greater;?>", xml);  
    }
    
    
    // This can't be round-tripped
    public void testNoInitialWhiteSpace() {
        
        try {
            new ProcessingInstruction("target", "   initial spaces"); 
            fail("allowed processing instruction data with leading space");
        }
        catch (IllegalDataException success) {
            assertEquals("   initial spaces", success.getData());
            assertNotNull(success.getMessage());   
        }   
        
        try {
            new ProcessingInstruction("target", "\tinitial tab"); 
            fail("allowed processing instruction data with leading space");
        }
        catch (IllegalDataException success) {
            assertEquals("\tinitial tab", success.getData());
            assertNotNull(success.getMessage());   
        }   
        
        try {
            new ProcessingInstruction("target", "\ninitial linefeed"); 
            fail("allowed processing instruction data with leading space");
        }
        catch (IllegalDataException success) {
            assertEquals("\ninitial linefeed", success.getData());
            assertNotNull(success.getMessage());   
        }   
        
        try {
            new ProcessingInstruction("target", "\r initial carriage return"); 
            fail("allowed processing instruction data with leading space");
        }
        catch (IllegalDataException success) {
            assertEquals("\r initial carriage return", success.getData());
            assertNotNull(success.getMessage());   
        }    
        
    }
    
    
    public void testNoXMLTargets() {

        try {
            new ProcessingInstruction("xml", "data"); 
            fail("allowed processing instruction with target xml");
        }
        catch (IllegalTargetException success) {
            assertEquals("xml", success.getData());
            assertNotNull(success.getMessage());   
        }

        try {
            new ProcessingInstruction("XML", "data"); 
            fail("allowed processing instruction with target XML");
        }
        catch (IllegalTargetException success) {
            assertEquals("XML", success.getData());
            assertNotNull(success.getMessage());   
        }

        try {
            new ProcessingInstruction("Xml", "data"); 
            fail("allowed processing instruction with target Xml");
        }
        catch (IllegalTargetException success) {
            assertEquals("Xml", success.getData());
            assertNotNull(success.getMessage());   
        }
        
    }

    
    public void testColonsNotAllowedInTargets() {

        try {
            new ProcessingInstruction("pre:target", "data"); 
            fail("allowed processing instruction with target that uses a prefixed name");
        }
        catch (IllegalTargetException success) {
            assertEquals("pre:target", success.getData());
            assertNotNull(success.getMessage());   
        }

        try {
            new ProcessingInstruction("pre:", "data"); 
            fail("allowed processing instruction with trailing colon in target");
        }
        catch (IllegalTargetException success) {
            assertEquals("pre:", success.getData());
            assertNotNull(success.getMessage());   
        }

        try {
            new ProcessingInstruction(":target", "data"); 
            fail("allowed processing instruction with initial colon in target");
        }
        catch (IllegalTargetException success) {
            assertEquals(":target", success.getData());
            assertNotNull(success.getMessage());   
        }
        
    }


}
