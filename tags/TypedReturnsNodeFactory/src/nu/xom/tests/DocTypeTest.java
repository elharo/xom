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

import nu.xom.DocType;
import nu.xom.IllegalNameException;
import nu.xom.WellformednessException;

/**
 * <p>
 *  various basic tests for the <code>DocType</code> class.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d22
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
        } catch (IllegalNameException ex) {
            // success
            assertNotNull(ex.getMessage());
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
        for (char c = 0; c < 0x9; c++) {
            try {
                checkPublicIDCharacter(c + "");
                fail("Allowed bad public ID character " 
                  + Integer.toHexString(c));
            }
            catch (WellformednessException ex) {
                // successfully detected bad public ID    
                assertNotNull(ex.getMessage());
            }
        } 
        for (char c = 0xB; c < 0xD; c++) {
            try {
                checkPublicIDCharacter(c + "");
                fail("Allowed bad public ID character " 
                  + Integer.toHexString(c));
            }
            catch (WellformednessException ex) {
                // successfully detected bad public ID    
                assertNotNull(ex.getMessage());
            }
        } 
        for (char c = 0xE; c < 0x20; c++) {
            try {
                checkPublicIDCharacter(c + "");
                fail("Allowed bad public ID character " 
                  + Integer.toHexString(c));
            }
            catch (WellformednessException ex) {
                // successfully detected bad public ID    
            }
        } 
        for (char c = '~'; c < 1000; c++) {
            try {
                checkPublicIDCharacter(c + "");
                fail("Allowed bad public ID character " 
                  + Integer.toHexString(c));
            }
            catch (WellformednessException ex) {
                // successfully detected bad public ID    
                assertNotNull(ex.getMessage());
            }
        } 

    }
    
    public void testLegalPublicIDs() {

        // PubidChar ::= #x20 | #xD | #xA | [a-zA-Z0-9] | [-'()+,./:=?;!*#@$_%]
        checkPublicIDCharacter(" ");
        checkPublicIDCharacter("\r");
        checkPublicIDCharacter("\n");
        checkPublicIDCharacter("-'()+,./:=?;!*#@$_%");
        for (char c = 'a'; c < 'z'; c++) checkPublicIDCharacter(c + "");
        for (char c = 'A'; c < 'Z'; c++) checkPublicIDCharacter(c + "");
        for (char c = '0'; c < '9'; c++) checkPublicIDCharacter(c + "");

    }
    
    public void testIllegalSystemIDs() {

        // "It is an error for a fragment identifier 
        // (beginning with a # character) 
        // to be part of a system identifier."
        try {
            new DocType("test", "http://www.example.com/index.html#test");
            fail("Allowed system ID with fragment identifier");
        }
        catch (WellformednessException ex) {
            // successfully detected bad system ID    
            assertNotNull(ex.getMessage());
        }
        try {
            new DocType("test", "http://www.example.com/index.html#");
            fail("Allowed # in system ID");
        }
        catch (WellformednessException ex) {
            // successfully detected bad system ID    
            assertNotNull(ex.getMessage());
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
        catch (WellformednessException ex) {
            // success   
            assertNotNull(ex.getMessage());
        }

        doctype.setPublicID(null);
        assertNull(doctype.getPublicID());
        doctype.setSystemID(null);
        assertNull(doctype.getSystemID());

    }

}