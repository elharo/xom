/* Copyright 2002-2004 Elliotte Rusty Harold
   
   This library is free software; you can redistribute it and/or modify
   it under the terms of version 2.1 of the GNU Lesser General Public 
   License as published by the Free Software Foundation.
   
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
   GNU Lesser General Public License for more details.
   
   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the 
   Free Software Foundation, Inc., 59 Temple Place, Suite 330, 
   Boston, MA 02111-1307  USA
   
   You can contact Elliotte Rusty Harold by sending e-mail to
   elharo@metalab.unc.edu. Please include the word "XOM" in the
   subject line. The XOM home page is located at http://www.xom.nu/
*/

package nu.xom.samples;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ParentNode;
import nu.xom.ParsingException;
import nu.xom.Serializer;


/**
 * <p>
 * Demonstrates a serializer which, unlike 
 * <code>nu.xom.Serializer</code>, is not limited by the Java stack
 * size and can process arbitrarily deep documents.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0
 *
 */
public final class NonRecursiveSerializer extends Serializer {
    
    
    public NonRecursiveSerializer(OutputStream out) {
        super(out);
    }
    

    public NonRecursiveSerializer(OutputStream out, String encoding)
      throws UnsupportedEncodingException {
        super(out, encoding);
    }
    
    
    protected final void write(Element element) 
      throws IOException {

        // treat empty elements specially to avoid an instance of test
        if (element.getChildCount() == 0) {
            super.writeEmptyElementTag(element);                
        }
        else {
            Node current = element;
            boolean end = false;
            int index = -1;
            while (true) {                   
                if (!end && current.getChildCount() > 0) {
                   writeStartTag((Element) current);
                   current = current.getChild(0);
                   index = 0;
                }
                else {
                    if (end) {
                        writeEndTag((Element) current);
                        if (current == element) break;
                    }
                    else {
                        writeChild(current);
                    }
                    end = false;
                    ParentNode parent = current.getParent();
                    if (parent.getChildCount() - 1 == index) {
                        current = parent;
                        if (current != element) {
                            parent = current.getParent();
                            index = parent.indexOf(current);
                        }
                        end = true;
                    }
                    else {
                        index++;
                        current = parent.getChild(index);
                    }
                }
            }   
        }
        
    }     

    
    public static void main(String[] args) {
  
        if (args.length <= 0) {
          System.out.println("Usage: java nu.xom.samples.PrettyPrinter URL");
          return;
        }
        
        try {
          Builder parser = new Builder();
          Document doc = parser.build(args[0]);
          Serializer serializer = new Serializer(System.out, "ISO-8859-1");
          serializer.setIndent(4);
          serializer.setMaxLength(64);
          serializer.setPreserveBaseURI(true);
          serializer.write(doc);
          serializer.flush();
        }
        catch (ParsingException ex) {
          System.out.println(args[0] + " is not well-formed.");
          System.out.println(ex.getMessage());
        }
        catch (IOException ex) { 
          System.out.println(
           "Due to an IOException, the parser could not check " 
           + args[0]
          ); 
        }
  
    }
    
}
