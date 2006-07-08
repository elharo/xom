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

import junit.framework.ComparisonFailure;
import nu.xom.Attribute;
import nu.xom.Comment;
import nu.xom.Element;
import nu.xom.Namespace;
import nu.xom.Node;
import nu.xom.Text;

/**
 * <p>
 * Unit tests for XOMTestCase. Added after the first bug discovered in
 * XOM 1.0 showed up in XOMTestCase. 
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.1a2
 *
 */
public class XOMTestCaseTest extends XOMTestCase {

    
    public XOMTestCaseTest(String name) {
        super(name);
    }

    
    public void testNullCheck() {
     
        Text t = new Text("");
        try {
            assertEquals(t, null);
            fail("Allowed comparison with null");
        }
        catch (ComparisonFailure ex) {
            assertNotNull(ex.getMessage());
        }
        
        try {
            assertEquals(null, t);
            fail("Allowed comparison with null");
        }
        catch (ComparisonFailure ex) {
            assertNotNull(ex.getMessage());
        }
        
    }
    
    
    public void testNamespaceEqualsItself() {
        Namespace ns = new Namespace("pre", "http://www.example.org", null);
        assertEquals(ns, ns);
    }
    
    
    public void testCompareMismatchedTypes() {
     
        Node n1 = new Text("");
        Node n2 = new Attribute("name", "value");
        
        try {
            assertEquals(n1, n2);
            fail("Text equals Attribute?!");
        }
        catch (ComparisonFailure ex) {
            assertNotNull(ex.getMessage());
        }
        
        try {
            assertEquals(n2, n1);
            fail("Text equals Attribute?!");
        }
        catch (ComparisonFailure ex) {
            assertNotNull(ex.getMessage());
        }
        
    }
    
    
    public void testCompareMismatchedNullNodeTypes() {
     
        Node n1 = new Text("");
        Node n2 = null;
        
        try {
            assertEquals(n1, n2);
            fail("Text equals null?!");
        }
        catch (ComparisonFailure ex) {
            assertNotNull(ex.getMessage());
        }
        
    }
    
    
    public void testCompareAttributesAsNodes() {
     
        Node a1 = new Attribute("test", "value");
        Node a2 = a1.copy();
        assertEquals(a1, a2);
        
    }
    
    
    public void testCombineTextNodes() {
     
        Element e1 = new Element("test");
        e1.appendChild("1");
        e1.appendChild("2");
        Element e2 = new Element("test");
        e2.appendChild("12");
        assertEquals(e1, e2);
        assertEquals(2, e1.getChildCount());
        
    }
    
    
    public void testTrickyCombineTextNodes() {
     
        Element e1 = new Element("test");
        e1.appendChild("12");
        e1.appendChild("3");
        Element e2 = new Element("test");
        e2.appendChild("1");
        e2.appendChild("23");
        assertEquals(e1, e2);
        assertEquals(2, e1.getChildCount());
        
    }
    
    
    public void testCombineThreeTextNodes() {
     
        Element e1 = new Element("test");
        e1.appendChild("1");
        e1.appendChild("2");
        e1.appendChild("3");
        Element e2 = new Element("test");
        e2.appendChild("123");
        assertEquals(e1, e2);
        
    }
    
    
    public void testCombineThreeTextNodes2() {
     
        Element e1 = new Element("test");
        e1.appendChild("\n");
        e1.appendChild(new Element("p"));
        e1.appendChild("1");
        e1.appendChild("2");
        e1.appendChild("3");
        Element e2 = new Element("test");
        e2.appendChild("\n");
        e2.appendChild(new Element("p"));
        e2.appendChild("123");
        assertEquals(e2, e1);
        
    }
    
    
    public void testUnequalElements() {
     
        Element e1 = new Element("test");
        e1.appendChild("1");
        e1.appendChild(new Element("b"));
        e1.appendChild("3");
        Element e2 = new Element("test");
        e2.appendChild("1");
        e2.appendChild(new Element("c"));
        e2.appendChild("3");
        try {
            assertEquals(e1, e2);
            fail("Unequal elements compared equal");
        }
        catch (ComparisonFailure success) {
            assertNotNull(success.getMessage());
        }
        
    }
    
    
    public void testCompareXMLBaseAttributes() {
     
        Node a1 = new Attribute("xml:base", Namespace.XML_NAMESPACE, "value.xml");
        Node a2 = new Attribute("xml:base", Namespace.XML_NAMESPACE, "./value.xml");
        assertEquals(a1, a2);
        
    }
    
    
    public void testCompareChildren() {
     
        Element e1 = new Element("e");
        Element e2 = new Element("e");
        e1.appendChild(new Comment("a"));
        e2.appendChild(new Comment("b"));
        try {
            assertEquals("BOO!", e1, e2);
            fail("didn't check children");
        }
        catch (ComparisonFailure ex) {
            assertTrue(ex.getMessage().indexOf("BOO!") >= 0 );
        }
        
    }
    
    
}
