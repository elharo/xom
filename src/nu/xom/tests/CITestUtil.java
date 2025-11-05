/* Copyright 2025 Elliotte Rusty Harold
   
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

/**
 * <p>
 * Utility methods for CI-specific test handling, particularly for network-dependent tests.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.3.9
 *
 */
class CITestUtil {

    // Prevent instantiation
    private CITestUtil() {}
    
    /**
     * Helper method to check if we're running in a CI environment.
     * Network-dependent tests can use this to skip execution in CI
     * where network connectivity may be unreliable.
     * 
     * @return true if running in CI, false otherwise
     */
    static boolean isRunningInCI() {
        String ci = System.getenv("CI");
        String githubActions = System.getenv("GITHUB_ACTIONS");
        return "true".equalsIgnoreCase(ci) || "true".equalsIgnoreCase(githubActions);
    }
    
    /**
     * Helper method to check if an exception is network-related.
     * Checks the exception chain for UnknownHostException or ConnectException.
     * 
     * @param ex the exception to check
     * @return true if the exception is network-related, false otherwise
     */
    static boolean isNetworkException(IOException ex) {
        Throwable cause = ex;
        while (cause != null) {
            if (cause instanceof java.net.UnknownHostException ||
                cause instanceof java.net.ConnectException ||
                cause instanceof java.net.SocketException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }
    
    /**
     * Helper method to determine if a network test failure should be ignored.
     * Network test failures are only ignored when running in CI and the exception
     * is network-related.
     * 
     * @param ex the exception to check
     * @return true if the test failure should be ignored, false otherwise
     */
    static boolean shouldIgnore(IOException ex) {
        return isRunningInCI() && isNetworkException(ex);
    }
}
