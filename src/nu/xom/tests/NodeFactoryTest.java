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
import nu.xom.ParsingException;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Comment;
import nu.xom.CycleException;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.IllegalAddException;
import nu.xom.Node;
import nu.xom.NodeFactory;
import nu.xom.Nodes;
import nu.xom.Text;
import nu.xom.ValidityException;

/**
 * <p>
 *   Tests that subclasses of <code>NodeFactory</code> can filter 
 *   on building in various ways.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d23
 *
 */
public class NodeFactoryTest extends XOMTestCase {

    public void testSkippingComment() 
      throws IOException, ParsingException {
        
        String data = "<a>1<!--tetetwkfjkl-->8</a>";
        Builder builder = new Builder(new CommentFilter());
        Document doc = builder.build(data, "http://www.example.org/");
        Element root = doc.getRootElement();
        assertEquals("Skipped comment interrupted text node", 
          1, root.getChildCount());
        assertEquals("18", doc.getValue());
        Node first = root.getChild(0);
        assertEquals(first.getValue(), "18");        
    }

    private static class CommentFilter extends NodeFactory {
        
        public Nodes makeComment(String data) {
            return new Nodes();
        }
        
    }

    public void testCantAddOneElementMultipleTimes() 
        throws IOException, ParsingException {
        
        String data = "<a><b>18</b></a>";
        Builder builder = new Builder(new SingleElementFactory());
        try {
            Document doc = builder.build(data,
              "http://www.example.org/");
            fail("Allowed one element in several places");
        }
        catch (CycleException ex) {
            // success   
            assertNotNull(ex.getMessage());
        }
                
    }

    public void testCantAddOneAttributeMultipleTimes() 
        throws IOException, ParsingException {
        
        String data = "<a test=\"value\" name=\"data\"></a>";
        Builder builder = new Builder(new SingleAttributeFactory());
        try {
            Document doc = builder.build(data,
              "http://www.example.org/");
            fail("Allowed one attribute twice");
        }
        catch (IllegalAddException ex) {
            // success   
            assertNotNull(ex.getMessage());
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

    private static class CFactory extends NodeFactory {

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

    private static class CallsMakeRoot extends NodeFactory {

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
          1, root.getChildCount()
        );
        assertEquals("18", doc.getValue());
        Node first = root.getChild(0);
        assertEquals(first.getValue(), "18");        
    }

    private static class ProcessingInstructionFilter 
      extends NodeFactory {

        public Nodes makeProcessingInstruction(
          String target, String data) {
            return new Nodes();
        }

    }

    public void testSkippingIgnorableWhiteSpace() 
      throws IOException, ParsingException {
        
        String data = "<!DOCTYPE root [ ";
        data += "<!ELEMENT root (p*)> ";
        data += "<!ELEMENT p (#PCDATA)> ]>";
        data += "<root> <p>   </p> </root>";
        Builder builder = new Builder(new IgnorableWhiteSpaceFilter());
        Document doc = builder.build(data, "http://www.example.org/");
        Element root = doc.getRootElement();
        assertEquals("Failed to ignore white space", 
          1, root.getChildCount());
        assertEquals("   ", doc.getValue());
        Node first = root.getChild(0);
        assertEquals(first.getValue(), "   ");        
    }

    private static class IgnorableWhiteSpaceFilter 
      extends NodeFactory {

        public Nodes makeWhiteSpaceInElementContent(String data) {
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
        assertEquals(3, root.getChildCount());
        Node first = root.getChild(0);
        assertEquals("12", first.getValue());
        Node middle = root.getChild(1);
        assertEquals("34innermost56", middle.getValue());
        Node last = root.getChild(2);
        assertEquals("78", last.getValue());
        
        Node innermost = middle.getChild(1);
        assertEquals("innermost", innermost.getValue());
        Node inner1 = middle.getChild(0);
        assertEquals("34", inner1.getValue());
        Node inner3 = middle.getChild(2);
        assertEquals("56", inner3.getValue());
        
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
        
        File input = new File("data/entitytest.xml");
        Builder builder = new Builder(new MinimizingFactory());
        Document doc = builder.build(input);
        assertEquals(1, doc.getChildCount());
        Element root = doc.getRootElement();
        assertEquals("root", root.getQualifiedName());
        assertEquals("", root.getNamespaceURI());
        assertEquals(0, root.getChildCount());
        assertEquals(0, root.getAttributeCount());
        
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
    
        protected Nodes finishMakingElement(Element element) {
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
    
        public Nodes makeWhiteSpaceInElementContent(String data) {
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
        
        File input = new File("data/entitytest.xml");
        Builder builder = new Builder(new NullElementFactory());
        try {
            Document doc = builder.build(input);
            fail("Allowed null root");
        }
        catch (NullPointerException ex) {
           // success   
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
        
        File input = new File("data/entitytest.xml");
        Builder builder = new Builder(new NullDocumentFactory());
        try {
            Document doc = builder.build(input);
            fail("Allowed null document");
        }
        catch (NullPointerException ex) {
           // success   
        }        
    }

    // Returns null for all elements including the root.
    // This should cause an exception.
    static class NullDocumentFactory extends NodeFactory {

        public Document makeDocument() {
            return null;   
        }

    }

    public void testSkipping() throws IOException, ParsingException {
        
        String data = "<a>data<b>data<a>data</a>data</b>data</a>";
        Builder builder = new Builder(new BFilter());
        Document doc = builder.build(data, "http://www.example.org/");
        Element root = doc.getRootElement();
        assertEquals(3, root.getChildCount());
        assertEquals("datadatadatadatadata", root.getValue());
        Element middle = (Element) root.getChild(1);
        assertEquals("data", middle.getValue());
        Node start = root.getChild(0);
        Node end = root.getChild(2);
        assertEquals("datadata", start.getValue());
        assertEquals("datadata", end.getValue());      
        
    }

    public void testCoalesceTextNodes() 
      throws IOException, ParsingException {  
        String data = "<a>data<!-- comment--> data</a>";
        Builder builder = new Builder(new CommentFilter());
        Document doc = builder.build(data, "http://www.example.org/");
        Element root = doc.getRootElement();
        assertEquals(1, root.getChildCount());
        assertEquals("data data", root.getValue());
        Text text = (Text) root.getChild(0);
        assertEquals("data data", text.getValue());      
    }

    private static class TripleElementFilter extends NodeFactory {
     
        public Nodes finishMakingElement(Element element) {
            Nodes result = new Nodes(element);   
            result.append(element.copy());
            result.append(element.copy());
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
        assertEquals("<a><b><c /><c /><c /></b><b><c /><c /><c /></b><b><c /><c /><c /></b></a>", root.toXML());      
    }

    private static class UncommentFilter extends NodeFactory {
     
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
        catch (IllegalAddException success) {
            assertNotNull(success.getMessage());   
        }            
    }
    
    private static class DocumentFilter extends NodeFactory {
     
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
        catch (IllegalAddException success) {
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
        catch (IllegalAddException success) {
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
        Element name = (Element) root.getChild(0);
        Element value = (Element) root.getChild(1);
        assertEquals("test", name.getValue());         
        assertEquals("data", value.getValue());         
        assertEquals("name", name.getLocalName());         
        assertEquals("value", value.getQualifiedName());         
    }
    
 
}
