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

import java.io.File;
import java.io.IOException;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Comment;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.IllegalAddException;
import nu.xom.IllegalNameException;
import nu.xom.MalformedURIException;
import nu.xom.MultipleParentException;
import nu.xom.NamespaceException;
import nu.xom.NoSuchAttributeException;
import nu.xom.Node;
import nu.xom.ParsingException;
import nu.xom.ProcessingInstruction;
import nu.xom.Text;


/**
 * <p>
 *   Tests for the <code>Element</code> class.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d23
 *
 */
public class ElementTest extends XOMTestCase {

    private Element element;
    private Element child1;
    private Text    child2;
    private Comment child3;
    private Element child4;
    private Element child5;
    private String[] legal = {"http://www.is.edu/sakdsk#sjadh",
        "http://www.is.edu/sakdsk?name=value&name=head",
        "uri:isbn:0832473864",
        "http://www.examples.com:80",
        "http://www.examples.com:80/",
        "http://www.is.edu/%20sakdsk#sjadh"};
         
    private String[] illegal = {
          "http://www.is.edu/%sakdsk#sjadh",
          "http://www.is.edu/k\u0245kakdsk#sjadh",
          "!@#$%^&*()",
          "fred",
          "#fred",
          "/fred"
        }; 
        

    protected void setUp() {
        
        child1 = new Element("test");
        child2 = new Text("test2");
        child3 = new Comment("test3");
        child4 = new Element("pre:test", "http://www.example.com");
        child5 = new Element("test", "http://www.example.com");
        element = new Element("name");
        
        element.appendChild(child1);
        element.appendChild(child2);
        element.appendChild(child3);
        element.appendChild(child4);
        element.appendChild(child5);
        element.appendChild("  \r\n");

    }

    public ElementTest(String name) {
        super(name);
    }
    
    public void testGetChildElementsNull() {
        Elements elements = element.getChildElements(
          "", "http://www.example.com");
        assertEquals(2, elements.size());
        elements = element.getChildElements("", "");
        assertEquals(1, elements.size());
        elements = element.getChildElements(null, 
          "http://www.example.com");
        assertEquals(2, elements.size());
        elements = element.getChildElements("", null);
        assertEquals(1, elements.size());     
    }
    
    public void testGetFirstChildElement() {
        Element first = element.getFirstChildElement("test");
        assertEquals(child1, first);
        
        first = element.getFirstChildElement(
          "test", "http://www.example.com");
        assertEquals(child4, first);
        
        assertNull(element.getFirstChildElement("nonesuch"));
        assertNull(element.getFirstChildElement("pre:test"));
        assertNull(
          element.getFirstChildElement(
          "nonesuch", "http://www.example.com")
        );
        
    }

    public void testConstructor1() {
        String name = "sakjdhjhd";
        Element e = new Element(name);
        
        assertEquals(name, e.getLocalName());
        assertEquals(name, e.getQualifiedName());
        assertEquals("",   e.getNamespacePrefix());
        assertEquals("",   e.getNamespaceURI());
    }

    public void testConstructor2() {
        String name = "sakjdhjhd";
        String uri = "http://www.something.com/";
        Element e = new Element(name, uri);
        
        assertEquals(name, e.getLocalName());
        assertEquals(name, e.getQualifiedName());
        assertEquals("",   e.getNamespacePrefix());
        assertEquals(uri,  e.getNamespaceURI());
    }

   public void testConstructor3() {
        String name = "red:sakjdhjhd";
        String uri = "http://www.something.com/";
        Element e = new Element(name, uri);
        
        assertEquals("sakjdhjhd", e.getLocalName());
        assertEquals(name, e.getQualifiedName());
        assertEquals("red", e.getNamespacePrefix());
        assertEquals(uri, e.getNamespaceURI());
    }

    public void testAllowEmptyNamespace() {
        String name = "sakjdhjhd";
        String uri = "http://www.something.com/";
        Element e = new Element(name, uri);
        
        e.setNamespaceURI("");
        
        assertEquals("", e.getNamespaceURI());
        
    }

    public void testToString() {
        String name = "sakjdhjhd";
        String uri = "http://www.something.com/";
        Element e = new Element(name, uri);
        
        String s = e.toString();
        assertTrue(s.startsWith("[nu.xom.Element: "));
        assertTrue(s.endsWith("]"));
        assertTrue(s.indexOf(name) != -1);       
    }

    public void testToXML() {
        String name = "sakjdhjhd";
        String uri = "http://www.something.com/";
        Element e = new Element(name, uri);
        
        String s = e.toXML();
        assertTrue(s.endsWith("/>"));
        assertTrue(s.startsWith("<" + name));
        assertTrue(s.indexOf(uri) != -1);
        assertTrue(s.indexOf("xmlns=") != -1);       
    }

