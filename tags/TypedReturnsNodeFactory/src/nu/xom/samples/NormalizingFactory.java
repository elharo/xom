// Copyright 2002, 2003 Elliotte Rusty Harold
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

package nu.xom.samples;

import java.io.IOException;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.NodeFactory;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.Text;

/**
 * <p>
 *   Demonstrates a custom <code>NodeFactory</code> that normalizes
 *   all white space in text nodes and attribute values.
 *   Normalization involves stripping all leading and trailing white
 *   space, converting all tabs, carriage returns, and line feeds to a 
 *   single space each, and then converting all remaining runs of white 
 *   space to a single space. Text nodes that contain nothing after
 *   this process is applied are not created. This may be useful for 
 *   record-like XML.
 * </p>
 * 
 * <p>
 *   This class does <strong>not</strong> perform 
 *   Unicode normalization.
 * </p>
 * 
 *  @author Elliotte Rusty Harold
 *  @version 1.0d22
 *
 */
public class NormalizingFactory extends NodeFactory {

    // We don't need text nodes at all    
    public Text makeText(String data) {
        data = normalizeSpace(data);
        if ("".equals(data)) return null; 
        return super.makeText(data); 
    }    

    public Attribute makeAttribute(String name, String URI, 
      String value, Attribute.Type type) {
        value = normalizeSpace(value);
        return super.makeAttribute(name, URI, value, type);
    }

    public Text makeWhiteSpaceInElementContent(String data) {
        return null;  
    }
    
    // not the most efficient implementation
    private static String normalizeSpace(String data) {
        data = data.replace('\t', ' ');
        data = data.replace('\n', ' ');
        data = data.replace('\r', ' ');
        data = data.trim();
        
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < data.length(); i++) {
            if (i == 0 || data.charAt(i-1) != ' ' 
              || data.charAt(i) != ' ') {
                result.append(data.charAt(i));
            }
        }
        
        return result.toString();
    }
    
    public static void main(String[] args) {
  
        if (args.length == 0) {
          System.out.println(
            "Usage: java nu.xom.samples.NormalizingFactory URL"
          );
          return;
        } 
          
        Builder builder = new Builder(new NormalizingFactory());
         
        try {
            Document doc = builder.build(args[0]);      
            Serializer serializer = new Serializer(System.out);
            serializer.write(doc);
        }
        // indicates a well-formedness error
        catch (ParsingException ex) { 
            System.out.println(args[0] + " is not well-formed.");
            System.out.println(ex.getMessage());
        }  
        catch (IOException ex) { 
            System.out.println(ex);
        }  
      
    }

}