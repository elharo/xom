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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Comment;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.NodeFactory;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.Text;

import nu.xom.xinclude.BadHTTPHeaderException;
import nu.xom.xinclude.BadHrefAttributeException;
import nu.xom.xinclude.BadParseAttributeException;
import nu.xom.xinclude.InclusionLoopException;
import nu.xom.xinclude.NoIncludeLocationException;
import nu.xom.xinclude.XIncludeException;
import nu.xom.xinclude.XIncluder;

/**
 * <p>
 *   Unit tests for the XInclude and XPointer engines.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0a4
 *
 */
public class XIncludeTest extends XOMTestCase {

    public XIncludeTest(String name) {
        super(name);
    }

    private Builder builder;
    
    protected void setUp() {        
        builder = new Builder();       
    }
    
    
    private void dumpResult(File original, Document result)
      throws IOException {
        
        String name = original.getName();
        File debug = new File("data/xinclude/debug/");
        File output = new File(debug, name);
        FileOutputStream out = new FileOutputStream(output);
        Serializer serializer = new Serializer(out);
        serializer.write(result);        
        
    }
    
    
    public void testXMLBaseUsedToResolveHref() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(
          "data/xinclude/input/xmlbasetest.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expected = builder.build(
          new File("data/xinclude/output/xmlbasetest.xml")
        );
        assertEquals(expected, result);
                
    }

    
    // Tests that use XPointer to
    // grab a part of the document that contains an include element
    // and make sure that's fully resolved too
    public void testResolveThroughXPointer() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/resolvethruxpointer.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/resolvethruxpointer.xml")
        );
        assertEquals(expectedResult, result);
        
    }    
 
    public void testXMLBaseOnIncludeElementUsedToResolveHref() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(
          "data/xinclude/input/xmlbasetest2.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expected = builder.build(
          new File("data/xinclude/output/xmlbasetest2.xml")
        );
        assertEquals(expected, result);
                
    }
    

    public void testXMLBaseRetainedFromUnincludedElement() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(
          "data/xinclude/input/xmlbasetest3.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expected = builder.build(
          new File("data/xinclude/output/xmlbasetest3.xml")
        );
        assertEquals(expected, result);
                
    }
    
 
    public void testMarsh() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/marshtest.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/marshtest.xml")
        );
        assertEquals(expectedResult, result);
        
    }

    
    public void testIncludeDocumentThatUsesIntradocumentReferences() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/includedocumentwithintradocumentreferences.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/includedocumentwithintradocumentreferences.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testXMLLangAttributes() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/langtest1.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/langtest1.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testInheritedXMLLangAttributes() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/langtest2.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/langtest2.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testNoLanguageSpecified() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/langtest3.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/langtest3.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    // According to RFC 2396 empty string URI always refers to the 
    // current document irrespective of base URI
    public void testXMLBaseNotUsedToResolveMissingHref() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/marshtestwithxmlbase.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/marshtestwithxmlbase.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testEmptyHrefTreatedSameAsMissingHref() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/marshtestwithxmlbaseandemptyhref.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/marshtestwithxmlbase.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testBaselessDocument() 
      throws IOException, ParsingException, XIncludeException {
        
        Element root = new Element("root");
        Element child1 = new Element("xi:include", XIncluder.XINCLUDE_NS);
        child1.addAttribute(new Attribute("xpointer", "p1"));
        Element child2 = new Element("child2");
        root.appendChild(child1);
        root.appendChild(child2);
        child2.addAttribute(new Attribute("id", "p1", Attribute.Type.ID));
        Document in = new Document(root);
        Document out = XIncluder.resolve(in);
        String result = out.toXML();
        assertEquals("<?xml version=\"1.0\"?>\r\n" +
           "<root><child2 id=\"p1\" /><child2 id=\"p1\" /></root>\r\n", result);
    }
    

    public void testIncludeTextWithCustomNodeFactory() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/c2.xml");
        Builder builder = new Builder(new TextNodeFactory());
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc, builder);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/c2.xml")
        );
        assertEquals(expectedResult, result);
        Element root = result.getRootElement();
        for (int i = 0; i < root.getChildCount(); i++) {
            Node node = root.getChild(i);
            if (node instanceof Text) {
                assertTrue(node instanceof TextSubclass);
            }
        }
        
    }
    
    
    public void testParseEqualsTextWithNodeFactoryThatRemovesAllTextNodes()  
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/c2.xml");
        Builder builder = new Builder();
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc, new Builder(new TextFilter()));
        Document expectedResult = builder.build(
          new File("data/xinclude/output/c2a.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    private static class TextFilter extends NodeFactory {
        
        public Nodes makeText(String data) {
            return new Nodes();
        }
        
    }

    
    public void testParseEqualsTextWithNodeFactoryThatReplacesTextNodesWithComments()  
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/c2.xml");
        Builder builder = new Builder();
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc, new Builder(new TextToComment()));
        Document expectedResult = builder.build(
          new File("data/xinclude/output/c2b.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    private static class TextToComment extends NodeFactory {
        
        public Nodes makeText(String data) {
            return new Nodes(new Comment(data));
        }
        
    }

    
    public void testParseEqualsTextWithNodeFactoryThatReplacesTextNodesWithAttributes()  
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/c2.xml");
        Builder builder = new Builder();
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc, new Builder(new TextToAttribute()));
        Document expectedResult = builder.build(
          new File("data/xinclude/output/c2c.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testParseEqualsTextWithNodeFactoryThatReplacesTextNodesWithTwoElements()  
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/c2.xml");
        Builder builder = new Builder();
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc, new Builder(new TextToElements()));
        Document expectedResult = builder.build(
          new File("data/xinclude/output/c2d.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    private static class TextToElements extends NodeFactory {
        
        public Nodes makeText(String data) {
            Nodes result = new Nodes();
            result.append(new Element("empty1"));
            result.append(new Element("empty2"));
            return result;
        }
        
    }

    
    private static class TextToAttribute extends NodeFactory {
        
        public Nodes makeText(String data) {
            return new Nodes(new Attribute("name", data));
        }
        
    }

 
    public void testUnrecognizedXPointerScheme() 
      throws ParsingException, IOException {
      
        File input = new File("data/xinclude/input/unrecognizedscheme.xml");
        Builder builder = new Builder();
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("Allowed unrecognized scheme");
        }
        catch (XIncludeException success) {
            assertNotNull(success.getMessage());
        }
        
    }
     
    
    public void testUnrecognizedXPointerSchemeWithFallback() 
      throws IOException, ParsingException, XIncludeException {
      
        File input = new File("data/xinclude/input/unrecognizedschemewithfallback.xml");
        File output = new File("data/xinclude/output/unrecognizedschemewithfallback.xml");
        Builder builder = new Builder();
        Document doc = builder.build(input);
        Document actual = XIncluder.resolve(doc);
        Document expected = builder.build(output);
        assertEquals(expected, actual);
        
    }
     
    
    public void testIncludeTextWithCustomNodeFactoryThatChangesElementNames() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/c1.xml");
        Builder builder = new Builder();
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc, new Builder(new NodeFactoryTest.CFactory()));
        Document expectedResult = builder.build(
          new File("data/xinclude/output/c1a.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testIncludeTextWithCustomNodeFactoryThatOnlyReturnsRoot() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/c1.xml");
        Builder builder = new Builder();
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc, new Builder(new NodeFactoryTest.MinimizingFactory()));
        Document expectedResult = builder.build(
          new File("data/xinclude/output/c1b.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testIncludeTextWithCustomNodeFactoryThatFiltersElementsNamedB() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/d1.xml");
        Builder builder = new Builder();
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc, new Builder(new NodeFactoryTest.BFilter()));
        Document expectedResult = builder.build(
          new File("data/xinclude/output/d1.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testIncludeTextWithCustomNodeFactoryThatReturnsEachNonRootElementThreeTimes() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/c1.xml");
        Builder builder = new Builder();
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc, 
          new Builder(new NodeFactoryTest.TripleElementFilter()));
        Document expectedResult = builder.build(
          new File("data/xinclude/output/triple.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void test1() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/test.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/test.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testBaseURIsPreservedInSameDocumentInclusion() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/includefromsamedocumentwithbase.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/includefromsamedocumentwithbase.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    /* public void testResolveNodes() 
      throws IOException, ParsingException, XIncludeException {
        File dir = new File("data/xinclude/input/");
        Element include = new Element("xi:include", XIncluder.XINCLUDE_NS);
        include.setBaseURI(dir.toURL().toExternalForm());
        include.addAttribute(new Attribute("href", "disclaimer.xml"));
        Nodes in = new Nodes(include);  
        Nodes out = XIncluder.resolve(in);
        assertEquals(1, out.size());
        Element result = (Element) out.get(0);
        assertEquals("disclaimer",result.getQualifiedName());
    } */

    
    public void testNullBaseURI() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/disclaimer.xml");
        String data = "<document xmlns:xi='http://www.w3.org/2001/XInclude'>"
          + "\n  <p>120 Mz is adequate for an average home user.</p>"
          + "\n  <xi:include href='" + input.toURL() + "'/>\n</document>";
        Reader reader = new StringReader(data);
        Document doc = builder.build(reader);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/c1.xml")
        );
        assertEquals(expectedResult, result);

    }  
    
    
    public void testBadIRIIsAFatalError() 
      throws IOException, ParsingException, XIncludeException {
     
        String data = "<document xmlns:xi='http://www.w3.org/2001/XInclude'>"
          + "<xi:include href='http://www.example.com/a%5.html'>"
          + "<xi:fallback>Ooops!</xi:fallback></xi:include></document>";
        Reader reader = new StringReader(data);
        Document doc = builder.build(reader);
        try {
            XIncluder.resolve(doc);
            fail("Resolved fallback when encountering a syntactically incorrect URI");
        }
        catch (BadHrefAttributeException success) {
            assertNotNull(success.getMessage());
        }
        
    }
    
    
    public void testBadIRIWithUnrecognizedSchemeIsAFatalError() 
      throws IOException, ParsingException, XIncludeException {
     
        String data = "<doc xmlns:xi='http://www.w3.org/2001/XInclude'>"
          + "<xi:include href='scheme://www.example.com/a%5.html'>"
          + "<xi:fallback>Ooops!</xi:fallback></xi:include></doc>";
        Reader reader = new StringReader(data);
        Document doc = builder.build(reader);
        try {
            XIncluder.resolve(doc);
            fail("Resolved fallback when encountering a syntactically incorrect URI");
        }
        catch (BadHrefAttributeException success) {
            assertNotNull(success.getMessage());
        }
        
    }
    
    
    public void testGoodIRIWithUnrecognizedSchemeIsAResourceError() 
      throws IOException, ParsingException, XIncludeException {
     
        String data = "<document xmlns:xi='http://www.w3.org/2001/XInclude'>"
          + "<xi:include href='scheme://www.example.com/a.html'>"
          + "<xi:fallback>Correct!</xi:fallback></xi:include></document>";
        Reader reader = new StringReader(data);
        Document doc = builder.build(reader);
        Document result = XIncluder.resolve(doc);
        assertEquals("<?xml version=\"1.0\"?>\r\n" 
                + "<document xmlns:xi=\"http://www.w3.org/2001/XInclude\">Correct!</document>\r\n", 
                result.toXML());
        
    }
    
    
    public void testBadAcceptAttribute() 
      throws ParsingException, IOException, XIncludeException {
      
        String data = "<document xmlns:xi='http://www.w3.org/2001/XInclude'>"
          + "\n  <p>120 MHz is adequate for an average home user.</p>"
          + "\n  <xi:include href='http://www.example.com' " 
          + "accept='text/html&#x0D;&#x0A;Something: bad'/>\n</document>";
        Reader reader = new StringReader(data);
        Document doc = builder.build(reader);
        try {
            XIncluder.resolve(doc);
            fail("Allowed accept header containing carriage return linefeed");
        }
        catch (BadHTTPHeaderException success) {
            assertNotNull(success.getMessage());
        }

    }  
    
    
    public void testBadAcceptAttributeWithLatin1Character() 
      throws ParsingException, IOException, XIncludeException {
      
        String data = "<document xmlns:xi='http://www.w3.org/2001/XInclude'>"
          + "\n  <p>120 MHz is adequate for an average home user.</p>"
          + "\n  <xi:include href='http://www.example.com' " 
          + "accept='text/html&#xA0;Something: bad'/>\n</document>";
        Reader reader = new StringReader(data);
        Document doc = builder.build(reader);
        try {
            XIncluder.resolve(doc);
            fail("Allowed accept header containing non-ASCII character");
        }
        catch (BadHTTPHeaderException success) {
            assertNotNull(success.getMessage());
        }

    }  
    
    
    public void testUnrecognizedAttributesAreIgnored() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/extraattributes.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/c1.xml")
        );
        assertEquals(expectedResult, result);

    }
    
    
    public void testEmptyFallback() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/emptyfallback.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/emptyfallback.xml")
        );
        assertEquals(expectedResult, result);

    }
    
    
    public void testFallbackInIncludedDocument() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/metafallbacktest.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/metafallbacktest.xml")
        );
        assertEquals(expectedResult, result);

    }
    
    
    public void testFallbackInIncludedDocumentUsesAnIntradocumentXPointer() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/metafallbacktest6.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/metafallbacktest6.xml")
        );
        assertEquals(expectedResult, result);

    }
    
    
    public void testFallbackInIncludedDocumentIncludesADocumentWithParseEqualsText() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/metafallbacktest2.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/metafallbacktest2.xml")
        );
        assertEquals(expectedResult, result);

    }

        
    
    public void testFallbackInIncludedDocumentWithBadParseAttribute() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/metafallbacktest3.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("Allowed bad parse attribute");
        }
        catch (BadParseAttributeException success) {
            assertNotNull(success.getMessage());
        }

    }
        
    
    public void testFallbackInIncludedDocumentWithMissingHrefAndParseAttributes() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/metafallbacktest4.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("Allowed bad parse attribute");
        }
        catch (NoIncludeLocationException success) {
            assertNotNull(success.getMessage());
        }

    }
        
    
    public void testFallbackInIncludedDocumentWithFragmentID() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/metafallbacktestwithfragmentid.xml");
        Document doc = builder.build(input);
        try {
            Document result = XIncluder.resolve(doc);
            fail("Allowed document with fragment ID in href attribute");
        }
        catch (BadHrefAttributeException success) {
            assertNotNull(success.getMessage());
        }
        /* Document expectedResult = builder.build(
          new File("data/xinclude/output/metafallbacktest.xml")
        );
        assertEquals(expectedResult, result);*/

    }
    

    public void testXPointerResourceErrorInIncludedDocument() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/metafallbacktest5.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/metafallbacktest5.xml")
        );
        assertEquals(expectedResult, result);

    }
    
    
    public void testFallbackInIncludedDocumentThatResolvesToNonElement() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/metafallbacktotexttest.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/metafallbacktotexttest.xml")
        );
        assertEquals(expectedResult, result);

    }
    
    
    public void testFallbackInIncludedDocumentWithXPointer() 
      throws ParsingException, IOException, XIncludeException {
        // This test case activates processFallbackSilently
        File input = new File("data/xinclude/input/metafallbacktestwithxpointer.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/metafallbacktestwithxpointer.xml")
        );
        assertEquals(expectedResult, result);

    }
    
    
    // test case where fallback falls back to text and comments rather than
    // an element
    public void testFallbackInIncludedDocumentWithXPointer2() 
      throws ParsingException, IOException, XIncludeException {
        // This test case activates processFallbackSilently
        File input = new File("data/xinclude/input/metafallbacktestwithxpointer2.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/metafallbacktestwithxpointer2.xml")
        );
        assertEquals(expectedResult, result);

    }
    

    public void testNoFallbackInIncludedDocumentWithXPointer() 
      throws ParsingException, IOException, XIncludeException {
        
        // This test case activates processFallbackSilently
        File input = new File("data/xinclude/input/metamissingfallbacktestwithxpointer.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("Should have thrown IOException");
        }
        catch (IOException success) {
            assertNotNull(success.getMessage());
        }

    }
    
    
    public void testFallbackInIncludedDocumentHasBadXPointer() 
      throws ParsingException, IOException, XIncludeException {
        // This test case activates processFallbackSilently
        File input = new File("data/xinclude/input/metafallbackwithbadxpointertest.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("Should have thrown XIncludeException");
        }
        catch (XIncludeException success) {
            assertNotNull(success.getMessage());
            assertNotNull(success.getCause());
        }

    }
    
        
    // from the XInclude CR
    public void testC1() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/c1.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/c1.xml")
        );
        assertEquals(expectedResult, result);

    }

    
    // same test with explicit parse="xml"
    public void testParseEqualsXML() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/parseequalxml.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/c1.xml")
        );
        assertEquals(expectedResult, result);

    }

    
    // In this case the circle is OK because the XPointer
    // doesn't cover the whole xinclude:include element
    public void testAcceptableCirclePointer() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/legalcircle.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/legalcircle.xml")
        );
        assertEquals(expectedResult, result);        

    }
    
    
    // from the XInclude CR
    public void testC2() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/c2.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/c2.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    // from the XInclude CR
    public void testC3() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/c3.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/c3.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    // C4 skipped for the moment because it uses XPointers
    // that XOM doesn't yet support

    // from the XInclude CR
    // Don't use this one yet, because there appear to be 
    // mistakes in the spec examples
    /*public void testC5() throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/c5.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(new File("data/xinclude/output/c5.xml"));
        XMLAssert.assertEquals(expectedResult, result);
        
    } */
    
    
    private static class TextNodeFactory extends NodeFactory {
        
        public Nodes makeText(String data) {
            return new Nodes(new TextSubclass(data));
        }
        
    }
    
    private static class TextSubclass extends Text {
        
        TextSubclass(String data) {
            super(data);
        }
        
        public Node copy() {
            return new TextSubclass(this.getValue());
        }
        
    }
    
    
    public void testRecurseWithinSameDocument() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/recursewithinsamedocument.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/recursewithinsamedocument.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testSiblingIncludes() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/paralleltest.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/paralleltest.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testNamespaces() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/namespacetest.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/namespacetest.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testIncludeReferencesItItself() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/internalcircular.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("Allowed include element to reference itself");
        }
        catch (InclusionLoopException success) {
            assertNotNull(success.getMessage());   
        }
        
    }
    
    
    public void testIncludeReferencesItsAncestor() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/internalcircularviaancestor.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("Allowed include element to reference its own ancestor");
        }
        catch (InclusionLoopException success) {
            assertNotNull(success.getMessage());   
        }
        
    }
    
    
    public void testNoInclusions() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/latin1.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/latin1.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void test2() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/simple.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/simple.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testReplaceRoot() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/roottest.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/roottest.xml")
        );
        assertEquals(expectedResult, result);
        
    }


    // In this test the included document has a prolog and an epilog
    public void testReplaceRoot2() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/roottest2.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/roottest2.xml")
        );
        assertEquals(expectedResult, result);
        
    }

    
    public void testIncludeElementsCannotHaveIncludeChildren() 
      throws ParsingException, IOException, XIncludeException {
        File input = new File("data/xinclude/input/nestedxinclude.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("allowed include element to contain another include element");
        }
        catch (XIncludeException success) {
            assertNotNull(success.getMessage());          
        }
    }

    
    public void testIncludeElementsCannotHaveChildrenFromXIncludeNamespace() 
      throws ParsingException, IOException, XIncludeException {
        File input = new File("data/xinclude/input/nestedxincludenamespace.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("allowed include element to contain another include element");
        }
        catch (XIncludeException success) {
            assertNotNull(success.getMessage());          
        }
    }

    
    public void testFallbackIsNotChildOfIncludeElement() 
      throws ParsingException, IOException, XIncludeException {
        File input = new File("data/xinclude/input/nakedfallback.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("allowed fallback that was not child of an include element");
        }
        catch (XIncludeException success) {
            assertNotNull(success.getMessage());          
        }
    }

    
    public void testFallbackCantContainFallbackElement() 
      throws ParsingException, IOException, XIncludeException {
        File input = new File("data/xinclude/input/fallbackcontainsfallback.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("allowed fallback inside another fallback element");
        }
        catch (XIncludeException success) {
            assertNotNull(success.getMessage());          
        }
    }

    
    // In this test the fallback is activated.
    public void testMultipleFallbacks() 
      throws ParsingException, IOException, XIncludeException {
        File input = new File("data/xinclude/input/multiplefallbacks.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("allowed multiple fallback elements");
        }
        catch (XIncludeException success) {
            assertNotNull(success.getMessage());          
        }
    }

    
    // In this test the fallback is not needed.
    public void testMultipleFallbacks2() 
      throws ParsingException, IOException, XIncludeException {
        File input = new File("data/xinclude/input/multiplefallbacks2.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("allowed multiple fallback elements");
        }
        catch (XIncludeException success) {
            assertNotNull(success.getMessage());          
        }
    }

    
    public void testDocumentIncludesItself() 
      throws ParsingException, IOException, XIncludeException {
        File input = new File("data/xinclude/input/circle1.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("allowed parsed include of self");
        }
        catch (InclusionLoopException success) {
            assertNotNull(success.getMessage());
            assertEquals(input.toURL().toExternalForm(), success.getURI());           
        }
    }

    
    public void testInclusionLoopWithLength2Cycle() 
      throws ParsingException, IOException, XIncludeException {
        
        File input = new File("data/xinclude/input/circle2a.xml");
        File errorFile = new File("data/xinclude/input/circle2b.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("allowed circular include, cycle length 1");
        }
        catch (InclusionLoopException success) {
            assertTrue(success.getMessage().indexOf(errorFile.toURL().toExternalForm()) > 1);           
            assertTrue(success.getMessage().indexOf(input.toURL().toExternalForm()) > 1);           
            assertEquals(errorFile.toURL().toExternalForm(), success.getURI());           
        }
        
    }
    
    
    public void testMissingHref() 
      throws ParsingException, IOException, XIncludeException {
        
        File input = new File("data/xinclude/input/missinghref.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("allowed missing href");
        }
        catch (NoIncludeLocationException success) {
            assertNotNull(success.getMessage());
            assertEquals(doc.getBaseURI(), success.getURI());           
        }
        
    }
    
    
    public void testBadParseAttribute() 
      throws ParsingException, IOException, XIncludeException {
        
        File input = new File("data/xinclude/input/badparseattribute.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("allowed bad parse attribute");
        }
        catch (BadParseAttributeException success) {
            assertNotNull(success.getMessage());
            URL u1 = input.toURL();
            URL u2 = new URL(success.getURI());
            assertEquals(u1, u2);
        }
        
    }
    
    
    public void testUnavailableResource() 
      throws ParsingException, IOException, XIncludeException {
        File input = new File("data/xinclude/input/missingfile.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("allowed unresolvable resource");
        }
        catch (IOException success) {
            assertNotNull(success.getMessage());  
        }
        
    }
    
    
    public void testFallback() 
      throws ParsingException, IOException, XIncludeException {
        
        File input = new File("data/xinclude/input/fallbacktest.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/fallbacktest.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testFallbackWithRecursiveInclude() 
      throws ParsingException, IOException, XIncludeException {
        
        File input = new File("data/xinclude/input/fallbacktest2.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/fallbacktest2.xml")
        );
        assertEquals(expectedResult, result);
        
    }

    
    public void testEncodingAttribute() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/utf16.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/utf16.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testXPointerBareNameID() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/xptridtest.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/xptridtest.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testShorthandXPointerMatchesNothing() 
      throws ParsingException, IOException {
      
        File input = new File("data/xinclude/input/xptridtest2.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("Resolved a document with an XPointer " +              "that selects no subresource");
        }
        catch (XIncludeException success) {
            assertNotNull(success.getMessage());
            // Must compare URLs instead of strings here to avoid 
            // issues of whether a file URL begins file:/ or file:///
            URL u1 = input.toURL();
            URL u2 = new URL(success.getURI());
            assertEquals(u1, u2);  
        }
        
        
        /* I used to think this case included nothing.
           Now I think an XPointer that matches no
           subresource, and does not have a fallback is in error.
        Document expectedResult = builder.build(
          new File("data/xinclude/output/xptridtest2.xml")
        );
        assertEquals(expectedResult, result);
        */
        
    }
    
    
    public void testXPointerPureTumbler() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/xptrtumblertest.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/xptrtumblertest.xml")
        );
        assertEquals(expectedResult, result);
        
    }

    
    public void testUnrecognizedColonizedSchemeNameBackedUpByTumbler() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/colonizedschemename.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/xptrtumblertest.xml")
        );
        assertEquals(expectedResult, result);
        
    }

    
    public void testXPointerSyntaxErrorInSecondPart() 
      throws ParsingException, IOException {
      
        File input = new File("data/xinclude/input/laterfailure.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("Didn't find syntax error in 2nd XPointer part" +
                " when the first part resolved successfully");
        }
        catch (XIncludeException success) {
            assertNotNull(success.getMessage());
        }  
        
    }

    
    public void testXPointerSyntaxErrorMissingFinalParenthesis() 
      throws ParsingException, IOException {
      
        File input = new File("data/xinclude/input/laterfailure2.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("Didn't find syntax error in 2nd XPointer part" +
                " when the first part resolved successfully");
        }
        catch (XIncludeException success) {
            assertNotNull(success.getMessage());
        }  
        
    }

    
    // Test we can include from same document using only 
    // an xpointer attribute
    public void testOnlyXPointer() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/onlyxpointer.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/onlyxpointer.xml")
        );
        // dumpResult(input, result); 
        
        assertEquals(expectedResult, result);
        
    }


    // Test with 3 element schemes in the XPointer.
    // The first and second one point to nothing. The third one
    // selects something.
    public void testXPointerTripleTumbler() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/xptr2tumblertest.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/xptrtumblertest.xml")
        );
        assertEquals(expectedResult, result);
        
    }

    
    // Test with 2 element schemes in the XPointer.
    // The first one uses an ID that doesn't exist 
    // and points to nothing. The second one
    // selects something.
    public void testXPointerDoubleTumbler() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/xptrdoubletumblertest.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/xptrtumblertest.xml")
        );
        assertEquals(expectedResult, result);
        
    }


    // Make sure XPointer failures are treated as a resource error,
    // not a fatal error.
    public void testXPointerFailureIsAResourceError() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(
          "data/xinclude/input/xptrtumblerfailsbutfallback.xml"
        );
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        // For debugging
        // dumpResult(input, result); 
        Document expectedResult = builder.build(
          new File("data/xinclude/output/xptrtumblertest.xml")
        );
        assertEquals(expectedResult, result);
        
    }

    
    // Make sure XPointer syntax errors are treated as a resource 
    // error, not a fatal error per section 4.2 of XInclude CR
    /* Resources that are unavailable for any reason 
      (for example the resource doesn't exist, connection 
      difficulties or security restrictions prevent it from being 
      fetched, the URI scheme isn't a fetchable one, the resource 
      is in an unsuppored encoding, the resource is determined 
      through implementation-specific mechanisms not to be XML, or a 
      syntax error in an [XPointer Framework]) result in a resource 
      error.  */
    public void testXPointerSyntaxErrorIsAResourceError() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(
          "data/xinclude/input/xptrsyntaxerrorbutfallback.xml"
        );
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/xptrtumblertest.xml")
        );
        assertEquals(expectedResult, result);
        
    }

    
    // Test with 3 element schemes in the XPointer,
    // separated by white space.
    // The first one points to nothing. The third one
    // selects something.
    public void testXPointerTumblerWithWhiteSpace() 
      throws ParsingException, IOException, XIncludeException {
      
        File input 
          = new File("data/xinclude/input/xptrtumblertest3.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/xptrtumblertest.xml")
        );
        assertEquals(expectedResult, result);
        
    }

    
    public void testXPointerTumblerMatchesNothing() 
      throws ParsingException, IOException {
      
        File input = new File(
          "data/xinclude/input/xptrtumblertest2.xml"
        );
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("Did not error on XPointer matching nothing");
        }
        catch (XIncludeException success) {
            assertNotNull(success.getMessage());
            URL u1 = input.toURL();
            URL u2 = new URL(success.getURI());
            assertEquals(u1, u2);            
        }
        
    }
    
    
    public void testMalformedXPointer() 
      throws ParsingException, IOException {   
        
        File input = new File("data/xinclude/input/badxptr.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("Allowed malformed XPointer");
        }
        catch (XIncludeException success) {
            assertNotNull(success.getMessage());
            URL u1 = input.toURL();
            URL u2 = new URL(success.getURI());
            assertEquals(u1, u2);            
        }
        
    }
    
    
    public void testAnotherMalformedXPointer() 
      throws ParsingException, IOException {
        
        // testing use of non NCNAME as ID
        File input = new File("data/xinclude/input/badxptr2.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("Allowed another malformed XPointer");
        }
        catch (XIncludeException success) {
            assertNotNull(success.getMessage());
            URL u1 = input.toURL();
            URL u2 = new URL(success.getURI());
            assertEquals(u1, u2);            
        }
        
    }
    
    
    public void testMalformedXPointerWithFallback() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/xptrfallback.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(new File(
          "data/xinclude/output/xptrfallback.xml")
        );
        assertEquals(expectedResult, result);
                
    }
    
    
    public void testIDAndTumbler() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(
          "data/xinclude/input/xptridandtumblertest.xml"
        );
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(new File(
          "data/xinclude/output/xptridandtumblertest.xml")
        );
        assertEquals(expectedResult, result);
                
    }

    
    public void testAutoDetectUTF16BigEndianWithByteOrderMark() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(
          "data/xinclude/input/UTF16BigEndianWithByteOrderMark.xml"
        );
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(new File(
          "data/xinclude/output/UTF16BigEndianWithByteOrderMark.xml")
        );
        assertEquals(expectedResult, result);
                
    }

    
    public void testAutoDetectUTF16LittleEndianWithByteOrderMark() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(
          "data/xinclude/input/UTF16LittleEndianWithByteOrderMark.xml"
        );
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(new File(
          "data/xinclude/output/UTF16LittleEndianWithByteOrderMark.xml"
        ));
        assertEquals(expectedResult, result);
                
    }

    
    public void testAutoDetectUTF8WithByteOrderMark() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(
          "data/xinclude/input/UTF8WithByteOrderMark.xml"
        );
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/UTF8WithByteOrderMark.xml")
        );
        assertEquals(expectedResult, result);
                
    }

    
    public void testAutoDetectUnicodeBigUnmarked() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(
          "data/xinclude/input/UnicodeBigUnmarked.xml"
        );
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/UnicodeBigUnmarked.xml")
        );
        assertEquals(expectedResult, result);
                
    }

    
    public void testUnicodeLittleUnmarked() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(
          "data/xinclude/input/UnicodeLittleUnmarked.xml"
        );
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/UnicodeLittleUnmarked.xml")
        );
        assertEquals(expectedResult, result);
                
    }

    
