/* Copyright 2002-2005 Elliotte Rusty Harold
   
   This library is free software; you can redistribute it and/or modify
   it under the terms of version 2.1 of the GNU Lesser General Public 
   License as published by the Free Software Foundation.
   
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
   GNU Lesser General Public License for more details.
   
   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the 
   Free Software Foundation, Inc., 59 Temple Place, Suite 330, 
   Boston, MA 02111-1307  USA
   
   You can contact Elliotte Rusty Harold by sending e-mail to
   elharo@metalab.unc.edu. Please include the word "XOM" in the
   subject line. The XOM home page is located at http://www.xom.nu/
*/

package nu.xom.tests;

import nu.xom.Node;
import nu.xom.Serializer;
import nu.xom.Element;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Builder;
import nu.xom.Comment;
import nu.xom.ParsingException;
import nu.xom.ProcessingInstruction;
import nu.xom.Attribute;
import nu.xom.UnavailableCharacterException;
import nu.xom.XMLException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

/**
 * <p>
 *   Tests for <code>Serializer</code> functionality.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.1a2
 *
 */
public class SerializerTest extends XOMTestCase {

    private Builder parser;
    private final static double version = Double.parseDouble(
      System.getProperty("java.version").substring(0,3)
    );
    Element root = new Element("root");
    Document doc = new Document(root);
    ByteArrayOutputStream out = new ByteArrayOutputStream();    
    
    
    public SerializerTest(String name) {
        super(name);
    }

    
    protected void setUp() {
       parser = new Builder();  
    }
    
    
    public void testCDATASectionEndDelimiter() throws IOException {

        root.appendChild("]]>");    
        Serializer serializer = new Serializer(out, "UTF-8");
        serializer.setMaxLength(20);
        serializer.write(doc);
        String result = out.toString("UTF-8");
        assertTrue(result.indexOf("]]&gt;") > 0);
        
    }

    
    public void testXMLSpacePreserve() throws IOException {

        root.addAttribute(
          new Attribute(
            "xml:space", 
            "http://www.w3.org/XML/1998/namespace", 
            "preserve"));
        String value =  
          "This is a long sentence with plenty of opportunities for " +
          "breaking from beginning to end.";
        root.appendChild(value);    
        Serializer serializer = new Serializer(out, "UTF-8");
        serializer.setMaxLength(20);
        serializer.write(doc);
        String result = out.toString("UTF-8");
        assertTrue(result.indexOf(value) > 0); 
        
    }

    
    /**
     * <p>
     *   Check that the UTF-16LE encoding omits the byte-order mark.
     * </p>
     * 
     * @throws IOException
     */
    public void testUTF16LEBOM() throws IOException {
        
        if (version >= 1.3) { 
            // UTF-16LE only supported in 1.3 and later
            Serializer serializer = new Serializer(out, "UTF-16LE");
            serializer.write(doc);
            serializer.flush();
            out.flush();
            out.close();
            byte[] data = out.toByteArray();
            assertEquals('<', (char) data[0]);     
            assertEquals((byte) 0, data[1]);
        }
        
    }   
    
    
    /**
     * <p>
     *   Check that the UTF-16 encoding outputs a byte-order mark.
     * </p>
     * 
     * @throws IOException
     */
    public void testUTF16BOM() throws IOException {

        Serializer serializer = new Serializer(out, "UTF-16");
        serializer.write(doc);
        serializer.flush();
        out.flush();
        out.close();
        byte[] data = out.toByteArray();
        assertEquals((byte) 0xFE, data[0]);
        assertEquals((byte) 0xFF, data[1]);
        assertEquals((byte) 0, data[2]);
        assertEquals('<', (char) data[3]);     

    }
    
    
    /**
     * <p>
     *   Check that the UTF-16BE encoding omits the byte-order mark.
     * </p>
     * 
     * @throws IOException
     */
    public void testUTF16BEBOM() throws IOException {

        if (version >= 1.3) { 
            // UTF-16LE only supported in 1.3 and later
            Serializer serializer = new Serializer(out, "UTF-16BE");
            serializer.write(doc);
            serializer.flush();
            out.flush();
            out.close();
            byte[] data = out.toByteArray();
            assertEquals((byte) 0, data[0]);
            assertEquals('<', (char) data[1]);
        }
        
    }

    
    public void testXMLSpaceDefault() throws IOException {

        root.addAttribute(
          new Attribute(
            "xml:space", 
            "http://www.w3.org/XML/1998/namespace", 
            "preserve"));
        Element child1 = new Element("preserve");
        String value = 
          "This is a long sentence with plenty of opportunities for " +
          "breaking from beginning to end.";
        child1.appendChild(value);    
        Element child2 = new Element("default");
        root.appendChild(child1);
        root.appendChild(child2);
        child2.addAttribute(
          new Attribute(
            "xml:space", 
            "http://www.w3.org/XML/1998/namespace", 
            "default"));
        String value2 = 
            "This is another very long sentence with plenty" +
            " of opportunities for breaking from beginning to end.";
        child2.appendChild(value2);

        String value3 = 
          "This is still another very long sentence with plenty of " +
          "opportunities for breaking from beginning to end.";
        Element preserveAgain = new Element("test");
        preserveAgain.appendChild(value3);
        child2.appendChild(preserveAgain);
        preserveAgain.addAttribute(
          new Attribute(
            "xml:space", 
            "http://www.w3.org/XML/1998/namespace", 
            "preserve"));


        Serializer serializer = new Serializer(out, "UTF-8");
        serializer.setMaxLength(20);
        serializer.write(doc);
        String result = out.toString("UTF-8");
        assertTrue(result.indexOf(value) > 0);            
        assertTrue(result.indexOf(value3) > 0);            
        assertEquals(-1, result.indexOf(value2));      
        
    }