    public void testToXML2() throws ParsingException, IOException {
        Builder builder = new Builder();
        File f = new File("data/soapresponse.xml");   
        Document doc = builder.build(f); 
        Element root = doc.getRootElement();
        String  form = root.toXML();
        Document doc2 
          = builder.build(form, "file://" + f.getCanonicalPath());
        Element root2 = doc2.getRootElement();
        assertEquals(root, root2);
         
    }
    
    public void testToXMLWithXMLLangAttribute() {
        Element e = new Element("e");
        e.addAttribute(new Attribute("xml:lang", "http://www.w3.org/XML/1998/namespace", "en"));
        assertEquals("<e xml:lang=\"en\" />", e.toXML());
    }    

    public void testAllowNullNamespace() {
        String name = "sakjdhjhd";
        String uri = "http://www.something.com/";
        Element e = new Element(name, uri);
        
        e.setNamespaceURI(null);
        
        assertEquals("", e.getNamespaceURI());
        
    }


    public void testCantInsertDoctype() {
        String name = "sakjdhjhd";
        String uri = "http://www.something.com/";
        Element e = new Element(name, uri);
        
        try {
            e.appendChild(new DocType(name));
            fail("Appended a document type declaration to an element");
        }
        catch (IllegalAddException ex) {
            // success   
            assertNotNull(ex.getMessage());
        }
        
    }



    public void testXMLNamespace() {
        
        String name = "red:sakjdhjhd";
        String uri = "http://www.red.com/";
        Element e = new Element(name, uri);
        
        String xmlNamespace = "http://www.w3.org/XML/1998/namespace";
        
        assertEquals(xmlNamespace, e.getNamespaceURI("xml"));
        
        try {
            e.addNamespaceDeclaration("xml", "http://www.yahoo.com/");
            fail("remapped xml prefix!");
        }
        catch (NamespaceException ex) {
            // success   
            assertNotNull(ex.getMessage());
        }
        assertEquals(e.getNamespaceURI("xml"), xmlNamespace);
        
        // This should succeed
        e.addNamespaceDeclaration("xml", xmlNamespace);
        assertEquals(xmlNamespace, e.getNamespaceURI("xml"));

        
    }

    public void testUndeclareDefaultNamespace() {
        
        String name = "red:sakjdhjhd";
        String uri = "http://www.red.com/";
        Element child = new Element(name, uri);
        Element parent = new Element(
          "parent", "http://www.example.com");

        assertEquals("http://www.example.com", 
            parent.getNamespaceURI(""));
        
        parent.appendChild(child);
        child.addNamespaceDeclaration("", "");
         
        assertEquals("", child.getNamespaceURI(""));
        assertEquals(
          "http://www.example.com", 
          parent.getNamespaceURI(""));
        
        Element child2 = new Element("name", "http://www.default.com");
        parent.appendChild(child2);
        
        // should not be able to undeclare default namespace on child2
        try {
            child2.addNamespaceDeclaration("", "");
            fail("Illegally undeclared default namespace");
        }
        catch (NamespaceException ex) {
            // success   
            assertNotNull(ex.getMessage());
        }
        
    }



    public void testUnsetDefaultNamespaceWithAttribute() {
        
        String name = "sakjdhjhd";
        String uri = "http://www.red.com/";
        Element element = new Element(name, uri);
        element.addAttribute(new Attribute("test", "test"));
        element.setNamespaceURI("");
        assertEquals("", element.getNamespaceURI(""));
        
    }


    public void testSetNamespaceWithAttribute() {
        
        String name = "red:sakjdhjhd";
        String uri = "http://www.red.com/";
        Element element = new Element(name, uri);
        element.addAttribute(
          new Attribute("red:attribute", uri, "test"));
        element.setNamespaceURI(uri);
        assertEquals(uri, element.getNamespaceURI());
        assertEquals(uri, element.getNamespaceURI("red"));
        
    }


    public void testAddNamespaceToElementWithAttribute() {
        
        String name = "a";
        String uri = "http://www.w3.org/1999/xhtml";
        Element element = new Element(name);
        element.addAttribute(
          new Attribute("href", "http://www.elharo.com"));
        element.setNamespaceURI(uri);
        assertEquals(uri, element.getNamespaceURI());
        
    }