/*// Java doesn't yet support the UTF-32BE and UTF32LE encodings
    public void testUTF32BE() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(
          "data/xinclude/input/UTF32BE.xml"
        );
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/UTF32BE.xml")
        );
        assertEquals(expectedResult, result);
                
    }

    public void testUTF32LE() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(
          "data/xinclude/input/UTF32LE.xml"
        );
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/UTF32LE.xml")
        );
        assertEquals(expectedResult, result);
                
    }
*/
    public void testEBCDIC() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(
          "data/xinclude/input/EBCDIC.xml"
        );
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expected = builder.build(
          new File("data/xinclude/output/EBCDIC.xml")
        );
        assertEquals(expected, result);
                
    }

    
    // This test requires files that I have not received permission
    // to distribute so for the moment you won't be able to run it.
    // For my own use it checks to see if the files are present
    // and runs if it does find them. You can't just install the
    // XInclude-Test-Suite data as distributed by the W3C here.
    // Some of those tests rely on optional features XOM does not
    // support such as the xpointer() scheme and notations.
    // Plus some of those tests have mistakes. You need my patched 
    // version of the tests.
    public void testXIncludeTestSuite()  
      throws ParsingException, IOException, XIncludeException {
        
        File testDescription = new File("data/XInclude-Test-Suite/testdescr.xml");
        if (testDescription.exists()) {
            Document master = builder.build(testDescription);
            Element testsuite = master.getRootElement();
            Elements testcases = testsuite.getChildElements("testcases");
            for (int i = 0; i < testcases.size(); i++) {
                Element group = testcases.get(i);   
                String basedir = group.getAttributeValue("basedir");
                Elements cases = group.getChildElements("testcase");
                for (int j = 0; j < cases.size(); j++) {
                    Element testcase = cases.get(j);
                    String id = testcase.getAttributeValue("id");
                    String features = testcase.getAttributeValue("features");
                    if (features != null) {
                        if (features.indexOf("unexpanded-entities") >= 0) continue;
                        if (features.indexOf("unparsed-entities") >= 0) continue;
                        if (features.indexOf("xpointer-scheme") >= 0) continue;
                    }
                    String description 
                      = testcase.getFirstChildElement("description").getValue();
                    File input = new File("data/XInclude-Test-Suite/" 
                      + basedir + '/' + testcase.getAttributeValue("href"));
                    Element output = testcase.getFirstChildElement("output");
                    if (output == null) { // test failure   
                        try {
                            Document doc = builder.build(input);
                            XIncluder.resolveInPlace(doc);
                            fail("Failed test " + id + ": " + description);
                        }
                        catch (XIncludeException success) {
                            assertNotNull(success.getMessage());
                        }
                        catch (IOException success) {
                           assertNotNull(success.getMessage());  
                        }
                        catch (ParsingException success) {
                            assertNotNull(success.getMessage());
                        }
                    }
                    else {
                        File in = new File("data/XInclude-Test-Suite/" 
                          + basedir + '/' + output.getValue());
                        Document expected = builder.build(in);
                        Document doc = builder.build(input);
                        XIncluder.resolveInPlace(doc);
                        assertEquals("Error when processing  " 
                          + in.getName(), expected, doc);
                    }
                }          
            }
        } 
        
    } 
    
    
    private void compare(File expected, File input) 
      throws IOException, ParsingException, XIncludeException {
        
        Document expectedDoc = builder.build(expected);
        Document doc = builder.build(input);
        XIncluder.resolveInPlace(doc);
        assertEquals("Error when processing  " 
          + input.getName(), expectedDoc, doc);
        
    }
    
    
 

  // Turn off these tests because Java doesn't support UCS4 yet
 /*   public void testAutoDetectUCS4BE() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/UCS4BE.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/UTF8WithByteOrderMark.xml")
        );
        assertEquals(expectedResult, result);
                
    }

    public void testAutoDetectUCS4LE() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/UCS4LE.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/UTF8WithByteOrderMark.xml")
        );
        assertEquals(expectedResult, result);
                
    } */
    
    
    // Need a test case where A includes B, B includes C
    // and B encounters the error (e.g. a missing href)
    // to make sure B's URL is in the error message, not A's
    public void testChildDocumentSetsErrorURI() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/toplevel.xml");
        File error = new File("data/xinclude/input/onedown.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("Missing HREF not detected");
        }
        catch (NoIncludeLocationException success) {
            assertNotNull(success.getMessage());
            URL u1 = error.toURL();
            URL u2 = new URL(success.getURI());
            assertEquals(u1, u2);            
        }
                
    } 

    
    public void testColonizedNameForIdValueInElementScheme() 
      throws ParsingException, IOException {
      
        File input = new File("data/xinclude/input/badxptr3.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("Bad ID in element not detected");
        }
        catch (XIncludeException success) {
            assertNotNull(success.getMessage());   
        }
                
    } 

    
    public void testBadIdValueInElementScheme() 
      throws ParsingException, IOException {
      
        File input = new File("data/xinclude/input/badxptr4.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("Bad ID in element not detected");
        }
        catch (XIncludeException success) {
            assertNotNull(success.getMessage());   
        }
                
    } 

    
    public void testCirclePointer() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/circlepointer1.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("Allowed circular reference via XPointer");
        }
        catch (InclusionLoopException success) {
            assertNotNull(success.getMessage());   
        }

    }
    
    
    public void testXPointerOverridesFragmentID() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(
          "data/xinclude/input/xpointeroverridesfragmentid.xml"
        );
        Document doc = builder.build(input);
        try {
            Document result = XIncluder.resolve(doc);
            fail("Allowed href attribute with fragment ID");
        }
        catch (XIncludeException success) {
            assertNotNull(success.getMessage());
        }


        /*Document expected = builder.build(
          new File("data/xinclude/output/xpointeroverridesfragmentid.xml")
        );
        assertEquals(expected, result);*/
                
    }
    
 
    public void testFailsOnFragmentID() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(
          "data/xinclude/input/ignoresfragmentid.xml");
        Document doc = builder.build(input);
        try {
            Document result = XIncluder.resolve(doc);
            fail("Allowed href attribute with fragment ID");
        }
        catch (XIncludeException success) {
            assertNotNull(success.getMessage());
        }
        /* Document expected = builder.build(
          new File("data/xinclude/output/ignoresfragmentid.xml")
        );
        assertEquals(expected, result);*/
                
    }
    
 
    public void testLineEnds() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(
          "data/xinclude/input/lineends.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expected = builder.build(
          new File("data/xinclude/output/lineends.xml")
        );
        assertEquals(expected, result);
                
    }
    
 
    // This is semantically bad; but still meets the
    // syntax of fragment IDs from RFC 2396
    public void testBadXPointerInFragmentIDIsIgnored() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(
          "data/xinclude/input/meaninglessfragmentid.xml");
        Document doc = builder.build(input);
        try {
            Document result = XIncluder.resolve(doc);
            fail("Allowed href attribute with fragment ID");
        }
        catch (XIncludeException success) {
            assertNotNull(success.getMessage());
        }
        /* Document expected = builder.build(
          new File("data/xinclude/output/ignoresfragmentid.xml")
        );
        assertEquals(expected, result); */
                
    }
    
    
    // These tests actually connect to IBiblio to load the included
    // data. This is necessary because file URLs don't support
    // content negotiation
    public void testAcceptLanguageFrench() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/acceptfrench.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/acceptfrench.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testAcceptLanguageEnglish() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/acceptenglish.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/acceptenglish.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testAcceptPlainText() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/acceptplaintext.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/acceptplaintext.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testAcceptHTML() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File("data/xinclude/input/accepthtml.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File("data/xinclude/output/accepthtml.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testBadHTTPHeaderExceptionConstructor() {
     
        String message = "test";
        XIncludeException ex = new BadHTTPHeaderException(
           message, "http://www.example.com/");
        assertEquals(message, ex.getMessage());
        assertEquals("http://www.example.com/", ex.getURI());
        
    }
 
    
    public void testBadHrefAttributerExceptionConstructor() {
     
        String message = "test";
        Exception ex = new BadHrefAttributeException(message);
        assertEquals(message, ex.getMessage());
        
    }
 
    
}
