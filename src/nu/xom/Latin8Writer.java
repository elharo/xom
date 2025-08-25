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
 *   ISO-8859-14, for Gaelic, Welsh, and other Celtic languages.
 *   Not yet supported by Sun's JDK as of 1.5 alpha. IBM's 1.4.1
 *   JDK does support it.
 * </p>
 *
 * @author Elliotte Rusty Harold
 * @version 1.0
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
        switch (c) {  // Latin-1 overlap
            case 0x00A3: return false; // POUND SIGN
            case 0x00A4: return true;  // place holder to enable table lookup
            case 0x00A5: return true;  // place holder to enable table lookup
            case 0x00A6: return true;  // place holder to enable table lookup
            case 0x00A7: return false; // SECTION SIGN
            case 0x00A8: return true;  // place holder to enable table lookup
            case 0x00A9: return false; // COPYRIGHT SIGN
            case 0x00AA: return true;  // place holder to enable table lookup
            case 0x00AB: return true;  // place holder to enable table lookup
            case 0x00AC: return true;  // place holder to enable table lookup
            case 0x00AD: return false; // SOFT HYPHEN
            case 0x00AE: return false; // REGISTERED SIGN
            case 0x00AF: return true;  // place holder to enable table lookup
            case 0x00B0: return true;  // place holder to enable table lookup
            case 0x00B1: return true;  // place holder to enable table lookup
            case 0x00B2: return true;  // place holder to enable table lookup
            case 0x00B3: return true;  // place holder to enable table lookup
            case 0x00B4: return true;  // place holder to enable table lookup
            case 0x00B5: return true;  // place holder to enable table lookup
            case 0x00B6: return false; // PILCROW SIGN
            case 0x00B7: return true;  // place holder to enable table lookup
            case 0x00B8: return true;  // place holder to enable table lookup
            case 0x00B9: return true;  // place holder to enable table lookup
            case 0x00BA: return true;  // place holder to enable table lookup
            case 0x00BB: return true;  // place holder to enable table lookup
            case 0x00BC: return true;  // place holder to enable table lookup
            case 0x00BD: return true;  // place holder to enable table lookup
            case 0x00BE: return true;  // place holder to enable table lookup
            case 0x00BF: return true;  // place holder to enable table lookup
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
            case 0x00D0: return true;  // place holder to enable table lookup
            case 0x00D1: return false; // LATIN CAPITAL LETTER N WITH TILDE
            case 0x00D2: return false; // LATIN CAPITAL LETTER O WITH GRAVE
            case 0x00D3: return false; // LATIN CAPITAL LETTER O WITH ACUTE
            case 0x00D4: return false; // LATIN CAPITAL LETTER O WITH CIRCUMFLEX
            case 0x00D5: return false; // LATIN CAPITAL LETTER O WITH TILDE
            case 0x00D6: return false; // LATIN CAPITAL LETTER O WITH DIAERESIS
            case 0x00D7: return true;  // place holder to enable table lookup
            case 0x00D8: return false; // LATIN CAPITAL LETTER O WITH STROKE
            case 0x00D9: return false; // LATIN CAPITAL LETTER U WITH GRAVE
            case 0x00DA: return false; // LATIN CAPITAL LETTER U WITH ACUTE
            case 0x00DB: return false; // LATIN CAPITAL LETTER U WITH CIRCUMFLEX
            case 0x00DC: return false; // LATIN CAPITAL LETTER U WITH DIAERESIS
            case 0x00DD: return false; // LATIN CAPITAL LETTER Y WITH ACUTE
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
            case 0x00F8: return false; // LATIN SMALL LETTER O WITH STROKE
            case 0x00F9: return false; // LATIN SMALL LETTER U WITH GRAVE
            case 0x00FA: return false; // LATIN SMALL LETTER U WITH ACUTE
            case 0x00FB: return false; // LATIN SMALL LETTER U WITH CIRCUMFLEX
            case 0x00FC: return false; // LATIN SMALL LETTER U WITH DIAERESIS
            case 0x00FD: return false; // LATIN SMALL LETTER Y WITH ACUTE
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
            case 0x010A: return false; // LATIN CAPITAL LETTER C WITH DOT ABOVE
            case 0x010B: return false; // LATIN SMALL LETTER C WITH DOT ABOVE
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
            case 0x011E: return true;  // place holder to enable table lookup
            case 0x011F: return true;  // place holder to enable table lookup       
            case 0x0120: return false; // LATIN CAPITAL LETTER G WITH DOT ABOVE
            case 0x0121: return false; // LATIN SMALL LETTER G WITH DOT ABOVE
        }
        switch (c) {         
            case 0x0174: return false; // LATIN CAPITAL LETTER W WITH CIRCUMFLEX
            case 0x0175: return false; // LATIN SMALL LETTER W WITH CIRCUMFLEX
            case 0x0176: return false; // LATIN CAPITAL LETTER Y WITH CIRCUMFLEX
            case 0x0177: return false; // LATIN SMALL LETTER Y WITH CIRCUMFLEX
            case 0x0178: return false; // LATIN CAPITAL LETTER Y WITH DIAERESIS
        }
        switch (c) {         
            case 0x1E02: return false; // LATIN CAPITAL LETTER B WITH DOT ABOVE
            case 0x1E03: return false; // LATIN SMALL LETTER B WITH DOT ABOVE
            case 0x1E04: return true;  // place holder to enable table lookup
            case 0x1E05: return true;  // place holder to enable table lookup
            case 0x1E06: return true;  // place holder to enable table lookup
            case 0x1E07: return true;  // place holder to enable table lookup
            case 0x1E08: return true;  // place holder to enable table lookup
            case 0x1E09: return true;  // place holder to enable table lookup
            case 0x1E0A: return false; // LATIN CAPITAL LETTER D WITH DOT ABOVE
            case 0x1E0B: return false; // LATIN SMALL LETTER D WITH DOT ABOVE
            case 0x1E0C: return true;  // place holder to enable table lookup
            case 0x1E0D: return true;  // place holder to enable table lookup
            case 0x1E0E: return true;  // place holder to enable table lookup
            case 0x1E0F: return true;  // place holder to enable table lookup
            case 0x1E10: return true;  // place holder to enable table lookup
            case 0x1E11: return true;  // place holder to enable table lookup
            case 0x1E12: return true;  // place holder to enable table lookup
            case 0x1E13: return true;  // place holder to enable table lookup
            case 0x1E14: return true;  // place holder to enable table lookup
            case 0x1E15: return true;  // place holder to enable table lookup
            case 0x1E16: return true;  // place holder to enable table lookup
            case 0x1E17: return true;  // place holder to enable table lookup
            case 0x1E18: return true;  // place holder to enable table lookup
            case 0x1E19: return true;  // place holder to enable table lookup
            case 0x1E1A: return true;  // place holder to enable table lookup
            case 0x1E1B: return true;  // place holder to enable table lookup
            case 0x1E1C: return true;  // place holder to enable table lookup
            case 0x1E1D: return true;  // place holder to enable table lookup
            case 0x1E1E: return false; // LATIN CAPITAL LETTER F WITH DOT ABOVE
            case 0x1E1F: return false; // LATIN SMALL LETTER F WITH DOT ABOVE
            case 0x1E20: return true;  // place holder to enable table lookup
            case 0x1E21: return true;  // place holder to enable table lookup
            case 0x1E22: return true;  // place holder to enable table lookup
            case 0x1E23: return true;  // place holder to enable table lookup
            case 0x1E24: return true;  // place holder to enable table lookup
            case 0x1E25: return true;  // place holder to enable table lookup
            case 0x1E26: return true;  // place holder to enable table lookup
            case 0x1E27: return true;  // place holder to enable table lookup
            case 0x1E28: return true;  // place holder to enable table lookup
            case 0x1E29: return true;  // place holder to enable table lookup
            case 0x1E2A: return true;  // place holder to enable table lookup
            case 0x1E2B: return true;  // place holder to enable table lookup
            case 0x1E2C: return true;  // place holder to enable table lookup
            case 0x1E2D: return true;  // place holder to enable table lookup
            case 0x1E2E: return true;  // place holder to enable table lookup
            case 0x1E2F: return true;  // place holder to enable table lookup
            case 0x1E30: return true;  // place holder to enable table lookup
            case 0x1E31: return true;  // place holder to enable table lookup
            case 0x1E32: return true;  // place holder to enable table lookup
            case 0x1E33: return true;  // place holder to enable table lookup
            case 0x1E34: return true;  // place holder to enable table lookup
            case 0x1E35: return true;  // place holder to enable table lookup
            case 0x1E36: return true;  // place holder to enable table lookup
            case 0x1E37: return true;  // place holder to enable table lookup
            case 0x1E38: return true;  // place holder to enable table lookup
            case 0x1E39: return true;  // place holder to enable table lookup
            case 0x1E3A: return true;  // place holder to enable table lookup
            case 0x1E3B: return true;  // place holder to enable table lookup
            case 0x1E3C: return true;  // place holder to enable table lookup
            case 0x1E3D: return true;  // place holder to enable table lookup
            case 0x1E3E: return true;  // place holder to enable table lookup
            case 0x1E3F: return true;  // place holder to enable table lookup        
            case 0x1E40: return false; // LATIN CAPITAL LETTER M WITH DOT ABOVE
            case 0x1E41: return false; // LATIN SMALL LETTER M WITH DOT ABOVE
            case 0x1E42: return true;  // place holder to enable table lookup
            case 0x1E43: return true;  // place holder to enable table lookup
            case 0x1E44: return true;  // place holder to enable table lookup
            case 0x1E45: return true;  // place holder to enable table lookup
            case 0x1E46: return true;  // place holder to enable table lookup
            case 0x1E47: return true;  // place holder to enable table lookup
            case 0x1E48: return true;  // place holder to enable table lookup
            case 0x1E49: return true;  // place holder to enable table lookup
            case 0x1E4A: return true;  // place holder to enable table lookup
            case 0x1E4B: return true;  // place holder to enable table lookup
            case 0x1E4C: return true;  // place holder to enable table lookup
            case 0x1E4D: return true;  // place holder to enable table lookup
            case 0x1E4E: return true;  // place holder to enable table lookup
            case 0x1E4F: return true;  // place holder to enable table lookup
            case 0x1E50: return true;  // place holder to enable table lookup
            case 0x1E51: return true;  // place holder to enable table lookup
            case 0x1E52: return true;  // place holder to enable table lookup
            case 0x1E53: return true;  // place holder to enable table lookup
            case 0x1E54: return true;  // place holder to enable table lookup
            case 0x1E55: return true;  // place holder to enable table lookup         
            case 0x1E56: return false; // LATIN CAPITAL LETTER P WITH DOT ABOVE
            case 0x1E57: return false; // LATIN SMALL LETTER P WITH DOT ABOVE
            case 0x1E58: return true;  // place holder to enable table lookup
            case 0x1E59: return true;  // place holder to enable table lookup
            case 0x1E5A: return true;  // place holder to enable table lookup
            case 0x1E5B: return true;  // place holder to enable table lookup
            case 0x1E5C: return true;  // place holder to enable table lookup
            case 0x1E5D: return true;  // place holder to enable table lookup
            case 0x1E5E: return true;  // place holder to enable table lookup
            case 0x1E5F: return true;  // place holder to enable table lookup
            case 0x1E60: return false; // LATIN CAPITAL LETTER S WITH DOT ABOVE
            case 0x1E61: return false; // LATIN SMALL LETTER S WITH DOT ABOVE
            case 0x1E62: return true;  // place holder to enable table lookup
            case 0x1E63: return true;  // place holder to enable table lookup
            case 0x1E64: return true;  // place holder to enable table lookup
            case 0x1E65: return true;  // place holder to enable table lookup
            case 0x1E66: return true;  // place holder to enable table lookup
            case 0x1E67: return true;  // place holder to enable table lookup
            case 0x1E68: return true;  // place holder to enable table lookup
            case 0x1E69: return true;  // place holder to enable table lookup
            case 0x1E6A: return false; // LATIN CAPITAL LETTER T WITH DOT ABOVE
            case 0x1E6B: return false; // LATIN SMALL LETTER T WITH DOT ABOVE
            case 0x1E6C: return true;  // place holder to enable table lookup
            case 0x1E6D: return true;  // place holder to enable table lookup
            case 0x1E6E: return true;  // place holder to enable table lookup
            case 0x1E6F: return true;  // place holder to enable table lookup
            case 0x1E70: return true;  // place holder to enable table lookup
            case 0x1E71: return true;  // place holder to enable table lookup
            case 0x1E72: return true;  // place holder to enable table lookup
            case 0x1E73: return true;  // place holder to enable table lookup
            case 0x1E74: return true;  // place holder to enable table lookup
            case 0x1E75: return true;  // place holder to enable table lookup
            case 0x1E76: return true;  // place holder to enable table lookup
            case 0x1E77: return true;  // place holder to enable table lookup
            case 0x1E78: return true;  // place holder to enable table lookup
            case 0x1E79: return true;  // place holder to enable table lookup
            case 0x1E7A: return true;  // place holder to enable table lookup
            case 0x1E7B: return true;  // place holder to enable table lookup
            case 0x1E7C: return true;  // place holder to enable table lookup
            case 0x1E7D: return true;  // place holder to enable table lookup
            case 0x1E7E: return true;  // place holder to enable table lookup
            case 0x1E7F: return true;  // place holder to enable table lookup
            case 0x1E80: return false; // LATIN CAPITAL LETTER W WITH GRAVE
            case 0x1E81: return false; // LATIN SMALL LETTER W WITH GRAVE
            case 0x1E82: return false; // LATIN CAPITAL LETTER W WITH ACUTE
            case 0x1E83: return false; // LATIN SMALL LETTER W WITH ACUTE
            case 0x1E84: return false; // LATIN CAPITAL LETTER W WITH DIAERESIS
            case 0x1E85: return false; // LATIN SMALL LETTER W WITH DIAERESIS
        }
        switch (c) {         
            case 0x1EF2: return false; // LATIN CAPITAL LETTER Y WITH GRAVE
            case 0x1EF3: return false; // LATIN SMALL LETTER Y WITH GRAVE
        }
        
        return true;
        
    }

}
