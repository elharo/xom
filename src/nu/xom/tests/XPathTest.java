/* Copyright 2005 Elliotte Rusty Harold
   
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
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Comment;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Namespace;
import nu.xom.NamespaceConflictException;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.ProcessingInstruction;
import nu.xom.Text;
import nu.xom.XPathContext;
import nu.xom.XPathException;
import nu.xom.XPathTypeException;

/**
 * <p>
 * Unit tests for XPath functionality
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.1a2
 *
 */
public class XPathTest extends XOMTestCase {
    
    public XPathTest(String name) {
        super(name);
    }
    
    
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
    
    public void testAllNodesQuery() {
        
        Document doc = new Document(new Element("doc"));
        Nodes subset = doc.query("//. | /");
        assertEquals(2, subset.size());
        
    }
    
    
    /* <a>
<x>1</x>
<b>
<x>2</x>
<x>3</x>
</b>
<x>4</x>
</a> */
    public void testDescendantAxisOrder() {
        
        Element a = new Element("a");
        Document doc = new Document(a);
        Element x1 = new Element("x");
        x1.appendChild("a1");
        Element x2 = new Element("x");
        x2.appendChild("b2");
        Element x3 = new Element("x");
        x3.appendChild("c3");
        Element x4 = new Element("x");
        x4.appendChild("d4");
        a.appendChild(x1);
        Element b = new Element("b");
        b.appendChild(x2);
        b.appendChild(x3);
        a.appendChild(b);
        a.appendChild(x4);
        Nodes result = doc.query("//x");
        assertEquals(4, result.size());
        assertTrue(result.get(0) instanceof Element);
        assertTrue(result.get(1) instanceof Element);
        assertTrue(result.get(2) instanceof Element);
        assertTrue(result.get(3) instanceof Element);
        assertEquals(x1, result.get(0));   
        assertEquals(x2, result.get(1));
        assertEquals(x3, result.get(2));
        assertEquals(x4, result.get(3));
        
    }
    

    public void testSimpleQuery() {
        
        Element parent = new Element("Test");
        Element child = new Element("child");
        parent.appendChild(child);
        
        Nodes result = parent.query("*");
        assertEquals(1, result.size());
        assertEquals(child, result.get(0));   
        
    }
    
    
    public void testChildWildCard() {
     
        Element a = new Element("a");
        Element b = new Element("b");
        Element c = new Element("c");
        Element d = new Element("d");
        
        a.appendChild(b);
        b.appendChild(c);
        c.appendChild(d);
        
        Nodes result = a.query("/a/b/*/d");
        assertEquals(d, result.get(0));
        
    }
    

    public void testLongs() {
        
        Document doc = new Document(new Element("root"));
        
        Nodes result = doc.query("/*[5000000000=5000000000]");
        assertEquals(1, result.size());
        
        result = doc.query("/*[5000000000 < 5000000001]");
        assertEquals(1, result.size());
        result = doc.query("/*[5000000000 > 5000000001]");
        assertEquals(0, result.size());
        result = doc.query("/*[5000000001 >= 5000000001]");
        assertEquals(1, result.size());
        result = doc.query("/*[5000000001 > 5000000001]");
        assertEquals(0, result.size());
        
    }
    

    public void testNamespaceNodeParent() {
        
        Element root = new Element("root");
        Document doc = new Document(root);
        Element child = new Element("pre:child", "http://www.ietf.org");
        root.appendChild(child);
        
        Nodes result = doc.query("/root/*/namespace::*/parent::*");
        assertEquals(1, result.size());
        assertEquals(child, result.get(0));
        
    }
    

    public void testNamespaceNodeChild() {
        
        Element root = new Element("root");
        Document doc = new Document(root);
        Element child = new Element("pre:child", "http://www.ietf.org");
        root.appendChild(child);
        
        Nodes result = doc.query("/root/*/namespace::*/child::*");
        assertEquals(0, result.size());
        
    }
    

    // JAXEN-46: http://jira.codehaus.org/browse/JAXEN-46
    public void testHashInAttributeValue() {
        
        Element root = new Element("test");
        Document doc = new Document(root);
        Attribute test = new Attribute("test", "SUBSCRIBER");
        Attribute test1 = new Attribute("test1", "SUBSCRIBER#");
        root.addAttribute(test);
        root.addAttribute(test1);
        
        Nodes result = doc.query("test[@test='SUBSCRIBER']");
        assertEquals(1, result.size());
        assertEquals(root, result.get(0));
        
        result = doc.query("test[@test1='SUBSCRIBER#']");
        assertEquals(1, result.size());
        assertEquals(root, result.get(0));
        
    }
    

    public void testUseRootNodeWhenQueryingDocumentLessElements() {
        
        Element test = new Element("Test");
        
        Nodes result = test.query("/*");
        assertEquals(1, result.size());
        assertEquals(test, result.get(0));   
        
        try {
            test.query("/");
            fail("Did not throw exception when querying rootless document for root");
        }
        catch (XPathException success) {
            assertNotNull(success.getMessage());
        }
        
    }
    

    public void testUseRootNodeWhenQueryingDocumentLessElements2() {
        
        Element test = new Element("Test");
        Nodes result = test.query("/None");
        assertEquals(0, result.size());
        
    }
    

    public void testUseRootNodeWhenQueryingDocumentLessElements3() {
        
        Element test = new Element("Test");  
        
        Nodes result = test.query("//*");
        assertEquals(1, result.size());
        
    }
    

    public void testUseRootNodeWhenQueryingDocumentLessElements4() {
        
        Element test = new Element("Test");  
        
        Nodes result = test.query("//* | /");
        assertEquals(1, result.size());
        
    }
    

    public void testUnionOfNodesWithInts() {
        
        Element parent = new Element("Test");
        Element child = new Element("child");
        parent.appendChild(child);
        
        try {
            parent.query("* | count(/*)");
            fail("Allowed query returning non-node-set");
        }
        catch (XPathException success) {
            assertNotNull(success.getMessage());
        }
        
    }
    
    
    public void testFranceschet1() throws ParsingException, IOException {
     
        Builder builder = new Builder();
        Document doc = builder.build("http://staff.science.uva.nl/~francesc/xpathmark/benchmark_canon.xml");
        Element root = doc.getRootElement();
        Element input1 = root.getFirstChildElement("document_1").getFirstChildElement("site");
        input1.detach();
        Element input2 = root.getFirstChildElement("document_2");
        input2.detach();
        
        Nodes doc1Queries = root.query("child::query[starts-with(@id, 'Q')]");
        
        for (int i = 0; i < doc1Queries.size(); i++) {
            Element query = (Element) doc1Queries.get(i);
            String xpath = query.getFirstChildElement("syntax").getValue();
            String id = query.getAttributeValue("id");
            // this query needs special comparison code due to 
            // adjacent text nodes
            if ("Q21".equals(id)) continue;
            // test suite bug: missing white space in mailbox 
            // element in item 3 in result
            else if ("Q32".equals(id)) continue;
            else if ("Q42".equals(id)) continue;
            // test suite bug relating to id() function
            else if (xpath.indexOf("id(") >= 0) continue;
            Nodes result = input1.query(xpath);
            Element xpath_result = query.getFirstChildElement("XPATH_RESULT");
            Nodes expected = new Nodes();
            for (int j = 0; j < xpath_result.getChildCount(); j++) {
                Node node = xpath_result.getChild(j);
                if (node instanceof Text) {
                    if (!("".equals(node.getValue().trim()))) {
                        expected.append(node);
                    }
                }
                else {
                    expected.append(node);
                }
            }
            assertEquals("Failed query " + id, expected.size(), result.size());
            for (int j = 0; j < result.size(); j++) {
                Node expectedNode = expected.get(j);
                Node actualNode = result.get(j);                
                assertEquals(id + " " + expectedNode.toXML() + " " + actualNode.toXML(), expectedNode, actualNode);
            }
        }
        
    }
    

