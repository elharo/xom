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
   subject line. The XOM home page is located at http://www.xom.nu/
*/

package nu.xom;

import java.io.Writer;

/**
 * <p>
 *   ISO 8859-6, ASCII plus Arabic
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0
 *
 */
class ISOArabicWriter extends TextWriter {

    ISOArabicWriter(Writer out, String encoding) {
        super(out, encoding);
    }

    /**
     * @see nu.xom.TextWriter#needsEscaping(char)
     */
    boolean needsEscaping(char c) {
        if (c <= 0xA0) return false;        
        switch (c) {
            case 0x060C: return false; // ARABIC COMMA
            case 0x060D: return true;  // place holder to allow table lookup                        
            case 0x060E: return true;  // place holder to allow table lookup                        
            case 0x060F: return true;  // place holder to allow table lookup                        
            case 0x0610: return true;  // place holder to allow table lookup                        
            case 0x0611: return true;  // place holder to allow table lookup                        
            case 0x0612: return true;  // place holder to allow table lookup                        
            case 0x0613: return true;  // place holder to allow table lookup                        
            case 0x0614: return true;  // place holder to allow table lookup                        
            case 0x0615: return true;  // place holder to allow table lookup                        
            case 0x0616: return true;  // place holder to allow table lookup                        
            case 0x0617: return true;  // place holder to allow table lookup                        
            case 0x0618: return true;  // place holder to allow table lookup                        
            case 0x0619: return true;  // place holder to allow table lookup                        
            case 0x061A: return true;  // place holder to allow table lookup                        
            case 0x061B: return false; // ARABIC SEMICOLON
            case 0x061C: return true;  // place holder to allow table lookup            
            case 0x061D: return true;  // place holder to allow table lookup            
            case 0x061E: return true;  // place holder to allow table lookup            
            case 0x061F: return false; // ARABIC QUESTION MARK
            case 0x0620: return true;  // place holder to allow table lookup
            case 0x0621: return false; // ARABIC LETTER HAMZA
            case 0x0622: return false; // ARABIC LETTER ALEF WITH MADDA ABOVE
            case 0x0623: return false; // ARABIC LETTER ALEF WITH HAMZA ABOVE
            case 0x0624: return false; // ARABIC LETTER WAW WITH HAMZA ABOVE
            case 0x0625: return false; // ARABIC LETTER ALEF WITH HAMZA BELOW
            case 0x0626: return false; // ARABIC LETTER YEH WITH HAMZA ABOVE
            case 0x0627: return false; // ARABIC LETTER ALEF
            case 0x0628: return false; // ARABIC LETTER BEH
            case 0x0629: return false; // ARABIC LETTER TEH MARBUTA
            case 0x062A: return false; // ARABIC LETTER TEH
            case 0x062B: return false; // ARABIC LETTER THEH
            case 0x062C: return false; // ARABIC LETTER JEEM
            case 0x062D: return false; // ARABIC LETTER HAH
            case 0x062E: return false; // ARABIC LETTER KHAH
            case 0x062F: return false; // ARABIC LETTER DAL
            case 0x0630: return false; // ARABIC LETTER THAL
            case 0x0631: return false; // ARABIC LETTER REH
            case 0x0632: return false; // ARABIC LETTER ZAIN
            case 0x0633: return false; // ARABIC LETTER SEEN
            case 0x0634: return false; // ARABIC LETTER SHEEN
            case 0x0635: return false; // ARABIC LETTER SAD
            case 0x0636: return false; // ARABIC LETTER DAD
            case 0x0637: return false; // ARABIC LETTER TAH
            case 0x0638: return false; // ARABIC LETTER ZAH
            case 0x0639: return false; // ARABIC LETTER AIN
            case 0x063A: return false; // ARABIC LETTER GHAIN
            case 0x063B: return true;  // place holder to allow table lookup                        
            case 0x063C: return true;  // place holder to allow table lookup                        
            case 0x063D: return true;  // place holder to allow table lookup                        
            case 0x063E: return true;  // place holder to allow table lookup                        
            case 0x063F: return true;  // place holder to allow table lookup                        
            case 0x0640: return false; // ARABIC TATWEEL
            case 0x0641: return false; // ARABIC LETTER FEH
            case 0x0642: return false; // ARABIC LETTER QAF
            case 0x0643: return false; // ARABIC LETTER KAF
            case 0x0644: return false; // ARABIC LETTER LAM
            case 0x0645: return false; // ARABIC LETTER MEEM
            case 0x0646: return false; // ARABIC LETTER NOON
            case 0x0647: return false; // ARABIC LETTER HEH
            case 0x0648: return false; // ARABIC LETTER WAW
            case 0x0649: return false; // ARABIC LETTER ALEF MAKSURA
            case 0x064A: return false; // ARABIC LETTER YEH
            case 0x064B: return false; // ARABIC FATHATAN
            case 0x064C: return false; // ARABIC DAMMATAN
            case 0x064D: return false; // ARABIC KASRATAN
            case 0x064E: return false; // ARABIC FATHA
            case 0x064F: return false; // ARABIC DAMMA
            case 0x0650: return false; // ARABIC KASRA
            case 0x0651: return false; // ARABIC SHADDA
            case 0x0652: return false; // ARABIC SUKUN
        }
        switch (c) { // random overlap with Latin-1
            case 0x00A4: return false; // CURRENCY SIGN
            case 0x00AD: return false; // SOFT HYPHEN
        }
        
        return true;
        
    }

}