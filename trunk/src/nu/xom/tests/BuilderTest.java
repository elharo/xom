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

import java.io.ByteArrayOutputStream;
import java.io.CharConversionException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UTFDataFormatException;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.XMLReaderFactory;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Comment;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.NodeFactory;
import nu.xom.ParsingException;
import nu.xom.ProcessingInstruction;
import nu.xom.Serializer;
import nu.xom.ValidityException;
import nu.xom.WellformednessException;
import nu.xom.XMLException;


/**
 * <p>
 *  Tests building documents from streams, strings, files,
 *  and other input sources.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.1b1
 *
 */
public class BuilderTest extends XOMTestCase {

    
    private File inputDir = new File("data");

     // This class tests error conditions, which Xerces
    // annoyingly logs to System.err. This hides System.err 
    // before each test and restores it after each test.
    private PrintStream systemErr = System.err;
    
    protected void setUp() {
        System.setErr(new PrintStream(new ByteArrayOutputStream()));
    }
    
    
    protected void tearDown() {
        System.setErr(systemErr);
    }
    
       
    // Custom parser to test what happens when parser supplies 
    // malformed data
    private static class CustomReader extends XMLFilterImpl {
        
        public void setFeature(String name, boolean value) {};

        public void parse(InputSource in) throws SAXException  {
            this.getContentHandler().startDocument();
            this.getContentHandler().startElement("87", "87", "87", new AttributesImpl());
            this.getContentHandler().endElement("87", "87", "87");
            this.getContentHandler().endDocument();  
        }
        
    }
    
    
    private static class DoNothingReader extends CustomReader {
        
        public void parse(InputSource in) throws SAXException  {}
        
    }
    

    private static class StartAndEndReader extends CustomReader {
        
        public void parse(InputSource in) throws SAXException  {
            this.getContentHandler().startDocument();
            this.getContentHandler().endDocument();  
        }
        
    }
    

    private static class StartOnlyReader extends CustomReader {
        
        public void parse(InputSource in) throws SAXException  {
            this.getContentHandler().startDocument();
        }
        
    }
    

    private static class EndOnlyReader extends CustomReader {
        
        public void parse(InputSource in) throws SAXException  {
            this.getContentHandler().endDocument();  
        }
        
    }
    

    public BuilderTest(String name) {
        super(name);   
    }
    
    
    // flag to turn on and off tests based on 
    // http://nagoya.apache.org/bugzilla/show_bug.cgi?id=24124
    private boolean xercesBroken = false;
    
    private String elementDeclaration = "<!ELEMENT root (#PCDATA)>";
    private String defaultAttributeDeclaration 
      = "<!ATTLIST test name CDATA \"value\">";
    private String attributeDeclaration 
      = "<!ATTLIST root anattribute CDATA #REQUIRED>";
    private String attributeDeclaration2 
      = "<!ATTLIST root anotherattribute CDATA \"value\">";
    private String unparsedEntityDeclaration 
      = "<!ENTITY hatch-pic SYSTEM " +
        "\"http://www.example.com/images/cup.gif\" NDATA gif>";
    private String unparsedEntityDeclarationPublic
      = "<!ENTITY public-pic PUBLIC \"public ID\" " +
        "\"http://www.example.com/images/cup.gif\" NDATA gif>";
    private String internalEntityDeclaration 
      = "<!ENTITY Pub-Status \"" +
        "This is a pre-release of the specification.\">";
    private String externalEntityDeclarationPublic = 
      "<!ENTITY open-hatch " 
      + "PUBLIC \"-//Textuality//TEXT Standard " +
        "open-hatch boilerplate//EN\" "
      + "\"http://www.textuality.com/boilerplate/OpenHatch.xml\">";
    private String externalEntityDeclarationSystem = 
      "<!ENTITY test SYSTEM " +
      "\"http://www.textuality.com/boilerplate/OpenHatch.xml\">";
    private String notationDeclarationSystem 
     = "<!NOTATION ISODATE SYSTEM "
     + "\"http://www.iso.ch/cate/d15903.html\">";
    private String notationDeclarationPublicAndSystem 
     = "<!NOTATION DATE PUBLIC \"DATE PUBLIC ID\" "
     + "\"http://www.iso.ch/cate/d15903.html\">";
    private String notationDeclarationPublic = "<!NOTATION gif PUBLIC "
    + "\"-//Textuality//TEXT Standard open-hatch boilerplate//EN\">";

    private String source = "<!DOCTYPE test [\r\n"
     + elementDeclaration + "\n" 
     + attributeDeclaration + "\n"
     + defaultAttributeDeclaration + "\n"
     + attributeDeclaration2 + "\n"
     + internalEntityDeclaration + "\n"
     + externalEntityDeclarationPublic + "\n"
     + externalEntityDeclarationSystem + "\n"
     + unparsedEntityDeclaration + "\n"
     + unparsedEntityDeclarationPublic + "\n"
     + notationDeclarationPublic + "\n"
     + notationDeclarationSystem + "\n"
     + notationDeclarationPublicAndSystem + "\n"
     + "]>\r\n"
     + "<?xml-stylesheet href=\"file.css\" type=\"text/css\"?>" 
     + "<!-- test -->"
     + "<test xmlns:xlink='http://www.w3.org/TR/1999/xlink'>Hello dear"
     + "\r\n<em id=\"p1\" xmlns:none=\"http://www.example.com\">"
     + "very important</em>"
     + "<span xlink:type='simple'>here&apos;s the link</span>\r\n"
     + "<svg:svg xmlns:svg='http://www.w3.org/TR/2000/svg'>"
     + "<svg:text>text in a namespace</svg:text></svg:svg>\r\n"
     + "<svg xmlns='http://www.w3.org/TR/2000/svg'><text>text in a " 
     +   "namespace</text></svg></test>\r\n<!--epilog-->";
     
    private String validDoc = "<!DOCTYPE test [\r\n"
     + "<!ELEMENT test (#PCDATA)>\n" 
     + "]>\r\n"
     + "<?xml-stylesheet href=\"file.css\" type=\"text/css\"?>" 
     + "<!-- test -->"
     + "<test>Hello dear</test>"
     + "<!--epilog-->";
     
    private Builder builder = new Builder();
    private Builder validator = new Builder(true);
    private String base = "http://www.example.com/";

    private String attributeDoc = "<!DOCTYPE test [\n"
     + "<!ELEMENT test (#PCDATA)>\n" 
     + "<!NOTATION GIF SYSTEM \"text/gif\">\n"
     + "<!ENTITY data SYSTEM \"http://www.example.org/cup.gif\">\n"
     + "<!ATTLIST test notationatt NOTATION (GIF) \"GIF\">\n" 
     + "<!ATTLIST test cdataatt CDATA \"GIF\">\n" 
     + "<!ATTLIST test entityatt ENTITY \"data\">\n" 
     + "<!ATTLIST test entitiesatt ENTITIES \"data\">\n" 
     + "<!ATTLIST test nmtokenatt NMTOKEN \" 1 \">\n" 
     + "<!ATTLIST test nmtokensatt NMTOKENS \" 1   2  3 \">\n" 
     + "<!ATTLIST test idatt ID \" p1 \">\n" 
     + "<!ATTLIST test idrefatt IDREF \" p1 \">\n" 
     + "<!ATTLIST test idrefsatt IDREFS \" p1 p2 \">\n" 
     + "]>\r\n"
     + "<test>Hello dear</test>";
    
    
    public void testDoNothingParser() 
      throws ParsingException, IOException {
        
        try {
            XMLReader parser = new DoNothingReader();
            Builder builder = new Builder(parser);
            Document doc = builder.build("http://www.example.org/");
            fail("built from bad data");
        }
        catch (ParsingException success) {
            assertNotNull(success.getMessage());
            assertEquals("http://www.example.org/", success.getURI());
        }      
        
    }    


    public void testStartAndEndParser() 
      throws ParsingException, IOException {
        
        XMLReader parser = new StartAndEndReader();
        Builder builder = new Builder(parser);
        Document doc = builder.build("http://www.example.org/");
        assertNotNull(doc.getRootElement());
        
    }    


    public void testStartOnlyParser() 
      throws ParsingException, IOException {
        
        XMLReader parser = new StartOnlyReader();
        Builder builder = new Builder(parser);
        Document doc = builder.build("http://www.example.org/");
        assertNotNull(doc.getRootElement());
        
    }    


    public void testEndOnlyParser() 
      throws ParsingException, IOException {
        
        try {
            XMLReader parser = new EndOnlyReader();
            Builder builder = new Builder(parser);
            Document doc = builder.build("http://www.example.org/");
            fail("built from bad data");
        }
        catch (ParsingException success) {
            assertTrue(success.getCause() instanceof NullPointerException);
        }      
        
    }    


    public void testBuildInternalDTDSubsetWithFixedDefaultAttributeValue() 
      throws ParsingException, IOException {
        
        String doctype = "<!DOCTYPE xsl:stylesheet [\n"
            + "<!ATTLIST a b CDATA #FIXED \"c\">]>";
        String document = doctype + "\n<root/>";
        Builder builder = new Builder();
        Document doc = builder.build(document, null);
        DocType dt = doc.getDocType();
        String internalDTDSubset = dt.getInternalDTDSubset();
        assertTrue(internalDTDSubset.indexOf("#FIXED \"c\"") > 0);
        
    }
    
    
    public void testNotationAttributeType() 
      throws IOException, ParsingException {
        
        Reader reader = new StringReader(attributeDoc);
        Document document = builder.build(reader);
        Element root = document.getRootElement();
        Attribute att = root.getAttribute("notationatt"); 
        assertEquals(Attribute.Type.NOTATION, att.getType()); 
        
    }
    
    
    public void testCDATAAttributeType() 
      throws IOException, ParsingException {
        
        Reader reader = new StringReader(attributeDoc);
        Document document = builder.build(reader);
        Element root = document.getRootElement();
        Attribute att = root.getAttribute("cdataatt"); 
        assertEquals(Attribute.Type.CDATA, att.getType());
        
    }
    
    
    public void testEntityAttributeType() 
      throws IOException, ParsingException {
        
        Reader reader = new StringReader(attributeDoc);
        Document document = builder.build(reader);
        Element root = document.getRootElement();
        Attribute att = root.getAttribute("entityatt"); 
        assertEquals(Attribute.Type.ENTITY, att.getType()); 
        
    }
    
    
    public void testEntitiesAttributeType() 
      throws IOException, ParsingException {
        
        Reader reader = new StringReader(attributeDoc);
        Document document = builder.build(reader);
        Element root = document.getRootElement();
        Attribute att = root.getAttribute("entitiesatt"); 
        assertEquals(Attribute.Type.ENTITIES, att.getType());

    }
    
    
    public void testNameTokenAttributeType() 
      throws IOException, ParsingException {
        
        Reader reader = new StringReader(attributeDoc);
        Document document = builder.build(reader);
        Element root = document.getRootElement();
        Attribute att = root.getAttribute("nmtokenatt"); 
        assertEquals(Attribute.Type.NMTOKEN, att.getType());      
        assertEquals("1", att.getValue());

    }
    
    
    // I'm specifically worried about a Xerces runtime MalformedURIException here
    public void testIllegalSystemIDThrowsRightException() {
        
        String document = "<!DOCTYPE root SYSTEM \"This is not a URI\"><root/>";
        try{
            builder.build(document, null);
        }
        catch (Exception ex) {
            assertTrue(ex instanceof ParsingException);
        }
            
    }
    
    
    public void testNameTokensAttributeType() 
      throws IOException, ParsingException {
        
        Reader reader = new StringReader(attributeDoc);
        Document document = builder.build(reader);
        Element root = document.getRootElement();
        Attribute att = root.getAttribute("nmtokensatt"); 
        assertEquals(Attribute.Type.NMTOKENS, att.getType());      
        assertEquals("1 2 3", att.getValue()); 
        
    }
    
    
    // verify that XML 1.1 is not supported
    public void testXML11() throws IOException {
        
        String data = "<?xml version='1.1'?><root/>";
        try {
            builder.build(data, "http://www.example.com");
            fail("XML 1.1 allowed");
        }
        catch (ParsingException ex) {
            assertNotNull(ex.getMessage());   
        }
        
    }
    
    
    // verify that XML 1.2 is not supported
    public void testXML12() throws IOException {
        
        String data = "<?xml version='1.2'?><root/>";
        try {
            builder.build(data, "http://www.example.com");
            fail("XML 1.2 allowed");
        }
        catch (ParsingException ex) {
            assertNotNull(ex.getMessage());   
        }
        
    }
    
    
    // verify that XML 2.0 is not supported
    public void testXML20() throws IOException {
        
        String data = "<?xml version='2.0'?><root/>";
        try {
            builder.build(data, "http://www.example.com");
            fail("XML 2.0 allowed");
        }
        catch (ParsingException ex) {
            assertNotNull(ex.getMessage());   
        }
        
    }
    
    
    public void testIDAttributeType() 
      throws IOException, ParsingException {
        
        Reader reader = new StringReader(attributeDoc);
        Document document = builder.build(reader);
        Element root = document.getRootElement();
        Attribute att = root.getAttribute("idatt"); 
        assertEquals(Attribute.Type.ID, att.getType());      
        assertEquals("p1", att.getValue()); 

    }
    
    
    public void testIDREFAttributeType() 
      throws IOException, ParsingException {
        
        Reader reader = new StringReader(attributeDoc);
        Document document = builder.build(reader);
        Element root = document.getRootElement();
        Attribute att = root.getAttribute("idrefatt"); 
        assertEquals(Attribute.Type.IDREF, att.getType());      
        assertEquals("p1", att.getValue()); 

    }
    
    
    public void testIDREFSAttributeType() 
      throws IOException, ParsingException {
        
        Reader reader = new StringReader(attributeDoc);
        Document document = builder.build(reader);
        Element root = document.getRootElement();
        Attribute att = root.getAttribute("idrefsatt"); 
        assertEquals(Attribute.Type.IDREFS, att.getType());      
        assertEquals("p1 p2", att.getValue()); 
        
    }

    
    public void testBuildFromReader() 
      throws IOException, ParsingException {
        
        Reader reader = new StringReader(source);
        Document document = builder.build(reader);
        verify(document);        
        assertEquals("", document.getBaseURI());
        
    }
    
    
    public void testBuildFromReaderWithBase()
      throws IOException, ParsingException {
        
        Reader reader = new StringReader(source);
        Document document = builder.build(reader, base);
        verify(document);        
        assertEquals(base, document.getBaseURI());
        
    }

 
    private static class NoLocator extends XMLFilterImpl {

