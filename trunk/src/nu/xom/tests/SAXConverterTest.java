/* Copyright 2003-2005 Elliotte Rusty Harold
   
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

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Comment;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.ProcessingInstruction;
import nu.xom.converters.SAXConverter;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

/**
 * <p>
 *   Basic tests for conversion from XOM trees
 *   to SAX ContentHandlers.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.1b4
 *
 */
public class SAXConverterTest extends XOMTestCase {

    
    public SAXConverterTest(String name) {
        super(name);
    }

    
    private DefaultHandler handler;
    private SAXConverter converter;
    private Builder builder = new Builder();
    
    
    protected void setUp() {
        handler = new DefaultHandler();
        converter = new SAXConverter(handler);   
    }

    
    public void testGetContentHandler() {
        assertEquals(handler, converter.getContentHandler());
    }

    
    public void testSetContentHandler() {   
        
        handler = new DefaultHandler();
        converter.setContentHandler(handler);
        assertEquals(handler, converter.getContentHandler());
        
        try {
            converter.setContentHandler(null);
            fail("Allowed null ContentHandler");
        }
        catch (NullPointerException success) {
            // success   
        }
        
    }

    
    public void testSetAndGetLexicalHandler() {
        
        LexicalHandler handler = new XMLWriter();
        converter.setLexicalHandler(handler);
        assertEquals(handler, converter.getLexicalHandler()); 
        
        converter.setLexicalHandler(null);
        assertNull(converter.getLexicalHandler()); 
        
    }
    
    
    private void convertAndCompare(Document doc) 
      throws IOException, SAXException, ParsingException {
        
        StringWriter result = new StringWriter();
        XMLWriter handler = new XMLWriter(result);
        converter.setContentHandler(handler);
        converter.setLexicalHandler(handler);
        converter.convert(doc);
        result.flush();
        result.close();
        String convertedDoc = result.toString();
        Document rebuiltDoc = builder.build(convertedDoc, doc.getBaseURI());
        assertEquals(doc, rebuiltDoc);
        
    }
    
    
    public void testSimplestDoc()  
      throws IOException, SAXException, ParsingException {
        Document doc = new Document(new Element("a"));  
        convertAndCompare(doc); 
    }
    
    
    public void testXMLBaseAttributesAreThrownAway() 
      throws SAXException {
        
        Element root = new Element("root");
        Element child = new Element("child");
        Attribute base = new Attribute("xml:base",
          "http://www.w3.org/XML/1998/namespace", "data");
        child.addAttribute(base);
        root.appendChild(child);
        Document doc = new Document(root);
        doc.setBaseURI("http://www.example.com/");
        converter.setContentHandler(new BaseChecker());
        converter.convert(doc);
        
    }

    
    private static class BaseChecker extends DefaultHandler {
        
        private Locator locator;
        
        public void setDocumentLocator(Locator locator) {
            this.locator = locator;
        }
        
        public void startElement(String localName, String qualifiedName, 
          String namespaceURI, Attributes attributes) 
          throws SAXException {
            
            if (localName.equals("root")) {
                assertEquals("http://www.example.com/", locator.getSystemId());
            }
            else if (localName.equals("child")) {
                assertEquals("http://www.example.com/data", locator.getSystemId());
            }
            
            for (int i=0; i < attributes.getLength(); i++) {
                String name = attributes.getLocalName(i);
                String uri  = attributes.getURI(i);
                if ("base".equals(name) 
                  && "http://www.w3.org/XML/1998/namespace".equals(uri)) {
                    fail("Passed xml:base attribute into SAXConverter");   
                }
            } 
            
        }
        
    }
    
    
    
