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

import nu.xom.Comment;
import nu.xom.CycleException;
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
 * @version 1.0d22
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

    
    public void testHasChildren() {       
        assertTrue(notEmpty.hasChildren());
        assertTrue(!empty.hasChildren());       
    } 

    public void testAppendChild() {      
        Element child = new Element("test");
        empty.appendChild(child);
        assertTrue(empty.hasChildren());
        assertEquals(empty.getChild(0), child);
        child.detach();
        
        notEmpty.appendChild(child);
        assertTrue(!notEmpty.getChild(0).equals(child));
        assertTrue(notEmpty.getChild(1).equals(child));
    } 

    public void testAppendChildToItself() {      
        Element child = new Element("test");
        try {
            child.appendChild(child);
            fail("Appended node to itself");
        }
        catch (CycleException ex) {
            // success   
            assertNotNull(ex.getMessage());
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
        catch (CycleException ex) {
            // success   
            assertNotNull(ex.getMessage());
        }
    } 

    public void testInsertChild() {
        
        Element parent = new Element("parent");
        
        // Test insert into empty element
        Element child1 = new Element("child");
        parent.insertChild(child1, 0);
        assertTrue(parent.hasChildren());
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
        catch (NullPointerException ex) {
            // success
        }

        try {
            parent.insertChild((Text) null, 0);
            fail("Inserted null");
        }
        catch (NullPointerException ex) {
            // success
        }

        try {
            parent.insertChild((Comment) null, 0);
            fail("Inserted null");
        }
        catch (NullPointerException ex) {
            // success
        }

        try {
            parent.insertChild((ProcessingInstruction) null, 0);
            fail("Inserted null");
        }
        catch (NullPointerException ex) {
            // success
        }

    } 

    public void testAppendChild2() {
        
        try {
            empty.appendChild(new Document(notEmpty));
            fail("appended a document to an element");
        }
        catch (IllegalAddException ex) {
            // success   
            assertNotNull(ex.getMessage());
        }
        
        try {
            empty.appendChild(child);
            fail("appended a child twice");
        }
        catch (MultipleParentException ex) {
            // success   
            assertNotNull(ex.getMessage());
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
        
        Element new4 = new Element("new4");
        try {
            empty.replaceChild(new4, new Element("test"));
            fail("Replaced Nonexistent element");     
        }
        catch (NoSuchChildException ex) {
            // success   
            assertNotNull(ex.getMessage());
        }

        // Test replacing node with itself
        empty.replaceChild(new1, new1);
        assertEquals(new1, empty.getChild(0));        

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
        catch (IndexOutOfBoundsException ex) {
            // success
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
        catch (IndexOutOfBoundsException ex) {
            // success
        }

    } 

   public void testRemoveChild() {
        
        try {
            empty.removeChild(0);
            fail("Removed child from empty element");   
        }
        catch (IndexOutOfBoundsException ex) {
            // success
        }
        

        Element old1 = new Element("old1");
        Element old2 = new Element("old2");
        Element old3 = new Element("old3");

        try {
            empty.removeChild(old1);
            fail("Removed non-existent child from empty element");   
        }
        catch (NoSuchChildException ex) {
            // success   
            assertNotNull(ex.getMessage());
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
        catch (IndexOutOfBoundsException ex) {
            // success
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
        catch (NoSuchChildException ex) {
            // success   
            assertNotNull(ex.getMessage());
        }

        try {
            empty.replaceChild(old1, null);
            fail("Replaced child with null");
        }
        catch (NullPointerException ex) {
            // success   
        }

        try {
            empty.replaceChild(null, new1);
            fail("Replaced null");
        }
        catch (NoSuchChildException ex) {
            // success   
            assertNotNull(ex.getMessage());
        }

    } 

}