    public void testSameNamespaceForElementAndAttribute() {
        
        String name = "a";
        String uri = "http://www.w3.org/1999/xhtml";
        Element element = new Element(name);
        element.addAttribute(
          new Attribute("html:href", uri, "http://www.elharo.com"));
        element.setNamespaceURI("http://www.example.com");
        element.setNamespacePrefix("pre");
        element.setNamespaceURI(uri);
        element.setNamespacePrefix("html");
        assertEquals(uri, element.getNamespaceURI());
        assertEquals("html", element.getNamespacePrefix());
        
    }



    public void testNamespaceMappings() {
        
        String name = "red:sakjdhjhd";
        String uri = "http://www.red.com/";
        Element e = new Element(name, uri);
        e.addNamespaceDeclaration("blue", "http://www.blue.com/");
        e.addNamespaceDeclaration("green", "http://www.green.com/");
        Attribute a1 = new Attribute("test", "test");
        Attribute a2 = new Attribute(
          "pre1:green", "http://www.green.com/", "data");
        Attribute a3 = new Attribute(
          "yellow:sfsdadf", "http://www.yellow.com/", "data");
        e.addAttribute(a1);
        e.addAttribute(a2);
        e.addAttribute(a3);
        
        assertEquals("http://www.red.com/", e.getNamespaceURI("red"));
        assertEquals(
          "http://www.green.com/", 
          e.getNamespaceURI("green"));
        assertEquals(
          "http://www.blue.com/", 
          e.getNamespaceURI("blue"));
        assertEquals(
          "http://www.green.com/", 
          e.getNamespaceURI("pre1"));
        assertEquals(
          "http://www.yellow.com/", 
          e.getNamespaceURI("yellow"));
        
        
        Element e2 = new Element(
          "mauve:child", "http://www.mauve.com");
        e.appendChild(e2);
        assertEquals(
          "http://www.red.com/", 
          e2.getNamespaceURI("red"));
        assertEquals(
          "http://www.blue.com/", 
          e2.getNamespaceURI("blue"));
        assertEquals(
          "http://www.green.com/", 
          e2.getNamespaceURI("green"));
        assertEquals(
          "http://www.green.com/", 
          e2.getNamespaceURI("pre1"));
        assertEquals(
          "http://www.yellow.com/", 
          e2.getNamespaceURI("yellow"));
        assertNull(e2.getNamespaceURI("head"));
        
        
        try {           
            e.addNamespaceDeclaration("pre1", "http://www.blue2.com");
            fail("Added conflicting namespace");    
        }
        catch (NamespaceException ex) {
            // success   
            assertNotNull(ex.getMessage());
        }
        
        try {
            Attribute a4 = new Attribute(
              "pre1:mauve", "http://www.sadas.com/", "data");
            e.addAttribute(a4);
            fail("Added conflicting namespace");    
        }
        catch (NamespaceException ex) {
            // success   
            assertNotNull(ex.getMessage());
        }
        
        // can't add conflicting attribute from 
        // different namespace even with identical QName
        try {
            Attribute a4 = new Attribute(
              "pre1:green", "http://www.example.com/", "data");
            e.addAttribute(a4);
        }
        catch (NamespaceException ex) {
            // success   
            assertNotNull(ex.getMessage());
        }
        
        e.removeNamespaceDeclaration("green");
        assertNull(e.getNamespaceURI("green"));
        e.addNamespaceDeclaration("green", "http://www.green2.com/");
        assertEquals(
          "http://www.green2.com/", 
          e.getNamespaceURI("green"));
         
    }

    public void testAttributes() {
        
        String name = "red:sakjdhjhd";
        String uri = "http://www.red.com/";
        Element e = new Element(name, uri);

        Attribute a1 = new Attribute("name", "simple");
        Attribute a2 = new Attribute(
          "pre1:green", "http://www.green.com/", "data");

        e.addAttribute(a1);
        e.addAttribute(a2);

        assertEquals(
          a2, 
          e.getAttribute("green", "http://www.green.com/"));
        assertEquals(a1, e.getAttribute("name"));
        assertEquals(a1, e.getAttribute("name", ""));
        assertEquals(e, a1.getParent());
        assertEquals("simple", e.getAttribute("name").getValue());
        
        a1.detach();
        assertNull(a1.getParent());
        assertNull(e.getAttribute("name"));
        
        
        e.removeAttribute(a2);
        assertNull(a2.getParent());
        assertNull( e.getAttribute("green", "http://www.green.com/"));
        
    }


    public void testRemoveNullAttribute() {
        
        String name = "red:sakjdhjhd";
        String uri = "http://www.red.com/";
        Element e = new Element(name, uri);

        Attribute a1 = new Attribute("name", "simple");
        Attribute a2 = new Attribute(
          "pre1:green", "http://www.green.com/", "data");

        e.addAttribute(a1);
        e.addAttribute(a2);

        try {
            e.removeAttribute(null);
            fail("Removed Null Attribute");
        }
        catch (NullPointerException ex) {
            // success   
            assertNotNull(ex.getMessage());
        }
        
    }

