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

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.ParentNode;
import nu.xom.ParseException;

/**
 * <p>
 *   Demonstrates removing elements from a tree
 *   while retaining their children.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d18
 *
 */
public class RDDLStripper {

    public final static String RDDL_NAMESPACE 
       = "http://www.rddl.org/";

    public static void main(String[] args) {

        if (args.length <= 0) {
          System.out.println("Usage: java nu.xom.samples.RDDLStripper URL");
          return;
        }
        
        try {
            Builder parser = new Builder();
            Document doc = parser.build(args[0]);
          
            strip(doc.getRootElement());
          

            System.out.println(doc.toXML());
        }
        catch (ParseException ex) {
          System.out.println(args[0] + " is not well-formed.");
          System.out.println(ex.getMessage());
        }
        catch (IOException ex) { 
          System.out.println(
           "Due to an IOException, the parser could not read " 
           + args[0]
          ); 
        }      
        
    }
    
    public static void strip(Element element) {
        
       if (element.getNamespaceURI().equals(RDDL_NAMESPACE)) {
            
            ParentNode parent = element.getParent();
            int position = 0;
            for (; position < parent.getChildCount(); position++) {
                if (parent.getChild(position) == element) break;
            }
            parent.removeChild(position);
            while (element.getChildCount() > 0) {
                Node child = element.getChild(0);
                element.removeChild(0);
                parent.insertChild(child, position);
                position++;
                if (child instanceof Element) strip((Element) child);
            }     
            
        }
        else {
            Elements elements = element.getChildElements();      
            for (int i = 0; i < elements.size(); i++) {
                strip(elements.get(i));
            }     
        }       
        
    } 
    
}