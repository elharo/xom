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
   elharo@metalab.unc.edu. Please include the word "XOM" in the
   subject line. The XOM home page is located at http://www.xom.nu/
*/

package nu.xom;

import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Locale;

/**
 * @author Elliotte Rusty Harold
 * @version 1.0b6
 */
class TextWriterFactory {

    public static TextWriter getTextWriter(
      Writer out, String encoding) {
    
        // Not all encoding names are case-insensitive in Java, even
        // though they should be. For instance, MacRoman isn't. 
        String encodingUpperCase = encoding.toUpperCase(Locale.ENGLISH);
        if (encodingUpperCase.startsWith("UTF") 
          || encodingUpperCase.startsWith("UNICODE")
          ) {  
            return new UnicodeWriter(out, encoding);    
        }    
        else if (encodingUpperCase.startsWith("ISO-10646-UCS") 
          || encodingUpperCase.startsWith("UCS") 
          || encodingUpperCase.equals("GB18030")) {
          // GB18030 has a 1-1 mapping to Unicode. However, the Sun
          // GB18030 VM is buggy with non-BMP characters. The IBM VM
          // gets this right, but for safety we'll escape all non-BMP
          // characters.
            return new UCSWriter(out, encoding);    
        }    
        else if (encodingUpperCase.equals("ISO-8859-1")) {
            return new Latin1Writer(out, encoding); 
        }          
        else if (encodingUpperCase.equals("ISO-8859-2")) {
            return new Latin2Writer(out, encodingUpperCase); 
        }           
        else if (encodingUpperCase.equals("ISO-8859-3")) {
            return new Latin3Writer(out, encodingUpperCase); 
        }           
        else if (encodingUpperCase.equals("ISO-8859-4")) {
            return new Latin4Writer(out, encodingUpperCase); 
        }           
        else if (encodingUpperCase.equals("ISO-8859-5")) {
            return new ISOCyrillicWriter(out, encodingUpperCase); 
        }           
        else if (encodingUpperCase.equals("ISO-8859-6")) {
            return new ISOArabicWriter(out, encodingUpperCase); 
        }           
        else if (encodingUpperCase.equals("ISO-8859-7")) {
            return new ISOGreekWriter(out, encodingUpperCase); 
        }           
        else if (encodingUpperCase.equals("ISO-8859-8")) {
            return new ISOHebrewWriter(out, encodingUpperCase); 
        }           
        else if (encodingUpperCase.equals("ISO-8859-9")
            || encodingUpperCase.equals("EBCDIC-CP-TR")
            || encodingUpperCase.equals("CP1037")) {
            return new Latin5Writer(out, encodingUpperCase); 
        }           
        else if (encoding.equals("ISO-8859-10")) {
            return new Latin6Writer(out, encoding); 
        }          
        else if (encodingUpperCase.equals("ISO-8859-11")
                || encodingUpperCase.equals("TIS-620")
                || encodingUpperCase.equals("TIS620")) {
            return new ISOThaiWriter(out, encodingUpperCase); 
        }           
        // There's no such thing as ISO-8859-12
        // nor is there likely to be one in the future.        
        else if (encodingUpperCase.equals("ISO-8859-13")) {
            return new Latin7Writer(out, encodingUpperCase); 
        }   
        else if (encoding.equals("ISO-8859-14")) {
            return new Latin8Writer(out, encoding); 
        }       
        else if (encodingUpperCase.equals("ISO-8859-15")) {
            return new Latin9Writer(out, encodingUpperCase); 
        }          
        else if (encoding.equals("ISO-8859-16")) {
            return new Latin10Writer(out, encoding); 
        }
        else if (encodingUpperCase.endsWith("ASCII")) {
            return new ASCIIWriter(out, encodingUpperCase); 
        }
        else if (encodingUpperCase.equals("IBM037")
              || encodingUpperCase.equals("CP037")
              || encodingUpperCase.equals("EBCDIC-CP-US")
              || encodingUpperCase.equals("EBCDIC-CP-CA")
              || encodingUpperCase.equals("EBCDIC-CP-WA")
              || encodingUpperCase.equals("EBCDIC-CP-NL")
              || encodingUpperCase.equals("CSIBM037")) {
            // EBCDIC-37 has same character set as ISO-8859-1;
            // just at different code points.
            return new Latin1Writer(out, encodingUpperCase); 
        }     
        else {
            try {
                return new GenericWriter(out, encoding); 
            }
            catch (UnsupportedEncodingException ex) {
                return new ASCIIWriter(out, encoding);
            }
        }
        
    }

}
