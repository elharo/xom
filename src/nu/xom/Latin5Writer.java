/* Copyright 2002-2004 Elliotte Rusty Harold
   
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
   elharo@ibiblio.org. Please include the word "XOM" in the
   subject line. The XOM home page is located at http://www.xom.nu/
*/

package nu.xom;

import java.io.Writer;

/**
 * <p>
 *   ISO 8859-9 for Turkish. 
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0
 * 
 *
 */
class Latin5Writer extends TextWriter {

    Latin5Writer(Writer out, String encoding) {
        super(out, encoding);
    }

    /**
     * @see nu.xom.TextWriter#needsEscaping(char)
     */
    boolean needsEscaping(char c) {
        if (c <= 0xCF) return false;        
        switch (c) {
            case 0x00D0: return true;  // place holder to enable table lookup
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
            case 0x00DD: return true;  // place holder to enable table lookup
            case 0x00DE: return true;  // place holder to enable table lookup
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
            case 0x00F0: return true;  // place holder to enable table lookup
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
            case 0x00FD: return true;  // place holder to enable table lookup
            case 0x00FE: return true;  // place holder to enable table lookup
            case 0x00FF: return false; // LATIN SMALL LETTER Y WITH DIAERESIS
            case 0x0100: return true;  // place holder to enable table lookup
            case 0x0101: return true;  // place holder to enable table lookup
            case 0x0102: return true;  // place holder to enable table lookup
            case 0x0103: return true;  // place holder to enable table lookup
            case 0x0104: return true;  // place holder to enable table lookup
            case 0x0105: return true;  // place holder to enable table lookup
            case 0x0106: return true;  // place holder to enable table lookup
            case 0x0107: return true;  // place holder to enable table lookup
            case 0x0108: return true;  // place holder to enable table lookup
            case 0x0109: return true;  // place holder to enable table lookup
            case 0x010A: return true;  // place holder to enable table lookup
            case 0x010B: return true;  // place holder to enable table lookup
            case 0x010C: return true;  // place holder to enable table lookup
            case 0x010D: return true;  // place holder to enable table lookup
            case 0x010E: return true;  // place holder to enable table lookup
            case 0x010F: return true;  // place holder to enable table lookup
            case 0x0110: return true;  // place holder to enable table lookup
            case 0x0111: return true;  // place holder to enable table lookup
            case 0x0112: return true;  // place holder to enable table lookup
            case 0x0113: return true;  // place holder to enable table lookup
            case 0x0114: return true;  // place holder to enable table lookup
            case 0x0115: return true;  // place holder to enable table lookup
            case 0x0116: return true;  // place holder to enable table lookup
            case 0x0117: return true;  // place holder to enable table lookup
            case 0x0118: return true;  // place holder to enable table lookup
            case 0x0119: return true;  // place holder to enable table lookup
            case 0x011A: return true;  // place holder to enable table lookup
            case 0x011B: return true;  // place holder to enable table lookup
            case 0x011C: return true;  // place holder to enable table lookup
            case 0x011D: return true;  // place holder to enable table lookup
            // Turkish letters
            case 0x011E: return false; // LATIN CAPITAL LETTER G WITH BREVE
            case 0x011F: return false; // LATIN SMALL LETTER G WITH BREVE
            case 0x0120: return true;  // place holder to enable table lookup
            case 0x0121: return true;  // place holder to enable table lookup
            case 0x0122: return true;  // place holder to enable table lookup
            case 0x0123: return true;  // place holder to enable table lookup
            case 0x0124: return true;  // place holder to enable table lookup
            case 0x0125: return true;  // place holder to enable table lookup
            case 0x0126: return true;  // place holder to enable table lookup
            case 0x0127: return true;  // place holder to enable table lookup
            case 0x0128: return true;  // place holder to enable table lookup
            case 0x0129: return true;  // place holder to enable table lookup
            case 0x012A: return true;  // place holder to enable table lookup
            case 0x012B: return true;  // place holder to enable table lookup
            case 0x012C: return true;  // place holder to enable table lookup
            case 0x012D: return true;  // place holder to enable table lookup
            case 0x012E: return true;  // place holder to enable table lookup
            case 0x012F: return true;  // place holder to enable table lookup
            case 0x0130: return false; // LATIN CAPITAL LETTER I WITH DOT ABOVE
            case 0x0131: return false; // LATIN SMALL LETTER DOTLESS I
            case 0x0132: return true;  // place holder to enable table lookup
            case 0x0133: return true;  // place holder to enable table lookup
            case 0x0134: return true;  // place holder to enable table lookup
            case 0x0135: return true;  // place holder to enable table lookup
            case 0x0136: return true;  // place holder to enable table lookup
            case 0x0137: return true;  // place holder to enable table lookup
            case 0x0138: return true;  // place holder to enable table lookup
            case 0x0139: return true;  // place holder to enable table lookup
            case 0x013A: return true;  // place holder to enable table lookup
            case 0x013B: return true;  // place holder to enable table lookup
            case 0x013C: return true;  // place holder to enable table lookup
            case 0x013D: return true;  // place holder to enable table lookup
            case 0x013E: return true;  // place holder to enable table lookup
            case 0x013F: return true;  // place holder to enable table lookup
            case 0x0140: return true;  // place holder to enable table lookup
            case 0x0141: return true;  // place holder to enable table lookup
            case 0x0142: return true;  // place holder to enable table lookup
            case 0x0143: return true;  // place holder to enable table lookup
            case 0x0144: return true;  // place holder to enable table lookup
            case 0x0145: return true;  // place holder to enable table lookup
            case 0x0146: return true;  // place holder to enable table lookup
            case 0x0147: return true;  // place holder to enable table lookup
            case 0x0148: return true;  // place holder to enable table lookup
            case 0x0149: return true;  // place holder to enable table lookup
            case 0x014A: return true;  // place holder to enable table lookup
            case 0x014B: return true;  // place holder to enable table lookup
            case 0x014C: return true;  // place holder to enable table lookup
            case 0x014D: return true;  // place holder to enable table lookup
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
            case 0x015E: return false; // LATIN CAPITAL LETTER S WITH CEDILLA
            case 0x015F: return false; // LATIN SMALL LETTER S WITH CEDILLA
        }
        
        return true;
        
    }

}