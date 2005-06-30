/* Copyright 2002, 2003, 2005 Elliotte Rusty Harold
   
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

import java.io.IOException;
import java.io.Writer;

/**
 * @author Elliotte Rusty Harold
 * @version 1.1b3
 *
 */
class UnicodeWriter extends TextWriter {

    UnicodeWriter(Writer out, String encoding) {
        super(out, encoding);
    }

    /**
     * @see nu.xom.TextWriter#needsEscaping(char)
     */
    boolean needsEscaping(char c) {
        return false;
    }  


    final void writeMarkup(String s) throws IOException {

         if (normalize) {
             s = normalize(s);
         }
         
         int unicodeStringLength = getUnicodeLengthForMarkup(s);
         if (unicodeStringLength >= 0) {
             out.write(s);
             if (unicodeStringLength > 0) {
                 column += unicodeStringLength;
                 lastCharacterWasSpace = false;
                 skipFollowingLinefeed = false;
                 justBroke=false;
             }
         }
         else { // write character by character
             int length = s.length();
             for (int i=0; i < length; i++) {
                 writeMarkup(s.charAt(i));
             }
         }
         
    }

    
    /*
     * This is tricky. This method is doing two things:
     * 
     * 1. It's counting the number of Unicode characters in s.
     * 2. It's checking to see if this text contains anything
     *    that might need to be escaped. 
     * 
     * If the latter it returns -1; otherwise it returns the number of characters.
     */
    private static int getUnicodeLengthForMarkup(String s) {
        
        int unicodeLength = 0;
        int javaLength = s.length();
        for (int i = 0; i < javaLength; i++) {
            char c = s.charAt(i);
            // ???? lookup table
            switch (c) { 
                // These characters cause an adjustment of 
                // lastCharacterWasSpace, skipFollowingLinefeed, and justBroke
                // They may need to be escaped but only in doctype declarations.
                // Should these have their own writeDoctypeDeclaration method????
                // Also an issue with spaces and such in PIs, XML declaration, comments
                case ' ':  return -1;
                case '\n': return -1;
                case '\t': return -1;
            }
            // Count the low surrogates but skip the high surrogates
            // so surrogate pairs aren't counted twice.
            if (c < 0xD800 || c > 0xDBFF) unicodeLength++;
        }
        return unicodeLength;
        
    }


    final void writeAttributeValue(String s) throws IOException {

         if (normalize) {
             s = normalize(s);
         }
         int unicodeStringLength = getUnicodeLengthForAttributeValue(s);
         if (unicodeStringLength >= 0) { 
             out.write(s);
             if (unicodeStringLength > 0) {
                 column += unicodeStringLength;
                 lastCharacterWasSpace = false;
                 skipFollowingLinefeed = false;
                 justBroke=false;
             }
         }
         else {
             int length = s.length();
             for (int i=0; i < length; i++) {
                 writeAttributeValue(s.charAt(i));
             }
         }

     }

    
    // All three getUnicodeLengthForFOO methods are very similar.
    // Could the code duplciation be elimianted efficiently somehow?
    private static int getUnicodeLengthForAttributeValue(String s) {
         
        int unicodeLength = 0;
        int javaLength = s.length();
        for (int i = 0; i < javaLength; i++) {
            char c = s.charAt(i);
            // ???? lookup table
            switch (c) {
                case ' ':  return -1;
                case '&':  return -1;
                case '<':  return -1;
                case '>':  return -1;
                case '"':  return -1;
                case '\t': return -1;
                case '\n': return -1;
                case '\r': return -1;
            }
            if (c < 0xd800 || c > 0xDBFF) unicodeLength++;
        }
        return unicodeLength;
        
     }

    
}
