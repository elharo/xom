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

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.IllegalDataException;
import nu.xom.IllegalNameException;
import nu.xom.MalformedURIException;
import nu.xom.NamespaceConflictException;

/**
 * <p>
 *  Basic tests for the <code>Attribute</code> class.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0b5
 *
 */
public class AttributeTest extends XOMTestCase {

    public AttributeTest(String name) {
        super(name);
    }

    
    private Attribute a1;
    private Attribute a2;

    
    protected void setUp() {
        a1 = new Attribute("test", "value");
        a2 = new Attribute("test", "  value  ");
    }

    
    public void testGetChildCount() {
        assertEquals(0, a1.getChildCount());
    }
    
    
    public void testGetChild() {
        try {
            a1.getChild(0);
            fail("Didn't throw IndexOutofBoundsException");
        }
        catch (IndexOutOfBoundsException ex) {
            // success   
        }
    }

    
    public void testConstructor() {
        assertEquals("test", a1.getLocalName());
        assertEquals("test", a1.getQualifiedName());
        assertEquals("", a1.getNamespacePrefix());
        assertEquals("", a1.getNamespaceURI());
        assertEquals("value", a1.getValue());
        assertEquals("  value  ", a2.getValue());
    }

    
    public void testConstructor2() {
        
        Attribute a1 = new Attribute("name", "value", Attribute.Type.CDATA);
        assertEquals("name", a1.getLocalName());
        assertEquals("name", a1.getQualifiedName());
        assertEquals("", a1.getNamespacePrefix());
        assertEquals("", a1.getNamespaceURI());
        assertEquals("value", a1.getValue());
        assertEquals(Attribute.Type.CDATA, a1.getType());
    }

    
    public void testGetExternalForm() {
        
        Attribute a1 = new Attribute("test", "value contains a \"");
        assertEquals("test=\"value contains a &quot;\"", a1.toXML());

        Attribute a2 = new Attribute("test", "value contains a '");
        assertEquals("test=\"value contains a &apos;\"", a2.toXML());

    }

    
    public void testSetLocalName() {
        
        Attribute a = new Attribute("name", "value");
        a.setLocalName("newname");
        assertEquals("newname", a.getLocalName());
        
        try {
            a.setLocalName("pre:a");
            fail("Allowed local attribute name containing colon");
        }
        catch (IllegalNameException success) {
            assertNotNull(success.getMessage());
        }
        
    }
    
    
    public void testSetLocalNameInNamespaceQualifiedAttribute() {
        
        Attribute a = new Attribute("pre:name", "http://www.example.org", "value");
        a.setLocalName("newname");
        assertEquals("newname", a.getLocalName());
        assertEquals("pre:newname", a.getQualifiedName());
        
    }
    
    
    // No xmlns attributes or xmlns:prefix attributes are allowed
    public void testXmlns() {
        
        try {
            new Attribute("xmlns", "http://www.w3.org/TR");
            fail("Created attribute with name xmlns");
        }
        catch (IllegalNameException success) {
            assertNotNull(success.getMessage());    
        }
 
        try {
            new Attribute("xmlns:prefix", "http://www.w3.org/TR");
            fail("Created attribute with name xmlns:prefix");
        }
        catch (IllegalNameException success) {
            assertNotNull(success.getMessage());    
        }
 
        // Now try with namespace URI from errata
        try {
            new Attribute("xmlns", "http://www.w3.org/2000/xmlns/", "http://www.w3.org/");
            fail("created xmlns attribute");
         }
         catch (IllegalNameException success) {
            assertNotNull(success.getMessage());    
         }
        
        // Now try with namespace URI from errata
        try {
            new Attribute("xmlns:pre", "http://www.w3.org/2000/xmlns/", "http://www.w3.org/");
            fail("created xmlns:pre attribute");
         }
         catch (IllegalNameException success) {
            assertNotNull(success.getMessage());    
         }

    }


