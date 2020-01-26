/* Copyright 2002-2004 Elliotte Rusty Harold
   
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
   elharo@ibiblio.org. Please include the word "XOM" in the
   subject line. The XOM home page is located at https://xom.nu/
*/

package nu.xom.tests;

import java.io.File;
import java.io.IOException;

import nu.xom.ParsingException;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Comment;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.NodeFactory;
import nu.xom.Nodes;
import nu.xom.ProcessingInstruction;
import nu.xom.Text;
import nu.xom.ValidityException;
import nu.xom.XMLException;

/**
 * <p>
 * Tests that subclasses of <code>NodeFactory</code> can filter 
 * on building in various ways.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.1b2
 *
 */
public class NodeFactoryTest extends XOMTestCase {

    
    private File data = new File("data");
    
    
    public NodeFactoryTest(String name) {
        super(name);
    }
    
    
    protected void setUp() {
        numNodesInExternalDTDSubset = 0;
    }
    
    
    public void testSkippingComment() 
      throws IOException, ParsingException {
        
        String data = "<a>1<!--tetetwkfjkl-->8</a>";
        Builder builder = new Builder(new CommentFilter());
        Document doc = builder.build(data, "http://www.example.org/");
        Element root = doc.getRootElement();
        assertEquals("Skipped comment interrupted text node", 
          2, root.getChildCount());
        assertEquals("18", doc.getValue());
        Node first = root.getChild(0);
        assertEquals(first.getValue(), "1");        
        Node second = root.getChild(1);
        assertEquals(second.getValue(), "8");   
        
    }

    
    static class CommentFilter extends NodeFactory {
        
        public Nodes makeComment(String data) {
            return new Nodes();
        }
        
    }

    
    public void testCantAddOneElementMultipleTimes() 
        throws IOException, ParsingException {
        
        String data = "<a><b>18</b></a>";
        Builder builder = new Builder(new SingleElementFactory());
        try {
            builder.build(data, "http://www.example.org/");
            fail("Allowed one element in several places");
        }
        catch (ParsingException success) {
            assertNotNull(success.getMessage());
        }
                
    }

    
    public void testCantAddOneAttributeMultipleTimes() 
        throws IOException, ParsingException {
        
        String data = "<a test=\"value\" name=\"data\"></a>";
        Builder builder = new Builder(new SingleAttributeFactory());
        try {
            builder.build(data, "http://www.example.org/");
            fail("Allowed one attribute twice");
        }
        catch (ParsingException success) {
            assertNotNull(success.getMessage());
        }
                
    }

    
    private static class SingleElementFactory extends NodeFactory {

        private Element test = new Element("test");

        public Element startMakingElement(String name, String namespace) {
            return test;
        }

    }
    
    
    private static class SingleAttributeFactory extends NodeFactory {

        private Attribute test = new Attribute("limit", "none");

        public Nodes makeAttribute(String name, String URI, 
          String value, Attribute.Type type) {
            return new Nodes(test);
        }

    }
    
    
    public void testChangingElementName() 
      throws IOException, ParsingException {
        
        String data 
          = "<a>1<b>2<a>3<b>4<a>innermost</a>5</b>6</a>7</b>8</a>";
        Builder builder = new Builder(new CFactory());
        Document doc = builder.build(data, "http://www.example.org/");
        Element root = doc.getRootElement();
        assertEquals("1234innermost5678", doc.getValue());
        assertEquals("c", root.getQualifiedName());
        assertEquals(3, root.getChildCount());
        Element b = (Element) root.getChild(1);
        assertEquals("c", b.getQualifiedName());
        
    }    

    
    static class CFactory extends NodeFactory {

        public Element startMakingElement(
          String name, String namespace) {
            return new Element("c");
        }

    }

    
    public void testMakeRoot() throws IOException, ParsingException {   
        
        String data = "<a><b>18</b></a>";
        Builder builder = new Builder(new CallsMakeRoot());
        Document doc = builder.build(data, "http://www.example.org/");
        Element root = doc.getRootElement();
        assertEquals("rootSubstitute", root.getQualifiedName());
        
        // make sure the other elements aren't changed
        assertNotNull(root.getFirstChildElement("b"));
        
    }

    
    static class CallsMakeRoot extends NodeFactory {

