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
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.IllegalDataException;

/**
 * <p>
 *  Various basic unit tests for the <code>Comment</code> class.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d23
 *
 */
public class CommentTest extends XOMTestCase {

    public CommentTest() {
        super("Comment tests");
    }

    public CommentTest(String name) {
        super(name);
    }

    public void testConstructor() {
         Comment c1 = new Comment("test");
         assertEquals("test", c1.getValue());
         Comment c2 = new Comment("");
         assertEquals("", c2.getValue());
     }

    public void testToString() {
        
        Comment c1 = new Comment("content");
        assertEquals("[nu.xom.Comment: content]", c1.toString());          
        
        c1.setValue("012345678901234567890123456789012345678901234567890123456789");
        assertEquals(
          "[nu.xom.Comment: 01234567890123456789012345678901234...]", 
          c1.toString()
        ); 
           
    }
    
    public void testToXML() {
        
        Comment c1 = new Comment("content");
        assertEquals("<!--content-->", c1.toXML());          
        
        c1.setValue(" 012345678901234567890123456789012345678901234567890123456789 ");
        assertEquals(
          "<!-- 012345678901234567890123456789012345678901234567890123456789 -->", 
          c1.toXML()
        ); 
           
    }
    
    // This is a problem becuase it cannot be serialized
    // since character and entity references aren't
    // recognized in comment data
    public void testCarriageReturnInCommentData() {
        try {
            Comment c = new Comment("data\rdata");
            fail("Allowed carriage return in comment");
        }
        catch (IllegalDataException ex) {
            assertNotNull(ex.getMessage());   
        }   
    }
    
    public void testSetter() {

        Comment c1 = new Comment("test");
        c1.setValue("legal");
        assertEquals("legal", c1.getValue());
        
        try {
          c1.setValue("test -- test");
          fail("Should raise an IllegalDataException");
        }
        catch (IllegalDataException ex) {
            assertNotNull(ex.getMessage());   
        }   
        try {
          c1.setValue("-test");
          fail("Should raise an IllegalDataException");
        }
        catch (IllegalDataException ex) {
            assertNotNull(ex.getMessage());   
        }   
        try {
          c1.setValue("test-");
          fail("Should raise an IllegalDataException");
        }
        catch (IllegalDataException ex) {
            assertNotNull(ex.getMessage());   
        }   

        c1.setValue(null);
        assertEquals("", c1.getValue());

     }


    public void testEquals() {
        Comment c1 = new Comment("test");
        Comment c2 = new Comment("test");
        Comment c3 = new Comment("skjlchsakdjh");

        assertEquals(c1, c1);
        assertEquals(c1.hashCode(), c1.hashCode());
        assertTrue(!c1.equals(c2));
        assertTrue(!c1.equals(c3));
    }

    public void testCopy() {
        Comment c1 = new Comment("test");
        Comment c2 = (Comment) c1.copy();

        assertEquals(c1.getValue(), c2.getValue());
        assertTrue(!c1.equals(c2));
        assertNull(c2.getParent());

    }

    // Check passing in a string with broken surrogate pairs
    // and with correct surrogate pairs
    public void testSurrogates() {

        String goodString = "test: \uD8F5\uDF80  ";
        Comment c = new Comment(goodString);
        assertEquals(goodString, c.getValue());

        // Two high-halves
        try {
          new Comment("test: \uD8F5\uDBF0  ");
          fail("Should raise an IllegalDataException");
        }
        catch (IllegalDataException ex) {
            assertNotNull(ex.getMessage());   
        }   


        // Two high-halves
        try {
          new Comment("test: \uD8F5\uD8F5  ");
          fail("Should raise an IllegalDataException");
        }
        catch (IllegalDataException ex) {
            assertNotNull(ex.getMessage());   
        }   

        // One high-half
        try {
           new Comment("test: \uD8F5  ");
           fail("Should raise an IllegalDataException");
        }
        catch (IllegalDataException ex) {
            assertNotNull(ex.getMessage());   
        }   

        // One low half
        try {
            new Comment("test: \uDF80  ");
            fail("Should raise an IllegalDataException");
         }
        catch (IllegalDataException ex) {
            assertNotNull(ex.getMessage());   
        }   

        // Low half before high half
         try {
            new Comment("test: \uDCF5\uD8F5  ");
            fail("Should raise an IllegalDataException");
         }
        catch (IllegalDataException ex) {
            assertNotNull(ex.getMessage());   
        }   


    }

    public void testLeafNode() {

        Comment c1 = new Comment("data");
        assertEquals(0, c1.getChildCount());
        assertTrue(!c1.hasChildren());
        try {
            c1.getChild(0);
            fail("Didn't throw IndexOutofBoundsException");
        }
        catch (IndexOutOfBoundsException ex) {
            // success  
            assertNotNull(ex.getMessage()); 
        }
        
        assertNull(c1.getParent());

        Element element = new Element("test");
        element.appendChild(c1);
        assertEquals(element, c1.getParent());
        assertEquals(element.getChild(0), c1);

        element.removeChild(c1);
        assertTrue(!element.hasChildren());

    }

    public void testGetDocument() {

        Comment c1 = new Comment("data");
        assertNull(c1.getDocument());
        Element root = new Element("root");
        root.appendChild(c1);
        assertNull(c1.getDocument());
        Document doc = new Document(root);
        assertEquals(doc, c1.getDocument());

    }

    public void testAllowReservedCharactersInData() {
        Comment comment = new Comment("<test>&amp;&greater;");
        String xml = comment.toXML();
        assertEquals("<!--<test>&amp;&greater;-->", xml);  
    }

}