    public void testXMLBase() {
        
        String xmlNamespace = "http://www.w3.org/XML/1998/namespace";        
        Attribute a1 = new Attribute("xml:base", xmlNamespace, "http://www.w3.org/");
        assertEquals( "base", a1.getLocalName());
        assertEquals("xml:base", a1.getQualifiedName());
        assertEquals(xmlNamespace, a1.getNamespaceURI());
        
        a1.setValue("http://www.example.com/>");
        assertEquals("http://www.example.com/>", a1.getValue());
    
        a1.setValue("http://www.example.com/<");
        assertEquals("http://www.example.com/<", a1.getValue());
        
        a1.setValue("http://www.example.com/\u00FE");
        assertEquals(a1.getValue(), "http://www.example.com/\u00FE");
        
    }

    
    public void testXmlPrefix() {
        
        try {
            new Attribute("xml:base", "http://www.w3.org/TR");
            fail("Created attribute with name xml:base");
        }
        catch (NamespaceConflictException success) {
            assertNotNull(success.getMessage());    
        }
 
        try {
            new Attribute("xml:space", "preserve");
            fail("Created attribute with local name xml:space");
        }
        catch (NamespaceConflictException success) {
            assertNotNull(success.getMessage());    
        }
 
        try {
            new Attribute("xml:lang", "fr-FR");
            fail("Created attribute with name xml:lang");
        }
        catch (NamespaceConflictException success) {
            assertNotNull(success.getMessage());    
        }
        
        String xmlNamespace = "http://www.w3.org/XML/1998/namespace";       
        Attribute a1 = new Attribute(
          "xml:base", xmlNamespace, "http://www.w3.org/");
        assertEquals("base", a1.getLocalName());
        assertEquals("xml:base", a1.getQualifiedName());
        assertEquals(xmlNamespace, a1.getNamespaceURI());

        Attribute a2 = new Attribute("xml:space", xmlNamespace, "preserve");
        assertEquals(a2.getLocalName(), "space");
        assertEquals("xml:space", a2.getQualifiedName());
        assertEquals(xmlNamespace, a2.getNamespaceURI());

        Attribute a3 
          = new Attribute("xml:lang", xmlNamespace, "en-UK");
        assertEquals("lang", a3.getLocalName());
        assertEquals("xml:lang", a3.getQualifiedName());
        assertEquals(xmlNamespace, a3.getNamespaceURI());

        try {
            new Attribute("xml:base", "http://www.notTheXMLNamespace", 
              "http://www.w3.org/");
            fail("remapped xml prefix");
        }
        catch (NamespaceConflictException success) {
            assertNotNull(success.getMessage());    
        }

    }

    
    public void testWrongPrefixNotAllowedWithXMLURI() {
        
        try {
            new Attribute("test:base", "http://www.w3.org/XML/1998/namespace", "value");
            fail("Allowed XML namespace to be associated with non-xml prefix");    
        }
        catch (NamespaceConflictException success) {
            assertNotNull(success.getMessage());   
        }
        
    }

    
    public void testToString() {   
        assertEquals(
          "[nu.xom.Attribute: test=\"value\"]", a1.toString());      
        assertEquals(
          "[nu.xom.Attribute: test=\"  value  \"]", a2.toString());             
    }
    
    
    public void testToXML() {        
        assertEquals("test=\"value\"", a1.toXML());          
        assertEquals("test=\"  value  \"", a2.toXML());                    
    }

    
    public void testEscapingWithToXML() {          
        a1.setValue("<");     
        assertEquals("test=\"&lt;\"", a1.toXML());  
        a1.setValue(">");        
        assertEquals("test=\"&gt;\"", a1.toXML());  
        a1.setValue("\"");        
        assertEquals("test=\"&quot;\"", a1.toXML());  
        a1.setValue("\'");        
        assertEquals("test=\"&apos;\"", a1.toXML());  
        a1.setValue("&");        
        assertEquals("test=\"&amp;\"", a1.toXML());  
    }

    
    public void testWhiteSpaceEscapingWithToXML() {          
        a1.setValue(" ");     
        assertEquals("test=\" \"", a1.toXML());  
        a1.setValue("\n");        
        assertEquals("test=\"&#x0A;\"", a1.toXML());  
        a1.setValue("\r");        
        assertEquals("test=\"&#x0D;\"", a1.toXML());  
        a1.setValue("\t");        
        assertEquals("test=\"&#x09;\"", a1.toXML());  
    }


    public void testSetValue() {
        
        String[] legal = {
          "Hello",
          "hello there",
          "  spaces on both ends  ",
          " quotes \" \" quotes",
          " single \'\' quotes",
          " both double and single \"\'\"\' quotes",  
          " angle brackets <  > <<<",  
          " carriage returns \r\r\r",  
          " ampersands & &&& &name; "  
        };

        // Things that shouldn't cause an exception
        for (int i = 0; i < legal.length; i++) {
            a1.setValue(legal[i]);    
            assertEquals(legal[i], a1.getValue());
        }
        
        try {
          a1.setValue("test \u0000 test ");
          fail("Should raise an IllegalDataException");
        }
        catch (IllegalDataException ex) {
            // success   
            assertNotNull(ex.getMessage());
        }

    }

    
    public void testNames() {
        
        String prefix = "testPrefix";
        String name = "testName";
        String URI = "http://www.elharo.com/";
        String value = "  here's some data";
        
        
        Attribute a1 = new Attribute(prefix + ":" + name, URI, value);
        assertEquals(name, a1.getLocalName());
        assertEquals(prefix + ":" + name, a1.getQualifiedName());
        assertEquals(URI, a1.getNamespaceURI());
    }


