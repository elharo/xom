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
import java.io.Reader;
import java.io.StringReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.XMLReaderFactory;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Comment;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.IllegalNameException;
import nu.xom.NodeFactory;
import nu.xom.ParsingException;
import nu.xom.ProcessingInstruction;
import nu.xom.ValidityException;


/**
 * <p>
 *  Tests building documents from streams, strings, files,
 *  and other input sources.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d25
 *
 */
public class BuilderTest extends XOMTestCase {


    // Cusotm parser to test what happens when parser supplies 
    // malformed data
    private static class CustomReader implements XMLReader {
        
        private ContentHandler handler;
     
        public void parse(String data) throws SAXException {
            handler.startDocument();
            handler.startElement("87", "87", "87", new AttributesImpl());
            handler.endElement("87", "87", "87");
            handler.endDocument();
        }

        public boolean getFeature(String uri) {
            return false;
        }

        public void setFeature(String uri, boolean value) {}
        
        public ContentHandler getContentHandler() {
            return handler;
        }

        public void setContentHandler(ContentHandler handler) {
            this.handler = handler;
        }
        
        public DTDHandler getDTDHandler() {
            return null;
        }

        public void setDTDHandler(DTDHandler handler) {
        }

        public EntityResolver getEntityResolver() {
            return null;
        }
        
        public void setEntityResolver(EntityResolver resolver) {}

        public ErrorHandler getErrorHandler() {
            return null;
        }

        public void setErrorHandler(ErrorHandler handler) {}

        public void parse(InputSource arg0) throws IOException,
                SAXException {}

        public Object getProperty(String uri) {
            return null;
        }

        public void setProperty(String uri, Object value) {}
        
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
      = "<!ENTITY Pub-Status \"" +        "This is a pre-release of the specification.\">";
    private String externalEntityDeclarationPublic = 
      "<!ENTITY open-hatch " 
      + "PUBLIC \"-//Textuality//TEXT Standard " +        "open-hatch boilerplate//EN\" "
      + "\"http://www.textuality.com/boilerplate/OpenHatch.xml\">";
    private String externalEntityDeclarationSystem = 
      "<!ENTITY test SYSTEM " +      "\"http://www.textuality.com/boilerplate/OpenHatch.xml\">";
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
     + "<svg xmlns='http://www.w3.org/TR/2000/svg'><text>text in a "      +   "namespace</text></svg></test>\r\n<!--epilog-->";
     
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


