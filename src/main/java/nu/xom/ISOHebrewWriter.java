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
 *   ISO 8859-8, ASCII plus Hebrew
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0
 *
 */
class ISOHebrewWriter extends TextWriter {

    ISOHebrewWriter(Writer out, String encoding) {
        super(out, encoding);
    }

    /**
     * @see nu.xom.TextWriter#needsEscaping(char)
     */
    boolean needsEscaping(char c) { 
        if (c <= 0xA0) return false;        
        switch (c) { // characters shared with Latin-1
            case 0x00A2: return false; // CENT SIGN
            case 0x00A3: return false; // POUND SIGN
            case 0x00A4: return false; // CURRENCY SIGN
            case 0x00A5: return false; // YEN SIGN
            case 0x00A6: return false; // BROKEN BAR
            case 0x00A7: return false; // SECTION SIGN
            case 0x00A8: return false; // DIAERESIS
            case 0x00A9: return false; // COPYRIGHT SIGN
            case 0x00AB: return false; // LEFT-POINTING DOUBLE ANGLE QUOTATION MARK
            case 0x00AC: return false; // NOT SIGN
            case 0x00AD: return false; // SOFT HYPHEN
            case 0x00AE: return false; // REGISTERED SIGN
            // A bug in Java prevents a macron from being correctly 
            // output as the actual character in this encoding even
            // though it does exist in the ISO-8859-8 character set.
            // See JDC bug 4760496
            // http://developer.java.sun.com/developer/bugParade/bugs/4760496.html
            // They have marked this as fixed in Tiger (i.e. Java 1.5)
            // I'm not going to fix it here yet though, because I'd 
            // prefer XOM to work correctly with earlier versions of
            // of Java; and it's not incorrect to output a character 
            // reference even if you don't have to. It is an issue if a
            // macron is used in a a comment or a processing 
            // instruction though. The macron is not a name character
            // so that's not an issue though.
            case 0x00AF: return true;  // MACRON
            case 0x00B0: return false; // DEGREE SIGN
            case 0x00B1: return false; // PLUS-MINUS SIGN
            case 0x00B2: return false; // SUPERSCRIPT TWO
            case 0x00B3: return false; // SUPERSCRIPT THREE
            case 0x00B4: return false; // ACUTE ACCENT
            case 0x00B5: return false; // MICRO SIGN
            case 0x00B6: return false; // PILCROW SIGN
            case 0x00B7: return false; // MIDDLE DOT
            case 0x00B8: return false; // CEDILLA
            case 0x00B9: return false; // SUPERSCRIPT ONE
            case 0x00BA: return true;  // place holder to allow optimization of switch statement
            case 0x00BB: return false; // RIGHT-POINTING DOUBLE ANGLE QUOTATION MARK
            case 0x00BC: return false; // VULGAR FRACTION ONE QUARTER
            case 0x00BD: return false; // VULGAR FRACTION ONE HALF
            case 0x00BE: return false; // VULGAR FRACTION THREE QUARTERS
        }
        switch (c) {  // Unicode Hebrew block
            case 0x05D0: return false; // HEBREW LETTER ALEF
            case 0x05D1: return false; // HEBREW LETTER BET
            case 0x05D2: return false; // HEBREW LETTER GIMEL
            case 0x05D3: return false; // HEBREW LETTER DALET
            case 0x05D4: return false; // HEBREW LETTER HE
            case 0x05D5: return false; // HEBREW LETTER VAV
            case 0x05D6: return false; // HEBREW LETTER ZAYIN
            case 0x05D7: return false; // HEBREW LETTER HET
            case 0x05D8: return false; // HEBREW LETTER TET
            case 0x05D9: return false; // HEBREW LETTER YOD
            case 0x05DA: return false; // HEBREW LETTER FINAL KAF
            case 0x05DB: return false; // HEBREW LETTER KAF
            case 0x05DC: return false; // HEBREW LETTER LAMED
            case 0x05DD: return false; // HEBREW LETTER FINAL MEM
            case 0x05DE: return false; // HEBREW LETTER MEM
            case 0x05DF: return false; // HEBREW LETTER FINAL NUN
            case 0x05E0: return false; // HEBREW LETTER NUN
            case 0x05E1: return false; // HEBREW LETTER SAMEKH
            case 0x05E2: return false; // HEBREW LETTER AYIN
            case 0x05E3: return false; // HEBREW LETTER FINAL PE
            case 0x05E4: return false; // HEBREW LETTER PE
            case 0x05E5: return false; // HEBREW LETTER FINAL TSADI
            case 0x05E6: return false; // HEBREW LETTER TSADI
            case 0x05E7: return false; // HEBREW LETTER QOF
            case 0x05E8: return false; // HEBREW LETTER RESH
            case 0x05E9: return false; // HEBREW LETTER SHIN
            case 0x05EA: return false; // HEBREW LETTER TAV
        }
        
        switch (c) {  // a few random, out of order characters
            case 0x00D7: return false; // MULTIPLICATION SIGN
            case 0x00F7: return false; // DIVISION SIGN
            // A bug in Java prevents a LEFT-TO-RIGHT MARK 
            // and RIGHT-TO-LEFT MARK from being correctly output
            // as the actual character in this encoding even
            // though it does exist in the ISO-8859-8 character set.
            // See JDC bug 4758951
            // http://developer.java.sun.com/developer/bugParade/bugs/4758951.html
            // They have marked this as fixed in Tiger (i.e. Java 1.5)
            // case 0x200E: return false; // LEFT-TO-RIGHT MARK
            // case 0x200F: return false; // RIGHT-TO-LEFT MARK
            case 0x2017: return false; // DOUBLE LOW LINE
        }
        
        return true;
        
    }

}