        public NoLocator(XMLReader reader) {
            super(reader);
        }
        
        public void setDocumentLocator(Locator locator) {}
        
    }    
    
    
    public void testBuildWithoutLocator()
      throws IOException, ParsingException, SAXException {
        
        XMLReader xerces = XMLReaderFactory.createXMLReader(
          "org.apache.xerces.parsers.SAXParser");
        XMLReader filter = new NoLocator(xerces);
        
        Builder builder = new Builder(filter);
        Document document = builder.build(source, "http://www.example.org/");
        verify(document);
        assertEquals("http://www.example.org/", document.getBaseURI());
        
    }

    
    private static class WeirdAttributeTypes extends XMLFilterImpl {

        public WeirdAttributeTypes(XMLReader reader) {
            super(reader);
        }
        
        public void startElement(String uri, String localName,
          String qualifiedName, Attributes atts) throws SAXException {
            
            AttributesImpl newAtts = new AttributesImpl(atts);
            for (int i = 0; i < newAtts.getLength(); i++) {
                newAtts.setType(i, "WEIRD");
            }
            
            super.startElement(uri, localName, qualifiedName, newAtts);
            
        }
        
    }    

    
    public void testWeirdAttributeTypes()
      throws IOException, ParsingException, SAXException {
        
        XMLReader xerces = XMLReaderFactory.createXMLReader(
          "org.apache.xerces.parsers.SAXParser");
        XMLReader filter = new WeirdAttributeTypes(xerces);
        
        Builder builder = new Builder(filter);
        Document document = builder.build(attributeDoc, "http://www.example.org/");
        Element root = document.getRootElement();
        assertTrue(root.getAttributeCount() > 0);
        for (int i = 0; i < root.getAttributeCount(); i++) {
            assertEquals(Attribute.Type.UNDECLARED, root.getAttribute(i).getType());
        }
        
    }
    
    
    // Here we're faking the non-standard behavior of some older parsers
    private static class ParenthesizedEnumeratedAttributeTypes extends XMLFilterImpl {

        public ParenthesizedEnumeratedAttributeTypes(XMLReader reader) {
            super(reader);
        }
        
