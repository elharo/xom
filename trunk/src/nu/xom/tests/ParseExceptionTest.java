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

import nu.xom.ParseException;
import nu.xom.tests.XOMTestCase;

/**
 * <p>
 *   This class provides unit tests for for the ParseException
 *   class.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d21
 *
 */
public class ParseExceptionTest extends XOMTestCase {
    
    private Exception ex;
    private Exception cause;
    
    protected void setUp() {
        ex = new ParseException();
        cause = new Exception();
    }

    public void testInitCause() {
        
        assertNull(ex.getCause());
        ex.initCause(cause);
        assertEquals(cause, ex.getCause());
        
        try {
            ex.initCause(null);   
            fail("Reinitialized cause over null");   
        }
        catch (IllegalStateException result) {
            // success   
        }
        
        try {
            ex.initCause(new Exception());   
            fail("Reinitialized cause over null");   
        }
        catch (IllegalStateException result) {
            // success   
        }
        
    }


    public void testNullInitCause() {
        
        Exception ex = new ParseException(null, null);
        assertNull(ex.getCause());
        
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
        Exception ex = new ParseException("testing");
        assertEquals("testing", ex.getMessage());
    }

    public void testGetNoMessage() {      
        Exception ex = new ParseException();
        assertNull(ex.getMessage());
    }

}
