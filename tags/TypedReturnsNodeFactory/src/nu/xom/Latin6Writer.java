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
 * @version 1.0d5
 * 
 *
 */
class Latin6Writer extends TextWriter {

    public Latin6Writer(Writer out, String encoding) {
        super(out, encoding);
    }

    /**
     * @see nu.xom.TextWriter#needsEscaping(char)
     */
    public boolean needsEscaping(char c) {
        if (c <= 0xA0) return false;        
        switch (c) {
            case 0x0104: return false; // LATIN CAPITAL LETTER A WITH OGONEK
            case 0x0112: return false; // LATIN CAPITAL LETTER E WITH MACRON
            case 0x0122: return false; // LATIN CAPITAL LETTER G WITH CEDILLA
            case 0x012A: return false; // LATIN CAPITAL LETTER I WITH MACRON
            case 0x0128: return false; // LATIN CAPITAL LETTER I WITH TILDE
            case 0x0136: return false; // LATIN CAPITAL LETTER K WITH CEDILLA
            case 0x00A7: return false; // SECTION SIGN
            case 0x013B: return false; // LATIN CAPITAL LETTER L WITH CEDILLA
            case 0x0110: return false; // LATIN CAPITAL LETTER D WITH STROKE
            case 0x0160: return false; // LATIN CAPITAL LETTER S WITH CARON
            case 0x0166: return false; // LATIN CAPITAL LETTER T WITH STROKE
            case 0x017D: return false; // LATIN CAPITAL LETTER Z WITH CARON
            case 0x00AD: return false; // SOFT HYPHEN
            case 0x016A: return false; // LATIN CAPITAL LETTER U WITH MACRON
            case 0x014A: return false; // LATIN CAPITAL LETTER ENG
            case 0x00B0: return false; // DEGREE SIGN
            case 0x0105: return false; // LATIN SMALL LETTER A WITH OGONEK
            case 0x0113: return false; // LATIN SMALL LETTER E WITH MACRON
            case 0x0123: return false; // LATIN SMALL LETTER G WITH CEDILLA
            case 0x012B: return false; // LATIN SMALL LETTER I WITH MACRON
            case 0x0129: return false; // LATIN SMALL LETTER I WITH TILDE
            case 0x0137: return false; // LATIN SMALL LETTER K WITH CEDILLA
            case 0x00B7: return false; // MIDDLE DOT
            case 0x013C: return false; // LATIN SMALL LETTER L WITH CEDILLA
            case 0x0111: return false; // LATIN SMALL LETTER D WITH STROKE
            case 0x0161: return false; // LATIN SMALL LETTER S WITH CARON
            case 0x0167: return false; // LATIN SMALL LETTER T WITH STROKE
            case 0x017E: return false; // LATIN SMALL LETTER Z WITH CARON
            case 0x2015: return false; // HORIZONTAL BAR
            case 0x016B: return false; // LATIN SMALL LETTER U WITH MACRON
            case 0x014B: return false; // LATIN SMALL LETTER ENG
            case 0x0100: return false; // LATIN CAPITAL LETTER A WITH MACRON
            case 0x00C1: return false; // LATIN CAPITAL LETTER A WITH ACUTE
            case 0x00C2: return false; // LATIN CAPITAL LETTER A WITH CIRCUMFLEX
            case 0x00C3: return false; // LATIN CAPITAL LETTER A WITH TILDE
            case 0x00C4: return false; // LATIN CAPITAL LETTER A WITH DIAERESIS
            case 0x00C5: return false; // LATIN CAPITAL LETTER A WITH RING ABOVE
            case 0x00C6: return false; // LATIN CAPITAL LETTER AE
            case 0x012E: return false; // LATIN CAPITAL LETTER I WITH OGONEK
            case 0x010C: return false; // LATIN CAPITAL LETTER C WITH CARON
            case 0x00C9: return false; // LATIN CAPITAL LETTER E WITH ACUTE
            case 0x0118: return false; // LATIN CAPITAL LETTER E WITH OGONEK
            case 0x00CB: return false; // LATIN CAPITAL LETTER E WITH DIAERESIS
            case 0x0116: return false; // LATIN CAPITAL LETTER E WITH DOT ABOVE
            case 0x00CD: return false; // LATIN CAPITAL LETTER I WITH ACUTE
            case 0x00CE: return false; // LATIN CAPITAL LETTER I WITH CIRCUMFLEX
            case 0x00CF: return false; // LATIN CAPITAL LETTER I WITH DIAERESIS
            case 0x00D0: return false; // LATIN CAPITAL LETTER ETH (Icelandic)
            case 0x0145: return false; // LATIN CAPITAL LETTER N WITH CEDILLA
            case 0x014C: return false; // LATIN CAPITAL LETTER O WITH MACRON
            case 0x00D3: return false; // LATIN CAPITAL LETTER O WITH ACUTE
            case 0x00D4: return false; // LATIN CAPITAL LETTER O WITH CIRCUMFLEX
            case 0x00D5: return false; // LATIN CAPITAL LETTER O WITH TILDE
            case 0x00D6: return false; // LATIN CAPITAL LETTER O WITH DIAERESIS
            case 0x0168: return false; // LATIN CAPITAL LETTER U WITH TILDE
            case 0x00D8: return false; // LATIN CAPITAL LETTER O WITH STROKE
            case 0x0172: return false; // LATIN CAPITAL LETTER U WITH OGONEK
            case 0x00DA: return false; // LATIN CAPITAL LETTER U WITH ACUTE
            case 0x00DB: return false; // LATIN CAPITAL LETTER U WITH CIRCUMFLEX
            case 0x00DC: return false; // LATIN CAPITAL LETTER U WITH DIAERESIS
            case 0x00DD: return false; // LATIN CAPITAL LETTER Y WITH ACUTE
            case 0x00DE: return false; // LATIN CAPITAL LETTER THORN (Icelandic)
            case 0x00DF: return false; // LATIN SMALL LETTER SHARP S (German)
            case 0x0101: return false; // LATIN SMALL LETTER A WITH MACRON
            case 0x00E1: return false; // LATIN SMALL LETTER A WITH ACUTE
            case 0x00E2: return false; // LATIN SMALL LETTER A WITH CIRCUMFLEX
            case 0x00E3: return false; // LATIN SMALL LETTER A WITH TILDE
            case 0x00E4: return false; // LATIN SMALL LETTER A WITH DIAERESIS
            case 0x00E5: return false; // LATIN SMALL LETTER A WITH RING ABOVE
            case 0x00E6: return false; // LATIN SMALL LETTER AE
            case 0x012F: return false; // LATIN SMALL LETTER I WITH OGONEK
            case 0x010D: return false; // LATIN SMALL LETTER C WITH CARON
            case 0x00E9: return false; // LATIN SMALL LETTER E WITH ACUTE
            case 0x0119: return false; // LATIN SMALL LETTER E WITH OGONEK
            case 0x00EB: return false; // LATIN SMALL LETTER E WITH DIAERESIS
            case 0x0117: return false; // LATIN SMALL LETTER E WITH DOT ABOVE
            case 0x00ED: return false; // LATIN SMALL LETTER I WITH ACUTE
            case 0x00EE: return false; // LATIN SMALL LETTER I WITH CIRCUMFLEX
            case 0x00EF: return false; // LATIN SMALL LETTER I WITH DIAERESIS
            case 0x00F0: return false; // LATIN SMALL LETTER ETH (Icelandic)
            case 0x0146: return false; // LATIN SMALL LETTER N WITH CEDILLA
            case 0x014D: return false; // LATIN SMALL LETTER O WITH MACRON
            case 0x00F3: return false; // LATIN SMALL LETTER O WITH ACUTE
            case 0x00F4: return false; // LATIN SMALL LETTER O WITH CIRCUMFLEX
            case 0x00F5: return false; // LATIN SMALL LETTER O WITH TILDE
            case 0x00F6: return false; // LATIN SMALL LETTER O WITH DIAERESIS
            case 0x0169: return false; // LATIN SMALL LETTER U WITH TILDE
            case 0x00F8: return false; // LATIN SMALL LETTER O WITH STROKE
            case 0x0173: return false; // LATIN SMALL LETTER U WITH OGONEK
            case 0x00FA: return false; // LATIN SMALL LETTER U WITH ACUTE
            case 0x00FB: return false; // LATIN SMALL LETTER U WITH CIRCUMFLEX
            case 0x00FC: return false; // LATIN SMALL LETTER U WITH DIAERESIS
            case 0x00FD: return false; // LATIN SMALL LETTER Y WITH ACUTE
            case 0x00FE: return false; // LATIN SMALL LETTER THORN (Icelandic)
            case 0x0138: return false; // LATIN SMALL LETTER KRA
        }
        return true;
    }

}