        public void startElement(String uri, String localName,
          String qualifiedName, Attributes atts) throws SAXException {
            
            AttributesImpl newAtts = new AttributesImpl(atts);
            for (int i = 0; i < newAtts.getLength(); i++) {
                newAtts.setType(i, "(test, data, value)");
            }
            
            super.startElement(uri, localName, qualifiedName, newAtts);
            
        }
        
    }    

    
    public void testParenthesizedEnumeratedAttributeTypes()
      throws IOException, ParsingException, SAXException {
        
        XMLReader xerces = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
        XMLReader filter = new ParenthesizedEnumeratedAttributeTypes(xerces);
        
        Builder builder = new Builder(filter);
        Document document = builder.build(attributeDoc, "http://www.example.org/");
        Element root = document.getRootElement();
        assertTrue(root.getAttributeCount() > 0);
        for (int i = 0; i < root.getAttributeCount(); i++) {
            assertEquals(Attribute.Type.ENUMERATION, root.getAttribute(i).getType());
        }
        
    }
    
    
    public void testBuildFromInputStreamWithBase()
      throws IOException, ParsingException {
        InputStream in = new ByteArrayInputStream(source.getBytes("UTF-8"));
        Document document = builder.build(in, base);
        verify(document);        
        assertEquals(base, document.getBaseURI());    
    }
    
    
    public void testBuildFromInputStreamWithoutBase()
      throws IOException, ParsingException {
        InputStream in = new ByteArrayInputStream(source.getBytes("UTF-8"));
        Document document = builder.build(in);
        verify(document);        
        assertEquals("", document.getBaseURI());
    }

    
    public void testBuildFromStringWithBase()
      throws IOException, ParsingException {
        Document document = builder.build(source, base);
        verify(document);       
        assertEquals(base, document.getBaseURI());  
    }
    
    
    public void testBuildDocumentThatUsesDoubleQuoteNumericCharacterReferenceInEntityDeclaration()
      throws IOException, ParsingException {
        
        String data = "<!DOCTYPE doc [\n"
            + "<!ELEMENT doc (#PCDATA)>"
            + " <!ENTITY e \"&#34;\">\n"
            + "]><root />";
        
        Document document = builder.build(data, null);
 
        Document roundtrip = builder.build(document.toXML(), null);
        assertEquals(document, roundtrip);
        
    }
    
    
    public void testBuildDocumentThatDeclaresStandardEntityReferences()
      throws IOException, ParsingException {
        
        String data = "<!DOCTYPE doc [\n"
            + "<!ELEMENT doc (#PCDATA)>"
            + "<!ENTITY lt     \"&#38;#60;\">\n"
            + "<!ENTITY gt     \"&#62;\">\n"
            + "<!ENTITY amp    \"&#38;#38;\">\n"
            + "<!ENTITY apos   \"&#39;\">\n"
            + "<!ENTITY quot   \"&#34;\">\n"
            + "]><root />";
        
        Document document = builder.build(data, null);
        Document roundtrip = builder.build(document.toXML(), null);
        assertEquals(document, roundtrip);
        
    }
    
    
    public void testBuildDocumentThatUsesAmpersandNumericCharacterReferenceInEntityDeclaration()
      throws IOException, ParsingException {
        
        String data = "<!DOCTYPE doc [\n"
            + "<!ELEMENT doc (#PCDATA)>"
            + " <!ENTITY e \"&#x26;\">\n"
            + "]><root />";
        
        Document document = builder.build(data, null);
 
        Document roundtrip = builder.build(document.toXML(), null);
        assertEquals(document, roundtrip);
        
    }
    
    
    public void testBuildDocumentThatUsesDoubleQuoteNumericCharacterReferenceInAttributeDeclaration()
      throws IOException, ParsingException {
        
        String data = "<!DOCTYPE doc [\n"
            + "<!ATTLIST root test (CDATA) \"&#x34;\">\n"
            + "]><root />";
        
        Document document = builder.build(data, null);
 
        Document roundtrip = builder.build(document.toXML(), null);
        assertEquals(document, roundtrip);
        
    }
    
    
    public void testBuildFromInvalidDoc()
      throws IOException, ParsingException {
        
        try {
            validator.build(source, base);
            fail("Built invalid doc");
        }
        catch (ValidityException ex) {
            Document document = ex.getDocument();
            // Can't do a full verify just yet due to bugs in Xerces
            // verify(ex.getDocument());
            assertTrue(document.getChild(1) instanceof ProcessingInstruction);
            assertTrue(document.getChild(2) instanceof Comment);        
            DocType doctype = document.getDocType();
            Element root = document.getRootElement();
    
            // assertEquals(1, root.getAttributeCount());
            // assertEquals("value", root.getAttributeValue("name"));
            assertEquals("test", root.getQualifiedName());
            assertEquals("test", root.getLocalName());
            assertEquals("", root.getNamespaceURI());
            
            assertTrue(doctype != null);
            assertTrue(document.getChild(0) instanceof DocType);
            assertTrue(document.getChild(4) instanceof Comment);
            assertTrue(document.getChild(2) instanceof Comment);
            assertEquals(" test ", document.getChild(2).getValue());
            assertEquals("epilog", document.getChild(4).getValue());
            assertTrue(document.getChild(1) instanceof ProcessingInstruction);
            assertEquals("test", doctype.getRootElementName());
            assertNull(doctype.getPublicID());
            assertNull(doctype.getSystemID());
            
            String internalDTDSubset = doctype.getInternalDTDSubset();
            assertTrue(
              internalDTDSubset, 
              internalDTDSubset.indexOf(elementDeclaration) > 0
            );
            assertTrue(
              internalDTDSubset, 
              internalDTDSubset.indexOf(attributeDeclaration) > 0
            );
            assertTrue(
              internalDTDSubset, 
              internalDTDSubset.indexOf(attributeDeclaration2) > 0
            );
            assertTrue(
              internalDTDSubset, 
              internalDTDSubset.indexOf(internalEntityDeclaration) > 0
            );
            assertTrue(
              internalDTDSubset, 
              internalDTDSubset.indexOf(externalEntityDeclarationPublic) > 0
            );
            assertTrue(
              internalDTDSubset, 
              internalDTDSubset.indexOf(externalEntityDeclarationSystem) > 0
            );
            assertTrue(
              internalDTDSubset,
              internalDTDSubset.indexOf(unparsedEntityDeclaration) > 0
            );
            assertTrue(
              internalDTDSubset,
              internalDTDSubset.indexOf(unparsedEntityDeclarationPublic) > 0
            );
            assertTrue(
              internalDTDSubset, 
              internalDTDSubset.indexOf(notationDeclarationPublic) > 0
            );
            assertTrue(
              internalDTDSubset, 
              internalDTDSubset.indexOf(notationDeclarationSystem) > 0
            );
            assertTrue(
              internalDTDSubset, 
              internalDTDSubset.indexOf(notationDeclarationPublicAndSystem) > 0
            );
            
        }  
        
    }
    
    
    public void testBuildFromStringWithNullBase()
      throws IOException, ParsingException {
        Document document = builder.build(source, null);
        verify(document);        
        assertEquals("", document.getBaseURI());    
    }
    
    
    private void verify(Document document) {
        
        assertTrue(document.getChild(1) instanceof ProcessingInstruction);
        assertTrue(document.getChild(2) instanceof Comment);        
        DocType doctype = document.getDocType();
        Element root = document.getRootElement();

        assertEquals(1, root.getAttributeCount());
        assertEquals("value", root.getAttributeValue("name"));
        assertEquals("test", root.getQualifiedName());
        assertEquals("test", root.getLocalName());
        assertEquals("", root.getNamespaceURI());
        
        assertTrue(doctype != null);
        assertTrue(document.getChild(0) instanceof DocType);
        assertTrue(document.getChild(4) instanceof Comment);
        assertTrue(document.getChild(2) instanceof Comment);
        assertEquals(" test ", document.getChild(2).getValue());
        assertEquals("epilog", document.getChild(4).getValue());
        assertTrue(document.getChild(1) instanceof ProcessingInstruction);
        assertEquals("test", doctype.getRootElementName());
        assertNull(doctype.getPublicID());
        assertNull(doctype.getSystemID());
        
        String internalDTDSubset = doctype.getInternalDTDSubset();
        assertTrue(
          internalDTDSubset, 
          internalDTDSubset.indexOf(elementDeclaration) > 0
        );
        assertTrue(
          internalDTDSubset, 
          internalDTDSubset.indexOf(attributeDeclaration) > 0
        );
        assertTrue(
          internalDTDSubset, 
          internalDTDSubset.indexOf(attributeDeclaration2) > 0
        );
        assertTrue(
          internalDTDSubset, 
          internalDTDSubset.indexOf(internalEntityDeclaration) > 0
        );
        assertTrue(
          internalDTDSubset, 
          internalDTDSubset.indexOf(externalEntityDeclarationPublic) > 0
        );
        assertTrue(
          internalDTDSubset, 
          internalDTDSubset.indexOf(externalEntityDeclarationSystem) > 0
        );
        assertTrue(
          internalDTDSubset,
          internalDTDSubset.indexOf(unparsedEntityDeclaration) > 0
        );
        assertTrue(
          internalDTDSubset,
          internalDTDSubset.indexOf(unparsedEntityDeclarationPublic) > 0
        );
        assertTrue(
          internalDTDSubset, 
          internalDTDSubset.indexOf(notationDeclarationPublic) > 0
        );
        assertTrue(
          internalDTDSubset, 
          internalDTDSubset.indexOf(notationDeclarationSystem) > 0
        );
        assertTrue(
          internalDTDSubset, 
          internalDTDSubset.indexOf(notationDeclarationPublicAndSystem) > 0
        );
               
    }

    
    public void testValidateFromReader() 
      throws IOException, ParsingException {
        
        Reader reader1 = new StringReader(validDoc);
        Document document1 = validator.build(reader1);       
        assertEquals("", document1.getBaseURI());
        Reader reader2 = new StringReader(validDoc);
        Document document2 = builder.build(reader2);  
        assertEquals(document2, document1); 
        
    }

    
    public void testDocumentWithDefaultNamespaceOnPrefixedElement()
      throws IOException, ParsingException {
        
        Reader reader = new StringReader("<pre:root " +
                "xmlns='http://www.example.org/' " +
                "xmlns:pre='http://www.cafeconleche.org/'/>");
        Document document = builder.build(reader); 
        Element root = document.getRootElement();
        assertEquals("http://www.example.org/", root.getNamespaceURI(""));    
        assertEquals("http://www.cafeconleche.org/", root.getNamespaceURI("pre"));    
        assertEquals("http://www.cafeconleche.org/", root.getNamespaceURI());   
        
    } 
    
    
    public void testValidateFromReaderWithBase()
      throws IOException, ParsingException {
        
        Reader reader = new StringReader(validDoc);
        Document document = validator.build(reader, base); 
        assertEquals(base, document.getBaseURI());
        Reader reader2 = new StringReader(validDoc);
        Document document2 = builder.build(reader2);  
        assertEquals(document2, document);    
        
    }
    
    
    public void testValidateFromInputStreamWithBase()
      throws IOException, ParsingException {
        
        InputStream in = new ByteArrayInputStream(validDoc.getBytes("UTF-8"));
        Document document = validator.build(in, base);  
        assertEquals(base, document.getBaseURI());  
        Reader reader2 = new StringReader(validDoc);
        Document document2 = builder.build(reader2);  
        assertEquals(document2, document);     
        
    }
    
    
    public void testValidateInSeries()
      throws IOException, ParsingException {
          
        try {
            Reader reader = new StringReader(source);
            validator.build(reader);   
            fail("Allowed invalid doc");
        }
        catch (ValidityException success) {
            assertNotNull(success.getMessage());   
        }  
        // now make sure validating a valid document doesn't
        // throw an exception
        InputStream in = new ByteArrayInputStream(validDoc.getBytes("UTF-8"));
        validator.build(in, base);   
        
    }
    
    
    public void testValidateFromInputStreamWithoutBase()
      throws IOException, ParsingException {
        
        InputStream in = new ByteArrayInputStream(validDoc.getBytes("UTF-8"));
        Document document = validator.build(in);        
        assertEquals("", document.getBaseURI());
        Reader reader2 = new StringReader(validDoc);
        Document document2 = builder.build(reader2);  
        assertEquals(document2, document);  
        
    }

    
    public void testValidateFromStringWithBase()
      throws IOException, ParsingException {
        
        Document document = validator.build(validDoc, base);        
        assertEquals(base, document.getBaseURI());  
        Reader reader2 = new StringReader(validDoc);
        Document document2 = builder.build(reader2);  
        assertEquals(document2, document);     
        
    }
    
    
    public void testValidateWithCrimson()
      throws IOException, ParsingException {
        
        XMLReader crimson;
        try {
            crimson = XMLReaderFactory.createXMLReader(
              "org.apache.crimson.parser.XMLReaderImpl");
        } 
        catch (SAXException ex) {
            // can't test Crimson if you can't load it
            return;
        }
        Builder validator = new Builder(crimson, true);
        Document document = validator.build(validDoc, base);        
        assertEquals(base, document.getBaseURI());  
        Reader reader2 = new StringReader(validDoc);
        Document document2 = builder.build(reader2);  
        assertEquals(document2, document);    
        
    }

    
    public void testNotationAttributeTypeWithCrimson()
      throws IOException, ParsingException {
        
        XMLReader crimson;
        try {
            crimson = XMLReaderFactory.createXMLReader(
              "org.apache.crimson.parser.XMLReaderImpl");
        } 
        catch (SAXException ex) {
            // can't test Crimson if you can't load it
            return;
        }
        
        String data = " <!DOCTYPE doc [\n"
            + "<!ATTLIST e a NOTATION (n) #IMPLIED>\n"
            + "<!ELEMENT document (e)*>\n"
            + "<!ELEMENT e (#PCDATA)>\n"
            + "<!NOTATION n PUBLIC \"whatever\">"
            + "]><document />";
        
        Builder builder = new Builder(crimson);
        Document document = builder.build(data, base); 
        
        String s = document.toXML();
        Document roundTrip = builder.build(s, base);
        assertEquals(document, roundTrip);
        
    }
   
    
    public void testEnumerationAttributeType()
      throws IOException, ParsingException {
        
        XMLReader crimson;
        try {
            crimson = XMLReaderFactory.createXMLReader(
              "org.apache.crimson.parser.XMLReaderImpl");
        } 
        catch (SAXException ex) {
            // can't test Crimson if you can't load it
            return;
        }
        Builder builder = new Builder(crimson, false);
        String doc = "<!DOCTYPE root [" +
                "<!ATTLIST root att (yes | no) #IMPLIED>" +
                "]><root att='yes'/>";
        Document document = builder.build(doc, base); 
        Element root = document.getRootElement();
        Attribute att = root.getAttribute(0);
        assertEquals(Attribute.Type.ENUMERATION, att.getType());
        
    }

    
    public void testWarningDoesNotStopBuild()
      throws IOException, ParsingException, SAXException {
        
        XMLReader xerces;
        try {
            xerces = XMLReaderFactory.createXMLReader(
              "org.apache.xerces.parsers.SAXParser");
        } 
        catch (SAXException ex) {
            // can't test Xerces if you can't load it
            return;
        }
        // This document generates a warning due to the duplicate
        // attribute declaration
        xerces.setFeature(
          "http://apache.org/xml/features/validation/warn-on-duplicate-attdef", 
          true);
        Builder builder = new Builder(xerces, true);
        Document document = builder.build("<!DOCTYPE root [" +
                "<!ELEMENT root ANY>" +
                "<!ATTLIST root b CDATA #IMPLIED>" +
                "<!ATTLIST root b NMTOKEN #REQUIRED>" +
                "]><root b='test'/>", base); 
        // The main test is that the document is built successfully.
        assertEquals(2, document.getChildCount());
        assertEquals("root", document.getRootElement().getQualifiedName());
        
    }   
    
    
    private static class EntitySkipper extends XMLFilterImpl {

        public EntitySkipper(XMLReader reader) {
            super(reader);
        }
        
        public void characters(char[] data, int start, int length) 
          throws SAXException {
            super.skippedEntity("name");
        }
        
    }
    
    
    public void testSkippedEntityThrowsParsingException()
      throws IOException, ParsingException, SAXException {
        
        XMLReader xerces = XMLReaderFactory.createXMLReader(
          "org.apache.xerces.parsers.SAXParser");
        XMLReader filter = new EntitySkipper(xerces);
        
        Builder builder = new Builder(filter, true);
        try {
            builder.build("<root>replace</root>", base); 
            fail("Allowed skipped entity");
        }
        catch (ParsingException success) {
            assertNotNull(success.getMessage());
        }   
        
    }   
    
    
    public void testValidateFromStringWithNullBase()
      throws IOException, ParsingException {
        Document document = validator.build(validDoc, null);    
        assertEquals("", document.getBaseURI());  
        Reader reader2 = new StringReader(validDoc);
        Document document2 = builder.build(reader2);  
        assertEquals(document2, document);     
    }