    public void testFranceschet2() throws ParsingException, IOException {
     
        Builder builder = new Builder();
        Document doc = builder.build("http://staff.science.uva.nl/~francesc/xpathmark/benchmark_canon.xml");
        Element root = doc.getRootElement();
        Element input = root.getFirstChildElement("document_2");
        Document html = new Document(new Element("fake"));
        int p = 0;
        while (true) {
            Node node = input.getChild(0);
            if (node instanceof Element) break;
            else {
                node.detach();
                if (node instanceof Text) continue;
                html.insertChild(node, p++);
            }
        }    
        Node newroot = input.getChild(0);
        newroot.detach();
        html.setRootElement((Element) newroot);
        while (input.getChildCount() > 0) {
            Node node = input.getChild(0);
            node.detach();
            if (node instanceof Text) continue;
            html.appendChild(node);
        }    
        
        Nodes doc2Queries = root.query("child::query[starts-with(@id, 'A')]");
        
        for (int i = 0; i < doc2Queries.size(); i++) {
            Element query = (Element) doc2Queries.get(i);
            String xpath = query.getFirstChildElement("syntax").getValue();
            String id = query.getAttributeValue("id");
            // test suite bug; should be fixed soon
            if ("A2".equals(id)) continue;
            // ???? Not sure what's going on here; need to figure it out
            else if ("A3".equals(id)) continue;
            else if ("A4".equals(id)) continue;
            // this query requires us to map the XLink prefix
            else if ("A5".equals(id)) continue;
            // this query requires us to map the SVG prefix
            else if ("A6".equals(id)) continue;
            else if ("A8".equals(id)) continue;

            Nodes result = html.query(xpath);
            Element xpath_result = query.getFirstChildElement("XPATH_RESULT");
            Nodes expected = new Nodes();
            for (int j = 0; j < xpath_result.getChildCount(); j++) {
                Node node = xpath_result.getChild(j);
                if (node instanceof Text) {
                    if (!("".equals(node.getValue().trim()))) {
                        expected.append(node);
                    }
                }
                else {
                    expected.append(node);
                }
            }
            assertEquals("Failed query " + id, expected.size(), result.size());
            for (int j = 0; j < result.size(); j++) {
                Node expectedNode = expected.get(j);
                Node actualNode = result.get(j);                
                assertEquals(id + " " + expectedNode.toXML() + " " + actualNode.toXML(), expectedNode, actualNode);
            }
        }
        
    }
    

    public void testQueryThatReturnsNumber() {
        
        Element parent = new Element("Test");
        Element child = new Element("child");
        parent.appendChild(child);
        
        try {
            parent.query("count(*)");
            fail("Allowed query to return number");
        }
        catch (XPathTypeException success) {
            assertNotNull(success.getMessage());
            assertEquals(new Double(1), success.getReturnValue());
            assertEquals("count(*)", success.getXPath());
        }
        
    }
    

    public void testEmptyTextNodesDontCount() {
        
        Element parent = new Element("Test");
        Element child1 = new Element("child1");
        parent.appendChild(child1);
        parent.appendChild(new Text(""));
        parent.appendChild(new Text(""));
        Element child2 = new Element("child2");
        parent.appendChild(child2);
        
        Nodes result = parent.query("*");
        assertEquals(2, result.size());
        assertEquals(child1, result.get(0));   
        
        result = parent.query("*[2]");
        assertEquals(1, result.size());
        assertEquals(child2, result.get(0));
        
    }
    

    public void testEmptyTextNodesDontCount2() {
        
        Element parent = new Element("Test");
        parent.appendChild(new Text(""));
        Element child1 = new Element("child1");
        parent.appendChild(child1);
        Element child2 = new Element("child2");
        parent.appendChild(child2);
        
        Nodes result = parent.query("node()");
        assertEquals(2, result.size());
        assertEquals(child1, result.get(0));   
        assertEquals(child2, result.get(1));   
        
        result = parent.query("node()[1]");
        assertEquals(1, result.size());
        assertEquals(child1, result.get(0));
        
    }
    

    public void testEmptyTextNodeNextToNonEmptyTextNode() {
        
        Element parent = new Element("Test");
        Text empty = new Text("");
        parent.appendChild(empty);
        Text nonempty = new Text("value");
        parent.appendChild(nonempty);
        Element child1 = new Element("child1");
        parent.appendChild(child1);
        Element child2 = new Element("child2");
        parent.appendChild(child2);
        
        Nodes result = parent.query("node()");
        assertEquals(4, result.size());
        assertEquals(empty, result.get(0));   
        assertEquals(nonempty, result.get(1));   
        
        result = parent.query("node()[1]");
        assertEquals(2, result.size());
        assertEquals(empty, result.get(0));
        
    }
    

    public void testBasicPredicate() {
        
        Element parent = new Element("Test");
        Element child1 = new Element("child");
        child1.appendChild("1");
        parent.appendChild(child1);
        Element child2 = new Element("child");
        child2.appendChild("2");
        parent.appendChild(child2);
        Element child3 = new Element("child");
        child3.appendChild("3");
        parent.appendChild(child3);
        
        Nodes result = parent.query("*[.='2']");
        assertEquals(1, result.size());
        assertEquals(child2, result.get(0));   
        
    }
    

    public void testXMLLang() {
        
        Element parent = new Element("Test");
        Element child1 = new Element("child");
        child1.addAttribute(new Attribute("xml:lang", 
          "http://www.w3.org/XML/1998/namespace", "en"));
        parent.appendChild(child1);
        Element child2 = new Element("child");
        child2.appendChild("2");
        child2.addAttribute(new Attribute("xml:lang", 
          "http://www.w3.org/XML/1998/namespace", "fr"));
        parent.appendChild(child2);
        Element child3 = new Element("child");
        child3.appendChild("3");
        parent.appendChild(child3);
        Element child4 = new Element("child");
        child4.appendChild("4");
        child4.addAttribute(new Attribute("xml:lang", 
          "http://www.w3.org/XML/1998/namespace", "en-US"));
        parent.appendChild(child4);
        
        Nodes result = parent.query("child::*[lang('en')]");
        assertEquals(2, result.size());
        assertEquals(child1, result.get(0));   
        assertEquals(child4, result.get(1));   
        
    }
    

    public void testXMLPrefixIsAlwaysBound() {
        
        Element parent = new Element("Test");
        Element child1 = new Element("child");
        child1.addAttribute(new Attribute("xml:lang", 
          "http://www.w3.org/XML/1998/namespace", "en"));
        parent.appendChild(child1);
        Element child2 = new Element("child");
        child2.appendChild("2");
        child2.addAttribute(new Attribute("xml:lang", 
          "http://www.w3.org/XML/1998/namespace", "fr"));
        parent.appendChild(child2);
        Element child3 = new Element("child");
        child3.appendChild("3");
        parent.appendChild(child3);
        Element child4 = new Element("child");
        child4.appendChild("4");
        child4.addAttribute(new Attribute("xml:lang", 
          "http://www.w3.org/XML/1998/namespace", "en-US"));
        parent.appendChild(child4);
        
        Nodes result = parent.query("child::*/@xml:lang");
        assertEquals(3, result.size());   
        
    }
    
    
    public void testCantRebindXMLPrefix() {
        
        XPathContext context = new XPathContext();
        try {
            context.addNamespace("xml", "http://www.example.org");
            fail("Rebound xml prefix");
        }
        catch (NamespaceConflictException success) {
            assertNotNull(success.getMessage());
        }
        
    }
    

