// Copyright 2003 Elliotte Rusty Harold
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
import nu.xom.Element;
import nu.xom.NodeFactory;
import nu.xom.ParseException;
import java.util.List;
import java.util.ArrayList;

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
            Document doc = builder.build(args[0]); 
        }
        catch (IOException ex) {
            System.err.println("Could not read " + args[0] 
              + " due to " + ex.getMessage());
        }       
        catch (ParseException ex) {
            System.err.println(args[0] + " is not well-formed");
        } 
              
    }
 
    private static class NamingNodeFactory extends NodeFactory {

        private List names = new ArrayList();
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
        
        // will this handle enumerated types????
        public Attribute makeAttribute(String name, String URI, 
          String value, Attribute.Type type) {
              
            String comboName = currentElement + '#' + name;
            if (!names.contains(comboName)) {
                names.add(comboName);
                System.out.println("<!ATTLIST " + currentElement + " "
                  + name + " " + type.toXML() + " #IMPLIED>");
            }
            return super.makeAttribute(name, URI, value, type);
        } 
    } 
    
}