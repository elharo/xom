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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.UnknownHostException;
import java.util.MissingResourceException;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Comment;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.IllegalAddException;
import nu.xom.MalformedURIException;
import nu.xom.NamespaceConflictException;
import nu.xom.NodeFactory;
import nu.xom.Nodes;
import nu.xom.ParentNode;
import nu.xom.ParsingException;
import nu.xom.ProcessingInstruction;
import nu.xom.Serializer;
import nu.xom.Text;
import nu.xom.ValidityException;
import nu.xom.XMLException;
import nu.xom.xslt.XSLException;
import nu.xom.xslt.XSLTransform;

/**
 * <p>
 *  Unit tests for the XSLT engine.
 * </p>
 * 
 * <p>
 * Many of the tests in this suite use an identity transformation.
 * This is often done to make sure I get a particular content into
 * the output tree in order to test the XSLTHandler.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0b1
 *
 */
public class XSLTransformTest extends XOMTestCase {

    
    public XSLTransformTest(String name) {
        super(name);   
    }

    
    // not a literal result element as stylesheet 
    // because it's missing the xsl:version attribute
    private String notAStyleSheet = 
     "<?xml-stylesheet href=\"file.css\" type=\"text/css\"?>" 
     + "<!-- test -->"
     + "<test xmlns:xlink='http://www.w3.org/TR/1999/xlink'>Hello dear"
     + "\r\n<em id=\"p1\" xmlns:none=\"http://www.example.com\">"
     + "very important</em>"
     + "<span xlink:type='simple'>here&apos;s the link</span>\r\n"
     + "<svg:svg xmlns:svg='http://www.w3.org/TR/2000/svg'>"
     + "<svg:text>text in a namespace</svg:text></svg:svg>\r\n"
     + "<svg xmlns='http://www.w3.org/TR/2000/svg'>"
     + "<text>text in a namespace</text></svg>"
     + "</test>\r\n"
     + "<!--epilog-->";

    
    /* public void testAttrDup() throws ParsingException, IOException, XSLException {
        
        Builder builder = new Builder();
        Document inputDoc = builder.build(new File("data/oasis-xslt-testsuite/TESTS/MSFT_Conformance_Tests/BVTs/data.xml"));
        Document styleDoc = builder.build(new File("data/oasis-xslt-testsuite/TESTS/MSFT_Conformance_Tests/BVTs/attr-dup.xsl"));
        XSLTransform xform = new XSLTransform(styleDoc);
        Nodes result = xform.transform(inputDoc);
        
    } */   
    
    // primarily this makes sure the XSLTHandler can handle various
    // edge cases
    public void testIdentityTransform() throws XSLException {
        
        File stylesheet = new File("data/xslt/input/identity.xsl");
        XSLTransform xform = new XSLTransform(stylesheet);
        Element root = new Element("root", "http://www.example.org");
        root.appendChild(new Text("some data"));
        root.appendChild(new Element("something"));
        root.addAttribute(new Attribute("test", "test"));
        root.addAttribute(new Attribute("pre:red", "http://www.red.com/", "value"));
        Document input = new Document(root);
        Nodes output = xform.transform(input);
        assertEquals(root, output.get(0));
        
    }
    
    public void testPrefixMappingIssues() 
      throws XSLException, ParsingException, IOException {
        
        String doc = "<test>"
           + "<span xmlns:a='http://www.example.com'/>"
           + "<span xmlns:b='http://www.example.net'/>"
           + "</test>"; 
        File stylesheet = new File("data/xslt/input/identity.xsl");
        XSLTransform xform = new XSLTransform(stylesheet);
        
        Builder builder = new Builder();
        Document input = builder.build(doc, "http://example.org/");
        Nodes result = xform.transform(input);
        assertEquals(input.getRootElement(), result.get(0));
        
    }
    
    
    public void testReaderConstructor() {
        
        try {
            new XSLTransform(new StringReader(notAStyleSheet));
            fail("Compiled non-stylesheet");
        }
        catch (XSLException success) {
            assertNotNull(success.getMessage());
        }
        
    }

    
    public void testInputStreamConstructor() throws IOException {
        
        try {
            byte[] data = notAStyleSheet.getBytes("UTF-8");
            new XSLTransform(new ByteArrayInputStream(data));
            fail("Compiled non-stylesheet");
        }
        catch (XSLException success) {
            assertNotNull(success.getMessage());
        }
        
    }

    
    public void testDocumentConstructor() 
      throws ParsingException, IOException {
        
        try {
            Builder builder = new Builder();
            Document doc = builder.build(notAStyleSheet, 
              "http://www.example.com");
            new XSLTransform(doc);
            fail("Compiled non-stylesheet");
        }
        catch (XSLException success) { 
            assertNotNull(success.getMessage());
        }
        
    }

    
    public void testFileConstructor() {
        
        try {
            File f = new File("data/schematest.xml");
            new XSLTransform(f);
            fail("Compiled non-stylesheet");
        }
        catch (XSLException success) {  
            assertNotNull(success.getMessage());
        }
        
    }

    
    public void testURLConstructor() throws IOException {
        
        try {
            File f = new File("data/schematest.xml");
            new XSLTransform(f.toURL().toExternalForm());
            fail("Compiled non-stylesheet");
        }
        catch (XSLException success) {  
            assertNotNull(success.getMessage());
        }
        
    }


/*   <xsl:template match="/">
    <element1>some data and <content/> for a test</element1>
    <element2>Remember, the XSLT processor is going to strip out the literal white space</element2>
    <element3>some data and <content/> for a <!--test--></element3>
    <element4/>
    <xsl:comment>test</xsl:comment>
    <xsl:processing-instruction name="test">PIs are not treated as literals in XSLT?</xsl:processing-instruction>
  </xsl:template> */
  
