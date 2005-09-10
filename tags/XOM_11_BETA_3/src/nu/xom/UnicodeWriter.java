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
final class UnicodeWriter extends TextWriter {

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
            if (c <= ' ') { 
                // Really we're testing only for \t, \n, and space here.
                // However all other characters less than or equal to 32
                // can't appear in markup sections.
                // These characters cause an adjustment of 
                // lastCharacterWasSpace, skipFollowingLinefeed, and justBroke
                // They may need to be escaped but only in doctype declarations.
                // Should these have their own writeDoctypeDeclaration method????
                // Also an issue with spaces and such in PIs, XML declaration, comments
                return -1;
            }
            // Count the low surrogates but skip the high surrogates
            // so surrogate pairs aren't counted twice.
            else if (c < 0xD800 || c > 0xDBFF) unicodeLength++;
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
    // Could the code duplciation be eliminated efficiently somehow?
    private static int getUnicodeLengthForAttributeValue(String s) {
         
        int unicodeLength = 0;
        int javaLength = s.length();
        for (int i = 0; i < javaLength; i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\t': return -1;
                case '\n': return -1;
                case   11: // unreachable
                case   12: throw new XMLException("Bad character snuck into document");
                case '\r': return -1;
                case 14: // unreachable
                case 15: // unreachable
                case 16: // unreachable
                case 17: // unreachable
                case 18: // unreachable
                case 19: // unreachable
                case 20: // unreachable
                case 21: // unreachable
                case 22: // unreachable
                case 23: // unreachable
                case 24: // unreachable
                case 25: // unreachable
                case 26: // unreachable
                case 27: // unreachable
                case 28: // unreachable
                case 29: // unreachable
                case 30: // unreachable
                case 31: // unreachable
                    throw new XMLException("Bad character snuck into document");
                case ' ':  return -1;
                case '!':
                    unicodeLength++;
                    break;
                case '"':
                    return -1;
                case '#':
                    unicodeLength++;
                    break;
                case '$':
                    unicodeLength++;
                    break;
                case '%':
                    unicodeLength++;
                    break;
                case '&':
                    return -1;
                case '\'':
                    unicodeLength++;
                    break;
                case '(':
                    unicodeLength++;
                    break;
                case ')':
                    unicodeLength++;
                    break;
                case '*':
                    unicodeLength++;
                    break;
                case '+':
                    unicodeLength++;
                    break;
                case ',':
                    unicodeLength++;
                    break;
                case '-':
                    unicodeLength++;
                    break;
                case '.':
                    unicodeLength++;
                    break;
                case '/':
                    unicodeLength++;
                    break;
                case '0':
                    unicodeLength++;
                    break;
                case '1':
                    unicodeLength++;
                    break;
                case '2':
                    unicodeLength++;
                    break;
                case '3':
                    unicodeLength++;
                    break;
                case '4':
                    unicodeLength++;
                    break;
                case '5':
                    unicodeLength++;
                    break;
                case '6':
                    unicodeLength++;
                    break;
                case '7':
                    unicodeLength++;
                    break;
                case '8':
                    unicodeLength++;
                    break;
                case '9':
                    unicodeLength++;
                    break;
                case ':':
                    unicodeLength++;
                    break;
                case ';':
                    unicodeLength++;
                    break;
                case '<':
                    return -1;
                case '=':
                    unicodeLength++;
                    break;
                case '>':
                    return -1;
                default:
                    if (c < 0xd800 || c > 0xDBFF) unicodeLength++;
            }
        }
        return unicodeLength;
        
     }

    
    final void writePCDATA(String s) throws IOException {

         if (normalize) {
             s = normalize(s);
         }
         
         int unicodeStringLength = getUnicodeLengthForPCDATA(s);
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
                 writePCDATA(s.charAt(i));
             }
         }
    
    }
    

    private static int getUnicodeLengthForPCDATA(String s) {
        
        int unicodeLength = 0;
        int javaLength = s.length();
        for (int i = 0; i < javaLength; i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\t': return -1;
                case '\n': return -1;
                case   11: // unreachable
                case   12: throw new XMLException("Bad character snuck into document");
                case '\r': return -1;
                case 14: // unreachable
                case 15: // unreachable
                case 16: // unreachable
                case 17: // unreachable
                case 18: // unreachable
                case 19: // unreachable
                case 20: // unreachable
                case 21: // unreachable
                case 22: // unreachable
                case 23: // unreachable
                case 24: // unreachable
                case 25: // unreachable
                case 26: // unreachable
                case 27: // unreachable
                case 28: // unreachable
                case 29: // unreachable
                case 30: // unreachable
                case 31: // unreachable
                    throw new XMLException("Bad character snuck into document");
                case ' ':  return -1;
                case '!':
                    unicodeLength++;
                    break;
                case '"':
                    unicodeLength++;
                    break;
                case '#':
                    unicodeLength++;
                    break;
                case '$':
                    unicodeLength++;
                    break;
                case '%':
                    unicodeLength++;
                    break;
                case '&':
                    return -1;
                case '\'':
                    unicodeLength++;
                    break;
                case '(':
                    unicodeLength++;
                    break;
                case ')':
                    unicodeLength++;
                    break;
                case '*':
                    unicodeLength++;
                    break;
                case '+':
                    unicodeLength++;
                    break;
                case ',':
                    unicodeLength++;
                    break;
                case '-':
                    unicodeLength++;
                    break;
                case '.':
                    unicodeLength++;
                    break;
                case '/':
                    unicodeLength++;
                    break;
                case '0':
                    unicodeLength++;
                    break;
                case '1':
                    unicodeLength++;
                    break;
                case '2':
                    unicodeLength++;
                    break;
                case '3':
                    unicodeLength++;
                    break;
                case '4':
                    unicodeLength++;
                    break;
                case '5':
                    unicodeLength++;
                    break;
                case '6':
                    unicodeLength++;
                    break;
                case '7':
                    unicodeLength++;
                    break;
                case '8':
                    unicodeLength++;
                    break;
                case '9':
                    unicodeLength++;
                    break;
                case ':':
                    unicodeLength++;
                    break;
                case ';':
                    unicodeLength++;
                    break;
                case '<':
                    return -1;
                case '=':
                    unicodeLength++;
                    break;
                case '>':
                    return -1;
                default:
                    if (c < 0xd800 || c > 0xDBFF) unicodeLength++;
            }
        }
        return unicodeLength;

    }
    
}
