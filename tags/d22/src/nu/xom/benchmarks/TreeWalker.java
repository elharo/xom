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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Node;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.ValidityException;

/**
 * 
 * <p>
 * Benchmark perofmrance against a specified URL for
 * building, tree-walking, and serializing.
 * </p>
 * 
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d22
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
            // Separate out the basic I/O by storing document
            // in byte array first. However, this only caches the
            // document itself. Any DTD the document references will
            // still need to be read from the actual file.
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            URL u = new URL(args[0]);
            InputStream in = u.openStream();
            for (int c = in.read(); c != -1; c = in.read()) {
                out.write(c);   
            }
            out.flush();
            out.close();
            byte[] data = out.toByteArray();            
            warmup(data, parser, iterator, 5, args[0]);
            InputStream raw = new BufferedInputStream(
              new ByteArrayInputStream(data)
            );
            
            // try to avoid garbage collection pauses
            System.gc(); System.gc(); System.gc();
            
            long prebuild = System.currentTimeMillis();         
            // Read the entire document into memory
            Document document = build(data, args[0], parser);
            long postbuild = System.currentTimeMillis();
            System.out.println((postbuild - prebuild) 
              + "ms to build the tree");  
           
            long prewalk = System.currentTimeMillis();         
            // Process it starting at the root
            walkTree(iterator, document);
            long postwalk = System.currentTimeMillis();
            System.out.println((postwalk - prewalk) 
              + "ms to walk tree");
            
            OutputStream result = new ByteArrayOutputStream(3000000);

            long preserialize = System.currentTimeMillis();
            serialize(document, result);
            long postserialize = System.currentTimeMillis();
            System.out.println((postserialize - preserialize) 
              + "ms to serialize the tree in UTF-8");

            out = new ByteArrayOutputStream(4000000);
            long preprettyserialize = System.currentTimeMillis();
            prettyPrint(document, result);
            long postprettyserialize = System.currentTimeMillis();
            System.out.println(
             (postprettyserialize - preprettyserialize) 
              + "ms to pretty print the tree in UTF-8");

            long preUTF16serialize = System.currentTimeMillis();
            serializeUTF16(document, result);
            long postUTF16serialize = System.currentTimeMillis();
            System.out.println((postUTF16serialize - preUTF16serialize)
              + "ms to serialize the tree in UTF-16");
        }
        catch (IOException ex) { 
          System.out.println(ex); 
        }
        catch (ParsingException ex) { 
          System.out.println(ex); 
        }
  
    }

    private static void serializeUTF16(
      Document document, OutputStream out)
        throws UnsupportedEncodingException, IOException {
        Serializer serializer = new Serializer(out, "UTF-16");
        serializer.write(document);
        serializer.flush();
    }

    private static void prettyPrint(
      Document document, OutputStream out)
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

    private static void walkTree(TreeWalker iterator, Document doc)
      throws IOException {
        iterator.followNode(doc);
    }
    
    private static Document build(
      byte[] data, String base, Builder parser)
      throws ParsingException, ValidityException, IOException {
        InputStream raw = new BufferedInputStream(
          new ByteArrayInputStream(data)
        );
        Document document = parser.build(raw, base); 
        return document;
    } 

    private static void warmup(byte[] data, Builder parser, 
      TreeWalker iterator, int numPasses, String base) 
      throws IOException, ParsingException {
        for (int i = 0; i < numPasses; i++) {
            InputStream raw = new BufferedInputStream(
              new ByteArrayInputStream(data)
            );    
            Document doc;
            Builder parser1 = parser;
            TreeWalker iterator1 = iterator;
            String base1 = base;
            Document doc1 = parser1.build(raw, base1);
            walkTree(iterator1, doc1);
            serialize(doc1, new ByteArrayOutputStream(3000000));
            prettyPrint(doc1, new ByteArrayOutputStream(4000000));
            serializeUTF16(doc1, new ByteArrayOutputStream(4000000));
        }
    }

    // note use of recursion
    public void followNode(Node node) throws IOException {
    
        for (int i = 0; i < node.getChildCount(); i++) {
            followNode(node.getChild(i));
        }
    
    }

}