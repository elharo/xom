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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Comment;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.MalformedURIException;
import nu.xom.ParsingException;
import nu.xom.Text;

/**
 * <p>
 *  Tests the getting and setting of base URI information
 *  on nodes. It's important to note that despite the name
 *  this is really a URI, not an IRI. The <code>xml:base</code>
 *  attribute may contain an unescaped URI; i.e. an IRI. However,
 *  the base URI is determined after this is converted to a 
 *  real URI with all percent escapes in place. See the <a 
 *  href="http://www.w3.org/TR/2001/REC-xmlbase-20010627/">XML
 *  Base specification</a> for elucidation of this point.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d23
 *
 */
public class BaseURITest extends XOMTestCase {

    public BaseURITest(String name) {
        super(name);
    }

    private Document doc;
    private String base1 = "http://www.base1.com/";
    private String base2 = "http://www.base2.com/";
    private String base3 = "base3.html";

    protected void setUp() {
        Element root = new Element("root");
        doc = new Document(root);
        doc.setBaseURI(base1);
        Element child = new Element("child");
        root.appendChild(child);
        child.setBaseURI(base2);
        child.appendChild(new Comment("here I am"));
        
        Element child2 = new Element("child2");
        root.appendChild(child2);
 
        Element child3 = new Element("child3");
        root.appendChild(child3);
        child3.addAttribute(new Attribute("xml:base", 
          "http://www.w3.org/XML/1998/namespace", base2));
 
        Element child4 = new Element("child4");
        root.appendChild(child4);
        child4.addAttribute(new Attribute("xml:base", 
          "http://www.w3.org/XML/1998/namespace", base3));
 
    }

    public void testDocBase() {
        assertEquals(base1, doc.getBaseURI());
    }

    public void testUnsetBase() {
        Element root = new Element("test");
        root.setBaseURI(base1);
        root.setBaseURI(null);
        assertNull(root.getBaseURI());
    }

    public void testInheritBaseFromDocument() {
        Element root = doc.getRootElement();
        root.setBaseURI("");
        assertEquals("", root.getBaseURI());
    }

    public void testAllowEmptyBase() {
        Element root = new Element("test");
        root.setBaseURI(base1);
        root.setBaseURI("");
        assertEquals("", root.getBaseURI());
    }

    public void testIPv6Base() {
        String ipv6 
          = "http://[FEDC:BA98:7654:3210:FEDC:BA98:7654:3210]:80/test.xml";
        Element root = new Element("test");
        root.setBaseURI(ipv6);
        assertEquals(ipv6, root.getBaseURI());
    }
    
    public void testBaseWithNonASCIICharacter() {
        String uri = "http://www.w3.org/\u00A9testing";
        Element root = new Element("test"); 
        try {
            root.setBaseURI(uri);
            fail("Allowed base URI containing non-ASCII character");
        }
        catch (MalformedURIException success) {
            assertNotNull(success.getMessage());
        }
        
        root.setBaseURI("http://www.example.org/D%C3%BCrst");
        assertEquals("http://www.example.org/D%C3%BCrst", root.getBaseURI());
    }

    public void testUppercaseBase() {
        String base = "HTTP://WWW.EXAMPLE.COM/TEST.XML";
        Element root = new Element("test");
        root.setBaseURI(base);
        assertEquals(base, root.getBaseURI());
    }

    public void testBaseWithUnusualParts() {
        String base = "HTTP://user@host:WWW.EXAMPLE.COM:65130/TEST-2+final.XML?name=value&name2=value2";
        Element root = new Element("test");
        root.setBaseURI(base);
        assertEquals(base, root.getBaseURI());
    }

    public void testBaseWithEscapedParts() {
        String base = "http://www.example.com/test%20test";
        Element root = new Element("test");
        root.setBaseURI(base);
        assertEquals(base, root.getBaseURI());
    }

    public void testBaseWithUnreservedCharacters() {
        String base = "http://www.example.com/()-_.!~*'";
        Element root = new Element("test");
        root.setBaseURI(base);
        assertEquals(base, root.getBaseURI());
    }

