/* Copyright 2002, 2003 Elliotte Rusty Harold
   
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

package nu.xom.samples;

import java.io.IOException;

import nu.xom.Builder;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

/**
 * <p>
 *   Demonstrates validation via the <code>Builder</code> class.
 * </p>
 * 
 * <p>Usage: <code>java nu.xom.samples.Validator [--strict] URL...</code></p>
 * <p>
 *   Validates each URL against its DTD. Without <code>--strict</code>,
 *   errors are reported but the process exits normally. With
 *   <code>--strict</code>, the process exits with status 1 on the
 *   first error.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.4.0
 *
 */
public class Validator {

  public static void main(String[] args) {
  
    if (args.length <= 0) {
      System.out.println("Usage: java nu.xom.samples.Validator [--strict] URL...");
      return;
    }

    boolean strict = false;
    int startIndex = 0;
    if (args[0].equals("--strict")) {
      strict = true;
      startIndex = 1;
    }

    if (startIndex >= args.length) {
      System.out.println("Usage: java nu.xom.samples.Validator [--strict] URL...");
      return;
    }

    Builder parser = new Builder(true);

    for (int i = startIndex; i < args.length; i++) {
      String url = args[i];
      try {
        parser.build(url);
        System.out.println(url + " is valid.");
      }
      catch (ValidityException ex) {
        System.out.println(url + " is not valid.");
        System.out.println(ex.getMessage());
        System.out.println(" at line " + ex.getLineNumber() 
          + ", column " + ex.getColumnNumber());
        if (strict) {
          System.exit(1);
        }
      }
      catch (ParsingException ex) {
        System.out.println(url + " is not well-formed.");
        System.out.println(ex.getMessage());
        System.out.println(" at line " + ex.getLineNumber() 
          + ", column " + ex.getColumnNumber());
        if (strict) {
          System.exit(1);
        }
      }
      catch (IOException ex) { 
        System.out.println(
         "Due to an IOException, the parser could not check " + url
        );
        ex.printStackTrace();
        if (strict) {
          System.exit(1);
        }
      }
    }
  
  }

}