    public void testCreateDocumentFragment() 
      throws ParsingException, IOException, XSLException {
        
        Element element1 = new Element("element1");
        element1.appendChild("some data and ");
        element1.appendChild(new Element("content"));
        element1.appendChild(" for a test");
        
        Element element2 = new Element("element2");
        element2.appendChild(
          "Remember, the XSLT processor is going to strip out the literal white space"
        );
        File doc = new File("data/xslt/input/8-14.xml");
        File stylesheet = new File("data/xslt/input/fragment.xsl");
        Builder builder = new Builder();
        XSLTransform xform = new XSLTransform(stylesheet);
        Document input = builder.build(doc);
        Nodes output = xform.transform(input);
        assertEquals(6, output.size());
        assertEquals(element1, output.get(0));
        assertEquals(element2, output.get(1));
        assertEquals(new Element("element4"), output.get(3));
        assertEquals(new Comment("test"), output.get(4));
        assertEquals(new ProcessingInstruction("test", 
          "PIs are not treated as literals in XSLT?"), output.get(5));
        
    }

    
    public void testTransform() 
      throws ParsingException, IOException, XSLException {
        
        File doc = new File("data/xslt/input/8-1.xml");
        File stylesheet = new File("data/xslt/input/8-8.xsl");
        Builder builder = new Builder();
        XSLTransform xform = new XSLTransform(stylesheet);
        Nodes output = xform.transform(builder.build(doc));
        assertEquals(1, output.size());
        Document result = new Document((Element) (output.get(0)));

        Document expected = builder.build("data/xslt/output/8-8.xml");
        assertEquals(expected, result);
        
    }

    
    public void testTransformWithCFilter() 
      throws ParsingException, IOException, XSLException {
        
        File doc = new File("data/xslt/input/8-1.xml");
        File stylesheet = new File("data/xslt/input/8-8.xsl");
        Builder builder = new Builder();
        XSLTransform xform = new XSLTransform(stylesheet);
        xform.setNodeFactory(new NodeFactoryTest.CFactory());
        
        Nodes output = xform.transform(builder.build(doc));
        assertEquals(1, output.size());
        Document result = new Document((Element) (output.get(0)));

        Document expected = builder.build("data/xslt/output/8-8c.xml");
        assertEquals(expected, result);
        
    }

    
    public void testCreateDocumentFragmentWithCommentFilter() 
      throws ParsingException, IOException, XSLException {
        
        Element element1 = new Element("element1");
        element1.appendChild("some data and ");
        element1.appendChild(new Element("content"));
        element1.appendChild(" for a test");
        
        Element element2 = new Element("element2");
        element2.appendChild(
          "Remember, the XSLT processor is going to strip out the literal white space"
        );
        File doc = new File("data/xslt/input/8-14.xml");
        File stylesheet = new File("data/xslt/input/fragment.xsl");
        Builder builder = new Builder();
        XSLTransform xform = new XSLTransform(stylesheet);
        xform.setNodeFactory(new NodeFactoryTest.CommentFilter());
        
        Document input = builder.build(doc);
        Nodes output = xform.transform(input);
        assertEquals(5, output.size());
        assertEquals(element1, output.get(0));
        assertEquals(element2, output.get(1));
        assertEquals(new Element("element4"), output.get(3));
        assertEquals(new ProcessingInstruction("test", 
          "PIs are not treated as literals in XSLT?"), output.get(4));
        
    }    
    
    
    public void testCreateDocumentFragmentWithProcessingInstructionFilter() 
      throws ParsingException, IOException, XSLException {
        
        Element element1 = new Element("element1");
        element1.appendChild("some data and ");
        element1.appendChild(new Element("content"));
        element1.appendChild(" for a test");
        
        Element element2 = new Element("element2");
        element2.appendChild(
          "Remember, the XSLT processor is going to strip out the literal white space"
        );
        File doc = new File("data/xslt/input/8-14.xml");
        File stylesheet = new File("data/xslt/input/fragment.xsl");
        Builder builder = new Builder();
        XSLTransform xform = new XSLTransform(stylesheet);
        xform.setNodeFactory(new NodeFactoryTest.ProcessingInstructionFilter());
        
        Document input = builder.build(doc);
        Nodes output = xform.transform(input);
        assertEquals(5, output.size());
        assertEquals(element1, output.get(0));
        assertEquals(element2, output.get(1));
        assertEquals(new Element("element4"), output.get(3));
        assertEquals(new Comment("test"), output.get(4));
        
    }    
    
    
    public void testCreateDocumentFragmentWithUncommentFilter() 
      throws ParsingException, IOException, XSLException {
        
        Element element1 = new Element("element1");
        element1.appendChild("some data and ");
        element1.appendChild(new Element("content"));
        element1.appendChild(" for a test");
        
        Element element2 = new Element("element2");
        element2.appendChild(
          "Remember, the XSLT processor is going to strip out the literal white space"
        );
        File doc = new File("data/xslt/input/8-14.xml");
        File stylesheet = new File("data/xslt/input/fragment.xsl");
        Builder builder = new Builder();
        XSLTransform xform = new XSLTransform(stylesheet);
        xform.setNodeFactory(new NodeFactoryTest.UncommentFilter());
        
        Document input = builder.build(doc);
        Nodes output = xform.transform(input);
        assertEquals(6, output.size());
        assertEquals(element1, output.get(0));
        assertEquals(element2, output.get(1));
        assertEquals(new Element("element4"), output.get(3));
        assertEquals(new Text("test"), output.get(4));
        assertEquals(new ProcessingInstruction("test", 
          "PIs are not treated as literals in XSLT?"), output.get(5));
        
    }    
    
    
    public void testTransform2() 
      throws ParsingException, IOException, XSLException {
        
        File doc = new File("data/xslt/input/8-1.xml");
        File stylesheet = new File("data/xslt/input/8-12.xsl");
        Builder builder = new Builder();
        XSLTransform xform = new XSLTransform(stylesheet);
        Nodes output = xform.transform(builder.build(doc));
        assertEquals(1, output.size());
        Document result = new Document((Element) (output.get(0)));

        Document expected = builder.build("data/xslt/output/8-12.xml");
        assertEquals(expected, result);
        
    }

    
    public void testTransformFromInputStream() 
      throws ParsingException, IOException, XSLException {
        
        File doc = new File("data/xslt/input/8-1.xml");
        InputStream stylesheet = new FileInputStream("data/xslt/input/8-12.xsl");
        Builder builder = new Builder();
        XSLTransform xform = new XSLTransform(stylesheet);
        Nodes output = xform.transform(builder.build(doc));
        assertEquals(1, output.size());
        Document result = new Document((Element) (output.get(0)));
        
        Document expected = builder.build("data/xslt/output/8-12.xml");
        assertEquals(expected, result);
        
    }

    
    public void testTransformFromReader() 
      throws ParsingException, IOException, XSLException {
        
        File doc = new File("data/xslt/input/8-1.xml");
        Reader stylesheet = new FileReader("data/xslt/input/8-12.xsl");
        Builder builder = new Builder();
        XSLTransform xform = new XSLTransform(stylesheet);
        Nodes output = xform.transform(builder.build(doc));
        assertEquals(1, output.size());
        Document result = new Document((Element) (output.get(0)));
        Document expected = builder.build("data/xslt/output/8-12.xml");
        assertEquals(expected, result);
        
    }

    
    // For debugging
    private static void dumpResult(Document result, String filename) 
      throws IOException {
        
        File debug = new File("data/xslt/debug/" + filename);
        OutputStream out = new FileOutputStream(debug);
        Serializer serializer = new Serializer(out);
        serializer.write(result);
        serializer.flush();
        out.close();
        
    }

    
    public void testTransformFromDocument() 
      throws ParsingException, IOException, XSLException {
        
        File doc = new File("data/xslt/input/8-1.xml");
        Builder builder = new Builder();
        Document stylesheet = builder.build("data/xslt/input/8-12.xsl");
        XSLTransform xform = new XSLTransform(stylesheet);
        Nodes output = xform.transform(builder.build(doc));
        assertEquals(1, output.size());
        Document result = new Document((Element) (output.get(0)));

        Document expected = builder.build("data/xslt/output/8-12.xml");
        assertEquals(expected, result);
        
    }

    
    public void testTransformFromSystemID() 
      throws ParsingException, IOException, XSLException {
        
        File doc = new File("data/xslt/input/8-1.xml");
        Builder builder = new Builder();
        String stylesheet = "data/xslt/input/8-12.xsl";
        XSLTransform xform = new XSLTransform(stylesheet);
        Nodes output = xform.transform(builder.build(doc));
        assertEquals(1, output.size());
        Document result = new Document((Element) (output.get(0)));

        Document expected = builder.build("data/xslt/output/8-12.xml");
        assertEquals(expected, result);
        
    }


