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
   elharo@ibiblio.org. Please include the word "XOM" in the
   subject line. The XOM home page is located at https://xom.nu/
*/

package nu.xom;

import java.io.Writer;

/**
 * <p>
 *   ISO 8859-7, ASCII plus Greek
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.1b2
 *
 */
class ISOGreekWriter extends TextWriter {

    ISOGreekWriter(Writer out, String encoding) {
        super(out, encoding);
    }

    /**
     * @see nu.xom.TextWriter#needsEscaping(char)
     */
    boolean needsEscaping(char c) {
        if (c < 127) return false;     
        switch (c) { // Greek
            case 0x0384: return false; // GREEK TONOS
            case 0x0385: return false; // GREEK DIALYTIKA TONOS
            case 0x0386: return false; // GREEK CAPITAL LETTER ALPHA WITH TONOS
            case 0x0387: return true;  // place holder to enable table lookup
            case 0x0388: return false; // GREEK CAPITAL LETTER EPSILON WITH TONOS
            case 0x0389: return false; // GREEK CAPITAL LETTER ETA WITH TONOS
            case 0x038A: return false; // GREEK CAPITAL LETTER IOTA WITH TONOS
            case 0x038B: return true;  // place holder to enable table lookup
            case 0x038C: return false; // GREEK CAPITAL LETTER OMICRON WITH TONOS
            case 0x038D: return true;  // place holder to enable table lookup
            case 0x038E: return false; // GREEK CAPITAL LETTER UPSILON WITH TONOS
            case 0x038F: return false; // GREEK CAPITAL LETTER OMEGA WITH TONOS
            case 0x0390: return false; // GREEK SMALL LETTER IOTA WITH DIALYTIKA AND TONOS
            case 0x0391: return false; // GREEK CAPITAL LETTER ALPHA
            case 0x0392: return false; // GREEK CAPITAL LETTER BETA
            case 0x0393: return false; // GREEK CAPITAL LETTER GAMMA
            case 0x0394: return false; // GREEK CAPITAL LETTER DELTA
            case 0x0395: return false; // GREEK CAPITAL LETTER EPSILON
            case 0x0396: return false; // GREEK CAPITAL LETTER ZETA
            case 0x0397: return false; // GREEK CAPITAL LETTER ETA
            case 0x0398: return false; // GREEK CAPITAL LETTER THETA
            case 0x0399: return false; // GREEK CAPITAL LETTER IOTA
            case 0x039A: return false; // GREEK CAPITAL LETTER KAPPA
            case 0x039B: return false; // GREEK CAPITAL LETTER LAMDA
            case 0x039C: return false; // GREEK CAPITAL LETTER MU
            case 0x039D: return false; // GREEK CAPITAL LETTER NU
            case 0x039E: return false; // GREEK CAPITAL LETTER XI
            case 0x039F: return false; // GREEK CAPITAL LETTER OMICRON
            case 0x03A0: return false; // GREEK CAPITAL LETTER PI
            case 0x03A1: return false; // GREEK CAPITAL LETTER RHO
            case 0x03A2: return true;  // place holder to enable table lookup
            case 0x03A3: return false; // GREEK CAPITAL LETTER SIGMA
            case 0x03A4: return false; // GREEK CAPITAL LETTER TAU
            case 0x03A5: return false; // GREEK CAPITAL LETTER UPSILON
            case 0x03A6: return false; // GREEK CAPITAL LETTER PHI
            case 0x03A7: return false; // GREEK CAPITAL LETTER CHI
            case 0x03A8: return false; // GREEK CAPITAL LETTER PSI
            case 0x03A9: return false; // GREEK CAPITAL LETTER OMEGA
            case 0x03AA: return false; // GREEK CAPITAL LETTER IOTA WITH DIALYTIKA
            case 0x03AB: return false; // GREEK CAPITAL LETTER UPSILON WITH DIALYTIKA
            case 0x03AC: return false; // GREEK SMALL LETTER ALPHA WITH TONOS
            case 0x03AD: return false; // GREEK SMALL LETTER EPSILON WITH TONOS
            case 0x03AE: return false; // GREEK SMALL LETTER ETA WITH TONOS
            case 0x03AF: return false; // GREEK SMALL LETTER IOTA WITH TONOS
            case 0x03B0: return false; // GREEK SMALL LETTER UPSILON WITH DIALYTIKA AND TONOS
            case 0x03B1: return false; // GREEK SMALL LETTER ALPHA
            case 0x03B2: return false; // GREEK SMALL LETTER BETA
            case 0x03B3: return false; // GREEK SMALL LETTER GAMMA
            case 0x03B4: return false; // GREEK SMALL LETTER DELTA
            case 0x03B5: return false; // GREEK SMALL LETTER EPSILON
            case 0x03B6: return false; // GREEK SMALL LETTER ZETA
            case 0x03B7: return false; // GREEK SMALL LETTER ETA
            case 0x03B8: return false; // GREEK SMALL LETTER THETA
            case 0x03B9: return false; // GREEK SMALL LETTER IOTA
            case 0x03BA: return false; // GREEK SMALL LETTER KAPPA
            case 0x03BB: return false; // GREEK SMALL LETTER LAMDA
            case 0x03BC: return false; // GREEK SMALL LETTER MU
            case 0x03BD: return false; // GREEK SMALL LETTER NU
            case 0x03BE: return false; // GREEK SMALL LETTER XI
            case 0x03BF: return false; // GREEK SMALL LETTER OMICRON
            case 0x03C0: return false; // GREEK SMALL LETTER PI
            case 0x03C1: return false; // GREEK SMALL LETTER RHO
            case 0x03C2: return false; // GREEK SMALL LETTER FINAL SIGMA
            case 0x03C3: return false; // GREEK SMALL LETTER SIGMA
            case 0x03C4: return false; // GREEK SMALL LETTER TAU
            case 0x03C5: return false; // GREEK SMALL LETTER UPSILON
            case 0x03C6: return false; // GREEK SMALL LETTER PHI
            case 0x03C7: return false; // GREEK SMALL LETTER CHI
            case 0x03C8: return false; // GREEK SMALL LETTER PSI
            case 0x03C9: return false; // GREEK SMALL LETTER OMEGA
            case 0x03CA: return false; // GREEK SMALL LETTER IOTA WITH DIALYTIKA
            case 0x03CB: return false; // GREEK SMALL LETTER UPSILON WITH DIALYTIKA
            case 0x03CC: return false; // GREEK SMALL LETTER OMICRON WITH TONOS
            case 0x03CD: return false; // GREEK SMALL LETTER UPSILON WITH TONOS
            case 0x03CE: return false; // GREEK SMALL LETTER OMEGA WITH TONOS
        }
        switch (c) { // overlap with Latin-1
            case 0x00A0: return false; // non-breaking space
            case 0x00A1: return true;  // place holder to enable table lookup
            case 0x00A2: return true;  // place holder to enable table lookup
            case 0x00A3: return false; // POUND SIGN
            case 0x00A4: return true;  // place holder to enable table lookup
            case 0x00A5: return true;  // place holder to enable table lookup
            case 0x00A6: return false; // BROKEN BAR
            case 0x00A7: return false; // SECTION SIGN
            case 0x00A8: return false; // DIAERESIS
            case 0x00A9: return false; // COPYRIGHT SIGN
            case 0x00AA: return true;  // place holder to enable table lookup
            case 0x00AB: return false; // LEFT-POINTING DOUBLE ANGLE QUOTATION MARK
            case 0x00AC: return false; // NOT SIGN
            case 0x00AD: return false; // SOFT HYPHEN
            case 0x00AE: return true;  // place holder to enable table lookup
            case 0x00AF: return true;  // place holder to enable table lookup
            case 0x00B0: return false; // DEGREE SIGN
            case 0x00B1: return false; // PLUS-MINUS SIGN
            case 0x00B2: return false; // SUPERSCRIPT TWO
            case 0x00B3: return false; // SUPERSCRIPT THREE
            case 0x00B4: return true;  // place holder to enable table lookup
            case 0x00B5: return true;  // place holder to enable table lookup
            case 0x00B6: return true;  // place holder to enable table lookup
            case 0x00B7: return false; // MIDDLE DOT
            case 0x00B8: return true;  // place holder to enable table lookup
            case 0x00B9: return true;  // place holder to enable table lookup
            case 0x00BA: return true;  // place holder to enable table lookup
            case 0x00BB: return false; // RIGHT-POINTING DOUBLE ANGLE QUOTATION MARK
            case 0x00BC: return true;  // place holder to enable table lookup
            case 0x00BD: return false; // VULGAR FRACTION ONE HALF
        }
        switch (c) { // assorted characters
            case 0x2015: return false; // HORIZONTAL BAR
            // A bug in Java 1.4 and 1.3 prevents a LEFT and RIGHT 
            // SINGLE QUOTATION MARKs
            // from being correctly output
            // as the actual character in this encoding
            // even though it does exist in the 
            // ISO-8859-7 character set.
            // case 0x2018: return false; // LEFT SINGLE QUOTATION MARK
            // case 0x2019: return false; // RIGHT SINGLE QUOTATION MARK        
        }
        return true;
    }

}