        public Element makeRootElement(
          String name, String namepaceURI) {
            return new Element("rootSubstitute");
        }

    }

    
    public void testSkippingProcessingInstruction()
      throws IOException, ParsingException {
        
        String data = "<a>1<?test some data?>8</a>";
        Builder builder = new Builder(new ProcessingInstructionFilter());
        Document doc = builder.build(data, "http://www.example.org/");
        Element root = doc.getRootElement();
        assertEquals(
          "Skipped processing instruction interrupted text node", 
          2, root.getChildCount()
        );
        assertEquals("18", doc.getValue());
        Node first = root.getChild(0);
        assertEquals(first.getValue(), "1");        
        Node second = root.getChild(1);
        assertEquals(second.getValue(), "8");   

    }

    
    static class ProcessingInstructionFilter extends NodeFactory {

        public Nodes makeProcessingInstruction(
          String target, String data) {
            return new Nodes();
        }

    }


    public void testSkipping2() throws IOException, ParsingException {
        
        String data 
          = "<a>1<b>2<a>3<b>4<a>innermost</a>5</b>6</a>7</b>8</a>";
        Builder builder = new Builder(new BFilter());
        Document doc = builder.build(data, "http://www.example.org/");
        Element root = doc.getRootElement();
        assertEquals("1234innermost5678", doc.getValue());
        assertEquals(5, root.getChildCount());
        Node first = root.getChild(0);
        assertEquals("1", first.getValue());
        Node middle = root.getChild(2);
        assertEquals("34innermost56", middle.getValue());
        Node last = root.getChild(4);
        assertEquals("8", last.getValue());
        
        Node innermost = middle.getChild(2);
        assertEquals("innermost", innermost.getValue());
        Node inner1 = middle.getChild(0);
        assertEquals("3", inner1.getValue());
        Node inner3 = middle.getChild(4);
        assertEquals("6", inner3.getValue());
        
    }

    
    static class BFilter extends NodeFactory {

        public Element startMakingElement(
          String name, String namespaceURI) {
            if (name.equals("b")) return null;   
            return super.startMakingElement(name, namespaceURI);
        }

    }

    
    public void testMinimalizedDocument() 
      throws IOException, ParsingException {
        
        File input = new File(data, "entitytest.xml");
        Builder builder = new Builder(new MinimizingFactory());
        Document doc = builder.build(input);
        assertEquals(1, doc.getChildCount());
        Element root = doc.getRootElement();
        assertEquals("root", root.getQualifiedName());
        assertEquals("", root.getNamespaceURI());
        assertEquals(0, root.getChildCount());
        assertEquals(0, root.getAttributeCount());
        
    }
    
    
    public void testValidateWithFactory() 
      throws ParsingException, IOException {
        Builder validator = new Builder(true, new MinimizingFactory());
        Document doc = validator.build("<!-- a comment --><!DOCTYPE root [" +
                "<!ELEMENT root EMPTY>" +
                "]>" +
                "<root/><?a processing instruction?>", 
                "http://www.example.org/");
        assertEquals(1, doc.getChildCount());
    }

    
    // Throws away everything except the document and the
    // root element
    static class MinimizingFactory extends NodeFactory {

        private Nodes empty = new Nodes();

        public Nodes makeComment(String data) {
            return empty;  
        }    
    
        public Nodes makeText(String data) {
            return empty;  
        }    
    
        public Nodes finishMakingElement(Element element) {
            if (element.getParent() instanceof Document) {
                return new Nodes(element);
            }        
            return empty;
        }
    
        public Nodes makeAttribute(String name, String URI, 
          String value, Attribute.Type type) {
            return empty;
        }
    
        public Nodes makeDocType(String rootElementName, 
          String publicID, String systemID) {
            return empty;    
        }
    
        public Nodes makeProcessingInstruction(
          String target, String data) {
            return empty; 
        }          
        
    }
    
    
    public void testCDATASectionsCanBeOverridden() 
      throws ValidityException, ParsingException, IOException {
        
        String data ="<root><![CDATA[text]]></root>";
        Builder builder = new Builder(new MinimizingFactory());
        Document doc = builder.build(data, "http://www.example.com");
        assertEquals("", doc.getValue());
        
    }

    
    public void testNullRootNotAllowed() 
      throws IOException, ParsingException {
        
        File input = new File(data, "entitytest.xml");
        Builder builder = new Builder(new NullElementFactory());
        try {
            builder.build(input);
            fail("Allowed null root");
        }
        catch (ParsingException success) {
           assertNotNull(success.getMessage());  
        }
        
    }

    
    // Returns null for all elements including the root.
    // This should cause an exception.
    static class NullElementFactory extends NodeFactory {

