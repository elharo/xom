/* Copyright 2002, 2003 Elliotte Rusty Harold
   
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
 * @author Elliotte Rusty Harold
 * @version 1.0
 *
 */
class ASCIIWriter extends TextWriter {

    /**
     * <p>
     * Constructor for ASCIIWriter.
     * </p>
     *
     * @param out the <code>Writer</code> to write to
     * @param encoding the encoding the writer uses
     */
    ASCIIWriter(Writer out, String encoding) {
        super(out, encoding);
    }

    /**
     * @param c the character tested for availability in the ASCII character set
     * @return true if this character must be escaped
     *              with a numeric character reference in ASCII
     * @see nu.xom.TextWriter#needsEscaping(char)
     */
    boolean needsEscaping(char c) {
        return c > 127;
    }

}