    public void testTransformWithNamespaces() 
      throws ParsingException, IOException, XSLException {
        
        File doc = new File("data/xslt/input/8-14.xml");
        File stylesheet = new File("data/xslt/input/8-15.xsl");
        Builder builder = new Builder();
        XSLTransform xform = new XSLTransform(stylesheet);
        Document input = builder.build(doc);
        Nodes output = xform.transform(input);
        assertEquals(1, output.size());
        Document result = new Document((Element) (output.get(0)));

        Document expected = builder.build("data/xslt/output/8-15.xml");
        assertEquals(expected, result);
        
    }

    
    public void testSingleTextNode() 
      throws ParsingException, IOException, XSLException {
        
        File doc = new File("data/xslt/input/8-14.xml");
        File stylesheet = new File("data/xslt/input/singlestring.xsl");
        Builder builder = new Builder();
        XSLTransform xform = new XSLTransform(stylesheet);
        Document input = builder.build(doc);
        Nodes output = xform.transform(input);
        assertEquals(1, output.size());
        Text data = (Text) (output.get(0));
        assertEquals("Data", data.getValue());
        
    }
    
    public void testToString() throws XSLException {
        
        File stylesheet = new File("data/xslt/input/singlestring.xsl");
        XSLTransform xform = new XSLTransform(stylesheet);
        assertTrue(xform.toString().startsWith("[nu.xom.xslt.XSLTransform: "));
        
    }    
    

    // Make sure that method="text" doesn't affect what we get
    // since this is not a serialized transform
    // See http://nagoya.apache.org/bugzilla/show_bug.cgi?id=30197
    public void testTextMethod() 
      throws ParsingException, IOException, XSLException {
        
        File doc = new File("data/xslt/input/8-14.xml");
        File stylesheet = new File("data/xslt/input/textmethod.xsl");
        Builder builder = new Builder(); 
        XSLTransform xform = new XSLTransform(stylesheet);
        Document input = builder.build(doc);
        Nodes output = xform.transform(input);
        assertEquals(6, output.size());
        assertEquals("12345", output.get(0).getValue());
        assertEquals("67890", output.get(1).getValue());
        assertEquals("", output.get(2).getValue());
        assertEquals("0987654321", output.get(3).getValue());
        assertTrue(output.get(4) instanceof Comment);
        assertTrue(output.get(5) instanceof ProcessingInstruction);
        
    }

    
    public void testCommentWithParent() throws XSLException {
        
        File stylesheet = new File("data/xslt/input/commentwithparent.xsl");
        XSLTransform xform = new XSLTransform(stylesheet);
        Document input = new Document(new Element("root"));
        Nodes output = xform.transform(input);
        assertEquals(1, output.size());
        assertEquals("", output.get(0).getValue());
        Element root = (Element) output.get(0);
        assertEquals(1, root.getChildCount());
        Comment child = (Comment) root.getChild(0);
        assertEquals("test", child.getValue());
        
    }

    
    public void testProcessingInstructionWithParent() 
      throws XSLException {
        
        File stylesheet = new File("data/xslt/input/piwithparent.xsl");
        XSLTransform xform = new XSLTransform(stylesheet);
        Document input = new Document(new Element("root"));
        Nodes output = xform.transform(input);
        assertEquals(1, output.size());
        assertEquals("", output.get(0).getValue());
        Element root = (Element) output.get(0);
        assertEquals(1, root.getChildCount());
        ProcessingInstruction child = (ProcessingInstruction) root.getChild(0);
        assertEquals("target", child.getTarget());
        assertEquals("test", child.getValue());
        
    } 

    
    public void testTransformNodes() throws XSLException {
        
        File stylesheet = new File("data/xslt/input/piwithparent.xsl");
        XSLTransform xform = new XSLTransform(stylesheet);
        Nodes input = new Nodes(new Element("root"));
        Nodes output = xform.transform(input);
        assertEquals(1, output.size());
        assertEquals("", output.get(0).getValue());
        Element root = (Element) output.get(0);
        assertEquals(1, root.getChildCount());
        ProcessingInstruction child = (ProcessingInstruction) root.getChild(0);
        assertEquals("target", child.getTarget());
        assertEquals("test", child.getValue());
        
    } 
    
    
    public void testTriple() 
      throws IOException, ParsingException, XSLException {
        
        File stylesheet = new File("data/xslt/input/identity.xsl");
        XSLTransform xform = new XSLTransform(stylesheet);
        xform.setNodeFactory(new NodeFactoryTest.TripleElementFilter());

        String data = "<a><b><c/></b></a>";
        Builder builder = new Builder();
        Document doc = builder.build(data, "http://www.example.org/");
        
        Nodes result = xform.transform(doc);
        
        assertEquals(3, result.size()); 
        assertEquals(result.get(0), result.get(1));
        assertEquals(result.get(1), result.get(2));
        Element a = (Element) result.get(2);
        assertEquals("a", a.getLocalName());
        assertEquals(3, a.getChildCount());
        assertEquals(0, a.getAttributeCount());
        Element b = (Element) a.getChild(1);
        assertEquals(3, b.getChildCount());
        assertEquals("b", b.getLocalName());
        
    }
   
    
    public void testPassingNullSetsDefaultFactory() 
      throws IOException, ParsingException, XSLException {  
        File stylesheet = new File("data/xslt/input/identity.xsl");
        XSLTransform xform = new XSLTransform(stylesheet);
        xform.setNodeFactory(new NodeFactoryTest.TripleElementFilter());

        String data = "<a><b><c/></b></a>";
        Builder builder = new Builder();
        Document doc = builder.build(data, "http://www.example.org/");
        
        xform.setNodeFactory(null);        
        Nodes result = xform.transform(doc);
        
        assertEquals(1, result.size()); 
        Element a = (Element) result.get(0);
        assertEquals("a", a.getLocalName());
        assertEquals(1, a.getChildCount());
        assertEquals(0, a.getAttributeCount());
        Element b = (Element) a.getChild(0);
        assertEquals(1, b.getChildCount());
        assertEquals("b", b.getLocalName());
        
    }
    
    
    public void testTransformEmptyNodesList() 
      throws IOException, ParsingException, XSLException {  
        File stylesheet = new File("data/xslt/input/identity.xsl");
        XSLTransform xform = new XSLTransform(stylesheet);
       
        Nodes result = xform.transform(new Nodes());
        
        assertEquals(0, result.size());
        
    }
    
    
    public void testMinimizingFactory() 
      throws XSLException, ParsingException, IOException {
        
        File stylesheet = new File("data/xslt/input/identity.xsl");
        XSLTransform xform = new XSLTransform(stylesheet);
        xform.setNodeFactory(new NodeFactoryTest.MinimizingFactory());
        
        Builder builder = new Builder();
        Document input = builder.build("<!-- test--><test>" +
                "<em>data</em>\r\n<psan>test</psan></test>" +
                "<?target data?>", "http://example.org/");
        Nodes output = xform.transform(input);
        assertEquals(0, output.size());
        
    } 
    
    
    public void testIllegalTransform() throws XSLException {
        
        File stylesheet = new File("data/xslt/input/illegaltransform.xsl");
        XSLTransform xform = new XSLTransform(stylesheet);
        Element root = new Element("root", "http://www.example.org");
        Document input = new Document(root);
        try {
            xform.transform(input);
            fail("Allowed illegal transform");
        }
        catch (XSLException ex) {
            assertNotNull(ex.getMessage());
        }
        
    }

    
    public void testRemapPrefixToSameURI() 
      throws IOException, ParsingException, XSLException { 
        
        File stylesheet = new File("data/xslt/input/identity.xsl");
        XSLTransform xform = new XSLTransform(stylesheet);

        String data = "<a xmlns:pre='http://www.example.org/'>" +
                "<b xmlns:pre='http://www.example.org/'>in B</b></a>";
        Builder builder = new Builder();
        Document doc = builder.build(data, "http://www.example.org/");
        
        Nodes result = xform.transform(doc);
        
        assertEquals(doc.getRootElement(), result.get(0));
        
    }
 
    
    public void testElementsToAttributes() 
      throws IOException, ParsingException, XSLException {  
        
        File stylesheet = new File("data/xslt/input/identity.xsl");
        XSLTransform xform = new XSLTransform(stylesheet);
        xform.setNodeFactory(new AttributeFactory());

        String data = "<a><b>in B<c>in C</c></b></a>";
        Builder builder = new Builder();
        Document doc = builder.build(data, "http://www.example.org/");
        
        Nodes result = xform.transform(doc);
        
        assertEquals(1, result.size());
        Element a = (Element) result.get(0);
        assertEquals("a", a.getLocalName());
        assertEquals(0, a.getChildCount());
        assertEquals(1, a.getAttributeCount());
        assertEquals("in B", a.getAttribute("b").getValue());
        
    }
 
    
    private static class AttributeFactory extends NodeFactory {

