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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Comment;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.ProcessingInstruction;
import nu.xom.Text;
import nu.xom.xslt.XSLException;
import nu.xom.xslt.XSLTransform;

/**
 * <p>
 *  Unit tests for the XSLT engine.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d23
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

    public void testReaderConstructor() {
        
        try {
            new XSLTransform(new StringReader(notAStyleSheet));
            fail("Compiled non-stylesheet");
        }
        catch (XSLException ex) {
            // success   
            assertNotNull(ex.getMessage());
        }
        
    }

    public void testInputStreamConstructor() throws IOException {
        
        try {
            byte[] data = notAStyleSheet.getBytes("UTF-8");
            new XSLTransform(new ByteArrayInputStream(data));
            fail("Compiled non-stylesheet");
        }
        catch (XSLException ex) {
            // success   
            assertNotNull(ex.getMessage());
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
        catch (XSLException ex) {
            // success   
            assertNotNull(ex.getMessage());
        }
        
    }

    public void testFileConstructor() {
        
        try {
            File f = new File("data/schematest.xml");
            new XSLTransform(f);
            fail("Compiled non-stylesheet");
        }
        catch (XSLException ex) {
            // success   
            assertNotNull(ex.getMessage());
        }
        
    }

    public void testURLConstructor() throws IOException {
        
        try {
            File f = new File("data/schematest.xml");
            new XSLTransform(f.toURL().toExternalForm());
            fail("Compiled non-stylesheet");
        }
        catch (XSLException ex) {
            // success   
            assertNotNull(ex.getMessage());
        }
        
    }


/*   <xsl:template match="/">
    <element1>some data and <content/> for a test</element1>
    <element2>Remember, the XSLT processor is going to strip out the literal white space</element2>
    <element3>some data and <content/> for a <!--test--></element3>
    <element4/>
    <xsl:comment>test</xsl:comment>
    <?test are PIs treated as literals in XSLT? ?>
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
        // For debugging
     /* File debug = new File("data/xslt/debug/8-8.xml");
        FileOutputStream out = new FileOutputStream(debug);
        Serializer serializer = new Serializer(out);
        serializer.write(result); */

        Document expected = builder.build("data/xslt/output/8-8.xml");
        assertEquals(expected, result);
        
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
        // For debugging
     /* File debug = new File("data/xslt/debug/8-12.xml");
        FileOutputStream out = new FileOutputStream(debug);
        Serializer serializer = new Serializer(out);
        serializer.write(result); */

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
        // For debugging
     /* File debug = new File("data/xslt/debug/8-12.xml");
        FileOutputStream out = new FileOutputStream(debug);
        Serializer serializer = new Serializer(out);
        serializer.write(result); */

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
        // For debugging
     /* File debug = new File("data/xslt/debug/8-12.xml");
        FileOutputStream out = new FileOutputStream(debug);
        Serializer serializer = new Serializer(out);
        serializer.write(result); */

        Document expected = builder.build("data/xslt/output/8-12.xml");
        assertEquals(expected, result);
        
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
        // For debugging
     /* File debug = new File("data/xslt/debug/8-12.xml");
        FileOutputStream out = new FileOutputStream(debug);
        Serializer serializer = new Serializer(out);
        serializer.write(result); */

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
        // For debugging
     /* File debug = new File("data/xslt/debug/8-12.xml");
        FileOutputStream out = new FileOutputStream(debug);
        Serializer serializer = new Serializer(out);
        serializer.write(result); */

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
        // For debugging
    /*  File debug = new File("data/xslt/debug/8-15.xml");
        FileOutputStream out = new FileOutputStream(debug);
        Serializer serializer = new Serializer(out);
        serializer.write(result); */

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
    
    
}
