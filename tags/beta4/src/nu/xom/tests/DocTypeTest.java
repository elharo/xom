// Copyright 2002-2004 Elliotte Rusty Harold
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

import java.io.IOException;

import nu.xom.Builder;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.IllegalDataException;
import nu.xom.IllegalNameException;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import nu.xom.WellformednessException;

/**
 * <p>
 *  Various tests for the <code>DocType</code> class.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0b4
 *
 */
public class DocTypeTest extends XOMTestCase {

    
    public DocTypeTest(String name) {
        super(name);
    }
    
    
    String name = "MyName";
    String systemID = "http://www.w3.org/TR/some.dtd";
    String publicID = "-//Me//some public ID";


    private DocType doctypePublicID;
    private DocType doctypeSystemID;
    private DocType doctypeRootOnly;

    
    protected void setUp() {
        doctypePublicID = new DocType(name, publicID, systemID);
        doctypeSystemID = new DocType(name, systemID);
        doctypeRootOnly = new DocType(name);
    }

    
    public void testToXML() {
        String expected 
          = "<!DOCTYPE " + name + " PUBLIC \"" 
            + publicID + "\" \"" + systemID + "\">";
        assertEquals(expected, doctypePublicID.toXML());    
        assertEquals(
          "<!DOCTYPE " + name + " SYSTEM \"" + systemID + "\">",
          doctypeSystemID.toXML()
        );    
        assertEquals(
          "<!DOCTYPE " + name + ">",
          doctypeRootOnly.toXML()
        );    
    }
    
    
    public void testToXMLWithInternalDTDSubset() 
      throws ValidityException, ParsingException, IOException {
        String data = "<?xml version=\"1.0\"?>\n" 
          + "<!DOCTYPE root [\n  <!ELEMENT test (#PCDATA)>\n]>"
          + "\n<test />\n";  
        Document doc = (new Builder()).build(data, null);
        String result = doc.toXML();
        assertEquals(data, result);    
        
    }  
    
    
    public void testToXMLWithCommentsInInternalDTDSubset() 
      throws ValidityException, ParsingException, IOException {
        
        String data = "<?xml version=\"1.0\"?>\n" 
          + "<!DOCTYPE root [\n" +
                "  <!--comment-->\n  <!ELEMENT test (#PCDATA)>" +
            "\n  <!--comment-->\n]>"
          + "\n<test />\n";  
        Document doc = (new Builder()).build(data, null);
        String result = doc.toXML();
        assertEquals(data, result);    
        
    }  
    
    
    public void testToXMLWithProcessingInstructionsInInternalDTDSubset() 
      throws ValidityException, ParsingException, IOException {
        
        String data = "<?xml version=\"1.0\"?>\n" 
          + "<!DOCTYPE root [\n" +
                "  <?target data?>\n  <!ELEMENT test (#PCDATA)>" +
            "\n  <?target?>\n]>"
          + "\n<test />\n";  
        Document doc = (new Builder()).build(data, null);
        String result = doc.toXML();
        assertEquals(data, result);    
        
    }  
    
    
    public void testInternalDTDSubset() 
      throws ParsingException, IOException {
        
        String data = "<!DOCTYPE root [ <!ELEMENT root EMPTY> ]><test/>";   
        Builder builder = new Builder();
        Document doc = builder.build(data, "http://www.example.com");
        DocType doctype = doc.getDocType();
        assertEquals("root", doctype.getRootElementName());
        String internalSubset =   doctype.getInternalDTDSubset();
        assertEquals("  <!ELEMENT root EMPTY>\n", internalSubset);
        assertTrue(doctype.toXML().indexOf("[") > 0);
        assertTrue(doctype.toXML().indexOf("]") > 0);
        assertTrue(doctype.toXML().indexOf("<!ELEMENT root EMPTY>") > 0);
        
    }

    
    public void testToString() {
        
        String expected 
          = "[nu.xom.DocType: " + name + "]";
        assertEquals(expected, doctypePublicID.toString());   
        
    }

    
    public void testConstructor1Arg() {

        String name = "MyName";
        DocType doctype = new DocType(name);
        assertEquals(name, doctype.getRootElementName());
        assertEquals("", doctype.getInternalDTDSubset());
        assertNull(doctype.getSystemID());
        assertNull(doctype.getPublicID());

        // legal to have a colon here
        name = "try:MyName";
        doctype = new DocType(name);
        assertEquals("", doctype.getInternalDTDSubset());
        assertEquals(name, doctype.getRootElementName());
        assertNull(doctype.getSystemID());
        assertNull(doctype.getPublicID());

        // illegal name
        try {
            name = "try MyName";
            doctype = new DocType(name);
            fail("allowed root element name to contain spaces");
        } 
        catch (IllegalNameException success) {
            assertNotNull(success.getMessage());
        }

    }

    
    public void testNullRootElementName() {
        
        try {
            new DocType((String) null);
            fail("Allowed null root element name");
        }
        catch (IllegalNameException success) {
            assertNotNull(success.getMessage());
        }
        
    }

    
    public void testRootElementNameBeginsWithDigit() {
        
        try {
            new DocType("1Data");
            fail("Allowed non-namestart character in root element name");
        }
        catch (IllegalNameException success) {
            assertNotNull(success.getMessage());
        }
        
    }

    
    public void testRootElementNameBeginsWithColon() {
        
        try {
            new DocType(":Data");
            fail("Allowed colon to begin root element name");
        }
        catch (IllegalNameException success) {
            assertNotNull(success.getMessage());
        }
        
    }

    
    public void testSetRootElementName() {
        
        DocType doctype = new DocType("root");
        doctype.setRootElementName("newname");
        assertEquals("newname", doctype.getRootElementName());
        doctype.setRootElementName("new:name");
        assertEquals("new:name", doctype.getRootElementName());
        try {
            doctype.setRootElementName(":Data");
            fail("Allowed colon to begin root element name");
        }
        catch (IllegalNameException success) {
            assertNotNull(success.getMessage());
        }
        
    }

    
    public void testEmptyRootElementName() {
        
        try {
            new DocType("");
            fail("Allowed empty string to be root element name");
        }
        catch (IllegalNameException success) {
            assertNotNull(success.getMessage());
        }
        
    }

    
    public void testNoChildren() {
        
        assertEquals(0, doctypePublicID.getChildCount());
        assertEquals(0, doctypeSystemID.getChildCount());
        assertEquals(0, doctypeRootOnly.getChildCount());
        
        try {
            doctypePublicID.getChild(0);
            fail("Got zeroth child");
        }
        catch (IndexOutOfBoundsException success) {
            assertNotNull(success.getMessage());
        }
        
    }
    
    
    public void testConstructor2Arg() {

        String name = "MyName";
        String systemID = "http://www.w3.org/TR/some.dtd";
        DocType doctype = new DocType(name, systemID);
        assertEquals(name, doctype.getRootElementName());
        assertEquals("", doctype.getInternalDTDSubset());
        assertEquals(systemID, doctype.getSystemID());
        assertNull(doctype.getPublicID());

        // empty system ID
        name = "try:MyName";
        systemID = "";
        doctype = new DocType(name, systemID);
        assertEquals(name, doctype.getRootElementName());
        assertEquals("", doctype.getInternalDTDSubset());
        assertEquals(systemID, doctype.getSystemID());
        assertNull(doctype.getPublicID());

    }

    
    public void testConstructor3Arg() {

        String name = "MyName";
        String systemID = "http://www.w3.org/TR/some.dtd";
        String publicID = "-//Me//some public ID";
        DocType doctype = new DocType(name, publicID, systemID);
        assertEquals(name, doctype.getRootElementName());
        assertEquals("", doctype.getInternalDTDSubset());
        assertEquals(systemID, doctype.getSystemID());
        assertEquals(publicID, doctype.getPublicID());

    }

    
    public void testEmptyStringForPublicID() {

        String name = "MyName";
        String systemID = "http://www.w3.org/TR/some.dtd";
        String publicID = "";
        DocType doctype = new DocType(name, publicID, systemID);
        assertEquals(name, doctype.getRootElementName());
        assertEquals("", doctype.getInternalDTDSubset());
        assertEquals(systemID, doctype.getSystemID());
        assertEquals(publicID, doctype.getPublicID());

    }

    
    public void testEmptyStringForSystemID() {

        String name = "MyName";
        String systemID = "";
        String publicID = "-//Me//some public ID";
        DocType doctype = new DocType(name, publicID, systemID);
        assertEquals(name, doctype.getRootElementName());
        assertEquals("", doctype.getInternalDTDSubset());
        assertEquals(systemID, doctype.getSystemID());
        assertEquals(publicID, doctype.getPublicID());

    }

    
    public void testIllegalPublicIDs() {

        // PubidChar ::= #x20 | #xD | #xA | [a-zA-Z0-9] | [-'()+,./:=?;!*#@$_%]
        for (char c = 0; c <= 0x9; c++) {
            try {
                checkPublicIDCharacter(c + "");
                fail("Allowed bad public ID character " 
                  + Integer.toHexString(c));
            }
            catch (WellformednessException success) {
                // successfully detected bad public ID    
                assertNotNull(success.getMessage());
            }
        } 
        for (char c = 0xB; c < 0xD; c++) {
            try {
                checkPublicIDCharacter(c + "");
                fail("Allowed bad public ID character " 
                  + Integer.toHexString(c));
            }
            catch (WellformednessException success) {
                // successfully detected bad public ID    
                assertNotNull(success.getMessage());
            }
        } 
        for (char c = 0xE; c < 0x20; c++) {
            try {
                checkPublicIDCharacter(c + "");
                fail("Allowed bad public ID character " 
                  + Integer.toHexString(c));
            }
            catch (WellformednessException success) {
                // successfully detected bad public ID 
                assertNotNull(success.getMessage());   
            }
        } 
        for (char c = '~'; c < 1000; c++) {
            try {
                checkPublicIDCharacter(c + "");
                fail("Allowed bad public ID character " 
                  + Integer.toHexString(c));
            }
            catch (WellformednessException success) {
                // successfully detected bad public ID    
                assertNotNull(success.getMessage());
            }
        } 
        
        char[] illegalPunctuationMarks = "<>`^&\"[]{}|\\~".toCharArray();
        for (int i = 0; i < illegalPunctuationMarks.length; i++) {
            char c = illegalPunctuationMarks[i];
            try {
                checkPublicIDCharacter(c + "");
                fail("Allowed bad public ID character " 
                  + Integer.toHexString(c));
            }
            catch (WellformednessException success) {
                // successfully detected bad public ID    
                assertNotNull(success.getMessage());
            }
        } 

    }
    
    
    public void testLegalPublicIDs() {

        // PubidChar ::= #x20 | #xD | #xA | [a-zA-Z0-9] | [-'()+,./:=?;!*#@$_%]
        // should public IDs allow initial and trailing space? No. 
        // These are normalized like attribute values according to 
        // section 4.2.2 of the XML spec.
        // This also means charriage returns and linefeeds are 
        // not roundtrippable. They can appear in the document but not
        // the infoset so XOM forbids them.
        checkPublicIDCharacter("-'()+,./:=?;!*#@$_%");
        for (char c = 'a'; c <= 'z'; c++) checkPublicIDCharacter(c + "");
        for (char c = 'A'; c <= 'Z'; c++) checkPublicIDCharacter(c + "");
        for (char c = '0'; c <= '9'; c++) checkPublicIDCharacter(c + "");

    }
    

