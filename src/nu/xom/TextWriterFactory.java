// Copyright 2002-2004 Elliotte Rusty Harold
// 
// This library is free software; you can redistribute 
// it and/or modify it under the terms of version 2.1 of 
// the GNU Lesser General Public License as published by  
// the Free Software Foundation.
// 
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
// GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General 
// Public License along with this library; if not, write to the 
// Free Software Foundation, Inc., 59 Temple Place, Suite 330, 
// Boston, MA  02111-1307  USA
// 
// You can contact Elliotte Rusty Harold by sending e-mail to
// elharo@metalab.unc.edu. Please include the word "XOM" in the
// subject line. The XOM home page is temporarily located at
// http://www.cafeconleche.org/XOM/  but will eventually move
// to http://www.xom.nu/

package nu.xom;

import java.io.Writer;

/**
 * @author Elliotte Rusty Harold
 * @version 1.0a3
 *
 */
class TextWriterFactory {

    public static TextWriter getTextWriter(
      Writer out, String encoding) {
    
        encoding = encoding.toUpperCase();
        if (encoding.startsWith("UTF") 
          || encoding.startsWith("UNICODE")
          ) {  
            return new UnicodeWriter(out, encoding);    
        }    
        else if (encoding.startsWith("ISO-10646-UCS") 
          || encoding.startsWith("UCS") 
          || encoding.equals("GB18030")) {
          // GB18030 has a 1-1 mapping to Unicode. However, the Sun
          // GB18030 VM is buggy with non-BMP characters. The IBM VM
          // gets this right, but for safety we'll escape all non-BMP
          // characters.
            return new UCSWriter(out, encoding);    
        }    
        else if (encoding.equals("ISO-8859-1")) {
            return new Latin1Writer(out, encoding); 
        }           
        else if (encoding.equals("ISO-8859-2")) {
            return new Latin2Writer(out, encoding); 
        }           
        else if (encoding.equals("ISO-8859-3")) {
            return new Latin3Writer(out, encoding); 
        }           
        else if (encoding.equals("ISO-8859-4")) {
            return new Latin4Writer(out, encoding); 
        }           
        else if (encoding.equals("ISO-8859-5")) {
            return new ISOCyrillicWriter(out, encoding); 
        }           
        else if (encoding.equals("ISO-8859-6")) {
            return new ISOArabicWriter(out, encoding); 
        }           
        else if (encoding.equals("ISO-8859-7")) {
            return new ISOGreekWriter(out, encoding); 
        }           
        else if (encoding.equals("ISO-8859-8")) {
            return new ISOHebrewWriter(out, encoding); 
        }           
        else if (encoding.equals("ISO-8859-9")) {
            return new Latin5Writer(out, encoding); 
        }           
        else if (encoding.equals("ISO-8859-10")) {
            return new Latin6Writer(out, encoding); 
        }           
        else if (encoding.equals("ISO-8859-11")
                || encoding.equals("TIS-620")
                || encoding.equals("TIS620")) {
            return new ISOThaiWriter(out, encoding); 
        }           
        // There's no such thing as ISO-8859-12
        // nor is there likely to be one in the future.        
        else if (encoding.equals("ISO-8859-13")) {
            return new Latin7Writer(out, encoding); 
        }           
        else if (encoding.equals("ISO-8859-14")) {
            return new Latin8Writer(out, encoding); 
        }           
        else if (encoding.equals("ISO-8859-15")) {
            return new Latin9Writer(out, encoding); 
        }           
        else if (encoding.equals("ISO-8859-16")) {
            return new Latin10Writer(out, encoding); 
        }
        else if (encoding.equals("BIG5")) {
            return new Big5Writer(out, encoding); 
        }
        else if (encoding.equals("IBM037")
              || encoding.equals("CP037")
              || encoding.equals("EBCDIC-CP-US")
              || encoding.equals("EBCDIC-CP-CA")
              || encoding.equals("EBCDIC-CP-WA")
              || encoding.equals("EBCDIC-CP-NL")
              || encoding.equals("CSIBM037")) {
            // EBCDIC-37 has same character set as ISO-8859-1;
            // just at different code points.
            return new Latin1Writer(out, encoding); 
        }     
        else {
            // I'm assuming here that all character sets can
            // handle the ASCII character set; even if not at the
            // same code points. This is not completely true.
            // There are some very old character sets that can't,
            // but no one is likely to be using them for XML.
            return new ASCIIWriter(out, encoding);  
        }
        
    }

}
