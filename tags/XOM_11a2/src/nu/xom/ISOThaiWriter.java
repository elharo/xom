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
 *   TIS-620, not quite the same as ISO 8859-11.
 *   TIS-620 does not have the non-breaking space or the C1 controls.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0
 *
 */
class ISOThaiWriter extends TextWriter {

    ISOThaiWriter(Writer out, String encoding) {
        super(out, encoding);
    }

    /**
     * @see nu.xom.TextWriter#needsEscaping(char)
     */
    boolean needsEscaping(char c) {
        
        if (c < 128) return false;
        // C1 controls do not appear to be assigned in this character set
        // Also, according to 
        // http://www.inet.co.th/cyberclub/trin/thairef/tis620-iso10646.html
        // "Special attention should be paid on codepoint A0. Contrary 
        // to many people's belief that TIS 620 defines codepoint A0
        // as no-break space (U+00A0), the standard does not assign any
        // character to this codepoint. Codepoints A0 as well as DB-DE 
        // and FC-FF are not part of the standard. Interpretations of 
        // these unassigned codepoints are implementation specific and 
        // may vary from implementation to implementation. To ensure full 
        // data interchangeability among various applications, it is 
        // suggested that Thai software implementors follows the 
        // TIS 620 standard strictly."
        
        switch (c) {
            case 0x0E01: return false; // THAI CHARACTER KO KAI
            case 0x0E02: return false; // THAI CHARACTER KHO KHAI
            case 0x0E03: return false; // THAI CHARACTER KHO KHUAT
            case 0x0E04: return false; // THAI CHARACTER KHO KHWAI
            case 0x0E05: return false; // THAI CHARACTER KHO KHON
            case 0x0E06: return false; // THAI CHARACTER KHO RAKHANG
            case 0x0E07: return false; // THAI CHARACTER NGO NGU
            case 0x0E08: return false; // THAI CHARACTER CHO CHAN
            case 0x0E09: return false; // THAI CHARACTER CHO CHING
            case 0x0E0A: return false; // THAI CHARACTER CHO CHANG
            case 0x0E0B: return false; // THAI CHARACTER SO SO
            case 0x0E0C: return false; // THAI CHARACTER CHO CHOE
            case 0x0E0D: return false; // THAI CHARACTER YO YING
            case 0x0E0E: return false; // THAI CHARACTER DO CHADA
            case 0x0E0F: return false; // THAI CHARACTER TO PATAK
            case 0x0E10: return false; // THAI CHARACTER THO THAN
            case 0x0E11: return false; // THAI CHARACTER THO NANGMONTHO
            case 0x0E12: return false; // THAI CHARACTER THO PHUTHAO
            case 0x0E13: return false; // THAI CHARACTER NO NEN
            case 0x0E14: return false; // THAI CHARACTER DO DEK
            case 0x0E15: return false; // THAI CHARACTER TO TAO
            case 0x0E16: return false; // THAI CHARACTER THO THUNG
            case 0x0E17: return false; // THAI CHARACTER THO THAHAN
            case 0x0E18: return false; // THAI CHARACTER THO THONG
            case 0x0E19: return false; // THAI CHARACTER NO NU
            case 0x0E1A: return false; // THAI CHARACTER BO BAIMAI
            case 0x0E1B: return false; // THAI CHARACTER PO PLA
            case 0x0E1C: return false; // THAI CHARACTER PHO PHUNG
            case 0x0E1D: return false; // THAI CHARACTER FO FA
            case 0x0E1E: return false; // THAI CHARACTER PHO PHAN
            case 0x0E1F: return false; // THAI CHARACTER FO FAN
            case 0x0E20: return false; // THAI CHARACTER PHO SAMPHAO
            case 0x0E21: return false; // THAI CHARACTER MO MA
            case 0x0E22: return false; // THAI CHARACTER YO YAK
            case 0x0E23: return false; // THAI CHARACTER RO RUA
            case 0x0E24: return false; // THAI CHARACTER RU
            case 0x0E25: return false; // THAI CHARACTER LO LING
            case 0x0E26: return false; // THAI CHARACTER LU
            case 0x0E27: return false; // THAI CHARACTER WO WAEN
            case 0x0E28: return false; // THAI CHARACTER SO SALA
            case 0x0E29: return false; // THAI CHARACTER SO RUSI
            case 0x0E2A: return false; // THAI CHARACTER SO SUA
            case 0x0E2B: return false; // THAI CHARACTER HO HIP
            case 0x0E2C: return false; // THAI CHARACTER LO CHULA
            case 0x0E2D: return false; // THAI CHARACTER O ANG
            case 0x0E2E: return false; // THAI CHARACTER HO NOKHUK
            case 0x0E2F: return false; // THAI CHARACTER PAIYANNOI
            case 0x0E30: return false; // THAI CHARACTER SARA A
            case 0x0E31: return false; // THAI CHARACTER MAI HAN-AKAT
            case 0x0E32: return false; // THAI CHARACTER SARA AA
            case 0x0E33: return false; // THAI CHARACTER SARA AM
            case 0x0E34: return false; // THAI CHARACTER SARA I
            case 0x0E35: return false; // THAI CHARACTER SARA II
            case 0x0E36: return false; // THAI CHARACTER SARA UE
            case 0x0E37: return false; // THAI CHARACTER SARA UEE
            case 0x0E38: return false; // THAI CHARACTER SARA U
            case 0x0E39: return false; // THAI CHARACTER SARA UU
            case 0x0E3A: return false; // THAI CHARACTER PHINTHU
        }
        // optimize by splitting switch into contiguous blocks per
        // Chapter 7 of Java Performance Tuning, Jack Shirazi
        switch (c) { 
            case 0x0E3F: return false; // THAI CURRENCY SYMBOL BAHT
            case 0x0E40: return false; // THAI CHARACTER SARA E
            case 0x0E41: return false; // THAI CHARACTER SARA AE
            case 0x0E42: return false; // THAI CHARACTER SARA O
            case 0x0E43: return false; // THAI CHARACTER SARA AI MAIMUAN
            case 0x0E44: return false; // THAI CHARACTER SARA AI MAIMALAI
            case 0x0E45: return false; // THAI CHARACTER LAKKHANGYAO
            case 0x0E46: return false; // THAI CHARACTER MAIYAMOK
            case 0x0E47: return false; // THAI CHARACTER MAITAIKHU
            case 0x0E48: return false; // THAI CHARACTER MAI EK
            case 0x0E49: return false; // THAI CHARACTER MAI THO
            case 0x0E4A: return false; // THAI CHARACTER MAI TRI
            case 0x0E4B: return false; // THAI CHARACTER MAI CHATTAWA
            case 0x0E4C: return false; // THAI CHARACTER THANTHAKHAT
            case 0x0E4D: return false; // THAI CHARACTER NIKHAHIT
            case 0x0E4E: return false; // THAI CHARACTER YAMAKKAN
            case 0x0E4F: return false; // THAI CHARACTER FONGMAN
            case 0x0E50: return false; // THAI DIGIT ZERO
            case 0x0E51: return false; // THAI DIGIT ONE
            case 0x0E52: return false; // THAI DIGIT TWO
            case 0x0E53: return false; // THAI DIGIT THREE
            case 0x0E54: return false; // THAI DIGIT FOUR
            case 0x0E55: return false; // THAI DIGIT FIVE
            case 0x0E56: return false; // THAI DIGIT SIX
            case 0x0E57: return false; // THAI DIGIT SEVEN
            case 0x0E58: return false; // THAI DIGIT EIGHT
            case 0x0E59: return false; // THAI DIGIT NINE
            case 0x0E5A: return false; // THAI CHARACTER ANGKHANKHU
            case 0x0E5B: return false; // THAI CHARACTER KHOMUT
        }
        
        return true;
        
    }

}