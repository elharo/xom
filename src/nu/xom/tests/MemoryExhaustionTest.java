/* Copyright 2026 Elliotte Rusty Harold
   
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

package nu.xom.tests;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import java.util.Arrays;
import junit.framework.TestCase;
import nu.xom.Builder;
import nu.xom.ParsingException;

/**
 * Mostly tests that verify behavior in the face of billion laughs attack and other failures
 * that happen in memory constrained environments. These run in a 
 * separate VM with small heaps to trigger the issue being tested for sooner.
 */
public class MemoryExhaustionTest extends TestCase {

    private static class HugeCommentInputStream extends InputStream {
        
        private static final byte[] PREFIX;
        private static final byte[] SUFFIX;
        private static final byte[] A_BYTES = new byte[8192];
        private static final long COMMENT_LENGTH = (long) Integer.MAX_VALUE + 10;
        static {
            try {
                PREFIX = "<root><!--".getBytes("UTF-8");
                SUFFIX = "--></root>".getBytes("UTF-8");
            }
            catch (UnsupportedEncodingException ex) {
                throw new RuntimeException(ex);
            }
            Arrays.fill(A_BYTES, (byte) 'A');
        }
        
        private int prefixIndex;
        private long commentIndex;
        private int suffixIndex;
        
        public int read(byte[] b, int off, int len) {
            if (prefixIndex < PREFIX.length) {
                int toCopy = Math.min(len, PREFIX.length - prefixIndex);
                System.arraycopy(PREFIX, prefixIndex, b, off, toCopy);
                prefixIndex += toCopy;
                return toCopy;
            }
            if (commentIndex < COMMENT_LENGTH) {
                int toCopy = (int) Math.min(len, COMMENT_LENGTH - commentIndex);
                System.arraycopy(A_BYTES, 0, b, off, toCopy);
                commentIndex += toCopy;
                return toCopy;
            }
            if (suffixIndex < SUFFIX.length) {
                int toCopy = Math.min(len, SUFFIX.length - suffixIndex);
                System.arraycopy(SUFFIX, suffixIndex, b, off, toCopy);
                suffixIndex += toCopy;
                return toCopy;
            }
            return -1;
        }
        
        public int read() {
            byte[] b = new byte[1];
            int n = read(b, 0, 1);
            if (n == -1) return -1;
            return b[0] & 0xFF;
        }
        
    }
    
    
    public void testHugeCommentCausesOutOfMemoryError() 
      throws ParsingException, IOException {
        
        Builder builder = new Builder();
        builder.build(new HugeCommentInputStream());
        
    }

}