    public void testNotationAttributeType() 
      throws IOException, ParsingException {
        Reader reader = new StringReader(attributeDoc);
        Document document = builder.build(reader);
        Element root = document.getRootElement();
        Attribute att = root.getAttribute("notationatt"); 
        assertEquals(Attribute.Type.NOTATION, att.getType());      
        assertNull(document.getBaseURI());
    }
    
    
    public void testCDATAAttributeType() 
      throws IOException, ParsingException {
        Reader reader = new StringReader(attributeDoc);
        Document document = builder.build(reader);
        Element root = document.getRootElement();
        Attribute att = root.getAttribute("cdataatt"); 
        assertEquals(Attribute.Type.CDATA, att.getType());      
        assertNull(document.getBaseURI());
    }
    
    
    public void testEntityAttributeType() 
      throws IOException, ParsingException {
        Reader reader = new StringReader(attributeDoc);
        Document document = builder.build(reader);
        Element root = document.getRootElement();
        Attribute att = root.getAttribute("entityatt"); 
        assertEquals(Attribute.Type.ENTITY, att.getType());      
        assertNull(document.getBaseURI());
    }
    
    
    public void testEntitiesAttributeType() 
      throws IOException, ParsingException {
        Reader reader = new StringReader(attributeDoc);
        Document document = builder.build(reader);
        Element root = document.getRootElement();
        Attribute att = root.getAttribute("entitiesatt"); 
        assertEquals(Attribute.Type.ENTITIES, att.getType());      
        assertNull(document.getBaseURI());
    }
    
    
    public void testNameTokenAttributeType() 
      throws IOException, ParsingException {
        Reader reader = new StringReader(attributeDoc);
        Document document = builder.build(reader);
        Element root = document.getRootElement();
        Attribute att = root.getAttribute("nmtokenatt"); 
        assertEquals(Attribute.Type.NMTOKEN, att.getType());      
        assertEquals("1", att.getValue());      
        assertNull(document.getBaseURI());
    }
    
    
    public void testNameTokensAttributeType() 
      throws IOException, ParsingException {
        Reader reader = new StringReader(attributeDoc);
        Document document = builder.build(reader);
        Element root = document.getRootElement();
        Attribute att = root.getAttribute("nmtokensatt"); 
        assertEquals(Attribute.Type.NMTOKENS, att.getType());      
        assertEquals("1 2 3", att.getValue());      
        assertNull(document.getBaseURI());
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
        assertNull(document.getBaseURI());
    }
    
    
    public void testIDREFAttributeType() 
      throws IOException, ParsingException {
        Reader reader = new StringReader(attributeDoc);
        Document document = builder.build(reader);
        Element root = document.getRootElement();
        Attribute att = root.getAttribute("idrefatt"); 
        assertEquals(Attribute.Type.IDREF, att.getType());      
        assertEquals("p1", att.getValue());      
        assertNull(document.getBaseURI());
    }
    
    
    public void testIDREFSAttributeType() 
      throws IOException, ParsingException {
        Reader reader = new StringReader(attributeDoc);
        Document document = builder.build(reader);
        Element root = document.getRootElement();
        Attribute att = root.getAttribute("idrefsatt"); 
        assertEquals(Attribute.Type.IDREFS, att.getType());      
        assertEquals("p1 p2", att.getValue());      
        assertNull(document.getBaseURI());
    }

    
    public void testBuildFromReader() 
      throws IOException, ParsingException {
        Reader reader = new StringReader(source);
        Document document = builder.build(reader);
        verify(document);        
        assertNull(document.getBaseURI());
    }
    
    
    public void testBuildFromReaderWithBase()
      throws IOException, ParsingException {
        Reader reader = new StringReader(source);
        Document document = builder.build(reader, base);
        verify(document);        
        assertEquals(base, document.getBaseURI());
        
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
        assertNull(document.getBaseURI());
    }

    
    public void testBuildFromStringWithBase()
      throws IOException, ParsingException {
        Document document = builder.build(source, base);
        verify(document);       
        assertEquals(base, document.getBaseURI());  
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
        assertNull(document.getBaseURI());    
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
        assertNull(document1.getBaseURI());
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
        assertNull(document.getBaseURI());
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
            crimson = XMLReaderFactory.createXMLReader("org.apache.crimson.parser.XMLReaderImpl");
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

    
    public void testEnumerationAttributeType()
      throws IOException, ParsingException {
        
        XMLReader crimson;
        try {
            crimson = XMLReaderFactory.createXMLReader("org.apache.crimson.parser.XMLReaderImpl");
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
            xerces = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
        } 
        catch (SAXException ex) {
            // can't test Crimson if you can't load it
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
    
    
    public void testValidateFromStringWithNullBase()
      throws IOException, ParsingException {
        Document document = validator.build(validDoc, null);    
        assertNull(document.getBaseURI());  
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
            assertTrue(ex.getErrorCount() > 0);
            for (int i = 0; i < ex.getErrorCount(); i++) {
                assertNotNull(ex.getValidityError(i));
                assertTrue(ex.getLineNumber(i) >= -1);   
                assertTrue(ex.getColumnNumber(i) >= -1);   
            }   
            if (!xercesBroken) {
                Document doc = builder.build(new ByteArrayInputStream(source.getBytes("UTF-8")), base); 
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
                Document doc = builder.build(new ByteArrayInputStream(source.getBytes("UTF-8"))); 
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
    

    public void testSkippedEntities()
      throws IOException, ParsingException, SAXException {
        
        String doc = "<!DOCTYPE root SYSTEM 'http://www.cafeaulait.org/DTD/none.dtd'>"
          + "<root>&none;</root>";
        XMLReader xerces = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
        Builder builder = new Builder(xerces);
        
        xerces.setFeature("http://xml.org/sax/features/external-general-entities", false);
        xerces.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        
        Document result = builder.build(doc, "http://www.example.org/");
        
    }   
        
        
    public void testInvalidDocWithCrimson()
      throws IOException, ParsingException {
        
        XMLReader crimson;
        try {
            crimson = XMLReaderFactory.createXMLReader("org.apache.crimson.parser.XMLReaderImpl");
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
        
        String str = "<?xml version='1.0' encoding='ISO8859_1'?>" +            "<root>é</root>"; 
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
        builder.build(new File("data/baserelative/test.xml"));
    }
    
    
    // make sure transcoders on input are using normalization
    // form C when converting from other encodings
    public void testNFC()
      throws IOException, ParsingException {
        
        Document doc = builder.build(new File("data/nfctest.xml"));
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
        File input = new File("data/entitytest.xml");
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
        File input = new File("data/externalDTDtest.xml");
        Builder builder = new Builder(false);
        Document doc = builder.build(input);
        assertEquals(2, doc.getChildCount());
        Element root = doc.getRootElement();
        Attribute name = root.getAttribute("name");
        assertEquals("value", name.getValue());
        DocType doctype = doc.getDocType();
        assertEquals("", doctype.getInternalDTDSubset());
    }

    
    // Not really ignore, simple resolve, and not 
    // treat specially otherwise; i.e. don't copy
    // parameter entity declarations into the internal
    // DTD subset string
    public void testIgnoreInternalParameterEntitiesInInternalDTDSubset()
      throws IOException, ParsingException {
        Builder builder = new Builder(false);
        Document doc = builder.build("<!DOCTYPE root [" +
                "<!ENTITY % name \"PCDATA\">" +
                "]><root/>", "http://www.example.com/");
        assertEquals(2, doc.getChildCount());
        DocType doctype = doc.getDocType();
        assertEquals("", doctype.getInternalDTDSubset());
    }
    
    
    public void testIgnoreExternalParameterEntitiesInInternalDTDSubset()
      throws IOException, ParsingException {
        Builder builder = new Builder(false);
        Document doc = builder.build("<!DOCTYPE root [" +
                "<!ENTITY % name SYSTEM \"http://www.example.org/\">" +
                "]><root/>", "http://www.example.com/");
        assertEquals(2, doc.getChildCount());
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
        File input = new File("data/internaldtdsubsettest.xml");
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
            assertFalse(factory.getClass().getName().endsWith("NonVerifyingFactory"));
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
       filter.setParent(XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser"));
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
        }
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
            crimson = XMLReaderFactory.createXMLReader("org.apache.crimson.parser.XMLReaderImpl");
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
        }
        
    }
    
    
    public void testBuildMalformedDocumentWithBadUnicodeData() 
      throws IOException {
        
        File f = new File("data/xmlconf/xmltest/not-wf/sa/170.xml");
        if (f.exists()) {
            try {
                builder.build(f);   
                fail("Allowed malformed doc");
            }
            catch (ParsingException success) {
                assertNotNull(success.getMessage());
            }
        }
        
    }
    
    
    public void testBuildAnotherMalformedDocumentWithBadUnicodeData() 
      throws IOException {
        
        File f = new File("data//oasis/p02fail30.xml");
        if (f.exists()) {
            try {
                builder.build(f);   
                fail("Allowed malformed doc");
            }
            catch (ParsingException success) {
                assertNotNull(success.getMessage());
            }
        }
        
    }
    
    
    public void testBuildMalformedDocumentWithBadParser() 
      throws ParsingException, IOException {
        
        try {
            XMLReader parser = new CustomReader();
            Builder builder = new Builder(parser);
            builder.build("data doesn't matter");
        }
        catch (IllegalNameException success) {
            assertNotNull(success.getMessage());
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
            crimson = XMLReaderFactory.createXMLReader("org.apache.crimson.parser.XMLReaderImpl");
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
        }
        
    }   
    
    
    // from XML Conformance Test Suite; James Clark test
    // valid 097
    public void testLineBreaksInInternalDTDSubset()
      throws ParsingException, IOException {
        
        Document doc = builder.build(new File("data/097.xml"));
        String expectedResult = "<?xml version=\"1.0\"?>\r\n"
            + "<!DOCTYPE doc [\r\n"
            + "  <!ELEMENT doc (#PCDATA)>\r\n"
            + "  <!ATTLIST doc a1 CDATA \"v1\">\r\n"
            + "  <!ATTLIST doc a2 CDATA #IMPLIED>\r\n"
            + "]>\r\n"
            + "<doc a1=\"v1\" />\r\n";
        String actual = doc.toXML();
        assertEquals(expectedResult, actual);
        
    }
  
    
}
