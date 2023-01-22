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
import nu.xom.Document;
import nu.xom.ParsingException;

/**
 *
 * <p>
 * Demonstrates parsing a document and reserializing it
 * so as to resolve all entity references.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0
 *
 */
public class DocumentMerger {

    public static void main(String[] args) {
        
        Builder builder = new Builder();
        try {
            Document doc = builder.build(args[0]);
            System.out.println(doc.toXML());
        }
        catch (IOException ex) {
            System.err.println(ex);
        }    
        catch (ParsingException ex) {
            System.err.println(ex);
        }        
        
    }
    
}
