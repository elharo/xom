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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import nu.xom.Element;
import nu.xom.XPathException;
import nu.xom.XPathTypeException;

/**
 * <p>
 * Unit tests for the <code>XPathException</code> class.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.1b4
 *
 */
public class XPathExceptionTest extends XOMTestCase {
    
    
    private XPathException ex;
    private Exception cause;
    
    
    public XPathExceptionTest(String name) {
        super(name);   
    }

    
    protected void setUp() {
        ex = new XPathException("message");
        cause = new Exception();
    }
    
    
    public void testConstructor() {
        String message = "testing 1-2-3";
        XPathException ex = new XPathException(message, cause);
        assertEquals(message, ex.getMessage());
        assertEquals(cause, ex.getCause()); 
    }

    
    public void testInitCause() {
        
        assertNull(ex.getCause());
        ex.initCause(cause);
        assertEquals(cause, ex.getCause());
        
        try {
            ex.initCause(null);   
            fail("Reinitialized cause over null");   
        }
        catch (IllegalStateException success) {
            assertNotNull(success.getMessage()); 
        }
        
        try {
            ex.initCause(new Exception());   
            fail("Reinitialized cause over null");   
        }
        catch (IllegalStateException success) {
            assertNotNull(success.getMessage()); 
        }
        
    }


    public void testNullInitCause() {
        
        XPathException ex = new XPathException(null, null);
        assertNull(ex.getCause());
        
        try {
            ex.initCause(new Exception());
            fail("Reinitialized cause over null");   
        }
        catch (IllegalStateException success) {
            assertNotNull(success.getMessage());   
        }

        try {
            ex.initCause(null);   
            fail("Reinitialized cause over null");   
        }
        catch (IllegalStateException success) {
            assertNotNull(success.getMessage());   
        }
        
    }

    
    public void testSelfCause() {
        
        try {
            ex.initCause(ex);   
            fail("Allowed self-causation");   
        }
        catch (IllegalArgumentException success) {
            assertNotNull(success.getMessage());   
        }
        
    }

    
    public void testGetMessage() {      
        Exception ex = new XPathException("testing");
        assertEquals("testing", ex.getMessage());
    }

    public void testGetXPathExpression() {
        
        Element parent = new Element("Test");
        
        try {
            parent.query("This is not an XPath expression");
            fail("Allowed malformed query");
        }
        catch (XPathException success) {
            assertEquals(
              "This is not an XPath expression", success.getXPath());
        }  
        
    }
    
    public void testSerializeXPathTypeException() throws IOException {
        
        Element parent = new Element("Test");
        Element child = new Element("child");
        parent.appendChild(child);
        
        try {
            parent.query("count(*)");
            fail("Allowed query to return number");
        }
        catch (XPathTypeException success) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream oout = new ObjectOutputStream(out);
            oout.writeObject(success);
            oout.close();
        }
        
    }
    

    
}
