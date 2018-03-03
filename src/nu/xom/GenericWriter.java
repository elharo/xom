/* Copyright 2002-2004, 2018 Elliotte Rusty Harold
   
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

import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Locale;

/**
 * <p>
 * <code>GenericWriter</code> figures out whether a 
 * character is or is not available in a particular encoding.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.2.11
 * 
 */
class GenericWriter extends TextWriter {
    
    private final boolean               isJapanese;
    private final CharsetEncoder        encoder;

    
    GenericWriter(Writer out, String encoding) 
      throws UnsupportedEncodingException {
        
        super(out, encoding);
        encoding = encoding.toUpperCase(Locale.ENGLISH);
        if (encoding.indexOf("EUC-JP") > -1
          || encoding.startsWith("EUC_JP")
          || encoding.equals("SHIFT_JIS")
          || encoding.equals("SJIS")
          || encoding.equals("ISO-2022-JP")) {
            isJapanese = true;
        }
        else {
           isJapanese = false; 
        }
        encoder = Charset.forName(encoding).newEncoder();
    }

    
    boolean needsEscaping(char c) {
       
        // assume everything has at least the ASCII characters
        if (c <= 127) return false;
        // work around various bugs in Japanese encodings
        if (isJapanese) {
            if (c == 0xA5) return true; // Yen symbol 
            if (c == 0x203E) return true; // Sun bugs in EUC-JP and SJIS
        }
        
        if (encoder.canEncode(c)) return false;
        return true;
    }
   
    
}
