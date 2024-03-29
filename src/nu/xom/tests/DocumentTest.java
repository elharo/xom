/* Copyright 2002-2004, 2011 Elliotte Rusty Harold
   
   This library is free software; you can redistribute it and/or modify
   it under the terms of version 2.1 of the GNU Lesser General Public 
   License as published by the Free Software Foundation.
   
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
   GNU Lesser General Public License for more details.
   
   You should have received a copy of the GNU Lesser General Public
   License along with this library. If not, see
   <https://www.gnu.org/licenses/>.
   
   You can contact Elliotte Rusty Harold by sending e-mail to
   elharo@ibiblio.org. Please include the word "XOM" in the
   subject line. The XOM home page is located at https://xom.nu/
*/

package nu.xom.tests;

import java.io.File;
import java.io.IOException;

import nu.xom.Builder;
import nu.xom.Comment;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.IllegalAddException;
import nu.xom.MultipleParentException;
import nu.xom.NoSuchChildException;
import nu.xom.ParsingException;
import nu.xom.ProcessingInstruction;
import nu.xom.Text;
import nu.xom.WellformednessException;


/**
 * <p>
 *  Various basic tests for the <code>Document</code> class.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.3.0
 *
 */
public class DocumentTest extends XOMTestCase {

    public DocumentTest(String name) {
        super(name);
    }
    
    private Element root;
    private Document doc;   
    
