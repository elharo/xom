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
import nu.xom.Attribute;
import nu.xom.NamespaceException;

/**
 * <p>
 * 
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d18
 *
 */
public class NamespacesTest extends XOMTestCase {

    public NamespacesTest() {
        super("Namespaces tests");
    }

    public NamespacesTest(String name) {
        super(name);
    }

    private Element someNamespaces;
    private Element noNamespaces;
    private Element severalNamespaces;


    protected void setUp() {
        noNamespaces = new Element("test");
        
        someNamespaces = new Element("test");
        someNamespaces.addNamespaceDeclaration("xlink", 
         "http://www.w3.org/2001/xlink");
        someNamespaces.addNamespaceDeclaration("xsl", 
         "http://www.w3.org/1999/xslt");
         
        severalNamespaces = new Element("test", "http://www.example.com/");
        severalNamespaces.addAttribute(
          new Attribute("xlink:type", 
         "http://www.w3.org/2001/xlink", "simple"));
        severalNamespaces.addNamespaceDeclaration("xsl", 
         "http://www.w3.org/1999/xslt");
        severalNamespaces.addNamespaceDeclaration("", 
         "http://www.example.com/");
        severalNamespaces.addNamespaceDeclaration("xlink", 
         "http://www.w3.org/2001/xlink");
         
        
        
    }
    
    public void testSetNamespacePrefixInConflictWithAdditionalNamespaceDeclaration() {
        someNamespaces.setNamespaceURI("http://www.example.net");
        try {
            someNamespaces.setNamespacePrefix("xsl");
            fail("changed prefix to conflict with additional namespace declaration");
        }
        catch (NamespaceException ex) {
            // success    
        }
        
    }
    
    public void testSetNamespaceURIInConflictWithAdditionalNamespaceDeclaration() {
        someNamespaces.setNamespaceURI("http://www.w3.org/2001/xlink");
        someNamespaces.setNamespacePrefix("xlink");
        try {
            someNamespaces.setNamespaceURI("http://www.example.net");
            fail("changed namespace URI to conflict with additional namespace declaration");
        }
        catch (NamespaceException ex) {
            // success    
        }
        
    }
    
    public void testXMLNamespace() {
        assertEquals(
          "http://www.w3.org/XML/1998/namespace",
          noNamespaces.getNamespaceURI("xml")
        );    
        assertEquals(
          "http://www.w3.org/XML/1998/namespace",
          severalNamespaces.getNamespaceURI("xml")
        );    
    }
    
    public void testXMLNSNamespace() {
        assertEquals("", noNamespaces.getNamespaceURI("xmlns"));    
        assertEquals("", severalNamespaces.getNamespaceURI("xmlns"));   
    }
    
    public void testCantUseXMLNSPrefix() {
        try {
            noNamespaces.addNamespaceDeclaration(
              "xmlns", "http://www.w3.org/2000/xmlns/");
            fail("added xmlns prefix");  
        }  
        catch (NamespaceException ex) {
            // success   
        }
        try {
            noNamespaces.addNamespaceDeclaration("xmlns", "http://www.example.com");
            fail("added xmlns prefix");  
        }  
        catch (NamespaceException ex) {
            // success   
        }
    }
    
    public void testCantUseXMLPrefix() {
        try {
            noNamespaces.addNamespaceDeclaration(
              "xml", "http://www.example.com");
            fail("added xmlns prefix");  
        }  
        catch (NamespaceException ex) {
            // success   
        }
    }
    
    public void testCanUseXMLPrefix() {
        noNamespaces.addNamespaceDeclaration(
          "xml", "http://www.w3.org/XML/1998/namespace");
        assertEquals(1, noNamespaces.getNamespaceDeclarationCount());
    }
    
    public void testIndexedAccess() {
     
        assertEquals("", noNamespaces.getNamespacePrefix(0));
        
        assertNotNull(someNamespaces.getNamespacePrefix(0));
        assertNotNull(someNamespaces.getNamespacePrefix(1));
        assertNotNull(someNamespaces.getNamespacePrefix(2));
        
        assertNotNull(severalNamespaces.getNamespacePrefix(0));
        assertNotNull(severalNamespaces.getNamespacePrefix(1));
        assertNotNull(severalNamespaces.getNamespacePrefix(2));
        try {
            severalNamespaces.getNamespacePrefix(3);
            fail("Got a namespace 3");
        }
        catch (IndexOutOfBoundsException ex) {
           // success;   
        }
        
        
    }

    public void testSize() {
        assertEquals(1, noNamespaces.getNamespaceDeclarationCount());
        assertEquals(3, someNamespaces.getNamespaceDeclarationCount());
        assertEquals(3, severalNamespaces.getNamespaceDeclarationCount());
     }

