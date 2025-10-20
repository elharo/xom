/* Copyright 2024 Elliotte Rusty Harold
   
   This library is free software; you can redistribute it and/or modify
   it under the terms of version 2.1 of the GNU Lesser General Public 
   License as published by the Free Software Foundation.
   
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
   GNU Lesser General Public License for more details.
   
   You can contact Elliotte Rusty Harold by sending e-mail to
   elharo@ibiblio.org. Please include the word "XOM" in the
   subject line. The XOM home page is located at https://xom.nu/
*/

import java.io.File;
import java.io.IOException;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.xinclude.XIncludeException;
import nu.xom.xinclude.XIncluder;

/**
 * Simple XInclude processor for documentation builds.
 * 
 * @author Elliotte Rusty Harold
 * @version 1.3.9
 */
public class XOMXIncluder {

    /**
     * <p>
     * The driver method for the XIncluder program.
     * </p>
     *
     * @param args <code>args[0]</code> contains the URL or  
     *      file name of the first document to be processed. 
     */
    public static void main(String[] args) {
  
        if (args.length < 1) {
            System.err.println("Usage: java XOMXIncluder document");
            System.exit(1);
        }
  
        Builder builder = new Builder();
        try {
            Document input = null;
            // Is args[0] a file or an absolute URL?
            if (args[0].indexOf("://") >= 0) {
                input = builder.build(args[0]);
            }
            else {
                input = builder.build(new File(args[0]));
            }
            
            XIncluder.resolveInPlace(input);
            
            Serializer outputter = new Serializer(System.out, "ISO-8859-1");
            outputter.write(input);
        }
        catch (IOException e) {
            System.err.println("I/O error reading " + args[0] + " " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        catch (ParsingException e) {
            System.err.println("Malformed XML while processing " + args[0] + " " + e.getMessage());
            System.exit(1);
        }
        catch (XIncludeException e) {
            System.err.println("XInclude error while processing " + args[0] + " " + e.getMessage());
            System.exit(1);
        }
  
    }

}