    public void testDocType()  
      throws IOException, SAXException, ParsingException {
        Document doc = new Document(new Element("a")); 
        doc.setDocType(new DocType("root")); 
        convertAndCompare(doc); 
    }
    
    
    public void testProcessingInstruction() 
      throws IOException, SAXException, ParsingException {
        
        Document doc = new Document(new Element("a"));
        doc.insertChild(new ProcessingInstruction(
          "xml-stylesheet", "type=\"application/xml\" href=\"stylesheet.xsl\""), 0);  
        convertAndCompare(doc); 
        
    }
    
    
    public void testComment() 
      throws IOException, SAXException, ParsingException {
        
        Element root = new Element("root");
        root.appendChild("   Lots of random text\n\n\n  ");
        Document doc = new Document(root);
        doc.insertChild(new Comment("some comment data"), 0);  
        root.insertChild(new Comment("some comment data"), 0);  
        doc.appendChild(new Comment("some comment data"));  
        convertAndCompare(doc); 
        
    }
    
    
    public void testDefaultNamespace()  
      throws IOException, SAXException, ParsingException {
        Document doc = new Document(new Element("a", "http://www.a.com/"));  
        convertAndCompare(doc); 
    }
    
    
    public void testTextContent()  
      throws IOException, SAXException, ParsingException {
        Element root = new Element("root");
        root.appendChild("   Lots of random text\n\n\n  ");
        Document doc = new Document(root);  
        convertAndCompare(doc); 
    }
    
    
    public void testPrefixedNamespace()  
      throws IOException, SAXException, ParsingException {
        Document doc = new Document(new Element("a:a", "http://www.a.com/"));  
        convertAndCompare(doc); 
    }
    
    
    public void testAdditionalNamespace()  
      throws IOException, SAXException, ParsingException {
        
        Element root = new Element("root");
        root.addNamespaceDeclaration("xsl", "http://www.w3.org/1999/XSL/Transform");
        Document doc = new Document(root);  
        convertAndCompare(doc); 
        
    }
    
    
    public void testPrefixAndAdditionalNamespace()  
      throws IOException, SAXException, ParsingException {
        
        Element root = new Element("xsl:root", "http://www.w3.org/1999/XSL/Transform");
        root.addNamespaceDeclaration("xsl", "http://www.w3.org/1999/XSL/Transform");
        Document doc = new Document(root);  
        convertAndCompare(doc); 
        
    }
    
    
    public void testPrefixAndAdditionalNamespaceFromParser()  
      throws IOException, SAXException, ParsingException {
        Document doc = builder.build(
          "<SOAP:Envelope xmlns:SOAP='http://schemas.xmlsoap.org/soap/envelope/'/>", 
          null); 
        convertAndCompare(doc);
    }
    
    
    public void testChildElementAddsNamespace()  
      throws IOException, SAXException, ParsingException {
        
        Element root = new Element("root");
        Element child = new Element("pre:child", "http://www.example.org/");
        child.addAttribute(new Attribute("xlink:type", "http://www.w3.org/1999/xlink", "simple"));
        root.appendChild(child);
        Document doc = new Document(root);  
        convertAndCompare(doc); 
        
    }
    
    
    public void testAttributesTypes()  
      throws IOException, SAXException, ParsingException {
        
        Element root = new Element("root");
        root.addAttribute(new Attribute("CDATA", "CDATA", Attribute.Type.CDATA));
        root.addAttribute(new Attribute("ID", "ID", Attribute.Type.ID));
        root.addAttribute(new Attribute("IDREF", "IDREF", Attribute.Type.IDREF));
        root.addAttribute(new Attribute("IDRES", "IDREFS", Attribute.Type.IDREFS));
        root.addAttribute(new Attribute("NMTOKEN", "NMTOKEN", Attribute.Type.NMTOKEN));
        root.addAttribute(new Attribute("NMTOKENS", "NMTOKENS", Attribute.Type.NMTOKENS));
        root.addAttribute(new Attribute("UNDECLARED", "UNDECLARED", Attribute.Type.UNDECLARED));
        root.addAttribute(new Attribute("ENTITY", "ENTITY", Attribute.Type.ENTITY));
        root.addAttribute(new Attribute("ENTITIES", "ENTITIES", Attribute.Type.ENTITIES));
        root.addAttribute(new Attribute("NOTATION", "NOTATION", Attribute.Type.NOTATION));
        root.addAttribute(new Attribute("ENUMERATION", "ENUMERATION", Attribute.Type.ENUMERATION));
        Document doc = new Document(root);  
        convertAndCompare(doc); 
        
    }
    
    
    public void testAttributes()  
      throws IOException, SAXException, ParsingException {
        
        Element root = new Element("root");
        root.addAttribute(new Attribute("a", "test"));
        root.addAttribute(new Attribute("xlink:type", 
          "http://www.w3.org/1999/xlink", "simple"));
        Document doc = new Document(root);  
        convertAndCompare(doc); 
        
    }
    
    
    public void testExternalDTDSubset()
      throws IOException, SAXException, ParsingException {

        File input = new File("data");
        input = new File(input, "externalDTDtest.xml");
        Document doc = builder.build(input);
        convertAndCompare(doc);
        
    }
   
    
    public void testBigDoc()
      throws IOException, SAXException, ParsingException {
        Document doc = builder.build("http://www.cafeconleche.org/");
        convertAndCompare(doc);
    }

    
    public void testNoPrefixMappingEventsForDefaultEmptyNamespace() 
      throws ParsingException, IOException, SAXException {
     
        String data = "<root/>";
        Document doc = builder.build(data, null);
        ContentHandler handler = new XMLPrefixTester2();
        SAXConverter converter = new SAXConverter(handler);
        converter.convert(doc);
        
    }
    
    
    public void testNoPrefixMappingEventsForXMLPrefix() 
      throws ParsingException, IOException, SAXException {
     
        String data = "<root xml:space='preserve'/>";
        Document doc = builder.build(data, null);
        ContentHandler handler = new XMLPrefixTester();
        SAXConverter converter = new SAXConverter(handler);
        converter.convert(doc);
        
    }
    
    
    public void testNoPrefixMappingEventsForXMLPrefixOnElement() 
      throws ParsingException, IOException, SAXException {
     
        String data = "<xml:root/>";
        Document doc = builder.build(data, null);
        ContentHandler handler = new XMLPrefixTester();
        SAXConverter converter = new SAXConverter(handler);
        converter.convert(doc);
        
    }
    
 
    private static class XMLPrefixTester extends DefaultHandler {
        