    public void testDefaultNamespace() {
        Element html = new Element("html", "http://www.w3.org/1999/xhtml");
        assertEquals(1, html.getNamespaceDeclarationCount());
        assertEquals("", html.getNamespacePrefix(0));
        assertEquals("http://www.w3.org/1999/xhtml", html.getNamespaceURI(""));
     }


   public void testGetByPrefix() {
    
        assertEquals("http://www.w3.org/2001/xlink", 
          someNamespaces.getNamespaceURI("xlink"));
        assertEquals("http://www.w3.org/1999/xslt", 
          someNamespaces.getNamespaceURI("xsl"));
        assertNull(someNamespaces.getNamespaceURI("fo"));
        assertNull(noNamespaces.getNamespaceURI("xsl"));
        assertEquals("", someNamespaces.getNamespaceURI(""));
        assertEquals("", noNamespaces.getNamespaceURI(""));

   }
   
   public void testGetNamespaceDeclarationCount() {
        Element test = new Element("test");
        assertEquals(1, test.getNamespaceDeclarationCount());
        test.setNamespaceURI("http://www.example.com");
        assertEquals(1, test.getNamespaceDeclarationCount());
        test.addAttribute(new Attribute("test", "test"));
        assertEquals(1, test.getNamespaceDeclarationCount());
        test.addAttribute(new Attribute("xlink:type", "http://www.w3.org/2001/xlink", "value"));
        assertEquals(2, test.getNamespaceDeclarationCount());
        test.addAttribute(new Attribute("xlink:href", "http://www.w3.org/2001/xlink", "value"));
        assertEquals(2, test.getNamespaceDeclarationCount());
        test.addNamespaceDeclaration("xlink", "http://www.w3.org/2001/xlink");
        assertEquals(2, test.getNamespaceDeclarationCount());
        test.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/xmlschema-instance");
        assertEquals(3, test.getNamespaceDeclarationCount());
           
   }

   public void testRemoving() {
    
        assertEquals("http://www.w3.org/2001/xlink", 
          severalNamespaces.getNamespaceURI("xlink"));
        assertEquals("http://www.w3.org/1999/xslt", 
          severalNamespaces.getNamespaceURI("xsl"));
        assertEquals("http://www.example.com/", 
          severalNamespaces.getNamespaceURI(""));

        severalNamespaces.removeNamespaceDeclaration("xlink");
        severalNamespaces.removeNamespaceDeclaration("xsl");
        severalNamespaces.removeNamespaceDeclaration("");
        severalNamespaces.removeNamespaceDeclaration("nosuchdeclaration");

        assertEquals("http://www.w3.org/2001/xlink", 
          severalNamespaces.getNamespaceURI("xlink"));
        assertNull(severalNamespaces.getNamespaceURI("xsl"));
        assertEquals("http://www.example.com/", 
          severalNamespaces.getNamespaceURI(""));

   }

   public void testAddSameNamespaceDeclaration() {
       Element e = new Element("test", "http://www.example.com");
       
       try {
           e.addNamespaceDeclaration("", "http://www.red.com");
           fail("added conflicting default namespace");   
       }
       catch (NamespaceException ex) {
           // success   
       }
       
       e.addNamespaceDeclaration("", "http://www.example.com");
       assertEquals("http://www.example.com", e.getNamespaceURI(""));
       assertEquals(1, e.getNamespaceDeclarationCount());
   } 



   public void testAddEmptyNamespaceDeclaration() {
       Element e = new Element("test");
       
       try {
           e.addNamespaceDeclaration("", "http://www.example.com");
           fail("added conflicting default namespace");   
       }
       catch (NamespaceException ex) {
           // success   
       }
       
       e.setNamespaceURI("http://www.example.com");
       e.setNamespacePrefix("pre");
       e.addNamespaceDeclaration("", "http://www.example.net");

       assertEquals("http://www.example.net", e.getNamespaceURI(""));
       
   } 

   public void testAddNullNamespaceDeclaration() {
       Element e = new Element("test");
       
       try {
           e.addNamespaceDeclaration(null, "http://www.example.com");
           fail("added null prefix");   
       }
       catch (NamespaceException ex) {
           // success   
       }
       
       e.setNamespaceURI("http://www.example.com");
       e.setNamespacePrefix("pre");
       e.addNamespaceDeclaration(null, "http://www.example.net");

       assertEquals("http://www.example.net", e.getNamespaceURI(""));
       
   } 

   public void testUndeclareDefaultNamespace() {
       Element e2 = new Element("pre:test", "http://www.example.net");
       
       e2.addNamespaceDeclaration("", "");
       assertEquals("", e2.getNamespaceURI(""));
       
   } 