        public Element startMakingElement(
          String name, String namespaceURI) {
            return null;   
        }

    }
       
    
    public void testNullDocumentNotAllowed() 
      throws IOException, ParsingException {
        
        File input = new File(data, "entitytest.xml");
        Builder builder = new Builder(new NullDocumentFactory());
        try {
            builder.build(input);
            fail("Allowed null document");
        }
        catch (ParsingException success) {
           assertTrue(success.getCause() instanceof NullPointerException); 
        }  
        
    }

    
    // Returns null for all elements including the root.
    // This should cause an exception.
    static class NullDocumentFactory extends NodeFactory {

        public Document startMakingDocument() {
            return null;   
        }

    }

    
    public void testSkipping() throws IOException, ParsingException {
        
        String data = "<a>data<b>data<a>data</a>data</b>data</a>";
        Builder builder = new Builder(new BFilter());
        Document doc = builder.build(data, "http://www.example.org/");
        Element root = doc.getRootElement();
        assertEquals(5, root.getChildCount());
        assertEquals("datadatadatadatadata", root.getValue());
        Element middle = (Element) root.getChild(2);
        assertEquals("data", middle.getValue());
        Node start = root.getChild(0);
        Node end = root.getChild(4);
        assertEquals("data", start.getValue());
        assertEquals("data", end.getValue());      
        
    }

    
    int numNodesInExternalDTDSubset = 0; 
    
    public void testDontReportCommentsAndProcessingInstructionsInExternalDTDSubset() 
      throws IOException, ParsingException {
        
        File input = new File(data, "contentindtd.xml");
        Builder builder = new Builder(new Counter());
        builder.build(input); 
        assertEquals(0, numNodesInExternalDTDSubset);
        
    }
    
    
    private class Counter extends NodeFactory {
        
        public Nodes makeComment(String data) {
            numNodesInExternalDTDSubset++;
            return super.makeComment(data);
        }
        
        public Nodes makeProcessingInstruction(String target, String data) {
            numNodesInExternalDTDSubset++;
            return super.makeProcessingInstruction(target, data);
        }
        
    }
    
    
    public void testDontCoalesceTextNodes() 
      throws IOException, ParsingException {
        
        String data = "<a>data<!-- comment--> data</a>";
        Builder builder = new Builder(new CommentFilter());
        Document doc = builder.build(data, "http://www.example.org/");
        Element root = doc.getRootElement();
        assertEquals(2, root.getChildCount());
        assertEquals("data data", root.getValue());
        Text text = (Text) root.getChild(0);
        assertEquals("data", text.getValue());  
        
    }

    
    static class TripleElementFilter extends NodeFactory {
     
        public Nodes finishMakingElement(Element element) {
            Nodes result = new Nodes(element);  
            if (!(element.getParent() instanceof Document)) { 
                result.append(element.copy());
                result.append(element.copy());
            }
            return result;
        }   
        
    }
    
    
    public void testTriple() 
      throws IOException, ParsingException {  
        
        String data = "<a><b><c/></b></a>";
        Builder builder = new Builder(new TripleElementFilter());
        Document doc = builder.build(data, "http://www.example.org/");
        Element root = doc.getRootElement();
        assertEquals(3, root.getChildCount());
        assertEquals("", root.getValue());
        Element b = (Element) root.getChild(0);
        assertEquals("b", b.getLocalName());
        assertEquals(
          "<a><b><c /><c /><c /></b><b><c /><c /><c /></b><b><c /><c /><c /></b></a>", 
          root.toXML());   
        
    }

    
    static class UncommentFilter extends NodeFactory {
     
        public Nodes makeComment(String data) {
            Nodes result = new Nodes(new Text(data));   
            return result;
        }   
        
    }
    
    
    public void testUncomment() 
      throws ParsingException, IOException {
        
        String data = "<!-- test --><a></a>";
        Builder builder = new Builder(new UncommentFilter());
        try {
            builder.build(data, "http://www.example.org/");
            fail("built Text into prolog");
        }
        catch (ParsingException success) {
            assertNotNull(success.getMessage());   
        }            
        
    }
    
    
    static class DocumentFilter extends NodeFactory {
     
