// Copyright 2002, 2003 Elliotte Rusty Harold
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

import java.io.Writer;

/**
 * @author Elliotte Rusty Harold
 * @version 1.0d23
 * 
 *
 */
class Latin8Writer extends TextWriter {

    Latin8Writer(Writer out, String encoding) {
        super(out, encoding);
    }

    /**
     * @see nu.xom.TextWriter#needsEscaping(char)
     */
    boolean needsEscaping(char c) {
        if (c <= 0xA0) return false;        
        switch (c) {
            case 0x1E02: return false; // LATIN CAPITAL LETTER B WITH DOT ABOVE
            case 0x1E03: return false; // LATIN SMALL LETTER B WITH DOT ABOVE
            case 0x00A3: return false; // POUND SIGN
            case 0x010A: return false; // LATIN CAPITAL LETTER C WITH DOT ABOVE
            case 0x010B: return false; // LATIN SMALL LETTER C WITH DOT ABOVE
            case 0x1E0A: return false; // LATIN CAPITAL LETTER D WITH DOT ABOVE
            case 0x00A7: return false; // SECTION SIGN
            case 0x1E80: return false; // LATIN CAPITAL LETTER W WITH GRAVE
            case 0x00A9: return false; // COPYRIGHT SIGN
            case 0x1E82: return false; // LATIN CAPITAL LETTER W WITH ACUTE
            case 0x1E0B: return false; // LATIN SMALL LETTER D WITH DOT ABOVE
            case 0x1EF2: return false; // LATIN CAPITAL LETTER Y WITH GRAVE
            case 0x00AD: return false; // SOFT HYPHEN
            case 0x00AE: return false; // REGISTERED SIGN
            case 0x0178: return false; // LATIN CAPITAL LETTER Y WITH DIAERESIS
            case 0x1E1E: return false; // LATIN CAPITAL LETTER F WITH DOT ABOVE
            case 0x1E1F: return false; // LATIN SMALL LETTER F WITH DOT ABOVE
            case 0x0120: return false; // LATIN CAPITAL LETTER G WITH DOT ABOVE
            case 0x0121: return false; // LATIN SMALL LETTER G WITH DOT ABOVE
            case 0x1E40: return false; // LATIN CAPITAL LETTER M WITH DOT ABOVE
            case 0x1E41: return false; // LATIN SMALL LETTER M WITH DOT ABOVE
            case 0x00B6: return false; // PILCROW SIGN
            case 0x1E56: return false; // LATIN CAPITAL LETTER P WITH DOT ABOVE
            case 0x1E81: return false; // LATIN SMALL LETTER W WITH GRAVE
            case 0x1E57: return false; // LATIN SMALL LETTER P WITH DOT ABOVE
            case 0x1E83: return false; // LATIN SMALL LETTER W WITH ACUTE
            case 0x1E60: return false; // LATIN CAPITAL LETTER S WITH DOT ABOVE
            case 0x1EF3: return false; // LATIN SMALL LETTER Y WITH GRAVE
            case 0x1E84: return false; // LATIN CAPITAL LETTER W WITH DIAERESIS
            case 0x1E85: return false; // LATIN SMALL LETTER W WITH DIAERESIS
            case 0x1E61: return false; // LATIN SMALL LETTER S WITH DOT ABOVE
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
            case 0x0174: return false; // LATIN CAPITAL LETTER W WITH CIRCUMFLEX
            case 0x00D1: return false; // LATIN CAPITAL LETTER N WITH TILDE
            case 0x00D2: return false; // LATIN CAPITAL LETTER O WITH GRAVE
            case 0x00D3: return false; // LATIN CAPITAL LETTER O WITH ACUTE
            case 0x00D4: return false; // LATIN CAPITAL LETTER O WITH CIRCUMFLEX
            case 0x00D5: return false; // LATIN CAPITAL LETTER O WITH TILDE
            case 0x00D6: return false; // LATIN CAPITAL LETTER O WITH DIAERESIS
            case 0x1E6A: return false; // LATIN CAPITAL LETTER T WITH DOT ABOVE
            case 0x00D8: return false; // LATIN CAPITAL LETTER O WITH STROKE
            case 0x00D9: return false; // LATIN CAPITAL LETTER U WITH GRAVE
            case 0x00DA: return false; // LATIN CAPITAL LETTER U WITH ACUTE
            case 0x00DB: return false; // LATIN CAPITAL LETTER U WITH CIRCUMFLEX
            case 0x00DC: return false; // LATIN CAPITAL LETTER U WITH DIAERESIS
            case 0x00DD: return false; // LATIN CAPITAL LETTER Y WITH ACUTE
            case 0x0176: return false; // LATIN CAPITAL LETTER Y WITH CIRCUMFLEX
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
            case 0x0175: return false; // LATIN SMALL LETTER W WITH CIRCUMFLEX
            case 0x00F1: return false; // LATIN SMALL LETTER N WITH TILDE
            case 0x00F2: return false; // LATIN SMALL LETTER O WITH GRAVE
            case 0x00F3: return false; // LATIN SMALL LETTER O WITH ACUTE
            case 0x00F4: return false; // LATIN SMALL LETTER O WITH CIRCUMFLEX
            case 0x00F5: return false; // LATIN SMALL LETTER O WITH TILDE
            case 0x00F6: return false; // LATIN SMALL LETTER O WITH DIAERESIS
            case 0x1E6B: return false; // LATIN SMALL LETTER T WITH DOT ABOVE
            case 0x00F8: return false; // LATIN SMALL LETTER O WITH STROKE
            case 0x00F9: return false; // LATIN SMALL LETTER U WITH GRAVE
            case 0x00FA: return false; // LATIN SMALL LETTER U WITH ACUTE
            case 0x00FB: return false; // LATIN SMALL LETTER U WITH CIRCUMFLEX
            case 0x00FC: return false; // LATIN SMALL LETTER U WITH DIAERESIS
            case 0x00FD: return false; // LATIN SMALL LETTER Y WITH ACUTE
            case 0x0177: return false; // LATIN SMALL LETTER Y WITH CIRCUMFLEX
            case 0x00FF: return false; // LATIN SMALL LETTER Y WITH DIAERESIS
        }
        return true;
    }

}