    public void testEquals() {
        Attribute c1 = new Attribute("test", "limit");
        Attribute c2 = new Attribute("test", "limit");
        Attribute c3 = new Attribute("retina", "retina test");

        assertEquals(c1, c1);
        assertEquals(c1.hashCode(), c1.hashCode());
        assertTrue(!c1.equals(c2));
        assertTrue(!c1.equals(c3));
        assertTrue(!c1.equals(null));
        assertFalse(c1.equals("limit"));
        assertFalse(c1.equals(new Element("test")));
    }

    
    public void testTypeEquals() {
        assertEquals(Attribute.Type.CDATA, Attribute.Type.CDATA);
        assertTrue(!Attribute.Type.CDATA.equals(Attribute.Type.NMTOKEN));
        assertTrue(!Attribute.Type.CDATA.equals(null));
        assertFalse(Attribute.Type.CDATA.equals("CDATA"));
        assertFalse(Attribute.Type.CDATA.equals(new Element("CDATA")));
    }

    
    public void testCopyConstructor() {
        Attribute c1 = new Attribute("test", "data");
        Attribute c2 = new Attribute(c1);

        assertEquals(c1.getValue(), c2.getValue());
        assertEquals(c1.getLocalName(), c2.getLocalName());
        assertEquals(c1.getQualifiedName(), c2.getQualifiedName());
        assertEquals(c1.getValue(), c2.getValue());
        assertTrue(!c1.equals(c2));
        assertNull(c2.getParent());

    }

    
    // Check passing in a string with broken surrogate pairs
    // and with correct surrogate pairs
    public void testSurrogates() {

        String goodString = "test: \uD8F5\uDF80  ";
        Attribute c = new Attribute("surrogate", goodString);
        assertEquals(goodString, c.getValue());

        // Two high-halves
        try {
          new Attribute("surrogate", "test: \uD8F5\uDBF0  ");
          fail("Should raise an IllegalDataException");
        }
        catch (IllegalDataException success) {
            assertEquals("test: \uD8F5\uDBF0  ", success.getData());
            assertNotNull(success.getMessage());    
        }

        // Two high-halves
        try {
          new Attribute("surrogate", "test: \uD8F5\uD8F5  ");
          fail("Should raise an IllegalDataException");
        }
        catch (IllegalDataException success) {
            assertEquals("test: \uD8F5\uD8F5  ", success.getData());
            assertNotNull(success.getMessage());    
        }

        // One high-half
        try {
           new Attribute("surrogate", "test: \uD8F5  ");
           fail("Should raise an IllegalDataException");
         }
        catch (IllegalDataException success) {   
            assertEquals("test: \uD8F5  ", success.getData());
            assertNotNull(success.getMessage());    
        }

        // One low half
        try {
            new Attribute("surrogate", "test: \uDF80  ");
            fail("One low half");
        }
        catch (IllegalDataException success) {   
             assertEquals("test: \uDF80  ", success.getData());
           assertNotNull(success.getMessage());    
        }

        // Low half before high half
        try {
            new Attribute("surrogate", "test: \uDCF5\uD8F5  ");
            fail("Low half before high half");
        }
        catch (IllegalDataException success) { 
            assertEquals("test: \uDCF5\uD8F5  ", success.getData());
            assertNotNull(success.getMessage());    
        }


    }
    
    
    public void testNullNamespace() {
        Attribute a = new Attribute("red:prefix", 
          "http://www.example.com", "data");
        a.setNamespace(null, null);
        assertEquals("", a.getNamespaceURI());
        assertEquals("", a.getNamespacePrefix());
    }

    
    public void testChangeNamespaceToSameNamespaceAsElement() {
        Attribute a = new Attribute("red:prefix", 
          "http://www.example.com", "data");
        Element e = new Element("pre:test", "http://www.example.org/");
        e.addAttribute(a);
        a.setNamespace("pre", "http://www.example.org/");
        assertEquals("http://www.example.org/", a.getNamespaceURI());
        assertEquals("pre", a.getNamespacePrefix());
        assertEquals("http://www.example.org/", e.getNamespaceURI());
        assertEquals("pre", e.getNamespacePrefix());
    }

    
    public void testSetNamespaceURI() {
        
        String name = "red:sakjdhjhd";
        String uri = "http://www.red.com/";
        String prefix = "red";
        Attribute a = new Attribute(name, uri, "");

        assertEquals(uri, a.getNamespaceURI());
        
        String[] legal = {"http://www.is.edu/sakdsk#sjadh",
        "http://www.is.edu/sakdsk?name=value&name=head",
        "uri:isbn:0832473864",
        "http://www.examples.com:80",
        "http://www.examples.com:80/",
        "http://www.is.edu/%20sakdsk#sjadh"};
         
        String[] illegal = {
          "http://www.is.edu/%sakdsk#sjadh",
          "http://www.is.edu/k\u0245kakdsk#sjadh",
          "!@#$%^&*()",
          "fred",
          "#fred",
          "/fred"
        }; 
        
        for (int i = 0; i < legal.length; i++) {
            a.setNamespace(prefix, legal[i]);          
            assertEquals(legal[i], a.getNamespaceURI());
        }
        
        for (int i = 0; i < illegal.length; i++) {
            try {
                a.setNamespace(prefix, illegal[i]);
                fail("Illegal namespace URI allowed");  
            }
            catch (MalformedURIException success) {
               assertEquals(illegal[i], success.getData());
            }
            catch (IllegalNameException success) {
               assertNotNull(success.getMessage());   
            }
        }
        
    }
    
    
    public void testSetNamespace() {
        
        Attribute a = new Attribute("name", "value");
        try {
            a.setNamespace("pre", "");
            fail("Allowed prefix with empty URI");
        }
        catch (NamespaceConflictException success) {
            assertNotNull(success.getMessage());
        }
        
        try {
            a.setNamespace("", "http://www.example.com");
            fail("Allowed empty prefix with non-empty URI");
        }
        catch (NamespaceConflictException success) {
            assertNotNull(success.getMessage());
        }
        
    }