        public Nodes makeComment(String data) {
            Element root = new Element("root");
            Nodes result = new Nodes(new Document(root));   
            return result;
        }   
        
    }

    
    public void testCantAddDocument() 
      throws ParsingException, IOException {
        
        String data = "<a><!-- test --></a>";
        Builder builder = new Builder(new DocumentFilter());
        try {
            builder.build(data, "http://www.example.org/");
            fail("built document into document");
        }
        catch (ParsingException success) {
            assertNotNull(success.getMessage());   
        }            
        
    }

    
    public void testCantAddTwoDoctypes() 
      throws ParsingException, IOException {
        
        String data = "<!DOCTYPE a><a></a>";
        Builder builder = new Builder(new NodeFactory() {
            
            public Nodes makeDocType(String name, String publicID, String systemID) {
                Nodes result = new Nodes();
                result.append(new DocType(name, publicID, systemID));
                result.append(new Comment("sajdha"));
                result.append(new DocType(name, publicID, systemID));
                return result;
            }
            
        });
        try {
            builder.build(data, "http://www.example.org/");
            fail("built two doctypes");
        }
        catch (ParsingException success) {
            assertNotNull(success.getMessage());   
        }       
        
    }
    
    
    public void testChangeAttributesToElements() 
      throws ParsingException, IOException {
        
        String data = "<a name=\"test\" value=\"data\"/>";
        Builder builder = new Builder(new NodeFactory() {
            
            public Nodes makeAttribute(String name, String URI, 
              String value, Attribute.Type type) {
                Nodes result = new Nodes();
                Element element = new Element(name, URI);
                element.appendChild(value);
                result.append(element);
                return result;
            }
            
        });
        Document doc = builder.build(data, "http://www.example.org/");
        Element root = doc.getRootElement();
        assertEquals(2, root.getChildCount());
        Element name = root.getFirstChildElement("name");
        Element value = root.getFirstChildElement("value");
        assertEquals("test", name.getValue());         
        assertEquals("data", value.getValue());         
        assertEquals("name", name.getLocalName());         
        assertEquals("value", value.getQualifiedName());     
        
    }

    
    public void testInsertElementsInInternalDTDSubsetViaProcessingInstruction() 
      throws ParsingException, IOException {
        
        String data = "<!DOCTYPE a [<?target data?>]><a><b>data1</b><c>text</c></a>";
        Builder builder = new Builder(new NodeFactory() {
            
            public Nodes makeProcessingInstruction(String target, String data) {
                Nodes result = new Nodes();
                Element e = new Element(target);
                e.appendChild(data);
                result.append(e);
                return result;
            }
            
        });
        try {
           builder.build(data, "http://www.example.org/");
           fail("Allowed element in internal DTD subset via processing instruction");
        }
        catch (ParsingException success) {
            assertNotNull(success.getMessage());
        }
        
    }
    
    
    public void testInsertElementsInInternalDTDSubsetViaComment() 
      throws ParsingException, IOException {
        
        String data = "<!DOCTYPE a [<!--data-->]><a><b>data1</b><c>text</c></a>";
        Builder builder = new Builder(new NodeFactory() {
            
            public Nodes makeComment(String data) {
                Nodes result = new Nodes();
                Element e = new Element("comment");
                e.appendChild(data);
                result.append(e);
                return result;
            }
            
        });
        try {
           builder.build(data, "http://www.example.org/");
           fail("Allowed element in internal DTD subset via comment");
        }
        catch (ParsingException success) {
            assertNotNull(success.getMessage());
        }
        
    }   
    
    
    public void testChangeElementsToAttributes() 
      throws ParsingException, IOException {
        
        String data = "<a><b>data1</b><c>text</c></a>";
        Builder builder = new Builder(new NodeFactory() {
            
            public Nodes finishMakingElement(Element element) {
                Nodes result = new Nodes();
                if (element.getParent() instanceof Document) {
                    result.append(element);
                }
                else {
                    result.append(new Attribute(element.getLocalName(), element.getValue()));
                }
                return result;
            }
            
        });
        Document doc = builder.build(data, "http://www.example.org/");
        Element root = doc.getRootElement();
        assertEquals(0, root.getChildCount());
        assertEquals(2, root.getAttributeCount());
        assertEquals("data1", root.getAttribute("b").getValue());         
        assertEquals("text", root.getAttribute("c").getValue());   
        
    }