    public void testCannotBuildNamespaceMalformedDocument()
      throws IOException {
        
        try {
            builder.build("<root:root/>", null);
            fail("Builder allowed undeclared prefix");
        }
        catch (ParsingException success) {
            assertNotNull(success.getMessage());   
        }   
        
    }

    
    public void testInvalidDocFromReader() 
      throws IOException, ParsingException {
        
        Reader reader = new StringReader(source);
        try {
            validator.build(reader);   
            fail("Allowed invalid doc");
        }
        catch (ValidityException success) {
            assertNotNull(success.getMessage());
            assertTrue(success.getErrorCount() > 0);
            for (int i = 0; i < success.getErrorCount(); i++) {
                assertNotNull(success.getValidityError(i));   
                assertTrue(success.getLineNumber(i) >= -1);   
                assertTrue(success.getColumnNumber(i) >= -1);   
            }   
            if (!xercesBroken) {
                Document doc = builder.build(new StringReader(source)); 
                this.verify(success.getDocument());
                assertEquals(doc, success.getDocument());
            }
        }
        
    }
    
    
    public void testNamespaceMalformedDocumentWithCrimson() 
      throws IOException {
        
        StringReader reader = new StringReader("<root:root/>");
        XMLReader crimson;
        try {
            crimson = XMLReaderFactory.createXMLReader(
              "org.apache.crimson.parser.XMLReaderImpl");
        }
        catch (SAXException ex) {
           // No Crimson in classpath; therefore can't test it
           return;
        }
        Builder builder = new Builder(crimson);
        try {
            builder.build(reader);   
            fail("Crimson allowed namespace malformed doc");
        }
        catch (ParsingException success) {
            assertNotNull(success.getMessage());
        }      
        
    }

    
    public void testValidateNamespaceMalformedInvalidDocumentWithCrimson() 
      throws IOException {
        
        StringReader reader = new StringReader("<!DOCTYPE root [" +
                "<!ELEMENT root (a)>\n" +
                "<!ELEMENT a (#PCDATA)> \n" +
                "]>\n" +
                "<root><b:b /></root>");
        XMLReader crimson;
        try {
            crimson = XMLReaderFactory.createXMLReader(
              "org.apache.crimson.parser.XMLReaderImpl");
        }
        catch (SAXException ex) {
           // No Crimson in classpath; therefore can't test it
           return;
        }
        Builder builder = new Builder(crimson);
        try {
            builder.build(reader);   
            fail("Crimson allowed namespace malformed doc");
        }
        catch (ValidityException ex) {
            fail("Crimson should have thrown ParsingException instead");
        }      
        catch (ParsingException success) {
            assertNotNull(success.getMessage());
        }      
        
    }
    
    
    public void testInvalidDocFromReaderWithBase()
      throws IOException, ParsingException {
        
        Reader reader1 = new StringReader(source);
        try {
            validator.build(reader1, base); 
            fail("Allowed invalid doc");
        }
        catch (ValidityException ex) {
            assertNotNull(ex.getMessage()); 
            assertEquals(base, ex.getURI());
            assertTrue(ex.getErrorCount() > 0);
            for (int i = 0; i < ex.getErrorCount(); i++) {
                assertNotNull(ex.getValidityError(i));   
                assertTrue(ex.getLineNumber(i) >= -1);   
                assertTrue(ex.getColumnNumber(i) >= -1);   
            }   
            if (!xercesBroken) {
                Document doc = builder.build(new StringReader(source), base); 
                this.verify(ex.getDocument());
                assertEquals(doc, ex.getDocument());
            }
        }
        
    }
 
    
    public void testInvalidDocFromInputStreamWithBase()
      throws IOException, ParsingException {
        
        InputStream in = new ByteArrayInputStream(source.getBytes("UTF-8"));
        try {
            validator.build(in, base);  
            fail("Allowed invalid doc");
        }
        catch (ValidityException ex) {
            assertNotNull(ex.getMessage());  
            assertEquals(base, ex.getURI());
            assertTrue(ex.getErrorCount() > 0);
            for (int i = 0; i < ex.getErrorCount(); i++) {
                assertNotNull(ex.getValidityError(i));
                assertTrue(ex.getLineNumber(i) >= -1);   
                assertTrue(ex.getColumnNumber(i) >= -1);   
            }   
            if (!xercesBroken) {
                Document doc = builder.build(
                  new ByteArrayInputStream(source.getBytes("UTF-8")), base
                ); 
                this.verify(ex.getDocument());
                assertEquals(doc, ex.getDocument());
            }
        }
        
    }
    
    
    public void testInvalidDocFromInputStreamWithoutBase()
      throws IOException, ParsingException {
        
        InputStream in = new ByteArrayInputStream(source.getBytes("UTF-8"));
        try {
            validator.build(in);        
            fail("Allowed invalid doc");
        }
        catch (ValidityException ex) {
            assertNotNull(ex.getMessage());   
            assertTrue(ex.getErrorCount() > 0);
            for (int i = 0; i < ex.getErrorCount(); i++) {
                assertNotNull(ex.getValidityError(i));   
                assertTrue(ex.getLineNumber(i) >= -1);   
                assertTrue(ex.getColumnNumber(i) >= -1);   
            }   
            if (!xercesBroken) {
                Document doc = builder.build(
                  new ByteArrayInputStream(source.getBytes("UTF-8"))
                ); 
                this.verify(ex.getDocument());
                assertEquals(doc, ex.getDocument());
            }
        }
        
    }

    
    public void testInvalidDocFromStringWithBase()
      throws IOException, ParsingException {
        
        try {
            validator.build(source, base);        
            fail("Allowed invalid doc");
        }
        catch (ValidityException ex) {
            assertNotNull(ex.getMessage()); 
            assertEquals(base, ex.getURI());
            assertTrue(ex.getErrorCount() > 0);
            for (int i = 0; i < ex.getErrorCount(); i++) {
                assertNotNull(ex.getValidityError(i));   
                assertTrue(ex.getLineNumber(i) >= -1);   
                assertTrue(ex.getColumnNumber(i) >= -1);   
            }   
            if (!xercesBroken) {
                Document doc = builder.build(source, base); 
                this.verify(ex.getDocument());
                assertEquals(doc, ex.getDocument());
            }
        }
        
    }
    

    public void testInvalidDocWithCrimson()
      throws IOException, ParsingException {
        
        XMLReader crimson;
        try {
            crimson = XMLReaderFactory.createXMLReader(
              "org.apache.crimson.parser.XMLReaderImpl");
        } 
        catch (SAXException ex) {
            // can't test Crimson if you can't load it
            return;
        }
        Builder validator = new Builder(crimson, true);
        try {
            validator.build(source, null);    
            fail("Allowed invalid doc");
        }
        catch (ValidityException ex) {
            assertTrue(ex.getErrorCount() > 0);
            assertNull(ex.getURI());
            for (int i = 0; i < ex.getErrorCount(); i++) {
                assertNotNull(ex.getValidityError(i));   
            }    
        }
        
    }         

    
    public void testInvalidDocFromStringWithNullBase()
      throws IOException, ParsingException {
        
        try {
            validator.build(source, null);    
            fail("Allowed invalid doc");
        }
        catch (ValidityException ex) {
            assertTrue(ex.getErrorCount() > 0);
            assertNull(ex.getURI());
            for (int i = 0; i < ex.getErrorCount(); i++) {
                assertNotNull(ex.getValidityError(i));   
            }   
            if (!xercesBroken) {
                Document doc = builder.build(source, null); 
                this.verify(ex.getDocument());
                assertEquals(doc, ex.getDocument());
            } 
        }
        
    }
    
    
    public void testJavaEncodings() 
      throws IOException, ParsingException {
        
        String str = "<?xml version='1.0' encoding='ISO8859_1'?>" +
            "<root>é</root>"; 
        byte[] data = str.getBytes("8859_1"); 
        InputStream in = new ByteArrayInputStream(data);
        Document doc = builder.build(in);
        assertEquals("é", doc.getValue()); 
        
    }

    
    // Crimson improperly converts 0x0D and 0x0A to spaces
    // even when the attribute type is not CDATA.
    // This bug explains why the canonicalizer tests fail
    // with Crimson
    public void testCrimsonCharacterReferenceBug()
      throws IOException, ParsingException {
        
        String data = 
          "<!DOCTYPE test [<!ATTLIST test name ID #IMPLIED>]>"
          + "<test name='&#x0D;'/>";
        InputStream in = new ByteArrayInputStream(
          data.getBytes("UTF8"));
        Document document = builder.build(in, null);      
        assertEquals("\r", 
          document.getRootElement().getAttributeValue("name"));
        
    }
    
    
    public void testBaseRelativeResolution()
      throws IOException, ParsingException {
        builder.build(new File(inputDir, "baserelative/test.xml"));
    }
    
    
    // make sure transcoders on input are using normalization
    // form C when converting from other encodings
    public void testNFC()
      throws IOException, ParsingException {
        
        Document doc = builder.build(new File(inputDir, "nfctest.xml"));
        Element root = doc.getRootElement();
        String s = root.getValue();
        assertEquals(1, s.length());
        assertEquals(0xE9, s.charAt(0));
        
    }
    
    
    // This tests XOM's workaround for a bug in Crimson, Xerces,
    // and possibly other parsers
    public void testBaseRelativeResolutionRemotely()
      throws IOException, ParsingException {
        builder.build("http://www.cafeconleche.org");
    }
    
    
    public void testExternalEntityResolution()
      throws IOException, ParsingException {
        
        File input = new File(inputDir, "entitytest.xml");
        Builder builder = new Builder(false);
        Document doc = builder.build(input);
        Element root = doc.getRootElement();
        Element external = root.getFirstChildElement("external");
        assertEquals("Hello from an entity!", external.getValue());
        
    }
     
    
    // This test exposes a bug in Crimson but not Xerces.
    // It's testing whether the external DTD subset is read,
    // default attribute values applied, and comments and
    // processing instructions in the external DTD subset are not
    // reported.
    public void testExternalDTDSubset()
      throws IOException, ParsingException {
        
        File input = new File(inputDir, "externalDTDtest.xml");
        Builder builder = new Builder(false);
        Document doc = builder.build(input);
        assertEquals(2, doc.getChildCount());
        Element root = doc.getRootElement();
        Attribute name = root.getAttribute("name");
        assertEquals("value", name.getValue());
        DocType doctype = doc.getDocType();
        assertEquals("", doctype.getInternalDTDSubset());
        
    }

    
    /* <?xml version="1.0"?>
<!DOCTYPE root [
  <!ELEMENT root (#PCDATA)>
  <!-- comment -->
  <?target PI data?>
  <!NOTATION JPEG SYSTEM "image/jpeg">
  <!ATTLIST  root source ENTITY #REQUIRED>
  <!ENTITY picture SYSTEM "picture.jpg" NDATA JPEG>  
]>
<root source="picture">
  This document is intended to test the building of
  various constructs in the internal DTD subset.
</root>
*/
    public void testInternalDTDSubset() 
      throws ValidityException, ParsingException, IOException {
        
        File input = new File(inputDir, "internaldtdsubsettest.xml");
        Builder builder = new Builder(false);
        Document doc = builder.build(input);
        String internalSubset = doc.getDocType().getInternalDTDSubset();
        assertTrue(internalSubset.indexOf("<!-- comment -->") > 0);
        assertTrue(internalSubset.indexOf("<?target PI data?>") > 0);
        assertTrue(internalSubset.indexOf("<!ELEMENT root (#PCDATA)>") > 0);
        assertTrue(internalSubset.indexOf("<!ATTLIST root source ENTITY #REQUIRED>") > 0);
        // some confusion in the parser resolving these as relative URLs.
        // This is in accordance with the SAX spec, see 
        // http://www.saxproject.org/apidoc/org/xml/sax/DTDHandler.html#notationDecl(java.lang.String,%20java.lang.String,%20java.lang.String)
        // but how does it know the notation system ID is really a URL?
        assertTrue(internalSubset.indexOf("<!ENTITY picture SYSTEM ") > 0);
        assertTrue(internalSubset.indexOf("picture.jpg\" NDATA JPEG>") > 0);
        assertTrue(internalSubset.indexOf("<!NOTATION JPEG SYSTEM ") > 0);
        assertTrue(internalSubset.indexOf("image/jpeg\">") > 0);
        
    }
    

    // This test exposes a bug in Crimson, Xerces 2.5 and earlier, 
    // and possibly other parsers. I've reported the bug in Xerces,
    // and it is fixed in Xerces 2.6.
    public void testBaseRelativeResolutionRemotelyWithDirectory()
      throws IOException, ParsingException {
        builder.build("http://www.ibiblio.org/xml");
    } 

    
    // This test exposes a bug in Crimson, Xerces 2.5 and earlier, 
    // and possibly other parsers. I've reported the bug in Xerces,
    // and it should be fixed in Xerces 2.6.
    public void testRelativeURIResolutionAgainstARedirectedBase()
      throws IOException, ParsingException {
        builder.build("http://www.ibiblio.org/xml/redirecttest.xml");
    } 

    
    public void testDontGetNodeFactory() {
        
        Builder builder = new Builder();
        NodeFactory factory = builder.getNodeFactory();
        if (factory != null) {
            assertFalse(
              factory.getClass().getName().endsWith("NonVerifyingFactory")
            );
        }
        
    }
    
    
    public void testGetNodeFactory() {
        NodeFactory factory = new NodeFactory();
        Builder builder = new Builder(factory);   
        assertEquals(factory, builder.getNodeFactory());
    }
    
    
    // Make sure additional namespaces aren't added for 
    // attributes. This test is flaky because it assumes 
    // the parser reports attributes in the correct order,
    // which is not guaranteed. I use a custom SAX Filter to 
    // make sure the namespace declaration comes before the attribute.
    public void testAttributesVsNamespaces() 
      throws ParsingException, IOException, SAXException {
          
       XMLFilter filter = new OrderingFilter();
       filter.setParent(
         XMLReaderFactory.createXMLReader(
           "org.apache.xerces.parsers.SAXParser"
         )
       );
       Builder builder = new Builder(filter);
       String data ="<a/>"; 
       Document doc = builder.build(data, null);
       Element root = doc.getRootElement();
       root.removeAttribute(root.getAttribute(0));
       assertNull(root.getNamespaceURI("pre"));
       
    }

    
    private static class OrderingFilter extends XMLFilterImpl {
        