    public void testNodeProperties() {

        Attribute a1 = new Attribute("test", "data");

        assertNull(a1.getParent());

        Element element = new Element("test");
        element.addAttribute(a1);
        assertEquals(element, a1.getParent());
        assertEquals(a1, element.getAttribute("test"));

        element.removeAttribute(a1);
        assertNull(element.getAttribute("test"));

    }
    
    
    public void testDistinctTypes() {
    
        assertTrue(!(Attribute.Type.CDATA.equals(Attribute.Type.UNDECLARED)));
           
        assertTrue(!(Attribute.Type.ID.equals(Attribute.Type.CDATA)));  
        assertTrue(!(Attribute.Type.IDREF.equals(Attribute.Type.ID)));  
        assertTrue(!(Attribute.Type.IDREFS.equals(Attribute.Type.IDREF)));   
        assertTrue(!(Attribute.Type.NMTOKEN.equals(Attribute.Type.IDREFS)));   
        assertTrue(!(Attribute.Type.NMTOKENS.equals(Attribute.Type.NMTOKEN)));  
        assertTrue(!(Attribute.Type.NOTATION.equals(Attribute.Type.NMTOKENS)));   
        assertTrue(!(Attribute.Type.ENTITY.equals(Attribute.Type.NOTATION)));   
        assertTrue(!(Attribute.Type.ENTITIES.equals(Attribute.Type.ENTITY)));   
        assertTrue(!(Attribute.Type.ENUMERATION.equals(Attribute.Type.ENTITIES)));  
        assertTrue(!(Attribute.Type.CDATA.equals(Attribute.Type.ENUMERATION)));
    }


    public void testAdditionConstraints() {

        Element element = new Element("test");
        Attribute a1 = new Attribute(
          "foo:data", "http://www.example.com", "valueFoo");
        Attribute a2 = new Attribute(
          "bar:data", "http://www.example.com", "valueBar");
        Attribute a3 = new Attribute("data", "valueFoo");
        Attribute a4 = new Attribute("data", "valueBar");

        element.addAttribute(a1);
        assertEquals("valueFoo", 
          element.getAttributeValue("data", "http://www.example.com"));
        assertEquals(1, element.getAttributeCount());
        element.addAttribute(a2);
        assertEquals(
          element.getAttributeValue("data", "http://www.example.com"), 
          "valueBar"
        );
        assertEquals(1, element.getAttributeCount());
        element.addAttribute(a3);
        assertEquals(element.getAttributeValue("data"), "valueFoo");
        assertEquals("valueBar", 
          element.getAttributeValue("data", "http://www.example.com"));
        assertEquals(2, element.getAttributeCount());
        element.addAttribute(a4);
        assertEquals("valueBar", element.getAttributeValue("data"));
        assertEquals(2, element.getAttributeCount());
        
        // an attribute can have two attributes in the same namespace
        // with different prefixes
        Attribute a5 = new Attribute(
          "red:ab", "http://www.example.org", "valueRed");
        Attribute a6 = new Attribute(
          "green:cd", "http://www.example.org", "valueGreen");
        element.addAttribute(a5);
        element.addAttribute(a6);
        assertEquals("valueRed", 
          element.getAttributeValue("ab", "http://www.example.org"));
        assertEquals("valueGreen", 
          element.getAttributeValue("cd", "http://www.example.org"));

    }
    
    
    public void testXMLLangCanBeEmptyString() {
        // per section 2.12 of the XML Rec
        
        Attribute a = new Attribute("xml:lang", "http://www.w3.org/XML/1998/namespace", "");
        assertEquals("", a.getValue());
        
    }

    
}