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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Node;
import nu.xom.ParseException;
import nu.xom.Serializer;
import nu.xom.ValidityException;


/**
 * 
 * <p>
 * Benchmark perofmrance against a specified URL for
 * building, tree-walking, and serializing.
 * </p>
 * replay several times and take average????
 * 
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d21
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
        Builder parser = new Builder();
        try {
            
            warmup(args[0], parser, iterator, 5);
            
            System.gc(); System.gc(); System.gc();
            
            long prebuild = System.currentTimeMillis();         
            // Read the entire document into memory
            Document document = build(args[0], parser);
            long postbuild = System.currentTimeMillis();
            System.out.println((postbuild - prebuild) 
              + "ms to build the tree");
              
           
            long prewalk = System.currentTimeMillis();         
            // Process it starting at the root
            walkTree(iterator, document);
            long postwalk = System.currentTimeMillis();
            System.out.println((postwalk - prewalk) 
              + "ms to walk tree");
            
            OutputStream out = new ByteArrayOutputStream(3000000);

            long preserialize = System.currentTimeMillis();
            serialize(document, out);
            long postserialize = System.currentTimeMillis();
            System.out.println((postserialize - preserialize) 
              + "ms to serialize the tree in UTF-8");

            out = new ByteArrayOutputStream(4000000);
            long preprettyserialize = System.currentTimeMillis();
            prettyPrint(document, out);
            long postprettyserialize = System.currentTimeMillis();
            System.out.println((postprettyserialize - preprettyserialize) 
              + "ms to pretty print the tree in UTF-8");

            long preUTF16serialize = System.currentTimeMillis();
            serializeUTF16(document, out);
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
  
    }

    private static void serializeUTF16(Document document, OutputStream out)
        throws UnsupportedEncodingException, IOException {
        Serializer serializer = new Serializer(out, "UTF-16");
        serializer.write(document);
        serializer.flush();
    }

    private static void prettyPrint(Document document, OutputStream out)
        throws UnsupportedEncodingException, IOException {
        Serializer serializer = new Serializer(out, "UTF-8");
        serializer.setIndent(2);
        serializer.setMaxLength(72);
        serializer.write(document);
        serializer.flush();
    }

    private static void serialize(Document document, OutputStream out)
        throws UnsupportedEncodingException, IOException {
        Serializer serializer = new Serializer(out, "UTF-8");
        serializer.write(document);
        serializer.flush();
    }

    private static void walkTree(TreeWalker iterator, Document document)
        throws IOException {
        iterator.followNode(document);
    }
    
    private static Document build(String url, Builder parser)
        throws ParseException, ValidityException, IOException {
            
        InputStream in = new BufferedInputStream((new URL(url)).openStream());
        Document document = parser.build(in, url); 
        return document;
    } 

    private static void warmup(String url, Builder parser, TreeWalker iterator, int numPasses) 
      throws IOException, ParseException {
        for (int i = 0; i < numPasses; i++) {
            Document doc = parser.build(url);
            walkTree(iterator, doc);
            serialize(doc, new ByteArrayOutputStream(3000000));
            prettyPrint(doc, new ByteArrayOutputStream(4000000));
            serializeUTF16(doc, new ByteArrayOutputStream(4000000));
        }
    }


    // note use of recursion
    public void followNode(Node node) throws IOException {
    
        for (int i = 0; i < node.getChildCount(); i++) {
            followNode(node.getChild(i));
        }
    
    }

}