    public void testRemoveAttributeFromElementWithNoAttributes() {
        
        String name = "red:sakjdhjhd";
        String uri = "http://www.red.com/";
        Element e = new Element(name, uri);

        Attribute a1 = new Attribute("name", "simple");

        try {
            e.removeAttribute(a1);
            fail("Removed Attribute that didn't belong");
        }
        catch (NoSuchAttributeException ex) {
            // success   
            assertTrue(ex.getMessage().indexOf(a1.getQualifiedName()) > 0);
        }
        
    }

    public void testRemoveAttributeFromElementWithDifferentAttributes() {
        
        String name = "red:sakjdhjhd";
        String uri = "http://www.red.com/";
        Element e = new Element(name, uri);
        e.addAttribute(new Attribute("name", "value"));
        Attribute a1 = new Attribute("name", "simple");

        try {
            e.removeAttribute(a1);
            fail("Removed Attribute that didn't belong");
        }
        catch (NoSuchAttributeException ex) {
            // success   
            assertTrue(ex.getMessage().indexOf(a1.getQualifiedName()) > 0);
        }
        
    }



    public void testGetValue() {
        
        String name = "red:sakjdhjhd";
        String uri = "http://www.red.com/";
        Element e = new Element(name, uri);

        assertEquals(e.getValue(), "");
        
        e.appendChild(new Text("data"));
        assertEquals(e.getValue(), "data");
        e.appendChild(new Text(" moredata"));
        assertEquals(e.getValue(), "data moredata");
        e.appendChild(new Comment(" moredata"));
        assertEquals(e.getValue(), "data moredata");
        e.appendChild(
          new ProcessingInstruction("target", "moredata"));
        assertEquals(e.getValue(), "data moredata");
        
        Element e2 = new Element("child");
        e.appendChild(e2);
        assertEquals("data moredata", e.getValue());
        e2.appendChild(new Text("something"));
        assertEquals("data moredatasomething", e.getValue());
        
    }

    public void testSetLocalName() {
        
        String name = "red:sakjdhjhd";
        String uri = "http://www.red.com/";
        Element e = new Element(name, uri);

        assertEquals("sakjdhjhd", e.getLocalName());
        
        e.setLocalName("dude");
        assertEquals("dude", e.getLocalName());
        e.setLocalName("digits__");
        assertEquals("digits__", e.getLocalName());
        e.setLocalName("digits1234");
        assertEquals("digits1234", e.getLocalName());
        e.setLocalName("digits-z");
        assertEquals("digits-z", e.getLocalName());
        
        
        try {
            e.setLocalName("spaces ");
            fail("Local name allowed to contain spaces");   
        }
        catch (IllegalNameException ex) {
            // success   
            assertNotNull(ex.getMessage());
        }
        try {
            e.setLocalName("digits:test");
            fail("Local name allowed to contain colon");    
        }
        catch (NamespaceException ex) {
            // success   
            assertNotNull(ex.getMessage());
        }
        try {
            e.setLocalName("digits!test");
            fail("Local name allowed to contain bang"); 
        }
        catch (IllegalNameException ex) {
            // success   
            assertNotNull(ex.getMessage());
        }
        try {
            e.setLocalName("digits\u0000test");
            fail("Local name allowed to contain null"); 
        }
        catch (IllegalNameException ex) {
            // success   
            assertNotNull(ex.getMessage());
        }
        
    }

    public void testSetNamespacePrefix() {
        
        String name = "red:sakjdhjhd";
        String uri = "http://www.red.com/";
        String prefix = "red";
        Element e = new Element(name, uri);

        assertEquals(prefix, e.getNamespacePrefix());
        
        e.setNamespacePrefix("dude");
        assertEquals("dude", e.getNamespacePrefix());
        e.setNamespacePrefix("digits__");
        assertEquals("digits__", e.getNamespacePrefix());
        e.setNamespacePrefix("digits1234");
        assertEquals("digits1234", e.getNamespacePrefix());
        e.setNamespacePrefix("digits-z");
        assertEquals("digits-z", e.getNamespacePrefix());
        e.setNamespacePrefix("");
        assertEquals("", e.getNamespacePrefix());
        e.setNamespacePrefix(null);
        assertEquals("", e.getNamespacePrefix());
        
        try {
            e.setNamespacePrefix("spaces ");
            fail("namespace prefix allowed to contain spaces"); 
        }
        catch (IllegalNameException ex) {
             // success   
            assertNotNull(ex.getMessage());
        }
        try {
            e.setNamespacePrefix("digits:test");
            fail("namespace prefix allowed to contain colon");  
        }
        catch (NamespaceException ex) {
            // success   
            assertNotNull(ex.getMessage());
        }
        try {
            e.setNamespacePrefix("digits!test");
            fail("namespace prefix allowed to contain bang");   
        }
        catch (IllegalNameException ex) {
            // success   
            assertNotNull(ex.getMessage());
        }
        try {
            e.setNamespacePrefix("digits\u0000test");
            fail("namespace prefix allowed to contain null");   
        }
        catch (IllegalNameException ex) {
            // success   
            assertNotNull(ex.getMessage());
        }
        
    }

