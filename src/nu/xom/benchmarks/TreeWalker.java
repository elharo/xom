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

package nu.xom.benchmarks;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Node;
import nu.xom.ParseException;
import nu.xom.Serializer;


/**
 * 
 * <p>
 * Need to give HotSpot a chance to warm up????
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d18
 *
 */
class TreeWalker {

    public static void main(String[] args) {
     
        if (args.length <= 0) {
          System.out.println(
            "Usage: java nu.xom.samples.TreeWalker URL"
          );
          return; 
        }
         
        TreeWalker iterator = new TreeWalker();
        try {
            
            long prebuild = System.currentTimeMillis();
            Builder parser = new Builder();
          
            // Read the entire document into memory
            Document document = parser.build(args[0]); 
            long postbuild = System.currentTimeMillis();
            System.out.println((postbuild - prebuild) 
              + "ms to build the tree");
           
            // Process it starting at the root
            iterator.followNode(document);
            long postwalk = System.currentTimeMillis();
            
            System.out.println((postwalk - postbuild) 
              + "ms to walk tree");
            
            OutputStream out = new ByteArrayOutputStream(3000000);

            long preserialize = System.currentTimeMillis();
            Serializer serializer = new Serializer(out, "UTF-8");
            serializer.write(document);
            serializer.flush();
            long postserialize = System.currentTimeMillis();
            System.out.println((postserialize - preserialize) 
              + "ms to serialize the tree in UTF-8");

            out = new ByteArrayOutputStream(4000000);
            long preprettyserialize = System.currentTimeMillis();
            serializer = new Serializer(out, "UTF-8");
            serializer.setIndent(2);
            serializer.setMaxLength(72);
            serializer.write(document);
            serializer.flush();
            long postprettyserialize = System.currentTimeMillis();
            System.out.println((postprettyserialize - preprettyserialize) 
              + "ms to pretty print the tree in UTF-8");

            long preUTF16serialize = System.currentTimeMillis();
            serializer = new Serializer(out, "UTF-16");
            serializer.write(document);
            serializer.flush();
            long postUTF16serialize = System.currentTimeMillis();
            System.out.println((postUTF16serialize - preUTF16serialize) 
              + "ms to serialize the tree in UTF-16");
        }
        catch (IOException e) { 
          System.out.println(e); 
        }
        catch (ParseException e) { 
          System.out.println(e); 
        }
  
    } // end main

    


    // note use of recursion
    public void followNode(Node node) throws IOException {
    
        for (int i = 0; i < node.getChildCount(); i++) {
            followNode(node.getChild(i));
        }
    
    }

}