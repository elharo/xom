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
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Comment;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.ProcessingInstruction;
import nu.xom.Text;
import nu.xom.XMLException;
import nu.xom.converters.DOMConverter;

import org.w3c.dom.CDATASection;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentFragment;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * <p>
 *  Basic tests for conversion from DOM trees to XOM trees
 *  and back again.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0b4
 *
 */
public class DOMConverterTest extends XOMTestCase {

    
    public DOMConverterTest(String name) {
        super(name);   
    }

    
    private String source = "<!DOCTYPE test [ ]>\r\n"
     + "<?xml-stylesheet href=\"file.css\" type=\"text/css\"?>" 
     + "<!-- test -->"
     + "<test xmlns:xlink='http://www.w3.org/TR/1999/xlink'>Hello dear"
     + "\r\n<em id=\"p1\" "
     + "xmlns:none=\"http://www.example.com\">very important</em>"
     + "<span xlink:type='simple'>here&apos;s the link</span>\r\n"
     + "<svg:svg xmlns:svg='http://www.w3.org/TR/2000/svg'><svg:text>"
     + "text in a namespace</svg:text></svg:svg>\r\n"
     + "<svg xmlns='http://www.w3.org/TR/2000/svg'><text>text in a "
     + "namespace</text></svg>"
     + "</test>\r\n"
     + "<!--epilog-->";
     
    
    private Document xomDocument;
    private org.w3c.dom.Document domDocument;
    private DOMImplementation impl;
    private DocumentBuilder builder;


    protected void setUp() throws ParserConfigurationException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        builder = factory.newDocumentBuilder();
        impl = factory.newDocumentBuilder().getDOMImplementation();        

        DocType type = new DocType("test");
        Element root = new Element("test");          
        xomDocument = new Document(root);
        xomDocument.insertChild(type, 0);
        xomDocument.insertChild(new ProcessingInstruction(
         "xml-stylesheet", "href=\"file.css\" type=\"text/css\""), 1);
        xomDocument.insertChild(new Comment(" test "), 2);
        xomDocument.appendChild(new Comment("epilog"));
        root.addNamespaceDeclaration("xlink", 
          "http://www.w3.org/TR/1999/xlink");
        root.appendChild("Hello dear\r\n");
        Element em = new Element("em");
        root.appendChild(em);
        em.addAttribute(new Attribute("id", "p1"));
        em.addNamespaceDeclaration("none", "http://www.example.com");
        em.appendChild("very important");
        Element span = new Element("span");
        root.appendChild(span);
        span.addAttribute(new Attribute("xlink:type", 
          "http://www.w3.org/TR/1999/xlink", "simple"));
        span.appendChild("here's the link");
        root.appendChild("\r\n");

        Element empty = new Element("empty");
        root.appendChild(empty);
        empty.addAttribute(new Attribute("xom:temp", 
          "http://xom.nu/", 
          "Just to see if this can handle namespaced attributes"));
        
        root.appendChild("\r\n");
        Element svg = new Element("svg:svg", 
          "http://www.w3.org/TR/2000/svg");
        root.appendChild(svg);
        Element text = new Element("svg:text", "http://www.w3.org/TR/2000/svg");
        svg.appendChild(text);
        text.appendChild("text in a namespace");
        root.appendChild("\r\n");

        svg = new Element("svg", "http://www.w3.org/TR/2000/svg");
        root.appendChild(svg);
        text = new Element("text", "http://www.w3.org/TR/2000/svg");
        svg.appendChild(text);
        text.appendChild("text in a namespace");
        
        Reader reader = new StringReader(source);
        InputSource inso = new InputSource(reader);
        