    public void testChangeDefaultNamespaceFromEnd() 
      throws ParsingException, IOException {
        
        String data = "<a><b xmlns='http://www.a.com'/></a>";
        Builder builder = new Builder(new NodeFactory() {
            
            public Nodes finishMakingElement(Element element) {
                Nodes result = new Nodes(element);
                element.setNamespaceURI("http://www.b.org/");
                return result;
            }
            
        });
        Document doc = builder.build(data, "http://www.example.org/");
        Element root = doc.getRootElement();
        Element child = (Element) root.getChild(0);
        assertEquals("http://www.b.org/", child.getNamespaceURI());
        
    }


    // XXX need to test changing namespaces of attributes too
    public void testChangePrefixedNamespaceFromEnd() 
      throws ParsingException, IOException {
        
        String data = "<a><pre:b xmlns:pre='http://www.a.com'/></a>";
        Builder builder = new Builder(new NodeFactory() {
            
            public Nodes finishMakingElement(Element element) {
                Nodes result = new Nodes(element);
                element.setNamespaceURI("http://www.b.org/");
                return result;
            }
            
        });
        Document doc = builder.build(data, "http://www.example.org/");
        Element root = doc.getRootElement();
        Element child = (Element) root.getChild(0);
        assertEquals("http://www.b.org/", child.getNamespaceURI());
        
    }
    
    
    public void testChangeDefaultNamespaceFromBeginning() 
      throws ParsingException, IOException {
        
        String data = "<a><b xmlns='http://www.a.com'/></a>";
        Builder builder = new Builder(new NodeFactory() {
            
            public Element startMakingElement(String name, String namespaceURI) {
                return new Element(name, "http://www.b.org/");
            }
            
        });
        Document doc = builder.build(data, "http://www.example.org/");
        Element root = doc.getRootElement();
        Element child = (Element) root.getChild(0);
        assertEquals("http://www.b.org/", child.getNamespaceURI());
        
    }


    public void testChangePrefixedNamespaceFromBeginning() 
      throws ParsingException, IOException {
        
        String data = "<a><pre:b xmlns:pre='http://www.a.com'/></a>";
        Builder builder = new Builder(new NodeFactory() {
            
            public Element startMakingElement(String name, String namespaceURI) {
                return new Element(name, "http://www.b.org/");
            }
            
        });
        Document doc = builder.build(data, "http://www.example.org/");
        Element root = doc.getRootElement();
        Element child = (Element) root.getChild(0);
        assertEquals("http://www.b.org/", child.getNamespaceURI());
        
    }


