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
   elharo@ibiblio.org. Please include the word "XOM" in the
   subject line. The XOM home page is located at https://xom.nu/
*/
package nu.xom.tests;

import nu.xom.Element;
import nu.xom.IllegalNameException;
import nu.xom.MalformedURIException;
import nu.xom.Namespace;
import nu.xom.NamespaceConflictException;
import nu.xom.NoSuchChildException;
import nu.xom.Nodes;

/**
 * <p>
 * Tests for namespace nodes used in XPath
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.3.0
 *
 */
public class NamespaceNodeTest extends XOMTestCase {

    
    public NamespaceNodeTest(String name) {
        super(name);
    }
    
    
    public void testGetters() {
     
        Element root = new Element("pre:root", "http://www.example.org/");
        Nodes result = root.query("namespace::pre");
        assertEquals(1, result.size());
        Namespace namespace = (Namespace) result.get(0);
        assertEquals("pre", namespace.getPrefix());
        assertEquals("http://www.example.org/", namespace.getValue());
        assertEquals(root, namespace.getParent());
        
    }

    
    public void testCopy() {
     
        Element root = new Element("pre:root", "http://www.example.org/");
        Nodes result = root.query("namespace::pre");
        assertEquals(1, result.size());
        Namespace namespace = (Namespace) result.get(0);
        
        Namespace copy = namespace.copy();
        assertEquals(namespace, copy);
        assertEquals("pre", copy.getPrefix());
        assertEquals("http://www.example.org/", copy.getValue());
        assertEquals(null, copy.getParent());
        
    }

    
    public void testToXML() {
     
        Element root = new Element("pre:root", "http://www.example.org/");
        Nodes result = root.query("namespace::pre");
        Namespace namespace = (Namespace) result.get(0);
        assertEquals("xmlns:pre=\"http://www.example.org/\"", namespace.toXML());
        
    }

    
    public void testGetChildCount() {
     
        Element root = new Element("pre:root", "http://www.example.org/");
        Nodes result = root.query("namespace::pre");
        Namespace namespace = (Namespace) result.get(0);
        assertEquals(0, namespace.getChildCount());
        
    }

    
    public void testGetChild() {
     
        Element root = new Element("pre:root", "http://www.example.org/");
        Nodes result = root.query("namespace::pre");
        Namespace namespace = (Namespace) result.get(0);
        try {
            namespace.getChild(0);
            fail("Got namespace child");
        }
        catch (IndexOutOfBoundsException success) {
            assertEquals("Namespaces do not have children", success.getMessage());
        }
        
    }

    
    public void testToXMLOnDefaultNamespace() {
     
        Element root = new Element("root", "http://www.example.org/");
        Nodes result = root.query("namespace::*[name() != 'xml']");
        Namespace namespace = (Namespace) result.get(0);
        assertEquals("xmlns=\"http://www.example.org/\"", namespace.toXML());
        
    }

    
    public void testDetachNamespaceNode() {
     
        Element root = new Element("pre:root", "http://www.example.org/");
        Nodes result = root.query("namespace::pre");
        Namespace namespace = (Namespace) result.get(0);
        namespace.detach();
        assertNull(namespace.getParent());
        
    }

    
    public void testRemoveNamespaceNode() {
     
        Element root = new Element("pre:root", "http://www.example.org/");
        Nodes result = root.query("namespace::pre");
        Namespace namespace = (Namespace) result.get(0);
        try {
            root.removeChild(namespace);
            fail("Namespaces are not children");
        }
        catch (NoSuchChildException success) {
            assertNotNull(success.getMessage());
        }
        
    }

    
    public void testGetParent() {
     
        Element root = new Element("pre:root", "http://www.example.org/");
        Nodes result = root.query("namespace::pre");
        Namespace namespace = (Namespace) result.get(0);
        assertEquals(root, namespace.getParent());
        
    }

    
    public void testToString() {
     
        Element root = new Element("pre:root", "http://www.example.org/");
        Nodes result = root.query("namespace::pre");
        Namespace namespace = (Namespace) result.get(0);
        assertEquals("[Namespace: xmlns:pre=\"http://www.example.org/\"]", namespace.toString());
        
    }
    
    
    public void testIllegalPrefix() {
     
        try {
            new Namespace("white space", "http://www.example.org", null);
            fail("Allowed prefix containing white space");
        }
        catch (IllegalNameException success) {
            assertNotNull(success.getMessage());
        }
        
    }

    
    public void testEmptyStringPrefix() {
        Namespace ns = new Namespace("", "http://www.example.org", null);
        assertEquals("", ns.getPrefix());
    }

    
    public void testNullPrefix() {
        Namespace ns = new Namespace(null, "http://www.example.org", null);
        assertEquals("", ns.getPrefix());
    }

    
    public void testIllegalURI() {
     
        try {
            new Namespace("pre", "http:// www.example.org", null);
            fail("Allowed URI containing white space");
        }
        catch (MalformedURIException success) {
            assertNotNull(success.getMessage());
        }
        
    }

    
    public void testNullURI() {
        Namespace ns = new Namespace("", null, null);
        assertEquals("", ns.getValue());
    }

    
    public void testCantBindPrefixToEmptyURI() {
        
        try {
            new Namespace("pre", "", null);
            fail("Bound prefix to no namespace");
        }
        catch (NamespaceConflictException success) {
            assertNotNull(success.getMessage());
        }
        
    }

    
    public void testCantBindXMLNS() {
        
        try {
            new Namespace("xmlns", "", null);
            fail("Bound xmlns prefix to no namespace");
        }
        catch (IllegalNameException success) {
            assertNotNull(success.getMessage());
        }
        
    }

    
    public void testCantBindXMLNSToDOMURI() {
        
        try {
            new Namespace("xmlns", "http://www.w3.org/2000/xmlns/", null);
            fail("Bound xmlns prefix to DOM namespace");
        }
        catch (IllegalNameException success) {
            assertNotNull(success.getMessage());
        }
        
    }

    
    public void testCantBindXMLPrefixToWrongURI() {
        
        try {
            new Namespace("xml", "http://www.w3.org/2000/xmlns/", null);
            fail("Bound xml prefix to DOM namespace");
        }
        catch (NamespaceConflictException success) {
            assertNotNull(success.getMessage());
        }
        
    }

    
    public void testCanBindXMLPrefixToCorrectURI() {
        
        Namespace ns = new Namespace("xml", Namespace.XML_NAMESPACE, null);
        assertEquals(Namespace.XML_NAMESPACE, ns.getValue());
        
    }

    
    public void testCanBindNonXMLPrefixToXMLURI() {
        
        try {
            new Namespace("pre", Namespace.XML_NAMESPACE, null);
            fail("Bound non-xml prefix to XML namespace");
        }
        catch (NamespaceConflictException success) {
            assertNotNull(success.getMessage());
        }
        
    }

    
}
