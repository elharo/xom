/* Copyright 2002-2006 Elliotte Rusty Harold
   
   This library is free software; you can redistribute it and/or modify
   it under the terms of version 2.1 of the GNU Lesser General Public 
   License as published by the Free Software Foundation.
   
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
   GNU Lesser General Public License for more details.
   
   You should have received a copy of the GNU Lesser General Public
   License along with this library. If not, see
   <https://www.gnu.org/licenses/>.
   
   You can contact Elliotte Rusty Harold by sending e-mail to
   elharo@ibiblio.org. Please include the word "XOM" in the
   subject line. The XOM home page is located at https://xom.nu/
*/
package nu.xom;

import java.io.IOException;
import java.io.Writer;

final class UnsynchronizedBufferedWriter extends Writer {
    
    private final static int CAPACITY = 8192;
    private char[] buffer = new char[CAPACITY];
    private int    position = 0;
    private Writer out;
    
    
    public UnsynchronizedBufferedWriter(Writer out) {
        this.out = out;
    }

    
    public void write(char[] buffer, int offset, int length) throws IOException {
        throw new UnsupportedOperationException("XOM bug: this statement shouldn't be reachable.");
    }
    
    
    public void write(String s) throws IOException {
         write(s, 0, s.length());
    }

    
    public void write(String s, int offset, int length) throws IOException {
    
        while (length > 0) {
            int n = CAPACITY - position;
            if (length < n) n = length;
            s.getChars(offset, offset + n, buffer, position);
            position += n;
            offset += n;
            length -= n;
            if (position >= CAPACITY) flushInternal();
        }
        
    }
        
    
    public void write(int c) throws IOException {
        if (position >= CAPACITY) flushInternal();
        buffer[position] = (char) c;
        position++;
    }

    
    public void flush() throws IOException {
        flushInternal();
        out.flush();
    }


    private void flushInternal() throws IOException {
        if (position != 0) {
            out.write(buffer, 0, position);
            position = 0;
        }
    }

    
    public void close() throws IOException {
        throw new UnsupportedOperationException("How'd we get here?");
    }

}
