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
 * <p>
 * ISO 8859-9 for Western Europe. Includes the Euro sign and
 * several uncommon French letters. otherwise the same as
 * Latin-1.
 * </p>
 *
 * @author Elliotte Rusty Harold
 * @version 1.0
 */
class Latin9Writer extends TextWriter {

    /**
     * <p>
     * Creates a new <code>TextWriter</code> for the
     * ISO-8859-15 (Latin-9) character encoding.
     * </p>
     *
     * @param out the <code>Writer</code> on which the text will be written
     * @param encoding the character set used by the <code>Writer</code>
     */
    Latin9Writer(Writer out, String encoding) {
        super(out, encoding);
    }

    /**
     * <p>
     * Returns true if and only if this character
     * needs to be replaced by a character reference when
     * output in this encoding.
     * </p>
     *
     * @see nu.xom.TextWriter#needsEscaping(char)
     */
    boolean needsEscaping(char c) {
        if (c <= 0xA3 ) return false;      
        if (c == 0x20AC) return false; // EURO SIGN
        
        switch (c) {  // Latin-1 overlap
            case 0x00A5: return false; // YEN SIGN
            case 0x00A6: return true;  // place holder to enable table lookup
            case 0x00A7: return false; // SECTION SIGN
            case 0x00A8: return true;  // place holder to enable table lookup
            case 0x00A9: return false; // COPYRIGHT SIGN
            case 0x00AA: return false; // FEMININE ORDINAL INDICATOR
            case 0x00AB: return false; // LEFT-POINTING DOUBLE ANGLE QUOTATION MARK
            case 0x00AC: return false; // NOT SIGN
            case 0x00AD: return false; // SOFT HYPHEN
            case 0x00AE: return false; // REGISTERED SIGN
            case 0x00AF: return false; // MACRON
            case 0x00B0: return false; // DEGREE SIGN
            case 0x00B1: return false; // PLUS-MINUS SIGN
            case 0x00B2: return false; // SUPERSCRIPT TWO
            case 0x00B3: return false; // SUPERSCRIPT THREE
            case 0x00B4: return true;  // place holder to enable table lookup
            case 0x00B5: return false; // MICRO SIGN
            case 0x00B6: return false; // PILCROW SIGN
            case 0x00B7: return false; // MIDDLE DOT
            case 0x00B8: return true;  // place holder to enable table lookup
            case 0x00B9: return false; // SUPERSCRIPT ONE
            case 0x00BA: return false; // MASCULINE ORDINAL INDICATOR
            case 0x00BB: return false; // RIGHT-POINTING DOUBLE ANGLE QUOTATION MARK
            case 0x00BC: return true;  // place holder to enable table lookup
            case 0x00BD: return true;  // place holder to enable table lookup
            case 0x00BE: return true;  // place holder to enable table lookup
            case 0x00BF: return false; // INVERTED QUESTION MARK
            case 0x00C0: return false; // LATIN CAPITAL LETTER A WITH GRAVE
            case 0x00C1: return false; // LATIN CAPITAL LETTER A WITH ACUTE
            case 0x00C2: return false; // LATIN CAPITAL LETTER A WITH CIRCUMFLEX
            case 0x00C3: return false; // LATIN CAPITAL LETTER A WITH TILDE
            case 0x00C4: return false; // LATIN CAPITAL LETTER A WITH DIAERESIS
            case 0x00C5: return false; // LATIN CAPITAL LETTER A WITH RING ABOVE
            case 0x00C6: return false; // LATIN CAPITAL LETTER AE
            case 0x00C7: return false; // LATIN CAPITAL LETTER C WITH CEDILLA
            case 0x00C8: return false; // LATIN CAPITAL LETTER E WITH GRAVE
            case 0x00C9: return false; // LATIN CAPITAL LETTER E WITH ACUTE
            case 0x00CA: return false; // LATIN CAPITAL LETTER E WITH CIRCUMFLEX
            case 0x00CB: return false; // LATIN CAPITAL LETTER E WITH DIAERESIS
            case 0x00CC: return false; // LATIN CAPITAL LETTER I WITH GRAVE
            case 0x00CD: return false; // LATIN CAPITAL LETTER I WITH ACUTE
            case 0x00CE: return false; // LATIN CAPITAL LETTER I WITH CIRCUMFLEX
            case 0x00CF: return false; // LATIN CAPITAL LETTER I WITH DIAERESIS
            case 0x00D0: return false; // LATIN CAPITAL LETTER ETH
            case 0x00D1: return false; // LATIN CAPITAL LETTER N WITH TILDE
            case 0x00D2: return false; // LATIN CAPITAL LETTER O WITH GRAVE
            case 0x00D3: return false; // LATIN CAPITAL LETTER O WITH ACUTE
            case 0x00D4: return false; // LATIN CAPITAL LETTER O WITH CIRCUMFLEX
            case 0x00D5: return false; // LATIN CAPITAL LETTER O WITH TILDE
            case 0x00D6: return false; // LATIN CAPITAL LETTER O WITH DIAERESIS
            case 0x00D7: return false; // MULTIPLICATION SIGN
            case 0x00D8: return false; // LATIN CAPITAL LETTER O WITH STROKE
            case 0x00D9: return false; // LATIN CAPITAL LETTER U WITH GRAVE
            case 0x00DA: return false; // LATIN CAPITAL LETTER U WITH ACUTE
            case 0x00DB: return false; // LATIN CAPITAL LETTER U WITH CIRCUMFLEX
            case 0x00DC: return false; // LATIN CAPITAL LETTER U WITH DIAERESIS
            case 0x00DD: return false; // LATIN CAPITAL LETTER Y WITH ACUTE
            case 0x00DE: return false; // LATIN CAPITAL LETTER THORN
            case 0x00DF: return false; // LATIN SMALL LETTER SHARP S
            case 0x00E0: return false; // LATIN SMALL LETTER A WITH GRAVE
            case 0x00E1: return false; // LATIN SMALL LETTER A WITH ACUTE
            case 0x00E2: return false; // LATIN SMALL LETTER A WITH CIRCUMFLEX
            case 0x00E3: return false; // LATIN SMALL LETTER A WITH TILDE
            case 0x00E4: return false; // LATIN SMALL LETTER A WITH DIAERESIS
            case 0x00E5: return false; // LATIN SMALL LETTER A WITH RING ABOVE
            case 0x00E6: return false; // LATIN SMALL LETTER AE
            case 0x00E7: return false; // LATIN SMALL LETTER C WITH CEDILLA
            case 0x00E8: return false; // LATIN SMALL LETTER E WITH GRAVE
            case 0x00E9: return false; // LATIN SMALL LETTER E WITH ACUTE
            case 0x00EA: return false; // LATIN SMALL LETTER E WITH CIRCUMFLEX
            case 0x00EB: return false; // LATIN SMALL LETTER E WITH DIAERESIS
            case 0x00EC: return false; // LATIN SMALL LETTER I WITH GRAVE
            case 0x00ED: return false; // LATIN SMALL LETTER I WITH ACUTE
            case 0x00EE: return false; // LATIN SMALL LETTER I WITH CIRCUMFLEX
            case 0x00EF: return false; // LATIN SMALL LETTER I WITH DIAERESIS
            case 0x00F0: return false; // LATIN SMALL LETTER ETH
            case 0x00F1: return false; // LATIN SMALL LETTER N WITH TILDE
            case 0x00F2: return false; // LATIN SMALL LETTER O WITH GRAVE
            case 0x00F3: return false; // LATIN SMALL LETTER O WITH ACUTE
            case 0x00F4: return false; // LATIN SMALL LETTER O WITH CIRCUMFLEX
            case 0x00F5: return false; // LATIN SMALL LETTER O WITH TILDE
            case 0x00F6: return false; // LATIN SMALL LETTER O WITH DIAERESIS
            case 0x00F7: return false; // DIVISION SIGN
            case 0x00F8: return false; // LATIN SMALL LETTER O WITH STROKE
            case 0x00F9: return false; // LATIN SMALL LETTER U WITH GRAVE
            case 0x00FA: return false; // LATIN SMALL LETTER U WITH ACUTE
            case 0x00FB: return false; // LATIN SMALL LETTER U WITH CIRCUMFLEX
            case 0x00FC: return false; // LATIN SMALL LETTER U WITH DIAERESIS
            case 0x00FD: return false; // LATIN SMALL LETTER Y WITH ACUTE
            case 0x00FE: return false; // LATIN SMALL LETTER THORN
            case 0x00FF: return false; // LATIN SMALL LETTER Y WITH DIAERESIS
        }
        switch (c) { // uncommon French letters
            case 0x0152: return false; // LATIN CAPITAL LIGATURE OE
            case 0x0153: return false; // LATIN SMALL LIGATURE OE
            case 0x0154: return true;  // place holder to enable table lookup
            case 0x0155: return true;  // place holder to enable table lookup
            case 0x0156: return true;  // place holder to enable table lookup
            case 0x0157: return true;  // place holder to enable table lookup
            case 0x0158: return true;  // place holder to enable table lookup
            case 0x0159: return true;  // place holder to enable table lookup
            case 0x015A: return true;  // place holder to enable table lookup
            case 0x015B: return true;  // place holder to enable table lookup
            case 0x015C: return true;  // place holder to enable table lookup
            case 0x015D: return true;  // place holder to enable table lookup
            case 0x015E: return true;  // place holder to enable table lookup
            case 0x015F: return true;  // place holder to enable table lookup
            case 0x0160: return false; // LATIN CAPITAL LETTER S WITH CARON
            case 0x0161: return false; // LATIN SMALL LETTER S WITH CARON
            case 0x0162: return true;  // place holder to enable table lookup
            case 0x0163: return true;  // place holder to enable table lookup
            case 0x0164: return true;  // place holder to enable table lookup
            case 0x0165: return true;  // place holder to enable table lookup
            case 0x0166: return true;  // place holder to enable table lookup
            case 0x0167: return true;  // place holder to enable table lookup
            case 0x0168: return true;  // place holder to enable table lookup
            case 0x0169: return true;  // place holder to enable table lookup
            case 0x016A: return true;  // place holder to enable table lookup
            case 0x016B: return true;  // place holder to enable table lookup
            case 0x016C: return true;  // place holder to enable table lookup
            case 0x016D: return true;  // place holder to enable table lookup
            case 0x016E: return true;  // place holder to enable table lookup
            case 0x016F: return true;  // place holder to enable table lookup
            case 0x0170: return true;  // place holder to enable table lookup
            case 0x0171: return true;  // place holder to enable table lookup
            case 0x0172: return true;  // place holder to enable table lookup
            case 0x0173: return true;  // place holder to enable table lookup
            case 0x0174: return true;  // place holder to enable table lookup
            case 0x0175: return true;  // place holder to enable table lookup
            case 0x0176: return true;  // place holder to enable table lookup
            case 0x0177: return true;  // place holder to enable table lookup
            case 0x0178: return false; // LATIN CAPITAL LETTER Y WITH DIAERESIS
            case 0x017D: return false; // LATIN CAPITAL LETTER Z WITH CARON
            case 0x017E: return false; // LATIN SMALL LETTER Z WITH CARON
        }
        
        return true;
        
    }

}
