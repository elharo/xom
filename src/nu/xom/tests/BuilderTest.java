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
import java.io.StringReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Comment;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
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
 * @version 1.0d22
 *
 */
public class BuilderTest extends XOMTestCase {

    public BuilderTest(String name) {
        super(name);   
    }
    
    private String elementDeclaration = "<!ELEMENT root (#PCDATA)>";
    private String defaultAttributeDeclaration 
      = "<!ATTLIST test name CDATA \"value\">";
    private String attributeDeclaration 
      = "<!ATTLIST root anattribute CDATA #REQUIRED>";
    private String attributeDeclaration2 
      = "<!ATTLIST root anotherattribute CDATA \"value\">";
    private String unparsedEntityDeclaration 
      = "<!ENTITY hatch-pic SYSTEM \"http://www.example.com/images/cup.gif\" NDATA gif>";
    private String internalEntityDeclaration 
      = "<!ENTITY Pub-Status \"This is a pre-release of the specification.\">";
    private String externalEntityDeclarationPublic = 
      "<!ENTITY open-hatch " 
      + "PUBLIC \"-//Textuality//TEXT Standard open-hatch boilerplate//EN\" "
      + "\"http://www.textuality.com/boilerplate/OpenHatch.xml\">";
    private String externalEntityDeclarationSystem = 
      "<!ENTITY test " 
      + "SYSTEM \"http://www.textuality.com/boilerplate/OpenHatch.xml\">";
    private String notationDeclarationSystem = "<!NOTATION ISODATE SYSTEM "
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
     + "\r\n<em id=\"p1\" xmlns:none=\"http://www.example.com\">very important</em>"
     + "<span xlink:type='simple'>here&apos;s the link</span>\r\n"
     + "<svg:svg xmlns:svg='http://www.w3.org/TR/2000/svg'><svg:text>text in a namespace</svg:text></svg:svg>\r\n"
     + "<svg xmlns='http://www.w3.org/TR/2000/svg'><text>text in a namespace</text></svg>"
     + "</test>\r\n"
     + "<!--epilog-->";
     
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

