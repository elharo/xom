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

import nu.xom.Serializer;
import nu.xom.Element;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Builder;
import nu.xom.Comment;
import nu.xom.ParseException;
import nu.xom.ProcessingInstruction;
import nu.xom.Attribute;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.StringReader;

/**
 * @author Elliotte Rusty Harold
 * @version 1.0d19
 *
 */
public class SerializerTest extends XOMTestCase {

    private Builder parser;

    public SerializerTest(String name) {
        super(name);
    }

    protected void setUp() {
       parser = new Builder();  
    }

    public void testXMLSpacePreserve() throws IOException {
        Element root = new Element("test");
        root.addAttribute(
          new Attribute(
            "xml:space", "http://www.w3.org/XML/1998/namespace", "preserve"));
        String value =  
          "This is a long sentence with plenty of opportunities for breaking from beginning to end.";
        root.appendChild(value);    
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(out, "UTF-8");
        serializer.setMaxLength(20);
        serializer.write(new Document(root));
        String result = out.toString("UTF-8");
        assertTrue(result.indexOf(value) > 0);            
    }

    public void testXMLSpaceDefault() throws IOException {
        Element root = new Element("test");
        root.addAttribute(
          new Attribute(
            "xml:space", "http://www.w3.org/XML/1998/namespace", "preserve"));
        Element child1 = new Element("preserve");
        String value = 
          "This is a long sentence with plenty of opportunities for breaking from beginning to end.";
        child1.appendChild(value);    
        Element child2 = new Element("default");
        root.appendChild(child1);
        root.appendChild(child2);
        child2.addAttribute(
          new Attribute(
            "xml:space", "http://www.w3.org/XML/1998/namespace", "default"));
        String value2 = "This is another very long sentence with plenty of opportunities for breaking from beginning to end.";
        child2.appendChild(value2);

        String value3 = 
          "This is still another very long sentence with plenty of opportunities for breaking from beginning to end.";
        Element preserveAgain = new Element("test");
        preserveAgain.appendChild(value3);
        child2.appendChild(preserveAgain);
        preserveAgain.addAttribute(
          new Attribute(
            "xml:space", "http://www.w3.org/XML/1998/namespace", "preserve"));


        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(out, "UTF-8");
        serializer.setMaxLength(20);
        serializer.write(new Document(root));
        String result = out.toString("UTF-8");
        assertTrue(result.indexOf(value) > 0);            
        assertTrue(result.indexOf(value3) > 0);            
        assertEquals(-1, result.indexOf(value2));            
    }

    public void testDontSerializeXMLNamespace() throws IOException {        
        Element root 
          = new Element("html", "http://www.w3.org/1999/xhtml");
        root.addAttribute(
          new Attribute(
            "xml:lang", "http://www.w3.org/XML/1998/namespace", "en"));
        Document doc = new Document(root);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(out, "UTF-8");
        serializer.write(doc);
        String result = out.toString("UTF-8");
        assertEquals(-1, result.indexOf("xmlns:xml"));
        assertTrue(result.indexOf("xml:lang=") > 1);
    }
    
    public void testDontSerializeNoNamespace() throws IOException {        
        Element root = new Element("root");
        Document doc = new Document(root);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(out, "UTF-8");
        serializer.write(doc);
        String result = out.toString("UTF-8");
        assertEquals(-1, result.indexOf("xmlns="));
    }
    
    public void testDefaultNamespace() throws IOException {        
        Element root = new Element("root", "http://www.example.com");
        Document doc = new Document(root);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(out, "UTF-8");
        serializer.write(doc);
        String result = out.toString("UTF-8");
        assertTrue(result.indexOf("xmlns=") > 1);
        assertTrue(result.indexOf("http://www.example.com") > 1);
    }
    
    public void testEmptyElement() throws IOException {        
        Element root = new Element("root");
        Document doc = new Document(root);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(out, "UTF-8");
        serializer.write(doc);
        String result = out.toString("UTF-8");
        assertEquals(
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<root/>\r\n",
          result
        );
    }
    
    public void testElementWithText() 
      throws IOException, ParseException {        
        Element root = new Element("root");
        String data = "   test   \n\n   \n  \n hello again";
        root.appendChild(data);
        Document doc = new Document(root);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(out, "UTF-8");
        serializer.write(doc);
        String result = out.toString("UTF-8");
        
        assertEquals( 
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<root>"
          + data + "</root>\r\n",
          result);    
    }    
    
