/* Copyright 2002-2004, 2006 Elliotte Rusty Harold
   
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

import nu.xom.Comment;
import nu.xom.CycleException;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.IllegalAddException;
import nu.xom.MultipleParentException;
import nu.xom.NoSuchChildException;
import nu.xom.Node;
import nu.xom.ProcessingInstruction;
import nu.xom.Text;


/**
 * <p>
 *   Tests adding, removing, and counting children from parent nodes.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.3.0
 *
 */
public class ParentNodeTest extends XOMTestCase {

    public ParentNodeTest(String name) {
        super(name);
    }
    
    
    private Element empty;
    private Element notEmpty;
    private Text child;
    
    
    protected void setUp() {
        empty = new Element("Empty");
        notEmpty = new Element("NotEmpty");
        child = new Text("Hello");
        notEmpty.appendChild(child);
    }

    
    public void testDetach() {
        
        Text text = new Text("This will be attached then detached");
        empty.appendChild(text);
        assertEquals(empty, text.getParent());
        text.detach();
        assertNull(text.getParent());
        
    }

    
    public void testAppendChild() {   
        
        Element child = new Element("test");
        empty.appendChild(child);
        assertEquals(1, empty.getChildCount());
        assertEquals(empty.getChild(0), child);
        child.detach();
        
        notEmpty.appendChild(child);
        assertFalse(notEmpty.getChild(0).equals(child));
        assertTrue(notEmpty.getChild(1).equals(child));
        
    } 

    
    public void testAppendNull() {   
        
        Element child = null;
        try {
            empty.appendChild(child);
            fail("Inserted null");
        }
        catch (NullPointerException ex) {
            assertNotNull(ex.getMessage());
        }
        
    } 

    
    public void testAppendChildToItself() { 
        
        Element child = new Element("test");
        try {
            child.appendChild(child);
            fail("Appended node to itself");
        }
        catch (CycleException success) {
            assertNotNull(success.getMessage());
        }
        
    } 

    
    public void testCycle() {  
        
        Element a = new Element("test");
        Element b = new Element("test");
        try {
            a.appendChild(b);
            b.appendChild(a);
            fail("Allowed cycle");
        }
        catch (CycleException success) {
            assertNotNull(success.getMessage());
        }
        
    } 