    protected void setUp() {
    }

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
        StringReader reader1 = new StringReader(attributeDoc);
        Document document = builder.build(reader1);
        Element root = document.getRootElement();
        Attribute att = root.getAttribute("notationatt"); 
        assertEquals(Attribute.Type.NOTATION, att.getType());      
        assertNull(document.getBaseURI());
    }
    
    public void testCDATAAttributeType() 
      throws IOException, ParsingException {
        StringReader reader1 = new StringReader(attributeDoc);
        Document document = builder.build(reader1);
        Element root = document.getRootElement();
        Attribute att = root.getAttribute("cdataatt"); 
        assertEquals(Attribute.Type.CDATA, att.getType());      
        assertNull(document.getBaseURI());
    }
    
    public void testEntityAttributeType() 
      throws IOException, ParsingException {
        StringReader reader1 = new StringReader(attributeDoc);
        Document document = builder.build(reader1);
        Element root = document.getRootElement();
        Attribute att = root.getAttribute("entityatt"); 
        assertEquals(Attribute.Type.ENTITY, att.getType());      
        assertNull(document.getBaseURI());
    }
    
    public void testEntitiesAttributeType() 
      throws IOException, ParsingException {
        StringReader reader1 = new StringReader(attributeDoc);
        Document document = builder.build(reader1);
        Element root = document.getRootElement();
        Attribute att = root.getAttribute("entitiesatt"); 
        assertEquals(Attribute.Type.ENTITIES, att.getType());      
        assertNull(document.getBaseURI());
    }
    
    public void testNameTokenAttributeType() 
      throws IOException, ParsingException {
        StringReader reader1 = new StringReader(attributeDoc);
        Document document = builder.build(reader1);
        Element root = document.getRootElement();
        Attribute att = root.getAttribute("nmtokenatt"); 
        assertEquals(Attribute.Type.NMTOKEN, att.getType());      
        assertEquals("1", att.getValue());      
        assertNull(document.getBaseURI());
    }
    
    public void testNameTokensAttributeType() 
      throws IOException, ParsingException {
        StringReader reader1 = new StringReader(attributeDoc);
        Document document = builder.build(reader1);
        Element root = document.getRootElement();
        Attribute att = root.getAttribute("nmtokensatt"); 
        assertEquals(Attribute.Type.NMTOKENS, att.getType());      
        assertEquals("1 2 3", att.getValue());      
        assertNull(document.getBaseURI());
    }
    
    public void testIDAttributeType() 
      throws IOException, ParsingException {
        StringReader reader1 = new StringReader(attributeDoc);
        Document document = builder.build(reader1);
        Element root = document.getRootElement();
        Attribute att = root.getAttribute("idatt"); 
        assertEquals(Attribute.Type.ID, att.getType());      
        assertEquals("p1", att.getValue());      
        assertNull(document.getBaseURI());
    }
    
    public void testIDREFAttributeType() 
      throws IOException, ParsingException {
        StringReader reader1 = new StringReader(attributeDoc);
        Document document = builder.build(reader1);
        Element root = document.getRootElement();
        Attribute att = root.getAttribute("idrefatt"); 
        assertEquals(Attribute.Type.IDREF, att.getType());      
        assertEquals("p1", att.getValue());      
        assertNull(document.getBaseURI());
    }
    
    public void testIDREFSAttributeType() 
      throws IOException, ParsingException {
        StringReader reader1 = new StringReader(attributeDoc);
        Document document = builder.build(reader1);
        Element root = document.getRootElement();
        Attribute att = root.getAttribute("idrefsatt"); 
        assertEquals(Attribute.Type.IDREFS, att.getType());      
        assertEquals("p1 p2", att.getValue());      
        assertNull(document.getBaseURI());
    }
    


    public void testBuildFromReader() 
      throws IOException, ParsingException {
        StringReader reader1 = new StringReader(source);
        Document document = builder.build(reader1);
        verify(document);        
        assertNull(document.getBaseURI());
    }
    
    public void testBuildFromReaderWithBase()
      throws IOException, ParsingException {
        StringReader reader1 = new StringReader(source);
        Document document = builder.build(reader1, base);
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
        assertTrue(document.getChild(4) instanceof nu.xom.Comment);
        assertTrue(document.getChild(2) instanceof nu.xom.Comment);
        assertEquals(" test ", document.getChild(2).getValue());
        assertEquals("epilog", document.getChild(4).getValue());
        assertTrue(document.getChild(1) instanceof nu.xom.ProcessingInstruction);
        assertEquals("test", doctype.getRootElementName());
        assertNull(doctype.getPublicID());
        assertNull(doctype.getSystemID());
        
        String internalDTDSubset = doctype.getInternalDTDSubset();
        assertTrue(internalDTDSubset, internalDTDSubset.indexOf(elementDeclaration) > 0);
        assertTrue(internalDTDSubset, internalDTDSubset.indexOf(attributeDeclaration) > 0);
        assertTrue(internalDTDSubset, internalDTDSubset.indexOf(attributeDeclaration2) > 0);
        assertTrue(internalDTDSubset, internalDTDSubset.indexOf(internalEntityDeclaration) > 0);
        assertTrue(internalDTDSubset, internalDTDSubset.indexOf(externalEntityDeclarationPublic) > 0);
        assertTrue(internalDTDSubset, internalDTDSubset.indexOf(externalEntityDeclarationSystem) > 0);
        assertTrue(internalDTDSubset, internalDTDSubset.indexOf(unparsedEntityDeclaration) > 0);
        assertTrue(internalDTDSubset, internalDTDSubset.indexOf(notationDeclarationPublic) > 0);
        assertTrue(internalDTDSubset, internalDTDSubset.indexOf(notationDeclarationSystem) > 0);
               
    }

    public void testValidateFromReader() 
      throws IOException, ParsingException {
        StringReader reader1 = new StringReader(validDoc);
        Document document = validator.build(reader1);       
        assertNull(document.getBaseURI());
    }
    
    public void testValidateFromReaderWithBase()
      throws IOException, ParsingException {
        StringReader reader1 = new StringReader(validDoc);
        Document document = validator.build(reader1, base); 
        assertEquals(base, document.getBaseURI());
        
    }
    
    public void testValidateFromInputStreamWithBase()
      throws IOException, ParsingException {
        InputStream in = new ByteArrayInputStream(validDoc.getBytes("UTF-8"));
        Document document = validator.build(in, base);  
        assertEquals(base, document.getBaseURI());  
    }
    
    public void testValidateFromInputStreamWithoutBase()
      throws IOException, ParsingException {
        InputStream in = new ByteArrayInputStream(validDoc.getBytes("UTF-8"));
        Document document = validator.build(in);        
        assertNull(document.getBaseURI());
    }

    public void testValidateFromStringWithBase()
      throws IOException, ParsingException {
        Document document = validator.build(validDoc, base);        
        assertEquals(base, document.getBaseURI());  
    }
    
    public void testValidateFromStringWithNullBase()
      throws IOException, ParsingException {
        Document document = validator.build(validDoc, null);    
        assertNull(document.getBaseURI());  
    }


    public void testCannotBuildNamespaceMalformedDocument()
      throws IOException {
        try {
            builder.build("<root:root/>", null);
            fail("Builder allowed undeclared prefix");
        }
        catch (ParsingException ex) {
            // success    
        }        
    }

    public void testInvalidDocFromReader() 
      throws IOException, ParsingException {
        StringReader reader1 = new StringReader(source);
        try {
            validator.build(reader1);   
            fail("Allowed invalid doc");
        }
        catch (ValidityException ex) {
             // success   
        }
    }
    
    public void testInvalidDocFromReaderWithBase()
      throws IOException, ParsingException {
        StringReader reader1 = new StringReader(source);
        try {
            validator.build(reader1, base); 
            fail("Allowed invalid doc");
        }
        catch (ValidityException ex) {
             // success   
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
             // success   
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
             // success   
        }
    }

    public void testInvalidDocFromStringWithBase()
      throws IOException, ParsingException {
        try {
            validator.build(source, base);        
            fail("Allowed invalid doc");
        }
        catch (ValidityException ex) {
             // success   
        }
    }
    
    public void testInvalidDocFromStringWithNullBase()
      throws IOException, ParsingException {
        try {
            validator.build(source, null);    
            fail("Allowed invalid doc");
        }
        catch (ValidityException ex) {
             // success   
        }
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
    
   // This test exposes a bug in Crimson and Xerces 
   // and possibly other parsers. I've reported the bug in Xerces.
   // I don't have a workaround for this yet.
   /* public void testBaseRelativeResolutionRemotelyWithDirectory()
      throws IOException, ParsingException {
        Document doc = builder.build("http://www.ibiblio.org/xml");
   } */

}