    public void testElementWithTextAndCarriageReturns() 
      throws IOException, ParseException {        
        Element root = new Element("root");
        String data = "   test   \r\n   \n  \r hello again";
        root.appendChild(data);
        Document doc = new Document(root);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(out, "UTF-8");
        serializer.write(doc);
        String result = out.toString("UTF-8");
        
        assertEquals( 
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<root>"
          + "   test   &#x0D;\n   \n  &#x0D; hello again" + "</root>\r\n",
          result);    
    }    
    
    private void serializeParseAndCompare(Document doc) 
      throws IOException, ParseException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(out, "UTF-8");
        serializer.write(doc);
        String result = out.toString("UTF-8");
        
        Document resultDoc = parser.build(result, null);
        XOMTestCase.assertEquals(doc, resultDoc);    
    }
    
    public void testBasicElementWithText() 
      throws IOException, ParseException {        
        Element root = new Element("root");
        String data = "   test   \n  hello again";
        root.appendChild(data);
        Document doc = new Document(root);
        serializeParseAndCompare(doc);
        
    }
    
    public void testAttributes() 
      throws IOException, ParseException {        
        Element root = new Element("root");
        root.addAttribute(new Attribute("test", "sadlkhasdk"));
        String data = "   test   \n  hello again";
        root.appendChild(data);
        Document doc = new Document(root);
        serializeParseAndCompare(doc);
        
        root.addAttribute(new Attribute("test2", "sadlkhasdk"));
        serializeParseAndCompare(doc);

        root.addAttribute(new Attribute("test3", " s adl  khasdk  "));
        serializeParseAndCompare(doc);

        root.addAttribute(new Attribute("xlink:type", 
          "http://www.w3.org/2001/xlink", " s adl  khasdk  "));
        serializeParseAndCompare(doc);

        
    }

    public void testChildElements() 
      throws IOException, ParseException {        
        Element root = new Element("root");
        Document doc = new Document(root);
        serializeParseAndCompare(doc);

        Element child1 = new Element("child");
        Element child2 = new Element("child");
        Element child3 = new Element("child");
        serializeParseAndCompare(doc);
        root.appendChild(child1);
        serializeParseAndCompare(doc);
        child1.appendChild(child2);
        serializeParseAndCompare(doc);
        root.appendChild(child3);
        serializeParseAndCompare(doc);
        child3.appendChild("some data");
        serializeParseAndCompare(doc);
        child2.appendChild("\nsome data with \n line breaks\n");
        serializeParseAndCompare(doc);
        root.insertChild("now let's have some mixed content", 0);        
        serializeParseAndCompare(doc);

        root.setNamespaceURI("http://www.example.org/");
        serializeParseAndCompare(doc);
        child1.setNamespaceURI("http://www.example.org/");
        serializeParseAndCompare(doc);
        child2.setNamespaceURI("http://www.example.org/");
        serializeParseAndCompare(doc);
        child3.setNamespaceURI("http://www.example.org/");
        serializeParseAndCompare(doc);
        child1.setNamespacePrefix("example");
        serializeParseAndCompare(doc);
        child2.setNamespacePrefix("perverse");
        serializeParseAndCompare(doc);    
        
    }

    public void testPrologAndEpilog() 
      throws IOException, ParseException {        
        Element root = new Element("root");
        Document doc = new Document(root);
        serializeParseAndCompare(doc);

        doc.insertChild(new Comment("Hello"), 0);
        serializeParseAndCompare(doc);    
        doc.insertChild(new DocType("root"), 0);
        serializeParseAndCompare(doc);    
        doc.insertChild(new ProcessingInstruction("test", "some data"), 
          0);
        serializeParseAndCompare(doc);    
        doc.insertChild(new Comment("Goodbye"), 0);
        serializeParseAndCompare(doc);    
        doc.insertChild(
          new ProcessingInstruction("goodbye", "some data"), 0);
        serializeParseAndCompare(doc);    
        doc.appendChild(new Comment("Hello"));
        serializeParseAndCompare(doc);    
        doc.appendChild(
          new ProcessingInstruction("test", "some data"));
        serializeParseAndCompare(doc);    
        
    }
    
    
    public void testChangeLineSeparator() throws IOException {        
        Element root = new Element("root");
        Document doc = new Document(root);
        String breaks = 
          "This\nstring\rcontains\r\nseveral\r\rweird line breaks.";
        root.appendChild(breaks);
        doc.insertChild(new Comment(breaks), 0);
        root.addAttribute(new Attribute("test", breaks));
        root.appendChild(new ProcessingInstruction("target", breaks));
            
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(out, "UTF-8");
        serializer.setLineSeparator("\n");
        serializer.write(doc);
        String result = out.toString("UTF-8");
        assertTrue(result.indexOf('\n') > 0);
        assertTrue(result + "**\n" + result.indexOf('\r'), 
          result.indexOf('\r') == -1);

        out = new ByteArrayOutputStream();
        serializer = new Serializer(out, "UTF-8");
        serializer.setLineSeparator("\r");
        serializer.write(doc);
        result = out.toString("UTF-8");
        assertTrue(result.indexOf('\r') > 0);
        assertTrue(result.indexOf('\n') == -1);
        
        out = new ByteArrayOutputStream();
        serializer = new Serializer(out, "UTF-8");
        serializer.setLineSeparator("\r\n");
        serializer.write(doc);
        result = out.toString("UTF-8");
        assertTrue(result.indexOf("\r\n") > 0);
        for (int i = 0; i < result.length(); i++) {
          int c = result.charAt(i);
          if (c == '\r') assertTrue(result.charAt(i+1) == '\n');    
        }     
        
    }
    
    public void testDontChangeLineSeparator() throws IOException {        
        Element root = new Element("root");
        Document doc = new Document(root);
        String breaks 
          = "This\nstring\rcontains\r\rseveral\n\nweird line breaks.";
        String breaksHalfEscaped 
          = "This\nstring&#x0D;contains&#x0D;&#x0D;several\n\nweird line breaks.";
        String breaksEscaped 
          = "This&#x0A;string&#x0D;contains&#x0D;&#x0D;several&#x0A;&#x0A;weird line breaks.";
        root.appendChild(breaks);
            
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(out, "UTF-8");
        serializer.write(doc);
        String result = out.toString("UTF-8");
        assertTrue(result.indexOf(breaksHalfEscaped) > 0);
        
        root = new Element("root");
        doc = new Document(root);
        doc.insertChild(new Comment(breaks), 0);
            
        out = new ByteArrayOutputStream();
        serializer = new Serializer(out, "UTF-8");
        serializer.write(doc);
        result = out.toString("UTF-8");
        assertTrue(result.indexOf('\n') > 0);
        assertTrue(result.indexOf('\r') > 0);
        assertTrue(result.indexOf("\r\r") > 0);
        assertTrue(result.indexOf("\n\n") > 0);
        assertTrue(result.indexOf(breaks) > 0);

        root = new Element("root");
        doc = new Document(root);
        root.addAttribute(new Attribute("test", breaks));
            
        out = new ByteArrayOutputStream();
        serializer = new Serializer(out, "UTF-8");
        serializer.write(doc);
        result = out.toString("UTF-8");
        assertTrue(result.indexOf(breaksEscaped) > 0);

        root = new Element("root");
        doc = new Document(root);
        root.appendChild(new ProcessingInstruction("target", breaks));
            
        out = new ByteArrayOutputStream();
        serializer = new Serializer(out, "UTF-8");
        serializer.write(doc);
        result = out.toString("UTF-8");
        assertTrue(result.indexOf('\n') > 0);
        assertTrue(result.indexOf('\r') > 0);
        assertTrue(result.indexOf("\r\r") > 0);
        assertTrue(result.indexOf("\n\n") > 0);
        assertTrue(result.indexOf(breaks) > 0);        
        
    }
    
    public void testPreserveBaseURI() throws IOException {        
        Element root = new Element("root");
        Document doc = new Document(root);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(out, "UTF-8");
        serializer.preserveBaseURI(true);
        serializer.write(doc);
        String result = out.toString("UTF-8");
        assertTrue(result.indexOf("<root") > 1);
        doc.setBaseURI("http://www.example.com/index.xml");
        serializer.write(doc);
        result = out.toString("UTF-8");
        assertTrue(result.indexOf("<root ") > 1);
        assertTrue(result.indexOf("xml:base=") > 1);
        assertTrue(
          result.indexOf("http://www.example.com/index.xml") > 1
        );
        
    } 
    
    public void testSetLineSeparator() {
        Serializer serializer = new Serializer(System.out);
        
        serializer.setLineSeparator("\r");
        assertEquals("\r", serializer.getLineSeparator());
        serializer.setLineSeparator("\n");
        assertEquals("\n", serializer.getLineSeparator());
        serializer.setLineSeparator("\r\n");
        assertEquals("\r\n", serializer.getLineSeparator());
        
        try {
            serializer.setLineSeparator("r");
            fail("Allowed illegal separator character");
        }
        catch (IllegalArgumentException ex) {
            // success
        }
        
        try {
            serializer.setLineSeparator("n");
            fail("Allowed illegal separator character");
        }
        catch (IllegalArgumentException ex) {
            // success
        }
        
        try {
            serializer.setLineSeparator(" ");
            fail("Allowed illegal separator character");
        }
        catch (IllegalArgumentException ex) {
            // success
        }
        
        try {
            serializer.setLineSeparator("rn");
            fail("Allowed illegal separator character");
        }
        catch (IllegalArgumentException ex) {
            // success
        }
        
        try {
            serializer.setLineSeparator("<");
            fail("Allowed illegal separator character");
        }
        catch (IllegalArgumentException ex) {
            // success
        }
        
        try {
            serializer.setLineSeparator("\u0085");
            fail("Allowed NEL separator character");
        }
        catch (IllegalArgumentException ex) {
            // success
        }
        
    }

      
    public void testLowerLimitOfUnicodeInCharacterData() 
      throws IOException {        
        Element root = new Element("root");
        root.appendChild("\uD800\uDC00");
        Document doc = new Document(root);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.write(doc);
        String result = out.toString("ISO-8859-1");
        assertTrue(result, result.indexOf("&#x10000;") > 12);
    }
    
    public void testUpperLimitOfUnicodeInCharacterData() 
      throws IOException {        
        Element root = new Element("root");
        root.appendChild("\uDBFF\uDFFD");
        Document doc = new Document(root);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.write(doc);
        String result = out.toString("ISO-8859-1");
        assertTrue(result, result.indexOf("&#x10fffd;") > 12);
    }
      
    public void testSerializePlane1CharacterInAttributeValue() 
      throws IOException {        
        Element root = new Element("root");
        root.addAttribute(new Attribute("test", "\uD834\uDD1E"));
        Document doc = new Document(root);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.write(doc);
        String result = out.toString("ISO-8859-1");
        assertTrue(result, result.indexOf("&#x1d11e;") > 12);
    }
    
    public void testSerializePlane1CharacterInCharacterData() 
      throws IOException {        
        Element root = new Element("root");
        root.appendChild("\uD834\uDD1E");
        Document doc = new Document(root);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.write(doc);
        String result = out.toString("ISO-8859-1");
        assertTrue(result, result.indexOf("&#x1d11e;") > 12);
    }
    
    public void testEscapeAttributeValue() throws IOException {        
        Element root = new Element("root");
        root.addAttribute(new Attribute("test", "\u0110"));
        Document doc = new Document(root);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.write(doc);
        String result = out.toString("ISO-8859-1");
        assertTrue(result, result.indexOf("&#x110;") > 5);
    }
    

    
    public void testLineFeedInAttributeValueWithDefaultOptions() 
      throws IOException, ParseException {        
        Element root = new Element("root");
        root.addAttribute(new Attribute("test", "\n"));
        Document original = new Document(root);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.write(original);
        out.close();
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        Document reparsed = parser.build(in);
        assertEquals(original, reparsed);
    }
    
    public void testCarriageReturnInAttributeValueWithDefaultOptions() 
      throws IOException, ParseException {        
        Element root = new Element("root");
        root.addAttribute(new Attribute("test", "\r"));
        Document original = new Document(root);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.write(original);
        out.close();
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        Document reparsed = parser.build(in);
        assertEquals(original, reparsed);
    }
    
    public void testCarriageReturnInTextWithDefaultOptions() 
      throws IOException, ParseException {        
        Element root = new Element("root");
        root.appendChild("\r");
        Document original = new Document(root);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.write(original);
        out.close();
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        Document reparsed = parser.build(in);
        assertEquals(original, reparsed);
    }
    
    public void testTabInAttributeValueWithDefaultOptions() 
      throws IOException, ParseException {        
        Element root = new Element("root");
        root.addAttribute(new Attribute("test", "\t"));
        Document original = new Document(root);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.write(original);
        out.close();
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        Document reparsed = parser.build(in);
        assertEquals(original, reparsed);
    }
    
    /**
     * <p>
     *   Test that tabs in attribute values are escaped even when
     *   a line separator is set.
     * </p>
     * 
     * @throws IOException
     * @throws ParseException
     */
    public void testTabInAttributeValueWithLineSeparator() 
      throws IOException, ParseException {        
        Element root = new Element("root");
        root.addAttribute(new Attribute("test", "\t"));
        Document original = new Document(root);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.setLineSeparator("\r");
        serializer.write(original);
        out.close();
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        Document reparsed = parser.build(in);
        assertEquals(original, reparsed);
    }
    
    /**
     * <p>
     *   Test that tabs in attribute values are not escaped when 
     *   indenting.
     * </p>
     * 
     * @throws IOException
     * @throws ParseException
     */
    public void testTabInAttributeValueWithIndenting() 
      throws IOException, ParseException {        
        Element root = new Element("root");
        root.addAttribute(new Attribute("test", "\t"));
        Document original = new Document(root);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.setIndent(2);
        serializer.write(original);
        out.close();
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        Document reparsed = parser.build(in);
        String result = reparsed.getRootElement().getAttributeValue("test");
        assertEquals("Tab not normalized to space", " ", result);
    }

    // This is the tough case; want to maintain line break
    // but not necessarily line break character????
    public void testCRLFInAttributeValueWithLineSeparatorCR() 
      throws IOException, ParseException {        
        Element root = new Element("root");
        root.addAttribute(new Attribute("test", "\r\n"));
        Document original = new Document(root);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.setLineSeparator("\r");
        serializer.write(original);
        out.close();
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        Document reparsed = parser.build(in);
        String result = reparsed.getRootElement().getAttributeValue("test");
        assertEquals("\r", result);
    }
    
    public void testCRLFInAttributeValueWithLineSeparatorLF() 
      throws IOException, ParseException {        
        Element root = new Element("root");
        root.addAttribute(new Attribute("test", "\r\n"));
        Document original = new Document(root);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.setLineSeparator("\n");
        serializer.write(original);
        out.close();
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        Document reparsed = parser.build(in);
        String result = reparsed.getRootElement().getAttributeValue("test");
        assertEquals("\n", result);
    }
    
    public void testLFInAttributeValueWithLineSeparatorCRLF() 
      throws IOException, ParseException {        
        Element root = new Element("root");
        root.addAttribute(new Attribute("test", "\n"));
        Document original = new Document(root);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.setLineSeparator("\r\n");
        serializer.write(original);
        out.close();
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        Document reparsed = parser.build(in);
        String result = reparsed.getRootElement().getAttributeValue("test");
        assertEquals("\r\n", result);
    }

    public void testNotEscapeLinefeedInTextContent() 
      throws IOException, ParseException {        
        Element root = new Element("root");
        root.appendChild("\r\n");
        Document original = new Document(root);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.write(original);
        out.close();
        String result = new String(out.toByteArray(), "ISO-8859-1");
        assertEquals(
          "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\r\n<root>&#x0D;\n</root>\r\n", 
          result
        );
    }
    
    public void testCRLFInAttributeValueWithIndenting() 
      throws IOException, ParseException {        
        Element root = new Element("root");
        root.addAttribute(new Attribute("test", "\r\n"));
        Document original = new Document(root);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.setIndent(2);
        serializer.write(original);
        out.close();
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        Document reparsed = parser.build(in);
        String result = reparsed.getRootElement().getAttributeValue("test");
        assertEquals("CRLF unnecessarily escaped", -1, result.indexOf('\r'));
        // Need to figure out the serializer should indent this 
        // and write a unit test for that too.
    }
    
    public void testCRLFInAttributeValueWithMaxLength() 
      throws IOException, ParseException {        
        Element root = new Element("root");
        root.addAttribute(new Attribute("test", "\r\n"));
        Document original = new Document(root);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.setMaxLength(64);
        serializer.write(original);
        out.close();
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        Document reparsed = parser.build(in);
        String result = reparsed.getRootElement().getAttributeValue("test");
        assertEquals("CRLF unnecessarily escaped", " ", result);
    }

    public void testCRInTextValueWithLineSeparator() 
      throws IOException, ParseException {        
        Element root = new Element("root");
        root.appendChild("\r");
        Document original = new Document(root);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.setLineSeparator("\n");
        serializer.write(original);
        out.close();
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        Document reparsed = parser.build(in);
        String result = reparsed.getValue();
        assertEquals("\n", result);
    } 
    
    public void testCRLFInTextValueWithLineSeparator() 
      throws IOException, ParseException {        
        Element root = new Element("root");
        root.appendChild("test \r\n test");
        Document original = new Document(root);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.setLineSeparator("\n");
        serializer.write(original);
        out.close();
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        Document reparsed = parser.build(in);
        String result = reparsed.getValue();
        assertEquals("test \n test", result);
    } 
    
    public void testCRInTextWithIndenting() 
      throws IOException, ParseException {        
        Element root = new Element("root");
        root.appendChild("\r");
        Document original = new Document(root);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.setIndent(2);
        serializer.write(original);
        out.close();
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        Document reparsed = parser.build(in);
        String result = reparsed.getValue();
        assertEquals("Carriage return unnecessarily escaped", 
          -1, result.indexOf('\r'));
        
        // really need to think about what the serializer should output here
        // and write a test case for that; this is not ideal output
    }
    
    public void testCRInTextWithMaxLength() 
      throws IOException, ParseException {        
        Element root = new Element("root");
        root.appendChild("\r");
        Document original = new Document(root);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.setMaxLength(64);
        serializer.write(original);
        out.close();
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        Document reparsed = parser.build(in);
        String result = reparsed.getValue();
        assertEquals("Carriage return unnecessarily escaped", "\n", result);
    }
    
    /**
     * <p>
     *   Test that tabs in attribute values are not escaped when 
     *   a max length is set. test for setting 0 maxlength and 
     *   negative max length????
     * </p>
     * 
     * @throws IOException
     * @throws ParseException
     */
    public void testTabInAttributeValueWithMaxLength() 
      throws IOException, ParseException {        
        Element root = new Element("root");
        root.addAttribute(new Attribute("test", "\t"));
        Document original = new Document(root);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.setMaxLength(64);
        serializer.write(original);
        out.close();
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        Document reparsed = parser.build(in);
        String result = reparsed.getRootElement().getAttributeValue("test");
        assertEquals("Tab not normalized to space", " ", result);
    }
    
    // add test for negative max length????
    // what should happen?
    public void testSetMaxLength() {
        Serializer serializer = new Serializer(System.out);
        
        serializer.setMaxLength(72);
        assertEquals(72, serializer.getMaxLength());
        serializer.setMaxLength(720);
        assertEquals(720, serializer.getMaxLength());
        serializer.setMaxLength(1);
        assertEquals(1, serializer.getMaxLength());
        serializer.setMaxLength(0);
        assertEquals(0, serializer.getMaxLength());
        
    }

    // add test for negative indent????
    // what should happen?
    public void testSetIndent() {
        Serializer serializer = new Serializer(System.out);
        
        serializer.setIndent(72);
        assertEquals(72, serializer.getIndent());
        serializer.setIndent(720);
        assertEquals(720, serializer.getIndent());
        serializer.setIndent(1);
        assertEquals(1, serializer.getIndent());
        serializer.setIndent(0);
        assertEquals(0, serializer.getIndent());
        
    }

    public void testLineLength() 
      throws IOException, ParseException {
          
        int length = 40;        
        Element root = new Element("root");
        String data = "This is a really long string that does not "
         + "contain any line breaks.  However, there is lots of " 
         + "white space so there shouldn't be any trouble wrapping it"
         + " into 40 characters or less per line. ";
        root.appendChild(data);
        Document doc = new Document(root);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(out, "UTF-8");
        serializer.setMaxLength(length);
        serializer.write(doc);
        String result = out.toString("UTF-8");
        
        BufferedReader reader 
          = new BufferedReader(new StringReader(result)); 
        for (String line = reader.readLine(); 
             line != null;
             line = reader.readLine()) {
            assertTrue(line.length() + ": " + line, line.length() <= length);    
        }
    }

}