   public void testAdding() {
    
        try {
           noNamespaces.addNamespaceDeclaration("", "http://www.example.com/");
           fail("added conflicting default namespace");
        }
        catch (NamespaceException ex) {
           // success   
        }

        try {
           severalNamespaces.addNamespaceDeclaration(
             "xlink", "http://www.example.com/");
           fail("added conflicting attribute prefix namespace");
        }
        catch (NamespaceException ex) {
           // success   
        }

        try {
           someNamespaces.addNamespaceDeclaration("xsl", "http://www.example.com/");
           fail("added conflicting additional prefix namespace");
        }
        catch (NamespaceException ex) {
           // success   
        }

        someNamespaces.addNamespaceDeclaration("foo", "http://www.example.com/");
        assertEquals("http://www.example.com/", someNamespaces.getNamespaceURI("foo"));

   }

   public void testReplacingNamespaceDeclaration() {
    
        assertEquals("http://www.w3.org/2001/xlink", someNamespaces.getNamespaceURI("xlink"));
        try {
            someNamespaces.addNamespaceDeclaration("xlink", "http://www.example.com/");
            fail("Redeclared without removal");   
        }
        catch (NamespaceException ex) {
           // success   
        }
        someNamespaces.removeNamespaceDeclaration("xlink");
        assertNull(someNamespaces.getNamespaceURI("xlink"));
        someNamespaces.addNamespaceDeclaration("xlink", "http://www.example.com/");
        assertEquals("http://www.example.com/", someNamespaces.getNamespaceURI("xlink"));
        

   }

   public void testSetPrefix() {
    
        try {
            Attribute a = severalNamespaces.getAttribute(0);
            a.setNamespace("xsl", "http://www.example.com/");
            fail("added conflicting attribute prefix");
        }
        catch (NamespaceException ex) {
           // success   
        }

   }


   public void testElementConflict() {
    
        Element element = new Element("pre:test", "http://www.example.com/");
        element.addNamespaceDeclaration("pre", "http://www.example.com/");
        try {
           element.setNamespaceURI("http://www.yahoo.com");
           fail("changed to conflicting element namespace");
        }
        catch (NamespaceException ex) {
           // success   
        }

   }

   public void testAttributeConflict() {
    
        Element element = new Element("test");
        element.addNamespaceDeclaration("pre", "http://www.example.com/");
        Attribute a = new Attribute("pre:test", "http://www.example.com/", "value");
        element.addAttribute(a);
        try {
           a.setNamespace("pre", "http://www.yahoo.com/");
           fail("changed to conflicting attribute namespace");
        }
        catch (NamespaceException ex) {
           // success   
        }

   }


   public void testSetAttributePrefix() {
    
        try {
           severalNamespaces.setNamespacePrefix("xlink");
           fail("added conflicting element prefix");
        }
        catch (NamespaceException ex) {
           // success   
        }

        try {
           severalNamespaces.setNamespacePrefix("xsl");
           fail("added conflicting element prefix");
        }
        catch (NamespaceException ex) {
           // success   
        }

   }
   
   /*
    * Test document submitted by Laurent Bihanic
    <b xmlns="urn:x-xom:b" id="b">
      <c:c xmlns:c="urn:x-xom:c" id="c" />
    </b>
   */
   public void testLaurent() {
    
     Element b = new Element("b", "urn:x-xom:b");
     b.addAttribute(new Attribute("id", "b"));
    
     Element c = new Element("c:c", "urn:x-xom:c");
     c.addAttribute(new Attribute("id", "c"));
     b.appendChild(c);
     
     assertEquals("urn:x-xom:b", b.getNamespaceURI());
     assertEquals("urn:x-xom:c", c.getNamespaceURI());
     assertEquals("urn:x-xom:b", b.getNamespaceURI(""));
     assertEquals("urn:x-xom:b", c.getNamespaceURI(""));
     assertEquals("urn:x-xom:c", c.getNamespaceURI("c"));
     
   }



   public void testGetNamespaceURIByPrefix() {

      Element a = new Element("a");
      a.addNamespaceDeclaration("foo", "urn:foo");
      Element b = new Element("b");
      Element c = new Element("c");
      Element d = new Element("foo:d", "urn:foo");
      a.appendChild(b);
      b.appendChild(c);
      c.appendChild(d);

      assertEquals("urn:foo", a.getNamespaceURI("foo"));
      assertEquals("urn:foo", b.getNamespaceURI("foo"));
      assertEquals("urn:foo", c.getNamespaceURI("foo"));
      assertEquals("urn:foo", d.getNamespaceURI("foo"));
   }

}