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

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.xml.sax.Attributes;
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
 * @version 1.0d23
 *
 */
public class BuilderTest extends XOMTestCase {

    public BuilderTest(String name) {
        super(name);   
    }
    
    // flag to turn on and off tests based on 
    // http://nagoya.apache.org/bugzilla/show_bug.cgi?id=24124
    private boolean xercesBroken = true;
    
    private String elementDeclaration = "<!ELEMENT root (#PCDATA)>";
    private String defaultAttributeDeclaration 
      = "<!ATTLIST test name CDATA \"value\">";
    private String attributeDeclaration 
      = "<!ATTLIST root anattribute CDATA #REQUIRED>";
    private String attributeDeclaration2 
      = "<!ATTLIST root anotherattribute CDATA \"value\">";
    private String unparsedEntityDeclaration 
      = "<!ENTITY hatch-pic SYSTEM " +        "\"http://www.example.com/images/cup.gif\" NDATA gif>";
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
     + notationDeclarationPublic + "\n"
     + notationDeclarationSystem + "\n"
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
            fail("XML 1.1 allowed");
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
            fail("XML 1.1 allowed");
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
          internalDTDSubset.indexOf(notationDeclarationPublic) > 0
        );
        assertTrue(
          internalDTDSubset, 
          internalDTDSubset.indexOf(notationDeclarationSystem) > 0
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
        catch (ParsingException ex) {
            assertNotNull(ex.getMessage());   
        }        
    }

    public void testInvalidDocFromReader() 
      throws IOException, ParsingException {
        Reader reader = new StringReader(source);
        try {
            validator.build(reader);   
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
                Document doc = builder.build(reader); 
                this.verify(ex.getDocument());
                assertEquals(doc, ex.getDocument());
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
                Document doc = builder.build(reader1, base); 
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
                Document doc = builder.build(in, base); 
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
                Document doc = builder.build(in); 
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
    
    // Do I need tests with PUBLIC IDs for these????
    
    
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
        assertNull(builder.getNodeFactory());
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
        catch (ParsingException ex) {
            assertNotNull(ex.getMessage());
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
    
}