    public void testParentAxis() {
        
        Element parent = new Element("Test");
        Element child = new Element("child");
        parent.appendChild(child);
        
        Nodes result = child.query("parent::*");
        assertEquals(1, result.size());
        assertEquals(parent, result.get(0));   
        
    }
    

    public void testAncestorAxis() {
        
        Element grandparent = new Element("Test");
        Document doc = new Document(grandparent);
        Element parent = new Element("Test");
        Element child = new Element("child");
        parent.appendChild(child);
        grandparent.appendChild(parent);
        
        Nodes result = child.query("ancestor::*");
        assertEquals(2, result.size());
        assertEquals(grandparent, result.get(0));   
        assertEquals(parent, result.get(1));
        
    }
    

    public void testParentAxisWithDocument() {
        
        Element root = new Element("Test");
        Document doc = new Document(root);
        
        Nodes result = root.query("parent::*");
        assertEquals(0, result.size());
        
    }
    
    
    public void testParentAxisWithNodeMatchingDocument() {
        
        Element root = new Element("Test");
        Document doc = new Document(root);
        
        Nodes result = root.query("parent::node()");
        assertEquals(1, result.size());
        assertEquals(doc, result.get(0));
        
    }
    
    
    public void testSubstringFunction() {
        
        Element root = new Element("Test");
        Document doc = new Document(root);
        
        Nodes result = root.query("/*[substring('12345', 0, 3)='12']");
        assertEquals(1, result.size());
        assertEquals(root, result.get(0));
        
    }
    
    
    public void testPrecedingAxisWithElementName() {
        
        Element root = new Element("Test");
        Document doc = new Document(root);
        
        Nodes result = doc.query("/descendant::*/preceding::x");
        assertEquals(0, result.size());
        
    }
    
    
    
    
    public void testDocTypeIsNotAnXPathNode() {
     
        Element root = new Element("root");
        Document doc = new Document(root);
        DocType doctype = new DocType("root");
        doc.setDocType(doctype);
        
        Nodes result = doc.query("child::node()[1]");
        assertEquals(1, result.size());
        assertEquals(root, result.get(0));
        
    }
    

    public void testGetNodeBeforeDocType() {
     
        Element root = new Element("root");
        Document doc = new Document(root);
        DocType doctype = new DocType("root");
        doc.setDocType(doctype);
        Comment c = new Comment("test");
        doc.insertChild(c, 0);
        
        Nodes result = doc.query("child::node()[1]");
        assertEquals(1, result.size());
        assertEquals(c, result.get(0));
        
    }
    

    public void testCantUseDocTypeAsXPathContextNode() {
     
        Element root = new Element("root");
        Document doc = new Document(root);
        DocType doctype = new DocType("root");
        doc.setDocType(doctype);
        
        try {
            doctype.query("/");
            fail("Allowed DocType as context node");
        }
        catch (XPathException success) {
            assertNotNull(success.getMessage());
        }
        
    }
    

    public void testDescendantAxis() {
        
        Element grandparent = new Element("Test");
        Document doc = new Document(grandparent);
        Element parent = new Element("Test");
        Element child = new Element("child");
        parent.appendChild(child);
        grandparent.appendChild(parent);
        
        Nodes result = doc.query("descendant::*");
        assertEquals(3, result.size());
        assertEquals(grandparent, result.get(0));   
        assertEquals(parent, result.get(1));
        assertEquals(child, result.get(2));
        
    }
  
    
    public void testGetElementQName() {
        
        Element grandparent = new Element("Test");
        Document doc = new Document(grandparent);
        Element parent = new Element("Test");
        Element child1 = new Element("pre:child", "http://www.example.org/");
        Element child2 = new Element("pre:child", "http://www.example.com/");
        parent.appendChild(child1);
        parent.appendChild(child2);
        grandparent.appendChild(parent);
        
        Nodes result = doc.query("descendant::*[name(.)='pre:child']");
        assertEquals(2, result.size());
        assertEquals(child1, result.get(0));   
        assertEquals(child2, result.get(1));
        
    }
    

    // This does not actually hit the method I was aiming at. 
    // Apparently, Jaxen never actually invokes
    // getElementStringValue()
    public void testGetElementStringValue() {
        
        Element grandparent = new Element("Test");
        Document doc = new Document(grandparent);
        Element parent = new Element("Test");
        Element child1 = new Element("child");
        child1.appendChild("foo");
        Element child2 = new Element("child");
        child2.appendChild("bar");
        parent.appendChild(child1);
        parent.appendChild(child2);
        grandparent.appendChild(parent);
        
        Nodes result = doc.query("descendant::*[.='foo']");
        assertEquals(1, result.size());
        assertEquals(child1, result.get(0)); 
        
    }
    

    public void testGetNonExistentNode() {
        
        Element grandparent = new Element("Test");
        Document doc = new Document(grandparent);
        Element parent = new Element("Test");
        Element child1 = new Element("child");
        child1.appendChild("foo");
        Element child2 = new Element("child");
        child2.appendChild("bar");
        parent.appendChild(child1);
        parent.appendChild(child2);
        grandparent.appendChild(parent);
        
        Nodes result = doc.query("/Test/Test/*[12]");
        assertEquals(0, result.size()); 
        
    }
    

    public void testGetAttributeQName() {
        
        Element grandparent = new Element("Test");
        Document doc = new Document(grandparent);
        Element parent = new Element("Test");
        Attribute a1 = new Attribute("pre:attribute", "http://www.example.org/", "test");
        Attribute a2 = new Attribute("pre:attribute", "http://www.example.com/", "test");
        parent.addAttribute(a2);
        grandparent.addAttribute(a1);
        grandparent.appendChild(parent);
        
        Nodes result = doc.query("descendant::*/attribute::*[name(.)='pre:attribute']");
        assertEquals(2, result.size());
        assertEquals(a1, result.get(0));   
        assertEquals(a2, result.get(1));
        
    }
    
    
    public void testGetNamespaceStringValue() {
        
        Element test = new Element("Test", "http://www.example.com/");
        
        Nodes result = test.query("self::*[contains(namespace::*, 'http://')]");
        assertEquals(1, result.size());
        assertEquals(test, result.get(0));
        
    }
    
    
    private File inputDir = new File("data");

    
    public void testGetDocument() throws MalformedURLException {
        
        Element element = new Element("test");
        File f = new File(inputDir, "prettyxml.xml");
        String url = f.toURL().toExternalForm();
        try {
            element.query("document('" + url + "')/*");
            fail("allowed document() function");
        }
        catch(XPathException success) {
            assertTrue(success.getMessage().indexOf("document() ") >= 0);
        }
        
    }
    

    public void testGetMultipleNodesViaDocumentFunction() 
      throws MalformedURLException {
        
        Element element = new Element("test");
        File f = new File(inputDir, "prettyxml.xml");
        String url = f.toURL().toExternalForm();
        try {
            element.query("document('" + url + "')//*");
            fail("allowed document() function");
        }
        catch(XPathException success) {
            assertTrue(success.getMessage().indexOf("document() ") >= 0);
        }
        
    }
    

