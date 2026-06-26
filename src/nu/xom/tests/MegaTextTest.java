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

import java.util.Arrays;
import nu.xom.DataCapacityException;
import nu.xom.Text;

/**
 *
 * <p>
 *  Tests for gigabyte sized <code>Text</code> nodes.
 * </p>
 *
 * @author Elliotte Rusty Harold
 * @version 1.5.0
 *
 */
public class MegaTextTest extends XOMTestCase {


  public MegaTextTest(String name) {
    super(name);
  }


  public void testConstructor() {
    String s = makeBigString();
    try {
       new Text(s);
       fail("Should have thrown DataCapacityException");
    }
    catch (DataCapacityException expected) {
      assertNotNull(expected.getMessage());
    }
  }


  /**
   * Generates a string containing 1.2 billion occurrences of the character '中'.
   * 中 is two bytes in UTF-16 but 3 bytes in UTF-8. Since XOM stores text nodes 
   * in a single byte array of UTF-8 it can't handle something this large.
   */
  private static String makeBigString() {
    int count = 740000000; // 740 million
    char c = '\u4E2D'; // 中 which means "middle"

    char[] buffer = new char[count];
    Arrays.fill(buffer, c);

    return new String(buffer);
  }

}
