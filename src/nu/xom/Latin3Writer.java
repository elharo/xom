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
class Latin3Writer extends TextWriter {

    Latin3Writer(Writer out, String encoding) {
        super(out, encoding);
    }

    /**
     * @see nu.xom.TextWriter#needsEscaping(char)
     */
    boolean needsEscaping(char c) {
        if (c <= 0xA0) return false;        
        switch (c) {
            case 0x0126: return false; // LATIN CAPITAL LETTER H WITH STROKE
            case 0x02D8: return false; // BREVE
            case 0x00A3: return false; // POUND SIGN
            case 0x00A4: return false; // CURRENCY SIGN
            case 0x0124: return false; // LATIN CAPITAL LETTER H WITH CIRCUMFLEX
            case 0x00A7: return false; // SECTION SIGN
            case 0x00A8: return false; // DIAERESIS
            case 0x0130: return false; // LATIN CAPITAL LETTER I WITH DOT ABOVE
            case 0x015E: return false; // LATIN CAPITAL LETTER S WITH CEDILLA
            case 0x011E: return false; // LATIN CAPITAL LETTER G WITH BREVE
            case 0x0134: return false; // LATIN CAPITAL LETTER J WITH CIRCUMFLEX
            case 0x00AD: return false; // SOFT HYPHEN
            case 0x017B: return false; // LATIN CAPITAL LETTER Z WITH DOT ABOVE
            case 0x00B0: return false; // DEGREE SIGN
            case 0x0127: return false; // LATIN SMALL LETTER H WITH STROKE
            case 0x00B2: return false; // SUPERSCRIPT TWO
            case 0x00B3: return false; // SUPERSCRIPT THREE
            case 0x00B4: return false; // ACUTE ACCENT
            case 0x00B5: return false; // MICRO SIGN
            case 0x0125: return false; // LATIN SMALL LETTER H WITH CIRCUMFLEX
            case 0x00B7: return false; // MIDDLE DOT
            case 0x00B8: return false; // CEDILLA
            case 0x0131: return false; // LATIN SMALL LETTER DOTLESS I
            case 0x015F: return false; // LATIN SMALL LETTER S WITH CEDILLA
            case 0x011F: return false; // LATIN SMALL LETTER G WITH BREVE
            case 0x0135: return false; // LATIN SMALL LETTER J WITH CIRCUMFLEX
            case 0x00BD: return false; // VULGAR FRACTION ONE HALF
            case 0x017C: return false; // LATIN SMALL LETTER Z WITH DOT ABOVE
            case 0x00C0: return false; // LATIN CAPITAL LETTER A WITH GRAVE
            case 0x00C1: return false; // LATIN CAPITAL LETTER A WITH ACUTE
            case 0x00C2: return false; // LATIN CAPITAL LETTER A WITH CIRCUMFLEX
            case 0x00C4: return false; // LATIN CAPITAL LETTER A WITH DIAERESIS
            case 0x010A: return false; // LATIN CAPITAL LETTER C WITH DOT ABOVE
            case 0x0108: return false; // LATIN CAPITAL LETTER C WITH CIRCUMFLEX
            case 0x00C7: return false; // LATIN CAPITAL LETTER C WITH CEDILLA
            case 0x00C8: return false; // LATIN CAPITAL LETTER E WITH GRAVE
            case 0x00C9: return false; // LATIN CAPITAL LETTER E WITH ACUTE
            case 0x00CA: return false; // LATIN CAPITAL LETTER E WITH CIRCUMFLEX
            case 0x00CB: return false; // LATIN CAPITAL LETTER E WITH DIAERESIS
            case 0x00CC: return false; // LATIN CAPITAL LETTER I WITH GRAVE
            case 0x00CD: return false; // LATIN CAPITAL LETTER I WITH ACUTE
            case 0x00CE: return false; // LATIN CAPITAL LETTER I WITH CIRCUMFLEX
            case 0x00CF: return false; // LATIN CAPITAL LETTER I WITH DIAERESIS
            case 0x00D1: return false; // LATIN CAPITAL LETTER N WITH TILDE
            case 0x00D2: return false; // LATIN CAPITAL LETTER O WITH GRAVE
            case 0x00D3: return false; // LATIN CAPITAL LETTER O WITH ACUTE
            case 0x00D4: return false; // LATIN CAPITAL LETTER O WITH CIRCUMFLEX
            case 0x0120: return false; // LATIN CAPITAL LETTER G WITH DOT ABOVE
            case 0x00D6: return false; // LATIN CAPITAL LETTER O WITH DIAERESIS
            case 0x00D7: return false; // MULTIPLICATION SIGN
            case 0x011C: return false; // LATIN CAPITAL LETTER G WITH CIRCUMFLEX
            case 0x00D9: return false; // LATIN CAPITAL LETTER U WITH GRAVE
            case 0x00DA: return false; // LATIN CAPITAL LETTER U WITH ACUTE
            case 0x00DB: return false; // LATIN CAPITAL LETTER U WITH CIRCUMFLEX
            case 0x00DC: return false; // LATIN CAPITAL LETTER U WITH DIAERESIS
            case 0x016C: return false; // LATIN CAPITAL LETTER U WITH BREVE
            case 0x015C: return false; // LATIN CAPITAL LETTER S WITH CIRCUMFLEX
            case 0x00DF: return false; // LATIN SMALL LETTER SHARP S
            case 0x00E0: return false; // LATIN SMALL LETTER A WITH GRAVE
            case 0x00E1: return false; // LATIN SMALL LETTER A WITH ACUTE
            case 0x00E2: return false; // LATIN SMALL LETTER A WITH CIRCUMFLEX
            case 0x00E4: return false; // LATIN SMALL LETTER A WITH DIAERESIS
            case 0x010B: return false; // LATIN SMALL LETTER C WITH DOT ABOVE
            case 0x0109: return false; // LATIN SMALL LETTER C WITH CIRCUMFLEX
            case 0x00E7: return false; // LATIN SMALL LETTER C WITH CEDILLA
            case 0x00E8: return false; // LATIN SMALL LETTER E WITH GRAVE
            case 0x00E9: return false; // LATIN SMALL LETTER E WITH ACUTE
            case 0x00EA: return false; // LATIN SMALL LETTER E WITH CIRCUMFLEX
            case 0x00EB: return false; // LATIN SMALL LETTER E WITH DIAERESIS
            case 0x00EC: return false; // LATIN SMALL LETTER I WITH GRAVE
            case 0x00ED: return false; // LATIN SMALL LETTER I WITH ACUTE
            case 0x00EE: return false; // LATIN SMALL LETTER I WITH CIRCUMFLEX
            case 0x00EF: return false; // LATIN SMALL LETTER I WITH DIAERESIS
            case 0x00F1: return false; // LATIN SMALL LETTER N WITH TILDE
            case 0x00F2: return false; // LATIN SMALL LETTER O WITH GRAVE
            case 0x00F3: return false; // LATIN SMALL LETTER O WITH ACUTE
            case 0x00F4: return false; // LATIN SMALL LETTER O WITH CIRCUMFLEX
            case 0x0121: return false; // LATIN SMALL LETTER G WITH DOT ABOVE
            case 0x00F6: return false; // LATIN SMALL LETTER O WITH DIAERESIS
            case 0x00F7: return false; // DIVISION SIGN
            case 0x011D: return false; // LATIN SMALL LETTER G WITH CIRCUMFLEX
            case 0x00F9: return false; // LATIN SMALL LETTER U WITH GRAVE
            case 0x00FA: return false; // LATIN SMALL LETTER U WITH ACUTE
            case 0x00FB: return false; // LATIN SMALL LETTER U WITH CIRCUMFLEX
            case 0x00FC: return false; // LATIN SMALL LETTER U WITH DIAERESIS
            case 0x016D: return false; // LATIN SMALL LETTER U WITH BREVE
            case 0x015D: return false; // LATIN SMALL LETTER S WITH CIRCUMFLEX
            case 0x02D9: return false; // DOT ABOVE
        }
        return true;
    }

}