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

import java.util.HashMap;
import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.Text;
import nu.xom.XPathException;

/**
 * <p>
 * Unit tests for XPath functionality
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.1d2
 *
 */
public class XPathTest extends XOMTestCase {

    public XPathTest(String name) {
        super(name);
    }
    
    public void testSimpleQuery() {
        
        Element parent = new Element("Test");
        Element child = new Element("child");
        parent.appendChild(child);
        
        Nodes result = parent.query("*");
        assertEquals(1, result.size());
        assertEquals(child, result.get(0));   
        
    }
    

    public void testParentAxis() {
        
        Element parent = new Element("Test");
        Element child = new Element("child");
        parent.appendChild(child);
        
        Nodes result = child.query("parent::*");
        assertEquals(1, result.size());
        assertEquals(parent, result.get(0));   
        
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
    

    public void testSelfAxisWithTextChild() {
        
        Element parent = new Element("parent");
        Node child = new Text("child");
        parent.appendChild(child);
        Nodes result = child.query("self::text()");
        assertEquals(1, result.size());
        assertEquals(child, result.get(0));
        
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
        
        Map namespaces = new HashMap();
        namespaces.put("pre", "http://www.example.org");
        Nodes result = parent.query("child::pre:child", namespaces);
        assertEquals(1, result.size());
        assertEquals(child, result.get(0));   
        
    }
    

    public void testNamespaceQueryWithAdjacentTextNodes() {
        
        Element parent = new Element("Test", "http://www.example.org");
        Element child = new Element("child", "http://www.example.org");
        parent.appendChild(child);
        child.appendChild("1");
        child.appendChild("2");
        
        Map namespaces = new HashMap();
        namespaces.put("pre", "http://www.example.org");
        Nodes result = parent.query("descendant::text()", namespaces);
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
    

    public void testAdjacentTextNodes() {
        
        Element parent = new Element("Test");
        parent.appendChild("test");
        parent.appendChild("again");
        
        Nodes result = parent.query("text()");
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
    

}
