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
import java.net.UnknownHostException;

import junit.framework.TestCase;

public class CITestUtilTest extends TestCase {

    public void testIsNetworkExceptionForHTTPServerError() {
        IOException ex = new IOException(
          "Server returned HTTP response code: 503 for URL: http://www.example.com"
        );
        assertTrue(CITestUtil.isNetworkException(ex));
    }


    public void testIsNetworkExceptionForHTTPClientError() {
        IOException ex = new IOException(
          "Server returned HTTP response code: 404 for URL: http://www.example.com"
        );
        assertFalse(CITestUtil.isNetworkException(ex));
    }


    public void testIsNetworkExceptionForUnknownHost() {
        IOException ex = new IOException(new UnknownHostException("www.example.com"));
        assertTrue(CITestUtil.isNetworkException(ex));
    }
    
}