        public void startElement(String namespaceURI, String localName,
          String qualifiedName, Attributes atts) throws SAXException {    
    
            AttributesImpl newAttributes = new AttributesImpl();
            newAttributes.addAttribute(
              "",
              "pre",
              "xmlns:pre",
              "CDATA",
              "http://www.example.com/");
            newAttributes.addAttribute(
              "http://www.example.com/",
              "name",
              "pre:name",
              "CDATA",
              "value");
            super.startElement(namespaceURI, localName, qualifiedName, 
              newAttributes);
        }        

    }

    
    public void testValidateMalformedDocument() 
      throws IOException {
        
        Reader reader = new StringReader("<!DOCTYPE root [" +
                "<!ELEMENT root (a, b)>" +
                "<!ELEMENT a (EMPTY)>" +
                "<!ELEMENT b (PCDATA)>" +
                "]><root><a/><b></b>");
        try {
            validator.build(reader);   
            fail("Allowed malformed doc");
        }
        catch (ValidityException ex) {
            fail("Threw validity error instead of well-formedness error");
        }
        catch (ParsingException ex) {
            assertNotNull(ex.getMessage());
            assertNull(ex.getURI());
        }
        
    }    

    
    /* Test for particular bug in Crimson with mixed content declarations */ 
    public void testBuildInternalDTDSubsetWithCrimson() 
      throws ParsingException, IOException {

        String dtd = "  <!ELEMENT doc (#PCDATA|a)*>\n";
        
        String document = "<!DOCTYPE a [\n" + dtd + "]>\n<a/>";
        XMLReader crimson;
        try {
            crimson = XMLReaderFactory.createXMLReader(
              "org.apache.crimson.parser.XMLReaderImpl");
        } 
        catch (SAXException ex) {
            // can't test Crimson if you can't load it
            return;
        }
        
        Builder builder = new Builder(crimson);
        Document doc = builder.build(document, null);
        
        String parsedDTD = doc.getDocType().getInternalDTDSubset();
        assertEquals(dtd, parsedDTD);
        
    }
    
    
    /* Test for particular bug in Crimson with mixed content declarations */ 
    public void testBuildXMLNamespaceDeclarationWithCrimson() 
      throws ParsingException, IOException {

        String dtd = "  <!ELEMENT doc (#PCDATA|a)*>\n";
        
        String document = "<doc xmlns:xml='http://www.w3.org/XML/1998/namespace' />";
        XMLReader crimson;
        try {
            crimson = XMLReaderFactory.createXMLReader(
              "org.apache.crimson.parser.XMLReaderImpl");
        } 
        catch (SAXException ex) {
            // can't test Crimson if you can't load it
            return;
        }
        
        Builder builder = new Builder(crimson);
        Document doc = builder.build(document, null);
        
        assertEquals("<doc />", doc.getRootElement().toXML());
        
    }
    
    
    public void testBuildIllegalXMLNamespaceDeclarationWithCrimson() 
      throws ParsingException, IOException {
        
        String document = "<doc xmlns:xml='http://www.w3.org/XML/2005/namespace' />";
        XMLReader crimson;
        try {
            crimson = XMLReaderFactory.createXMLReader(
              "org.apache.crimson.parser.XMLReaderImpl");
        } 
        catch (SAXException ex) {
            // can't test Crimson if you can't load it
            return;
        }
        
        Builder builder = new Builder(crimson);
        try {
            builder.build(document, null);
            fail("Allowed wrong namespace URI for xml prefix");
        }
        catch (ParsingException success) {
            assertNotNull(success.getMessage());
        }
        
    }
    
    
    public void testATTLISTDeclaresXMLSpacePreserveOnlyWithCrimson() 
      throws ParsingException, IOException {

        String dtd = "<!DOCTYPE a [<!ATTLIST doc xml:space (preserve) 'preserve'>]\n>";
        
        String data = dtd + "<doc />";
        XMLReader crimson;
        try {
            crimson = XMLReaderFactory.createXMLReader(
              "org.apache.crimson.parser.XMLReaderImpl");
        } 
        catch (SAXException ex) {
            // can't test Crimson if you can't load it
            return;
        }
        
        Builder builder = new Builder(crimson);
        Document doc = builder.build(data, null);
        assertEquals(1, doc.getRootElement().getAttributeCount());
        
    }
    
    
    public void testXHTMLStrictWithCrimson() 
      throws ParsingException, IOException {

        XMLReader crimson;
        try {
            crimson = XMLReaderFactory.createXMLReader(
              "org.apache.crimson.parser.XMLReaderImpl");
        } 
        catch (SAXException ex) {
            // can't test Crimson if you can't load it
            return;
        }
        
        Builder builder = new Builder(crimson);
        Document doc = builder.build("http://www.cafeconleche.org/");
        assertEquals("html", doc.getDocument().getRootElement().getQualifiedName());
        DocType type = new DocType("root");
        String subset = doc.getDocType().getInternalDTDSubset();
        assertEquals("", subset);
        
    }
    
    
    public void testValidateMalformedDocumentWithCrimson() 
      throws IOException {
        
        Reader reader = new StringReader("<!DOCTYPE root [" +
                "<!ELEMENT root (a, b)>" +
                "<!ELEMENT a (EMPTY)>" +
                "<!ELEMENT b (PCDATA)>" +
                "]><root><a/><b></b>");
        XMLReader crimson;
        try {
            crimson = XMLReaderFactory.createXMLReader(
              "org.apache.crimson.parser.XMLReaderImpl");
        } 
        catch (SAXException ex) {
            // can't test Crimson if you can't load it
            return;
        }
        Builder validator = new Builder(crimson, true);
        try {
            validator.build(reader);   
            fail("Allowed malformed doc");
        }
        catch (ValidityException ex) {
            fail("Crimson threw validity error instead of well-formedness error");
        }
        catch (ParsingException success) {
            assertNotNull(success.getMessage());
            assertNull(success.getURI());
        }
        
    }        

    
    // This is testing a work-around for a Xerces bug
    // http://nagoya.apache.org/bugzilla/show_bug.cgi?id=27583
    // that reports this as an IOException rather than a SAXException
    public void testBuildMalformedDocumentWithUnpairedSurrogate() 
      throws IOException {
        
        String doc = "<doc>A\uD800A</doc>";
        try {
            builder.build(doc, "http://www.example.com");   
            fail("Allowed malformed doc");
        }
        catch (ParsingException success) {
            assertNotNull(success.getMessage());
            assertEquals("http://www.example.com/", success.getURI());
        }
        
    }
    
    
    public void testBuildMalformedDocumentWithBadUnicodeData() 
      throws IOException {
        
        File f = new File(inputDir, "xmlconf");
        f = new File(f, "xmltest");
        f = new File(f, "not-wf");
        f = new File(f, "sa");
        f = new File(f, "170.xml");
        if (f.exists()) {
            try {
                builder.build(f);   
                fail("Allowed malformed doc");
            }
            catch (ParsingException success) {
                assertNotNull(success.getMessage());
                assertTrue(success.getURI().endsWith(
                  "data/xmlconf/xmltest/not-wf/sa/170.xml"));
                assertTrue(success.getURI().startsWith("file:/"));
            }
        }
        
    }
    
    
    public void testBuildAnotherMalformedDocumentWithBadUnicodeData() 
      throws IOException {
        
        String filename = "data/oasis/p02fail30.xml";
        File f = new File(inputDir, "oasis");
        f = new File(f, "p02fail30.xml");
        if (f.exists()) {
            try {
                builder.build(f);   
                fail("Allowed malformed doc");
            }
            catch (ParsingException success) {
                assertNotNull(success.getMessage());
                assertTrue(success.getURI().endsWith(filename));
                assertTrue(success.getURI().startsWith("file:/"));
            }
        }
        
    }
    
    
    public void testBuildMalformedDocumentWithBadParser() 
      throws ParsingException, IOException {
        
        try {
            XMLReader parser = new CustomReader();
            Builder builder = new Builder(parser);
            builder.build("http://www.example.org/");
            fail("built from bad data");
        }
        catch (ParsingException success) {
            assertNotNull(success.getMessage());
            assertTrue(success.getCause() instanceof WellformednessException);
        }      
        
    }

    
    public void testBuildMalformedDocumentWithCrimson() 
      throws IOException {
        
        Reader reader = new StringReader("<!DOCTYPE root [" +
                "<!ELEMENT root (a, b)>" +
                "<!ELEMENT a (EMPTY)>" +
                "<!ELEMENT b (PCDATA)>" +
                "]><root><a/><b></b>");
        XMLReader crimson;
        try {
            crimson = XMLReaderFactory.createXMLReader(
              "org.apache.crimson.parser.XMLReaderImpl");
        } 
        catch (SAXException ex) {
            // can't test Crimson if you can't load it
            return;
        }
        Builder builder = new Builder(crimson);
        try {
            builder.build(reader);   
            fail("Allowed malformed doc");
        }
        catch (ValidityException ex) {
            fail("Crimson threw validity error instead of well-formedness error");
        }
        catch (ParsingException ex) {
            assertNotNull(ex.getMessage());
            assertNull(ex.getURI());
        }
        
    }   
    
    
    public void testBuildFunkyNamespacesWithUntrustedParser() 
      throws ParsingException, IOException, SAXException {
        
        Reader reader = new StringReader(
          "<root xmlns='http://example.org/'>" +
          "<pre:a xmlns:pre='http://www.root.org/' " +
          "xmlns='http://www.red.com'>" +
          "<b/>" +
          "</pre:a></root>");
        XMLReader parser = XMLReaderFactory.createXMLReader(
          "org.apache.xerces.parsers.SAXParser");
        XMLFilter filter = new XMLFilterImpl();
        filter.setParent(parser);
        Builder builder = new Builder(filter);
        Document doc = builder.build(reader);  
        Element root = doc.getRootElement();
        Element prea = (Element) root.getChild(0);
        Element b = (Element) prea.getChild(0);
        assertEquals("http://www.red.com", b.getNamespaceURI());
        
    }   
    
    
    // from XML Conformance Test Suite; James Clark test
    // valid 097
    public void testLineBreaksInInternalDTDSubset()
      throws ParsingException, IOException {
        
        Document doc = builder.build(new File(inputDir, "097.xml"));
        String expectedResult = "<?xml version=\"1.0\"?>\n"
            + "<!DOCTYPE doc [\n"
            + "  <!ELEMENT doc (#PCDATA)>\n"
            + "  <!ENTITY % e SYSTEM \"097.ent\">\n"
            + "  <!ATTLIST doc a1 CDATA \"v1\">\n"
            + "  <!ATTLIST doc a2 CDATA #IMPLIED>\n"
            + "]>\n"
            + "<doc a1=\"v1\" />\n";
        String actual = doc.toXML();
        assertEquals(expectedResult, actual);
        
    }
    
    
    public void testBuildDocumentThatUndeclaresDefaultNamespace()
      throws ParsingException, IOException {
        
        Document doc = builder.build(new File(inputDir, "undeclare.xml"));
        String expectedResult = "<?xml version=\"1.0\"?>\n" 
          + "<root xmlns=\"http://www.example.org\" " 
          + "xmlns:pre=\"http://www.red.com/\" test=\"test\" " 
          + "pre:red=\"value\">some data<something xmlns=\"\" />" 
          + "</root>\n";
        String actual = doc.toXML();
        assertEquals(expectedResult, actual);
        
    }
    
    
    public void testBuildFromFileThatContainsNonASCIICharacterInName()
      throws ParsingException, IOException {
        
        File f = new File(inputDir, "resumé.xml");
        try {
            Writer out = new OutputStreamWriter(
              new FileOutputStream(f), "UTF8");
            out.write("<resumé />");
            out.flush();
            out.close();
            Document doc = builder.build(f);
            String expectedResult = "<?xml version=\"1.0\"?>\n"
                + "<resumé />\n";
            String actual = doc.toXML();
            assertEquals(expectedResult, actual);
            assertTrue(doc.getBaseURI().startsWith("file:/"));
            assertTrue(doc.getBaseURI().endsWith("data/resum%C3%A9.xml"));
        }
        finally {
            if (f.exists()) f.delete();
        }
        
    }
    
    
    // This test fails on Mac OS X. It passes on Linux. 
    public void testBuildFromFileThatContainsPlane1CharacterInName()
      throws ParsingException, IOException { 
        
        int gclef = 0x1D120;
        char high = (char) ((gclef - 0x10000)/0x400 + 0xD800);
        char low = (char) ((gclef - 0x10000) % 0x400 + 0xDC00); 
        File f = new File(inputDir, "music" + high + "" + low + ".xml");
        try {
            Writer out = new OutputStreamWriter(
              new FileOutputStream(f), "UTF8");
            out.write("<resumé />");
            out.flush();
            out.close();
            Document doc = builder.build(f);
            String expectedResult = "<?xml version=\"1.0\"?>\n"
                + "<resumé />\n";
            String actual = doc.toXML();
            assertEquals(expectedResult, actual);
        }
        finally {
            if (f.exists()) f.delete();
        }
        
    }
    
    
    private File makeFile(String name) throws IOException {
        
        File f = new File(inputDir, "" + name);
        Writer out = new OutputStreamWriter(
          new FileOutputStream(f), "UTF8");
        out.write("<data/>");
        out.flush();
        out.close();
        return f;
        
    }


