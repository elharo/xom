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
 */
class Latin10Writer extends TextWriter {

    Latin10Writer(Writer out, String encoding) {
        super(out, encoding);
    }

    /**
     * @see nu.xom.TextWriter#needsEscaping(char)
     */
    boolean needsEscaping(char c) {
        if (c <= 0xA0) return false;        
        switch (c) {
            case 0x0104: return false; // LATIN CAPITAL LETTER A WITH OGONEK
            case 0x0105: return false; // LATIN SMALL LETTER A WITH OGONEK
            case 0x0141: return false; // LATIN CAPITAL LETTER L WITH STROKE
            case 0x20AC: return false; // EURO SIGN
            case 0x201E: return false; // DOUBLE LOW-9 QUOTATION MARK
            case 0x0160: return false; // LATIN CAPITAL LETTER S WITH CARON
            case 0x00A7: return false; // SECTION SIGN
            case 0x0161: return false; // LATIN SMALL LETTER S WITH CARON
            case 0x00A9: return false; // COPYRIGHT SIGN
            case 0x0218: return false; // LATIN CAPITAL LETTER S WITH COMMA BELOW
            case 0x00AB: return false; // LEFT-POINTING DOUBLE ANGLE QUOTATION MARK
            case 0x0179: return false; // LATIN CAPITAL LETTER Z WITH ACUTE
            case 0x00AD: return false; // SOFT HYPHEN
            case 0x017A: return false; // LATIN SMALL LETTER Z WITH ACUTE
            case 0x017B: return false; // LATIN CAPITAL LETTER Z WITH DOT ABOVE
            case 0x00B0: return false; // DEGREE SIGN
            case 0x00B1: return false; // PLUS-MINUS SIGN
            case 0x010C: return false; // LATIN CAPITAL LETTER C WITH CARON
            case 0x0142: return false; // LATIN SMALL LETTER L WITH STROKE
            case 0x017D: return false; // LATIN CAPITAL LETTER Z WITH CARON
            case 0x201D: return false; // RIGHT DOUBLE QUOTATION MARK
            case 0x00B6: return false; // PILCROW SIGN
            case 0x00B7: return false; // MIDDLE DOT
            case 0x017E: return false; // LATIN SMALL LETTER Z WITH CARON
            case 0x010D: return false; // LATIN SMALL LETTER C WITH CARON
            case 0x0219: return false; // LATIN SMALL LETTER S WITH COMMA BELOW
            case 0x00BB: return false; // RIGHT-POINTING DOUBLE ANGLE QUOTATION MARK
            case 0x0152: return false; // LATIN CAPITAL LIGATURE OE
            case 0x0153: return false; // LATIN SMALL LIGATURE OE
            case 0x0178: return false; // LATIN CAPITAL LETTER Y WITH DIAERESIS
            case 0x017C: return false; // LATIN SMALL LETTER Z WITH DOT ABOVE
            case 0x00C0: return false; // LATIN CAPITAL LETTER A WITH GRAVE
            case 0x00C1: return false; // LATIN CAPITAL LETTER A WITH ACUTE
            case 0x00C2: return false; // LATIN CAPITAL LETTER A WITH CIRCUMFLEX
            case 0x0102: return false; // LATIN CAPITAL LETTER A WITH BREVE
            case 0x00C4: return false; // LATIN CAPITAL LETTER A WITH DIAERESIS
            case 0x0106: return false; // LATIN CAPITAL LETTER C WITH ACUTE
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
            case 0x0110: return false; // LATIN CAPITAL LETTER D WITH STROKE
            case 0x0143: return false; // LATIN CAPITAL LETTER N WITH ACUTE
            case 0x00D2: return false; // LATIN CAPITAL LETTER O WITH GRAVE
            case 0x00D3: return false; // LATIN CAPITAL LETTER O WITH ACUTE
            case 0x00D4: return false; // LATIN CAPITAL LETTER O WITH CIRCUMFLEX
            case 0x0150: return false; // LATIN CAPITAL LETTER O WITH DOUBLE ACUTE
            case 0x00D6: return false; // LATIN CAPITAL LETTER O WITH DIAERESIS
            case 0x015A: return false; // LATIN CAPITAL LETTER S WITH ACUTE
            case 0x0170: return false; // LATIN CAPITAL LETTER U WITH DOUBLE ACUTE
            case 0x00D9: return false; // LATIN CAPITAL LETTER U WITH GRAVE
            case 0x00DA: return false; // LATIN CAPITAL LETTER U WITH ACUTE
            case 0x00DB: return false; // LATIN CAPITAL LETTER U WITH CIRCUMFLEX
            case 0x00DC: return false; // LATIN CAPITAL LETTER U WITH DIAERESIS
            case 0x0118: return false; // LATIN CAPITAL LETTER E WITH OGONEK
            case 0x021A: return false; // LATIN CAPITAL LETTER T WITH COMMA BELOW
            case 0x00DF: return false; // LATIN SMALL LETTER SHARP S
            case 0x00E0: return false; // LATIN SMALL LETTER A WITH GRAVE
            case 0x00E1: return false; // LATIN SMALL LETTER A WITH ACUTE
            case 0x00E2: return false; // LATIN SMALL LETTER A WITH CIRCUMFLEX
            case 0x0103: return false; // LATIN SMALL LETTER A WITH BREVE
            case 0x00E4: return false; // LATIN SMALL LETTER A WITH DIAERESIS
            case 0x0107: return false; // LATIN SMALL LETTER C WITH ACUTE
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
            case 0x0111: return false; // LATIN SMALL LETTER D WITH STROKE
            case 0x0144: return false; // LATIN SMALL LETTER N WITH ACUTE
            case 0x00F2: return false; // LATIN SMALL LETTER O WITH GRAVE
            case 0x00F3: return false; // LATIN SMALL LETTER O WITH ACUTE
            case 0x00F4: return false; // LATIN SMALL LETTER O WITH CIRCUMFLEX
            case 0x0151: return false; // LATIN SMALL LETTER O WITH DOUBLE ACUTE
            case 0x00F6: return false; // LATIN SMALL LETTER O WITH DIAERESIS
            case 0x015B: return false; // LATIN SMALL LETTER S WITH ACUTE
            case 0x0171: return false; // LATIN SMALL LETTER U WITH DOUBLE ACUTE
            case 0x00F9: return false; // LATIN SMALL LETTER U WITH GRAVE
            case 0x00FA: return false; // LATIN SMALL LETTER U WITH ACUTE
            case 0x00FB: return false; // LATIN SMALL LETTER U WITH CIRCUMFLEX
            case 0x00FC: return false; // LATIN SMALL LETTER U WITH DIAERESIS
            case 0x0119: return false; // LATIN SMALL LETTER E WITH OGONEK
            case 0x021B: return false; // LATIN SMALL LETTER T WITH COMMA BELOW
            case 0x00FF: return false; // LATIN SMALL LETTER Y WITH DIAERESIS
        }
        return true;
    }

}