        try {
            domDocument = builder.parse(inso);
        }
        catch (Exception ex) {
            // shouldn't happen from known good doc 
            throw new RuntimeException("Oops!");  
        }
        
    }

    
    public void testToDOM() {      
        
        org.w3c.dom.Document domDoc = DOMConverter.convert(xomDocument, impl);
        org.w3c.dom.DocumentType doctype = domDoc.getDoctype();
        
        assertEquals("test", domDoc.getDocumentElement().getNodeName());
        assertTrue(doctype != null);
        assertEquals(org.w3c.dom.Node.DOCUMENT_TYPE_NODE,
          domDoc.getFirstChild().getNodeType());
        assertEquals(org.w3c.dom.Node.COMMENT_NODE,
          domDoc.getLastChild().getNodeType());
        
    }

    
    public void testToXOM() {
        
        Document xomDoc = DOMConverter.convert(domDocument);
        DocType doctype = xomDoc.getDocType();
        Element root = xomDoc.getRootElement();

        assertEquals("test", root.getQualifiedName());
        assertEquals("test", root.getLocalName());
        assertEquals("", root.getNamespaceURI());
        
        assertTrue(doctype != null);
        assertTrue(xomDoc.getChild(0) instanceof DocType);
        assertTrue(xomDoc.getChild(4) instanceof nu.xom.Comment);
        assertTrue(xomDoc.getChild(2) instanceof nu.xom.Comment);
        assertEquals(" test ", xomDoc.getChild(2).getValue());
        assertEquals("epilog", xomDoc.getChild(4).getValue());
        assertTrue(
          xomDoc.getChild(1) instanceof nu.xom.ProcessingInstruction);
        assertEquals("test", doctype.getRootElementName());
        assertNull(doctype.getPublicID());
        assertNull(doctype.getSystemID());
       
    }

    
    public void testDefaultNamespacedElement() 
      throws SAXException, IOException, ParserConfigurationException {
        
        byte[] data = "<root xmlns=\"http://www.example.com\"/>".getBytes();
        org.w3c.dom.Document doc = builder.parse(new ByteArrayInputStream(data));
        Document xomDoc = DOMConverter.convert(doc);
        
        Element root = xomDoc.getRootElement();
        assertEquals("root", root.getQualifiedName());
        assertEquals("http://www.example.com", root.getNamespaceURI());     
        
    }

    
    public void testPrefixedElement() 
      throws SAXException, IOException, ParserConfigurationException {
        
        byte[] data = "<pre:root xmlns:pre=\"http://www.example.com\"/>".getBytes();
        org.w3c.dom.Document doc = builder.parse(new ByteArrayInputStream(data));
        Document xomDoc = DOMConverter.convert(doc);
        
        Element root = xomDoc.getRootElement();
        assertEquals("pre:root", root.getQualifiedName());
        assertEquals("http://www.example.com", root.getNamespaceURI());  
        
    }

    
    public void testConvertAttr() 
      throws SAXException, IOException, ParserConfigurationException {
        
        byte[] data = ("<element name='value' " +            "xmlns='http://example.com/' " +            "xmlns:pre='http://example.net'/>").getBytes();
        org.w3c.dom.Document doc = builder.parse(new ByteArrayInputStream(data));
          
        org.w3c.dom.Element root = doc.getDocumentElement();
        Attribute attribute = DOMConverter.convert(root.getAttributeNode("name"));
        assertEquals("name", attribute.getQualifiedName());
        assertEquals("", attribute.getNamespacePrefix());
        assertEquals("", attribute.getNamespaceURI());
        assertEquals("value", attribute.getValue());
        
        try {
            DOMConverter.convert(root.getAttributeNode("xmlns"));
            fail("Converted xmlns attribute");
        }
        catch (XMLException ex) {
           assertNotNull(ex.getMessage());   
        }
        try {
            DOMConverter.convert(root.getAttributeNode("xmlns:pre"));
            fail("Converted xmlns:pre attribute");
        }
        catch (XMLException ex) {
           assertNotNull(ex.getMessage());   
        }
                 
    }

    
    public void testConvertElement() 
      throws SAXException, IOException, ParserConfigurationException {
        
        byte[] data = ("<element name='value' " +            "xmlns='http://example.com/' " +            "xmlns:pre='http://example.net'/>").getBytes();
        org.w3c.dom.Document doc = builder.parse(new ByteArrayInputStream(data));
          
        org.w3c.dom.Element root = doc.getDocumentElement();
        Element xomRoot = DOMConverter.convert(root);
        assertEquals("name", xomRoot.getAttribute("name").getQualifiedName());
        assertEquals("", xomRoot.getAttribute("name").getNamespacePrefix());
        assertEquals("", xomRoot.getAttribute("name").getNamespaceURI());
        assertEquals("value", xomRoot.getAttribute("name").getValue());
        assertEquals("element", xomRoot.getQualifiedName());
        assertEquals("", xomRoot.getValue());
        assertEquals(0, xomRoot.getChildCount());
        assertEquals("http://example.com/", xomRoot.getNamespaceURI());
        assertEquals("http://example.net", xomRoot.getNamespaceURI("pre"));
                 
    }

    
    public void testConvertComment() 
      throws SAXException, IOException, ParserConfigurationException {

        byte[] data = "<element><!--data--></element>".getBytes();
        org.w3c.dom.Document doc = builder.parse(new ByteArrayInputStream(data));
          
        org.w3c.dom.Element root = doc.getDocumentElement();
        org.w3c.dom.Comment comment = (org.w3c.dom.Comment) (root.getChildNodes().item(0));
        Comment xomComment = DOMConverter.convert(comment);
        assertEquals(comment.getNodeValue(), xomComment.getValue());
                 
    }

    
    public void testConvertText() 
      throws SAXException, IOException, ParserConfigurationException {

        byte[] data = "<element> here's the text </element>".getBytes();
        org.w3c.dom.Document doc = builder.parse(new ByteArrayInputStream(data));
          
        org.w3c.dom.Element root = doc.getDocumentElement();
        org.w3c.dom.Text node = (org.w3c.dom.Text) (root.getChildNodes().item(0));
        Text text = DOMConverter.convert(node);
        assertEquals(node.getNodeValue(), text.getValue());
                 
    }

    
    public void testConvertCDATASection() 
      throws SAXException, IOException, ParserConfigurationException {

        byte[] data = "<element><![CDATA[ here's the text ]]></element>".getBytes();
        org.w3c.dom.Document doc = builder.parse(new ByteArrayInputStream(data));
          
        org.w3c.dom.Element root = doc.getDocumentElement();
        CDATASection node = (CDATASection) (root.getChildNodes().item(0));
        Text text = DOMConverter.convert(node);
        assertEquals(node.getNodeValue(), text.getValue());   
        
        // Now test indirect conversion
        Document xomDoc = DOMConverter.convert(doc);
        assertEquals(node.getNodeValue(), xomDoc.getValue());
                 
    }

    
    public void testConvertProcessingInstruction() 
      throws SAXException, IOException, ParserConfigurationException {

        byte[] data = "<element><?target PI data?></element>".getBytes();
        org.w3c.dom.Document doc = builder.parse(new ByteArrayInputStream(data));
          
        org.w3c.dom.Element root = doc.getDocumentElement();
        org.w3c.dom.ProcessingInstruction node 
          = (org.w3c.dom.ProcessingInstruction) (root.getChildNodes().item(0));
        ProcessingInstruction pi = DOMConverter.convert(node);
        assertEquals(node.getNodeValue(), pi.getValue());
        assertEquals(node.getTarget(), pi.getTarget());
                 
    }

    
    public void testConvertDocType() 
      throws SAXException, IOException, ParserConfigurationException {

        byte[] data = "<!DOCTYPE root ><element />".getBytes();
        org.w3c.dom.Document doc = builder.parse(new ByteArrayInputStream(data));
          
        org.w3c.dom.DocumentType type = doc.getDoctype();
        DocType xomType = DOMConverter.convert(type);
        assertEquals(type.getName(), xomType.getRootElementName());
                 
    }

   
    public void testChildElementAddsNamespace() {
        
        Element root = new Element("root");
        Element child = new Element("pre:child", "http://www.example.org/");
        child.addAttribute(new Attribute("xlink:type", "http://www.w3.org/1999/xlink", "simple"));
        root.appendChild(child);
        Document doc = new Document(root);  
        
        assertEquals(doc, DOMConverter.convert(DOMConverter.convert(doc, impl)));
        
    }
    
    
    public void testChildElementUsesSameNamespace() {
        
        Element root = new Element("pre:root", "http://www.example.org/");
        Element child = new Element("pre:child", "http://www.example.org/");
        root.appendChild(child);
        Document doc = new Document(root);  
        assertEquals(doc, DOMConverter.convert(DOMConverter.convert(doc, impl)));
        
    }
    
    
    public void testPrefixMappingChanges() {
        
        Element root = new Element("pre:root", "http://www.example.org/");
        Element child = new Element("pre:child", "http://www.example.net/");
        root.appendChild(child);
        Document doc = new Document(root);  
        assertEquals(doc, DOMConverter.convert(DOMConverter.convert(doc, impl)));
        
    }
    
    
    public void testConvertDocumentFragment() {
     
        DocumentFragment fragment = domDocument.createDocumentFragment();
        org.w3c.dom.Element element = domDocument.createElement("name");
        element.setAttribute("name", "value");
        fragment.appendChild(element);
        fragment.appendChild(domDocument.createComment("data"));  
        fragment.appendChild(domDocument.createTextNode("text"));
        
        Nodes result = DOMConverter.convert(fragment);
        
        Element first = (Element) result.get(0);
        assertEquals("name", first.getQualifiedName());
        assertEquals("name", first.getAttribute("name").getQualifiedName());
        assertEquals("value", first.getAttribute("name").getValue());
        
        Comment second = (Comment) result.get(1);
        assertEquals("data", second.getValue());
        
        Text third = (Text) result.get(2);
        assertEquals("text", third.getValue());
        
    }
    
    
    // regression found by Wolfgang Hoscheck
    public void testWolfgang() throws ParsingException, IOException {
        
        String data = "<m>" 
         + "<a code='3.1415292'>" 
         + "<u>http://www.example.com</u>" 
         + "<h>Some data</h>"
         + "<s>StockAccess</s>"
         + "</a>"
         + "</m>";

        Document doc = new Builder().build(data, null);       
        Element article = doc.getRootElement().getChildElements().get(0);
        Document temp = new Document(new Element(article));
        
        org.w3c.dom.Element domArticle 
          = DOMConverter.convert(temp, impl).getDocumentElement();
        assertEquals(3, domArticle.getChildNodes().getLength());
        Element out = DOMConverter.convert(domArticle);
        assertEquals(article, out);
        
    }
    
    
    public void testConvertNullXOMDocument() {
     
        try {
            DOMConverter.convert((Document) null, impl);
            fail("Converted null document");
        }
        catch (NullPointerException success) {
        }
        
        
    }
 
    
    public void testConvertNullDOMElement() {
     
        try {
            DOMConverter.convert((org.w3c.dom.Element) null);
            fail("Converted null element");
        }
        catch (NullPointerException success) {
        }
        
        
    }
 
    
    public void testConvertNullDOMDocument() {
     
        try {
            DOMConverter.convert((org.w3c.dom.Document) null);
            fail("Converted null element");
        }
        catch (NullPointerException success) {
        }
        
        
    }
    
    
    /**
     * Test a bug in a document provided by Wolfgang Hoschek.
     */
    public void testFullHoschek() throws ParsingException, IOException {
        
        File f = new File("data/hoschektest.xml");
        Builder builder = new Builder();
        Document xomDocIn = builder.build(f);
        org.w3c.dom.Document domDoc = DOMConverter.convert(xomDocIn, impl);
        org.w3c.dom.Element domRoot = domDoc.getDocumentElement();
        Document xomDocOut = DOMConverter.convert(domDoc);
        assertEquals(xomDocIn, xomDocOut);
        
    } 
 
    
    /**
     * Test a bug in identified by Wolfgang Hoschek.
     */
    public void testSimplifiedHoschekDOMToXOM() 
      throws SAXException, IOException {
         
        File f = new File("data/simplehoschektest.xml");
        org.w3c.dom.Document domDocIn = builder.parse(f);
        int domRootNumber = domDocIn.getDocumentElement().getChildNodes().getLength(); 
        Document xomDoc = DOMConverter.convert(domDocIn);
        Element xomRoot = xomDoc.getRootElement();
        assertEquals(domRootNumber, xomRoot.getChildCount());
        
    }
    
    
    public void testChildNodesRemainChildNodes() 
      throws ParsingException, IOException {
        
        String data = "<root><scope><a>1</a><b>2</b><c /></scope></root>";
        Builder builder = new Builder();
        Document xomDocIn = builder.build(data, null);
        int xomRootNumber = xomDocIn.getRootElement().getChildCount(); 
        org.w3c.dom.Document domDoc = DOMConverter.convert(xomDocIn, impl);
        org.w3c.dom.Element domRoot = domDoc.getDocumentElement();
        assertEquals(xomRootNumber, domRoot.getChildNodes().getLength());
        Document xomDocOut = DOMConverter.convert(domDoc);
        assertEquals(xomDocIn, xomDocOut);
        
    } 
 
    
    public void testLastChildNodeIsLeafNode() 
      throws ParsingException, IOException {
        
        String data = "<root><top><empty />ABCD</top></root>";
        Builder builder = new Builder();
        Document xomDocIn = builder.build(data, null);
        int xomRootNumber = xomDocIn.getRootElement().getChildCount(); 
        org.w3c.dom.Document domDoc = DOMConverter.convert(xomDocIn, impl);
        org.w3c.dom.Element domRoot = domDoc.getDocumentElement();
        org.w3c.dom.Node domTop = domRoot.getFirstChild();
        assertEquals(2, domTop.getChildNodes().getLength());
        
    } 
    
    
    public void testNamespaceAttributes() {
     
        Element root = new Element("root", "http://www.example.org/");
        root.appendChild("text");
        Document xomDocIn = new Document(root);
        org.w3c.dom.Document domDoc = DOMConverter.convert(xomDocIn, impl);
        org.w3c.dom.Element domRoot = domDoc.getDocumentElement();
        assertTrue(domRoot.hasAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns"));
        
    }
 
    
    public void testConvertSingleElementDocumentFromXOMToDOM() {
     
        Element root = new Element("root");
        Document xomDocIn = new Document(root);
        org.w3c.dom.Document domDoc = DOMConverter.convert(xomDocIn, impl);
        org.w3c.dom.Element domRoot = domDoc.getDocumentElement();
        assertEquals(0, domRoot.getChildNodes().getLength());
        assertEquals("root", domRoot.getNodeName());
        
    }
 
    
    public void testConvertSingleElementDocumentFromDOMToXOM() 
      throws SAXException, IOException {
     
        byte[] data = "<element />".getBytes();
        org.w3c.dom.Document doc = builder.parse(new ByteArrayInputStream(data));
        Document xomDoc = DOMConverter.convert(doc);
        Element root = xomDoc.getRootElement();
        assertEquals(0, root.getChildCount());
        assertEquals("element", root.getLocalName());
        
    }
 
    
}