    public void testSpaceContainingPublicIDs() {

        // According to section 4.2.2 of the XML spec, public IDs are
        // normalized like attribute values of non-CDATA type
        try {
            new DocType("root", " test", "http://www.example.org");
            fail("allowed initial space in public ID");
        }
        catch (WellformednessException success) {
            assertNotNull(success.getMessage());
        }

        try {
            new DocType("root", "test ", "http://www.example.org");
            fail("allowed trailing space in public ID");
        }
        catch (WellformednessException success) {
            assertNotNull(success.getMessage());
        }

        try {
            new DocType("root", "test\ntest", "http://www.example.org");
            fail("allowed linefeed public ID");
        }
        catch (WellformednessException success) {
            assertNotNull(success.getMessage());
        }

        try {
            new DocType("root", "test\rtest", "http://www.example.org");
            fail("allowed carriage return in public ID");
        }
        catch (WellformednessException success) {
            assertNotNull(success.getMessage());
        }

        try {
            new DocType("root", "test\r\ntest", "http://www.example.org");
            fail("allowed carriage return linefeed pair public ID");
        }
        catch (WellformednessException success) {
            assertNotNull(success.getMessage());
        }

        try {
            new DocType("root", "test  test", "http://www.example.org");
            fail("allowed multiple consecutive spaces in public ID");
        }
        catch (WellformednessException success) {
            assertNotNull(success.getMessage());
        }
        
        // one space is legal
        DocType test = new DocType("root", "test test", "http://www.example.org");
        assertEquals(test.getPublicID(), "test test");

    }
    
    
    public void testSystemIDWithDollarSignAndComma() {

        String systemID = "http://www.example.com/test$red/limit,data.xml";
        DocType doctype = new DocType("root", systemID);
        assertEquals(systemID, doctype.getSystemID());
        
    }
    
    
    public void testSystemIDWithSemicolon() {

        String systemID = "smb://domain;user:pass@server/share/path/to/file";
        DocType doctype = new DocType("root", systemID);
        assertEquals(systemID, doctype.getSystemID());
        
    }
    
    
    public void testIllegalSystemIDs() {

        // "It is an error for a fragment identifier 
        // (beginning with a # character) 
        // to be part of a system identifier."
        try {
            new DocType("test", "http://www.example.com/index.html#test");
            fail("Allowed system ID with fragment identifier");
        }
        catch (IllegalDataException success) {
            // successfully detected bad system ID    
            assertNotNull(success.getMessage());
        }
        
        try {
            new DocType("test", "http://www.example.com/index.html#");
            fail("Allowed # in system ID");
        }
        catch (IllegalDataException success) {
            // successfully detected bad system ID    
            assertNotNull(success.getMessage());
        }

        try {
            new DocType("test", "http://www.example.com/\u00A9.html#");
            fail("Allowed non-ASCII character in system ID");
        }
        catch (IllegalDataException success) {
            // successfully detected bad system ID    
            assertNotNull(success.getMessage());
        }

        try {
            new DocType("test", "http://www.example.com/\u0007.html#");
            fail("Allowed C0 control character in system ID");
        }
        catch (IllegalDataException success) {
            // successfully detected bad system ID    
            assertNotNull(success.getMessage());
        }

        try {
            new DocType("test", "test\" and ' in the same ID");
            fail("Allowed both \" and ' in system ID");
        }
        catch (IllegalDataException success) {
            // successfully detected bad system ID    
            assertNotNull(success.getMessage());
        }

    }
    
    
    void checkPublicIDCharacter(String publicID) {
        String name = "MyName";
        String systemID = "http://www.w3.org/TR/some.dtd";
        DocType doctype = new DocType(name, publicID, systemID);
        assertEquals(publicID, doctype.getPublicID());
    }

    
    public void testClone() {

        String name = "MyName";
        String systemID = "http://www.w3.org/TR/some.dtd";
        String publicID = "-//Me//some public ID";
        DocType doctype = new DocType(name, publicID, systemID);

        DocType other = (DocType) doctype.copy();

        assertEquals(
          doctype.getRootElementName(),
          other.getRootElementName());
        assertEquals(
          doctype.getInternalDTDSubset(),
          other.getInternalDTDSubset()
        );
        assertEquals(doctype.getSystemID(), other.getSystemID());
        assertEquals(doctype.getPublicID(), other.getPublicID());
        assertTrue(!other.equals(doctype));

    }

    
    public void testGetters() {

        String name = "MyName";
        String systemID = "http://www.w3.org/TR/some.dtd";
        String publicID = "-//Me//some public ID";
        DocType doctype = new DocType(name, publicID, systemID);

        assertEquals("", doctype.getValue());

    }

    
    public void testSystemIDRequiredForPublicID() {

        String name = "MyName";
        DocType doctype = new DocType(name);

        try {
            doctype.setPublicID("-//Me//some public ID");
            fail("created a doctype with a public ID and no system ID");
        } 
        catch (WellformednessException ex) {
            // success   
            assertNotNull(ex.getMessage());
        }

    }

    
    public void testRemove() {

        String name = "MyName";
        String publicID = "-//Me//some public ID";
        DocType doctype =
          new DocType(name, publicID, "http://www.example.com");

        doctype.setPublicID(null);
        assertNull(doctype.getPublicID());
        doctype.setPublicID(publicID);
        assertEquals(publicID, doctype.getPublicID());

        try {
            doctype.setSystemID(null);
            fail("removed system ID before removing public ID");
        } 
        catch (WellformednessException success) {  
            assertNotNull(success.getMessage());
        }

        doctype.setPublicID(null);
        assertNull(doctype.getPublicID());
        doctype.setSystemID(null);
        assertNull(doctype.getSystemID());

    }

    
}