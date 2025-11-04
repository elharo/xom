/* Copyright 2002-2005, 2011, 2018 Elliotte Rusty Harold
   
   This library is free software; you can redistribute it and/or modify
   it under the terms of version 2.1 of the GNU Lesser General Public 
   License as published by the Free Software Foundation.
   
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
   GNU Lesser General Public License for more details.
   
   You should have received a copy of the GNU Lesser General Public
   License along with this library. If not, see
   <https://www.gnu.org/licenses/>.
   
   You can contact Elliotte Rusty Harold by sending e-mail to
   elharo@ibiblio.org. Please include the word "XOM" in the
   subject line. The XOM home page is located at https://xom.nu/
*/

package nu.xom.tests;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Comment;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Namespace;
import nu.xom.Node;
import nu.xom.NodeFactory;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.Text;
import nu.xom.xinclude.BadEncodingAttributeException;
import nu.xom.xinclude.BadHTTPHeaderException;
import nu.xom.xinclude.BadHrefAttributeException;
import nu.xom.xinclude.BadParseAttributeException;
import nu.xom.xinclude.InclusionLoopException;
import nu.xom.xinclude.NoIncludeLocationException;
import nu.xom.xinclude.XIncludeException;
import nu.xom.xinclude.XIncluder;

/**
 * <p>
 * Unit tests for the XInclude and XPointer engines.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.3.0
 *
 */
public class XIncludeTest extends XOMTestCase {

    private static boolean windows 
      = System.getProperty("os.name", "Unix").indexOf("Windows") >= 0;
    
    
    public XIncludeTest(String name) {
        super(name);
    }

    
    private Builder builder = new Builder();
    private File inputDir;
    private File outputDir;
    
    // This class tests error conditions, which Xerces
    // annoyingly logs to System.err. This hides System.err 
    // before each test and restores it after each test.
    private PrintStream systemErr = System.err;
    
    
    protected void setUp() {
        
        System.setErr(new PrintStream(new ByteArrayOutputStream()));
        
        inputDir = new File("data");
        inputDir = new File(inputDir, "xinclude");
        inputDir = new File(inputDir, "input");
        
        outputDir = new File("data");
        outputDir = new File(outputDir, "xinclude");
        outputDir = new File(outputDir, "output");
        
    }
    
    
    protected void tearDown() {
        System.setErr(systemErr);
    }    
    
