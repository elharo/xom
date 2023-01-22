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
 *  ISO 8859-5, ASCII plus Cyrillic (Russian, Byelorussian, etc.)
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0
 *
 */
class ISOCyrillicWriter extends TextWriter {

    ISOCyrillicWriter(Writer out, String encoding) {
        super(out, encoding);
    }

    /**
     * @see nu.xom.TextWriter#needsEscaping(char)
     */
    boolean needsEscaping(char c) {
        if (c <= 0xA0) return false;        
        switch (c) {
            case 0x0401: return false; // CYRILLIC CAPITAL LETTER IO
            case 0x0402: return false; // CYRILLIC CAPITAL LETTER DJE
            case 0x0403: return false; // CYRILLIC CAPITAL LETTER GJE
            case 0x0404: return false; // CYRILLIC CAPITAL LETTER UKRAINIAN IE
            case 0x0405: return false; // CYRILLIC CAPITAL LETTER DZE
            case 0x0406: return false; // CYRILLIC CAPITAL LETTER BYELORUSSIAN-UKRAINIAN I
            case 0x0407: return false; // CYRILLIC CAPITAL LETTER YI
            case 0x0408: return false; // CYRILLIC CAPITAL LETTER JE
            case 0x0409: return false; // CYRILLIC CAPITAL LETTER LJE
            case 0x040A: return false; // CYRILLIC CAPITAL LETTER NJE
            case 0x040B: return false; // CYRILLIC CAPITAL LETTER TSHE
            case 0x040C: return false; // CYRILLIC CAPITAL LETTER KJE
            case 0x040D: return true;  // place holder to enable table lookup
            case 0x040E: return false; // CYRILLIC CAPITAL LETTER SHORT U
            case 0x040F: return false; // CYRILLIC CAPITAL LETTER DZHE
            case 0x0410: return false; // CYRILLIC CAPITAL LETTER A
            case 0x0411: return false; // CYRILLIC CAPITAL LETTER BE
            case 0x0412: return false; // CYRILLIC CAPITAL LETTER VE
            case 0x0413: return false; // CYRILLIC CAPITAL LETTER GHE
            case 0x0414: return false; // CYRILLIC CAPITAL LETTER DE
            case 0x0415: return false; // CYRILLIC CAPITAL LETTER IE
            case 0x0416: return false; // CYRILLIC CAPITAL LETTER ZHE
            case 0x0417: return false; // CYRILLIC CAPITAL LETTER ZE
            case 0x0418: return false; // CYRILLIC CAPITAL LETTER I
            case 0x0419: return false; // CYRILLIC CAPITAL LETTER SHORT I
            case 0x041A: return false; // CYRILLIC CAPITAL LETTER KA
            case 0x041B: return false; // CYRILLIC CAPITAL LETTER EL
            case 0x041C: return false; // CYRILLIC CAPITAL LETTER EM
            case 0x041D: return false; // CYRILLIC CAPITAL LETTER EN
            case 0x041E: return false; // CYRILLIC CAPITAL LETTER O
            case 0x041F: return false; // CYRILLIC CAPITAL LETTER PE
            case 0x0420: return false; // CYRILLIC CAPITAL LETTER ER
            case 0x0421: return false; // CYRILLIC CAPITAL LETTER ES
            case 0x0422: return false; // CYRILLIC CAPITAL LETTER TE
            case 0x0423: return false; // CYRILLIC CAPITAL LETTER U
            case 0x0424: return false; // CYRILLIC CAPITAL LETTER EF
            case 0x0425: return false; // CYRILLIC CAPITAL LETTER HA
            case 0x0426: return false; // CYRILLIC CAPITAL LETTER TSE
            case 0x0427: return false; // CYRILLIC CAPITAL LETTER CHE
            case 0x0428: return false; // CYRILLIC CAPITAL LETTER SHA
            case 0x0429: return false; // CYRILLIC CAPITAL LETTER SHCHA
            case 0x042A: return false; // CYRILLIC CAPITAL LETTER HARD SIGN
            case 0x042B: return false; // CYRILLIC CAPITAL LETTER YERU
            case 0x042C: return false; // CYRILLIC CAPITAL LETTER SOFT SIGN
            case 0x042D: return false; // CYRILLIC CAPITAL LETTER E
            case 0x042E: return false; // CYRILLIC CAPITAL LETTER YU
            case 0x042F: return false; // CYRILLIC CAPITAL LETTER YA
            case 0x0430: return false; // CYRILLIC SMALL LETTER A
            case 0x0431: return false; // CYRILLIC SMALL LETTER BE
            case 0x0432: return false; // CYRILLIC SMALL LETTER VE
            case 0x0433: return false; // CYRILLIC SMALL LETTER GHE
            case 0x0434: return false; // CYRILLIC SMALL LETTER DE
            case 0x0435: return false; // CYRILLIC SMALL LETTER IE
            case 0x0436: return false; // CYRILLIC SMALL LETTER ZHE
            case 0x0437: return false; // CYRILLIC SMALL LETTER ZE
            case 0x0438: return false; // CYRILLIC SMALL LETTER I
            case 0x0439: return false; // CYRILLIC SMALL LETTER SHORT I
            case 0x043A: return false; // CYRILLIC SMALL LETTER KA
            case 0x043B: return false; // CYRILLIC SMALL LETTER EL
            case 0x043C: return false; // CYRILLIC SMALL LETTER EM
            case 0x043D: return false; // CYRILLIC SMALL LETTER EN
            case 0x043E: return false; // CYRILLIC SMALL LETTER O
            case 0x043F: return false; // CYRILLIC SMALL LETTER PE
            case 0x0440: return false; // CYRILLIC SMALL LETTER ER
            case 0x0441: return false; // CYRILLIC SMALL LETTER ES
            case 0x0442: return false; // CYRILLIC SMALL LETTER TE
            case 0x0443: return false; // CYRILLIC SMALL LETTER U
            case 0x0444: return false; // CYRILLIC SMALL LETTER EF
            case 0x0445: return false; // CYRILLIC SMALL LETTER HA
            case 0x0446: return false; // CYRILLIC SMALL LETTER TSE
            case 0x0447: return false; // CYRILLIC SMALL LETTER CHE
            case 0x0448: return false; // CYRILLIC SMALL LETTER SHA
            case 0x0449: return false; // CYRILLIC SMALL LETTER SHCHA
            case 0x044A: return false; // CYRILLIC SMALL LETTER HARD SIGN
            case 0x044B: return false; // CYRILLIC SMALL LETTER YERU
            case 0x044C: return false; // CYRILLIC SMALL LETTER SOFT SIGN
            case 0x044D: return false; // CYRILLIC SMALL LETTER E
            case 0x044E: return false; // CYRILLIC SMALL LETTER YU
            case 0x044F: return false; // CYRILLIC SMALL LETTER YA
            case 0x0450: return true;  // place holder to enable table lookup
            case 0x0451: return false; // CYRILLIC SMALL LETTER IO
            case 0x0452: return false; // CYRILLIC SMALL LETTER DJE
            case 0x0453: return false; // CYRILLIC SMALL LETTER GJE
            case 0x0454: return false; // CYRILLIC SMALL LETTER UKRAINIAN IE
            case 0x0455: return false; // CYRILLIC SMALL LETTER DZE
            case 0x0456: return false; // CYRILLIC SMALL LETTER BYELORUSSIAN-UKRAINIAN I
            case 0x0457: return false; // CYRILLIC SMALL LETTER YI
            case 0x0458: return false; // CYRILLIC SMALL LETTER JE
            case 0x0459: return false; // CYRILLIC SMALL LETTER LJE
            case 0x045A: return false; // CYRILLIC SMALL LETTER NJE
            case 0x045B: return false; // CYRILLIC SMALL LETTER TSHE
            case 0x045C: return false; // CYRILLIC SMALL LETTER KJE
            case 0x045D: return true;  // place holder to enable table lookup
            case 0x045E: return false; // CYRILLIC SMALL LETTER SHORT U
            case 0x045F: return false; // CYRILLIC SMALL LETTER DZHE
        }
        
        switch (c) { // assorted leftover characters
            case 0x00AD: return false; // SOFT HYPHEN
            case 0x00A7: return false; // SECTION SIGN
            case 0x2116: return false; // NUMERO SIGN
        }

        return true;
    }

}