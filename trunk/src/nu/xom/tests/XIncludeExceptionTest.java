/* Copyright 2003, 2004 Elliotte Rusty Harold
   
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

import nu.xom.xinclude.BadParseAttributeException;
import nu.xom.xinclude.MisplacedFallbackException;
import nu.xom.xinclude.NoIncludeLocationException;
import nu.xom.xinclude.XIncludeException;

/**
 * <p>
 *   Unit tests for the <code>XIncludeException</code> class.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d23
 *
 */
public class XIncludeExceptionTest extends XOMTestCase {
    
    private XIncludeException ex;
    private Exception cause;
    
    
    public XIncludeExceptionTest(String name) {
        super(name);
    }

    
    protected void setUp() {
        ex = new XIncludeException("message");
        cause = new Exception();
    }
    
    
    public void testConstructor() {
        ex = new XIncludeException("test", "http://ex.com/");
        assertEquals("test", ex.getMessage());
        assertEquals("http://ex.com/", ex.getURI());
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
        
        XIncludeException ex
          = new XIncludeException("message", (Exception) null);
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
        catch (IllegalArgumentException success) {
            // success   
        }
        
    }

    
    public void testGetMessage() {      
        Exception ex = new XIncludeException("testing");
        assertEquals("testing", ex.getMessage());
    }
    
    
    public void testMisplacedFallbackException() {
        String message = "message";
        Exception ex = new MisplacedFallbackException(message);
        assertEquals(message, ex.getMessage());
    }

    
    public void testBadParseAttributeException() {
        
        String message = "message";
        Exception ex = new BadParseAttributeException(message);
        assertEquals(message, ex.getMessage());
        
    }

    
    public void testNoIncludeLocationException() {
        String message = "message";
        XIncludeException ex = new NoIncludeLocationException(message);
        assertEquals(message, ex.getMessage());
        assertNull(ex.getCause());
        
        Exception cause = new Exception();
        ex = new NoIncludeLocationException(message, cause);
        assertEquals(message, ex.getMessage());
        assertEquals(cause, ex.getCause());
        
    }

}