    private void dumpResult(File original, Document result)
      throws IOException {
        
        String name = original.getName();
        File debug = new File("data");
        debug = new File(debug, "xinclude");
        debug = new File(debug, "debug");
        File output = new File(debug, name);
        FileOutputStream out = new FileOutputStream(output);
        Serializer serializer = new Serializer(out);
        serializer.write(result);        
        
    }
    
    
    public void testXPointersResolvedAgainstAcquiredInfoset() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "tobintop.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expected = builder.build(
          new File(outputDir, "tobintop.xml")
        );
        assertEquals(expected, result);
                
    }

    
    public void testXMLBaseUsedToResolveHref() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "xmlbasetest.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expected = builder.build(
          new File(outputDir, "xmlbasetest.xml")
        );
        assertEquals(expected, result);
                
    }

    
    // Tests that use XPointer to
    // grab a part of the document that contains an include element
    // and make sure that's fully resolved too
    public void testResolveThroughXPointer() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "resolvethruxpointer.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "resolvethruxpointer.xml")
        );
        assertEquals(expectedResult, result);
        
    }    
 
    
    public void testXMLBaseOnIncludeElementUsedToResolveHref() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "xmlbasetest2.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expected = builder.build(
          new File(outputDir, "xmlbasetest2.xml")
        );
        assertEquals(expected, result);
                
    }
    

    public void testXMLBaseRetainedFromUnincludedElement() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "xmlbasetest3.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expected = builder.build(
          new File(outputDir, "xmlbasetest3.xml")
        );
        assertEquals(expected, result);
                
    }
    
 
    public void testMarsh() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "marshtest.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "marshtest.xml")
        );
        assertEquals(expectedResult, result);
        
    }

    
    public void testIncludeDocumentThatUsesIntradocumentReferences() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "includedocumentwithintradocumentreferences.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "includedocumentwithintradocumentreferences.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testXMLLangAttributes() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "langtest1.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "langtest1.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testInheritedXMLLangAttributes() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "langtest2.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "langtest2.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testNoLanguageSpecified() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "langtest3.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "langtest3.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    // According to RFC 2396 empty string URI always refers to the 
    // current document irrespective of base URI
    public void testXMLBaseNotUsedToResolveMissingHref() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "marshtestwithxmlbase.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "marshtestwithxmlbase.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testEmptyHrefTreatedSameAsMissingHref() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "marshtestwithxmlbaseandemptyhref.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "marshtestwithxmlbase.xml")
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
        assertEquals("<?xml version=\"1.0\"?>\n" +
           "<root><child2 id=\"p1\" /><child2 id=\"p1\" /></root>\n", result);
        
    }
    

    public void testRelativeURLInBaselessDocument() 
      throws IOException, ParsingException, XIncludeException {
        
        Element root = new Element("root");
        Element child1 = new Element("xi:include", XIncluder.XINCLUDE_NS);
        child1.addAttribute(new Attribute("href", "test.xml"));
        Element child2 = new Element("child2");
        root.appendChild(child1);
        root.appendChild(child2);
        Document in = new Document(root);
        try {
            XIncluder.resolve(in);
            fail("Resolved relative URI in baseless document");
        }
        catch (BadHrefAttributeException success) {
            assertEquals(
              "Could not resolve relative URI test.xml because the "
              + "xi:include element does not have a base URI.", 
              success.getMessage());
        }
        
    }
    

    public void testIncludeTextWithCustomNodeFactory() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "c2.xml");
        Builder builder = new Builder(new TextNodeFactory());
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc, builder);
        Document expectedResult = builder.build(
          new File(outputDir, "c2.xml")
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
      
        File input = new File(inputDir, "c2.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc, new Builder(new TextFilter()));
        Document expectedResult = builder.build(
          new File(outputDir, "c2a.xml")
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
      
        File input = new File(inputDir, "c2.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc, new Builder(new TextToComment()));
        Document expectedResult = builder.build(
          new File(outputDir, "c2b.xml")
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
      
        File input = new File(inputDir, "c2.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc, new Builder(new TextToAttribute()));
        Document expectedResult = builder.build(
          new File(outputDir, "c2c.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testParseEqualsTextWithNodeFactoryThatReplacesTextNodesWithTwoElements()  
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "c2.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc, new Builder(new TextToElements()));
        Document expectedResult = builder.build(
          new File(outputDir, "c2d.xml")
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
      
        File input = new File(inputDir, "unrecognizedscheme.xml");
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
      
        File input = new File(inputDir, "unrecognizedschemewithfallback.xml");
        File output = new File(outputDir, "unrecognizedschemewithfallback.xml");
        Document doc = builder.build(input);
        Document actual = XIncluder.resolve(doc);
        Document expected = builder.build(output);
        assertEquals(expected, actual);
        
    }
     
    
    public void testIncludeTextWithCustomNodeFactoryThatChangesElementNames() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "c1.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc, new Builder(new NodeFactoryTest.CFactory()));
        Document expectedResult = builder.build(
          new File(outputDir, "c1a.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testIncludeTextWithCustomNodeFactoryThatOnlyReturnsRoot() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "c1.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc, new Builder(new NodeFactoryTest.MinimizingFactory()));
        Document expectedResult = builder.build(
          new File(outputDir, "c1b.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testIncludeTextWithCustomNodeFactoryThatFiltersElementsNamedB() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "d1.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc, new Builder(new NodeFactoryTest.BFilter()));
        Document expectedResult = builder.build(
          new File(outputDir, "d1.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testIncludeTextWithCustomNodeFactoryThatReturnsEachNonRootElementThreeTimes() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "c1.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc, 
          new Builder(new NodeFactoryTest.TripleElementFilter()));
        Document expectedResult = builder.build(
          new File(outputDir, "triple.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void test1() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "test.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "test.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testBaseURIsPreservedInSameDocumentInclusion() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "includefromsamedocumentwithbase.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "includefromsamedocumentwithbase.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testBaseURIsPreservedInResultDocument() 
      throws ParsingException, IOException, XIncludeException {
      
        Document doc = new Document(new Element("root"));
        doc.setBaseURI("http://www.example.org/");
        Document result = XIncluder.resolve(doc);
        assertEquals("http://www.example.org/", result.getBaseURI());
        
    }
    
    
    /* public void testResolveNodes() 
      throws IOException, ParsingException, XIncludeException {
        File dir = new File(inputDir, "");
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
      
        File input = new File(inputDir, "disclaimer.xml");
        String data = "<document xmlns:xi='http://www.w3.org/2001/XInclude'>"
          + "\n  <p>120 Mz is adequate for an average home user.</p>"
          + "\n  <xi:include href='" + input.toURI() + "'/>\n</document>";
        Reader reader = new StringReader(data);
        Document doc = builder.build(reader);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "c1.xml")
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
        assertEquals("<?xml version=\"1.0\"?>\n" 
          + "<document xmlns:xi=\"http://www.w3.org/2001/XInclude\">Correct!</document>\n", 
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
      
        File input = new File(inputDir, "extraattributes.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "c1.xml")
        );
        assertEquals(expectedResult, result);

    }
    
    
    public void testEmptyFallback() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "emptyfallback.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "emptyfallback.xml")
        );
        assertEquals(expectedResult, result);

    }
    
    
    public void testFallbackInIncludedDocument() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "metafallbacktest.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "metafallbacktest.xml")
        );
        assertEquals(expectedResult, result);

    }
    
    
    public void testFallbackInIncludedDocumentUsesAnIntradocumentXPointer() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "metafallbacktest6.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "metafallbacktest6.xml")
        );
        assertEquals(expectedResult, result);

    }
    
    
    // changed for b5
    public void testFallbackInIncludedDocumentIncludesADocumentWithParseEqualsText() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "metafallbacktest2.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "metafallbacktest2.xml")
        );
        assertEquals(expectedResult, result);

    }
        
    
    public void testFallbackInIncludedDocumentWithBadParseAttribute() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "metafallbacktest3.xml");
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
      
        File input = new File(inputDir, "metafallbacktest4.xml");
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
      
        File input = new File(inputDir, "metafallbacktestwithfragmentid.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("Allowed document with fragment ID in href attribute");
        }
        catch (BadHrefAttributeException success) {
            assertNotNull(success.getMessage());
        }

    }
    

    // changed in b5
    public void testXPointerIsNotResolvedAgainstTheSourceInfoset() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "metafallbacktest5.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("Allowed XPointer that doesn't resolve against the acquired infoset but does resolve against the source infoset");
        }
        catch (XIncludeException ex) {
            assertNotNull(ex.getMessage());
        }

    }
    
    
    public void testFallbackInIncludedDocumentThatResolvesToNonElement() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "metafallbacktotexttest.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "metafallbacktotexttest.xml")
        );
        assertEquals(expectedResult, result);

    }
    
    
    public void testFallbackInIncludedDocumentWithXPointer() 
      throws ParsingException, IOException, XIncludeException {
        // This test case activates processFallbackSilently
        File input = new File(inputDir, "metafallbacktestwithxpointer.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "metafallbacktestwithxpointer.xml")
        );
        assertEquals(expectedResult, result);

    }
    
    
    // changed in b5
    // test case where fallback falls back to text and comments rather than
    // an element
    public void testFallbackInIncludedDocumentWithXPointer2() 
      throws ParsingException, IOException, XIncludeException {
        
        // This test case activates processFallbackSilently
        File input = new File(inputDir, "metafallbacktestwithxpointer2.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "metafallbacktestwithxpointer2.xml")
        );
        assertEquals(expectedResult, result);

    }
    

    public void testNoFallbackInIncludedDocumentWithXPointer() 
      throws ParsingException, IOException, XIncludeException {
        
        // This test case activates processFallbackSilently
        File input = new File(inputDir, "metamissingfallbacktestwithxpointer.xml");
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
        File input = new File(inputDir, "metafallbackwithbadxpointertest.xml");
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
      
        File input = new File(inputDir, "c1.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "c1.xml")
        );
        assertEquals(expectedResult, result);

    }

    
    public void testRelativeURLBaseURIFixup() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "relative.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        assertEquals(doc.getBaseURI(), result.getBaseURI());
        Document expectedResult = builder.build(
          new File(outputDir, "relative.xml")
        );
        assertEquals(expectedResult, result);
        Element root = result.getRootElement();
        Element red = root.getFirstChildElement("red");
        String base = red.getAttributeValue("base", Namespace.XML_NAMESPACE);
        assertEquals("basedata/red.xml", base);

    }

    
    // same test with explicit parse="xml"
    public void testParseEqualsXML() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "parseequalxml.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "c1.xml")
        );
        assertEquals(expectedResult, result);

    }

    
    // changed in b5
    public void testAcceptableCirclePointer() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "legalcircle.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("Allowed circular reference");
        }
        catch (InclusionLoopException success) {
            assertNotNull(success.getMessage());
        }

    }
    
    
    // from the XInclude CR
    public void testC2() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "c2.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "c2.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    // from the XInclude CR
    public void testC3() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "c3.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "c3.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    // C4 skipped for the moment because it uses XPointers
    // that XOM doesn't yet support

    // from the XInclude CR
    // Don't use this one yet, because there appear to be 
    // mistakes in the spec examples
    /*public void testC5() throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "c5.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(new File(outputDir, "c5.xml"));
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
        
        public Text copy() {
            return new TextSubclass(this.getValue());
        }
        
    }
    
    
    public void testRecurseWithinSameDocument() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "recursewithinsamedocument.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "recursewithinsamedocument.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testSiblingIncludes() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "paralleltest.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "paralleltest.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testNamespaces() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "namespacetest.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "namespacetest.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testIncludeReferencesItItself() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "internalcircular.xml");
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
      
        File input = new File(inputDir, "internalcircularviaancestor.xml");
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
      
        File input = new File(inputDir, "latin1.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "latin1.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void test2() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "simple.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "simple.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testReplaceRoot() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "roottest.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "roottest.xml")
        );
        assertEquals(expectedResult, result);
        
    }


    // In this test the included document has a prolog and an epilog
    public void testReplaceRoot2() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "roottest2.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "roottest2.xml")
        );
        assertEquals(expectedResult, result);
        
    }

    
    public void testIncludeElementsCannotHaveIncludeChildren() 
      throws ParsingException, IOException, XIncludeException {
        File input = new File(inputDir, "nestedxinclude.xml");
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
        File input = new File(inputDir, "nestedxincludenamespace.xml");
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
        File input = new File(inputDir, "nakedfallback.xml");
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
        File input = new File(inputDir, "fallbackcontainsfallback.xml");
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
        File input = new File(inputDir, "multiplefallbacks.xml");
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
        File input = new File(inputDir, "multiplefallbacks2.xml");
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
        File input = new File(inputDir, "circle1.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("allowed parsed include of self");
        }
        catch (InclusionLoopException success) {
            assertNotNull(success.getMessage());
            assertEquals(input.toURI().toString(), success.getURI());           
        }
    }

    
    public void testInclusionLoopWithLength2Cycle() 
      throws ParsingException, IOException, XIncludeException {
        
        File input = new File(inputDir, "circle2a.xml");
        File errorFile = new File(inputDir, "circle2b.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("allowed circular include, cycle length 1");
        }
        catch (InclusionLoopException success) {
            assertTrue(success.getMessage().indexOf(errorFile.toURI().toString()) > 1);           
            assertTrue(success.getMessage().indexOf(input.toURI().toString()) > 1);           
            assertEquals(errorFile.toURI().toString(), success.getURI());           
        }
        
    }
    
    
    public void testMissingHref() 
      throws ParsingException, IOException, XIncludeException {
        
        File input = new File(inputDir, "missinghref.xml");
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
      throws ParsingException, IOException, XIncludeException, URISyntaxException {
        
        File input = new File(inputDir, "badparseattribute.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("allowed bad parse attribute");
        }
        catch (BadParseAttributeException success) {
            assertNotNull(success.getMessage());
            URI u1 = input.toURI();
            URI u2 = new URI(success.getURI());
            assertEquals(u1, u2);
        }
        
    }
    
    
    public void testUnavailableResource() 
      throws ParsingException, IOException, XIncludeException {
        File input = new File(inputDir, "missingfile.xml");
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
        
        File input = new File(inputDir, "fallbacktest.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "fallbacktest.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testFallbackWithRecursiveInclude() 
      throws ParsingException, IOException, XIncludeException {
        
        File input = new File(inputDir, "fallbacktest2.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "fallbacktest2.xml")
        );
        assertEquals(expectedResult, result);
        
    }

    
    public void testEncodingAttribute() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "utf16.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "utf16.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testXPointerBareNameID() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "xptridtest.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "xptridtest.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testXPointerXMLID() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "xmlidtest.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "xmlidtest.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testShorthandXPointerMatchesNothing() 
      throws ParsingException, IOException, URISyntaxException {
      
        File input = new File(inputDir, "xptridtest2.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("Resolved a document with an XPointer " +
              "that selects no subresource");
        }
        catch (XIncludeException success) {
            assertNotNull(success.getMessage());
            // Must compare URLs instead of strings here to avoid 
            // issues of whether a file URL begins file:/ or file:///
            URI u1 = input.toURI();
            URI u2 = new URI(success.getURI());
            assertEquals(u1, u2);  
        }
        
        
        /* I used to think this case included nothing.
           Now I think an XPointer that matches no
           subresource, and does not have a fallback is in error.
        Document expectedResult = builder.build(
          new File(outputDir, "xptridtest2.xml")
        );
        assertEquals(expectedResult, result);
        */
        
    }
    
    
    public void testXPointerPureTumbler() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "xptrtumblertest.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "xptrtumblertest.xml")
        );
        assertEquals(expectedResult, result);
        
    }

    
    public void testUnrecognizedColonizedSchemeNameBackedUpByTumbler() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "colonizedschemename.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "xptrtumblertest.xml")
        );
        assertEquals(expectedResult, result);
        
    }

    
    public void testXPointerSyntaxErrorInSecondPart() 
      throws ParsingException, IOException {
      
        File input = new File(inputDir, "laterfailure.xml");
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
    
    
    public void testBadElementSchemeDataIsNotAnError() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "badelementschemedata.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "badelementschemedata.xml")
        );
        // dumpResult(input, result); 
        
        assertEquals(expectedResult, result);
        
    }

    
    public void testXPointerSyntaxErrorMissingFinalParenthesis() 
      throws ParsingException, IOException {
      
        File input = new File(inputDir, "laterfailure2.xml");
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
      
        File input = new File(inputDir, "onlyxpointer.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "onlyxpointer.xml")
        );
        
        assertEquals(expectedResult, result);
        
    }


    // Test with 3 element schemes in the XPointer.
    // The first and second one point to nothing. The third one
    // selects something.
    public void testXPointerTripleTumbler() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "xptr2tumblertest.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "xptrtumblertest.xml")
        );
        assertEquals(expectedResult, result);
        
    }

    
    // Test with 2 element schemes in the XPointer.
    // The first one uses an ID that doesn't exist 
    // and points to nothing. The second one
    // selects something.
    public void testXPointerDoubleTumbler() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "xptrdoubletumblertest.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "xptrtumblertest.xml")
        );
        assertEquals(expectedResult, result);
        
    }


    // Test with 2 element schemes in the XPointer.
    // The first one uses an ID that points to something. 
    // The second one points to something too. Both element schemes
    // use IDs exclusively, no child sequences.
    public void testXPointerDoubleElementByID() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "xptrdoubleelementtest.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "xptrdoubleelementtest.xml")
        );
        assertEquals(expectedResult, result);
        
    }


    // Test with 2 element schemes in the XPointer.
    // The first one uses a child sequence that points to something. 
    // The second one points to something too. Both element schemes
    // use child sequences exclusively, no IDs.
    public void testXPointerDoubleElementByChildSequence() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "xptrdoublechildsequence.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "xptrdoubleelementtest.xml")
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
          new File(outputDir, "xptrtumblertest.xml")
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
          new File(outputDir, "xptrtumblertest.xml")
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
          = new File(inputDir, "xptrtumblertest3.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "xptrtumblertest.xml")
        );
        assertEquals(expectedResult, result);
        
    }

    
    public void testXPointerTumblerMatchesNothing() 
      throws ParsingException, IOException, URISyntaxException {
      
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
            URI u1 = input.toURI();
            URI u2 = new URI(success.getURI());
            assertEquals(u1, u2);              
        }
        
    }
    
    
    public void testMalformedXPointer() 
      throws ParsingException, IOException, URISyntaxException {   
        
        File input = new File(inputDir, "badxptr.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("Allowed malformed XPointer");
        }
        catch (XIncludeException success) {
            assertNotNull(success.getMessage());
            URI u1 = input.toURI();
            URI u2 = new URI(success.getURI());
            assertEquals(u1, u2);            
        }
        
    }
    
    
    public void testXPointerExceptionSelfCausation() 
      throws ParsingException, IOException {   
        
        File input = new File(inputDir, "badxptr.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("Allowed malformed XPointer");
        }
        catch (XIncludeException success) {
            Exception cause = (Exception) success.getCause();
            assertNotNull(cause.getMessage());
            try {
                cause.initCause(cause);
                fail("Self causation");
            }
            catch (IllegalArgumentException ex) {
                assertNotNull(ex.getMessage());
            }
        }
        
    }
    
    
    public void testXPointerExceptionGetCause() 
      throws ParsingException, IOException {   
        
        File input = new File(inputDir, "badxptr.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("Allowed malformed XPointer");
        }
        catch (XIncludeException success) {
            Exception cause = (Exception) success.getCause();
            Exception ex = new Exception();
            cause.initCause(ex);
            assertEquals(ex, cause.getCause());
        }
        
    }
    
    
    public void testAnotherMalformedXPointer() 
      throws ParsingException, IOException, URISyntaxException {
        
        // testing use of non NCNAME as ID
        File input = new File(inputDir, "badxptr2.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("Allowed another malformed XPointer");
        }
        catch (XIncludeException success) {
            assertNotNull(success.getMessage());
            URI u1 = input.toURI();
            URI u2 = new URI(success.getURI());
            assertEquals(u1, u2);          
        }
        
    }
    
    
    public void testMalformedXPointerWithFallback() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "xptrfallback.xml");
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
          new File(outputDir, "UTF8WithByteOrderMark.xml")
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
          new File(outputDir, "UnicodeBigUnmarked.xml")
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
          new File(outputDir, "UnicodeLittleUnmarked.xml")
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
          new File(outputDir, "UTF32BE.xml")
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
          new File(outputDir, "UTF32LE.xml")
        );
        assertEquals(expectedResult, result);
                
    }
