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

import nu.xom.ValidityException;

/**
 * <p>
 *   Unit tests for the <code>ParsingException</code> class.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d23
 *
 */
public class ValidityExceptionTest extends XOMTestCase {
    
    private ValidityException ex;
    private Exception cause;
    private String message = "testing 1-2-3";
    
    public ValidityExceptionTest(String name) {
        super(name);
    }
    
    protected void setUp() {
        ex = new ValidityException("message");
        cause = new Exception();
    }

    public void testConstructor() {
        Exception ex = new ValidityException(message, cause);
        assertEquals(message, ex.getMessage());
        assertEquals(cause, ex.getCause()); 
    }
    
    public void testLineAndColumnNumbers() {
        ValidityException ex = new ValidityException(message, 10, 20);
        assertEquals(message, ex.getMessage());
        assertNull(ex.getCause());
        assertEquals(10, ex.getLineNumber()); 
        assertEquals(20, ex.getColumnNumber()); 
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
            // success   
        }
        
        try {
            ex.initCause(new Exception());   
            fail("Reinitialized cause over null");   
        }
        catch (IllegalStateException success) {
            // success   
        }
        
    }


    public void testNullInitCause() {
        
        ValidityException ex = new ValidityException(null, null);
        assertNull(ex.getCause());
        assertNull(ex.getMessage());
        assertEquals(-1, ex.getLineNumber()); 
        assertEquals(-1, ex.getColumnNumber()); 
        
        try {
            ex.initCause(new Exception());
            fail("Reinitialized cause over null");   
        }
        catch (IllegalStateException result) {
            // success   
        }

        try {
            ex.initCause(null);   
            fail("Reinitialized cause over null");   
        }
        catch (IllegalStateException result) {
            // success   
        }
        
    }

    public void testSelfCause() {
        
        try {
            ex.initCause(ex);   
            fail("Allowed self-causation");   
        }
        catch (IllegalArgumentException result) {
            // success   
        }
        
    }

    public void testGetMessage() {      
        Exception ex = new ValidityException("testing");
        assertEquals("testing", ex.getMessage());
    }

}