    public void testChangeTextToAttributes() 
      throws ParsingException, IOException {
        
        String data = "<a><b>data1</b><c>text</c></a>";
        Builder builder = new Builder(new NodeFactory() {
            
            public Nodes makeText(String text) {
                Nodes result = new Nodes();
                result.append(new Attribute("name", text));
                return result;
            }
            
        });
        Document doc = builder.build(data, "http://www.example.org/");
        Element root = doc.getRootElement();
        assertEquals(2, root.getChildCount());
        assertEquals(0, root.getAttributeCount());
        assertEquals("", root.getValue());
        Element b = root.getFirstChildElement("b");
        Element c = root.getFirstChildElement("c");
        assertEquals("data1", b.getAttribute("name").getValue());
        assertEquals("text", c.getAttribute("name").getValue());
        
    }

    
    public void testChangeRootElementsToAttribute() 
      throws ParsingException, IOException {
        
        String data = "<a><b>data1</b><c>text</c></a>";
        Builder builder = new Builder(new NodeFactory() {
            
            public Nodes finishMakingElement(Element element) {
                Nodes result = new Nodes();
                result.append(new Attribute(element.getLocalName(), element.getValue()));
                return result;
            }
            
        });
        try {
            builder.build(data, "http://www.example.org/");
            fail("replaced root element with attribute");
        }
        catch (ParsingException success) {
            assertNotNull(success.getMessage());
        }
        
    }
    
    
    public void testCantBypassMultipleParentChecks() 
      throws ParsingException, IOException {
        
        String doc = "<root><a/><a/></root>";   
        Builder builder = new Builder(new NodeFactory() {
            
            private Element a = new Element("a");
            
            public Element startMakingElement(String name, String namespace) {
                if (name.equals("a")) return a;
                return new Element(name, namespace);    
            }
            
        });
        try {
            builder.build(doc, "http://www.example.org/");
            fail("built with multiple parents");
        }
        catch (ParsingException success) {
            assertNotNull(success.getMessage());   
        }            

    }

    
    public void testCantBypassMultipleParentChecksFromFinishMakingElement() 
      throws ParsingException, IOException {
        
        String doc = "<root><a/><a/></root>";   
        Builder builder = new Builder(new NodeFactory() {
            
            private Element a = new Element("a");
            
            public Nodes finishMakingElement(Element element) {
                if (element.getLocalName().equals("a")) return new Nodes(a);
                else return new Nodes(element);    
            }
            
        });
        try {
            builder.build(doc, "http://www.example.org/");
            fail("built with multiple parents");
        }
        catch (ParsingException success) {
            assertNotNull(success.getMessage());   
        }            

    }
 
    
    public void testFinishMakingElementIsCalledForRootElement() 
      throws ParsingException, IOException {
        
        String doc = "<root/>";   
        Builder builder = new Builder(new NodeFactory() {
            
            public Nodes finishMakingElement(Element element) {
                throw new XMLException("Method was called");   
            }
            
        });
        try {
            builder.build(doc, "http://www.example.org/");
            fail("Did not call finishMakingElement for root");
        }
        catch (ParsingException success) {
            assertEquals("Method was called", success.getMessage());   
        }            

    }
 
    
    public void testCanReplaceRootElementFromFinishMakingElement() 
      throws ParsingException, IOException {
        
        String data = "<root/>";   
        Builder builder = new Builder(new NodeFactory() {
            
            public Nodes finishMakingElement(Element element) {
                Nodes result = new Nodes();
                result.append(new Comment("test"));
                result.append(new Element("newroot"));
                result.append(new ProcessingInstruction("test", "test"));
                return result;   
            }
            
        });
        Document doc = builder.build(data, "http://www.example.org/");
        assertEquals("newroot", doc.getRootElement().getQualifiedName());            
        assertEquals(3, doc.getChildCount());

    }
 
    
    public void testCanAddAroundExistingRootElementFromFinishMakingElement() 
      throws ParsingException, IOException {
        
        String data = "<root/>";   
        Builder builder = new Builder(new NodeFactory() {
            
            public Nodes finishMakingElement(Element element) {
                Nodes result = new Nodes();
                result.append(new Comment("test"));
                result.append(element);
                result.append(new ProcessingInstruction("test", "test"));
                return result;   
            }
            
        });
        Document doc = builder.build(data, "http://www.example.org/");
        assertEquals("root", doc.getRootElement().getQualifiedName());            
        assertEquals(3, doc.getChildCount());

    }
 
    
    public void testCantReplaceRootElementWithNoElement() 
      throws ParsingException, IOException {
        
        String data = "<root/>";   
        Builder builder = new Builder(new NodeFactory() {
            
            public Nodes finishMakingElement(Element element) {
                Nodes result = new Nodes();
                result.append(new Comment("test"));
                result.append(new ProcessingInstruction("test", "test"));
                return result;   
            }
            
        });
        try {
            builder.build(data, "http://www.example.org/");
            fail("Built document without root element");
        }
        catch (ParsingException success) {
            assertNotNull(success.getMessage()); 
        }            

    }

    
    public void testCantReplaceRootElementWithNothing() 
      throws ParsingException, IOException {
        
        String data = "<root/>";   
        Builder builder = new Builder(new NodeFactory() {
            
            public Nodes finishMakingElement(Element element) {
                return new Nodes();   
            }
            
        });
        try {
            builder.build(data, "http://www.example.org/");
            fail("Built document without root element");
        }
        catch (ParsingException success) {
            assertNotNull(success.getMessage()); 
        }            

    }

    
    public void testReplaceCommentWithAttribute() 
      throws ParsingException, IOException {
        
        String data = "<root><!--comment--></root>";   
        Builder builder = new Builder(new NodeFactory() {
            
            public Nodes makeComment(String data) {
                return new Nodes(new Attribute("name", "value"));   
            }
            
        });
        Document doc = builder.build(data, "http://www.example.org/");
        Element root = doc.getRootElement();
        assertEquals(0, root.getChildCount());
        assertEquals(1, root.getAttributeCount());
        assertEquals("value", root.getAttribute("name").getValue());

    }
 
    
    public void testReplaceProcessingInstructionWithAttribute() 
      throws ParsingException, IOException {
        
        String data = "<root><?target data?></root>";   
        Builder builder = new Builder(new NodeFactory() {
            
            public Nodes makeProcessingInstruction(String target, String data) {
                return new Nodes(new Attribute("name", "value"));   
            }
            
        });
        Document doc = builder.build(data, "http://www.example.org/");
        Element root = doc.getRootElement();
        assertEquals(0, root.getChildCount());
        assertEquals(1, root.getAttributeCount());
        assertEquals("value", root.getAttribute("name").getValue());

    }
 
    
    public void testReplaceProcessingInstructionWithText() 
      throws ParsingException, IOException {
        
        String data = "<root><?target data?></root>";   
        Builder builder = new Builder(new NodeFactory() {
            
            public Nodes makeProcessingInstruction(String target, String data) {
                return new Nodes(new Text(data));   
            }
            
        });
        Document doc = builder.build(data, "http://www.example.org/");
        Element root = doc.getRootElement();
        assertEquals(1, root.getChildCount());
        assertEquals(0, root.getAttributeCount());
        assertEquals("data", root.getValue());

    }
 
    
    public void testCantReplaceRootElementWithTwoElements() 
      throws ParsingException, IOException {
        
        String data = "<root/>";   
        Builder builder = new Builder(new NodeFactory() {
            
            public Nodes finishMakingElement(Element element) {
                Nodes result = new Nodes();
                result.append(new Element("first"));
                result.append(new Element("second"));
                return result;   
            }
            
        });
        try {
            builder.build(data, "http://www.example.org/");
            fail("Built document without root element");
        }
        catch (ParsingException success) {
            assertNotNull(success.getMessage()); 
        }            

    }
    
    
    public void testOrderOfCalls() 
      throws ParsingException, IOException {
        
        String data = "<root>1<child>2</child>3</root>";
        Builder builder = new Builder(new NodeFactory() {
            
            String s = "";
            
            public Nodes makeText(String text) {
                s += text;
                return super.makeText(text);
            }
            
            public Element startMakingElement(String name, String namespace) {
                
                if (name.equals("child")) assertEquals("1", s);
                return super.startMakingElement(name, namespace);    
            }
            
            public Nodes finishMakingElement(Element element) {
                if (element.getLocalName().equals("child")) assertEquals("12", s);
                if (element.getLocalName().equals("root")) assertEquals("123", s);
                return super.finishMakingElement(element);    
            }
            
        });
        builder.build(data, null);
    }
 
    
    public void testOrderOfCallsWithPI() 
      throws ParsingException, IOException {
        
        String data = "<root>1<?data ?>2</root>";
        Builder builder = new Builder(new NodeFactory() {
            
            String s = "";
            
            public Nodes makeText(String text) {
                s += text;
                return super.makeText(text);
            }
            
            public Nodes makeProcessingInstruction(String target, String data) {
                
                assertEquals("1", s);
                return new Nodes();    
            }
            
        });
        Document doc = builder.build(data, null);
        assertEquals(2, doc.getRootElement().getChildCount());
        
    }
 
    
    public void testFinishMakingElementDetachesItsArgument() 
      throws ParsingException, IOException {
        
        String data = "<root><a><b /></a></root>";
        Builder builder = new Builder(new NodeFactory() {
            
            public Nodes finishMakingElement(Element element) {
                
                if (element.getLocalName().equals("b")) {
                    element.detach();
                    return new Nodes();    
                }
                return new Nodes(element);
            }
            
        });
        
        try {
            builder.build(data, null);
            fail("Allowed finishmakingElement to detach its argument");
        }
        catch (ParsingException success) {
            assertNotNull(success.getMessage());
        }
        
    }

}