    public void testInsertChild() {
        
        Element parent = new Element("parent");
        
        // Test insert into empty element
        Element child1 = new Element("child");
        parent.insertChild(child1, 0);
        assertTrue(parent.getChildCount() > 0);
        assertEquals(0, parent.indexOf(child1));
        
        // Test insert at beginning
        Element child2 = new Element("child2");
        parent.insertChild(child2, 0);
        assertEquals(0, parent.indexOf(child2));
        assertEquals(1, parent.indexOf(child1));
        
        // Test insert in middle
        Element child3 = new Element("child3");
        parent.insertChild(child3, 1);
        assertEquals(0, parent.indexOf(child2));
        assertEquals(1, parent.indexOf(child3));
        assertEquals(2, parent.indexOf(child1));
        
        // Test insert at beginning with children
        Element child4 = new Element("child4");
        parent.insertChild(child4, 0);
        assertEquals(0, parent.indexOf(child4));
        assertEquals(1, parent.indexOf(child2));
        assertEquals(2, parent.indexOf(child3));
        assertEquals(3, parent.indexOf(child1));
        
        // Test insert at end with children
        Element child5 = new Element("child5");
        parent.insertChild(child5, 4);
        assertEquals(0, parent.indexOf(child4));
        assertEquals(1, parent.indexOf(child2));
        assertEquals(2, parent.indexOf(child3));
        assertEquals(3, parent.indexOf(child1));
        assertEquals(4, parent.indexOf(child5));   
        
        try {
            parent.insertChild((Element) null, 0);
            fail("Inserted null");
        }
        catch (NullPointerException success) {
            assertNotNull(success.getMessage());
        }

        try {
            parent.insertChild((Text) null, 0);
            fail("Inserted null");
        }
        catch (NullPointerException success) {
            assertNotNull(success.getMessage());
        }

        try {
            parent.insertChild((Comment) null, 0);
            fail("Inserted null");
        }
        catch (NullPointerException success) {
            assertNotNull(success.getMessage());
        }

        try {
            parent.insertChild((ProcessingInstruction) null, 0);
            fail("Inserted null");
        }
        catch (NullPointerException success) {
            assertNotNull(success.getMessage());
        }

    } 

    
    public void testAppendChild2() {
        
        try {
            empty.appendChild(new Document(notEmpty));
            fail("appended a document to an element");
        }
        catch (IllegalAddException success) {
            assertNotNull(success.getMessage());
        }
        
        try {
            empty.appendChild(child);
            fail("appended a child twice");
        }
        catch (MultipleParentException success) {
            assertNotNull(success.getMessage());
        }

    } 

    
    public void testReplaceChild() {
        
        Element old1 = new Element("old1");
        Element old2 = new Element("old2");
        Element old3 = new Element("old3");
        Element new1 = new Element("new1");
        Element new2 = new Element("new2");
        Element new3 = new Element("new3");
        
        empty.appendChild(old1);
        empty.appendChild(old2);
        empty.appendChild(old3);
        
        empty.replaceChild(old1, new1);
        empty.replaceChild(old3, new3);
        empty.replaceChild(old2, new2);

        Node current1 = empty.getChild(0);
        Node current2 = empty.getChild(1);
        Node current3 = empty.getChild(2);
        
        assertEquals(new1, current1);
        assertEquals(new2, current2);
        assertEquals(new3, current3);
        
        try {
            empty.replaceChild(new1, null);
        }
        catch (NullPointerException success) {
            assertNotNull(success.getMessage());
        }
        
        try {
            empty.replaceChild(null, old1);
        }
        catch (NullPointerException success) {
            assertNotNull(success.getMessage());
        }
        
        Element new4 = new Element("new4");
        
        try {
            empty.replaceChild(new4, new Element("test"));
            fail("Replaced Nonexistent element");     
        }
        catch (NoSuchChildException success) {
            assertNotNull(success.getMessage());
        }

        // Test replacing node with itself
        empty.replaceChild(new1, new1);
        assertEquals(new1, empty.getChild(0));        
        assertEquals(empty, new1.getParent());        

        // Test replacing node with a sibling
        try {
            empty.replaceChild(new1, new2);
            fail("replaced a node with its sibling");
        }
        catch (MultipleParentException success) {
            assertNotNull(success.getMessage());
        }
        
        
    } 

    
    public void testIndexOf() {
        
        Element child1 = new Element("old1");
        Text child2 = new Text("old2");
        Comment child3 = new Comment("old3");
        
        assertEquals(-1, empty.indexOf(child1));        
        
        empty.appendChild(child1);
        empty.appendChild(child2);
        empty.appendChild(child3);
        
        assertEquals(0, empty.indexOf(child1));
        assertEquals(1, empty.indexOf(child2));
        assertEquals(2, empty.indexOf(child3));
        assertEquals(-1, empty.indexOf(empty));
        assertEquals(-1, empty.indexOf(new Text("test")));

    } 

    
    public void testGetChild() {
        
        Element old1 = new Element("old1");
        Element old2 = new Element("old2");
        Element old3 = new Element("old3");

        try {
            empty.getChild(0);
            fail("No index exception");   
        }
        catch (IndexOutOfBoundsException success) {
            // success
            assertNotNull(success.getMessage());
        }
        
        empty.appendChild(old1);
        empty.appendChild(old2);
        empty.appendChild(old3);
        
        assertEquals(old1, empty.getChild(0));
        assertEquals(old3, empty.getChild(2));
        assertEquals(old2, empty.getChild(1));

        try {
            empty.getChild(5);
            fail("No index exception");   
        }
        catch (IndexOutOfBoundsException success) {
            // success
            assertNotNull(success.getMessage());
        }

    } 

    
    public void testRemoveChild() {
        
        try {
            empty.removeChild(0);
            fail("Removed child from empty element");   
        }
        catch (IndexOutOfBoundsException success) {
            assertNotNull(success.getMessage());
        }

        Element old1 = new Element("old1");
        Element old2 = new Element("old2");
        Element old3 = new Element("old3");

        try {
            empty.removeChild(old1);
            fail("Removed non-existent child from empty element");   
        }
        catch (NoSuchChildException success) {
            assertNotNull(success.getMessage());
        }

        empty.appendChild(old1);
        empty.appendChild(old2);
        empty.appendChild(old3);
        
        empty.removeChild(1);
        assertEquals(old1, empty.getChild(0));
        assertEquals(old3, empty.getChild(1));

        try {
            empty.removeChild(5);
            fail("No IndexOutOfBoundsException");   
        }
        catch (IndexOutOfBoundsException success) {
            assertNotNull(success.getMessage());
        }
        
        empty.removeChild(1);
        empty.removeChild(0);
        assertNull(old2.getParent());
        
        assertEquals(0, empty.getChildCount());

        empty.appendChild(old1);
        empty.appendChild(old2);
        empty.appendChild(old3);
        
        assertEquals(3, empty.getChildCount());

        empty.removeChild(old3);
        empty.removeChild(old1);
        empty.removeChild(old2);
        
        assertEquals(0, empty.getChildCount());
        assertNull(old1.getParent());   

    } 