    public void testXMLBaseWithNonASCIICharacters() 
      throws UnsupportedEncodingException, URISyntaxException {
        String omega = "\u03A9";
        // In UTF-8 %ce%a9
        String base = "http://www.example.com/" + omega;
        Element root = new Element("test");
        root.addAttribute(new Attribute("xml:base", "http://www.w3.org/XML/1998/namespace", base));
        // This is a Java 1.4 dependence
        URI uri = new URI(root.getBaseURI());
        assertEquals("/" + omega, uri.getPath());
    }
    
    public void testBaseWithNonASCIICharacters() 
      throws UnsupportedEncodingException, URISyntaxException {
        String base = "http://www.example.com/%ce%a9";
        Element root = new Element("test");
        root.setBaseURI(base);
        assertEquals(base, root.getBaseURI());
    }
    
    public void testBadIPv6Base() {
        Element root = new Element("test");
        try {
            root.setBaseURI(
              "http://[FEDC:BA98:7654:3210:FEDC:BA98:7654:3210]/test.xml#xpointer(/*[1])"
            );
            fail("allowed multiple brackets");
        }
        catch (MalformedURIException ex) {
            assertNotNull(ex.getMessage());   
        }

        try {
            root.setBaseURI(
              "http://[FEDC:BA98:7654:3210:FEDC:BA98:7654:3210/"
            );
            fail("allowed mismatched brackets");
        }
        catch (MalformedURIException ex) {
            // success
            assertNotNull(ex.getMessage());
        }

        try {
            root.setBaseURI(
              "http://]FEDC:BA98:7654:3210:FEDC:BA98:7654:3210[/"
            );
            fail("allowed right bracket before left bracket");
        }
        catch (MalformedURIException ex) {
            // success
            assertNotNull(ex.getMessage());
        }

    }

    public void testAllowEmptyXMLBase() {
        Element root = doc.getRootElement();
        root.setBaseURI(base1);
        root.addAttribute(new Attribute("xml:base", 
          "http://www.w3.org/XML/1998/namespace", ""));
        assertEquals(base1, root.getBaseURI());
    }

    public void testFailures() {
        Element root = doc.getRootElement();
        
        try {
            root.setBaseURI("http://www.w3.org/ testing");
            fail("Allowed URI containing space");
        }
        catch (MalformedURIException ex) {
            // success
            assertNotNull(ex.getMessage());
        }

        try {
            root.setBaseURI("http://www.w3.org/tes%ting");
            fail("Allowed URI containing %");
        }
        catch (MalformedURIException ex) {
            // success
            assertNotNull(ex.getMessage());
        }

        try {
            root.setBaseURI("http://www.w3.org/%Atesting");
            fail("Allowed URI containing half percent");
        }
        catch (MalformedURIException ex) {
            // success
            assertNotNull(ex.getMessage());
        }

        try {
            root.setBaseURI("http://www.w3.org/%A");
            fail("Allowed URI containing half percent at end of path");
        }
        catch (MalformedURIException ex) {
            // success
            assertNotNull(ex.getMessage());
        }

        try {
            root.setBaseURI("http://www.w3.org/^testing");
            fail("Allowed URI containing unwise character");
        }
        catch (MalformedURIException ex) {
            // success
            assertNotNull(ex.getMessage());
        }

        try {
            root.setBaseURI("http://www.w3.org/<testing");
            fail("Allowed URI containing unwise < character");
        }
        catch (MalformedURIException ex) {
            // success
            assertNotNull(ex.getMessage());
        }

        try {
            root.setBaseURI("http://www.w3.org/\u0000testing");
            fail("Allowed URI containing unwise null C0 control character");
        }
        catch (MalformedURIException ex) {
            // success
            assertNotNull(ex.getMessage());
        }

        try {
            root.setBaseURI("http://www.w3.org/\u0007testing");
            fail("Allowed URI containing unwise BEL C0 control character");
        }
        catch (MalformedURIException ex) {
            // success
            assertNotNull(ex.getMessage());
        }

    }

