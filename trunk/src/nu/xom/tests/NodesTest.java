// Copyright 2003 Elliotte Rusty Harold
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
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.Text;

/**
 * <p>
 *  Various basic tests for the <code>Nodes</code> class.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d23
 *
 */
public class NodesTest extends XOMTestCase {

    public NodesTest(String name) {
        super(name);
    }
    
    public void testNoArgsConstructor() {
        Nodes nodes = new Nodes();
        assertEquals(0, nodes.size());   
    }

    public void testOneArgConstructor() {
        Element test = new Element("test");
        Nodes nodes = new Nodes(test);
        assertEquals(1, nodes.size()); 
        Element stored = (Element) nodes.get(0);
        assertEquals(test, stored);  
    }
    
    public void testIndexOutofBoundsException() {
        Nodes nodes = new Nodes();
        try {
            Node stored = nodes.get(0);
            fail("Didn't throw IndexOutOfBoundsException for empty list");
        }
        catch (IndexOutOfBoundsException success) {
            assertNotNull(success.getMessage());   
        }  
        
        nodes.append(new Comment("data"));
        try {
            Node stored = nodes.get(-1);
            fail("Didn't throw IndexOutOfBoundsException for -1");
        }
        catch (IndexOutOfBoundsException success) {
            assertNotNull(success.getMessage());   
        }  
        try {
            Node stored = nodes.get(1);
            fail("Didn't throw IndexOutOfBoundsException for fencepost");
        }
        catch (IndexOutOfBoundsException success) {
            assertNotNull(success.getMessage());   
        }   
        
    }

    public void testAppendAndGet() {
        Nodes nodes = new Nodes();
        int length = 10;
        for (int i = 0; i < length; i++) {
            nodes.append(new Text(String.valueOf(i)));   
        }
        assertEquals(length, nodes.size());
        for (int i = 0; i < length; i++) {
            assertEquals(String.valueOf(i), nodes.get(i).getValue());   
        }     
    }

    public void testInsert() {
        Nodes nodes = new Nodes();
        int length = 10;
        for (int i = 0; i < length; i++) {
            nodes.append(new Text(String.valueOf(i)));   
        }
        nodes.insert(new Comment("dTA"), 3);
        nodes.insert(new Comment("dTA"), 5);
        nodes.insert(new Comment("dTA"), 12);
        assertEquals(length+3, nodes.size());
        for (int i = 0; i < 3; i++) {
            assertEquals(String.valueOf(i), nodes.get(i).getValue());   
        }     
        assertEquals("dTA", nodes.get(3).getValue());
        assertEquals("dTA", nodes.get(5).getValue());
        assertEquals("dTA", nodes.get(12).getValue());
        for (int i = 6; i < length+2; i++) {
            assertEquals(String.valueOf(i-2), nodes.get(i).getValue());   
        } 
        
        try {
            nodes.insert(new Text("data"), 14);   
        }
        catch (IndexOutOfBoundsException ex) {
            assertNotNull(ex.getMessage());
        }
                 
        try {
            nodes.insert(new Text("data"), 140);   
        }
        catch (IndexOutOfBoundsException ex) {
            assertNotNull(ex.getMessage());
        }
                 
        try {
            nodes.insert(new Text("data"), -14);   
        }
        catch (IndexOutOfBoundsException ex) {
            assertNotNull(ex.getMessage());
        }
                 
    }
    
    public void testDelete() {
        
        Nodes nodes = new Nodes();
        int length = 10;
        for (int i = 0; i < length; i++) {
            nodes.append(new Text(String.valueOf(i)));   
        }     
        
        nodes.remove(0);
        assertEquals(length-1, nodes.size());
        for (int i = 0; i < nodes.size(); i++) {
            assertEquals(String.valueOf(i+1), nodes.get(i).getValue());   
        }  
        nodes.remove(nodes.size()-1);
        assertEquals(length-2, nodes.size());
        for (int i = 0; i < nodes.size(); i++) {
            assertEquals(String.valueOf(i+1), nodes.get(i).getValue());   
        }
        nodes.remove(2); 
        for (int i = 0; i < 2; i++) {
            assertEquals(String.valueOf(i+1), nodes.get(i).getValue());   
        }        
        for (int i = 2; i < nodes.size(); i++) {
            assertEquals(String.valueOf(i+2), nodes.get(i).getValue());   
        }
        assertEquals(length-3, nodes.size());        
        
        try {
            nodes.remove(14);   
        }
        catch (IndexOutOfBoundsException ex) {
            assertNotNull(ex.getMessage());
        }
                 
        try {
            nodes.remove(nodes.size());   
        }
        catch (IndexOutOfBoundsException ex) {
            assertNotNull(ex.getMessage());
        }
                 
        try {
            nodes.remove(-14);   
        }
        catch (IndexOutOfBoundsException ex) {
            assertNotNull(ex.getMessage());
        }
          
    }
    
}