    public void testDoubleDocument() throws MalformedURLException {
        
        Element element = new Element("test");
        File f1 = new File(inputDir, "prettyxml.xml");
        String url1 = f1.toURL().toExternalForm();
        File f2 = new File(inputDir, "test.xml");
        String url2 = f2.toURL().toExternalForm();
        try {
            element.query("document('" + url1 + "')/* | " 
              + "document('" + url2 + "')/*");
            fail("allowed document() function");
        }
        catch(XPathException success) {
            assertTrue(success.getMessage().indexOf("document() ") >= 0);
        }
        
    }
    

    public void testGetNonExistentDocument() {
        
        Element element = new Element("test");
        try {
            element.query("document('http://www.ibiblio.org/aksdjhk/')/*");
            fail("That file doesn't exist!");
        }
        catch (XPathException success) {
            assertNotNull(success.getMessage());
        }
        
    }
    

    public void testMalformedDocument() {
        
        Element element = new Element("test");
        try {
            element.query("document('http://www.cafeaulait.org/formatter/Formatter.java')/*");
            fail("Queried malformed document!");
        }
        catch (XPathException success) {
            assertNotNull(success.getMessage());
        }
        
    }
    

    public void testGetDocumentNode() {
        
        Element element = new Element("test");
        Document doc = new Document(element);
        Nodes result = element.query("/");
        assertEquals(1, result.size());
        assertEquals(doc, result.get(0));
        
    }
    

    public void testCommentNodeTest() {
        
        Element grandparent = new Element("Test");
        Document doc = new Document(grandparent);
        Element parent = new Element("Test");
        Element child = new Element("child");
        parent.appendChild(child);
        grandparent.appendChild(parent);
        
        Comment c1 = new Comment("c1");
        Comment c2 = new Comment("c2");
        Comment c3 = new Comment("c3");
        Comment c4 = new Comment("c4");
        
        doc.insertChild(c1, 0);
        grandparent.insertChild(c2, 0);
        parent.insertChild(c3, 0);
        child.insertChild(c4, 0);
        
        Nodes result = doc.query("descendant::comment()");
        assertEquals(4, result.size());
        assertEquals(c1, result.get(0));   
        assertEquals(c2, result.get(1));
        assertEquals(c3, result.get(2));
        assertEquals(c4, result.get(3));
        
    }
    

    public void testCommentStringValue() {
        
        Element grandparent = new Element("Test");
        Document doc = new Document(grandparent);
        Element parent = new Element("Test");
        Element child = new Element("child");
        parent.appendChild(child);
        grandparent.appendChild(parent);
        
        Comment c1 = new Comment("c1");
        Comment c2 = new Comment("c2");
        Comment c3 = new Comment("c3");
        Comment c4 = new Comment("c4");
        
        doc.insertChild(c1, 0);
        grandparent.insertChild(c2, 0);
        parent.insertChild(c3, 0);
        child.insertChild(c4, 0);
        
        Nodes result = doc.query("descendant::comment()[.='c3']");
        assertEquals(1, result.size());
        assertEquals(c3, result.get(0));
        
    }
    
    
    public void testGetProcessingInstructionData() {
        
        Element grandparent = new Element("Test");
        Document doc = new Document(grandparent);
        Element parent = new Element("Test");
        Element child = new Element("child");
        parent.appendChild(child);
        grandparent.appendChild(parent);
        
        ProcessingInstruction p1 = new ProcessingInstruction("c1", "1");
        ProcessingInstruction p2 = new ProcessingInstruction("c1", "2");
        ProcessingInstruction p3 = new ProcessingInstruction("c1", "3");
        ProcessingInstruction p4 = new ProcessingInstruction("c1", "4");
        
        doc.insertChild(p1, 0);
        grandparent.insertChild(p2, 0);
        parent.insertChild(p3, 0);
        child.insertChild(p4, 0);
        
        Nodes result = doc.query("descendant::processing-instruction()[.='3']");
        assertEquals(1, result.size());
        assertEquals(p3, result.get(0));
        
    }
    
    
    public void testProcessingInstructionNodeTest() {
        
        Element grandparent = new Element("Test");
        Document doc = new Document(grandparent);
        Element parent = new Element("Test");
        Element child = new Element("child");
        parent.appendChild(child);
        grandparent.appendChild(parent);
        
        Comment c1 = new Comment("c1");
        Comment c2 = new Comment("c2");
        Comment c3 = new Comment("c3");
        Comment c4 = new Comment("c4");
        
        doc.insertChild(c1, 0);
        grandparent.insertChild(c2, 0);
        parent.insertChild(c3, 0);
        child.insertChild(c4, 0);
        ProcessingInstruction pi = new ProcessingInstruction("appendix", "text");
        doc.appendChild(pi);
        ProcessingInstruction pi2 = new ProcessingInstruction("test", "text");
        parent.appendChild(pi2);
        
        Nodes result = doc.query("descendant::processing-instruction('test')");
        assertEquals(1, result.size());
        assertEquals(pi2, result.get(0));
        
    }
    

    public void testDescendantOrSelfAxis() {
        
        Element grandparent = new Element("Test");
        Element parent = new Element("Test");
        Element child = new Element("child");
        parent.appendChild(child);
        grandparent.appendChild(parent);
        
        Nodes result = grandparent.query("descendant-or-self::*");
        assertEquals(3, result.size());
        assertEquals(grandparent, result.get(0));   
        assertEquals(parent, result.get(1));
        assertEquals(child, result.get(2));
        
    }
    

    public void testAncestorOrSelfAxis() {
        
        Element grandparent = new Element("Test");
        Document doc = new Document(grandparent);
        Element parent = new Element("Test");
        Element child = new Element("child");
        parent.appendChild(child);
        grandparent.appendChild(parent);
        
        Nodes result = child.query("ancestor-or-self::*");
        assertEquals(3, result.size());
        assertEquals(child, result.get(2));   
        assertEquals(parent, result.get(1));   
        assertEquals(grandparent, result.get(0));
        
    }
    

    public void testSelfAxis() {
        
        Element parent = new Element("Test");
        Element child = new Element("child");
        parent.appendChild(child);
        
        Nodes result = child.query("self::*");
        assertEquals(1, result.size());
        assertEquals(child, result.get(0));   
        result = parent.query("self::*");
        assertEquals(1, result.size());
        assertEquals(parent, result.get(0));   
        
    }
    

    public void testSelfAxisWithUnparentedText() {
        
        Text text = new Text("test");
        Nodes result = text.query("self::text()");
        assertEquals(1, result.size());
        assertEquals(text, result.get(0));  
        
    }
    
    public void testSelfAxisWithAttribute() {
        
        Element e = new Element("child");
        e.addAttribute(new Attribute("test", "value"));
        Nodes result = e.query("@*[self::test]");
        assertEquals(0, result.size());
        
    }
    

    public void testSelfAxisWithTextChild() {
        
        Element parent = new Element("parent");
        Node child = new Text("child");
        parent.appendChild(child);
        Nodes result = child.query("self::text()");
        assertEquals(1, result.size());
        assertEquals(child, result.get(0));
        
    }
    

    public void testSelfAxisWithTextChildren() {
        
        Element parent = new Element("parent");
        Node child1 = new Text("1");
        Node child2 = new Text("2");
        Node child3 = new Text("3");
        Node child4 = new Text("4");
        parent.appendChild(child1);
        parent.appendChild(child2);
        parent.appendChild(child3);
        parent.appendChild(child4);
        Nodes result = child1.query("self::text()");
        assertEquals(4, result.size());
        assertEquals(child1, result.get(0));
        assertEquals(child2, result.get(1));
        assertEquals(child3, result.get(2));
        assertEquals(child4, result.get(3));  
        
    }
    