    public void testReplaceChildFailures() {
        
        Element old1 = new Element("old1");
        Element old2 = new Element("old2");
        Element old3 = new Element("old3");
        Element new1 = new Element("new1");
        Element new3 = new Element("new3");
        
        empty.appendChild(old1);
        empty.appendChild(old2);
        
        try {
            empty.replaceChild(old3, new3);
            fail("Replaced non-existent child");
        }
        catch (NoSuchChildException success) {
            assertNotNull(success.getMessage());
        }

        try {
            empty.replaceChild(old1, null);
            fail("Replaced child with null");
        }
        catch (NullPointerException success) {
            assertNotNull(success.getMessage());
        }

        try {
            empty.replaceChild(null, new1);
            fail("Replaced null");
        }
        catch (NullPointerException success) {
            assertNotNull(success.getMessage());
        }

    } 
    
    
    public void testReplaceChildInEmptyParent() {
        
        Element test1 = new Element("test");
        Element test2 = new Element("test");
        try {
            empty.replaceChild(test1, test2);
            fail("Replaced element in empty parent");
        }   
        catch (NoSuchChildException success) {
            assertNotNull(success.getMessage());   
        } 
        
    }

    
    // Document that this behavior is intentional
    // An element cannot be replaced by its sibling unless
    // the sibling is first detached. 
    public void testReplaceSibling() {
        
        Element parent = new Element("parent");
        Element test1 = new Element("test");
        Element test2 = new Element("test");
        parent.appendChild(test1);
        parent.appendChild(test2);
        try {
            parent.replaceChild(test1, test2);
            fail("Replaced element without detaching first");
        }   
        catch (IllegalAddException success) {
            assertNotNull(success.getMessage());   
        } 
        
        assertEquals(2, parent.getChildCount());
        assertEquals(parent, test1.getParent());
        assertEquals(parent, test2.getParent());
        
    }

    
    // Similarly, this test documents the conscious decision
    // that you cannot insert an existing child into its own parent,
    // even at the same position
    public void testCantInsertExisitngChild() {
        
        Element parent = new Element("parent");
        Element test1 = new Element("test");
        Element test2 = new Element("test");
        parent.appendChild(test1);
        parent.appendChild(test2);
        try {
            parent.insertChild(test2, 0);
            fail("Inserted element without detaching first");
        }   
        catch (MultipleParentException success) {
            assertNotNull(success.getMessage());   
        } 
        
        try {
            parent.insertChild(test2, 1);
            fail("Inserted element without detaching first");
        }   
        catch (MultipleParentException success) {
            assertNotNull(success.getMessage());   
        } 
        
        assertEquals(2, parent.getChildCount());
        assertEquals(parent, test1.getParent());
        assertEquals(parent, test2.getParent());
        
    }

    
    // can't remove when insertion is legal;
    // succeed or fail as unit
    public void testReplaceChildAtomicity() {
        
        Element parent = new Element("parent");
        Text child = new Text("child");
        parent.appendChild(child);
        
        try {
            parent.replaceChild(child, new DocType("root"));
            fail("allowed doctype child of element");
        }
        catch (IllegalAddException success) {
            assertEquals(parent, child.getParent());
            assertEquals(1, parent.getChildCount());
        }
        
        Element e = new Element("e");
        Text child2 = new Text("child2");
        e.appendChild(child2);
        try {
            parent.replaceChild(child, child2);
            fail("allowed replacement with existing parent");
        }
        catch (MultipleParentException success) {
            assertEquals(e, child2.getParent());
            assertEquals(parent, child.getParent());
            assertEquals(1, parent.getChildCount());
            assertEquals(1, e.getChildCount());
        }
        
    }
    
    
}