        public void startPrefixMapping(String prefix, String uri) {
            if ("xml".equals(prefix)) {
                fail("start mapped prefix xml");
            }
        }
        
        public void endPrefixMapping(String prefix) {
            if ("xml".equals(prefix)) {
                fail("end mapped prefix xml");
            }
        }
        
    }
    
    
    private static class XMLPrefixTester2 extends DefaultHandler {
        
        public void startPrefixMapping(String prefix, String uri) {
            fail("start mapped prefix " + prefix);
        }
        
        public void endPrefixMapping(String prefix) {
            fail("end mapped prefix " + prefix);
        }
        
    }
    
    
    public void testNoRedundantPrefixMappingEventsForDefaultNamespace() 
      throws ParsingException, IOException, SAXException {
     
        String data = "<root xmlns='http://www.example.org'> <a> <b/> </a> </root>";
        Document doc = builder.build(data, null);
        XMLPrefixMapCounter handler = new XMLPrefixMapCounter();
        SAXConverter converter = new SAXConverter(handler);
        converter.convert(doc);
        assertEquals(1, handler.getStarts());
        assertEquals(1, handler.getEnds());
        
    }
    
    
    public void testNoRedundantPrefixMappingEventsForPrefixedNamespace() 
      throws ParsingException, IOException, SAXException {
     
        String data = "<a:root xmlns:a='http://www.example.org' />";
        Document doc = builder.build(data, null);
        XMLPrefixMapCounter handler = new XMLPrefixMapCounter();
        SAXConverter converter = new SAXConverter(handler);
        converter.convert(doc);
        assertEquals(1, handler.getStarts());
        assertEquals(1, handler.getEnds());
        
    }
    
    
    private static class XMLPrefixMapCounter extends DefaultHandler {
        
        private int starts = 0;
        private int ends = 0;
        
        public void startPrefixMapping(String prefix, String uri) {
            starts++;
        }
        
        public void endPrefixMapping(String prefix) {
            ends++;
        }
        
        int getStarts() {
            return starts;
        }
        
        int getEnds() {
            return ends;
        }
        
    }
 
    
}