        public Nodes finishMakingElement(Element element) {
            ParentNode parent = element.getParent();
            if (parent == null || parent instanceof Document) {
                return new Nodes(element);
            }        
            return new Nodes(new Attribute(element.getQualifiedName(), 
                    element.getNamespaceURI(), element.getValue()));
        }     
        
    }
    

    public void testAttributesToElements() 
      throws IOException, ParsingException, XSLException {  
        
        File stylesheet = new File("data/xslt/input/identity.xsl");
        XSLTransform xform = new XSLTransform(stylesheet);
        xform.setNodeFactory(new AttributesToElements());

        String data = "<a name='value'><b x='y' a='b'/></a>";
        Builder builder = new Builder();
        Document doc = builder.build(data, "http://www.example.org/");
        
        Nodes result = xform.transform(doc);
        
        assertEquals(1, result.size());
        Element a = (Element) result.get(0);
        assertEquals("a", a.getLocalName());
        assertEquals(2, a.getChildCount());
        assertEquals(0, a.getAttributeCount());
        Element name = (Element) a.getChild(0);
        assertEquals("name", name.getLocalName());
        assertEquals("value", name.getValue());
        Element b = (Element) a.getChild(1);
        assertEquals("b", b.getLocalName());
        assertEquals(2, b.getChildCount());
        assertEquals("y", b.getFirstChildElement("x").getValue());
        assertEquals("b", b.getFirstChildElement("a").getValue());
        
    }
 
    
    private static class AttributesToElements extends NodeFactory {

        public Nodes makeAttribute(String name, String URI, 
          String value, Attribute.Type type) {
            Element element = new Element(name, URI);
            element.appendChild(value);
            return new Nodes(element);
        }   
        
    }


    public void testCommentToAttribute() 
      throws IOException, ParsingException, XSLException {
        
        File stylesheet = new File("data/xslt/input/identity.xsl");
        XSLTransform xform = new XSLTransform(stylesheet);
        xform.setNodeFactory(new NodeFactory() {
            public Nodes makeComment(String text) {
                return new Nodes(new Attribute("comment", text));   
            }
        });

        String data = "<a><!--test--></a>";
        Builder builder = new Builder();
        Document doc = builder.build(data, "http://www.example.org/");
        
        Nodes result = xform.transform(doc);
        
        assertEquals(1, result.size());
        Element a = (Element) result.get(0);
        assertEquals("a", a.getLocalName());
        assertEquals(0, a.getChildCount());
        assertEquals(1, a.getAttributeCount());
        Attribute comment = a.getAttribute(0);
        assertEquals("comment", comment.getLocalName());
        assertEquals("test", comment.getValue());
        
    }

    
    public void testAdditionalDefaultNamespace() 
      throws IOException, ParsingException, XSLException {  
        
        File stylesheet = new File("data/xslt/input/identity.xsl");
        XSLTransform xform = new XSLTransform(stylesheet);

        String data = "<pre:a xmlns:pre='http://www.example.org' " +
                "xmlns='http://www.example.net'>data</pre:a>";
        Builder builder = new Builder();
        Document doc = builder.build(data, "http://www.example.org/");
        
        Nodes result = xform.transform(doc);
        
        assertEquals(1, result.size());
        Element a = (Element) result.get(0);
        assertEquals("a", a.getLocalName());
        assertEquals("pre:a", a.getQualifiedName());
        assertEquals("data", a.getValue());
        assertEquals("http://www.example.org", a.getNamespaceURI("pre"));
        assertEquals("http://www.example.net", a.getNamespaceURI(""));
        assertEquals(2, a.getNamespaceDeclarationCount());
        
    }
    
  



