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
class Latin7Writer extends TextWriter {

    Latin7Writer(Writer out, String encoding) {
        super(out, encoding);
    }

    /**
     * @see nu.xom.TextWriter#needsEscaping(char)
     */
    boolean needsEscaping(char c) {
        if (c <= 0xA0) return false;        
        switch (c) {
            case 0x201D: return false; // RIGHT DOUBLE QUOTATION MARK
            case 0x00A2: return false; // CENT SIGN
            case 0x00A3: return false; // POUND SIGN
            case 0x00A4: return false; // CURRENCY SIGN
            case 0x201E: return false; // DOUBLE LOW-9 QUOTATION MARK
            case 0x00A6: return false; // BROKEN BAR
            case 0x00A7: return false; // SECTION SIGN
            case 0x00D8: return false; // LATIN CAPITAL LETTER O WITH STROKE
            case 0x00A9: return false; // COPYRIGHT SIGN
            case 0x0156: return false; // LATIN CAPITAL LETTER R WITH CEDILLA
            case 0x00AB: return false; // LEFT-POINTING DOUBLE ANGLE QUOTATION MARK
            case 0x00AC: return false; // NOT SIGN
            case 0x00AD: return false; // SOFT HYPHEN
            case 0x00AE: return false; // REGISTERED SIGN
            case 0x00C6: return false; // LATIN CAPITAL LETTER AE
            case 0x00B0: return false; // DEGREE SIGN
            case 0x00B1: return false; // PLUS-MINUS SIGN
            case 0x00B2: return false; // SUPERSCRIPT TWO
            case 0x00B3: return false; // SUPERSCRIPT THREE
            case 0x201C: return false; // LEFT DOUBLE QUOTATION MARK
            case 0x00B5: return false; // MICRO SIGN
            case 0x00B6: return false; // PILCROW SIGN
            case 0x00B7: return false; // MIDDLE DOT
            case 0x00F8: return false; // LATIN SMALL LETTER O WITH STROKE
            case 0x00B9: return false; // SUPERSCRIPT ONE
            case 0x0157: return false; // LATIN SMALL LETTER R WITH CEDILLA
            case 0x00BB: return false; // RIGHT-POINTING DOUBLE ANGLE QUOTATION MARK
            case 0x00BC: return false; // VULGAR FRACTION ONE QUARTER
            case 0x00BD: return false; // VULGAR FRACTION ONE HALF
            case 0x00BE: return false; // VULGAR FRACTION THREE QUARTERS
            case 0x00E6: return false; // LATIN SMALL LETTER AE
            case 0x0104: return false; // LATIN CAPITAL LETTER A WITH OGONEK
            case 0x012E: return false; // LATIN CAPITAL LETTER I WITH OGONEK
            case 0x0100: return false; // LATIN CAPITAL LETTER A WITH MACRON
            case 0x0106: return false; // LATIN CAPITAL LETTER C WITH ACUTE
            case 0x00C4: return false; // LATIN CAPITAL LETTER A WITH DIAERESIS
            case 0x00C5: return false; // LATIN CAPITAL LETTER A WITH RING ABOVE
            case 0x0118: return false; // LATIN CAPITAL LETTER E WITH OGONEK
            case 0x0112: return false; // LATIN CAPITAL LETTER E WITH MACRON
            case 0x010C: return false; // LATIN CAPITAL LETTER C WITH CARON
            case 0x00C9: return false; // LATIN CAPITAL LETTER E WITH ACUTE
            case 0x0179: return false; // LATIN CAPITAL LETTER Z WITH ACUTE
            case 0x0116: return false; // LATIN CAPITAL LETTER E WITH DOT ABOVE
            case 0x0122: return false; // LATIN CAPITAL LETTER G WITH CEDILLA
            case 0x0136: return false; // LATIN CAPITAL LETTER K WITH CEDILLA
            case 0x012A: return false; // LATIN CAPITAL LETTER I WITH MACRON
            case 0x013B: return false; // LATIN CAPITAL LETTER L WITH CEDILLA
            case 0x0160: return false; // LATIN CAPITAL LETTER S WITH CARON
            case 0x0143: return false; // LATIN CAPITAL LETTER N WITH ACUTE
            case 0x0145: return false; // LATIN CAPITAL LETTER N WITH CEDILLA
            case 0x00D3: return false; // LATIN CAPITAL LETTER O WITH ACUTE
            case 0x014C: return false; // LATIN CAPITAL LETTER O WITH MACRON
            case 0x00D5: return false; // LATIN CAPITAL LETTER O WITH TILDE
            case 0x00D6: return false; // LATIN CAPITAL LETTER O WITH DIAERESIS
            case 0x00D7: return false; // MULTIPLICATION SIGN
            case 0x0172: return false; // LATIN CAPITAL LETTER U WITH OGONEK
            case 0x0141: return false; // LATIN CAPITAL LETTER L WITH STROKE
            case 0x015A: return false; // LATIN CAPITAL LETTER S WITH ACUTE
            case 0x016A: return false; // LATIN CAPITAL LETTER U WITH MACRON
            case 0x00DC: return false; // LATIN CAPITAL LETTER U WITH DIAERESIS
            case 0x017B: return false; // LATIN CAPITAL LETTER Z WITH DOT ABOVE
            case 0x017D: return false; // LATIN CAPITAL LETTER Z WITH CARON
            case 0x00DF: return false; // LATIN SMALL LETTER SHARP S (German)
            case 0x0105: return false; // LATIN SMALL LETTER A WITH OGONEK
            case 0x012F: return false; // LATIN SMALL LETTER I WITH OGONEK
            case 0x0101: return false; // LATIN SMALL LETTER A WITH MACRON
            case 0x0107: return false; // LATIN SMALL LETTER C WITH ACUTE
            case 0x00E4: return false; // LATIN SMALL LETTER A WITH DIAERESIS
            case 0x00E5: return false; // LATIN SMALL LETTER A WITH RING ABOVE
            case 0x0119: return false; // LATIN SMALL LETTER E WITH OGONEK
            case 0x0113: return false; // LATIN SMALL LETTER E WITH MACRON
            case 0x010D: return false; // LATIN SMALL LETTER C WITH CARON
            case 0x00E9: return false; // LATIN SMALL LETTER E WITH ACUTE
            case 0x017A: return false; // LATIN SMALL LETTER Z WITH ACUTE
            case 0x0117: return false; // LATIN SMALL LETTER E WITH DOT ABOVE
            case 0x0123: return false; // LATIN SMALL LETTER G WITH CEDILLA
            case 0x0137: return false; // LATIN SMALL LETTER K WITH CEDILLA
            case 0x012B: return false; // LATIN SMALL LETTER I WITH MACRON
            case 0x013C: return false; // LATIN SMALL LETTER L WITH CEDILLA
            case 0x0161: return false; // LATIN SMALL LETTER S WITH CARON
            case 0x0144: return false; // LATIN SMALL LETTER N WITH ACUTE
            case 0x0146: return false; // LATIN SMALL LETTER N WITH CEDILLA
            case 0x00F3: return false; // LATIN SMALL LETTER O WITH ACUTE
            case 0x014D: return false; // LATIN SMALL LETTER O WITH MACRON
            case 0x00F5: return false; // LATIN SMALL LETTER O WITH TILDE
            case 0x00F6: return false; // LATIN SMALL LETTER O WITH DIAERESIS
            case 0x00F7: return false; // DIVISION SIGN
            case 0x0173: return false; // LATIN SMALL LETTER U WITH OGONEK
            case 0x0142: return false; // LATIN SMALL LETTER L WITH STROKE
            case 0x015B: return false; // LATIN SMALL LETTER S WITH ACUTE
            case 0x016B: return false; // LATIN SMALL LETTER U WITH MACRON
            case 0x00FC: return false; // LATIN SMALL LETTER U WITH DIAERESIS
            case 0x017C: return false; // LATIN SMALL LETTER Z WITH DOT ABOVE
            case 0x017E: return false; // LATIN SMALL LETTER Z WITH CARON
            case 0x2019: return false; // RIGHT SINGLE QUOTATION MARK
        }
        return true;
    }

}