    public void testXMLSpacePreserveWithIndenting() 
      throws IOException {

        root.addAttribute(
          new Attribute(
            "xml:space", 
            "http://www.w3.org/XML/1998/namespace", 
            "preserve"));
        root.appendChild(new Element("sameline"));    
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(out, "UTF-8");
        serializer.setIndent(4);
        serializer.write(doc);
        String result = out.toString("UTF-8");
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
          + "<root xml:space=\"preserve\"><sameline/></root>\r\n",
           result);  
        
    }

    
    public void testXMLSpaceUnspecifiedValueWithIndenting() 
      throws IOException {
        
        root.addAttribute(
          new Attribute(
            "xml:space", 
            "http://www.w3.org/XML/1998/namespace", 
            "undefined"));
        root.appendChild(new Element("sameline"));    
        Serializer serializer = new Serializer(out, "UTF-8");
        serializer.setIndent(4);
        serializer.write(doc);
        String result = out.toString("UTF-8");
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
          + "<root xml:space=\"undefined\">\r\n    <sameline/>\r\n</root>\r\n",
           result);   
        
    }

    
    public void testXMLSpaceDefaultWithIndenting() throws IOException {
        
        root.addAttribute(
          new Attribute(
            "xml:space", 
            "http://www.w3.org/XML/1998/namespace", 
            "preserve"));
        Element child = new Element("child");
        child.addAttribute(
          new Attribute(
            "xml:space", 
            "http://www.w3.org/XML/1998/namespace", 
            "default"));
        root.appendChild(child);    
        Serializer serializer = new Serializer(out, "UTF-8");
        serializer.setIndent(4);
        serializer.write(doc);
        String result = out.toString("UTF-8");
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
          + "<root xml:space=\"preserve\">" +
            "<child xml:space=\"default\"/></root>\r\n",
           result); 
        
    }

    
    public void testXMLSpaceDefaultWithIndentingAndGrandchildren() 
      throws IOException {
        
        root.addAttribute(
          new Attribute(
            "xml:space", 
            "http://www.w3.org/XML/1998/namespace", 
            "preserve"));
        Element child = new Element("child");
        child.addAttribute(
          new Attribute(
            "xml:space", 
            "http://www.w3.org/XML/1998/namespace", 
            "default"));
        root.appendChild(child);
        child.appendChild(new Element("differentLine"));    
        Serializer serializer = new Serializer(out, "UTF-8");
        serializer.setIndent(2);
        serializer.write(doc);
        String result = out.toString("UTF-8");
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
          + "<root xml:space=\"preserve\">" +
            "<child xml:space=\"default\">\r\n    <differentLine/>\r\n" +
            "  </child></root>\r\n",
           result); 
        
    }


    public void testDontSerializeXMLNamespace() throws IOException {
        
        Element root 
          = new Element("html", "http://www.w3.org/1999/xhtml");
        root.addAttribute(
          new Attribute(
            "xml:lang", "http://www.w3.org/XML/1998/namespace", "en"));
        Document doc = new Document(root);
        Serializer serializer = new Serializer(out, "UTF-8");
        serializer.write(doc);
        String result = out.toString("UTF-8");
        assertEquals(-1, result.indexOf("xmlns:xml"));
        assertTrue(result.indexOf("xml:lang=") > 1);
        
    }
    
    public void testDontSerializeNoNamespace() throws IOException { 
        
        Serializer serializer = new Serializer(out, "UTF-8");
        serializer.write(doc);
        String result = out.toString("UTF-8");
        assertEquals(-1, result.indexOf("xmlns="));
        
    }
    
    
    public void testDefaultNamespace() throws IOException { 
        
        Element root = new Element("root", "http://www.example.com");
        Document doc = new Document(root);
        Serializer serializer = new Serializer(out, "UTF-8");
        serializer.write(doc);
        String result = out.toString("UTF-8");
        assertTrue(result.indexOf("xmlns=") > 1);
        assertTrue(result.indexOf("http://www.example.com") > 1);
        
    }
    
    
    public void testEmptyElement() throws IOException {    
        
        Serializer serializer = new Serializer(out, "UTF-8");
        serializer.write(doc);
        String result = out.toString("UTF-8");
        assertEquals(
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<root/>\r\n",
          result
        );
        
    }
    
    
    public void testElementWithText() throws IOException { 
        
        String data = "   test   \n\n   \n  \n hello again";
        root.appendChild(data);
        Serializer serializer = new Serializer(out, "UTF-8");
        serializer.write(doc);
        String result = out.toString("UTF-8");
        
        assertEquals( 
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<root>"
          + data + "</root>\r\n",
          result);  
        
    }    
    
    
    public void testStaticElementWithText() 
      throws IOException {        
        
        String data = "   test   \n\n   \n  \n hello again";
        root.appendChild(data);
        Serializer serializer = new Serializer(out);
        serializer.write(doc);
        String result = out.toString("UTF-8");
        
        assertEquals( 
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<root>"
          + data + "</root>\r\n",
          result);    
        
    }    
    
    
    public void testElementWithTextAndCarriageReturns() 
      throws IOException { 
        
        String data = "   test   \r\n   \n  \r hello again";
        root.appendChild(data);
        Serializer serializer = new Serializer(out, "UTF-8");
        serializer.write(doc);
        String result = out.toString("UTF-8");
        
        assertEquals( 
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<root>"
          + "   test   &#x0D;\n   \n  &#x0D; hello again" 
          + "</root>\r\n",
          result);   
        
    }    
    
    
    private void serializeParseAndCompare(Document doc) 
      throws IOException, ParsingException {
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(out, "UTF-8");
        serializer.write(doc);
        String result = out.toString("UTF-8");
        
        Document resultDoc = parser.build(result, null);
        XOMTestCase.assertEquals(doc, resultDoc);
           
        setOutputStreamSerializeParseAndCompare(doc); 
        
    }

    
    private void setOutputStreamSerializeParseAndCompare(Document doc) 
      throws IOException, ParsingException {
        
        Serializer serializer = new Serializer(out);
        serializer.write(doc);
        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
        serializer.setOutputStream(out2);
        serializer.write(doc);
        String result = out2.toString("UTF-8");
        
        Document resultDoc = parser.build(result, null);
        XOMTestCase.assertEquals(doc, resultDoc); 
        
    }
    
    
    public void testComment() 
      throws IOException, ParsingException {
        
        String data = "  <>&amp;&entity; test   \n  hello again";
        root.appendChild(new Comment(data));
        serializeParseAndCompare(doc);
        
    }
    
    
    public void testProcessingInstruction() 
      throws IOException, ParsingException { 
        
        String data = "<>&amp;&entity; test   \n  hello again";
        root.appendChild(new ProcessingInstruction("target", data));
        serializeParseAndCompare(doc); 
        
    }
    
    public void testBasicElementWithText() 
      throws IOException, ParsingException {  
        
        String data = "   test   \n  hello again";
        root.appendChild(data);
        serializeParseAndCompare(doc); 
        
    }
    
    
    public void testAttributes() 
      throws IOException, ParsingException { 
        
        root.addAttribute(new Attribute("test", "sadlkhasdk"));
        String data = "   test   \n  hello again";
        root.appendChild(data);
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
      throws IOException, ParsingException {  
     
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
      throws IOException, ParsingException { 
        
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
        
        String breaks = 
          "This\nstring\rcontains\r\nseveral\r\rweird line breaks.";
        root.appendChild(breaks);
        root.addAttribute(new Attribute("test", breaks));
            
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
        
        String breaks 
          = "This\nstring\rcontains\r\rseveral\n\nweird line breaks.";
        String breaksHalfEscaped 
          = "This\nstring&#x0D;contains&#x0D;&#x0D;several" +
            "\n\nweird line breaks.";
        String breaksEscaped 
          = "This&#x0A;string&#x0D;contains&#x0D;&#x0D;several" +
            "&#x0A;&#x0A;weird line breaks.";
        root.appendChild(breaks);
            
        Serializer serializer = new Serializer(out, "UTF-8");
        serializer.write(doc);
        String result = out.toString("UTF-8");
        assertTrue(result.indexOf(breaksHalfEscaped) > 0);

        root = new Element("root");
        doc = new Document(root);
        root.addAttribute(new Attribute("test", breaks));
            
        out = new ByteArrayOutputStream();
        serializer = new Serializer(out, "UTF-8");
        serializer.write(doc);
        result = out.toString("UTF-8");
        assertTrue(result.indexOf(breaksEscaped) > 0);        
        
    }
 
    
    public void testPreserveBaseURI() throws IOException {        

        Serializer serializer = new Serializer(out, "UTF-8");
        serializer.setPreserveBaseURI(true);
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

    
    public void testPreserveBaseURIWithChildren() throws IOException {        

        String base = "http://www.example.com/index.xml";
        root.setBaseURI(base);
        Element child = new Element("child");
        child.setBaseURI(base);
        root.appendChild(child);
        Serializer serializer = new Serializer(out, "UTF-8");
        serializer.setPreserveBaseURI(true);
        serializer.write(doc);
        String result = out.toString("UTF-8");
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
          + "<root xml:base=\"" + base + "\"><child/></root>\r\n", result);
        
    } 

    
    public void testPreserveBaseURIDoesntOverrideXMLBase() 
      throws IOException { 
        
        root.addAttribute(new Attribute("xml:base", 
          "http://www.w3.org/XML/1998/namespace", 
          "http://www.cafeconleche.org/"));
        Serializer serializer = new Serializer(out, "UTF-8");
        serializer.setPreserveBaseURI(true);
        serializer.write(doc);
        String result = out.toString("UTF-8");
        assertTrue(result.indexOf("<root") > 1);
        doc.setBaseURI("http://www.example.com/index.xml");
        serializer.write(doc);
        result = out.toString("UTF-8");
        assertTrue(result.indexOf("<root ") > 1);
        assertTrue(result.indexOf("xml:base=") > 1);
        assertTrue(result.indexOf("http://www.cafeconleche.org/") > 1);
        assertEquals(-1, result.indexOf("http://www.example.com/index.xml"));
        
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
        catch (IllegalArgumentException success) {
            assertNotNull(success.getMessage());
        }
        
        try {
            serializer.setLineSeparator("n");
            fail("Allowed illegal separator character");
        }
        catch (IllegalArgumentException success) {
            assertNotNull(success.getMessage());
        }
        
        try {
            serializer.setLineSeparator(" ");
            fail("Allowed illegal separator character");
        }
        catch (IllegalArgumentException success) {
            assertNotNull(success.getMessage());
        }
        
        try {
            serializer.setLineSeparator("rn");
            fail("Allowed illegal separator character");
        }
        catch (IllegalArgumentException success) {
            assertNotNull(success.getMessage());
        }
        
        try {
            serializer.setLineSeparator("<");
            fail("Allowed illegal separator character");
        }
        catch (IllegalArgumentException success) {
            assertNotNull(success.getMessage());
        }
        
        try {
            serializer.setLineSeparator("\u0085");
            fail("Allowed NEL separator character");
        }
        catch (IllegalArgumentException success) {
            assertNotNull(success.getMessage());
        }
        
    }

      
    public void testLowerLimitOfUnicodeInCharacterData() 
      throws IOException {   
        
        root.appendChild("\uD800\uDC00");
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.write(doc);
        String result = out.toString("ISO-8859-1");
        assertTrue(result, result.indexOf("&#x10000;") > 12);
        
    }
    
    
    public void testUpperLimitOfUnicodeInCharacterData() 
      throws IOException {  
        
        root.appendChild("\uDBFF\uDFFD");
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.write(doc);
        String result = out.toString("ISO-8859-1");
        assertTrue(result, result.indexOf("&#x10FFFD;") > 12);
        
    }
      
    
    public void testSerializePlane1CharacterInAttributeValue() 
      throws IOException {        
        
        root.addAttribute(new Attribute("test", "\uD834\uDD1E"));
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.write(doc);
        String result = out.toString("ISO-8859-1");
        assertTrue(result, result.indexOf("&#x1D11E;") > 12);
        
    }
    
    public void testSerializePlane1CharacterInCharacterData() 
      throws IOException {   
        
        root.appendChild("\uD834\uDD1E");
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.write(doc);
        String result = out.toString("ISO-8859-1");
        assertTrue(result, result.indexOf("&#x1D11E;") > 12);
        
    }
    
    
    public void testSurrogatePairCountsAsOneCharacterForColumnCount() 
      throws IOException {   
        
        Element root = new Element("r");
        root.appendChild("\uD834\uDD1E");
        Document doc = new Document(root);
        Serializer serializer = new ColumnSerializer(out);
        serializer.write(doc);
        
    }
    
    
    private static class ColumnSerializer extends Serializer {
     
        
        ColumnSerializer(OutputStream out) {
            super(out);
        }
        
        
        public void write(Document doc) throws IOException {
            
            for (int i = 0; i < doc.getChildCount(); i++) {
                writeChild(doc.getChild(i)); 
            }       
            super.flush();
            assertEquals(8, super.getColumnNumber());
            
        }   

        
    }
    
    
    public void testEscapeAttributeValue() throws IOException {   
        
        root.addAttribute(new Attribute("test", "\u0110"));
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.write(doc);
        String result = out.toString("ISO-8859-1");
        assertTrue(result, result.indexOf("&#x110;") > 5);
        
    }   

    
    public void testLineFeedInAttributeValueWithDefaultOptions() 
      throws IOException, ParsingException {   
        
        root.addAttribute(new Attribute("test", "\n"));
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.write(doc);
        out.close();
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        Document reparsed = parser.build(in);
        assertEquals(doc, reparsed);
        
    }
    
    
    public void testCarriageReturnInAttributeValueWithDefaultOptions()
      throws IOException, ParsingException {  
        
        root.addAttribute(new Attribute("test", "\r"));
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.write(doc);
        out.close();
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        Document reparsed = parser.build(in);
        assertEquals(doc, reparsed);
        
    }
    
    
    public void testCarriageReturnInTextWithDefaultOptions() 
      throws IOException, ParsingException { 
        
        root.appendChild("\r");
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.write(doc);
        out.close();
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        Document reparsed = parser.build(in);
        assertEquals(doc, reparsed);
        
    }
    
    
    public void testTabInAttributeValueWithDefaultOptions() 
      throws IOException, ParsingException { 
        
        root.addAttribute(new Attribute("test", "\t"));
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.write(doc);
        out.close();
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        Document reparsed = parser.build(in);
        assertEquals(doc, reparsed);
        
    }
    
    
    /**
     * <p>
     *   Test that tabs in attribute values are escaped even when
     *   a line separator is set.
     * </p>
     */
    public void testTabInAttributeValueWithLineSeparator() 
      throws IOException, ParsingException {   
        
        root.addAttribute(new Attribute("test", "\t"));
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.setLineSeparator("\r");
        serializer.write(doc);
        out.close();
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        Document reparsed = parser.build(in);
        assertEquals(doc, reparsed);
        
    }
    
    
    
    /**
     * <p>
     *   Test that tabs in attribute values are not escaped when 
     *   indenting.
     * </p>
     */
    public void testTabInAttributeValueWithIndenting() 
      throws IOException, ParsingException { 
      
        root.addAttribute(new Attribute("test", "\t"));
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.setIndent(2);
        serializer.write(doc);
        out.close();
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        Document reparsed = parser.build(in);
        String result = reparsed.getRootElement().getAttributeValue("test");
        assertEquals("Tab not normalized to space", " ", result);
        
    }

    
    public void testCRLFInAttributeValueWithLineSeparatorCR() 
      throws IOException, ParsingException {   
        
        root.addAttribute(new Attribute("test", "\r\n"));
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.setLineSeparator("\r");
        serializer.write(doc);
        out.close();
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        Document reparsed = parser.build(in);
        String result = reparsed.getRootElement().getAttributeValue("test");
        assertEquals("\r", result);
        
    }
    
    
    public void testCRLFInAttributeValueWithLineSeparatorLF() 
      throws IOException, ParsingException {  
        
        root.addAttribute(new Attribute("test", "\r\n"));
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.setLineSeparator("\n");
        serializer.write(doc);
        out.close();
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        Document reparsed = parser.build(in);
        String result = reparsed.getRootElement().getAttributeValue("test");
        assertEquals("\n", result);
        
    }
    
    public void testLFInAttributeValueWithLineSeparatorCRLF() 
      throws IOException, ParsingException {  
        
        root.addAttribute(new Attribute("test", "\n"));
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.setLineSeparator("\r\n");
        serializer.write(doc);
        out.close();
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        Document reparsed = parser.build(in);
        String result = reparsed.getRootElement().getAttributeValue("test");
        assertEquals("\r\n", result);
        
    }

    
    public void testNotEscapeLinefeedInTextContent() 
      throws IOException { 

        root.appendChild("\r\n");
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.write(doc);
        out.close();
        String result = new String(out.toByteArray(), "ISO-8859-1");
        assertEquals(
          "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\r\n<root>&#x0D;\n</root>\r\n", 
          result
        );
        
    }
    
    
    public void testCRLFInAttributeValue() 
      throws IOException, ParsingException {  

        root.addAttribute(new Attribute("test", "a\r\na"));
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.write(doc);
        out.close();
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        Document reparsed = parser.build(in);
        String result = reparsed.getRootElement().getAttributeValue("test");
        assertEquals("CRLF not escaped", "a\r\na", result);
        
    }
    
    
    public void testCRLFInAttributeValueWithIndenting() 
      throws IOException, ParsingException {  

        root.addAttribute(new Attribute("test", "\r\n"));
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.setIndent(2);
        serializer.write(doc);
        out.close();
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        Document reparsed = parser.build(in);
        String result = reparsed.getRootElement().getAttributeValue("test");
        assertEquals("CRLF unnecessarily escaped", -1, result.indexOf('\r'));
        // Need to figure out the serializer should indent this 
        // and write a unit test for that too.
        
    }
    
    
    public void testCRLFInAttributeValueWithMaxLength() 
      throws IOException, ParsingException { 

        root.addAttribute(new Attribute("test", "\r\n"));

        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.setMaxLength(64);
        serializer.write(doc);
        out.close();
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        Document reparsed = parser.build(in);
        String result = reparsed.getRootElement().getAttributeValue("test");
        assertEquals("CRLF unnecessarily escaped", " ", result);
        
    }

    
    public void testCRInTextValueWithLineSeparator() 
      throws IOException, ParsingException {

        root.appendChild("\r");
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.setLineSeparator("\n");
        serializer.write(doc);
        out.close();
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        Document reparsed = parser.build(in);
        String result = reparsed.getValue();
        assertEquals("\n", result);
        
    } 
    
    
    public void testCRLFInTextValueWithLineSeparator() 
      throws IOException, ParsingException {   
        
        root.appendChild("test \r\n test");
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.setLineSeparator("\n");
        serializer.write(doc);
        out.close();
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        Document reparsed = parser.build(in);
        String result = reparsed.getValue();
        assertEquals("test \n test", result);
        
    } 
    
    
    public void testCRInTextWithIndenting() 
      throws IOException, ParsingException {  
        
        root.appendChild("\r");
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.setIndent(2);
        serializer.write(doc);
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
      throws IOException, ParsingException {        

        root.appendChild("\r");
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.setMaxLength(64);
        serializer.write(doc);
        out.close();
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        Document reparsed = parser.build(in);
        String result = reparsed.getValue();
        assertEquals("Carriage return unnecessarily escaped", "\n", result);
        
    }
    
    
    public void testTabInAttributeValueWithMaxLength() 
      throws IOException, ParsingException {  
        
        root.addAttribute(new Attribute("test", "\t"));
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.setMaxLength(64);
        serializer.write(doc);
        out.close();
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        Document reparsed = parser.build(in);
        String result = reparsed.getRootElement().getAttributeValue("test");
        assertEquals("Tab not normalized to space", " ", result);
        
    }

    
    /**
     * <p>
     *   Test that tabs in attribute values are escaped when 
     *   max length is set to 0
     * </p>
     */
    public void testTabInAttributeValueWithZeroMaxLength() 
      throws IOException, ParsingException {  

        root.addAttribute(new Attribute("test", "\t"));
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.setMaxLength(0);
        serializer.write(doc);
        out.close();
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        Document reparsed = parser.build(in);
        String result = reparsed.getRootElement().getAttributeValue("test");
        assertEquals("Tab not normalized to space", "\t", result);
        
    }
    

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
        serializer.setMaxLength(-1);
        assertEquals(0, serializer.getMaxLength());
        
    }

    
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
        try {
            serializer.setIndent(-1);
            fail("Allowed negative indent");
        }
        catch (IllegalArgumentException success) {
            assertNotNull(success.getMessage());
        }
        
    }

    
    public void testLineLength() throws IOException {
          
        int length = 40;        
        String data = "This is a really long string that does not "
         + "contain any line breaks.  However, there is lots of " 
         + "white space so there shouldn't be any trouble wrapping it"
         + " into 40 characters or less per line. ";
        root.appendChild(data);
        Serializer serializer = new Serializer(out, "UTF-8");
        serializer.setMaxLength(length);
        serializer.write(doc);
        String result = out.toString("UTF-8");
        
        BufferedReader reader 
          = new BufferedReader(new StringReader(result)); 
        for (String line = reader.readLine(); 
             line != null;
             line = reader.readLine()) {
            assertTrue(line.length() + ": " + line, 
              line.length() <= length);    
        }
        
    }

    
    public void testLineLengthWithSetOutputStream() 
      throws IOException {
          
        int length = 40;        
        String data = "This is a really long string that does not "
         + "contain any line breaks.  However, there is lots of " 
         + "white space so there shouldn't be any trouble wrapping it"
         + " into 40 characters or less per line. ";
        root.appendChild(data);
        Serializer serializer = new Serializer(new ByteArrayOutputStream(), "UTF-8");
        serializer.setMaxLength(length);
        serializer.write(doc);
        serializer.setOutputStream(out);
        serializer.write(doc);
        String result = out.toString("UTF-8");
        
        BufferedReader reader 
          = new BufferedReader(new StringReader(result)); 
        for (String line = reader.readLine(); 
             line != null;
             line = reader.readLine()) {
            assertTrue(line.length() + ": " + line, 
              line.length() <= length);    
        }
        
    }   
    
    
    public void testPrettyXML() throws IOException {
        
        Element items = new Element("itemSet");
        items.appendChild(new Element("item1"));
        items.appendChild(new Element("item2"));
        Document doc = new Document(items);
        Serializer serializer = new Serializer(out);
        serializer.setIndent(4);
        serializer.write(doc);
        serializer.flush();
        out.close();
        String result = new String(out.toByteArray(), "UTF-8");
        assertEquals(
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
          + "<itemSet>\r\n    <item1/>\r\n    <item2/>\r\n"
          + "</itemSet>\r\n", 
          result
        );
        
    }

    
    public void testIndentAndBreakBeforeComment() throws IOException {
        
        Element items = new Element("itemSet");
        items.appendChild(new Comment("item1"));
        Document doc = new Document(items);
        Serializer serializer = new Serializer(out);
        serializer.setIndent(4);
        serializer.write(doc);
        serializer.flush();
        out.close();
        String result = new String(out.toByteArray(), "UTF-8");
        assertEquals(
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
          + "<itemSet>\r\n    <!--item1-->\r\n"
          + "</itemSet>\r\n", 
          result
        );
        
    }
    
    
    public void testWhiteSpaceBetweenCommentsIsBoundaryWhiteSpace() 
      throws IOException {
        
        Element items = new Element("itemSet");
        items.appendChild(new Comment("item1"));
        items.appendChild("      \r\n              ");
        items.appendChild(new Comment("item2"));
        Document doc = new Document(items);
        Serializer serializer = new Serializer(out);
        serializer.setIndent(4);
        serializer.write(doc);
        serializer.flush();
        out.close();
        String result = new String(out.toByteArray(), "UTF-8");
        assertEquals(
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
          + "<itemSet>\r\n    <!--item1-->\r\n    <!--item2-->\r\n"
          + "</itemSet>\r\n", 
          result
        );
        
    }
    
    
    public void testWhiteSpaceBeforeCommentIsBoundaryWhiteSpace() 
      throws IOException {
        
        Element items = new Element("itemSet");
        items.appendChild("      \r\n              ");
        items.appendChild(new Comment("item1"));
        items.appendChild(new Comment("item2"));
        Document doc = new Document(items);
        Serializer serializer = new Serializer(out);
        serializer.setIndent(4);
        serializer.write(doc);
        serializer.flush();
        out.close();
        String result = new String(out.toByteArray(), "UTF-8");
        assertEquals(
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
          + "<itemSet>\r\n    <!--item1-->\r\n    <!--item2-->\r\n"
          + "</itemSet>\r\n", 
          result
        );
        
    }
    
    
    public void testWhiteSpaceAfterCommentsIsBoundaryWhiteSpace() 
      throws IOException {
        
        Element items = new Element("itemSet");
        items.appendChild(new Comment("item1"));
        items.appendChild(new Comment("item2"));
        items.appendChild("      \r\n              ");
        Document doc = new Document(items);
        Serializer serializer = new Serializer(out);
        serializer.setIndent(4);
        serializer.write(doc);
        serializer.flush();
        out.close();
        String result = new String(out.toByteArray(), "UTF-8");
        assertEquals(
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
          + "<itemSet>\r\n    <!--item1-->\r\n    <!--item2-->\r\n"
          + "</itemSet>\r\n", 
          result
        );
        
    }
    
    
    public void testIndentAndBreakBeforeProcessingInstruction() 
      throws IOException {
        
        Element items = new Element("itemSet");
        items.appendChild(new ProcessingInstruction("target", "value"));
        Document doc = new Document(items);
        Serializer serializer = new Serializer(out);
        serializer.setIndent(4);
        serializer.write(doc);
        serializer.flush();
        out.close();
        String result = new String(out.toByteArray(), "UTF-8");
        assertEquals(
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
          + "<itemSet>\r\n    <?target value?>\r\n"
          + "</itemSet>\r\n", 
          result
        );
        
    }
    
    
    public void testDontBreakLineInElementWithSimpleContent() 
      throws IOException {
        
        Element items = new Element("itemSet");
        Element item1 = new Element("item1");
        items.appendChild(item1);
        item1.appendChild("content");
        Document doc = new Document(items);
        Serializer serializer = new Serializer(out);
        serializer.setIndent(4);
        serializer.write(doc);
        serializer.flush();
        out.close();
        String result = new String(out.toByteArray(), "UTF-8");
        assertEquals(
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
          + "<itemSet>\r\n    <item1>content</item1>\r\n"
          + "</itemSet>\r\n", 
          result
        );
        
    }
    
    
    public void testPrettyXMLWithSetOutputStream() throws IOException {
        
        Element items = new Element("itemSet");
        items.appendChild(new Element("item1"));
        items.appendChild(new Element("item2"));
        Document doc = new Document(items);
        Serializer serializer = new Serializer(new ByteArrayOutputStream());
        serializer.setIndent(4);
        serializer.setLineSeparator("\n");
        serializer.write(doc);
        serializer.setOutputStream(out);
        serializer.write(doc);
        serializer.flush();
        out.close();
        String result = new String(out.toByteArray(), "UTF-8");
        assertEquals(
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
          + "<itemSet>\n    <item1/>\n    <item2/>\n"
          + "</itemSet>\n", 
          result
        );
        
    }
    
    
    public void testAmpersandAndLessThanInText() throws IOException {
        
        root.appendChild("data<data&data");
        Serializer serializer = new Serializer(out);
        serializer.write(doc);
        serializer.flush();
        out.close();
        String result = new String(out.toByteArray(), "UTF-8");
        assertEquals(
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
          + "<root>data&lt;data&amp;data"
          + "</root>\r\n", 
          result
        );
        
    }

    
    public void testAmpersandAndAngleBracketsInAttributeValue() 
      throws IOException {
        
        root.addAttribute(new Attribute("b", "data<data>data&"));
        Serializer serializer = new Serializer(out);
        serializer.write(doc);
        serializer.flush();
        out.close();
        String result = new String(out.toByteArray(), "UTF-8");
        assertEquals(
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
          + "<root b=\"data&lt;data&gt;data&amp;\"/>\r\n", 
          result
        );
        
    }
    
    
    public void testSetNFC() {
        
        Serializer serializer = new Serializer(System.out);
        assertFalse(serializer.getUnicodeNormalizationFormC());
        serializer.setUnicodeNormalizationFormC(true);
        assertTrue(serializer.getUnicodeNormalizationFormC());
        
    }

    
    public void testNFCInElementContent() throws IOException {
        
        Element root = new Element("a");
        root.appendChild("c\u0327"); // c with combining cedilla
        Document doc = new Document(root);
        Serializer serializer = new Serializer(out);
        serializer.setUnicodeNormalizationFormC(true);
        serializer.write(doc);
        serializer.flush();
        out.close();
        String result = new String(out.toByteArray(), "UTF-8");
        assertEquals(
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
          + "<a>\u00E7</a>\r\n", 
          result
        ); 
        
    }

    
    public void testNoNFCByDefault() throws IOException {
        
        Element root = new Element("c\u0327");
        root.appendChild("c\u0327"); // c with combining cedilla
        Document doc = new Document(root);
        Serializer serializer = new Serializer(out);
        serializer.write(doc);
        serializer.flush();
        out.close();
        String result = new String(out.toByteArray(), "UTF-8");
        assertEquals(
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
          + "<c\u0327>c\u0327</c\u0327>\r\n", 
          result
        );  
        
    }


    public void testNFCInAttribute() throws IOException {
        
        root.addAttribute(new Attribute("c\u0327", "c\u0327")); // c with combining cedilla
        Serializer serializer = new Serializer(out);
        serializer.setUnicodeNormalizationFormC(true);
        serializer.write(doc);
        serializer.flush();
        out.close();
        String result = new String(out.toByteArray(), "UTF-8");
        assertEquals(
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
          + "<root \u00E7=\"\u00E7\"/>\r\n", 
          result
        );  
        
    }

    
    public void testNFCInElementName() throws IOException {
        
        Element root = new Element("c\u0327"); // c with combining cedilla
        Document doc = new Document(root);
        Serializer serializer = new Serializer(out);
        serializer.setUnicodeNormalizationFormC(true);
        serializer.write(doc);
        serializer.flush();
        out.close();
        String result = new String(out.toByteArray(), "UTF-8");
        assertEquals(
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
          + "<\u00E7/>\r\n", 
          result
        );   
        
    }

    
    public void testNFCInComment() throws IOException {
        
        Element root = new Element("a"); 
        Document doc = new Document(root);
        doc.insertChild(new Comment("c\u0327hat"), 0); // c with combining cedilla
        Serializer serializer = new Serializer(out);
        serializer.setUnicodeNormalizationFormC(true);
        serializer.write(doc);
        serializer.flush();
        out.close();
        String result = new String(out.toByteArray(), "UTF-8");
        assertEquals(
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
          + "<!--\u00E7hat-->\r\n"
          + "<a/>\r\n", 
          result
        );   
        
    }

    
    public void testNFCInProcessingInstruction() throws IOException {
        
        doc.appendChild(new ProcessingInstruction("c\u0327hat", "c\u0327hat"));
        Serializer serializer = new Serializer(out);
        serializer.setUnicodeNormalizationFormC(true);
        serializer.write(doc);
        serializer.flush();
        out.close();
        String result = new String(out.toByteArray(), "UTF-8");
        assertEquals(
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
          + "<root/>\r\n"
          + "<?\u00E7hat \u00E7hat?>\r\n",
          result
        );   
        
    }

    
    public void testNFCInElementContentWithNonUnicodeEncoding() 
      throws IOException {
        
        root.appendChild("c\u0327"); // c with combining cedilla
        Serializer serializer = new Serializer(out, "ISO-8859-5");
        serializer.setUnicodeNormalizationFormC(true);
        serializer.write(doc);
        serializer.flush();
        out.close();
        String result = new String(out.toByteArray(), "ISO-8859-5");
        assertEquals(
          "<?xml version=\"1.0\" encoding=\"ISO-8859-5\"?>\r\n"
          + "<root>&#xE7;</root>\r\n", 
          result
        );    
        
    }

    
    public void testNFCWithSetOutputStream() 
      throws IOException {
        
        root.appendChild("c\u0327"); // c with combining cedilla
        Serializer serializer = new Serializer(new ByteArrayOutputStream(), "ISO-8859-5");
        serializer.setUnicodeNormalizationFormC(true);
        serializer.write(doc);
        serializer.setOutputStream(out);
        serializer.write(doc);          
        serializer.flush();
        out.close();
        String result = new String(out.toByteArray(), "ISO-8859-5");
        assertEquals(
          "<?xml version=\"1.0\" encoding=\"ISO-8859-5\"?>\r\n"
          + "<root>&#xE7;</root>\r\n", 
          result
        );      
        
    }
    
    
    public void testNullOutputStream() {
        
        try {
            new Serializer(null);
            fail("Allowed null output stream");   
        }   
        catch (NullPointerException success) {
            assertNotNull(success.getMessage());   
        }
        
    }

    
    public void testNullOutputStreamWithEncoding() 
      throws UnsupportedEncodingException {
        
        try {
            new Serializer(null, "UTF-8");
            fail("Allowed null output stream");   
        }   
        catch (NullPointerException success) {
            assertNotNull(success.getMessage());   
        }
        
    }

    
    public void testNullEncoding() 
      throws UnsupportedEncodingException {
        
        try {
            new Serializer(System.out, null);
            fail("Allowed null encoding");   
        }   
        catch (NullPointerException success) {
            assertNotNull(success.getMessage());   
        }
        
    }

    
    // make sure null pointer exception doesn't cause any output
    public void testNullDocument() throws IOException {
        
        Serializer serializer = new Serializer(out, "UTF-16");
        try {
            serializer.write(null);  
            fail("Wrote null document"); 
        }   
        catch (NullPointerException success) {
            // success   
        }
        byte[] result = out.toByteArray();
        assertEquals(0, result.length);
        
    }

    
    public void testGetEncoding() 
      throws UnsupportedEncodingException {
        
        Serializer serializer = new Serializer(System.out, "ISO-8859-1");
        assertEquals("ISO-8859-1", serializer.getEncoding());
        
    }

    
    public void testGetPreserveBaseURI() 
      throws UnsupportedEncodingException {
        
        Serializer serializer = new Serializer(System.out, "ISO-8859-1");
        assertFalse(serializer.getPreserveBaseURI());
        serializer.setPreserveBaseURI(true);
        assertTrue(serializer.getPreserveBaseURI());
        serializer.setPreserveBaseURI(false);
        assertFalse(serializer.getPreserveBaseURI());
        
    }


    public void testSerializeDocTypeWithSystemID() 
      throws IOException {
        
        Serializer serializer = new Serializer(out);
        Document doc = new Document(new Element("a"));
        doc.setDocType(new DocType("b", "example.dtd"));
        serializer.write(doc);
        String result = out.toString("UTF-8");
        assertTrue(result.endsWith("<a/>\r\n"));
        assertTrue(result.indexOf("<!DOCTYPE b SYSTEM \"example.dtd\">") > 0);  
        
    }

    
    public void testSerializeDocTypeWithPublicAndSystemID() 
      throws IOException {
        
        Serializer serializer = new Serializer(out);
        Document doc = new Document(new Element("a"));
        doc.setDocType(new DocType("b", 
          "-//W3C//DTD XHTML 1.0 Transitional//EN", "example.dtd"));
        serializer.write(doc);
        String result = out.toString("UTF-8");
        assertTrue(result.endsWith("<a/>\r\n"));
        assertTrue(result.indexOf(
          "<!DOCTYPE b PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"example.dtd\">") > 0); 
        
    }

    
    public void testSerializeDocTypeWithInternalDTDSubset() 
      throws ParsingException, IOException {
        
        Serializer serializer = new Serializer(out);
        String data = "<!DOCTYPE root [ <!ELEMENT root EMPTY> ]><test/>";   
        Builder builder = new Builder();
        Document doc = builder.build(data, "http://www.example.com");
        serializer.write(doc);
        String result = out.toString("UTF-8");
        assertTrue(result.endsWith("<test/>\r\n"));
        assertTrue(result.indexOf("<!DOCTYPE root [\r\n") > 0);         
        assertTrue(result.indexOf("\r\n]>\r\n") > 0);         
        assertTrue(result.indexOf("<!ELEMENT root EMPTY>\r\n") > 0);  
        
    }

    
    public void testSerializeInternalDTDSubsetContainingUnavailableCharacter() 
      throws ParsingException, IOException {
        
        Serializer serializer = new Serializer(out, "US-ASCII");
        String data = "<!DOCTYPE root ["
          + "<!ELEMENT root EMPTY> " 
          + "<!ATTLIST root attr CDATA 'café creme'> "
          + "]><test/>";   
        Builder builder = new Builder();
        Document doc = builder.build(data, "http://www.example.com");
        try {
            serializer.write(doc);
            fail("How'd you serialize é in ASCII?");
        }
        catch (UnavailableCharacterException success) {
            assertTrue(success.getMessage().indexOf("é (&#xE9;)") > 1);
        }
        
    }

    
    public void testLineBreaksInInternalDTDSubset() 
      throws ParsingException, IOException {
        
        Serializer serializer = new Serializer(out);
        serializer.setLineSeparator("\r");
        String data = "<!DOCTYPE root [ <!ELEMENT root EMPTY> <!ELEMENT data EMPTY> ]><test/>";   
        Builder builder = new Builder();
        Document doc = builder.build(data, "http://www.example.com");
        serializer.write(doc);
        String result = out.toString("UTF-8");
        assertTrue(result.endsWith("<test/>\r"));
        assertTrue(result.indexOf("<!DOCTYPE root [\r") > 0);         
        assertTrue(result.indexOf("\r]>\r") > 0);         
        assertTrue(result.indexOf("<!ELEMENT root EMPTY>\r") > 0);  
        assertTrue(result.indexOf("<!ELEMENT data EMPTY>\r") > 0);  
        assertEquals(-1, result.indexOf("<!ELEMENT data EMPTY>\r\n"));  
        assertEquals(-1, result.indexOf("<!ELEMENT root EMPTY>\r\n"));  
        assertEquals(-1, result.indexOf("<!ELEMENT data EMPTY>\r\r"));  
        assertEquals(-1, result.indexOf("<!ELEMENT root EMPTY>\r\r"));  
        assertEquals(-1, result.indexOf('\n'));  
        
    }

    
    public void testSerializeQuoteInAttributeValue() 
      throws IOException {
        
        Serializer serializer = new Serializer(out);
        root.addAttribute(new Attribute("name", "\""));
        serializer.write(doc);
        String result = out.toString("UTF-8");
        assertTrue(result.endsWith("<root name=\"&quot;\"/>\r\n"));
        
    }

    
    public void testSerializeUnavailableCharacterInMarkup() 
      throws IOException {
        
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        Element root = new Element("\u0419");
        Document doc = new Document(root);
        try {
            serializer.write(doc);
            fail("Wrote bad character: " + out.toString("ISO-8859-1"));
        }
        catch (UnavailableCharacterException success) {
            assertNotNull(success.getMessage()); 
            assertEquals('\u0419', success.getCharacter());
            assertEquals("ISO-8859-1", success.getEncoding());
        }  
        
    }

    
    public void testTurnLineFeedInAttributeValueIntoSpaceWhenIndenting() 
      throws IOException {
        
        Element root = new Element("a");
        root.appendChild("c"); 
        root.addAttribute(new Attribute("name", "value1\nvalue2")); 
        Document doc = new Document(root);
        Serializer serializer = new Serializer(out);
        serializer.setMaxLength(245);
        serializer.setIndent(4);
        serializer.write(doc);
        serializer.flush();
        out.close();
        String result = new String(out.toByteArray(), "UTF-8");
        assertEquals(
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
          + "<a name=\"value1 value2\">c</a>\r\n", 
          result
        );      
        
    }

    
    public void testConflictBetweenMaxLengthAndIndent() 
      throws IOException {
        
        Element root = new Element("a");
        Element b = new Element("b");
        Element c = new Element("c");
        Element d = new Element("d");
        b.appendChild(c);
        root.appendChild(b); 
        c.appendChild(d);
        d.appendChild("data");
        Document doc = new Document(root);
        Serializer serializer = new Serializer(out);
        serializer.setMaxLength(16);
        serializer.setIndent(4);
        serializer.write(doc);
        serializer.flush();
        out.close();
        String result = new String(out.toByteArray(), "UTF-8");
        assertEquals(
          "<?xml version=\"1.0\"\r\nencoding=\"UTF-8\"?>\r\n"
          + "<a>\r\n    <b>\r\n        <c>\r\n        <d>data</d>\r\n        </c>\r\n    </b>\r\n</a>\r\n", 
          result
        );  
        
    }

    
    public void testWriteChild() throws IOException {
                  
        ExposingSerializer serializer = new ExposingSerializer(out, "UTF-8");
        try {
            serializer.writeChild(doc);
            fail("writeChild wrote a document");
        }
        catch (XMLException success) {
            assertNotNull(success.getMessage());
        }
        try {
            serializer.writeChild(new Attribute("name", "value"));
            fail("writeChild wrote an attribute");
        }
        catch (XMLException success) {
            assertNotNull(success.getMessage());
        }
       
    }
    
    
    // just so we can test protected methods
    private static class ExposingSerializer extends Serializer {
        
        ExposingSerializer(OutputStream out, String encoding) 
          throws UnsupportedEncodingException {
            super(out, encoding);   
        }
        
        public void writeChild(Node node) throws IOException {
            super.writeChild(node);
        }
        
        public void exposedWriteRaw(String text) throws IOException {
            writeRaw(text);
        }
        
        public void exposedWriteEscaped(String text) throws IOException {
            writeEscaped(text);
        }
        
        public void exposedWriteAttributeValue(String text) throws IOException {
            writeAttributeValue(text);
        }
        
        public int exposeGetColumnNumber() {
            return super.getColumnNumber();
        }
        
    }

    
    private static class ElementSerializer extends Serializer {
        
        ElementSerializer(OutputStream out, String encoding) 
          throws UnsupportedEncodingException {
            super(out, encoding);   
        }
        
        protected void write(Element element) throws IOException {
            super.write(element);
        }
        
    }

    
    public void testWriteRaw() throws IOException {

        ExposingSerializer serializer = new ExposingSerializer(out, "UTF-8");
        serializer.exposedWriteRaw("<>&\"'");
        assertEquals(5, serializer.exposeGetColumnNumber()); 
        serializer.flush();
        String result = out.toString("UTF-8");
        assertEquals("<>&\"'", result);
        
    }    

    
    public void testWriteParentlessElementInANamespace() throws IOException {

        ElementSerializer serializer = new ElementSerializer(out, "UTF-8");
        Element element = new Element("a", "http://www.example.org");
        serializer.write(element); 
        serializer.flush();
        String result = out.toString("UTF-8");
        assertEquals("<a xmlns=\"http://www.example.org\" />", result);
        
    }    

    
    public void testWriteEscaped() throws IOException {

        ExposingSerializer serializer = new ExposingSerializer(out, "UTF-8");
        serializer.exposedWriteEscaped("<>&\"'");
        assertEquals(15, serializer.exposeGetColumnNumber());   
        serializer.flush();
        byte[] data = out.toByteArray();
        String result = new String(data, "UTF-8");
        assertEquals("&lt;&gt;&amp;\"'", result);
        
    }    

    
    public void testWriteAttributeValue() throws IOException {

        ExposingSerializer serializer = new ExposingSerializer(out, "UTF-8");
        serializer.exposedWriteAttributeValue("<>&\"'");
        assertEquals(20, serializer.exposeGetColumnNumber());   
        serializer.flush();
        String result = out.toString("UTF-8");
        assertEquals("&lt;&gt;&amp;&quot;'", result);
        
    }    
    
    
    // This test case reproduces a bug that
    // showed up while working on SAX conformance testing.
    public void testElementsThatOnlyContainsAnEmptyTextNodeShouldBeOutputWithAnEmptyElementTag() 
      throws IOException {
        
        Element child = new Element("b");
        child.appendChild("");
        root.appendChild(child);
        
        Serializer serializer = new Serializer(out);
        serializer.write(doc);
           
        serializer.flush();
        String result = out.toString("UTF-8");
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
          "<root><b/></root>\r\n", result); 
        
    }
    
    
    // This test case reproduces a bug that
    // showed up while working on SAX conformance testing.
    public void testElementsThatOnlyContainEmptyTextNodesShouldBeOutputWithAnEmptyElementTag() 
      throws IOException {
        
        Element root = new Element("a");
        Element child = new Element("b");
        child.appendChild("");
        child.appendChild("");
        child.appendChild("");
        root.appendChild(child);
        
        Document doc = new Document(root);
        
        Serializer serializer = new Serializer(out);
        serializer.write(doc);
           
        serializer.flush();
        String result = out.toString("UTF-8");
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
          "<a><b/></a>\r\n", result);
        
    }

    
    // This test case reproduces a bug that
    // showed up while working on SAX conformance testing.
    public void testElementsThatOnlyContainASingleSpaceShouldNotBeSplitWhileIndenting() 
      throws IOException {
        
        Element root = new Element("a");
        Element child = new Element("b");
        child.appendChild(" ");
        root.appendChild(child);
        
        Document doc = new Document(root);
        
        Serializer serializer = new Serializer(out);
        serializer.setIndent(4);
        serializer.write(doc);
           
        serializer.flush();
        String result = out.toString("UTF-8");
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
          "<a>\r\n    <b> </b>\r\n</a>\r\n", result);

    }
    
    
    public void testElementsThatOnlyContainTextNodesWithBoundaryWhiteSpaceShouldNotBeSplitWhileIndenting() 
      throws IOException {
        
        Element root = new Element("a");
        Element child = new Element("b");
        child.appendChild(" ");
        child.appendChild(" ");
        root.appendChild(child);
        
        Document doc = new Document(root);
        
        Serializer serializer = new Serializer(out);
        serializer.setIndent(4);
        serializer.write(doc);
           
        serializer.flush();
        String result = out.toString("UTF-8");
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
          "<a>\r\n    <b> </b>\r\n</a>\r\n", result);

    }
    
    
    public void testElementsThatOnlyContainASingleLinefeedShouldNotBeSplitWhileIndenting() 
      throws IOException {
        
        Element root = new Element("a");
        Element child = new Element("b");
        child.appendChild("\n");
        root.appendChild(child);
        
        Document doc = new Document(root);
        
        Serializer serializer = new Serializer(out);
        serializer.setIndent(4);
        serializer.write(doc);
           
        serializer.flush();
        String result = out.toString("UTF-8");
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
          "<a>\r\n    <b> </b>\r\n</a>\r\n", result);

    }
    
    
    public void testEndTagsOfElementsWithContentGoOnSeparateLine() 
      throws ParsingException, IOException {
      
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.setIndent(4);
        serializer.setPreserveBaseURI(true);
        serializer.flush();
    
        File f = new File("data");
        f = new File(f, "prettyxml.xml");
        Builder builder = new Builder();
        Document doc = builder.build(f);
        serializer.write(doc);
        String result = out.toString("UTF-8");
        assertTrue(result.endsWith("\r\n</html>\r\n"));
      
    }

    
    public void testDontDoubleBreak() 
      throws ParsingException, IOException {
      
        Serializer serializer = new Serializer(out, "ISO-8859-1");
        serializer.setIndent(4);
        serializer.setMaxLength(64);

        File f = new File("data");
        f = new File(f, "prettytest.xml");
        Builder builder = new Builder();
        Document doc = builder.build(f);
        serializer.write(doc);
        String result = out.toString("UTF-8");
        assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\r\n" +
          "<html a=\"AReallyLongNameWithNoOpportunitiesToBreakToPutUsPastTheMaxLineLengthAndForceABreak\">\r\n" +    
          "    <head> </head>\r\n" + 
          "</html>\r\n", result);
      
    }

    
}