    public void testSelfAxisWithTextChildren2() {
        
        Element parent = new Element("parent");
        Node child1 = new Text("1");
        Node child2 = new Text("2");
        Node child3 = new Text("3");
        Node child4 = new Text("4");
        parent.appendChild(child1);
        parent.appendChild(child2);
        parent.appendChild(child3);
        parent.appendChild(child4);
        Nodes result = child3.query("self::text()");
        assertEquals(4, result.size());
        assertEquals(child1, result.get(0));
        assertEquals(child2, result.get(1));
        assertEquals(child3, result.get(2));
        assertEquals(child4, result.get(3));
        
    }
    

    public void testSelfAxisWithTextChildAndNoParent() {
        
        Node child = new Text("child");
        Nodes result = child.query("self::text()");
        assertEquals(1, result.size());
        assertEquals(child, result.get(0));
        
    }
    

    public void testAttributeAxis() {
        
        Element parent = new Element("Test");
        Element child = new Element("child");
        parent.appendChild(child);
        parent.addAttribute(new Attribute("name", "value"));
        parent.addAttribute(new Attribute("name2", "value"));
        
        Nodes result = child.query("attribute::*");
        assertEquals(0, result.size());
        result = parent.query("attribute::*");
        assertEquals(2, result.size());
        result = parent.query("attribute::name");
        assertEquals(1, result.size()); 
        
    }
    

    public void testAttributeAxisOnNonElement() {
        
        Text text = new Text("Test");
        Nodes result = text.query("attribute::*");
        assertEquals(0, result.size());
        
    }
    

    public void testEmptyParentAxis() {
        
        Element parent = new Element("Test");
        Element child = new Element("child");
        parent.appendChild(child);
        
        Nodes result = parent.query("parent::*");
        assertEquals(0, result.size());   
        
    }
    

    public void testPrecedingSiblingAxis() {
        
        Element parent = new Element("Test");
        Element child1 = new Element("child1");
        Element child2 = new Element("child2");
        Element child3 = new Element("child3");
        parent.appendChild(child1);
        parent.appendChild(child2);
        parent.appendChild(child3);
        
        Nodes result = child1.query("preceding-sibling::*");
        assertEquals(0, result.size());   
        result = child2.query("preceding-sibling::*");
        assertEquals(1, result.size());   
        assertEquals(child1, result.get(0));   
        result = child3.query("preceding-sibling::*");
        assertEquals(2, result.size());    
        
    }
    

    public void testAttributeHasNoSiblings() {
        
        Element parent = new Element("Test");
        Element child1 = new Element("child1");
        Element child2 = new Element("child2");
        Element child3 = new Element("child3");
        parent.appendChild(child1);
        parent.appendChild(child2);
        parent.appendChild(child3);
        Attribute a1 = new Attribute("a1", "value");
        Attribute a2 = new Attribute("a2", "value");
        Attribute a3 = new Attribute("a3", "value");
        child2.addAttribute(a1);
        child2.addAttribute(a2);
        child2.addAttribute(a3);
        
        Nodes result = a2.query("preceding-sibling::node()");
        assertEquals(0, result.size());   
        result = a2.query("following-sibling::node()");
        assertEquals(0, result.size());    
        
    }
    

    public void testIDFunction() {
        
        Element parent = new Element("Test");
        Element child1 = new Element("child1");
        Element child2 = new Element("child2");
        Element child3 = new Element("child3");
        Attribute id = new Attribute("a", "anchor");
        id.setType(Attribute.Type.ID);
        child2.addAttribute(id);
        
        parent.appendChild(child1);
        parent.appendChild(child2);
        parent.appendChild(child3);
        
        Nodes result = parent.query("id('anchor')");
        assertEquals(1, result.size());     
        assertEquals(child2, result.get(0));
        
    }
    

    public void testIDQueryOnDocumentNode() {
        
        Element parent = new Element("Test");
        Element child1 = new Element("child1");
        Element child2 = new Element("child2");
        Element child3 = new Element("child3");
        Attribute id = new Attribute("a", "anchor");
        id.setType(Attribute.Type.ID);
        child2.addAttribute(id);
        
        parent.appendChild(child1);
        parent.appendChild(child2);
        parent.appendChild(child3);
        Document doc = new Document(parent);
        
        Nodes result = doc.query("id('anchor')");
        assertEquals(1, result.size());     
        assertEquals(child2, result.get(0));
        
    }
    

    public void testIDFunctionWithoutType() {
        
        Element parent = new Element("Test");
        Element child1 = new Element("child1");
        Element child2 = new Element("child2");
        Element child3 = new Element("child3");
        Attribute id = new Attribute("id", "anchor");
        child2.addAttribute(id);
        
        parent.appendChild(child1);
        parent.appendChild(child2);
        parent.appendChild(child3);
        
        Nodes result = parent.query("id('anchor')");
        assertEquals(0, result.size());
        
    }
    

    public void testIDFunctionFromTextNode() {
        
        Element parent = new Element("Test");
        Element child1 = new Element("child1");
        Element child2 = new Element("child2");
        Element child3 = new Element("child3");
        Text text = new Text("test");
        child3.appendChild(text);
        Attribute id = new Attribute("a", "anchor");
        id.setType(Attribute.Type.ID);
        child2.addAttribute(id);
        
        parent.appendChild(child1);
        parent.appendChild(child2);
        parent.appendChild(child3);
        
        Nodes result = text.query("id('anchor')");
        assertEquals(1, result.size());     
        assertEquals(child2, result.get(0));
        
    }
    

    public void testIDFunctionFromUnparentedTextNode() {
        
        Text text = new Text("test");
        Nodes result = text.query("id('anchor')");
        assertEquals(0, result.size());
        
    }
    

    public void testIDFunctionFromDisconnectedTextNode() {
        
        Text text = new Text("test");       
        Nodes result = text.query("id('anchor')");
        assertEquals(0, result.size());
        
    }
    

    public void testFollowingSiblingAxis() {
        
        Element parent = new Element("Test");
        Element child1 = new Element("child1");
        Element child2 = new Element("child2");
        Element child3 = new Element("child3");
        parent.appendChild(child1);
        parent.appendChild(child2);
        parent.appendChild(child3);
        
        Nodes result = child3.query("following-sibling::*");
        assertEquals(0, result.size());   
        result = child2.query("following-sibling::*");
        assertEquals(1, result.size());   
        assertEquals(child3, result.get(0));   
        result = child1.query("following-sibling::*");
        assertEquals(2, result.size());    
        
    }
    