    public void testSetNamespaceURI() {
        
        String name = "red:sakjdhjhd";
        String uri = "http://www.red.com/";
        Element e = new Element(name, uri);

        assertEquals(e.getNamespaceURI(), uri);
        
        for (int i = 0; i < legal.length; i++) {
            e.setNamespaceURI(legal[i]);          
            assertEquals(legal[i], e.getNamespaceURI());
        }
        
        for (int i = 0; i < illegal.length; i++) {
            try {
                e.setNamespaceURI(illegal[i]);
                fail("illegal namespace URI allowed");  
            }
            catch (MalformedURIException ex) {
                // success   
                assertNotNull(ex.getMessage());
            }
        }
             
    }
    

    public void 
      testSetNamespaceURIConflictsWithAdditionalNamespaceDeclaration() 
      {
        
        String name = "red:sakjdhjhd";
        String uri = "http://www.red.com/";
        Element e = new Element(name, uri);
        e.addNamespaceDeclaration("red", "http://www.red.com/");
        
        try {
            e.setNamespaceURI("http://www.example.com");
            fail("illegal namespace conflict");  
        }
        catch (NamespaceException ex) {
            // success   
            assertNotNull(ex.getMessage());
        }
        
    }
    
    public void testSetNamespaceURIConflictsWithAttributeNamespace() {
        
        String name = "red:sakjdhjhd";
        String uri = "http://www.red.com/";
        Element e = new Element(name, uri);
        e.addAttribute(
          new Attribute("red:test", "http://www.red.com/", "value"));
        
        try {
            e.setNamespaceURI("http://www.example.com");
            fail("illegal attribute namespace conflict");  
        }
        catch (NamespaceException ex) {
            // success   
            assertNotNull(ex.getMessage());
        }
        
    }
    
    public void testChangePrefix() {
        
        String name = "red:sakjdhjhd";
        String uri = "http://www.red.com/";
        Element element = new Element(name, uri);
        element.addNamespaceDeclaration("blue", "http://www.foo.com/");
        element.addAttribute(new Attribute("green:money", 
         "http://example.com/", "value"));
        element.addNamespaceDeclaration("purple", uri);
        element.addAttribute(
          new Attribute("mauve:money", uri, "value"));
        
        try {
            element.setNamespacePrefix("blue");
            fail("Conflicting prefix allowed against additional " +                "namespace declaration");  
        }
        catch (NamespaceException ex) {
            // success   
            assertNotNull(ex.getMessage());
        }
        
        try {
            element.setNamespacePrefix("green");
            fail("Conflicting prefix allowed against attribute");  
        }
        catch (NamespaceException ex) {
            // success   
            assertNotNull(ex.getMessage());
        }
        
        element.setNamespacePrefix("purple");
        assertEquals("purple", element.getNamespacePrefix());
        element.setNamespacePrefix("mauve");
        assertEquals("mauve", element.getNamespacePrefix());       
        
    }


    public void testDeclareNamespacePrefix() {
        
        String name = "red:sakjdhjhd";
        String uri = "http://www.red.com/";
        Element e = new Element(name, uri);

        for (int i = 0; i < legal.length; i++) {
            e.removeNamespaceDeclaration("prefix");          
            e.addNamespaceDeclaration("prefix", legal[i]);          
            assertEquals(legal[i], e.getNamespaceURI("prefix"));
        }
        
        for (int i = 0; i < illegal.length; i++) {
            try {
                e.addNamespaceDeclaration("prefix", illegal[i]);          
                fail("illegal namespace URI allowed " + illegal[i]);  
            }
            catch (MalformedURIException ex) {
            // success   
            assertNotNull(ex.getMessage());
            }
        }
        
    }

