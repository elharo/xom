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

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;


class EBCDICWriter extends OutputStreamWriter {
    
    
    private final static int NEL = 0x85;
    private OutputStream raw;
    
    
    public EBCDICWriter(OutputStream out) 
      throws UnsupportedEncodingException {
        super(out, "Cp037");    
        this.raw = out;    
    }
    
    
    // work around broken implementation of EBCDIC-37
    public void write(int c) throws IOException {
        
        if (c == NEL) {
            flush();
            raw.write(0x15);
        }
        else {
            super.write(c);          
        }
    
    }

    
    public void write(char[] text, int offset, int length)
      throws IOException {      
       for (int i = offset; i < offset+length; i++) write(text[i]);          
    }


}
