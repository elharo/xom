/* Copyright 2003-2005 Elliotte Rusty Harold
   
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

import nu.xom.xslt.XSLException;

/**
 * <p>
 *   Unit tests for the <code>XSLException</code> class.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.1d2
 *
 */
public class XSLExceptionTest extends XOMTestCase {
    
    
    private XSLException ex;
    private Exception cause;
    
    
    public XSLExceptionTest(String name) {
        super(name);   
    }

    
    protected void setUp() {
        ex = new XSLException("message");
        cause = new Exception();
    }
    
    
    public void testConstructor() {
        String message = "testing 1-2-3";
        XSLException ex = new XSLException(message, cause);
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
        
        XSLException ex = new XSLException(null, null);
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
        Exception ex = new XSLException("testing");
        assertEquals("testing", ex.getMessage());
    }

}
