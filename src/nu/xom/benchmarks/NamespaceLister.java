/* Copyright 2006 Elliotte Rusty Harold
   
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

package nu.xom.benchmarks;

import java.io.IOException;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Namespace;
import nu.xom.Nodes;
import nu.xom.ParsingException;

/**
 * 
 * <p>
 * Benchmarks listing the namespace prefixes and URIs in scope 
 * and declared for every element in a document
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.2d1
 *
 */
class NamespaceLister {

    public static void main(String[] args) {
     
        if (args.length <= 0) {
          System.out.println(
            "Usage: java nu.xom.benchmarks.NamespaceLister URL"
          );
          return; 
        }
         
        NamespaceLister lister = new NamespaceLister();
        Builder parser = new Builder();
        try {
            Document document = parser.build(args[0]);

            // warmup Hotspot
            bench(lister, document);
            
            long ms = 0;
            int repeat = 100;
            for (int i = 1; i < repeat; i++) {
                ms += bench(lister, document);
            }
            System.out.println(ms/(double) repeat + "ms to list all URIs on average");

        }
        catch (IOException ex) { 
          System.out.println(ex); 
        }
        catch (ParsingException ex) { 
          System.out.println(ex); 
        }
  
    }

    private static long bench(NamespaceLister iterator, Document document) {

        long prewalk = System.currentTimeMillis();         
        // Process it starting at the root
        iterator.list(document.getRootElement());
        long postwalk = System.currentTimeMillis();
        return postwalk - prewalk;
    }

    
    private void list(Element original) {

        getDeclaredNamepaces(original);
        getInscopeNamespaces(original);
        Elements children = original.getChildElements();
        for (int i = 0; i < children.size(); i++) {
            list(children.get(i));
        }
        
    }

    private void getDeclaredNamepaces(Element original) {

        for (int i = original.getNamespaceDeclarationCount()-1; i >= 0; i--) {
            String prefix = original.getNamespacePrefix(i);
            System.out.println(original.getNamespaceURI(prefix));            
        }
    }

    private void getInscopeNamespaces(Element original) {
        Nodes spaces = original.query("namespace::node()");
        for (int i = spaces.size() -1; i >= 0; i--) {
            Namespace space = (Namespace) spaces.get(i);
            System.out.println(space.getValue());            
        }
    }

}