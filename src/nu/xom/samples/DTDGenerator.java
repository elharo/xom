/* Copyright 2003, 2019 Elliotte Rusty Harold
   
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
   elharo@ibiblio.org. Please include the word "XOM" in the
   subject line. The XOM home page is located at https://xom.nu/
*/

package nu.xom.samples;

import java.io.IOException;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.NodeFactory;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * <p>
 * Demonstrates walking the tree while collecting
 * element and attribute names and types.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.3.1
 *
 */
public class DTDGenerator {
 
    public static void main(String[] args) {
        
        if (args.length == 0) {
            System.err.println(
              "Usage: java nu.xom.samples.DTDGenerator URL"
            );
            return;   
        }
        
        try {
            Builder builder = new Builder(new NamingNodeFactory());
            builder.build(args[0]); 
        }
        catch (IOException ex) {
            System.err.println("Could not read " + args[0] 
              + " due to " + ex.getMessage());
        }       
        catch (ParsingException ex) {
            System.err.println(args[0] + " is not well-formed");
        } 
              
    }
 
    private static class NamingNodeFactory extends NodeFactory {

        private List<String> names = new ArrayList<String>();
        private String currentElement;
        
        public Element startMakingElement(
          String name, String namespace) {
            if (!names.contains(name)) {
                System.out.println("<!ELEMENT " + name + " ANY>");   
                names.add(name);
            }
            currentElement = name;
            return super.startMakingElement(name, namespace);
        }
        
        public Nodes makeAttribute(String name, String URI, 
          String value, Attribute.Type type) {
              
            if (type.equals(Attribute.Type.ENUMERATION) 
              || type.equals(Attribute.Type.UNDECLARED)) {
                type = Attribute.Type.CDATA;      
            }
            String comboName = currentElement + '#' + name;
            if (!names.contains(comboName)) {
                names.add(comboName);
                System.out.println("<!ATTLIST " + currentElement + " "
                  + name + " " + type.getName() + " #IMPLIED>");
            }
            return super.makeAttribute(name, URI, value, type);
        } 
    } 
    
}