    private static boolean indentYes(Document styleDoc) {
        
        Element output = styleDoc
          .getRootElement()
          .getFirstChildElement("output", 
             "http://www.w3.org/1999/XSL/Transform");
        if (output == null) return false;
        
        String indent = output.getAttributeValue("indent"); // trim????
        if ("yes".equals(indent)) return true;
        else return false;
        
    }


    public void testOASISXalanConformanceSuite()  
      throws IOException, ParsingException, XSLException {
        
        Builder builder = new Builder();
        File base = new File("data/oasis-xslt-testsuite/TESTS/Xalan_Conformance_Tests/");
        File catalog = new File(base, "catalog.xml");
        
        // The test suite need to be installed separately. If we can't
        // find the catalog, we just don't run these tests.
        if (catalog.exists()) {
            Document doc = builder.build(catalog);
            Element testsuite = doc.getRootElement();
            Elements submitters = testsuite.getChildElements("test-catalog");
            for (int i = 0; i < submitters.size(); i++) {
                Element submitter = submitters.get(i);
                Elements testcases = submitter.getChildElements("test-case");
                for (int j = 0; j < testcases.size(); j++) {
                    Element testcase = testcases.get(j);
                    String id = testcase.getAttributeValue("id");
                    if (id.startsWith("output_")) {
                        // These test cases are mostly about producing 
                        // HTML and plain text output that isn't 
                        // relevant to XOM
                        continue;
                    }
                    File root = new File(base, testcase.getFirstChildElement("file-path").getValue());
                    File input = null;
                    File style = null;
                    File output = null;
                    Element scenario = testcase.getFirstChildElement("scenario");
                    Elements inputs = scenario.getChildElements("input-file");
                    for (int k = 0; k < inputs.size(); k++) {
                        Element file = inputs.get(k);
                        String role = file.getAttributeValue("role");
                        if ("principal-data".equals(role)) {
                            input = new File(root, file.getValue());
                        }
                        else if ("principal-stylesheet".equals(role)) {
                            style = new File(root, file.getValue());
                        }
                    }
                    Elements outputs = scenario.getChildElements("output-file");
                    for (int k = 0; k < outputs.size(); k++) {
                        Element file = outputs.get(k);
                        String role = file.getAttributeValue("role");
                        if ("principal".equals(role)) {
                            // Fix up OASIS catalog bugs
                            File parent = new File(root.getParent());
                            parent = new File(parent, "REF_OUT");
                            parent = new File(parent, root.getName());
                            String outputFileName = file.getValue();
                            output = new File(parent, outputFileName);
                        }
                    }
                    
                    try {
                        Document inputDoc = builder.build(input);
                        Document styleDoc = builder.build(style);
                        // XXX For the moment let's just skip all tests
                        // that specify indent="yes" for the output;
                        // we can fix this with special comparison later.
                        if (indentYes(styleDoc)) continue;
                        XSLTransform xform = new XSLTransform(styleDoc);
                        Nodes result = xform.transform(inputDoc);
                        if (output == null) {
                            // transform should have failed
                            fail("Transformed " + testcase.getAttributeValue("id"));
                        }
                        else { 
                            try {
                                Document expectedResult = builder.build(output);
                                Document actualResult = XSLTransform.toDocument(result);
                                
                                if (id.equals("attribset_attribset40")) {
                                    // This test does not necessarily 
                                    // produce an identical infoset due
                                    // to necessary remapping of 
                                    // namespace prefixes.
                                    continue;
                                }
                                else if (id.equals("copy_copy56") 
                                  || id.equals("copy_copy58")
                                  || id.equals("copy_copy60")
                                  || id.equals("copy_copy59")) {
                                    // Xalan bug;
                                    // XXX diagnose and report
                                } 
                                else if (id.equals("idkey_idkey31")
                                  || id.equals("idkey_idkey59")
                                  || id.equals("idkey_idkey61")
                                  || id.equals("idkey_idkey62")) {
                                    // Xalan bug?;
                                    // XXX diagnose and report
                                } 
                                else if (id.equals("impincl_impincl11")) {
                                    // Xalan bug?;
                                    // XXX diagnose and report
                                }
                                else if (id.equals("math_math110")
                                  || id.equals("math_math111")) {
                                    // Xalan or test suite bug?;
                                    // XXX diagnose and report
                                }
                                else if (id.equals("position_position104")) {
                                    // test suite bug; Xalan is right,
                                    // sample output is incorrect; report????
                                }
                                else if (id.equals("position_position106")) {
                                    // test suite bug and xsltproc; Xalan is right,
                                    // sample output is incorrect; report for test suite????
                                    // already reported for libxslt
                                }
                                else if (id.equals("whitespace_whitespace17")
                                  || id.equals("position_position107")
                                  || id.equals("position_position109")) {
                                    // tests indent="yes" which is not
                                    // not relevant within XOM
                                } 
                                else {
                                    assertEquals("Problem with " + id,
                                      expectedResult, actualResult);
                                }
                            }
                            catch (ParsingException ex) {  
                                // a few of the test cases generate 
                                // text or HTML output rather than 
                                // well-formed XML. For the moment, I 
                                // just skip these; but could I compare
                                // the raw text to the value of the 
                                // document object instead????
                                continue;
                            }
                            catch (IllegalAddException ex) {
                                // A few of the test cases generate 
                                // incomplete documents so we can't
                                // compare output. Perhaps I could
                                // wrap in an element,t hen get children
                                // to build a Node object rather than a
                                // Document???? i.e. a fragment parser?
                                // Could use a SequenceInputStream to hack this
                            }
                            // XXX report issues in test suite with indent="yes" in stylesheets
                            // where indenting is not specifically being tested
                        }
                        
                    }
                    catch (MalformedURIException ex) {
                        // Some of the test cases contain relative 
                        // namespace URIs XOM does not support
                    }
                    catch (XSLException ex) {
                        // If the output was null the transformation 
                        // was expected to fail
                        if (output != null) {
                            // a few of the test cases use relative namespace URIs
                            // XOM doesn't support
                            Throwable cause = ex.getCause();
                            if (cause instanceof MalformedURIException) {
                                continue;
                            }
                            
                            if ("axes_axes62".equals(id)) {  
                                // Bug 12690
                                // http://nagoya.apache.org/bugzilla/show_bug.cgi?id=12690
                                continue;
                            }
                            else if ("impincl_impincl27".equals(id)) {  
                                // Test case uses file: URI XOM doesn't support
                                continue;
                            }
                            else if ("select_select85".equals(id)) {  
                                // This has been fixed in Xalan 2.6.0
                                // However, it's a bug in earlier versions of Xalan
                                // bundled with the JDK 1.4.2_05
                                continue;
                            }
                            else if ("numberformat_numberformat45".equals(id)
                              || "numberformat_numberformat46".equals(id)) {  
                                // Test case uses illegal number format pattern
                                // Xalan catches this. Test case is in error.
                                // report this????
                                continue;
                            }
                            
                            System.err.println(id);
                            System.err.println(ex.getMessage());
                            throw ex;
                        }
                    }
                    
                }
            } 
            
        }
     
    }
    
    
    public void testOASISMicrosoftConformanceSuite()  
      throws IOException, ParsingException, XSLException {
        
        Builder builder = new Builder();
        File base = new File("data/oasis-xslt-testsuite/TESTS/");
        File catalog = new File(base, "catalog.xml");
        
        // The test suite need to be installed separately. If we can't
        // find the catalog, we just don't run these tests.
        if (catalog.exists()) {
            Document doc = builder.build(catalog);
            Element testsuite = doc.getRootElement();
            Elements submitters = testsuite.getChildElements("test-catalog");
            Element submitter = submitters.get(1);
            Elements testcases = submitter.getChildElements("test-case");
            for (int j = 0; j < testcases.size(); j++) {
                Element testcase = testcases.get(j);
                String id = testcase.getAttributeValue("id");
                File root = new File(base, "MSFT_Conformance_Tests");
                root = new File(root, testcase.getFirstChildElement("file-path").getValue());
                File input = null;
                File style = null;
                File output = null;
                Element scenario = testcase.getFirstChildElement("scenario");
                Elements inputs = scenario.getChildElements("input-file");
                for (int k = 0; k < inputs.size(); k++) {
                    Element file = inputs.get(k);
                    String role = file.getAttributeValue("role");
                    if ("principal-data".equals(role)) {
                        input = new File(root, file.getValue());
                    }
                    else if ("principal-stylesheet".equals(role)) {
                        style = new File(root, file.getValue());
                    }
                }  // end for 
                Elements outputs = scenario.getChildElements("output-file");
                for (int k = 0; k < outputs.size(); k++) {
                    Element file = outputs.get(k);
                    String role = file.getAttributeValue("role");
                    if ("principal".equals(role)) {
                        // Fix up OASIS catalog bugs
                        File parent = new File(root.getParent());
                        parent = new File(parent, "REF_OUT");
                        parent = new File(parent, root.getName());
                        String outputFileName = file.getValue();
                        output = new File(parent, outputFileName);
                    }
                }  // end for 
                
                try {
                    Document styleDoc = builder.build(style);
                    // XXX For the moment let's just skip all tests
                    // that specify indent="yes" for the output;
                    // we can fix this with special comparison later.
                    if (indentYes(styleDoc)) continue;
                    else if ("BVTs_bvt002".equals(id)) {
                        // Xalan bug report????;
                        continue;
                    }
                    else if ("BVTs_bvt041".equals(id) || "BVTs_bvt063".equals(id)
                        || "BVTs_bvt070".equals(id)) {
                        // Xalan does not recover from this error involving multiple
                        // conflicting xsl:output at same import precedence
                        continue;
                    }
                    Document inputDoc = builder.build(input);
                    XSLTransform xform = new XSLTransform(styleDoc);
                    Nodes result = xform.transform(inputDoc);
                    if (output == null) {
                        if ("Attributes__89463".equals(id)
                          || "Attributes__89465".equals(id)) {
                            // Processors are allowed to recover from
                            // this problem.
                            assertEquals(0, result.size());
                        }
                        else if ("Attributes__89464".equals(id)) {
                            // Processors are allowed to recover from
                            // this problem.
                            assertEquals(0, ((Element) result.get(0)).getAttributeCount());
                        }
                        else if (id.startsWith("Errors_")) {
                            // Processors are allowed to recover from
                            // most of these problems.
                        }
                        else if (id.startsWith("FormatNumber")) {
                            // Processors are allowed to recover from
                            // most of these problems.
                        }
                        else if ("BVTs_bvt074".equals(id)) {
                            // Processors are allowed to recover from
                            // this problem.
                            assertEquals(0, result.get(0).getChildCount());
                        }
                        else if ("Attributes_Attribute_UseXmlnsNsAsNamespaceForAttribute".equals(id)
                          || "Attributes_Attribute_UseXmlnsAsNamespaceForAttributeImplicitly".equals(id)
                          || "Elements_Element_UseXslElementWithNameSpaceAttrEqualToXmlnsUri".equalsIgnoreCase(id)
                          || "Elements_Element_UseXslElementWithNameSpaceEqualToXmlnsUri".equalsIgnoreCase(id)
                          ) {
                            // test follows namespace errata we don't accept
                        }
                        else if ("AttributeSets_RefToUndefinedAttributeSet".equals(id)
                          || "Namespace__77665".equals(id)
                          || "Namespace__77675".equals(id)
                          ) {
                            // I think the test case is wrong; I see 
                            // nothing in the spec that says this is
                            // an error
                            // XXX Verify and report
                        }
                        else if ("Variables__84633".equals(id)
                          || "Variables__84634".equals(id)
                          || "Variables__84697".equals(id)
                          || "Variables__84710".equals(id)
                          ) {
                            // And error. See 11.4
                            // but are processors allowed to recover?
                            // Hmm acording to section 17, the processor may signal these errors
                            // and may but need not recover from them. 
                            // XXX Verify and report
                        }
                        else if ("Output__78176".equals(id)
                          ) {
                            // I think the test case is wrong; I see 
                            // nothing in the spec that says this is
                            // an error
                            // XXX Verify and report
                        }
                        else if (id.startsWith("XSLTFunctions__100")) {
                            // I think the test case is wrong; I see 
                            // nothing in the spec that says this is
                            // an error. These are all about the unparsed entity
                            // function.
                            // XXX Verify and report
                        }
                        else if ("Namespace__78027".equals(id)) {
                            // XXX Possible Xalan bug; Verify and report
                        }
                        else if ("Output_Output_UseStandAloneAttributeWithMultipleRoots".equals(id)) {
                            // Error only appears when document is serialized;
                            // not before
                        }
                        else { // transform should have failed
                            fail("Transformed " + style + "\n id: "
                              + testcase.getAttributeValue("id"));
                        }
                    }
                    else { 
                        try { 
                            if ("Attributes_xsl_attribute_dup_attr_with_namespace_conflict".equals(id)) {
                               // I think this test case is wrong;
                               // XXX document and report   
                            }
                            else if ("BVTs_bvt041".equals(id)) {
                               // I think this test case output is wrong;
                               // XXX document and report   
                            }
                            else if ("Comment_DisableOutputEscaping_XslTextInXslComment".equals(id)) {
                               // I think this test case output is wrong;
                               // XXX document and report   
                            }
                            else if ("Output__77927".equals(id) 
                              || "Output__77927".equals(id)
                              || "Output__77928".equals(id)
                              || "Output__84304".equals(id)
                              || "Output__84305".equals(id)
                              || "Output__84312".equals(id)
                              || "Output__84619".equals(id)
                              || "Output__84620".equals(id)
                              || "Output_EntityRefInAttribHtml".equals(id)
                            ) {
                               // I think this test case output is wrong;
                               // XXX document and report   
                            }
                            else if ("BVTs_bvt057".equals(id)) {
                               // I think this test case requires remapping prefixes
                               // so output isn't quite identical
                               // XXX verify
                                continue;
                            }
                            else if ("Output_DoctypePublicAndSystemAttribute".equals(id)) {
                               // This test case uses non-URI system identifier
                               // XXX possible Xerces bug and test suite bug; report verify
                                continue;
                            }
                            else if ("Output_Modified84433".equals(id)) {
                               // This test case uses disable output escaping
                               // so the results don't match up
                                continue;
                            }
                            else if ("Sorting_Sort_SortTextWithNonTextCharacters".equals(id)) {
                               // Xalan and MSXML don't sort non alphabetic characters 
                               // exactly the same, but that's legal
                                continue;
                            }
                            else if ("Text_DoeWithCdataInText".equals(id)) {
                               // XXX explore further but I think this is an issue
                               // with consecutive text nodes in result tree vs. 
                               // parsed tree
                                continue;
                            }
                            else if ("Whitespaces__91434".equals(id)
                              || "Whitespaces__91436".equals(id)        
                              || "Whitespaces__91442".equals(id)        
                              || "Whitespaces__91444".equals(id)        
                              || "Whitespaces__91446".equals(id)        
                              || "Whitespaces__91450".equals(id)        
                            ) {
                               // XXX explore further; may be a Xalan bug
                                continue;
                            }
                            else if ("AVTs__77591".equals(id) 
                              || "Copying_ResultTreeFragmentWithEscapedText".equals(id)
                              || "Keys_MultipltKeysInclude".equals(id)
                              || "Keys_PerfRepro3".equals(id)
                              || "Number__84683".equals(id)
                              || "Number__84687".equals(id)
                              || "Number__84692".equals(id)
                              || "Number__84694".equals(id)
                              || "Number__84699".equals(id)
                              || "Number__84700".equals(id)
                              || "Number__84716".equals(id)
                              || "Number__84717".equals(id)
                              || "Number__84722".equals(id)
                              || "Number__84723".equals(id)
                              || "Number__84724".equals(id)
                              || "Number__84725".equals(id)
                              || "Number_NaNOrInvalidValue".equals(id)
                              || "Number_ValueAsNodesetTest1".equals(id)
                              || "Number_ValueAsEmptyNodeset".equals(id)
                            ) {
                               // XXX I don't understand what fails in these cases; figure it out 
                                // a lot of these are probably just differences in locales supported
                            }
                            else if (id.equals("XSLTFunctions_BooleanFunction")) {
                                // I think the test case is wrong; or perhaps unspecified
                                // XXX Verify and report
                            }
                            else if (id.equals("XSLTFunctions_TestIdFuncInComplexStruct")) {
                                // I think the test case output white space is wrong; 
                                // XXX Verify and report
                            }
                            else {
                                Document expectedResult = builder.build(output);
                                Document actualResult = XSLTransform.toDocument(result);
                                assertEquals("Mismatch with " + id,
                                  expectedResult, actualResult);
                            }
                        } // end try
                        catch (ParsingException ex) {  
                            // a few of the test cases generate 
                            // text or HTML output rather than 
                            // well-formed XML. For the moment, I 
                            // just skip these; but could I compare
                            // the raw text to the value of the 
                            // document object instead????
                            continue;
                        }
                        catch (IllegalAddException ex) {
                            // A few of the test cases generate 
                            // incomplete documents so we can't
                            // compare output. Perhaps I could
                            // wrap in an element, then get children
                            // to build a Node object rather than a
                            // Document???? i.e. a fragment parser?
                            // Could use a SequenceInputStream to hack this
                        }
                    } // end else
                    
                } // end try
                catch (MalformedURIException ex) {
                    // several stylesheets use relative namespace URIs XOM
                    // does not support; skip the test
                }
                catch (FileNotFoundException ex) {
                    // The catalog doesn't always match what's on disk
                }
                catch (UnknownHostException ex) {
                    // A few tests like ProcessingInstruction__78197 
                    // point to external DTD subsets that can't be loaded
                }
                catch (ParsingException ex) {
                    String operation = scenario.getAttributeValue("operation");
                    if (!"execution-error".equals(operation)) {
                        if ("Namespace_XPath_PredefinedPrefix_XML".equals(id)) {
                            // XXX double check
                            // uses relative namespace URIs
                        }
                        else if ("Sorting__78191".equals(id)
                          || "Text__78245".equals(id)
                          || "Text__78273".equals(id)
                          || "Text__78281".equals(id)
                        ) {
                            // binds XML namespace to prefix other than xml
                        }
                        else {
                            System.err.println(id + ": " + ex.getMessage());
                            throw ex;
                        }
                    }
                }
                catch (XSLException ex) {
                    // If the output was null the transformation 
                    // was expected to fail
                    if (output != null) {
                        Throwable cause = ex.getCause();
                        if ("Attributes__81487".equals(id)
                          || "Attributes__81551".equals(id)) {
                            // spec inconsistency; see 
                            // http://lists.w3.org/Archives/Public/xsl-editors/2004JulSep/0003.html
                            continue;
                        }
                        else if (cause instanceof MissingResourceException) {
                            // XXX Number__84705 is either a Xalan bug or a problem with your Xalan jar file
                            // fix either way
                        }
                        else if ("Include_Include_IncludedStylesheetShouldHaveDifferentBaseUri".equals(id)
                          || "Include_RelUriTest1".equals(id)
                          || "Include_RelUriTest2".equals(id)
                          || "Include_RelUriTest3".equals(id)
                          || "Include_RelUriTest4".equals(id)
                          || "Include_RelUriTest5".equals(id)
                          || "Include_RelUriTest6".equals(id)
                        ) {
                           // I think this test case is wrong; Uses backslash in URI
                           // XXX verify, document and report   
                        }
                        else if ("Elements__89070".equals(id)) {
                            // bug fixed in later versions of Xalan
                        }
                        else if ("Namespace-alias_Namespace-Alias_NSAliasForDefaultWithExcludeResPref".equals(id)) {
                           // I think this test case is wrong; 
                           // XXX verify, document and report   
                        }
                        else if ("Variables_VariableWithinVariable".equals(id)
                          ) {
                            // Xalan does not recover from this one
                        }
                        else if ("BVTs_bvt054".equals(id)
                          || "BVTs_bvt094".equals(id)) {
                           // I think this test case output is wrong;
                           // XXX document and report   
                            continue;
                        }
                        else if ("Output__78177".equals(id)
                          || "Output__84009".equals(id)) {
                           // Xalan does not recover from this error 
                           // which involves duplicate and possibly conflicting xsl:output elements
                            continue;
                        }
                        else if ("Comment_Comment_CDATAWithSingleHyphen".equals(id)
                          || "Comment_Comment_DoubleHypenEntitywithDelCharacter".equals(id)
                          || "Comment_Comment_LineOfAllHyphens".equals(id)
                          || "Comment_Comment_SingleHyphenOnly".equals(id)
                          || "Comment_Comment_DoubleHyphenONLY".equals(id)) {
                           // Begins comment data with hyphen, which XOM doesn't allow   
                            continue;
                        }
                        else if ("ProcessingInstruction_ValueOfandTextWithDoeInProcInstr".equals(id)) {
                           // Begins processing instruction data with white space, which XOM doesn't allow   
                            continue;
                        }
                        else if ("Elements__89716".equals(id)
                          || "Elements__89717".equals(id)
                          || "Elements__89718".equals(id)
                          || "Output__84309".equals(id)
                          || "Namespace__77670".equals(id))
                          {
                           // Xalan doesn't recover from these, though recovery is allowed   
                            continue;
                        }
                        else if ("Output__84306".equals(id))
                          {
                           // XXX I think this one's a Xalan bug; report it   
                            continue;
                        }
                        else if (cause != null
                          && cause instanceof MalformedURIException) {
                            // Some of the tests generate relative namespace URIs
                            // XOM doesn't support
                            continue;
                        }
                        else {
                            System.err.println(id + ": " + ex.getMessage());
                            System.err.println("in " + style);
                            if (cause != null) {
                                System.err.println("cause: " + cause.getMessage());                                
                            }
                            throw ex;
                        }
                    }
                } // end catch
                catch (XMLException ex) {
                    if ("Text_modified78309".equals(id)) {
                       // output is not a full document   
                    }
                    else {
                        System.err.println(id);
                        throw ex;
                    }
                }
                
            } // end for 
            
        } // end if 
     
    }
    
    
    public void testToDocumentWithEmptyNodes() {
     
        try {
            XSLTransform.toDocument(new Nodes());
            fail("Converted empty nodes to document");
        }
        catch (XMLException success) {
            assertNotNull(success.getMessage());
        }
        
    }
    
    
    public void testToDocumentWithNoRoot() {
     
        Nodes input = new Nodes();
        input.append(new Comment("data"));
        try {
            XSLTransform.toDocument(new Nodes());
            fail("Converted comment to document");
        }
        catch (XMLException success) {
            assertNotNull(success.getMessage());
        }
        
    }
    
    
    public void testToDocumentWithText() {
     
        Nodes input = new Nodes();
        Element root = new Element("root");
        Comment comment = new Comment("data");
        ProcessingInstruction pi = new ProcessingInstruction("target", "data");
        input.append(comment);
        input.append(root);
        input.append(pi);
        input.append(new Text("text"));
        try {
            XSLTransform.toDocument(new Nodes());
            fail("Converted text to document");
        }
        catch (XMLException success) {
            assertNotNull(success.getMessage());
        }
        
    }
    
    
    public void testToDocumentWithAttribute() {
     
        Nodes input = new Nodes();
        Element root = new Element("root");
        Comment comment = new Comment("data");
        ProcessingInstruction pi = new ProcessingInstruction("target", "data");
        input.append(comment);
        input.append(root);
        input.append(pi);
        input.append(new Attribute("name", "text"));
        try {
            XSLTransform.toDocument(new Nodes());
            fail("Converted text to document");
        }
        catch (XMLException success) {
            assertNotNull(success.getMessage());
        }
        
    }
    
    
    public void testToDocumentWithDocType() {
     
        Nodes input = new Nodes();
        Element root = new Element("root");
        DocType doctype = new DocType("root");
        Comment comment = new Comment("data");
        ProcessingInstruction pi = new ProcessingInstruction("target", "data");
        input.append(comment);
        input.append(doctype);
        input.append(root);
        input.append(pi);
        Document output = XSLTransform.toDocument(input);
        assertEquals(root, output.getRootElement());
        assertEquals(comment, output.getChild(0));
        assertEquals(doctype, output.getChild(1));
        assertEquals(pi, output.getChild(3));
        assertEquals(input.size(), output.getChildCount());
        
    }
    
    
    public void testToDocumentWithDocTypeInEpilog() {
     
        Nodes input = new Nodes();
        Element root = new Element("root");
        DocType doctype = new DocType("root");
        Comment comment = new Comment("data");
        ProcessingInstruction pi = new ProcessingInstruction("target", "data");
        input.append(comment);
        input.append(root);
        input.append(doctype);
        input.append(pi);
        try {
            XSLTransform.toDocument(input);
            fail("Allowed doctype in epilog");
        }
        catch (XMLException success) {
            assertNotNull(success.getMessage());
        }
        
    }
    
    
    public void testToDocumentWithDoubleRoot() {
     
        Nodes input = new Nodes();
        Element root = new Element("root");
        Comment comment = new Comment("data");
        input.append(comment);
        input.append(root);
        input.append(new Element("root2"));
        try {
            XSLTransform.toDocument(input);
            fail("Allowed two root elements");
        }
        catch (XMLException success) {
            assertNotNull(success.getMessage());
        }
        
    }
    
    
    public void testToDocumentWithSingleRoot() {
     
        Nodes input = new Nodes();
        Element root = new Element("root");
        input.append(root);
        Document output = XSLTransform.toDocument(input);
        assertEquals(root, output.getRootElement());
        assertEquals(input.size(), output.getChildCount());
        
    }
    

    public void testToDocumentWithPrologAndEpilog() {
     
        Nodes input = new Nodes();
        Element root = new Element("root");
        Comment comment = new Comment("data");
        ProcessingInstruction pi = new ProcessingInstruction("target", "data");
        input.append(comment);
        input.append(root);
        input.append(pi);
        Document output = XSLTransform.toDocument(input);
        assertEquals(root, output.getRootElement());
        assertEquals(comment, output.getChild(0));
        assertEquals(pi, output.getChild(2));
        assertEquals(input.size(), output.getChildCount());
        
    }
    
    


    
}
