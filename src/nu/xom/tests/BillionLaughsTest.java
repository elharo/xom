/* Copyright 2026 Elliotte Rusty Harold
   
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

import java.io.File;
import java.io.IOException;

import nu.xom.Builder;
import nu.xom.ParsingException;
import org.xml.sax.SAXException;

/**
 * Tests for Billion Laughs attacks.
 * 
 * @author Elliotte Rusty Harold
 * @version 1.5.0
 */
public class BillionLaughsTest extends XOMTestCase {

    private File inputDir = new File("data/billionlaughs");
    private Builder builder = new Builder();
    
    protected void tearDown() {
        System.gc();
    }
    
    public BillionLaughsTest(String name) {
        super(name);   
    }

    public void testLargeTextNode() throws IOException {
        try {
            builder.build(new File(inputDir, "large_text_node.xml"));
            fail("should have thrown ParsingException");
        } catch (ParsingException expected) {
            assertTrue(expected.getCause() instanceof SAXException);
        }
    }

    public void testExcessiveElements() throws IOException {
        try {
            builder.build(new File(inputDir, "excessive_elements.xml"));
            // This test can fail with a very large heap. If terabyte heaps
            // become a thing in the future, then excessive_elements.xml may
            // need to be made even larger to trigger the exception.
            fail("should have thrown ParsingException");
        } catch (ParsingException expected) {
            assertTrue(expected.getCause() instanceof SAXException);
        }
    }
    
}