    public void testNamespaceQuery() {
        
        Element parent = new Element("Test", "http://www.example.org");
        Element child = new Element("child", "http://www.example.org");
        parent.appendChild(child);
        
        XPathContext context = new XPathContext("pre", "http://www.example.org");
        Nodes result = parent.query("child::pre:child", context);
        assertEquals(1, result.size());
        assertEquals(child, result.get(0));   
        
    }
    
    
    public void testNamespaceAxis() {
        
        Element parent = new Element("Test", "http://www.example.org");
        
        Nodes result = parent.query("namespace::*");
        assertEquals(2, result.size());
        Namespace n1 = (Namespace) result.get(0);
        Namespace n2 = (Namespace) result.get(1);
        assertTrue(n1.getPrefix().equals("") || n2.getPrefix().equals(""));
        assertTrue(n1.getPrefix().equals("xml") || n2.getPrefix().equals("xml"));
        assertTrue(n1.getValue().equals("http://www.example.org") 
          || n2.getValue().equals("http://www.example.org"));
        assertTrue(n1.getValue().equals(Namespace.XML_NAMESPACE) 
          || n2.getValue().equals(Namespace.XML_NAMESPACE));
        
    }
    
    
    public void testNamespaceAxisFromNonElement() {
        
        Text text = new Text("test");
        
        Nodes result = text.query("namespace::*");
        assertEquals(0, result.size()); 
        
    }
    
    
    public void testPredicateWithNamespaceAxis() {
        
        Element parent = new Element("Test");
        Element child = new Element("child", "http://www.example.com");
        Element grandchild = new Element("child", "http://www.example.com");
        grandchild.addNamespaceDeclaration("pre", "http://www.w3.org/");
        parent.appendChild(child);
        child.appendChild(grandchild);
        
        // Every node has at least a mapping for xml prefix.
        Nodes result = parent.query("self::*[count(namespace::*)=0]");
        assertEquals(0, result.size());   
        
        result = parent.query("self::*[count(namespace::*)=1]");
        assertEquals(1, result.size());   
        assertEquals(parent, result.get(0));
        
        result = child.query("self::*[count(namespace::*)=2]");
        assertEquals(1, result.size());   
        assertEquals(child, result.get(0));
        
        result = grandchild.query("self::*[count(namespace::*)=3]");
        assertEquals(1, result.size());   
        assertEquals(grandchild, result.get(0));
        
    }
    
    
    public void testPredicateWithNamespaceAxis2() {
        
        Element parent = new Element("Test");
        Element child = new Element("child", "http://www.example.com");
        Element grandchild = new Element("child", "http://www.example.com");
        grandchild.addNamespaceDeclaration("pre", "http://www.w3.org/");
        parent.appendChild(child);
        child.appendChild(grandchild);
        
        // Every node has at least a mapping for xml prefix.
        Nodes result = parent.query("*[count(namespace::*)=0]");
        assertEquals(0, result.size());   
        
        result = parent.query(".//self::*[count(namespace::*)=1]");
        assertEquals(1, result.size());   
        assertEquals(parent, result.get(0));
        
        result = parent.query(".//*[count(namespace::*)=2]");
        assertEquals(1, result.size());   
        assertEquals(child, result.get(0));
        
        result = parent.query(".//*[count(namespace::*)=3]");
        assertEquals(1, result.size());   
        assertEquals(grandchild, result.get(0));
        
    }
    
    
    public void testNamespaceQueryWithNullPrefix() {
        
        Element parent = new Element("Test", "http://www.example.org");
        Element child = new Element("child", "http://www.example.org");
        parent.appendChild(child);
        
        XPathContext context = new XPathContext("pre", "http://www.example.org");
        context.addNamespace(null, "http://www.w3.org");
        Nodes result = parent.query("child::pre:child", context);
        assertEquals(1, result.size());
        assertEquals(child, result.get(0));   
        
    }
    
    
    public void testNamespaceQueryWithNullPrefix2() {
        
        Element parent = new Element("Test");
        Element child = new Element("child");
        parent.appendChild(child);
        
        XPathContext context = new XPathContext(null, "http://www.example.org");
        Nodes result = parent.query("child::child", context);
        assertEquals(1, result.size());
        assertEquals(child, result.get(0));   
        
    }
    
    
    public void testNamespaceQueryWithEmptyPrefix() {
        
        Element parent = new Element("Test", "http://www.example.org");
        Element child = new Element("child", "http://www.example.org");
        parent.appendChild(child);
        
        XPathContext context = new XPathContext("pre", "http://www.example.org");
        context.addNamespace("", "http://www.w3.org");
        Nodes result = parent.query("child::pre:child", context);
        assertEquals(1, result.size());
        assertEquals(child, result.get(0));   
        
    }
    
    
    public void testNamespaceQueryWithEmptyPrefix2() {
        
        Element parent = new Element("Test");
        Element child = new Element("child");
        parent.appendChild(child);
        
        XPathContext context = new XPathContext("", "http://www.example.org");
        Nodes result = parent.query("child::child", context);
        assertEquals(1, result.size());
        assertEquals(child, result.get(0));   
        
    }
    
    
    public void testNamespaceQueryWithNullURI() {
        
        Element parent = new Element("Test", "http://www.example.org");
        Element child = new Element("child", "http://www.example.org");
        parent.appendChild(child);
        
        XPathContext context = new XPathContext("pre", null);
        try {
            parent.query("child::pre:child", context);
            fail("Allowed null URI");
        }
        catch (XPathException success) {
            assertNotNull(success.getCause());
            assertNotNull(success.getMessage());
        }
        
    }
    
    
    public void testNamespaceQueryWithEmptyURI() {
        
        Element parent = new Element("Test", "http://www.example.org");
        Element child = new Element("child", "http://www.example.org");
        parent.appendChild(child);
        
        XPathContext context = new XPathContext("pre", "");
        try {
            parent.query("child::pre:child", context);
            fail("Allowed empty string as namespace URI");
        }
        catch (XPathException success) {
            assertNotNull(success.getCause());
            assertNotNull(success.getMessage());
        }
        
    }
    
    
    public void testNamespaceQueryWithReboundPrefix() {
        
        Element parent = new Element("Test", "http://www.example.org");
        Element child = new Element("child", "http://www.example.org");
        parent.appendChild(child);
        
        XPathContext context = new XPathContext("pre", "http://www.example.com");
        Nodes result = parent.query("child::pre:child", context);
        assertEquals(0, result.size());
        
        context.addNamespace("pre", "http://www.example.org");
        result = parent.query("child::pre:child", context);
        assertEquals(1, result.size());
        assertEquals(child, result.get(0));   
        
    }
    
    
    public void testNamespaceQueryWithUnboundPrefix() {
        
        Element parent = new Element("Test", "http://www.example.org");
        Element child = new Element("child", "http://www.example.org");
        parent.appendChild(child);
        
        XPathContext context = new XPathContext("not", "http://www.example.com");
        try {
            parent.query("child::pre:child", context);
            fail("Queried with unbound prefix");
        }
        catch (XPathException success) {
            assertNotNull(success.getMessage());
            assertNotNull(success.getCause());
        }
        
        try {
            parent.query("child::pre:child");
            fail("Queried with unbound prefix");
        }
        catch (XPathException success) {
            assertNotNull(success.getMessage());
            assertNotNull(success.getCause());
        }
        
    }
    
    
    public void testElementBasedNamespaceContext() {
        
        Element parent = new Element("Test", "http://www.example.org");
        Element child = new Element("child", "http://www.example.org");
        parent.appendChild(child);
        
        Element test = new Element("pre:test", "http://www.example.org");
        XPathContext context = XPathContext.makeNamespaceContext(test);
        Nodes result = parent.query("child::pre:child", context);
        assertEquals(1, result.size());
        assertEquals(child, result.get(0));   
        
    }
    
    
    public void testAttributeBasedNamespaceContext() {
        
        Element parent = new Element("Test", "http://www.example.org");
        Element child = new Element("child", "http://www.example.org");
        parent.appendChild(child);
        
        Element test = new Element("test");
        test.addAttribute(new Attribute("pre:test", "http://www.example.org", "value"));
        XPathContext context = XPathContext.makeNamespaceContext(test);
        Nodes result = parent.query("child::pre:child", context);
        assertEquals(1, result.size());
        assertEquals(child, result.get(0));   
        
    }
    
    
    public void testAdditionalNamespaceBasedNamespaceContext() {
        
        Element parent = new Element("Test", "http://www.example.org");
        Element child = new Element("child", "http://www.example.org");
        parent.appendChild(child);
        
        Element test = new Element("test");
        test.addNamespaceDeclaration("pre", "http://www.example.org");
        XPathContext context = XPathContext.makeNamespaceContext(test);
        Nodes result = parent.query("child::pre:child", context);
        assertEquals(1, result.size());
        assertEquals(child, result.get(0));   
        
    }
    
    
    public void testAncestorElementBasedNamespaceContext() {
        
        Element parent = new Element("Test", "http://www.example.org");
        Element child = new Element("child", "http://www.example.org");
        parent.appendChild(child);
        
        Element test = new Element("pre:test", "http://www.example.org");
        Element testChild = new Element("testchild");
        test.appendChild(testChild);
        XPathContext context = XPathContext.makeNamespaceContext(testChild);
        Nodes result = parent.query("child::pre:child", context);
        assertEquals(1, result.size());
        assertEquals(child, result.get(0));   
        
    }
    
    
    public void testAncestorAttributeBasedNamespaceContext() {
        
        Element parent = new Element("Test", "http://www.example.org");
        Element child = new Element("child", "http://www.example.org");
        parent.appendChild(child);
        
        Element test = new Element("test");
        test.addAttribute(new Attribute("pre:test", "http://www.example.org", "value"));
        Element testChild = new Element("testchild");
        test.appendChild(testChild);
        XPathContext context = XPathContext.makeNamespaceContext(testChild);
        Nodes result = parent.query("child::pre:child", context);
        assertEquals(1, result.size());
        assertEquals(child, result.get(0));   
        
    }
    
    
    public void testAncestorAdditionalNamespaceBasedNamespaceContext() {
        
        Element parent = new Element("Test", "http://www.example.org");
        Element child = new Element("child", "http://www.example.org");
        parent.appendChild(child);
        
        Element test = new Element("test");
        test.addNamespaceDeclaration("pre", "http://www.example.org");
        Element testChild = new Element("testchild");
        test.appendChild(testChild);
        XPathContext context = XPathContext.makeNamespaceContext(testChild);
        Nodes result = parent.query("child::pre:child", context);
        assertEquals(1, result.size());
        assertEquals(child, result.get(0));   
        
    }
    
    
    public void testPrefixedNamespaceQuery() {
        
        Element parent = new Element("a:Test", "http://www.example.org");
        Element child = new Element("b:child", "http://www.example.org");
        Attribute att = new Attribute("c:dog", "http://www.cafeconleche.org/", "test");
        parent.appendChild(child);
        child.addAttribute(att);
        
        XPathContext context = new XPathContext("pre", "http://www.example.org");
        context.addNamespace("c", "http://www.cafeconleche.org/");
        Nodes result = parent.query("child::pre:child", context);
        assertEquals(1, result.size());
        assertEquals(child, result.get(0)); 
        
        result = child.query("@c:*", context);
        assertEquals(1, result.size());
        assertEquals(att, result.get(0)); 
        
    }
    
    
    public void testBradley() {
     
        Element element = new Element("root");
        Text t1 = new Text("makes ");
        Text t2 = new Text("a");
        Text t3 = new Text(" good");
        Text t4 = new Text(" point.");
        Element child = new Element("someElement");
        Text t5 = new Text("  Yes");
        Text t6 = new Text(" he");
        Text t7 = new Text(" does!");
        element.appendChild(t1);
        element.appendChild(t2);
        element.appendChild(t3);
        element.appendChild(t4);
        element.appendChild(child);
        element.appendChild(t5);
        element.appendChild(t6);
        element.appendChild(t7);
        
        Nodes result = element.query("./text()[contains(., 'o')]");
        assertEquals(7, result.size());
        assertEquals(t1, result.get(0));
        assertEquals(t2, result.get(1));
        assertEquals(t3, result.get(2));
        assertEquals(t4, result.get(3));
        assertEquals(t5, result.get(4));
        assertEquals(t6, result.get(5));
        assertEquals(t7, result.get(6));
        
        
    }
    

