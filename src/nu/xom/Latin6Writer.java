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
 *   ISO-8859-10, for Lithuanian, Estonian, Greenlandic, Icelandic,
 *   Inuit, Lappish, and other Northern European languages.
 * </p>
 *
 * @author Elliotte Rusty Harold
 * @version 1.0
 *
 */
class Latin6Writer extends TextWriter {

    Latin6Writer(Writer out, String encoding) {
        super(out, encoding);
    }

    /**
     * @see nu.xom.TextWriter#needsEscaping(char)
     */
    boolean needsEscaping(char c) {
        if (c <= 0xA0) return false;        
        switch (c) {
            case 0x00A1: return true;  // place holder to enable table lookup
            case 0x00A2: return true;  // place holder to enable table lookup
            case 0x00A3: return true;  // place holder to enable table lookup
            case 0x00A4: return true;  // place holder to enable table lookup
            case 0x00A5: return true;  // place holder to enable table lookup
            case 0x00A6: return true;  // place holder to enable table lookup
            case 0x00A7: return false; // SECTION SIGN
            case 0x00A8: return true;  // place holder to enable table lookup
            case 0x00A9: return true;  // place holder to enable table lookup
            case 0x00AA: return true;  // place holder to enable table lookup
            case 0x00AB: return true;  // place holder to enable table lookup
            case 0x00AC: return true;  // place holder to enable table lookup
            case 0x00AD: return false; // SOFT HYPHEN
            case 0x00AE: return true;  // place holder to enable table lookup
            case 0x00AF: return true;  // place holder to enable table lookup
            case 0x00B0: return false; // DEGREE SIGN
            case 0x00B1: return true;  // place holder to enable table lookup
            case 0x00B2: return true;  // place holder to enable table lookup
            case 0x00B3: return true;  // place holder to enable table lookup
            case 0x00B4: return true;  // place holder to enable table lookup
            case 0x00B5: return true;  // place holder to enable table lookup
            case 0x00B6: return true;  // place holder to enable table lookup
            case 0x00B7: return false; // MIDDLE DOT
            case 0x00B8: return true;  // place holder to enable table lookup
            case 0x00B9: return true;  // place holder to enable table lookup
            case 0x00BA: return true;  // place holder to enable table lookup
            case 0x00BB: return true;  // place holder to enable table lookup
            case 0x00BC: return true;  // place holder to enable table lookup
            case 0x00BD: return true;  // place holder to enable table lookup
            case 0x00BE: return true;  // place holder to enable table lookup
            case 0x00BF: return true;  // place holder to enable table lookup
            case 0x00C0: return true;  // place holder to enable table lookup
            case 0x00C1: return false; // LATIN CAPITAL LETTER A WITH ACUTE
            case 0x00C2: return false; // LATIN CAPITAL LETTER A WITH CIRCUMFLEX
            case 0x00C3: return false; // LATIN CAPITAL LETTER A WITH TILDE
            case 0x00C4: return false; // LATIN CAPITAL LETTER A WITH DIAERESIS
            case 0x00C5: return false; // LATIN CAPITAL LETTER A WITH RING ABOVE
            case 0x00C6: return false; // LATIN CAPITAL LETTER AE
            case 0x00C7: return true;  // place holder to enable table lookup
            case 0x00C8: return true;  // place holder to enable table lookup
            case 0x00C9: return false; // LATIN CAPITAL LETTER E WITH ACUTE
            case 0x00CA: return true;  // place holder to enable table lookup
            case 0x00CB: return false; // LATIN CAPITAL LETTER E WITH DIAERESIS
            case 0x00CC: return true;  // place holder to enable table lookup
            case 0x00CD: return false; // LATIN CAPITAL LETTER I WITH ACUTE
            case 0x00CE: return false; // LATIN CAPITAL LETTER I WITH CIRCUMFLEX
            case 0x00CF: return false; // LATIN CAPITAL LETTER I WITH DIAERESIS
            case 0x00D0: return false; // LATIN CAPITAL LETTER ETH (Icelandic)
            case 0x00D1: return true;  // place holder to enable table lookup
            case 0x00D2: return true;  // place holder to enable table lookup
            case 0x00D3: return false; // LATIN CAPITAL LETTER O WITH ACUTE
            case 0x00D4: return false; // LATIN CAPITAL LETTER O WITH CIRCUMFLEX
            case 0x00D5: return false; // LATIN CAPITAL LETTER O WITH TILDE
            case 0x00D6: return false; // LATIN CAPITAL LETTER O WITH DIAERESIS
            case 0x00D7: return true;  // place holder to enable table lookup
            case 0x00D8: return false; // LATIN CAPITAL LETTER O WITH STROKE
            case 0x00D9: return true;  // place holder to enable table lookup
            case 0x00DA: return false; // LATIN CAPITAL LETTER U WITH ACUTE
            case 0x00DB: return false; // LATIN CAPITAL LETTER U WITH CIRCUMFLEX
            case 0x00DC: return false; // LATIN CAPITAL LETTER U WITH DIAERESIS
            case 0x00DD: return false; // LATIN CAPITAL LETTER Y WITH ACUTE
            case 0x00DE: return false; // LATIN CAPITAL LETTER THORN (Icelandic)
            case 0x00DF: return false; // LATIN SMALL LETTER SHARP S (German)
            case 0x00E0: return true;  // place holder to enable table lookup
            case 0x00E1: return false; // LATIN SMALL LETTER A WITH ACUTE
            case 0x00E2: return false; // LATIN SMALL LETTER A WITH CIRCUMFLEX
            case 0x00E3: return false; // LATIN SMALL LETTER A WITH TILDE
            case 0x00E4: return false; // LATIN SMALL LETTER A WITH DIAERESIS
            case 0x00E5: return false; // LATIN SMALL LETTER A WITH RING ABOVE
            case 0x00E6: return false; // LATIN SMALL LETTER AE
            case 0x00E7: return true;  // place holder to enable table lookup
            case 0x00E8: return true;  // place holder to enable table lookup
            case 0x00E9: return false; // LATIN SMALL LETTER E WITH ACUTE
            case 0x00EA: return true;  // place holder to enable table lookup
            case 0x00EB: return false; // LATIN SMALL LETTER E WITH DIAERESIS
            case 0x00EC: return true;  // place holder to enable table lookup
            case 0x00ED: return false; // LATIN SMALL LETTER I WITH ACUTE
            case 0x00EE: return false; // LATIN SMALL LETTER I WITH CIRCUMFLEX
            case 0x00EF: return false; // LATIN SMALL LETTER I WITH DIAERESIS
            case 0x00F0: return false; // LATIN SMALL LETTER ETH (Icelandic)
            case 0x00F1: return true;  // place holder to enable table lookup
            case 0x00F2: return true;  // place holder to enable table lookup
            case 0x00F3: return false; // LATIN SMALL LETTER O WITH ACUTE
            case 0x00F4: return false; // LATIN SMALL LETTER O WITH CIRCUMFLEX
            case 0x00F5: return false; // LATIN SMALL LETTER O WITH TILDE
            case 0x00F6: return false; // LATIN SMALL LETTER O WITH DIAERESIS
            case 0x00F7: return true;  // place holder to enable table lookup
            case 0x00F8: return false; // LATIN SMALL LETTER O WITH STROKE
            case 0x00F9: return true;  // place holder to enable table lookup
            case 0x00FA: return false; // LATIN SMALL LETTER U WITH ACUTE
            case 0x00FB: return false; // LATIN SMALL LETTER U WITH CIRCUMFLEX
            case 0x00FC: return false; // LATIN SMALL LETTER U WITH DIAERESIS
            case 0x00FD: return false; // LATIN SMALL LETTER Y WITH ACUTE
            case 0x00FE: return false; // LATIN SMALL LETTER THORN (Icelandic)
            case 0x00FF: return true;  // place holder to enable table lookup
            case 0x0100: return false; // LATIN CAPITAL LETTER A WITH MACRON
            case 0x0101: return false; // LATIN SMALL LETTER A WITH MACRON
            case 0x0102: return true;  // place holder to enable table lookup
            case 0x0103: return true;  // place holder to enable table lookup
            case 0x0104: return false; // LATIN CAPITAL LETTER A WITH OGONEK
            case 0x0105: return false; // LATIN SMALL LETTER A WITH OGONEK
            case 0x0106: return true;  // place holder to enable table lookup
            case 0x0107: return true;  // place holder to enable table lookup
            case 0x0108: return true;  // place holder to enable table lookup
            case 0x0109: return true;  // place holder to enable table lookup
            case 0x010A: return true;  // place holder to enable table lookup
            case 0x010B: return true;  // place holder to enable table lookup
            case 0x010C: return false; // LATIN CAPITAL LETTER C WITH CARON
            case 0x010D: return false; // LATIN SMALL LETTER C WITH CARON
            case 0x010E: return true;  // place holder to enable table lookup
            case 0x010F: return true;  // place holder to enable table lookup
            case 0x0110: return false; // LATIN CAPITAL LETTER D WITH STROKE
            case 0x0111: return false; // LATIN SMALL LETTER D WITH STROKE
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
            case 0x0128: return false; // LATIN CAPITAL LETTER I WITH TILDE
            case 0x0129: return false; // LATIN SMALL LETTER I WITH TILDE
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
            case 0x0138: return false; // LATIN SMALL LETTER KRA
            case 0x0139: return true;  // place holder to enable table lookup
            case 0x013A: return true;  // place holder to enable table lookup
            case 0x013B: return false; // LATIN CAPITAL LETTER L WITH CEDILLA
            case 0x013C: return false; // LATIN SMALL LETTER L WITH CEDILLA
            case 0x013D: return true;  // place holder to enable table lookup
            case 0x013E: return true;  // place holder to enable table lookup
            case 0x013F: return true;  // place holder to enable table lookup
            case 0x0140: return true;  // place holder to enable table lookup
            case 0x0141: return true;  // place holder to enable table lookup
            case 0x0142: return true;  // place holder to enable table lookup
            case 0x0143: return true;  // place holder to enable table lookup
            case 0x0144: return true;  // place holder to enable table lookup
            case 0x0145: return false; // LATIN CAPITAL LETTER N WITH CEDILLA
            case 0x0146: return false; // LATIN SMALL LETTER N WITH CEDILLA
            case 0x0147: return true;  // place holder to enable table lookup
            case 0x0148: return true;  // place holder to enable table lookup
            case 0x0149: return true;  // place holder to enable table lookup
            case 0x014A: return false; // LATIN CAPITAL LETTER ENG
            case 0x014B: return false; // LATIN SMALL LETTER ENG
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
            case 0x0166: return false; // LATIN CAPITAL LETTER T WITH STROKE
            case 0x0167: return false; // LATIN SMALL LETTER T WITH STROKE
            case 0x0168: return false; // LATIN CAPITAL LETTER U WITH TILDE
            case 0x0169: return false; // LATIN SMALL LETTER U WITH TILDE
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
            case 0x0179: return true;  // place holder to enable table lookup
            case 0x017A: return true;  // place holder to enable table lookup
            case 0x017B: return true;  // place holder to enable table lookup
            case 0x017C: return true;  // place holder to enable table lookup
            case 0x017D: return false; // LATIN CAPITAL LETTER Z WITH CARON
            case 0x017E: return false; // LATIN SMALL LETTER Z WITH CARON
        }
        
        if (c == 0x2015) return false; // HORIZONTAL BAR
        
        return true;
        
    }

}