/* Copyright 2003-2005 Elliotte Rusty Harold
   
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
   subject line. The XOM home page is located at http://www.xom.nu/
*/

package nu.xom.tests;

import nu.xom.canonical.CanonicalizationException;

/**
 * <p>
 *   Unit tests for the <code>CanonicalizationException</code> class.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.1d7
 *
 */
public class CanonicalizationExceptionTest extends XOMTestCase {
    
    
    private CanonicalizationException ex;
    private Exception cause;
    
    
    public CanonicalizationExceptionTest(String name) {
        super(name);   
    }

    
    protected void setUp() {
        ex = new CanonicalizationException("message");
        cause = new Exception();
    }
    
    
    public void testConstructor() {
        String message = "testing 1-2-3";
        CanonicalizationException ex = new CanonicalizationException(message);
        assertEquals(message, ex.getMessage());
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
        
        CanonicalizationException ex = new CanonicalizationException(null);
        ex.initCause(null);
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
        Exception ex = new CanonicalizationException("testing");
        assertEquals("testing", ex.getMessage());
    }

}
