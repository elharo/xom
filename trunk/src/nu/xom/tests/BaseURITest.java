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

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

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

    
    public void testASCIILettersWithXMLBaseAttribute() {

        String alphabet = "abcdefghijklmnopqrstuvwxyz";

        String base = "HTTP://WWW.EXAMPLE.COM/" + alphabet;
        Element root = new Element("test");
        root.addAttribute(new Attribute("xml:base", 
          "http://www.w3.org/XML/1998/namespace", base));
        assertEquals(base, root.getBaseURI());
        
        base = "HTTP://WWW.EXAMPLE.COM/" + alphabet.toUpperCase();
        root.addAttribute(new Attribute("xml:base", 
          "http://www.w3.org/XML/1998/namespace", base));
        assertEquals(base, root.getBaseURI()); 
        
    }

    
    public void testXMLBaseWithParameters() {
        String base = "scheme://authority/data/name;v=1.1/test.db";
        Element root = new Element("test");
        root.addAttribute(new Attribute("xml:base", 
          "http://www.w3.org/XML/1998/namespace", base));
        assertEquals(base, root.getBaseURI());
    }
    
    
    public void testXMLBaseWithCommaParameter() {
        String base = "scheme://authority/data/name,1.1/test.db";
        Element root = new Element("test");
        root.addAttribute(new Attribute("xml:base", 
          "http://www.w3.org/XML/1998/namespace", base));
        assertEquals(base, root.getBaseURI());
    }
    
    
    // This one appears to be mostly theoretical
    public void testXMLBaseWithDollarSign() {
        String base = "scheme://authority/data$important";
        Element root = new Element("test");
        root.addAttribute(new Attribute("xml:base", 
          "http://www.w3.org/XML/1998/namespace", base));
        assertEquals(base, root.getBaseURI());
    }
    
    
    public void testFragmentIDWithXMLBaseAttribute() {

        String base = "HTTP://WWW.EXAMPLE.COM/#test";
        Element root = new Element("test");
        root.addAttribute(new Attribute("xml:base", 
          "http://www.w3.org/XML/1998/namespace", base));
        assertEquals(base, root.getBaseURI());
        
    }

    
    public void testQueryString() {

        String base = "http://www.example.com/test?name=value&data=important";
        Element root = new Element("test");
        root.addAttribute(new Attribute("xml:base", 
          "http://www.w3.org/XML/1998/namespace", base));
        assertEquals(base, root.getBaseURI());
        
    }

    // -" | "_" | "." | "!" | "~" | "*" | "'" | "(" | ")"
    public void testUnreserved() {

        String unreserved = "-.!~*'()";
        String base = "http://www.example.com/" + unreserved;
        Element root = new Element("test");
        root.addAttribute(new Attribute("xml:base", 
          "http://www.w3.org/XML/1998/namespace", base));
        assertEquals(base, root.getBaseURI());
        
    }
    
    
    public void testDelims() {

        String[] delims = {"<", ">", "\""};
        for (int i = 0; i < delims.length; i++) {
            String base = "http://www.example.com/" + delims[i] + "/";
            Element root = new Element("test");
            root.addAttribute(new Attribute("xml:base", 
              "http://www.w3.org/XML/1998/namespace", base));
            assertEquals("http://www.example.com/%" 
              + Integer.toHexString(delims[i].charAt(0)).toUpperCase() 
              + "/", root.getBaseURI());
        }
        
    }
    
    
    public void testUnwise() {

        char[] unwise = {'{', '}', '|', '\\', '^', '`'};
        for (int i = 0; i < unwise.length; i++) {
            String base = "http://www.example.com/" + unwise[i] + "/";
            Element root = new Element("test");
            root.addAttribute(new Attribute("xml:base", 
              "http://www.w3.org/XML/1998/namespace", base));
            assertEquals("http://www.example.com/%" 
              + Integer.toHexString(unwise[i]).toUpperCase() 
              + "/", root.getBaseURI());
        }
        
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

    
    public void testXMLBaseWithPlus() {
        String base = "http://www.example.com/test+test";
        Element root = new Element("test");
        root.addAttribute(new Attribute("xml:base", 
          "http://www.w3.org/XML/1998/namespace", base));
        assertEquals(base, root.getBaseURI());
    }

    
    public void testXMLBaseWithUserInfoWithXMLBaseAttribute() {
        String base = "http://invited:test@www.example.com/";
        Element root = new Element("test");
        root.addAttribute(new Attribute("xml:base", 
          "http://www.w3.org/XML/1998/namespace", base));
        assertEquals(base, root.getBaseURI());
    }

    
    public void testXMLBaseWithUnreservedCharacters() {
        String base = "http://www.example.com/()-_.!~*'";
        Element root = new Element("test");
        root.addAttribute(new Attribute("xml:base", 
          "http://www.w3.org/XML/1998/namespace", base));
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
        catch (MalformedURIException success) {
            assertNotNull(success.getMessage());
        }

        try {
            root.setBaseURI("http://www.w3.org/tes%ting");
            fail("Allowed URI containing %");
        }
        catch (MalformedURIException success) {
            assertNotNull(success.getMessage());
        }

        try {
            root.setBaseURI("http://www.w3.org/%Atesting");
            fail("Allowed URI containing half percent");
        }
        catch (MalformedURIException success) {
            assertNotNull(success.getMessage());
        }

        try {
            root.setBaseURI("http://www.w3.org/%A");
            fail("Allowed URI containing half percent at end of path");
        }
        catch (MalformedURIException success) {
            assertNotNull(success.getMessage());
        }


        try {
            root.setBaseURI("http://www.w3.org/^testing");
            fail("Allowed URI containing unwise character");
        }
        catch (MalformedURIException success) {
            assertNotNull(success.getMessage());
        }

        try {
            root.setBaseURI("http://www.w3.org/<testing");
            fail("Allowed URI containing unwise < character");
        }
        catch (MalformedURIException success) {
            assertNotNull(success.getMessage());
        }

        try {
            root.setBaseURI("http://www.w3.org/\u0000testing");
            fail("Allowed URI containing unwise null C0 control character");
        }
        catch (MalformedURIException success) {
            assertNotNull(success.getMessage());
        }

        try {
            root.setBaseURI("http://www.w3.org/\u0007testing");
            fail("Allowed URI containing unwise BEL C0 control character");
        }
        catch (MalformedURIException success) {
            assertNotNull(success.getMessage());
        }


    }

    
    // Note that the xml:base attribute can contain an IRI,
    // not a URI, so this is a little different than the failures
    // on setBaseURI
    public void testXMLBaseFailures() {
        Attribute base = new Attribute("xml:base", 
          "http://www.w3.org/XML/1998/namespace", "base.html");
        Element test = new Element("test");
        test.addAttribute(base);
        
        base.setValue("http://www.w3.org/tes%ting");
        assertNull(test.getBaseURI());

        base.setValue("http://www.w3.org/%Atesting");
        assertNull(test.getBaseURI());

        base.setValue("http://www.w3.org/%A");
        assertNull(test.getBaseURI());
        
        base.setValue("http://www.w3.org/%0testing");
        assertNull(test.getBaseURI());
        
        base.setValue("http://www.w3.org/%7testing");
        assertNull(test.getBaseURI());

    }
    
    
    // Note that the xml:base attribute can contain an IRI,
    // not a URI. It may also contain unescaped characters that
    // need to be escaped. This tests for unescaped values.
    public void testValuesLegalInXMLBaseButNotInAURI() {
        Element element = new Element("test");
        Attribute base = new Attribute("xml:base", 
          "http://www.w3.org/XML/1998/namespace", "base.html");
        element.addAttribute(base);
        
        base.setValue("http://www.w3.org/ testing");
        assertEquals("http://www.w3.org/%20testing", element.getBaseURI());

        base.setValue("http://www.w3.org/^testing");
        assertEquals("http://www.w3.org/%5Etesting", element.getBaseURI());

        base.setValue("http://www.w3.org/<testing");
        assertEquals("http://www.w3.org/%3Ctesting", element.getBaseURI());

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
        Element e = doc.getRootElement().getFirstChildElement("child4");
        String u = e.getBaseURI();
        assertEquals("http://www.base1.com/base3.html", u);
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
   

    // Don't use the parent to resolve the relative base URI
    // when parent and child come from different entities
    public void testElementsFromDifferentActualBases() {
        Element parent = new Element("parent");
        parent.setBaseURI("http://www.cafeconleche.org/");
        Element child = new Element("child");
        child.setBaseURI("http://www.example.com/");
        parent.appendChild(child);
        child.addAttribute(new Attribute("xml:base", 
          "http://www.w3.org/XML/1998/namespace", "/test/data/"));
        String base = child.getBaseURI();
        assertEquals("http://www.example.com/test/data/", base);
    }
    
    
    public void testBadURIInElementsFromDifferentActualBases() {
        Element parent = new Element("parent");
        parent.setBaseURI("http://www.cafeconleche.org/");
        Element child = new Element("child");
        parent.appendChild(child);
        child.addAttribute(new Attribute("xml:base", 
          "http://www.w3.org/XML/1998/namespace",
          "%GF.html"));
        String base = child.getBaseURI();
        assertNull(base);
    }
    
    
    public void testBadURIInElementsFromSameActualBases() {
        Element parent = new Element("parent");
        parent.setBaseURI("http://www.cafeconleche.org/");
        Element child = new Element("child");
        child.setBaseURI("http://www.cafeconleche.org/");
        parent.appendChild(child);
        child.addAttribute(new Attribute("xml:base", 
          "http://www.w3.org/XML/1998/namespace",
          "http://www.example.com/%5.html"));
        assertNull(child.getBaseURI());
    }
    
    
    public void testBadURIInBaseAttributeWithParent() {
        Element parent = new Element("parent");
        parent.setBaseURI("http://www.cafeconleche.org/");
        Element child = new Element("child");
        child.setBaseURI("http://www.cafeconleche.org/");
        parent.appendChild(child);
        child.addAttribute(new Attribute("xml:base", 
          "http://www.w3.org/XML/1998/namespace",
          "%TR.html"));
        assertNull(child.getBaseURI());
    }
    
    
    public void testHierarchicalURIsWithoutProtocolHandlers() {   
        
        String[] urls = {
          "gopher://gopher.uminn.edu/", "GOPHER://gopher.uminn.edu/",
          "gopher://gopher.uminn.edu", "GOPHER://gopher.uminn.edu",
          "wais://wais.example.com:78/database", "WAIS://wais.example.com:78/database",
          "file://vms.host.edu/disk$user/my/notes/note12345.txt", 
          "FILE://vms.host.edu/disk$user/my/notes/note12345.txt",
          "prospero://host.dom//pros/name", "PROSPERO://host.dom:1525//pros/name",
          "z39.50s://melvyl.ucop.edu/cat", "Z39.50S://melvyl.ucop.edu/cat", 
          "z39.50r://melvyl.ucop.edu/mags?elecworld.v30.n19", 
          "Z39.50R://melvyl.ucop.edu/mags?elecworld.v30.n19", 
          "z39.50r://cnidr.org:2100/tmf?bkirch_rules__a1;esn=f;rs=marc",
          "Z39.50R://cnidr.org:2100/tmf?bkirch_rules__a1;esn=f;rs=marc",
          "vemmi://zeus.mctel.fr/demo", "VEMMI://zeus.mctel.fr/demo",
          "vemmi://mctel.fr/demo;$USERDATA=smith;account=1234",
          "xmlrpc.beeps://stateserver.example.com/NumberToName",
          "XMLRPC.BEEPS://stateserver.example.com/NumberToName",
          "tn3270://login.example.com/"
        };
        for (int i = 0; i < urls.length; i++) {
            Element e = new Element("test");
            e.addAttribute(new Attribute("xml:base", 
              "http://www.w3.org/XML/1998/namespace",
              urls[i]));
            Element child = new Element("child");
            child.addAttribute(new Attribute("xml:base", 
              "http://www.w3.org/XML/1998/namespace",
              "TR.html"));
            e.appendChild(child);
            String base = child.getBaseURI();
            assertTrue(urls[i] + " " + base, base.endsWith("/TR.html"));
            assertTrue(base.indexOf("://") >= 4 );
        }

    }
    
    
    public void testOpaqueURIs() {   
        
        String[] urls = {
          "MAILTO:elharo@metalab.unc.edu?Subject=XOM%20Namespace",
          "mailto:elharo@metalab.unc.edu?Subject=XOM%20Namespace",
          "telnet:namespaces.ibiblio.org", "TELNET:namespaces.ibiblio.org",
          "uri:urn:nwalsh:namespaces", "URI:urn:nwalsh:namespaces",
          "news:comp.lang.xml", "NEWS:comp.lang.xml",
          "mid:960830.1639@XIson.com/partA.960830.1639@XIson.com",
          "MID:960830.1639@XIson.com/partA.960830.1639@XIson.com",
          "cid:foo4*foo1@bar.net", "CID:foo4*foo1@bar.net",
          "opaquelocktoken:f81d4fae-7dec-11d0-a765-00a0c91e6bf6",
          "OPAQUELOCKTOKEN:f81d4fae-7dec-11d0-a765-00a0c91e6bf6",
          "fax:+358.555.1234567", "FAX:+358.555.1234567",
          "modem:+3585551234567;type=v32b?7e1;type=v110",
          "tel:0w003585551234567;phone-context=+3585551234",
          "tel:+1234567890;phone-context=+1234;vnd.company.option=foo",
          "h323:user@h323.example.com", "H323:user@h323.example.com",
        };
        for (int i = 0; i < urls.length; i++) {
            Element e = new Element("test");
            e.addAttribute(new Attribute("xml:base", 
              "http://www.w3.org/XML/1998/namespace",
              urls[i]));
            Element child = new Element("child");
            child.addAttribute(new Attribute("xml:base", 
              "http://www.w3.org/XML/1998/namespace",
              "TR.html"));
            e.appendChild(child);
            String base = child.getBaseURI();
            assertEquals("TR.html", base);
        }

    }

    
    public void testXMLBaseUsedToResolveHref() 
      throws ParsingException, IOException {
      
        File input = new File("data/xmlbasetest.xml");
        Builder builder = new Builder();
        Document doc = builder.build(input);
        Element root = doc.getRootElement();
        String base = root.getBaseURI();
        // This constructor only works if we have an absolute URI. 
        // If the test fails an aexception is thrown here. I can't 
        // assert equality with the expected absolute URI because 
        // that varies from one installation to the next
        new URL(base); 
        assertTrue(base.startsWith("file:/"));
                
    } 
    
    
}