    public void testBuildFromFileThatContainsAmpersandInName()
      throws ParsingException, IOException {
        
        Document doc = builder.build(new File(inputDir, "&file.xml"));
        String expectedResult = "<?xml version=\"1.0\"?>\n"
            + "<data />\n";
        String actual = doc.toXML();
        assertEquals(expectedResult, actual);
        assertTrue(doc.getBaseURI().startsWith("file:/"));
        assertTrue(doc.getBaseURI().endsWith("data/&file.xml"));
        
    }
  
    
    public void testBuildFromFileThatContainsSpaceInName()
      throws ParsingException, IOException {
        
        File f = makeFile("space file.xml");
        Document doc = builder.build(f);
        String expectedResult = "<?xml version=\"1.0\"?>\n"
            + "<data />\n";
        String actual = doc.toXML();
        f.delete();
        assertEquals(expectedResult, actual);
        assertTrue(doc.getBaseURI().startsWith("file:/"));
        assertTrue(doc.getBaseURI().endsWith("data/space%" 
          + Integer.toHexString(' ') + "file.xml"));
        
    }
  
    
    public void testBuildFromFileThatContainsSharpInName()
      throws ParsingException, IOException {
        
        File f = new File(inputDir, "#file.xml");
        try {
            Writer out = new OutputStreamWriter(
              new FileOutputStream(f), "UTF8");
            out.write("<data />");
            out.flush();
            out.close();
            Document doc = builder.build(f);
            String expectedResult = "<?xml version=\"1.0\"?>\n"
                + "<data />\n";
            String actual = doc.toXML();
            assertEquals(expectedResult, actual);
            assertTrue(doc.getBaseURI().startsWith("file:/"));
            assertTrue(doc.getBaseURI().endsWith("data/%23file.xml"));
        }
        finally {
            if (f.exists()) f.delete();
        }
        
    }
  
    
    public void testBuildFromFileThatContainsExclamationPointInName()
      throws ParsingException, IOException {
        
        Document doc = builder.build(new File(inputDir, "!file.xml"));
        String expectedResult = "<?xml version=\"1.0\"?>\n"
            + "<data />\n";
        String actual = doc.toXML();
        assertEquals(expectedResult, actual);
        assertTrue(doc.getBaseURI().startsWith("file:/"));
        assertTrue(doc.getBaseURI().endsWith("data/!file.xml"));
        
    }
  
    
    public void testBuildFromFileThatContainsDoubleQuoteInName()
      throws ParsingException, IOException {
        
        File f = makeFile("\"file\".xml");
        try {
            Document doc = builder.build(f);
            f.delete();
            String expectedResult = "<?xml version=\"1.0\"?>\n<data />\n";
            String actual = doc.toXML();
            assertEquals(expectedResult, actual);
            assertTrue(doc.getBaseURI().startsWith("file:/"));
            assertTrue(doc.getBaseURI().endsWith("data/%22file%22.xml"));
        }
        catch (FileNotFoundException ex) {
            // This platform doesn't allow double quotes in file names 
        }
        
    }
  
    
    public void testBuildFromFileThatContainsSingleQuoteInName()
      throws ParsingException, IOException {
        
        File f = makeFile("'file'.xml");
        Document doc = builder.build(f);
        f.delete();
        String expectedResult = "<?xml version=\"1.0\"?>\n"
            + "<data />\n";
        String actual = doc.toXML();
        assertEquals(expectedResult, actual);
        assertTrue(doc.getBaseURI().startsWith("file:/"));
        assertTrue(doc.getBaseURI().endsWith("data/'file'.xml"));
        
    }
  
    
    public void testBuildFromFileThatContainsParenthesesInName()
      throws ParsingException, IOException {
        
        Document doc = builder.build(new File(inputDir, "()file.xml"));
        String expectedResult = "<?xml version=\"1.0\"?>\n"
            + "<data />\n";
        String actual = doc.toXML();
        assertEquals(expectedResult, actual);
        assertTrue(doc.getBaseURI().startsWith("file:/"));
        assertTrue(doc.getBaseURI().endsWith("data/()file.xml"));
        
    }
  
    
    public void testBuildFromFileThatContainsCurlyBracesInName()
      throws ParsingException, IOException {
        
        Document doc = builder.build(new File(inputDir, "{file}.xml"));
        String expectedResult = "<?xml version=\"1.0\"?>\n"
            + "<data />\n";
        String actual = doc.toXML();
        assertEquals(expectedResult, actual);
        assertTrue(doc.getBaseURI().startsWith("file:/"));
        assertTrue(doc.getBaseURI().endsWith("data/%" 
          + Integer.toHexString('{').toUpperCase() + "file%"
          + Integer.toHexString('}').toUpperCase() + ".xml"));
        
    }
  
    
    public void testBuildFromFileThatContainsSquareBracketsInName()
      throws ParsingException, IOException {
        
        Document doc = builder.build(new File(inputDir, "[file].xml"));
        String expectedResult = "<?xml version=\"1.0\"?>\n"
            + "<data />\n";
        String actual = doc.toXML();
        assertEquals(expectedResult, actual);
        assertTrue(doc.getBaseURI().startsWith("file:/"));
        assertTrue(doc.getBaseURI().endsWith("data/%" 
          + Integer.toHexString('[').toUpperCase() + "file%"
          + Integer.toHexString(']').toUpperCase() + ".xml"));
        
    }
  
    
    public void testBuildFromFileThatContainsVerticalBarInName()
      throws ParsingException, IOException {
        
        File f = makeFile("|file.xml");
        try {
            Document doc = builder.build(f);
            f.delete();
            String expectedResult = "<?xml version=\"1.0\"?>\n"
                + "<data />\n";
            String actual = doc.toXML();
            assertEquals(expectedResult, actual);
            assertTrue(doc.getBaseURI().startsWith("file:/"));
            assertTrue(doc.getBaseURI().endsWith("data/%" 
              + Integer.toHexString('|').toUpperCase()
              + "file.xml"));
        }
        catch (FileNotFoundException ex) {
            // This platform doesn't allow vertical bars in file names 
        }
        
    }

    
    public void testBuildFromFileThatContainsColonInName()
      throws ParsingException, IOException {
        
        File f = makeFile(":file.xml");
        try {
            Document doc = builder.build(f);
            f.delete();
            String expectedResult = "<?xml version=\"1.0\"?>\n"
                + "<data />\n";
            String actual = doc.toXML();
            assertEquals(expectedResult, actual);
            assertTrue(doc.getBaseURI().startsWith("file:/"));
            assertTrue(doc.getBaseURI().endsWith("data/:file.xml"));
        }
        catch (FileNotFoundException ex) {
            // This platform doesn't allow colons in file names 
        }
        
    }

    
    public void testBuildFromFileThatContainsUnderscoreInName()
      throws ParsingException, IOException {
        
        File f = makeFile("_file.xml");
        Document doc = builder.build(f);
        f.delete();
        String expectedResult = "<?xml version=\"1.0\"?>\n"
            + "<data />\n";
        String actual = doc.toXML();
        assertEquals(expectedResult, actual);
        assertTrue(doc.getBaseURI().startsWith("file:/"));
        assertTrue(doc.getBaseURI().endsWith("data/_file.xml"));
        
    }

    
    public void testBuildFromFileThatContainsUppercaseASCIIInName()
      throws ParsingException, IOException {
        
        File f = makeFile("ABCDEFGHIJKLMONPQRSTUVWXYZ.xml");
        Document doc = builder.build(f);
        f.delete();
        String expectedResult = "<?xml version=\"1.0\"?>\n"
            + "<data />\n";
        String actual = doc.toXML();
        assertEquals(expectedResult, actual);
        assertTrue(doc.getBaseURI().startsWith("file:/"));
        assertTrue(doc.getBaseURI().endsWith("data/ABCDEFGHIJKLMONPQRSTUVWXYZ.xml"));
        
    }

    
    public void testBuildFromFileThatContainsAsteriskInName()
      throws ParsingException, IOException {
        
        File f = makeFile("*file.xml");
        try {
            Document doc = builder.build(f);
            f.delete();
            String expectedResult = "<?xml version=\"1.0\"?>\n"
                + "<data />\n";
            String actual = doc.toXML();
            assertEquals(expectedResult, actual);
            assertTrue(doc.getBaseURI().startsWith("file:/"));
            assertTrue(doc.getBaseURI().endsWith("data/*file.xml"));
        }
        catch (FileNotFoundException ex) {
            // This platform doesn't allow asterisks in file names 
        }
        
    }
  
    
    public void testBuildFromFileThatContainsSemicolonInName()
      throws ParsingException, IOException {
        
        Document doc = builder.build(new File(inputDir, ";file.xml"));
        String expectedResult = "<?xml version=\"1.0\"?>\n"
            + "<data />\n";
        String actual = doc.toXML();
        assertEquals(expectedResult, actual);
        assertTrue(doc.getBaseURI().startsWith("file:/"));
        assertTrue(doc.getBaseURI().endsWith("data/;file.xml"));
        
    }
  
    
    public void testBuildFromFileThatContainsPlusSignInName()
      throws ParsingException, IOException {
        
        Document doc = builder.build(new File(inputDir, "+file.xml"));
        String expectedResult = "<?xml version=\"1.0\"?>\n"
            + "<data />\n";
        String actual = doc.toXML();
        assertEquals(expectedResult, actual);
        assertTrue(doc.getBaseURI().startsWith("file:/"));
        assertTrue(doc.getBaseURI().endsWith("data/%" 
          + Integer.toHexString('+').toUpperCase() + "file.xml"));
        
    }
  
    
    public void testBuildFromFileThatContainsCommaInName()
      throws ParsingException, IOException {
        
        File f = new File(inputDir, ",file.xml");
        try {
            Writer out = new OutputStreamWriter(
              new FileOutputStream(f), "UTF8");
            out.write("<data />");
            out.flush();
            out.close();
            Document doc = builder.build(f);
            String expectedResult = "<?xml version=\"1.0\"?>\n"
                + "<data />\n";
            String actual = doc.toXML();
            assertEquals(expectedResult, actual);
            assertTrue(doc.getBaseURI().startsWith("file:/"));
            assertTrue(doc.getBaseURI().endsWith("data/,file.xml"));
        }
        finally {
            if (f.exists()) f.delete();
        }
        
    }
  
    
    public void testBuildFromFileThatContainsBackslashInName()
      throws ParsingException, IOException {
        
        String os = System.getProperty("os.name", "Unix");
        if (os.indexOf("Windows") >= 0) return;
  
        File f = new File(inputDir, "\\file.xml");
        try {
            Writer out = new OutputStreamWriter(
              new FileOutputStream(f), "UTF8");
            out.write("<data />");
            out.flush();
            out.close();
            Document doc = builder.build(f);
            String expectedResult = "<?xml version=\"1.0\"?>\n"
                + "<data />\n";
            String actual = doc.toXML();
            assertEquals(expectedResult, actual);
            assertTrue(doc.getBaseURI().startsWith("file:/"));
            assertTrue(doc.getBaseURI().endsWith("data/%5Cfile.xml"));
        }
        finally {
            if (f.exists()) f.delete();
        }
        
    }
  
    
    public void testBuildFromFileThatContainsC0ControlCharacterInName()
      throws ParsingException, IOException {
        
        File f = new File(inputDir, "\u0019file.xml");
        try {
            Writer out = new OutputStreamWriter(
              new FileOutputStream(f), "UTF8");
            out.write("<data />");
            out.flush();
            out.close();
            Document doc = builder.build(f);
            String expectedResult = "<?xml version=\"1.0\"?>\n"
                + "<data />\n";
            String actual = doc.toXML();
            assertEquals(expectedResult, actual);
            assertTrue(doc.getBaseURI().startsWith("file:/"));
            assertTrue(doc.getBaseURI().endsWith("data/%19file.xml"));
        }
        catch (FileNotFoundException ex) {
            // This platform doesn't allow C0 controls in file names 
        }
        finally {
            if (f.exists()) f.delete();
        }
        
    }
  
    
    public void testBuildFromFileThatContainsTabCharacterInName()
      throws ParsingException, IOException {
        
        File f = new File(inputDir, "\tfile.xml");
        try {
            Writer out = new OutputStreamWriter(
              new FileOutputStream(f), "UTF8");
            out.write("<data />");
            out.flush();
            out.close();
            Document doc = builder.build(f);
            String expectedResult = "<?xml version=\"1.0\"?>\n"
                + "<data />\n";
            String actual = doc.toXML();
            assertEquals(expectedResult, actual);
            assertTrue(doc.getBaseURI().startsWith("file:/"));
            assertTrue(doc.getBaseURI().endsWith("data/%09file.xml"));
        }
        catch (FileNotFoundException ex) {
            // This platform doesn't allow tabs in file names 
        }
        finally {
            if (f.exists()) f.delete();
        }
        
    }
  
    
    public void testBuildFromFileThatContainsTildeInName()
      throws ParsingException, IOException {
        
        File f = new File(inputDir, "~file.xml");
        try {
            Writer out = new OutputStreamWriter(
              new FileOutputStream(f), "UTF8");
            out.write("<data />");
            out.flush();
            out.close();
            Document doc = builder.build(f);
            String expectedResult = "<?xml version=\"1.0\"?>\n"
                + "<data />\n";
            String actual = doc.toXML();
            assertEquals(expectedResult, actual);
            assertTrue(doc.getBaseURI().startsWith("file:/"));
            assertTrue(doc.getBaseURI().endsWith("data/~file.xml"));
        }
        finally {
            if (f.exists()) f.delete();
        }
        
    }
  
    
    public void testBuildFromFileThatContainsAngleBracketsInName()
      throws ParsingException, IOException {
        
        File f = makeFile("<file>.xml");
        try {
            Document doc = builder.build(f);
            f.delete();
            String expectedResult = "<?xml version=\"1.0\"?>\n"
                + "<data />\n";
            String actual = doc.toXML();
            assertEquals(expectedResult, actual);
            assertTrue(doc.getBaseURI().startsWith("file:/"));
            assertTrue(doc.getBaseURI().endsWith("data/%" 
              + Integer.toHexString('<').toUpperCase() + "file%"
              + Integer.toHexString('>').toUpperCase() + ".xml"));
        }
        catch (FileNotFoundException ex) {
            // This platform doesn't allow < and > in file names 
        }
        
    }
  
    
    public void testBuildFromFileThatContainsDollarSignInName()
      throws ParsingException, IOException {
        
        Document doc = builder.build(new File(inputDir, "$file.xml"));
        String expectedResult = "<?xml version=\"1.0\"?>\n"
            + "<data />\n";
        String actual = doc.toXML();
        assertEquals(expectedResult, actual);
        assertTrue(doc.getBaseURI().startsWith("file:/"));
        assertTrue(doc.getBaseURI().endsWith("data/$file.xml"));
        
    }
  
    
    public void testBuildFromFileThatContainsPercentSignInName()
      throws ParsingException, IOException {
        
        Document doc = builder.build(new File(inputDir, "%file.xml"));
        String expectedResult = "<?xml version=\"1.0\"?>\n"
            + "<data />\n";
        String actual = doc.toXML();
        assertEquals(expectedResult, actual);
        assertTrue(doc.getBaseURI().startsWith("file:/"));
        assertTrue(doc.getBaseURI().endsWith("data/%" 
          + Integer.toHexString('%') + "file.xml"));
        
    }
  
    
    public void testBuildFromFileThatContainsQuestionMarkInName()
      throws ParsingException, IOException {
        
        File f = makeFile("?file.xml");
        try {
            Document doc = builder.build(f);
            f.delete();
            String expectedResult = "<?xml version=\"1.0\"?>\n"
                + "<data />\n";
            String actual = doc.toXML();
            assertEquals(expectedResult, actual);
            assertTrue(doc.getBaseURI().startsWith("file:/"));
            assertTrue(doc.getBaseURI().endsWith("data/%" 
              + Integer.toHexString('?').toUpperCase() + "file.xml"));
        }
        catch (FileNotFoundException ex) {
            // This platform doesn't allow question marks in file names 
        }
        
    }
  
    
    public void testBuildFromFileThatContainsAtSignInName()
      throws ParsingException, IOException {
        
        Document doc = builder.build(new File(inputDir, "@file.xml"));
        String expectedResult = "<?xml version=\"1.0\"?>\n"
            + "<data />\n";
        String actual = doc.toXML();
        assertEquals(expectedResult, actual);
        assertTrue(doc.getBaseURI().startsWith("file:/"));
        assertTrue(doc.getBaseURI().endsWith("data/%"
          + Integer.toHexString('@') + "file.xml"));
        
    }
  
    
    public void testBuildFromFileThatContainsEqualsSignInName()
      throws ParsingException, IOException {
        
        Document doc = builder.build(new File(inputDir, "=file.xml"));
        String expectedResult = "<?xml version=\"1.0\"?>\n"
            + "<data />\n";
        String actual = doc.toXML();
        assertEquals(expectedResult, actual);
        assertTrue(doc.getBaseURI().startsWith("file:/"));
        assertTrue(doc.getBaseURI().endsWith("data/=file.xml"));
        
    }
  
    
    public void testBuildFromFileThatContainsCaretInName()
      throws ParsingException, IOException {
        
        Document doc = builder.build(new File(inputDir, "^file.xml"));
        String expectedResult = "<?xml version=\"1.0\"?>\n"
            + "<data />\n";
        String actual = doc.toXML();
        assertEquals(expectedResult, actual);
        assertTrue(doc.getBaseURI().startsWith("file:/"));
        assertTrue(doc.getBaseURI().endsWith("data/%" 
          + Integer.toHexString('^').toUpperCase() + "file.xml"));
        
    }
  
    
    public void testBuildFromFileThatContainsBactickInName()
      throws ParsingException, IOException {
        
        Document doc = builder.build(new File(inputDir, "`file.xml"));
        String expectedResult = "<?xml version=\"1.0\"?>\n"
            + "<data />\n";
        String actual = doc.toXML();
        assertEquals(expectedResult, actual);
        assertTrue(doc.getBaseURI().startsWith("file:/"));
        assertTrue(doc.getBaseURI().endsWith("data/%" 
          + Integer.toHexString('`') + "file.xml"));
        
    }
    
    
    private static class NonValidatingFilter extends XMLFilterImpl {
        
