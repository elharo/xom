/* Copyright 2024 Elliotte Rusty Harold
   
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

/**
 * <p>
 * Utility class for handling flaky network tests.
 * Provides methods to check if tests are running on CI.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.3.10
 */
public class NetworkTestHelper {
    
    /**
     * <p>
     * Checks if the tests are running on a CI system.
     * </p>
     * 
     * @return true if running on CI, false otherwise
     */
    public static boolean isRunningOnCI() {
        // Check common CI environment variables
        String ci = System.getenv("CI");
        String githubActions = System.getenv("GITHUB_ACTIONS");
        String jenkins = System.getenv("JENKINS_URL");
        String travis = System.getenv("TRAVIS");
        String circleci = System.getenv("CIRCLECI");
        
        return "true".equalsIgnoreCase(ci) 
            || "true".equalsIgnoreCase(githubActions)
            || jenkins != null
            || "true".equalsIgnoreCase(travis)
            || "true".equalsIgnoreCase(circleci);
    }
}
