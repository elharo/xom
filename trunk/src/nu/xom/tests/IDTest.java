/* Copyright 2005 Elliotte Rusty Harold
   
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

import java.io.IOException;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.IllegalDataException;
import nu.xom.Nodes;
import nu.xom.ParsingException;

/**
 * <p>
 * Tests for <code>xml:id</code> attributes.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.1d3
 *
 */
public class IDTest extends XOMTestCase {

    
    public IDTest(String name) {
        super(name);
    }

    
    public void testBuilderRejectsNonNCNameXmlIdAttributes() 
      throws IOException {
        
        Builder builder = new Builder();
        String data = "<root xml:id='p 2'/>";
        try {
            builder.build(data, null);
            fail("Allowed non-NCName for xml:id");
        }
        catch (ParsingException success) {
            assertNotNull(success.getMessage());
        }
        
    }
    
    
    public void testIDMustBeNCName() {
        
        Attribute id = new Attribute("xml:id", 
          "http://www.w3.org/XML/1998/namespace", "name");
        assertEquals("name", id.getValue());
        
        try {
            id.setValue("not a name");
            fail("allowed non-NCName as value of xml:id attribute");
        }
        catch (IllegalDataException success) {
            assertNotNull(success.getMessage());
            assertEquals("not a name", success.getData());
        }
        
    }

    
    public void testNameSetIDMustBeNCName() {
        
        Attribute id = new Attribute("id", "not a name");
        
        try {
            id.setNamespace("xml", 
              "http://www.w3.org/XML/1998/namespace");
            fail("allowed non-NCName as value of xml:id attribute");
        }
        catch (IllegalDataException success) {
            assertNotNull(success.getMessage());
            assertEquals("not a name", success.getData());
        }
        
    }

    
    public void testBuilderNormalizesXmlIdAttributes() 
      throws ParsingException, IOException {
        
        Builder builder = new Builder();
        String data = "<root xml:id='  p2  '/>";
        Document doc = builder.build(data, null);
        Element root = doc.getRootElement();
        String value = root.getAttributeValue("id", 
          "http://www.w3.org/XML/1998/namespace");
        assertEquals("p2", value);
        
    }

    
    public void testBuiltXmlIdAttributeHasTypeId() 
      throws ParsingException, IOException {
        
        Builder builder = new Builder();
        String data = "<root xml:id='  p2  '/>";
        Document doc = builder.build(data, null);
        Element root = doc.getRootElement();
        Attribute id = root.getAttribute("id", 
          "http://www.w3.org/XML/1998/namespace");
        assertEquals(Attribute.Type.ID, id.getType());
        
    }
    
    
    public void testConstructedXmlIdAttributeHasTypeId() 
      throws ParsingException, IOException {
        
        Attribute id = new Attribute("xml:id", 
          "http://www.w3.org/XML/1998/namespace", "p2");
        assertEquals(Attribute.Type.ID, id.getType());
        
    }
    
    
    public void testNamespaceSetXmlIdAttributeHasTypeId() {
        
        Attribute id = new Attribute("id", "p2");
        id.setNamespace("xml", "http://www.w3.org/XML/1998/namespace");
        assertEquals(Attribute.Type.ID, id.getType());
        
    }

    
    public void testNameSetXmlIdAttributeHasTypeId() {
        
        Attribute id = new Attribute("xml:space", 
          "http://www.w3.org/XML/1998/namespace", "preserve");
        id.setLocalName("id");
        assertEquals(Attribute.Type.ID, id.getType());
        
    }

    
    public void testNameSetXmlIdAttributeMustBeNCName() {
        
        Attribute id = new Attribute("xml:space", 
          "http://www.w3.org/XML/1998/namespace", "not a name");
        try {
            id.setLocalName("id");
            fail("Allowed non-NCNAME ID");
        }
        catch (IllegalDataException success) {
            assertNotNull(success.getMessage());
        }
        
    }

    
    public void testCantChangeTypeOfXMLIDAttribute() {
        
        Attribute id = new Attribute("xml:id", 
          "http://www.w3.org/XML/1998/namespace", "p2");
        
        try {
            id.setType(Attribute.Type.CDATA);
            fail("changed xml:id attribute to CDATA");
        }
        catch (IllegalDataException success) {
            assertNotNull(success.getMessage());
        }
        assertEquals(Attribute.Type.ID, id.getType());
        
    }

    
    public void testCantChangeValueOfXMLIDAttributeToNonNCName() {
        
        Attribute id = new Attribute("xml:id", 
          "http://www.w3.org/XML/1998/namespace", "p2");
        Attribute id2 = new Attribute(id);
        try {
            id.setValue("not a name");
            fail("Allowed non-name for xml:id");
        }
        catch (IllegalDataException success) {
            assertNotNull(success.getMessage());
        }
        
        // nothing changed
        assertEquals(id, id2);
        
    }

    
    public void testXPathRecognizesXmlIDAttributes() 
      throws ParsingException, IOException {
        
        Element root = new Element("root");
        Document doc = new Document(root);
        Element child = new Element("child");
        root.appendChild(child);
        Attribute id = new Attribute("id", "p2");
        child.addAttribute(id);
        id.setNamespace("xml", "http://www.w3.org/XML/1998/namespace");
        Nodes result = doc.query("id('p2')");
        assertEquals(1, result.size());
        assertEquals(child, result.get(0));
        
    }

    
    // need a test that this works with XInclude????
    
}
