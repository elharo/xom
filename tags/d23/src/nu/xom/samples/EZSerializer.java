// Copyright 2003, 2004 Elliotte Rusty Harold
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

package nu.xom.samples;

import java.io.IOException;
import java.io.OutputStream;

import nu.xom.Document;
import nu.xom.Serializer;

/**
 * <p>
 *  A collection of static methods for serializing documents
 *  in one method call. 
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d23
 * 
 */
public class EZSerializer {

    
    /**
     * <p>
     * Serializes a document onto the output stream in UTF-8 with no 
     * pretty printing. The stream is flushed but not closed when this 
     * method completes.
     * </p>
     * 
     * @param doc the <code>Document</code> to serialize
     * @param out the <code>OutputStream</code> on which the document
     *     is written
     * 
     * @throws IOException if the underlying <code>OutputStream</code>
     *     encounters an I/O error
     * @throws NullPointerException if <code>doc</code> is null
     */
    public static void write(Document doc, OutputStream out) 
      throws IOException {
        Serializer serializer = new Serializer(out);
        serializer.write(doc);
        serializer.flush();
    }
    
    /**
     * <p>
     * Serializes a document onto the output stream in the specified 
     * encoding. White space is added to attempt to pretty print the
     * document, potentially changing the document's content.
     * Existing white space in the document may be trimmed or changed 
     * in the process. The stream is flushed but not closed when this 
     * method completes.
     * </p>
     * 
     * <p>
     *   The <code>indent</code> and <code>maxLength</code> arguments
     *   are suggestive, not prescriptive. XOM will attempt to honor
     *   them but cannot guarantee to do so. For instance, if an 
     *   element name is longer than the maximum line length, XOM
     *   cannot break the element name. It has to emit an 
     *   excessively long line.
     * </p>
     * 
     * @param doc the <code>Document</code> to serialize
     * @param out the <code>OutputStream</code> on which the document
     *     is written
     * @param encoding the character encoding in which to write 
     *     the document
     * @param indent the number of spaces to indent each successive
     *    level of the hierarchy
     * @param maxLength the maximum preferred number of characters per
     *    line
     * 
     * @throws IOException if the underlying <code>OutputStream</code>
     *     encounters an I/O error
     * @throws NullPointerException if <code>doc</code> is null
     */
    public static void write(Document doc, OutputStream out, String encoding,
      int indent, int maxLength) 
      throws IOException {
        Serializer serializer = new Serializer(out, encoding);
        serializer.setIndent(indent);
        serializer.setMaxLength(maxLength);
        serializer.write(doc);
        serializer.flush();
    }
    
}
