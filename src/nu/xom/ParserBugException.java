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

package nu.xom;


import org.xml.sax.XMLReader;

/**
 * <p>
 *  Thrown when the underlying XML parser throws an unexpected
 *  runtime exception such as {@link NullPointerException} or
 *  {@link IndexOutOfBoundsException}. This generally indicates a bug
 *  in the parser itself rather than in the XML being parsed, though
 *  it can sometimes indicate both.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.5.0
 *
 */
public class ParserBugException extends ParsingException {

    private static final long serialVersionUID = 4674449458284084556L;

    /**
     * <p>
     * Creates a new <code>ParserBugException</code> with 
     * a detail message and an underlying root cause.
     * The detail message is constructed from the original 
     * exception's message to indicate that this is a bug in 
     * the underlying parser.
     * </p>
     * 
     * @param message the detail message from the parser
     * @param uri the URI of the document being parsed
     * @param cause the original runtime exception thrown by the parser
     */
    ParserBugException(XMLReader parser, String message, String uri, Throwable cause) {
        super("Probable bug in the underlying parser: " + parser.getClass().getName() + "; " + message
          + ". Please report this bug to the parser vendor.", uri, cause);
    }

}