        public void setFeature(String uri, boolean value) 
          throws SAXNotRecognizedException, SAXNotSupportedException {
           
            if ("http://xml.org/sax/features/validation".equals(uri) && value) {
                throw new SAXNotSupportedException("");
            }
            super.setFeature(uri, value);
            
        }
        
        public boolean getFeature(String uri) 
          throws SAXNotRecognizedException, SAXNotSupportedException {
            
            if ("http://xml.org/sax/features/validation".equals(uri)) {
                return false;
            }
            return super.getFeature(uri);
            
        }
        
        
    }
    
    
    public void testNonValidatingParserException() throws SAXException {
        
        XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
        XMLFilter filter = new NonValidatingFilter();
        filter.setParent(parser);
        
        try {
            new Builder(filter, true, null);
            fail("Validating with a non-validating parser");
        }
        catch (XMLException success) {
            assertNotNull(success.getMessage());
        }
        
    }
  
    private static class NonEntityResolvingFilter extends XMLFilterImpl {
        
        public void setFeature(String uri, boolean value) 
          throws SAXNotRecognizedException, SAXNotSupportedException {
           
            if (value && (
              "http://xml.org/sax/features/validation".equals(uri) 
              || "http://xml.org/sax/features/external-general-entities".equals(uri))
              || "http://xml.org/sax/features/external-parameter-entities".equals(uri)) {
                throw new SAXNotSupportedException("");
            }
            super.setFeature(uri, value);
            
        }
        
        public boolean getFeature(String uri) 
          throws SAXNotRecognizedException, SAXNotSupportedException {
            
            if ("http://xml.org/sax/features/validation".equals(uri)
              || "http://xml.org/sax/features/external-general-entities".equals(uri)
              || "http://xml.org/sax/features/external-parameter-entities".equals(uri)) {
                return false;
            }
            return super.getFeature(uri);
            
        }
        
        
    }
    
    
    public void testNonEntityResolvingParserException() throws SAXException {
        
        XMLReader parser = XMLReaderFactory.createXMLReader(
          "org.apache.xerces.parsers.SAXParser");
        XMLFilter filter = new NonEntityResolvingFilter();
        filter.setParent(parser);
        
        try {
            new Builder(filter, false, null);
            fail("Accepted a non-entity resolving parser");
        }
        catch (XMLException success) {
            assertNotNull(success.getMessage());
        }
        
    }
  
    
    // Fake certain errors to test workarounds for bugs in certain
    // parsers, especially Piccolo. 
    private static class ExceptionTester extends XMLFilterImpl {
        
        private Exception ex;
        
        ExceptionTester(Exception ex) {
            this.ex = ex;
        }
        
        public void parse(InputSource in) throws IOException, SAXException {
            if (ex instanceof IOException) throw (IOException) ex;
            else if (ex instanceof SAXException) throw (SAXException) ex;
            else throw (RuntimeException) ex;
        }
        
    }
    
    
    public void testParserThrowsNullPointerException() 
      throws SAXException, IOException {
        
        XMLReader parser = XMLReaderFactory.createXMLReader(
          "org.apache.xerces.parsers.SAXParser");
        Exception cause = new NullPointerException();
        XMLFilter filter = new ExceptionTester(cause);
        filter.setParent(parser);
        Builder builder = new Builder(filter);
        
        try {
            builder.build("<data/>");
        }
        catch (ParsingException success) {
            assertEquals(cause, success.getCause());
        }
        
    }
    
    
    public void testParserThrowsNegativeArraySizeException() 
      throws SAXException, IOException {
        
        XMLReader parser = XMLReaderFactory.createXMLReader(
          "org.apache.xerces.parsers.SAXParser");
        Exception cause = new NegativeArraySizeException();
        XMLFilter filter = new ExceptionTester(cause);
        filter.setParent(parser);
        Builder builder = new Builder(filter);
        
        try {
            builder.build("<data/>");
        }
        catch (ParsingException success) {
            assertEquals(cause, success.getCause());
        }
        
    }
    
    
    public void testParserThrowsArrayIndexOutOfBoundsException() 
      throws SAXException, IOException {
        
        XMLReader parser = XMLReaderFactory.createXMLReader(
          "org.apache.xerces.parsers.SAXParser");
        Exception cause = new ArrayIndexOutOfBoundsException();
        XMLFilter filter = new ExceptionTester(cause);
        filter.setParent(parser);
        Builder builder = new Builder(filter);
        
        try {
            builder.build("<data/>");
        }
        catch (ParsingException success) {
            assertEquals(cause, success.getCause());
        }
        
    }
    
    
    public void testParserThrowsUTFDataFormatException() 
      throws SAXException, IOException {
        
        XMLReader parser = XMLReaderFactory.createXMLReader(
          "org.apache.xerces.parsers.SAXParser");
        Exception cause = new UTFDataFormatException();
        XMLFilter filter = new ExceptionTester(cause);
        filter.setParent(parser);
        Builder builder = new Builder(filter);
        
        try {
            builder.build("<data/>");
        }
        catch (ParsingException success) {
            assertEquals(cause, success.getCause());
        }
        
    }
    