    // Note that the xml:base attribute can contain an IRI,
    // not a URI, so this is a little different than the failures
    // on setBaseURI
    public void testXMLBaseFailures() {
        Attribute base = new Attribute("xml:base", 
          "http://www.w3.org/XML/1998/namespace", "base.html");

        try {
            base.setValue("http://www.w3.org/tes%ting");
            fail("Allowed URI containing unescaped %");
        }
        catch (MalformedURIException ex) {
            // success
            assertNotNull(ex.getMessage());
        }

        try {
            base.setValue("http://www.w3.org/%Atesting");
            fail("Allowed IRI containing half percent escape");
        }
        catch (MalformedURIException ex) {
            // success
            assertNotNull(ex.getMessage());
        }

        try {
            base.setValue("http://www.w3.org/%A");
            fail("Allowed IRI containing half percent at end of path");
        }
        catch (MalformedURIException ex) {
            // success
            assertNotNull(ex.getMessage());
        }

        try {
            base.setValue("http://www.w3.org/\u0000testing");
            fail("Allowed URI containing unwise null C0 control character");
        }
        catch (MalformedURIException ex) {
            // success
            assertNotNull(ex.getMessage());
        }

        try {
            base.setValue("http://www.w3.org/\u0007testing");
            fail("Allowed URI containing unwise BEL C0 control character");
        }
        catch (MalformedURIException ex) {
            // success
            assertNotNull(ex.getMessage());
        }

    }
    
    // Note that the xml:base attribute can contain an IRI,
    // not a URI. Here we test for legal IRIs but illegal URIs
    public void testValuesLegalInXMLBaseButNotInAURI() {
        Element element = new Element("test");
        Attribute base = new Attribute("xml:base", 
          "http://www.w3.org/XML/1998/namespace", "base.html");
        element.addAttribute(base);
        
        base.setValue("http://www.w3.org/ testing");
        assertEquals("http://www.w3.org/%20testing", element.getBaseURI());

        base.setValue("http://www.w3.org/^testing");
        assertEquals("http://www.w3.org/%5etesting", element.getBaseURI());

        base.setValue("http://www.w3.org/<testing");
        assertEquals("http://www.w3.org/%3ctesting", element.getBaseURI());

    }
    
    
    public void testXMLBaseValuesCanContainPercentEscapes() {
        Attribute base = new Attribute("xml:base", 
          "http://www.w3.org/XML/1998/namespace", "base.html");
        Element e = new Element("test");
        e.addAttribute(base);
        base.setValue("http://www.w3.org/%20testing");
        String baseURI = e.getBaseURI();
        assertEquals("http://www.w3.org/%20testing", baseURI);
    }
    
    
    public void testInheritBaseFromDoc() {
        assertEquals(base1, doc.getRootElement().getBaseURI());
    }

    public void testLoadElementFromDifferentEntity() {
        assertEquals(base2, 
          doc.getRootElement().getChild(0).getBaseURI());
     }

    public void testLeafNode() {
        assertEquals(
          doc.getRootElement().getChild(0).getBaseURI(), 
          doc.getRootElement().getChild(0).getChild(0).getBaseURI()
        );
     }

    public void testLoadElementFromSameEntity() {
        assertEquals(
          base1, 
          doc.getRootElement().getFirstChildElement("child2").getBaseURI()
        );
     }

    public void testXMLBaseAbsolute() {
        assertEquals(
          base2, 
          doc.getRootElement().getFirstChildElement("child3").getBaseURI()
        );
     }

    public void testXMLBaseRelative() {
        assertEquals(
          "http://www.base1.com/base3.html", 
          doc.getRootElement().getFirstChildElement("child4").getBaseURI()
        );
     }

    public void testXMLBaseRelativeWithNoRoot() {
        Element element = new Element("test");
        element.addAttribute(new Attribute("xml:base", 
          "http://www.w3.org/XML/1998/namespace", "base.html"));
        assertEquals("base.html", element.getBaseURI());
        
        element = new Element("test");
        element.setBaseURI("base.html");
        assertEquals("base.html", element.getBaseURI());
        
    }

    public void testRelativeURIResolutionAgainstARedirectedBase()
      throws IOException, ParsingException {
        Builder builder = new Builder();
        Document doc = builder.build(
          "http://www.ibiblio.org/xml/redirecttest.xml");
        assertEquals(
          "http://www.ibiblio.org/xml/redirected/target.xml", 
          doc.getBaseURI()
        );
    } 
   
    public void testParentlessNodesHaveNullBaseURIs() {
        Text t = new Text("data");   
        assertNull(t.getBaseURI());
        
        Element e = new Element("a");
        assertNull(e.getBaseURI());
    }
   

}