    public void testNamespaceQueryWithAdjacentTextNodes() {
        
        Element parent = new Element("Test", "http://www.example.org");
        Element child = new Element("child", "http://www.example.org");
        parent.appendChild(child);
        child.appendChild("1");
        child.appendChild("2");
        
        XPathContext context = new XPathContext("pre", "http://www.example.org");
        Nodes result = parent.query("descendant::text()", context);
        assertEquals(2, result.size());
        assertEquals("1", result.get(0).getValue());   
        assertEquals("2", result.get(1).getValue());   
        
    }
    

    public void testNamespaceQueryWithoutPrefixMapping() {
        
        Element parent = new Element("Test", "http://www.example.org");
        Element child = new Element("child", "http://www.example.org");
        parent.appendChild(child);
        
        Nodes result = parent.query("child");
        assertEquals(0, result.size());   
        
    }
    

    public void testAdjacentTextObjects() {
        
        Element parent = new Element("Test");
        parent.appendChild("test");
        parent.appendChild("again");
        
        Nodes result = parent.query("text()");
        assertEquals(2, result.size());
        assertEquals("test", result.get(0).getValue());   
        assertEquals("again", result.get(1).getValue());   
        
    }
    

    public void testQueryCrossesAdjacentTextObjects() {
        
        Element parent = new Element("Test");
        parent.appendChild("test");
        parent.appendChild("again");
        
        Nodes result = parent.query("node()[contains(., 'tag')]");
        assertEquals(2, result.size());
        assertEquals("test", result.get(0).getValue());   
        assertEquals("again", result.get(1).getValue());   
        
    }
    

    // According to section 5.7 of the XPath 1.0 spec,
    // "As much character data as possible is grouped into each text 
    // node: a text node never has an immediately following or 
    // preceding sibling that is a text node."
    public void testAdjacentTextNodes2() {
        
        Element parent = new Element("Test");
        parent.appendChild("test");
        parent.appendChild("again");
        
        Nodes result = parent.query("child::text()[1]");
        assertEquals(2, result.size());
        assertEquals("test", result.get(0).getValue());   
        assertEquals("again", result.get(1).getValue());   
        
    }
    

    // According to section 5.7 of the XPath 1.0 spec,
    // "A text node always has at least one character of data."
    public void testEmptyTextNodes() {
        
        Element parent = new Element("Test");
        parent.appendChild("");
        
        Nodes result = parent.query("child::text()");
        assertEquals(0, result.size());  
        
    }
    

    public void testEmptyTextFollowsNonEmptyText() {
        
        Element parent = new Element("parent");
        Text empty = new Text("");
        Text nonempty = new Text("value");
        parent.appendChild(nonempty);
        parent.appendChild(empty);
        Nodes result = parent.query("node()");
        assertEquals(2, result.size());
        
    }
    
    
    public void testCountAdjacentEmptyAndNonEmptyTextNodes() {
        
        Element parent = new Element("parent");
        Text empty = new Text("");
        Text nonempty = new Text("value");
        parent.appendChild(nonempty);
        parent.appendChild(empty);

        Nodes result2 = parent.query("/*[count(node())=1]");
        assertEquals(1, result2.size());
        assertEquals(parent, result2.get(0));
        
        Nodes nodes1 = parent.query("node()[1]");
        assertEquals(2, nodes1.size());
        Nodes nodes2 = parent.query("node()[2]");
        assertEquals(0, nodes2.size());
        
    }
    
    
    public void testAdjacentEmptyAndNonEmptyTextNodes() {
        
        Element parent = new Element("parent");
        Text empty = new Text("");
        Text nonempty = new Text("value");
        Text nonempty2 = new Text("value2");
        parent.appendChild(empty);
        parent.appendChild(nonempty);
        parent.appendChild(nonempty2);
        
        Nodes result = parent.query("node()");
        assertEquals(3, result.size());

        Nodes result2 = parent.query("/*[count(node())=1]");
        assertEquals(1, result2.size());
        assertEquals(parent, result2.get(0));
        
        Nodes nodes1 = parent.query("node()[1]");
        assertEquals(3, nodes1.size());
        
        Nodes nodes2 = parent.query("node()[2]");
        assertEquals(0, nodes2.size());
        
    }


