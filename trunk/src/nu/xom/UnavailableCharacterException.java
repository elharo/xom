// Copyright 2004 Elliotte Rusty Harold
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

package nu.xom;

/**
 * <p>
 * An <code>UnavailableCharacterException</code> is thrown when
 * serializing documents that contain characters not available
 * in the current encoding, and which cannot be escaped (for instance,
 * because they're in an element name or processing instruction data).
 * This can never happen if the encoding is UTF-8 or UTF-16.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d25
 *
 */
public class UnavailableCharacterException extends XMLException {

    private char   unavailableCharacter;
    private String encoding;
    
    
    /**
     * <p>
     * Creates a new <code>UnavailableCharacterException</code>.
     * </p>
     * 
     * @param character the character which caused the exception
     * @param encoding the encoding which does not contain the character
     */
    public UnavailableCharacterException(char character, String encoding) {
    
        super("Cannot use the character &0x" 
          + Integer.toHexString(character) + "; in the " 
          + encoding + " encoding.");
        this.unavailableCharacter = character;
        this.encoding = encoding;
        
    }

    
    /**
     * <p>
     * Returns the character which could not be written
     * in the current encoding.
     * </p>

     * @return the character which caused the exception
     */
    public char getCharacter() {
        return this.unavailableCharacter;
    }
    
    
    /**
     * <p>
     * Returns the encoding that does not support the character.
     * </p>
     *
     * @return the encoding used by the serializer when the exception
     *    was thrown
     */
    public String getEncoding() {
        return this.encoding;
    }
    
    
}