*/
    
    
    public void testEBCDIC() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "EBCDIC.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expected = builder.build(new File(outputDir, "EBCDIC.xml"));
        assertEquals(expected, result);
                
    }
 

  // Turn off these tests because Java doesn't support UCS4 yet
 /*   public void testAutoDetectUCS4BE() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "UCS4BE.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "UTF8WithByteOrderMark.xml")
        );
        assertEquals(expectedResult, result);
                
    }

    public void testAutoDetectUCS4LE() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "UCS4LE.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "UTF8WithByteOrderMark.xml")
        );
        assertEquals(expectedResult, result);
                
    } */
    
    
    // Need a test case where A includes B, B includes C
    // and B encounters the error (e.g. a missing href)
    // to make sure B's URL is in the error message, not A's
    public void testChildDocumentSetsErrorURI() 
      throws ParsingException, IOException, XIncludeException, URISyntaxException {
      
        File input = new File(inputDir, "toplevel.xml");
        File error = new File(inputDir, "onedown.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("Missing HREF not detected");
        }
        catch (NoIncludeLocationException success) {
            assertNotNull(success.getMessage());
            URI u1 = error.toURI();
            URI u2 = new URI(success.getURI());
            assertEquals(u1, u2);           
        }
                
    } 

    
    public void testColonizedNameForIdValueInElementScheme() 
      throws ParsingException, IOException {
      
        File input = new File(inputDir, "badxptr3.xml");
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
      
        File input = new File(inputDir, "badxptr4.xml");
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
      
        File input = new File(inputDir, "circlepointer1.xml");
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
      
        File input = new File(inputDir, "xpointeroverridesfragmentid.xml"
        );
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("Allowed href attribute with fragment ID");
        }
        catch (XIncludeException success) {
            assertNotNull(success.getMessage());
        }
                
    }
    
 
    public void testFailsOnFragmentID() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "ignoresfragmentid.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("Allowed href attribute with fragment ID");
        }
        catch (XIncludeException success) {
            assertNotNull(success.getMessage());
        }
                
    }
    
 
    // This also tests that the base URI applied to an element is as set by the xml:base
    // attribute, not the document.
    public void testFragmentIDsAreRemovedFromElementBaseURIsAfterInclusion() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "basewithfragmentid.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "basewithfragmentid.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testIncludeLowerCaseFileNames() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "lowercasealphabet.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "lowercasealphabet.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testIncludeUpperCaseFileNames() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "uppercasealphabet.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "uppercasealphabet.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testIncludeDigitFileNames() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "numeric.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "numeric.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testIncludeHighPunctuationFileNames() 
      throws ParsingException, IOException, XIncludeException {
      
        // Windows has a problem with some of these file names so
        // first we have to generate the file, just to avoid storing
        // it in the zip archive
        try {
            File f = new File(inputDir, "{|}.txt");
            Writer out = new OutputStreamWriter(
              new FileOutputStream(f), "UTF8");
            out.write("{|}");
            out.flush();
            out.close();
            
            File input = new File(inputDir, "punctuation.xml");
            Document doc = builder.build(input);
            Document result = XIncluder.resolve(doc);
            Document expectedResult = builder.build(
              new File(outputDir, "punctuation.xml")
            );
            f.delete();
            assertEquals(expectedResult, result);
        }
        catch (FileNotFoundException ex) {
            // This file can't even exist on Windows.
            // We can only test this on Unix. 
            if (!windows) throw ex;
        }
        
    }
    
    
    public void testMiddlePunctuationError() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "middlepunctuationerror.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("Allowed illegal IRI with right square bracket ]");
        }
        catch (BadHrefAttributeException success) {
            assertNotNull(success.getMessage());
        }
        
    }
    
    
    public void testIncludeLowerPunctuationFileNames() 
      throws ParsingException, IOException, XIncludeException {
      
        try {
            File f = new File(inputDir, "!\"$&'+,.txt");
            Writer out = new OutputStreamWriter(
              new FileOutputStream(f), "UTF8");
            out.write("!\"$&'+,");
            out.flush();
            out.close();

            File input = new File(inputDir, "lowerpunctuation.xml");
            Document doc = builder.build(input);
            Document result = XIncluder.resolve(doc);
            Document expectedResult = builder.build(
              new File(outputDir, "lowerpunctuation.xml")
            );
            f.delete();
            assertEquals(expectedResult, result);
        }
        catch (FileNotFoundException ex) {
            // This file can't even exist on Windows.
            // We can only test this on Unix. 
            if (!windows) throw ex;
        }
        
    }
    
    
    public void testLineEnds() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "lineends.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expected = builder.build(
          new File(outputDir, "lineends.xml")
        );
        assertEquals(expected, result);
                
    }
    
 
    // This is semantically bad; but still meets the
    // syntax of fragment IDs from RFC 2396
    public void testBadXPointerInFragmentIDIsFatalError() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(
          "data/xinclude/input/meaninglessfragmentid.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("Allowed href attribute with fragment ID");
        }
        catch (XIncludeException success) {
            assertNotNull(success.getMessage());
        }
                
    }
    
    
    // These tests actually connect to IBiblio to load the included
    // data. This is necessary because file URLs don't support
    // content negotiation
    public void testAcceptLanguageFrench() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "acceptfrench.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "acceptfrench.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testAcceptLanguageEnglish() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "acceptenglish.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "acceptenglish.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testAcceptPlainText() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "acceptplaintext.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "acceptplaintext.xml")
        );
        assertEquals(expectedResult, result);
        
    }
    
    
    public void testAcceptHTML() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "accepthtml.xml");
        Document doc = builder.build(input);
        Document result = XIncluder.resolve(doc);
        Document expectedResult = builder.build(
          new File(outputDir, "accepthtml.xml")
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
 
    
    public void testPercentEscapesAreNotAllowedInXPointerAttributes() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "xpointerwithpercentescape.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("Allowed xpointer attribute with percent escape");
        }
        catch (XIncludeException success) {
            assertNotNull(success.getMessage());
            Exception cause = (Exception) success.getCause();
            assertNotNull(cause);
        }
                
    }
     
    public void testXPointerExceptionReinitializeCause() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "xpointerwithpercentescape.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("Allowed xpointer attribute with percent escape");
        }
        catch (XIncludeException success) {
            Exception cause = (Exception) success.getCause();
            try {
                cause.initCause(new Exception());
                fail("Reinitialized cause");
            }
            catch (IllegalStateException ex) {
                assertNotNull(ex.getMessage());
            }
        }
                
    }
    
    
    
    // WARNING: this test is one interpretation of the XInclude 
    // proposed recommendation. It asserts that encoding attributes 
    // that do not contain legal encoding names are fatal errors.
    // This is far from certain. It is also possible the working group
    // will choose to interpret these as resource errors. 
    public void testMalformedEncodingAttribute() 
      throws IOException, ParsingException, XIncludeException {
      
        File input = new File(inputDir, "badencoding.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("Allowed encoding attribute with white space");
        }
        catch (BadEncodingAttributeException success) {
            assertNotNull(success.getMessage());
            assertTrue(success.getURI().endsWith(input.getName()));
        }
                
    }
        
    
    public void testEmptyEncodingAttribute() 
      throws IOException, ParsingException, XIncludeException {
      
        File input = new File(inputDir, "emptyencoding.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("Allowed encoding attribute with no value");
        }
        catch (BadEncodingAttributeException success) {
            assertNotNull(success.getMessage());
            assertTrue(success.getURI().endsWith(input.getName()));
        }
                
    }
        
    
    public void testEncodingAttributeStartsWithDigit() 
      throws IOException, ParsingException, XIncludeException {
      
        File input = new File(inputDir, "digitencoding.xml");
        Document doc = builder.build(input);
        try {
            XIncluder.resolve(doc);
            fail("Allowed encoding attribute starting with digit");
        }
        catch (BadEncodingAttributeException success) {
            assertNotNull(success.getMessage());
            assertTrue(success.getURI().endsWith(input.getName()));
        }
                
    }
        
    
    // Test that a malformed parse attribute is not thrown when the
    // fallback element containing it is not activated.
    public void testHiddenError() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "hiddenerror.xml");
        Document doc = builder.build(input);
        XIncluder.resolve(doc);
                
    }
        

    // Test that an href attribute that has a fragment identifier
    // is not a fatal error when the fallback element containing 
    // it is not activated.
    public void testHiddenError2() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "hiddenerror2.xml");
        Document doc = builder.build(input);
        XIncluder.resolve(doc);
                
    }

    
    // Test that a fallback element with a non-include parent is not a
    // fatal error when the ancestor fallback element containing it is
    // not activated.
    public void testHiddenError3() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "hiddenerror3.xml");
        Document doc = builder.build(input);
        XIncluder.resolve(doc);
                
    }

   
    // Test that an xpointer attribute that uses percent escapes 
    // is a not a fatal error when the 
    // fallback element containing it is not activated. See
    // http://lists.w3.org/Archives/Public/www-xml-xinclude-comments/2004Oct/0008.html
    public void testXpointerAttributeContainsPercentEscapeInUnactivatedFallback() 
      throws ParsingException, IOException, XIncludeException {
      
        File input = new File(inputDir, "hiddenerror3.xml");
        Document doc = builder.build(input);
        XIncluder.resolve(doc);
                
    }
    
    
}