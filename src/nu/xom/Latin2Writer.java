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
class Latin2Writer extends TextWriter {

    public Latin2Writer(Writer out, String encoding) {
        super(out, encoding);
    }

    /**
     * @see nu.xom.TextWriter#needsEscaping(char)
     */
    public boolean needsEscaping(char c) {
        if (c <= 0xA0) return false;        
        switch (c) {
            case 0x00A4: return false; // CURRENCY SIGN
            case 0x00A7: return false; // SECTION SIGN
            case 0x00A8: return false; // DIAERESIS
            case 0x00AD: return false; // SOFT HYPHEN
            case 0x00B0: return false; // DEGREE SIGN
            case 0x00B4: return false; // ACUTE ACCENT
            case 0x00B8: return false; // CEDILLA
            case 0x00C1: return false; // LATIN CAPITAL LETTER A WITH ACUTE
            case 0x00C2: return false; // LATIN CAPITAL LETTER A WITH CIRCUMFLEX
            case 0x00C4: return false; // LATIN CAPITAL LETTER A WITH DIAERESIS
            case 0x00C7: return false; // LATIN CAPITAL LETTER C WITH CEDILLA
            case 0x00C9: return false; // LATIN CAPITAL LETTER E WITH ACUTE
            case 0x00CB: return false; // LATIN CAPITAL LETTER E WITH DIAERESIS
            case 0x00CD: return false; // LATIN CAPITAL LETTER I WITH ACUTE
            case 0x00CE: return false; // LATIN CAPITAL LETTER I WITH CIRCUMFLEX
            case 0x00D3: return false; // LATIN CAPITAL LETTER O WITH ACUTE
            case 0x00D4: return false; // LATIN CAPITAL LETTER O WITH CIRCUMFLEX
            case 0x00D6: return false; // LATIN CAPITAL LETTER O WITH DIAERESIS
            case 0x00D7: return false; // MULTIPLICATION SIGN
            case 0x00DA: return false; // LATIN CAPITAL LETTER U WITH ACUTE
            case 0x00DC: return false; // LATIN CAPITAL LETTER U WITH DIAERESIS
            case 0x00DD: return false; // LATIN CAPITAL LETTER Y WITH ACUTE
            case 0x00DF: return false; // LATIN SMALL LETTER SHARP S
            case 0x00E1: return false; // LATIN SMALL LETTER A WITH ACUTE
            case 0x00E2: return false; // LATIN SMALL LETTER A WITH CIRCUMFLEX
            case 0x00E4: return false; // LATIN SMALL LETTER A WITH DIAERESIS
            case 0x00E7: return false; // LATIN SMALL LETTER C WITH CEDILLA
            case 0x00E9: return false; // LATIN SMALL LETTER E WITH ACUTE
            case 0x00EB: return false; // LATIN SMALL LETTER E WITH DIAERESIS
            case 0x00ED: return false; // LATIN SMALL LETTER I WITH ACUTE
            case 0x00EE: return false; // LATIN SMALL LETTER I WITH CIRCUMFLEX
            case 0x00F3: return false; // LATIN SMALL LETTER O WITH ACUTE
            case 0x00F4: return false; // LATIN SMALL LETTER O WITH CIRCUMFLEX
            case 0x00F6: return false; // LATIN SMALL LETTER O WITH DIAERESIS
            case 0x00F7: return false; // DIVISION SIGN
            case 0x00FA: return false; // LATIN SMALL LETTER U WITH ACUTE
            case 0x00FC: return false; // LATIN SMALL LETTER U WITH DIAERESIS
            case 0x00FD: return false; // LATIN SMALL LETTER Y WITH ACUTE
            case 0x02C7: return false; // CARON
            case 0x02D8: return false; // BREVE
            case 0x02D9: return false; // DOT ABOVE
            case 0x02DB: return false; // OGONEK
            case 0x02DD: return false; // DOUBLE ACUTE ACCENT
            case 0x010C: return false; // LATIN CAPITAL LETTER C WITH CARON
            case 0x010D: return false; // LATIN SMALL LETTER C WITH CARON
            case 0x010E: return false; // LATIN CAPITAL LETTER D WITH CARON
            case 0x010F: return false; // LATIN SMALL LETTER D WITH CARON
            case 0x011A: return false; // LATIN CAPITAL LETTER E WITH CARON
            case 0x011B: return false; // LATIN SMALL LETTER E WITH CARON
            case 0x013A: return false; // LATIN SMALL LETTER L WITH ACUTE
            case 0x013D: return false; // LATIN CAPITAL LETTER L WITH CARON
            case 0x013E: return false; // LATIN SMALL LETTER L WITH CARON
            case 0x015A: return false; // LATIN CAPITAL LETTER S WITH ACUTE
            case 0x015B: return false; // LATIN SMALL LETTER S WITH ACUTE
            case 0x015E: return false; // LATIN CAPITAL LETTER S WITH CEDILLA
            case 0x015F: return false; // LATIN SMALL LETTER S WITH CEDILLA
            case 0x016E: return false; // LATIN CAPITAL LETTER U WITH RING ABOVE
            case 0x016F: return false; // LATIN SMALL LETTER U WITH RING ABOVE
            case 0x017A: return false; // LATIN SMALL LETTER Z WITH ACUTE
            case 0x017B: return false; // LATIN CAPITAL LETTER Z WITH DOT ABOVE
            case 0x017C: return false; // LATIN SMALL LETTER Z WITH DOT ABOVE
            case 0x017D: return false; // LATIN CAPITAL LETTER Z WITH CARON
            case 0x017E: return false; // LATIN SMALL LETTER Z WITH CARON
            case 0x0102: return false; // LATIN CAPITAL LETTER A WITH BREVE
            case 0x0103: return false; // LATIN SMALL LETTER A WITH BREVE
            case 0x0104: return false; // LATIN CAPITAL LETTER A WITH OGONEK
            case 0x0105: return false; // LATIN SMALL LETTER A WITH OGONEK
            case 0x0106: return false; // LATIN CAPITAL LETTER C WITH ACUTE
            case 0x0107: return false; // LATIN SMALL LETTER C WITH ACUTE
            case 0x0110: return false; // LATIN CAPITAL LETTER D WITH STROKE
            case 0x0111: return false; // LATIN SMALL LETTER D WITH STROKE
            case 0x0118: return false; // LATIN CAPITAL LETTER E WITH OGONEK
            case 0x0119: return false; // LATIN SMALL LETTER E WITH OGONEK
            case 0x0139: return false; // LATIN CAPITAL LETTER L WITH ACUTE
            case 0x0141: return false; // LATIN CAPITAL LETTER L WITH STROKE
            case 0x0142: return false; // LATIN SMALL LETTER L WITH STROKE
            case 0x0143: return false; // LATIN CAPITAL LETTER N WITH ACUTE
            case 0x0144: return false; // LATIN SMALL LETTER N WITH ACUTE
            case 0x0147: return false; // LATIN CAPITAL LETTER N WITH CARON
            case 0x0148: return false; // LATIN SMALL LETTER N WITH CARON
            case 0x0150: return false; // LATIN CAPITAL LETTER O WITH DOUBLE ACUTE
            case 0x0151: return false; // LATIN SMALL LETTER O WITH DOUBLE ACUTE
            case 0x0154: return false; // LATIN CAPITAL LETTER R WITH ACUTE
            case 0x0155: return false; // LATIN SMALL LETTER R WITH ACUTE
            case 0x0158: return false; // LATIN CAPITAL LETTER R WITH CARON
            case 0x0159: return false; // LATIN SMALL LETTER R WITH CARON
            case 0x0160: return false; // LATIN CAPITAL LETTER S WITH CARON
            case 0x0161: return false; // LATIN SMALL LETTER S WITH CARON
            case 0x0162: return false; // LATIN CAPITAL LETTER T WITH CEDILLA
            case 0x0163: return false; // LATIN SMALL LETTER T WITH CEDILLA
            case 0x0164: return false; // LATIN CAPITAL LETTER T WITH CARON
            case 0x0165: return false; // LATIN SMALL LETTER T WITH CARON
            case 0x0170: return false; // LATIN CAPITAL LETTER U WITH DOUBLE ACUTE
            case 0x0171: return false; // LATIN SMALL LETTER U WITH DOUBLE ACUTE
            case 0x0179: return false; // LATIN CAPITAL LETTER Z WITH ACUTE
        }
        return true;
    }

}