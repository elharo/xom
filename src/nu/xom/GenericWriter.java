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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 * <p>
 * <code>GenericWriter</code> is a hack that figures out whether a 
 * character is or is not avilable in a particular encoding by writing
 * it onto an OutputStream and seeing whether or not the character 
 * written is a question mark (Java's substitution character). 
 * There's amore staright-forward way to do this using 
 * <code>java.nio.Charset</code> in Java 1.4, but I'm not willing to 
 * assume Java 1.4. 
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0a4
 * 
 */
class GenericWriter extends TextWriter {
    
    private ByteArrayOutputStream bout;
    private OutputStreamWriter wout;

    GenericWriter(Writer out, String encoding) 
      throws UnsupportedEncodingException {
        super(out, encoding);
        bout = new ByteArrayOutputStream(32);
        wout = new OutputStreamWriter(bout, encoding);
    }

    boolean needsEscaping(char c) {
       
        // assume everything has at least the ASCII characters
        if (c <= 127) return false;
        if (c == 0xA5) return true; // Yen symbol is problematic in some Japanese encodings
        if (c == 0x203E) return true; // work around Sun bugs in EUC-JP

        boolean result = false;
        try {
            wout.write(c);
            wout.flush();
            byte[] data = bout.toByteArray();    
            if (data.length == 0) result = true; // surrogate pair
            else if (data[0] == '?') result = true;
        }
        catch (IOException ex) {
            throw new RuntimeException(
              "Impossible to have an IOException when writing to a byte array"
            );
        }
        catch (Error err) {
            // This appears to be a wrapper around an undocumented
            // sun.io.UnknownCharacterException or some such. In any
            // case Java doesn't know how to output this character.
            return true;
        }
        finally {
            bout.reset();
        }
        return result;
        
    }
    
}