    public void testInsertChildUsingString() {
        
        String name = "red:sakjdhjhd";
        String uri = "http://www.red.com/";
        Element e = new Element(name, uri);
        
        Element e2 = new Element(
          "mauve:child", "http://www.mauve.com");
        e.insertChild(e2, 0);
        Element e3 = new Element(
          "mauve:child", "http://www.mauve.com");
        e.insertChild(e3, 0);

        e.insertChild("Hello", 0);
        assertEquals("Hello", e.getChild(0).getValue());

    }

    public void testInsertNull() {
        
        String name = "red:sakjdhjhd";
        String uri = "http://www.red.com/";
        Element e = new Element(name, uri);
        String data = null;
        try {
            e.insertChild(data, 0);
            fail("Inserted null");
        }
        catch (NullPointerException ex) {
            // success;   
        }
    }

    public void appendNullChild() {
        
        String name = "red:sakjdhjhd";
        String uri = "http://www.red.com/";
        Element e = new Element(name, uri);
        String data = null;
        try {
            e.appendChild(data);
            fail("Appended null");
        }
        catch (NullPointerException ex) {
            // success;   
        }
    }

    public void testInsertChild() {
        
        String name = "red:sakjdhjhd";
        String uri = "http://www.red.com/";
        Element e = new Element(name, uri);
        
        Element e2 = new Element("mv:child", "http://www.mauve.com");
        e.insertChild(e2, 0);
        Element e3 = new Element("mv:child", "http://www.mauve.com");
        e.insertChild(e3, 0);
        Element e4 = new Element("mv:child", "http://www.mauve.com");
        e3.insertChild(e4, 0);
        
        assertEquals(e3, e.getChild(0));
        
        try {
            Element root = new Element("root");
            Document doc = new Document(root);
            e.insertChild(doc, 0);
            fail("Added document as child");    
        }
        catch (IllegalAddException ex) {
            // success   
            assertNotNull(ex.getMessage());
        }
        try {
            e.insertChild(e2, 0);
            fail("Added child twice");  
        }
        catch (MultipleParentException ex) {
            // success   
            assertNotNull(ex.getMessage());
        }
        try {
            e.insertChild(e4, 0);
            fail("Added child twice");  
        }
        catch (MultipleParentException ex) {
            // success   
            assertNotNull(ex.getMessage());
        }
        try {
            e.insertChild((Node) null, 0);
            fail("Added null child");  
        }
        catch (NullPointerException ex) {
            // success   
        }
        try {
            e.insertChild(new Comment("test"), 20);
            fail("Added comment after end");    
        }
        catch (IndexOutOfBoundsException ex) {
            // success    
            assertNotNull(ex.getMessage()); 
        }
        try {
            e.insertChild(new Comment("test"), -20);
            fail("Added comment before start"); 
        }
        catch (IndexOutOfBoundsException ex) {
            // success   
            assertNotNull(ex.getMessage()); 
        }     
        
    }


    public void testInsertString() {
        
        Element element = new Element("test");
        element.insertChild("test", 0);

        String s = null;
        try {
            element.insertChild(s, 0);
            fail("Inserted string as null");
        }
        catch (NullPointerException ex) {
            // success
        }
        
        element.insertChild("" , 0);
        // empty node should be created
        assertEquals(2, element.getChildCount());   

    } 
    

    public void testUnsetNamespaceWhenPrefixed() {
        
        Element element 
          = new Element("prefix:name", "http://www.foo.com/");

        try {
            element.setNamespaceURI("");
            fail("Unset namespace");
        }
        catch (NamespaceException ex) {
            // success   
            assertNotNull(ex.getMessage());
        }
        try {
            element.setNamespaceURI(null);
            fail("Unset namespace");
        }
        catch (NamespaceException ex) {
            // success   
            assertNotNull(ex.getMessage());
        }
 
    }
    
    public void testGetChildElements() {
        
        Elements children = element.getChildElements();
        assertEquals(3, children.size());
        assertEquals(child1, children.get(0));
        assertEquals(child4, children.get(1));
        assertEquals(child5, children.get(2));
        
        children = element.getChildElements("nonesuch");
        assertEquals(0, children.size());
        
        children = element.getChildElements("test");
        assertEquals(1, children.size());
        assertEquals(child1, children.get(0));

        children = element.getChildElements(
          "test", "http://www.example.com");
        assertEquals(2, children.size());
        assertEquals(child4, children.get(0));
        assertEquals(child5, children.get(1));
        
    }