    public void testParserThrowsCharConversionException() 
      throws SAXException, IOException {
        
        XMLReader parser = XMLReaderFactory.createXMLReader(
          "org.apache.xerces.parsers.SAXParser");
        Exception cause = new CharConversionException();
        XMLFilter filter = new ExceptionTester(cause);
        filter.setParent(parser);
        Builder builder = new Builder(filter);
        
        try {
            builder.build("<data/>");
        }
        catch (ParsingException success) {
            assertEquals(cause, success.getCause());
        }
        
    }
    

    public void testParserThrowsPlainSAXException() 
      throws SAXException, IOException {
        
        XMLReader parser = XMLReaderFactory.createXMLReader(
          "org.apache.xerces.parsers.SAXParser");
        Exception cause = new SAXException("What happened to no-args constructor?");
        XMLFilter filter = new ExceptionTester(cause);
        filter.setParent(parser);
        Builder builder = new Builder(filter);
        
        try {
            builder.build("<data/>");
        }
        catch (ParsingException success) {
            assertEquals(cause, success.getCause());
        }
        
    }
    

    public void testParserThrowsUnexpectedRuntimeException() 
      throws SAXException, IOException {
        
        XMLReader parser = XMLReaderFactory.createXMLReader(
          "org.apache.xerces.parsers.SAXParser");
        Exception cause = new RuntimeException();
        XMLFilter filter = new ExceptionTester(cause);
        filter.setParent(parser);
        Builder builder = new Builder(filter);
        
        try {
            builder.build("<data/>");
        }
        catch (ParsingException success) {
            assertEquals(cause, success.getCause());
        }
        
    }
    

    public void testParserThrowsIOException() 
      throws SAXException, ParsingException {
        
        XMLReader parser = XMLReaderFactory.createXMLReader(
          "org.apache.xerces.parsers.SAXParser");
        Exception cause = new IOException();
        XMLFilter filter = new ExceptionTester(cause);
        filter.setParent(parser);
        Builder builder = new Builder(filter);
        
        try {
            builder.build("<data/>");
        }
        catch (IOException success) {
            assertEquals(cause, success);
        }
        
    }
    
    
    public void testCrimsonIgnoresWarning() 
      throws SAXException, ParsingException, IOException {
        
        
        XMLReader parser;
        try {
          parser = XMLReaderFactory.createXMLReader(
            "org.apache.crimson.parser.XMLReaderImpl"
          );
        }
        catch (SAXException ex) {
            // Can't test Crimson if you can't load it
            return;
        }
        XMLFilter filter = new WarningFilter();
        filter.setParent(parser);
        Builder builder = new Builder(filter);
        
        Document doc = builder.build("<data/>", null);
        assertEquals("<?xml version=\"1.0\"?>\n<data />\n", doc.toXML());
        
    }
    
    
    private static class WarningFilter extends XMLFilterImpl {
        
        public void startElement(String namespaceURI, String localName,
          String qualifiedName, Attributes atts) throws SAXException {    
    
            this.getErrorHandler().warning(
              new SAXParseException("Warning", new LocatorImpl())
            );
            super.startElement(namespaceURI, localName, qualifiedName, 
              atts);
            
        }        

    }
    

    public void testSaxonsAElfredIsVerified() 
      throws SAXException, IOException {
        
        XMLReader parser;
        try {
          parser = XMLReaderFactory.createXMLReader(
            "com.icl.saxon.aelfred.SAXDriver"
          );
        }
        catch (SAXException ex) {
            // Can't test SAXON if you can't load it
            return;
        }
        Builder builder = new Builder(parser);
        
        try {
            // known bug in Saxon; doesn't catch 
            // colon in processing instruction targets
            builder.build("<?test:data ?><data/>", null);
            fail("Didn't verify Saxon's input");
        }
        catch (ParsingException success) {
            assertNotNull(success.getMessage());
        }
        
    }
    
    
    public void testSaxon7sAElfredIsVerified() 
      throws SAXException, IOException {
        
        XMLReader parser;
        try {
          parser = XMLReaderFactory.createXMLReader(
            "net.sf.saxon.aelfred.SAXDriver"
          );
        }
        catch (SAXException ex) {
            // Can't test SAXON if you can't load it
            return;
        }
        Builder builder = new Builder(parser);
        
        try {
            // known bug in Saxon: doesn't catch 
            // colon in processing instruction targets
            builder.build("<?test:data ?><data/>", null);
            fail("Didn't verify Saxon's input");
        }
        catch (ParsingException success) {
            assertNotNull(success.getMessage());
        }
        
    }
    
    
   public void testGNUJAXPIsVerified() 
      throws SAXException, IOException {
        
        XMLReader parser;
        try {
          parser = XMLReaderFactory.createXMLReader(
            "gnu.xml.aelfred2.XmlReader"
          );
        }
        catch (SAXException ex) {
            // Can't test GNU JAXP if you can't load it
            return;
        }
        Builder builder = new Builder(parser);
        
        try {
            // known bug in GNUJAXP: doesn't catch 
            // colon in processing instruction targets
            builder.build("<?test:data ?><data/>", null);
            fail("Didn't verify GNU JAXP's input");
        }
        catch (ParsingException success) {
            assertNotNull(success.getMessage());
        }
        
    }
   
   
   public void testCatalogOnTopOfTrustedParserIsTrusted() throws  
     NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
       
        try {
            XMLReader parser = XMLReaderFactory.createXMLReader(
              "org.apache.crimson.parser.XMLReaderImpl"
            );
        
            Class filter = Class.forName("org.apache.xml.resolver.tools.ResolvingXMLFilter");
            Class[] types = {XMLReader.class};
            Constructor constructor = filter.getConstructor(types);
            Object[] args = {parser};
            XMLReader reader = (XMLReader) constructor.newInstance(args);
            Builder builder = new Builder(reader);
            // If the factory is a nonverifying factory, then 
            // getNodeFactory() won't return it.
            assertNull(builder.getNodeFactory());
        }
        catch (ClassNotFoundException ex) {
            // Can't test if we can't find the class
        }
        catch (SAXException ex) {
            // Need a trusted parser to test this
        }

    }
   
    
    // XML conformance test case xmlconf/xmltest/valid/not-sa/014.ent
    // shows how this can be necessary. In brief, the internal DTD 
    // subset can define or override parameter entities used in the
    // external DTD subset, and that the external DTD subset depends
    // on for well-formedness
    public void testPreserveParameterEntitiesInInternalDTDSubset() 
      throws ParsingException, IOException {
       
        String data = "<!DOCTYPE doc [\n" 
          + "<!ENTITY % e 'INCLUDE'>]><doc />";
        Document doc = builder.build(data, null);
        String subset = doc.getDocType().getInternalDTDSubset();
        assertEquals("  <!ENTITY % e \"INCLUDE\">\n", subset);
        
    }
    
   
    public void testTrickyCaseFromAppendixA2OfXMLSpec() 
      throws ParsingException, IOException {
        
        String data = "<?xml version='1.0'?>\n"
          + "<!DOCTYPE test [\n"
          + "<!ELEMENT test (#PCDATA) >\n"
          + "<!ENTITY % xx '&#37;zz;'>\n"
          + "<!ENTITY % zz '&#60;!ENTITY tricky \"error-prone\" >' >\n"
          + "%xx;\n"
          + "]>\n"
          + "<test>This sample shows a &tricky; method.</test>\n";
        
        Document doc = builder.build(data, null);
        String s = doc.toXML();
        Document roundTrip = builder.build(s, null);
        assertEquals(doc, roundTrip);
        
    }
    
    
    // This is an example of case where preserving external entity
    // declaration in internal DTD subset is necessary to maintain
    // well-formedness
    public void testPreserveExternalGeneralEntityDeclaration() 
      throws ParsingException, IOException {
     
        Document doc = builder.build(new File(inputDir, "ge.xml"));
        DocType doctype = doc.getDocType();
        assertEquals("  <!ENTITY ccl SYSTEM \"ge.txt\">\n", doctype.getInternalDTDSubset());  
    }
    
    
    // This is an example of case where preserving external entity
    // declaration in internal DTD subset is necessary to maintain
    // validity
    public void testPreserveExternalParameterEntityDeclaration() 
      throws ParsingException, IOException {
     
        Document doc = builder.build(new File(inputDir, "pe.xml"));
        DocType doctype = doc.getDocType();
        assertEquals("  <!ENTITY % ccl SYSTEM \"pe.txt\">\n", doctype.getInternalDTDSubset());  
    }
    
        
    public void testNMTOKENSNormalizationOfCarriageReturnLineFeedEntityReferences() 
      throws ParsingException, IOException {
        
        String data = "<!DOCTYPE attributes  [\n"
          + "<!ATTLIST attributes nmtokens NMTOKENS #IMPLIED>]>\n"
          + "<attributes nmtokens =  \" this&#x0d;&#x0a; also  gets&#x20; normalized \" />";
        
        Document doc = builder.build(data, null);
        String s = doc.toXML();
        Document roundTrip = builder.build(s, null);
        assertEquals(doc, roundTrip);
        
    }
    
    
    public void testXMLConformanceTestSuiteDocuments() 
      throws ParsingException, IOException {
      
        File data = new File("data");
        File canonical = new File(data, "canonical");
        File masterList = new File(canonical, "xmlconf");
        masterList = new File(masterList, "xmlconf.xml");
        if (masterList.exists()) {
            Document xmlconf = builder.build(masterList);
            Elements testcases = xmlconf.getRootElement().getChildElements("TESTCASES");
            processTestCases(testcases);
        }

    }

    
    // xmlconf/xmltest/valid/sa/097.xml appears to be screwed up by a lot
    // of parsers 
    private void processTestCases(Elements testcases) 
      throws ParsingException, IOException {
        
        for (int i = 0; i < testcases.size(); i++) {
              Element testcase = testcases.get(i); 
              Elements tests = testcase.getChildElements("TEST");
              processTests(tests);
              Elements level2 = testcase.getChildElements("TESTCASES");
              // need to be recursive to handle recursive IBM test cases
              processTestCases(level2);
        }
        
    }


    private void processTests(Elements tests) 
      throws ParsingException, IOException  {
        
        Element parent = new Element("e");
        Element child = new Element("a");
        parent.appendChild(child);
            
        int size = tests.size();
        for (int i = 0; i < size; i++) {
            Element test = tests.get(i);
            String namespace = test.getAttributeValue("NAMESPACE");
            if ("no".equals(namespace)) continue;
            String type = test.getAttributeValue("TYPE");
            if ("not-wf".equals(type)) continue;
            String uri = test.getAttributeValue("URI");
            String base = test.getBaseURI();
            // Hack because URIUtil isn't public; and I don't want to
            // depend on 1.4 only java.net.URI
            parent.setBaseURI(base);
            child.addAttribute(new Attribute("xml:base", 
              "http://www.w3.org/XML/1998/namespace", uri));
            String resolvedURI = child.getBaseURI();
            
            Document doc = builder.build(resolvedURI);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                Serializer serializer = new Serializer(out);
                serializer.write(doc);
            }
            finally {
                out.close();
            }           
            byte[] actual = out.toByteArray();
            
            InputStream in = new ByteArrayInputStream(actual);
            try {
                Document roundTrip = builder.build(in, resolvedURI);
                assertEquals("Failed to roundtrip " + uri, doc, roundTrip);
            }
            catch (ParsingException ex) {
                System.out.println(ex.getURI());
                System.out.println(doc.toXML());
                throw ex;
            }
            finally {
                in.close();
            }
        }
        
    }
    
}
