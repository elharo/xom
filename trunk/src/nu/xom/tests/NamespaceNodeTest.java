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

import nu.xom.Element;
import nu.xom.Namespace;
import nu.xom.Nodes;

public class NamespaceNodeTest extends XOMTestCase {

    
    public NamespaceNodeTest(String name) {
        super(name);
    }
    
    
    public void testCopy() {
     
        Element root = new Element("pre:root", "http://www.example.org/");
        Nodes result = root.query("namespace::pre");
        assertEquals(1, result.size());
        Namespace namespace = (Namespace) result.get(0);
        assertEquals("pre", namespace.getPrefix());
        assertEquals("http://www.example.org/", namespace.getValue());
        assertEquals(root, namespace.getParent());
        
        Namespace copy = (Namespace) namespace.copy();
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

    
    public void testToString() {
     
        Element root = new Element("pre:root", "http://www.example.org/");
        Nodes result = root.query("namespace::pre");
        Namespace namespace = (Namespace) result.get(0);
        assertEquals("[Namespace: xmlns:pre=\"http://www.example.org/\"]", namespace.toString());
        
    }

    
}