    public void testAddAttribute() {
        
        Element element = new Element("name");
        Attribute a1 = new Attribute("name", "value");
        Attribute a2 = new Attribute("xlink:type", 
          "http://www.w3.org/TR/1999/xlink", "simple");
        
        element.addAttribute(a1);
        element.addAttribute(a2);
        
        assertEquals(2, element.getAttributeCount());
        
        
        Element element2 = new Element("name");
        try {
            element2.addAttribute(a1);
            fail("added attribute with existing parent");
        }
        catch (IllegalAddException ex) {
            // success   
            assertNotNull(ex.getMessage());
        }
        
        a1.detach();
        element2.addAttribute(a1);
        
        a2.detach();
        Element funky 
          = new Element("xlink:funky", "http://www.funky.org");
        try {
            funky.addAttribute(a2); 
            fail("added conflicting namespace"); 
        }
        catch (NamespaceException ex) { 
            // success   
            assertNotNull(ex.getMessage());
        }
        
        a2.detach();
        Element notasfunky = new Element(
          "prefix:funky", "http://www.w3.org/TR/1999/xlink");
        notasfunky.addAttribute(a2);

 
        Attribute a3 = new Attribute(
          "xlink:type", "http://www.w3.org/TR/1999/xlink", "simple");
        Attribute a4 = new Attribute(
          "xlink:href", "http://www.w3.org/1998/xlink", "simple");
        Element test = new Element("test");
        try {
            test.addAttribute(a3); 
            test.addAttribute(a4); 
            fail("added conflicting attributes"); 
        }
        catch (NamespaceException ex) { 
            // success   
            assertNotNull(ex.getMessage());
        }

        Attribute a5 = new Attribute(
          "xlink:type", "http://www.w3.org/TR/1999/xlink", "simple");
        Attribute a6 = new Attribute(
          "xlink:type", "http://www.w3.org/1998/xlink", "simple");
        Element test2 = new Element("test");
        try {
           test2.addAttribute(a5); 
           test2.addAttribute(a6); 
           fail("added conflicting attributes"); 
        }
        catch (NamespaceException ex) { 
            // success   
            assertNotNull(ex.getMessage());
        }
        
    }


    public void testAddAttributesWithAdditionalNamespaces() {
        
        Element element = new Element("name");
        element.addNamespaceDeclaration(
          "xlink", "http://www.w3.org/TR/1999/xlink");
        element.addNamespaceDeclaration(
          "pre", "http://www.example.com");
        
        
        Attribute a1 = new Attribute("name", "value");
        Attribute a2 = new Attribute("xlink:type", 
          "http://www.w3.org/TR/1999/xlink", "simple");
        
        element.addAttribute(a1);
        element.addAttribute(a2);
        
        assertEquals(2, element.getAttributeCount());
        
        try {
            element.addAttribute(
              new Attribute("pre:att", "ftp://example.com/", "value")
            );
            fail("added attribute that conflicts with " +                "additional namespace declaration");
        }
        catch (NamespaceException ex) {
            // success   
            assertNotNull(ex.getMessage());
        }

        element.addAttribute(
          new Attribute("ok:att", "ftp://example.com/", "value")
        );
        assertEquals(3, element.getAttributeCount());

        try {
            element.addNamespaceDeclaration(
              "ok", "http://www.example.net");
            fail("added namespace declaration that " +                "conflicts with attribute");
        }
        catch (NamespaceException ex) {
            // success   
            assertNotNull(ex.getMessage());
        }
        
        assertEquals(
          "ftp://example.com/", 
          element.getNamespaceURI("ok"));
        assertEquals(
          "http://www.w3.org/TR/1999/xlink", 
          element.getNamespaceURI("xlink"));
        assertEquals(
          "http://www.example.com", 
          element.getNamespaceURI("pre"));
        
    }    

    public void testCopy() {

        String name = "red:sakjdhjhd";
        String uri = "http://www.red.com/";
        String baseURI = "http://www.example.com/";
        Element e = new Element(name, uri);

        e.addNamespaceDeclaration("blue", "http://www.blue.com");
        e.addNamespaceDeclaration("green", "http://www.green.com");
        Attribute a1 = new Attribute("test", "test");
        Attribute a2 = new Attribute("pre1:green", 
          "http://www.green.com/", "data");
        Attribute a3 = new Attribute("yellow:sfsdadf", 
          "http://www.yellow.com/", "data");
        e.addAttribute(a1);
        e.addAttribute(a2);
        e.addAttribute(a3);
        
        
        Element e2 = new Element("mv:child", "http://www.mauve.com");
        e.appendChild(e2);
        
        Element e3 = new Element("mv:child", "http://www.mauve.com");
        e.insertChild(e3, 0);
        Element e4 = new Element("mv:child", "http://www.mauve.com");
        e3.insertChild(e4, 0);
        e.setBaseURI(baseURI);
        
        Element copy = (Element) e.copy();
        
        assertEquals(
          e.getNamespaceURI("red"), 
          copy.getNamespaceURI("red"));
        assertEquals(
          e.getNamespaceURI("blue"), 
          copy.getNamespaceURI("blue"));
        assertEquals(e.getValue(), copy.getValue());
        assertEquals(e.getAttribute("test").getValue(), 
          copy.getAttribute("test").getValue());
        assertEquals(e.getBaseURI(), copy.getBaseURI());

    }