    public void testBadXPathExpression() {
        
        Element parent = new Element("Test");
        
        try {
            parent.query("This is not an XPath expression");
            fail("Allowed malformed query");
        }
        catch (XPathException success) {
            assertNotNull(success.getMessage());
        }  
        
    }
    
    
    public void testNaNEvaluatesToFalse() {
        
        Element root = new Element("root");
        Document doc = new Document(root);
        
        Nodes result = doc.query("/*[boolean(0 div 0)]");
        assertEquals(0, result.size());
        
    }
    
    
    /* <body>
<p>
  <span></span>
</p>
<div></div>
</body> */
     public void testPrecedingAxis() {
      
         Element body = new Element("body");
         Element p = new Element("p");
         body.appendChild(p);
         Element span = new Element("span");
         p.appendChild(span);
         Element div = new Element("div");
         body.appendChild(div);
         
         Nodes result = div.query("preceding::*[1]");
         assertEquals(1, result.size());
         assertEquals(span, result.get(0));
         
     }
    
    
     public void testRootNodeValueIsNonEmpty() {
      
         Element root = new Element("html");
         Document doc = new Document(root);
         root.appendChild("test");
         
         Nodes result = doc.query("/*[string(/) != '']");
         assertEquals(1, result.size());
         assertEquals(root, result.get(0));
         
     }
    
    
     public void testContextPositionForParaentlessNodeIs1() {
      
         Element root = new Element("html");
         
         Nodes result = root.query("self::*[1]");
         assertEquals(1, result.size());
         assertEquals(root, result.get(0));
         
     }
    
    
     public void testContextSizeForParaentlessNodeIs1() {
      
         Element root = new Element("html");
         
         Nodes result = root.query("self::*[last()=1]");
         assertEquals(1, result.size());
         assertEquals(root, result.get(0));
         
     }
    
    
     public void testLastFunction() {
      
         Element root = new Element("html");
         Element child1 = new Element("child1");
         Element child2 = new Element("child2");
         root.appendChild(child1);
         root.appendChild(child2);
         Document doc = new Document(root);
         
         Nodes result = child2.query("self::*[position()=last()]");
         assertEquals(1, result.size());
         assertEquals(child2, result.get(0));
         
     }
     
     
     public void testJaxen51() throws ParsingException, IOException {

         String data = "<root>\n <servlet-mapping>\n" 
             + "  <servlet-name>DeviceInfoServlet</servlet-name>\n"
             + "  <url-pattern>/tools/*</url-pattern>\n"
             + "</servlet-mapping>\n</root>";
         
         Builder builder = new Builder();
         Document doc = builder.build(data, null);
         
         Nodes result = doc.query("//*[./../servlet-name = 'DeviceInfoServlet']");
         assertEquals(2, result.size());
       
     }         


    public void testXPathNamespaceParentage() 
      throws ParsingException, IOException {
        
        String input = "<!DOCTYPE doc [\n"
            + "<!ATTLIST e2 xml:space (default|preserve) 'preserve'>\n"
            + "<!ATTLIST e3 id ID #IMPLIED>\n"
            + "]>\n"
            + "<doc xmlns=\"http://www.ietf.org\" xmlns:w3c=\"http://www.w3.org\">\n"
            + "   <e1>\n"
            + "      <e2 xmlns=\"\">\n"
            + "         <e3 id=\"E3\"/>\n"
            + "      </e2>\n"
            + "   </e1>\n"
            + "</doc>";
        
        Document doc = (new Builder()).build(input, null);
        XPathContext context = new XPathContext("ietf", "http://www.ietf.org");
        String xpath = "(/*/* | /*/*/namespace::*)\n";
        Nodes result = doc.query(xpath);
        assertEquals(4, result.size());
        Element parent = (Element) result.get(0);
        for (int i = 1; i < 4; i++) {
            Namespace namespace = (Namespace) result.get(i); 
            assertEquals(parent, namespace.getParent());
        }
        
    }
    

    public void testAttributesFollowElementsInDocumentOrder() 
      throws ParsingException, IOException {
        
        String input = "<document>\n"
            + "   <e1 a='b' c='d' e='f'>\n"
            + "      <e2>\n"
            + "         <e3 id=\"E3\"/>\n"
            + "      </e2>\n"
            + "   </e1>\n"
            + "</document>";
        
        Document doc = (new Builder()).build(input, null);
        String xpath = "(/*/* | /*/*/attribute::*)\n";
        Nodes result = doc.query(xpath);
        assertEquals(4, result.size());
        Element parent = (Element) result.get(0);
        for (int i = 1; i < 4; i++) {
            Attribute attribute = (Attribute) result.get(i); 
            assertEquals(parent, attribute.getParent());
        }
        
    }
    
    
    public void testDoubleSlashIsIncorrect() {
        
        Element root = new Element("root", "http://www.example.org");
        Document doc = new Document(root);
        root.appendChild(new Element("child"));
        root.appendChild("test");
        root.addAttribute(new Attribute("test", "test"));
        
        try {
            Nodes nodes = doc.query("//");
            fail("Queried //");
        }
        catch (XPathException success) {
            assertNotNull(success.getMessage());
        }
        
    }
    

    public void testDoubleSlashIsIncorrect2() {
        
        Element root = new Element("root", "http://www.example.org");
        Document doc = new Document(root);
        root.appendChild(new Element("child"));
        root.appendChild("test");
        root.addAttribute(new Attribute("test", "test"));
        
        try {
            Nodes nodes = doc.query("// ");
            fail("Queried // ");
        }
        catch (XPathException success) {
            assertNotNull(success.getMessage());
        }
        
    }
    

    public void testWhiteSpaceIsAllowedBetweenTokens() {
        
        Element root = new Element("root", "http://www.example.org");
        Document doc = new Document(root);
        root.appendChild(new Element("child"));
        root.appendChild("test");
        root.addAttribute(new Attribute("test", "test"));
        
        Nodes nodes = doc.query("// a");
        assertEquals(0, nodes.size());
        
    }
    
    
/*    public void testMassimo() throws ParsingException, IOException {
        
        Builder builder = new Builder();
        Document doc = builder.build("http://staff.science.uva.nl/~francesc/xpathmark/benchmark_canon.xml");
        Element root = doc.getRootElement();
        Element input_1 = root.getFirstChildElement("document_1");
        Element input_2 = root.getFirstChildElement("document_2");
        
        Nodes queries = root.query("child::query[starts-with('@id', 'Q')]");
        for (int i = 0; i < queries.size(); i++) {
            Element query = (Element) queries.get(i);
            String xpath = query.getFirstChildElement("syntax").getValue();
            Nodes actual = input_1.query(xpath);
            Elements expected = query.getChildElements();
        }
        
    } */
    

}
