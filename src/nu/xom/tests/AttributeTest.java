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

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.IllegalDataException;
import nu.xom.MalformedURIException;
import nu.xom.NamespaceException;

/**
 * <p>
 *  Basic tests for the <code>Attribute</code> class.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d19
 *
 */
public class AttributeTest extends XOMTestCase {

    public AttributeTest() {
        super("Attribute tests");
    }

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

    public void testHasChildren() {
        assertTrue(!a1.hasChildren());
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

    public void testTypes() {
        assertEquals(Attribute.Type.CDATA, Attribute.Type.CDATA);
        assertEquals(Attribute.Type.ID, Attribute.Type.ID);
        assertEquals(Attribute.Type.IDREF, Attribute.Type.IDREF);
        assertEquals(Attribute.Type.IDREFS, Attribute.Type.IDREFS);
        assertEquals(Attribute.Type.UNDECLARED, Attribute.Type.UNDECLARED);
        assertEquals(Attribute.Type.NMTOKEN, Attribute.Type.NMTOKEN);
        assertEquals(Attribute.Type.NMTOKENS, Attribute.Type.NMTOKENS);
        assertTrue(Attribute.Type.CDATA != Attribute.Type.ID);
        assertTrue(Attribute.Type.ID != Attribute.Type.IDREF);
        assertTrue(Attribute.Type.ID != Attribute.Type.IDREFS);
        assertTrue(Attribute.Type.ID != Attribute.Type.NMTOKEN);
        assertTrue(Attribute.Type.ID != Attribute.Type.NMTOKENS);
        assertTrue(Attribute.Type.UNDECLARED != Attribute.Type.CDATA);
        assertTrue(Attribute.Type.NMTOKEN != Attribute.Type.CDATA);
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

    // No xmlns attributes or xmlns:prefix attributes are allowed
    public void testXmlns() {
        
        try {
               new Attribute("xmlns", "http://www.w3.org/TR");
            fail("Created attribute with name xmlns");
        }
        catch (NamespaceException ex) {
            // success    
        }
 
        try {
               new Attribute("xmlns:prefix", "http://www.w3.org/TR");
            fail("Created attribute with name xmlns:prefix");
        }
        catch (NamespaceException ex) {
            // success    
        }
 
        // Now try with namespace URI from errata
        try {
             new Attribute("xmlns", "http://www.w3.org/2000/xmlns/", "http://www.w3.org/");
            fail("created xmlns attribute");
         }
         catch (NamespaceException e) {
             // success    
         }
        
        // Now try with namespace URI from errata
        try {
             new Attribute("xmlns:pre", "http://www.w3.org/2000/xmlns/", "http://www.w3.org/");
            fail("created xmlns:pre attribute");
         }
         catch (NamespaceException e) {
             // success    
         }

    }


    public void testXMLBase() {
        
        String xmlNamespace = "http://www.w3.org/XML/1998/namespace";        
        Attribute a1 = new Attribute("xml:base", xmlNamespace, "http://www.w3.org/");
        assertEquals( "base", a1.getLocalName());
        assertEquals("xml:base", a1.getQualifiedName());
        assertEquals(xmlNamespace, a1.getNamespaceURI());
        
        try {
            a1.setValue("http://www.example.com/>");
            fail("allowed non-IRI for xml:base value");
        }
        catch (IllegalDataException ex) {
            
            // success
        }
    
        try {
            a1.setValue("http://www.example.com/<");
            fail("allowed non-IRI for xml:base value");
        }
        catch (IllegalDataException ex) {
            
            // success
        }
        
        a1.setValue("http://www.example.com/\u00FE");
        assertEquals(a1.getValue(), "http://www.example.com/\u00FE");
        
    }

    public void testXmlPrefix() {
        
        try {
            new Attribute("xml:base", "http://www.w3.org/TR");
            fail("Created attribute with name xml:base");
        }
        catch (NamespaceException ex) {
            // success    
        }
 
        try {
            new Attribute("xml:space", "preserve");
            fail("Created attribute with name xml:space");
        }
        catch (NamespaceException ex) {
            // success    
        }
 
        try {
            new Attribute("xml:lang", "fr-FR");
            fail("Created attribute with name xml:lang");
        }
        catch (NamespaceException ex) {
            // success    
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
            new Attribute("xml:base", "http://www.nothteXMLNamespace", 
              "http://www.w3.org/");
            fail("remapped xml prefix");
         }
         catch (NamespaceException e) {
             // success    
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
        catch (IllegalDataException success) {}

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
        Attribute c3 = new Attribute("skjlchsakdjh", "sajdh sajh ");

        assertEquals(c1, c1);
        assertEquals(c1.hashCode(), c1.hashCode());
        assertTrue(!c1.equals(c2));
        assertTrue(!c1.equals(c3));
        assertTrue(!c1.equals(null));
    }

    public void testTypeEquals() {
        assertEquals(Attribute.Type.CDATA, Attribute.Type.CDATA);
        assertTrue(!Attribute.Type.CDATA.equals(Attribute.Type.NMTOKEN));
        assertTrue(!Attribute.Type.CDATA.equals(null));
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
        catch (IllegalDataException success) {}


        // Two high-halves
        try {
          new Attribute("surrogate", "test: \uD8F5\uD8F5  ");
          fail("Should raise an IllegalDataException");
        }
        catch (IllegalDataException success) {}

        // One high-half
        try {
           new Attribute("surrogate", "test: \uD8F5  ");
           fail("Should raise an IllegalDataException");
         }
         catch (IllegalDataException success) {}

        // One low half
         try {
            new Attribute("surrogate", "test: \uDF80  ");
            fail("Should raise an IllegalDataException");
          }
          catch (IllegalDataException success) {}

        // Low half before high half
         try {
            new Attribute("surrogate", "test: \uDCF5\uD8F5  ");
            fail("Should raise an IllegalDataException");
          }
          catch (IllegalDataException success) {}


    }
    
    public void testNullNamespace() {
        Attribute a = new Attribute("red:prefix", 
          "http://www.example.com", "data");
        a.setNamespace(null, null);
        assertEquals(a.getNamespaceURI(), "");
        assertEquals(a.getNamespacePrefix(), "");
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
          "/fred",
          ""
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
            catch (MalformedURIException ex) {
               // Success   
            }
            catch (NamespaceException ex) {
               // Success   
            }
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

}