    protected void setUp() {
        root = new Element("root");
        doc = new Document(root);
    }
    
    
    public void testToString() {
        assertEquals("[nu.xom.Document: root]", doc.toString());
    }
    
    
    public void testDocTypeInsertion() {
        
        DocType type1 = new DocType("root");
        
        try {
            doc.insertChild(type1, 1);
            fail("inserted doctype after root element");
        }
        catch (IllegalAddException success) {
            assertNotNull(success.getMessage());
        }
        
        doc.insertChild(type1, 0);
        assertEquals(type1, doc.getDocType());
        
        DocType type2 = new DocType("test");
        try {
            doc.insertChild(type2, 1);
            fail("Inserted 2nd DocType");
        }
        catch (IllegalAddException success) {
            assertNotNull(success.getMessage());
        }
        assertEquals(type1, doc.getDocType());
        assertNull(type2.getParent());
        assertEquals(type1, doc.getChild(0));
        
        doc.setDocType(type2);
        assertEquals(doc.getDocType(), type2);
        assertNull(type1.getParent());
        assertEquals(type2, doc.getChild(0));
        
    }

    
    public void testSetDocType() {
        
        DocType type1 = new DocType("root");       
        doc.setDocType(type1);
        assertEquals(type1, doc.getDocType());
        
        doc.insertChild(new Comment("test"), 0);

        DocType type2 = new DocType("root", "http://www.example.com/");       
        doc.setDocType(type2);
        assertEquals(type2, doc.getDocType());
        assertEquals(1, doc.indexOf(type2));
        assertNull(type1.getParent());
        assertEquals(doc, type2.getParent());
        
        // set same doctype
        doc.setDocType(type2);
        assertEquals(type2, doc.getDocType());
        assertEquals(1, doc.indexOf(type2));
        assertEquals(doc, type2.getParent());
        
        try {
            doc.setDocType(null); 
            fail("Allowed null doctype");   
        }
        catch (NullPointerException success) {
            assertNotNull(success.getMessage());
            assertEquals(type2, doc.getDocType());   
        }
        
        try {
            Document doc2 = new Document(new Element("root"));
            doc2.setDocType(type2); 
            fail("Allowed multiple parents for doctype");   
        }
        catch (MultipleParentException success) {
            assertNotNull(success.getMessage());   
        }
        
    }

    
    public void testBaseURI() {
        
        assertEquals("", doc.getBaseURI());
        doc.setBaseURI("http://www.example.com/index.xml");
        assertEquals(
          "http://www.example.com/index.xml",
          doc.getBaseURI()
        );
        doc.setBaseURI("file:///home/elharo/XOM/data/test.xml");
        assertEquals(
          "file:///home/elharo/XOM/data/test.xml", 
          doc.getBaseURI()
        );
        doc.setBaseURI("file:///home/elharo/XO%4D/data/test.xml");
        assertEquals(
          "file:///home/elharo/XO%4D/data/test.xml",
          doc.getBaseURI()
        );

    }
    
    
    public void testSecondRoot() {
    
        try {
            doc.insertChild(new Element("test"), 0);
            fail("Added second root element");
        }
        catch (IllegalAddException success) {
            assertNotNull(success.getMessage());
        }
        
    }

    
    public void testSetRoot() {
        
        Element newRoot = new Element("newroot");
        doc.setRootElement(newRoot);
        
        assertEquals(newRoot, doc.getRootElement());
        assertNull(root.getParent());
        
        try {
            doc.setRootElement(null);              
        }
        catch (NullPointerException success) {
            assertNotNull(success.getMessage());
        }
        
        Element top = new Element("top");
        Element child = new Element("child");
        top.appendChild(child);
        try {
            doc.setRootElement(child);
            fail("Allowed element with two parents");
        }
        catch (MultipleParentException success) {
            assertNotNull(success.getMessage());
        }
        
    }

    
    public void testReplaceRootWithItself() {
        Element root = doc.getRootElement();
        doc.setRootElement(root);
        assertEquals(root, doc.getRootElement());
    }

    
    public void testReplaceRootElementWithDifferentElementUsingReplaceChild() {
        
        Element newRoot = new Element("newRoot");
        Element oldRoot = doc.getRootElement();
        doc.replaceChild(oldRoot, newRoot);
        assertEquals(newRoot, doc.getRootElement());
        assertNull(oldRoot.getParent());
        assertEquals(doc, newRoot.getParent());
        
    }

    
    public void testInsertionAllowed() {
        
        Document doc = new Document(new Element("root"));
        Comment original = new Comment("original");
        doc.insertChild(original, 0);
        
        Element temp = new Element("temp");
        Comment c2 = new Comment("new comment");
        temp.appendChild(c2);
        
        try {
            doc.replaceChild(original, c2);
            fail("Missed multiple parent exception");
        }
        catch (MultipleParentException success) {
            assertEquals(2, doc.getChildCount());
        }
        
    }
    
    
    public void testReplaceDocTypeWithDifferentDocTypeUsingReplaceChild() {
        
        DocType newDocType = new DocType("new");
        DocType oldDocType = new DocType("old");
        doc.setDocType(oldDocType);
        doc.replaceChild(oldDocType, newDocType);
        assertEquals(newDocType, doc.getDocType());
        assertNull(oldDocType.getParent());
        assertEquals(doc, newDocType.getParent());
        
    }

    
    public void testReplaceDocTypeWithParentedDocTypeUsingReplaceChild() {
        
        DocType newDocType = new DocType("new");
        DocType oldDocType = new DocType("old");
        Document temp = new Document(new Element("root"));
        temp.setDocType(newDocType);
        
        doc.setDocType(oldDocType);
        try {
            doc.replaceChild(oldDocType, newDocType);
            fail("Missed MultipleParentException");
        }
        catch (MultipleParentException success) {
            assertEquals(2, doc.getChildCount());
            assertEquals(2, temp.getChildCount());
        }
        
        assertEquals(oldDocType, doc.getDocType());
        assertNotNull(oldDocType.getParent());
        assertEquals(doc, oldDocType.getParent());
        assertEquals(newDocType, temp.getDocType());
        assertNotNull(oldDocType.getParent());
        assertEquals(temp, newDocType.getParent());
        
    }

    
    public void testReplaceRootElementWithParentedElementUsingReplaceChild() {
        
        Element oldRoot = new Element("oldRoot");
        Element newRoot = new Element("newRoot");
        Document doc = new Document(oldRoot);
        Element temp = new Element("temp");
        temp.appendChild(newRoot);
        
        try {
            doc.replaceChild(oldRoot, newRoot);
            fail("Missed MultipleParentException");
        }
        catch (MultipleParentException success) {
            assertEquals(1, doc.getChildCount());
            assertEquals(1, temp.getChildCount());
        }
        
        assertEquals(oldRoot, doc.getRootElement());
        assertEquals(newRoot, temp.getChild(0));
        assertNotNull(oldRoot.getParent());
        assertEquals(temp, newRoot.getParent());
        
    }

    
    public void testReplaceRootElementWithComment() {
        
        Element oldRoot = new Element("oldRoot");
        Document doc = new Document(oldRoot);
        
        try {
            doc.replaceChild(oldRoot, new Comment("c"));
            fail("Replaced root with comment");
        }
        catch (WellformednessException success) {
            assertNotNull(success.getMessage());
        }
        
        assertEquals(oldRoot, doc.getRootElement());
        assertEquals(doc, oldRoot.getParent());
        
    }

    
    public void testReplaceNonDocTypeWithDocTypeUsingReplaceChild() {
        
        Comment c = new Comment("Not a doctype");
        DocType newDocType = new DocType("new");
        doc.insertChild(c, 0);
        doc.replaceChild(c, newDocType);
        assertEquals(newDocType, doc.getDocType());
        assertNull(c.getParent());
        assertEquals(doc, newDocType.getParent());
        
    }

    
    public void testDetach() {
        Comment comment 
          = new Comment("This will be attached then detached");
        doc.appendChild(comment);
        assertEquals(doc, comment.getParent());
        comment.detach();
        assertNull(comment.getParent());
    }

    
    public void testGetDocument() {
        assertEquals(doc, doc.getDocument());
    }

    
    public void testConstructor() {
    
        assertEquals(root, doc.getRootElement());
        assertEquals(1, doc.getChildCount());
        
        Element newRoot = new Element("newRoot");
        doc.setRootElement(newRoot);
        assertEquals(newRoot, doc.getRootElement());
        assertEquals(1, doc.getChildCount());
        
        doc.appendChild(new Comment("test"));
        assertEquals(2, doc.getChildCount());

        doc.insertChild(new Comment("prolog comment"), 0);
        assertEquals(3, doc.getChildCount());
        assertTrue(doc.getChild(0) instanceof Comment);
        assertTrue(doc.getChild(1) instanceof Element);
        assertTrue(doc.getChild(2) instanceof Comment);

        doc.insertChild(new ProcessingInstruction("target", "data"),1);
        assertTrue(doc.getChild(0) instanceof Comment);
        assertTrue(doc.getChild(1) instanceof ProcessingInstruction);
        assertTrue(doc.getChild(2) instanceof Element);
        assertTrue(doc.getChild(3) instanceof Comment);

        doc.insertChild(new ProcessingInstruction("epilog", "data"),3);
        assertTrue(doc.getChild(0) instanceof Comment);
        assertTrue(doc.getChild(1) instanceof ProcessingInstruction);
        assertTrue(doc.getChild(2) instanceof Element);
        assertTrue(doc.getChild(3) instanceof ProcessingInstruction);
        assertTrue(doc.getChild(4) instanceof Comment);

        
        try {
            Element nullRoot = null;
            new Document(nullRoot);
            fail("allowed null root!");
        }
        catch (NullPointerException success) {
        }
        
        try {
            Document nullDoc = null;
            new Document(nullDoc);
            fail("allowed null doc!");
        }
        catch (NullPointerException success) {
            // success  
        }
        
    }
    
    
    public void testCopyConstructor() {
        
        doc.insertChild(new Comment("text"), 0);
        doc.insertChild(new ProcessingInstruction("text", "data"), 1);
        doc.insertChild(new DocType("text"), 2);
        root.appendChild("some data");
        doc.appendChild(new Comment("after"));
        doc.appendChild(new ProcessingInstruction("text", "after"));
        
        Document doc2 = new Document(doc);
        assertEquals(doc, doc2);
        
    }
    
    
    public void testCopyConstructorBaseURI() {
        
        doc.setBaseURI("http://www.example.com/");
        
        Document doc2 = new Document(doc);
        assertEquals(doc.getBaseURI(), doc2.getBaseURI());
        assertEquals("http://www.example.com/", doc2.getBaseURI());
        assertEquals(
          doc.getRootElement().getBaseURI(),
          doc2.getRootElement().getBaseURI()
        );
        assertEquals(
          "http://www.example.com/", 
          doc2.getRootElement().getBaseURI()
        );
        
    }
    
    
    public void testCopy() {
        
        doc.insertChild(new Comment("text"), 0);
        doc.insertChild(new ProcessingInstruction("text", "data"), 1);
        doc.insertChild(new DocType("text"), 2);
        root.appendChild("some data");
        doc.appendChild(new Comment("after"));
        doc.appendChild(new ProcessingInstruction("text", "after"));
        
        Document doc2 = doc.copy();
        assertEquals(doc, doc2);
        
    }
    
    
    public void testAppend() {
    
        Element root = new Element("root");
        Document doc = new Document(root);

        try {
            doc.appendChild(new Text("test"));
            fail("appended string");
        }   
        catch (IllegalAddException success) {
            assertNotNull(success.getMessage());
        }
        
        try {
            doc.appendChild(new Text("    "));
            fail("appended white space");
        }   
        catch (IllegalAddException success) {
            assertNotNull(success.getMessage());
        }
        
        try {
            doc.appendChild(new Text("test"));
            fail("appended Text");
        }   
        catch (IllegalAddException success) {
            assertNotNull(success.getMessage());
        }
        
        try {
            doc.appendChild(new Comment("test"));
            doc.appendChild(new Element("test"));
            fail("appended element");
        }   
        catch (IllegalAddException success) {
            assertNotNull(success.getMessage());
        }
        
        try {
            doc.insertChild(new Element("test"), 0);
            fail("inserted element");
        }   
        catch (IllegalAddException success) {
            assertNotNull(success.getMessage());
        }
        
    }

    
    public void testRemoval() {
    
        Element root = new Element("root");
        Document doc = new Document(root);

        try {
            root.detach();
            fail("detached root element");
        }   
        catch (WellformednessException success) {
            assertNotNull(success.getMessage());
        }
        
        try {
            doc.removeChild(root);
            fail("removed root element");
        }   
        catch (WellformednessException success) {
            assertNotNull(success.getMessage());
        }
        
        try {
            doc.removeChild(0);
            fail("removed root element");
        }   
        catch (WellformednessException success) {
            assertNotNull(success.getMessage());
        }
        
        doc.appendChild(new Comment("test"));
        doc.removeChild(1);
        assertEquals(1, doc.getChildCount());
        
        Comment test = new Comment("test");
        doc.appendChild(test);
        doc.removeChild(test);
        assertEquals(1, doc.getChildCount());
        
        try {
            Comment something = new Comment("sd");
            doc.removeChild(something);
            fail("Removed nonchild");
        }
        catch (NoSuchChildException success) {
            assertNotNull(success.getMessage());
        }

        try {
            doc.removeChild(20);
            fail("removed overly sized element");
        }   
        catch (IndexOutOfBoundsException success) {
            assertNotNull(success.getMessage());
        }
        
    }

    
    public void testToXML() throws ParsingException, IOException {
        
        Builder builder = new Builder();
        File f = new File("data");
        f = new File(f, "test.xml");   
        Document input = builder.build(f);
        String s = input.toXML();
        Document output = builder.build(s, f.toURI().toString());
        assertEquals(input, output);
        
    }

    
}
