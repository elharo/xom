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

import nu.xom.Element;
import nu.xom.Attribute;
import nu.xom.MalformedURIException;
import nu.xom.NamespaceConflictException;

/**
 * <p>
 *   Tests that namespace well-formedness is maintained.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0a1
 *
 */
public class NamespacesTest extends XOMTestCase {

    public NamespacesTest() {
        super("Namespaces tests");
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
         
        severalNamespaces 
          = new Element("test", "http://www.example.com/");
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
        catch (NamespaceConflictException success) { 
            assertNotNull(success.getMessage());
        }
        
    }
    
    
    public void testSetNamespaceURIInConflictWithAdditionalNamespaceDeclaration() {
        
        someNamespaces.setNamespaceURI("http://www.w3.org/2001/xlink");
        someNamespaces.setNamespacePrefix("xlink");
        try {
            someNamespaces.setNamespaceURI("http://www.example.net");
            fail("changed namespace URI to conflict with additional namespace declaration");
        }
        catch (NamespaceConflictException ex) {
            assertNotNull(ex.getMessage());
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
    
    
    public void testWrongPrefixNotAllowedWithXMLURI() {
        
        try {
            noNamespaces.addNamespaceDeclaration("pre", "http://www.w3.org/XML/1998/namespace");
            fail("Allowed XML namespace to be associated with non-xml prefix");    
        }
        catch (NamespaceConflictException success) {
            assertNotNull(success.getMessage());   
        }
        
    }
    
    
    public void testUnmappingPrefix() {
        
        try {
            noNamespaces.addNamespaceDeclaration("pre", "");
        }
        catch (MalformedURIException success) {
            assertNotNull(success.getMessage());   
        }
        
    }
    
    
    public void testAllowCapitalSchemes() {       
        noNamespaces.addNamespaceDeclaration("pre", "HTTP://WWW.EXAMPLE.COM/");
        assertEquals(noNamespaces.getNamespaceURI("pre"), "HTTP://WWW.EXAMPLE.COM/"); 
    }

    
    public void testBadSchemes() {   
        
        try {
            noNamespaces.addNamespaceDeclaration("pre", "uri!urn:somedata");
            fail("Allowed illegal characters in scheme");
        }
        catch (MalformedURIException success) {
            assertNotNull(success.getMessage());   
        }
    }


    public void testXMLPrefixNotAllowedWithWrongURI() {
        
        try {
            noNamespaces.addNamespaceDeclaration("xml", "http://www.example.org/");
            fail("Allowed xml prefix to be associated with wrong URI");    
        }
        catch (NamespaceConflictException success) {
            assertNotNull(success.getMessage());   
        }
        
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
        catch (NamespaceConflictException success) {
            assertNotNull(success.getMessage());
        }
        
        try {
            noNamespaces.addNamespaceDeclaration("xmlns",
              "http://www.example.com");
            fail("added xmlns prefix");  
        }  
        catch (NamespaceConflictException success) { 
            assertNotNull(success.getMessage());
        }
        
    }
    
    
    public void testCantUseXMLPrefix() {
        
        try {
            noNamespaces.addNamespaceDeclaration(
              "xml", "http://www.example.com");
            fail("added xmlns prefix");  
        }  
        catch (NamespaceConflictException success) { 
            assertNotNull(success.getMessage());
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
        test.addAttribute(new Attribute("xlink:type", 
          "http://www.w3.org/2001/xlink", "value"));
        assertEquals(2, test.getNamespaceDeclarationCount());
        test.addAttribute(new Attribute("xlink:href", 
          "http://www.w3.org/2001/xlink", "value"));
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
        catch (NamespaceConflictException success) {
            assertNotNull(success.getMessage());
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
        catch (NamespaceConflictException success) { 
            assertNotNull(success.getMessage());
        }
       
        e.setNamespaceURI("http://www.example.com");
        e.setNamespacePrefix("pre");
        e.addNamespaceDeclaration("", "http://www.example.net");
    
        assertEquals("http://www.example.net", e.getNamespaceURI(""));
       
    } 

    
    public void testAddNullPrefix() {
        
        Element e = new Element("test");
       
        try {
            e.addNamespaceDeclaration(null, 
              "http://www.example.com");
            fail("added conflicting empty prefix to element in no namespace");   
        }
        catch (NamespaceConflictException success) {
            assertNotNull(success.getMessage());
        }
       
        e.setNamespaceURI("http://www.example.com");
        e.setNamespacePrefix("pre");
        e.addNamespaceDeclaration(null, "http://www.example.net");

        assertEquals("http://www.example.net", e.getNamespaceURI(""));
       
    } 

    
    public void testAddNullURI() {
        Element parent = new Element("parent", "http://www.example.org/");
        Element e = new Element("pre:test", "http://www.example.com/"); 
        parent.appendChild(e);
        e.addNamespaceDeclaration("", null);
        assertEquals("", e.getNamespaceURI(""));   
    } 

    
    public void testRemoveNullPrefix() {
        Element e = new Element("pre:test", "http://www.example.com/"); 
        e.addNamespaceDeclaration("", "http://www.example.net");
        e.removeNamespaceDeclaration(null);
        assertEquals("", e.getNamespaceURI(""));   
    } 

    
    public void testUndeclareDefaultNamespace() {
        Element parent = new Element("parent", "http://www.example.org/");
        Element e2 = new Element("pre:test", "http://www.example.net");
        parent.appendChild(e2);
        e2.addNamespaceDeclaration("", "");
        assertEquals("", e2.getNamespaceURI(""));      
    } 

    
    public void testForConflictWithDefaultNamespace() {
        Element e = new Element("test", "http://www.example.net");
        try {
            e.addNamespaceDeclaration("", "http://www.example.com");
            fail("Conflicting default namespace");
        }
        catch (NamespaceConflictException success) {
            assertNotNull(success.getMessage());   
        }
    } 

    
    public void testConflictingUndeclarationOfDefaultNamespace() {
        Element e = new Element("test", "http://www.example.net");
        try {
            e.addNamespaceDeclaration("", "");
            fail("Conflicting undeclaration of default namespace");
        }
        catch (NamespaceConflictException success) {
            assertNotNull(success.getMessage());   
        }
    } 

    
    public void testAdding() {
    
        try {
            noNamespaces.addNamespaceDeclaration("", 
              "http://www.example.com/");
            fail("added conflicting default namespace");
        }
        catch (NamespaceConflictException success) {
            assertNotNull(success.getMessage());
        }

        try {
            severalNamespaces.addNamespaceDeclaration(
              "xlink", "http://www.example.com/");
            fail("added conflicting attribute prefix namespace");
        }
        catch (NamespaceConflictException success) {
            assertNotNull(success.getMessage());
        }

        try {
            someNamespaces.addNamespaceDeclaration("xsl", 
              "http://www.example.com/");
            fail("added conflicting additional prefix namespace");
        }
        catch (NamespaceConflictException success) {
            assertNotNull(success.getMessage());
        }

        someNamespaces.addNamespaceDeclaration("foo", 
          "http://www.example.com/");
        assertEquals("http://www.example.com/",
          someNamespaces.getNamespaceURI("foo"));

    }

    
    public void testReplacingNamespaceDeclaration() {
    
        assertEquals("http://www.w3.org/2001/xlink", 
          someNamespaces.getNamespaceURI("xlink"));
        try {
            someNamespaces.addNamespaceDeclaration("xlink", 
              "http://www.example.com/");
            fail("Redeclared without removal");   
        }
        catch (NamespaceConflictException success) {
            assertNotNull(success.getMessage());
        }
        someNamespaces.removeNamespaceDeclaration("xlink");
        assertNull(someNamespaces.getNamespaceURI("xlink"));
        someNamespaces.addNamespaceDeclaration("xlink", 
          "http://www.example.com/");
        assertEquals("http://www.example.com/", 
          someNamespaces.getNamespaceURI("xlink"));
        

    }

    
    public void testSetPrefix() {
    
        try {
            Attribute a = severalNamespaces.getAttribute(0);
            a.setNamespace("xsl", "http://www.example.com/");
            fail("added conflicting attribute prefix");
        }
        catch (NamespaceConflictException success) { 
            assertNotNull(success.getMessage());
        }

    }


    public void testElementConflict() {
    
        Element element = new Element("pre:test", 
          "http://www.example.com/");
        element.addNamespaceDeclaration("pre", 
          "http://www.example.com/");
        try {
           element.setNamespaceURI("http://www.yahoo.com");
           fail("changed to conflicting element namespace");
        }
        catch (NamespaceConflictException success) {
            assertNotNull(success.getMessage());
        }

    }

    
    public void testAttributeConflict() {
    
        Element element = new Element("test");
        element.addNamespaceDeclaration("pre", 
          "http://www.example.com/");
        Attribute a = new Attribute("pre:test", 
          "http://www.example.com/", "value");
        element.addAttribute(a);
        try {
           a.setNamespace("pre", "http://www.yahoo.com/");
           fail("changed to conflicting attribute namespace");
        }
        catch (NamespaceConflictException success) { 
            assertNotNull(success.getMessage());
        }

    }


    public void testSetAttributePrefix() {
    
        try {
           severalNamespaces.setNamespacePrefix("xlink");
           fail("added conflicting element prefix");
        }
        catch (NamespaceConflictException success) { 
            assertNotNull(success.getMessage());
        }

        try {
           severalNamespaces.setNamespacePrefix("xsl");
           fail("added conflicting element prefix");
        }
        catch (NamespaceConflictException success) {  
            assertNotNull(success.getMessage());
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

    
    public void testCountNamespaces() {
        Element html = new Element("html");
        assertEquals(1, html.getNamespaceDeclarationCount());
        html.setNamespaceURI("http://www.w3.org/1999/xhtml");
        assertEquals(1, html.getNamespaceDeclarationCount());
        html.addAttribute(new Attribute("pre:test", 
          "http://www.examnple.org/", "value"));
        assertEquals(2, html.getNamespaceDeclarationCount());
        html.addNamespaceDeclaration("rddl", "http://www.rddl.org");
        assertEquals(3, html.getNamespaceDeclarationCount());
        html.addNamespaceDeclaration("xlink", 
          "http://www.w3.org/2001/xlink");
        assertEquals(4, html.getNamespaceDeclarationCount());
        html.addAttribute(new Attribute("xml:space", 
          "http://www.w3.org/XML/1998/namespace", "default"));
        assertEquals(4, html.getNamespaceDeclarationCount());        
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
    
    
    public void testNumbersAllowedInSchemes() {
        String namespace = "u0123456789:schemespecificdata";
        Element e = new Element("test", namespace);
        assertEquals(namespace, e.getNamespaceURI());
    }

    
    public void testPunctuationMarksAllowedInSchemes() {
        String namespace = "u+-.:schemespecificdata";
        Element e = new Element("test", namespace);
        assertEquals(namespace, e.getNamespaceURI());
    }

    
    public void testPunctuationMarksCantStartSchemes() {
        String namespace = "+:schemespecificdata";
        try {
            new Element("test", namespace);
            fail("Allowed scheme name to start with +");
        }
        catch (MalformedURIException success) {
            assertNotNull(success.getMessage());
        }
    }

    
    public void testNumbersCantStartSchemes() {
        String namespace = "8uri:schemespecificdata";
        try {
            new Element("test", namespace);
            fail("Allowed scheme name to start with digit");
        }
        catch (MalformedURIException success) {
            assertNotNull(success.getMessage());
        }
    }

    
    public void testURIReferenceCantHaveTwoFragmentIDs() {
        String namespace = "uri:schemespecificdata#test#id";
        try {
            new Element("test", namespace);
            fail("Allowed URI reference to contain multiple fragment IDs");
        }
        catch (MalformedURIException success) {
            assertNotNull(success.getMessage());
        }
    }

    
    public void testHalfPercentEscape() {
        String namespace = "http://www.example.org/%ce%a";
        try {
            new Element("test", namespace);
            fail("Allowed path to contain only half a percent escape");
        }
        catch (MalformedURIException success) {
            assertNotNull(success.getMessage());
        }
        
    }

    
    public void testUnescapedPercentSign() {
        
        String namespace = "http://www.example.org/%ce%";
        try {
            new Element("test", namespace);
            fail("Allowed path to contain only percent");
        }
        catch (MalformedURIException success) {
            assertNotNull(success.getMessage());
        }
        
    }

    
    public void testPercentSignFollowedByNonHexDigits() {
        
        for (char c = 'G'; c <= '`'; c++) {
            String namespace = "http://www.example.org/%1" + c + "test/";
            try {
                new Element("test", namespace);
                fail("Allowed malformed namespace URI " + namespace);
            }
            catch (MalformedURIException success) {
                assertNotNull(success.getMessage());
            }
        }
        
        for (char c = ':'; c <= '@'; c++) {
            String namespace = "http://www.example.org/%1" + c + "test/";
            try {
                new Element("test", namespace);
                fail("Allowed malformed namespace URI " + namespace);
            }
            catch (MalformedURIException success) {
                assertNotNull(success.getMessage());
            }
        }
        
    }

    
    public void testVariousSchemes() {    
        
        String[] urls = {
          "ftp://example.com", "FTP://example.com/pub/",
          "MAILTO:elharo@metalab.unc.edu?Subject=XOM%20Namespace",
          "mailto:elharo@metalab.unc.edu?Subject=XOM%20Namespace",
          "telnet:namespaces.ibiblio.org", "TELNET:namespaces.ibiblio.org",
          "gopher://gopher.uminn.edu/", "GOPHER://gopher.uminn.edu/",
          "uri:urn:nwalsh:namespaces", "URI:urn:nwalsh:namespaces",
          "news:comp.lang.xml", "NEWS:comp.lang.xml",
          "wais://wais.example.com:78/database", "WAIS://wais.example.com:78/database",
          "file://vms.host.edu/disk$user/my/notes/note12345.txt", 
          "FILE://vms.host.edu/disk$user/my/notes/note12345.txt",
          "prospero://host.dom//pros/name", "PROSPERO://host.dom:1525//pros/name",
          "z39.50s://melvyl.ucop.edu/cat", "Z39.50S://melvyl.ucop.edu/cat", 
          "z39.50r://melvyl.ucop.edu/mags?elecworld.v30.n19", 
          "Z39.50R://melvyl.ucop.edu/mags?elecworld.v30.n19", 
          "z39.50r://cnidr.org:2100/tmf?bkirch_rules__a1;esn=f;rs=marc",
          "Z39.50R://cnidr.org:2100/tmf?bkirch_rules__a1;esn=f;rs=marc",
          "mid:960830.1639@XIson.com/partA.960830.1639@XIson.com",
          "MID:960830.1639@XIson.com/partA.960830.1639@XIson.com",
          "cid:foo4*foo1@bar.net", "CID:foo4*foo1@bar.net",
          "vemmi://zeus.mctel.fr/demo", "VEMMI://zeus.mctel.fr/demo",
          "vemmi://mctel.fr/demo;$USERDATA=smith;account=1234",
          "opaquelocktoken:f81d4fae-7dec-11d0-a765-00a0c91e6bf6",
          "OPAQUELOCKTOKEN:f81d4fae-7dec-11d0-a765-00a0c91e6bf6",
          "fax:+358.555.1234567", "FAX:+358.555.1234567",
          "modem:+3585551234567;type=v32b?7e1;type=v110",
          "tel:0w003585551234567;phone-context=+3585551234",
          "tel:+1234567890;phone-context=+1234;vnd.company.option=foo",
          "xmlrpc.beeps://stateserver.example.com/NumberToName",
          "XMLRPC.BEEPS://stateserver.example.com/NumberToName",
          "h323:user@h323.example.com", "H323:user@h323.example.com",
          "tn3270://login.example.com"
        };
        for (int i = 0; i < urls.length; i++) {
            Element e = new Element("pre:test", urls[i]);
            assertEquals(e.getNamespaceURI("pre"), urls[i]); 
        }
        
    }
    
    
    public void testPercentEscapes() {

        // Namespace URIs are compared for direct string equality;
        // no de-escaping is performed
        for (char c = ' '; c <= '~'; c++) {
            String url = "http://www.example.com/%" + Integer.toHexString(c) + "test/";
            Element e = new Element("pre:test", url);
            assertEquals(url, e.getNamespaceURI());
            assertEquals(url, e.getNamespaceURI("pre"));
        }
        // repeat in upper case
        for (char c = ' '; c <= '~'; c++) {
            String url = "http://www.example.com/%" + Integer.toHexString(c).toUpperCase() + "test/";
            Element e = new Element("pre:test", url);
            assertEquals(url, e.getNamespaceURI());
            assertEquals(url, e.getNamespaceURI("pre"));
        }

    }
    

    public void testDelims() {

        String[] delims = {"<", ">", "\""};
        for (int i = 0; i < delims.length; i++) {
            String url = "http://www.example.com/" + delims[i] + "/";
            try {
                new Element("test", url);
                fail("Allowed " + url + " as namespace URI");
            }
            catch (MalformedURIException success) {
                assertNotNull(success.getMessage());
            }
        }
        
    }
    
    
    public void testUnwise() {

        char[] unwise = {'{', '}', '|', '\\', '^', '`'};
        for (int i = 0; i < unwise.length; i++) {
            String url = "http://www.example.com/" + unwise[i] + "/";
            try {
                new Element("test", url);
                fail("Allowed " + url + " as namespace URI");
            }
            catch (MalformedURIException success) {
                assertNotNull(success.getMessage());
            }
        }
        
    }
    
    
    public void testSetPrefixOnElementInNoNamespace() {
        
        Element e = new Element("Seq");
        try {
            e.setNamespacePrefix("rdf");
            fail("Set prefix on element in no namespace");
        }
        catch (NamespaceConflictException success) {
            assertNotNull(success.getMessage());
        }
        
    }
     
    
}