/* Copyright 2003, 2004 Elliotte Rusty Harold
   
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

import nu.xom.ParsingException;

/**
 * <p>
 *   Unit tests for the <code>ParsingException</code> class.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.1b2
 *
 */
public class ParsingExceptionTest extends XOMTestCase {
    
    
    private ParsingException ex;
    private Exception cause;
    private String message = "testing 1-2-3";
    
    
    public ParsingExceptionTest(String name) {
        super(name);
    }
    
    
    protected void setUp() {
        ex = new ParsingException("message");
        cause = new Exception();
    }

    
    public void testConstructor() {
        ParsingException ex = new ParsingException(message, cause);
        assertEquals(message, ex.getMessage());
        assertEquals(cause, ex.getCause()); 
    }
    
    
    public void testFourArgumentConstructor() {
            
        ParsingException ex = new ParsingException(message, 100000, 400000, cause);
        assertEquals(message, ex.getMessage());
        assertEquals(cause, ex.getCause()); 
        assertEquals(100000, ex.getLineNumber()); 
        assertEquals(400000, ex.getColumnNumber()); 

    }
    
    
    public void testLineAndColumnNumbers() {
        ParsingException ex = new ParsingException(message, 10, 20);
        assertEquals(message, ex.getMessage());
        assertNull(ex.getCause());
        assertEquals(10, ex.getLineNumber()); 
        assertEquals(20, ex.getColumnNumber()); 
    }
    
    
    public void testLineAndColumnNumbersInToString() {
        ParsingException ex = new ParsingException(message, -1, -1);
        String result = ex.toString();
        assertEquals(-1, result.indexOf("-1"));
    }
    
    
    public void testToString() {
        ParsingException ex = new ParsingException(message, 10, 20);
        assertTrue(ex.toString().endsWith(" at line 10, column 20")); 
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
        
        ParsingException ex = new ParsingException(null, null);
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
        Exception ex = new ParsingException("testing");
        assertEquals("testing", ex.getMessage());
    }

    
    public void testGetURI() { 
        
        ParsingException ex = new ParsingException("testing", "http://www.example.org/", 32, 24);
        assertEquals("http://www.example.org/", ex.getURI());
        
        Exception cause = new Exception("test");
        ex = new ParsingException("testing", "http://www.example.org/", 32, 24, cause);
        assertEquals("http://www.example.org/", ex.getURI());
        assertEquals(cause, ex.getCause());
        
    }

    
    public void testURIInToString() { 
        
        ParsingException ex = new ParsingException("testing", "http://www.example.org/", 32, 24);
        assertTrue(ex.toString().indexOf("http://www.example.org/") > 1);
        
    }

    
}
