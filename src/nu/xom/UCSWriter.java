/* Copyright 2004 Elliotte Rusty Harold
   
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

package nu.xom;

import java.io.Writer;

/**
 * <p>
 * Theoretically, I should be able to use a UnicodeWriter
 * for UCS4. However, there's an apparent bug in handling non-BMP
 * characters that I haven't been able to track down, so this class
 * works around it buy escaping all non-BMP characters.
 * </p>
 *
 * @author Elliotte Rusty Harold
 * @version 1.0
 *
 */
class UCSWriter extends TextWriter {

    UCSWriter(Writer out, String encoding) {
        super(out, encoding);
    }

    /**
     * @see nu.xom.TextWriter#needsEscaping(char)
     */
    boolean needsEscaping(char c) {
        if (c < 0xD800) return false;
        if (c <= 0xDFFF) return true;
        return false;
    }

}
