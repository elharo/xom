/* Copyright 2002, 2003 Elliotte Rusty Harold
   
   This library is free software; you can redistribute it and/or modify
   it under the terms of version 2.1 of the GNU Lesser General Public 
   License as published by the Free Software Foundation.
   
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
   GNU Lesser General Public License for more details.
   
   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the 
   Free Software Foundation, Inc., 59 Temple Place, Suite 330, 
   Boston, MA 02111-1307  USA
   
   You can contact Elliotte Rusty Harold by sending e-mail to
   elharo@metalab.unc.edu. Please include the word "XOM" in the
   subject line. The XOM home page is located at http://www.xom.nu/
*/

package nu.xom;

import java.io.Writer;

/**
 * <p>
 *   ISO-8859-13, for Latvian and other Baltic languages.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d23
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
        switch (c) {  // Latin-1 overlap
            case 0x00A1: return true;  // place holder to enable table lookup
            case 0x00A2: return false; // CENT SIGN
            case 0x00A3: return false; // POUND SIGN
            case 0x00A4: return false; // CURRENCY SIGN
            case 0x00A5: return true;  // place holder to enable table lookup
            case 0x00A6: return false; // BROKEN BAR
            case 0x00A7: return false; // SECTION SIGN
            case 0x00A8: return true;  // place holder to enable table lookup
            case 0x00A9: return false; // COPYRIGHT SIGN
            case 0x00AA: return true;  // place holder to enable table lookup
            case 0x00AB: return false; // LEFT-POINTING DOUBLE ANGLE QUOTATION MARK
            case 0x00AC: return false; // NOT SIGN
            case 0x00AD: return false; // SOFT HYPHEN
            case 0x00AE: return false; // REGISTERED SIGN
            case 0x00AF: return true;  // place holder to enable table lookup
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
            case 0x00BA: return true;  // place holder to enable table lookup
            case 0x00BB: return false; // RIGHT-POINTING DOUBLE ANGLE QUOTATION MARK
            case 0x00BC: return false; // VULGAR FRACTION ONE QUARTER
            case 0x00BD: return false; // VULGAR FRACTION ONE HALF
            case 0x00BE: return false; // VULGAR FRACTION THREE QUARTERS
            case 0x00BF: return true;  // place holder to enable table lookup
            case 0x00C0: return true;  // place holder to enable table lookup
            case 0x00C1: return true;  // place holder to enable table lookup
            case 0x00C2: return true;  // place holder to enable table lookup
            case 0x00C3: return true;  // place holder to enable table lookup
            case 0x00C4: return false; // LATIN CAPITAL LETTER A WITH DIAERESIS
            case 0x00C5: return false; // LATIN CAPITAL LETTER A WITH RING ABOVE
            case 0x00C6: return false; // LATIN CAPITAL LETTER AE
            case 0x00C7: return true;  // place holder to enable table lookup
            case 0x00C8: return true;  // place holder to enable table lookup
            case 0x00C9: return false; // LATIN CAPITAL LETTER E WITH ACUTE
            case 0x00CA: return true;  // place holder to enable table lookup
            case 0x00CB: return true;  // place holder to enable table lookup
            case 0x00CC: return true;  // place holder to enable table lookup
            case 0x00CD: return true;  // place holder to enable table lookup
            case 0x00CE: return true;  // place holder to enable table lookup
            case 0x00CF: return true;  // place holder to enable table lookup
            case 0x00D0: return true;  // place holder to enable table lookup
            case 0x00D1: return true;  // place holder to enable table lookup
            case 0x00D2: return true;  // place holder to enable table lookup
            case 0x00D3: return false; // LATIN CAPITAL LETTER O WITH ACUTE
            case 0x00D4: return true;  // place holder to enable table lookup
            case 0x00D5: return false; // LATIN CAPITAL LETTER O WITH TILDE
            case 0x00D6: return false; // LATIN CAPITAL LETTER O WITH DIAERESIS
            case 0x00D7: return false; // MULTIPLICATION SIGN
            case 0x00D8: return false; // LATIN CAPITAL LETTER O WITH STROKE
            case 0x00D9: return true;  // place holder to enable table lookup
            case 0x00DA: return true;  // place holder to enable table lookup
            case 0x00DB: return true;  // place holder to enable table lookup
            case 0x00DC: return false; // LATIN CAPITAL LETTER U WITH DIAERESIS
            case 0x00DD: return true;  // place holder to enable table lookup
            case 0x00DE: return true;  // place holder to enable table lookup
            case 0x00DF: return false; // LATIN SMALL LETTER SHARP S (German)
            case 0x00E0: return true;  // place holder to enable table lookup
            case 0x00E1: return true;  // place holder to enable table lookup
            case 0x00E2: return true;  // place holder to enable table lookup
            case 0x00E3: return true;  // place holder to enable table lookup
            case 0x00E4: return false; // LATIN SMALL LETTER A WITH DIAERESIS
            case 0x00E5: return false; // LATIN SMALL LETTER A WITH RING ABOVE
            case 0x00E6: return false; // LATIN SMALL LETTER AE
            case 0x00E7: return true;  // place holder to enable table lookup
            case 0x00E8: return true;  // place holder to enable table lookup
            case 0x00E9: return false; // LATIN SMALL LETTER E WITH ACUTE
            case 0x00EA: return true;  // place holder to enable table lookup
            case 0x00EB: return true;  // place holder to enable table lookup
            case 0x00EC: return true;  // place holder to enable table lookup
            case 0x00ED: return true;  // place holder to enable table lookup
            case 0x00EE: return true;  // place holder to enable table lookup
            case 0x00EF: return true;  // place holder to enable table lookup
            case 0x00F0: return true;  // place holder to enable table lookup
            case 0x00F1: return true;  // place holder to enable table lookup
            case 0x00F2: return true;  // place holder to enable table lookup
            case 0x00F3: return false; // LATIN SMALL LETTER O WITH ACUTE
            case 0x00F4: return true;  // place holder to enable table lookup
            case 0x00F5: return false; // LATIN SMALL LETTER O WITH TILDE
            case 0x00F6: return false; // LATIN SMALL LETTER O WITH DIAERESIS
            case 0x00F7: return false; // DIVISION SIGN
            case 0x00F8: return false; // LATIN SMALL LETTER O WITH STROKE
            case 0x00F9: return true;  // place holder to enable table lookup
            case 0x00FA: return true;  // place holder to enable table lookup
            case 0x00FB: return true;  // place holder to enable table lookup
            case 0x00FC: return false; // LATIN SMALL LETTER U WITH DIAERESIS
            case 0x00FD: return true;  // place holder to enable table lookup
            case 0x00FE: return true;  // place holder to enable table lookup
            case 0x00FF: return true;  // place holder to enable table lookup
            case 0x0100: return false; // LATIN CAPITAL LETTER A WITH MACRON
            case 0x0101: return false; // LATIN SMALL LETTER A WITH MACRON
            case 0x0102: return true;  // place holder to enable table lookup
            case 0x0103: return true;  // place holder to enable table lookup
            case 0x0104: return false; // LATIN CAPITAL LETTER A WITH OGONEK
            case 0x0105: return false; // LATIN SMALL LETTER A WITH OGONEK
            case 0x0106: return false; // LATIN CAPITAL LETTER C WITH ACUTE
            case 0x0107: return false; // LATIN SMALL LETTER C WITH ACUTE
            case 0x0108: return true;  // place holder to enable table lookup
            case 0x0109: return true;  // place holder to enable table lookup
            case 0x010A: return true;  // place holder to enable table lookup
            case 0x010B: return true;  // place holder to enable table lookup
            case 0x010C: return false; // LATIN CAPITAL LETTER C WITH CARON
            case 0x010D: return false; // LATIN SMALL LETTER C WITH CARON
            case 0x010E: return true;  // place holder to enable table lookup
            case 0x010F: return true;  // place holder to enable table lookup
            case 0x0110: return true;  // place holder to enable table lookup
            case 0x0111: return true;  // place holder to enable table lookup
            case 0x0112: return false; // LATIN CAPITAL LETTER E WITH MACRON
            case 0x0113: return false; // LATIN SMALL LETTER E WITH MACRON
            case 0x0114: return true;  // place holder to enable table lookup
            case 0x0115: return true;  // place holder to enable table lookup
            case 0x0116: return false; // LATIN CAPITAL LETTER E WITH DOT ABOVE
            case 0x0117: return false; // LATIN SMALL LETTER E WITH DOT ABOVE
            case 0x0118: return false; // LATIN CAPITAL LETTER E WITH OGONEK
            case 0x0119: return false; // LATIN SMALL LETTER E WITH OGONEK
            case 0x011A: return true;  // place holder to enable table lookup
            case 0x011B: return true;  // place holder to enable table lookup
            case 0x011C: return true;  // place holder to enable table lookup
            case 0x011D: return true;  // place holder to enable table lookup
            case 0x011E: return true;  // place holder to enable table lookup
            case 0x011F: return true;  // place holder to enable table lookup
            case 0x0120: return true;  // place holder to enable table lookup
            case 0x0121: return true;  // place holder to enable table lookup
            case 0x0122: return false; // LATIN CAPITAL LETTER G WITH CEDILLA
            case 0x0123: return false; // LATIN SMALL LETTER G WITH CEDILLA
            case 0x0124: return true;  // place holder to enable table lookup
            case 0x0125: return true;  // place holder to enable table lookup
            case 0x0126: return true;  // place holder to enable table lookup
            case 0x0127: return true;  // place holder to enable table lookup
            case 0x0128: return true;  // place holder to enable table lookup
            case 0x0129: return true;  // place holder to enable table lookup
            case 0x012A: return false; // LATIN CAPITAL LETTER I WITH MACRON
            case 0x012B: return false; // LATIN SMALL LETTER I WITH MACRON
            case 0x012C: return true;  // place holder to enable table lookup
            case 0x012D: return true;  // place holder to enable table lookup
            case 0x012E: return false; // LATIN CAPITAL LETTER I WITH OGONEK
            case 0x012F: return false; // LATIN SMALL LETTER I WITH OGONEK
            case 0x0130: return true;  // place holder to enable table lookup
            case 0x0131: return true;  // place holder to enable table lookup
            case 0x0132: return true;  // place holder to enable table lookup
            case 0x0133: return true;  // place holder to enable table lookup
            case 0x0134: return true;  // place holder to enable table lookup
            case 0x0135: return true;  // place holder to enable table lookup
            case 0x0136: return false; // LATIN CAPITAL LETTER K WITH CEDILLA
            case 0x0137: return false; // LATIN SMALL LETTER K WITH CEDILLA
            case 0x0138: return true;  // place holder to enable table lookup
            case 0x0139: return true;  // place holder to enable table lookup
            case 0x013A: return true;  // place holder to enable table lookup
            case 0x013B: return false; // LATIN CAPITAL LETTER L WITH CEDILLA
            case 0x013C: return false; // LATIN SMALL LETTER L WITH CEDILLA
            case 0x013D: return true;  // place holder to enable table lookup
            case 0x013E: return true;  // place holder to enable table lookup
            case 0x013F: return true;  // place holder to enable table lookup
            case 0x0140: return true;  // place holder to enable table lookup
            case 0x0141: return false; // LATIN CAPITAL LETTER L WITH STROKE
            case 0x0142: return false; // LATIN SMALL LETTER L WITH STROKE
            case 0x0143: return false; // LATIN CAPITAL LETTER N WITH ACUTE
            case 0x0144: return false; // LATIN SMALL LETTER N WITH ACUTE
            case 0x0145: return false; // LATIN CAPITAL LETTER N WITH CEDILLA
            case 0x0146: return false; // LATIN SMALL LETTER N WITH CEDILLA
            case 0x0147: return true;  // place holder to enable table lookup
            case 0x0148: return true;  // place holder to enable table lookup
            case 0x0149: return true;  // place holder to enable table lookup
            case 0x014A: return true;  // place holder to enable table lookup
            case 0x014B: return true;  // place holder to enable table lookup
            case 0x014C: return false; // LATIN CAPITAL LETTER O WITH MACRON
            case 0x014D: return false; // LATIN SMALL LETTER O WITH MACRON
            case 0x014E: return true;  // place holder to enable table lookup
            case 0x014F: return true;  // place holder to enable table lookup
            case 0x0150: return true;  // place holder to enable table lookup
            case 0x0151: return true;  // place holder to enable table lookup
            case 0x0152: return true;  // place holder to enable table lookup
            case 0x0153: return true;  // place holder to enable table lookup
            case 0x0154: return true;  // place holder to enable table lookup
            case 0x0155: return true;  // place holder to enable table lookup
            case 0x0156: return false; // LATIN CAPITAL LETTER R WITH CEDILLA
            case 0x0157: return false; // LATIN SMALL LETTER R WITH CEDILLA
            case 0x0158: return true;  // place holder to enable table lookup
            case 0x0159: return true;  // place holder to enable table lookup
            case 0x015A: return false; // LATIN CAPITAL LETTER S WITH ACUTE
            case 0x015B: return false; // LATIN SMALL LETTER S WITH ACUTE
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
            case 0x016A: return false; // LATIN CAPITAL LETTER U WITH MACRON
            case 0x016B: return false; // LATIN SMALL LETTER U WITH MACRON
            case 0x016C: return true;  // place holder to enable table lookup
            case 0x016D: return true;  // place holder to enable table lookup
            case 0x016E: return true;  // place holder to enable table lookup
            case 0x016F: return true;  // place holder to enable table lookup
            case 0x0170: return true;  // place holder to enable table lookup
            case 0x0171: return true;  // place holder to enable table lookup
            case 0x0172: return false; // LATIN CAPITAL LETTER U WITH OGONEK
            case 0x0173: return false; // LATIN SMALL LETTER U WITH OGONEK
            case 0x0174: return true;  // place holder to enable table lookup
            case 0x0175: return true;  // place holder to enable table lookup
            case 0x0176: return true;  // place holder to enable table lookup
            case 0x0177: return true;  // place holder to enable table lookup
            case 0x0178: return true;  // place holder to enable table lookup
            case 0x0179: return false; // LATIN CAPITAL LETTER Z WITH ACUTE
            case 0x017A: return false; // LATIN SMALL LETTER Z WITH ACUTE
            case 0x017B: return false; // LATIN CAPITAL LETTER Z WITH DOT ABOVE
            case 0x017C: return false; // LATIN SMALL LETTER Z WITH DOT ABOVE
            case 0x017D: return false; // LATIN CAPITAL LETTER Z WITH CARON
            case 0x017E: return false; // LATIN SMALL LETTER Z WITH CARON
        }
        switch (c) {
            case 0x2019: return false; // RIGHT SINGLE QUOTATION MARK
            case 0x201A: return true;  // place holder to enable table lookup
            case 0x201B: return true;  // place holder to enable table lookup
            case 0x201C: return false; // LEFT DOUBLE QUOTATION MARK
            case 0x201D: return false; // RIGHT DOUBLE QUOTATION MARK
            case 0x201E: return false; // DOUBLE LOW-9 QUOTATION MARK
        }
        
        return true;
        
    }

}