    public void testRemoveChildren() {
        String name = "red:sakjdhjhd";
        String uri = "http://www.red.com/";
        Element e = new Element(name, uri);

        Attribute a1 = new Attribute("test", "test");       
        e.addAttribute(a1);
        
        Element e2 = new Element("mv:child", "http://www.mauve.com");
        e.insertChild(e2, 0);
        Element e3 = new Element("mv:child", "http://www.mauve.com");
        e.insertChild(e3, 0);
        Element e4 = new Element("mv:child", "http://www.mauve.com");
        e3.insertChild(e4, 0);
  
  
        assertEquals(e3, e4.getParent());
        assertEquals(e, e2.getParent());
        assertEquals(e, e3.getParent());
       
        e.removeChildren();
 
        assertEquals(0, e.getChildCount());
        assertNull(e2.getParent());
        assertNull(e3.getParent());
        assertEquals(e3, e4.getParent());
        assertEquals(e, a1.getParent());
        
    }

    public void testGetAttributeValue() {
        String name = "sakjdhjhd";
        Element e = new Element(name);

        assertNull(e.getAttributeValue("test"));
        assertNull(e.getAttributeValue("base", 
          "http://www.w3.org/XML/1998/namespace"));
        
        e.addAttribute(new Attribute("test", "value"));
        e.addAttribute(new Attribute("xml:base", 
          "http://www.w3.org/XML/1998/namespace", 
          "http://www.example.com/"));
        
        assertEquals("value", e.getAttributeValue("test"));
        assertEquals(
          "http://www.example.com/", 
          e.getAttributeValue("base", 
            "http://www.w3.org/XML/1998/namespace"));
        assertNull(e.getAttributeValue("xml:base"));
        assertNull(e.getAttributeValue("base"));
        assertNull(e.getAttributeValue("test", 
          "http://www.w3.org/XML/1998/namespace"));
    }

    public void testGetAttribute() {
        String name = "sakjdhjhd";
        Element e = new Element(name);

        assertNull(e.getAttribute("test"));
        assertNull(e.getAttribute("base", 
          "http://www.w3.org/XML/1998/namespace"));
        
        try {
            e.getAttribute(0);
        }
        catch (IndexOutOfBoundsException ex) {
            // success
        }
        
        Attribute a1 = new Attribute("test", "value");
        Attribute a2 = new Attribute("xml:base", 
          "http://www.w3.org/XML/1998/namespace", 
          "http://www.example.com/");
        
        e.addAttribute(a1);
        e.addAttribute(a2);
        
        assertEquals(a1, e.getAttribute("test"));
        assertEquals(a2, e.getAttribute("base", 
          "http://www.w3.org/XML/1998/namespace"));
          
        try {
            e.getAttribute(2);
        }
        catch (IndexOutOfBoundsException ex) {
            // success   
            assertNotNull(ex.getMessage()); 
        }
  
        try {
            e.getAttribute(-1);
        }
        catch (IndexOutOfBoundsException ex) {
            // success   
            assertNotNull(ex.getMessage()); 
        }
           
    }

    public void testGetNamespacePrefix() {      
        Element html = new Element("html");
        assertEquals("", html.getNamespacePrefix());         
    }

    public void testXMLPrefixAllowed() {
        Element test = new Element("xml:base",
              "http://www.w3.org/XML/1998/namespace");
        assertEquals("xml", test.getNamespacePrefix());
        assertEquals("http://www.w3.org/XML/1998/namespace", test.getNamespaceURI());
        assertEquals("xml:base", test.getQualifiedName());
    }

    public void testXMLPrefixNotAllowedWithWrongURI() {
        try {
            new Element("xml:base", "http://www.example.org/");
            fail("Allowed wrong namespace for xml prefix");    
        }
        catch (NamespaceException success) {
            assertNotNull(success.getMessage());   
        }
        
    }

    public void testWrongPrefixNotAllowedWithXMLURI() {
        try {
            new Element("test:base", "http://www.w3.org/XML/1998/namespace");
            fail("Allowed XML namespace to be associated with non-xml prefix");    
        }
        catch (NamespaceException success) {
            assertNotNull(success.getMessage());   
        }
        
    }


}
