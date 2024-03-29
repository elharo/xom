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

import java.io.*;

import nu.xom.Attribute;
import nu.xom.Comment;
import nu.xom.Document;
import nu.xom.Builder;
import nu.xom.NodeFactory;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.ProcessingInstruction;
import nu.xom.Serializer;
import nu.xom.Text;

/**
 * @author Elliotte Rusty Harold
 * @version 1.0
 *
 */
public class StreamingROT13 extends NodeFactory {

    public static String rot13(String s) {
    
    	StringBuilder out = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
          int c = s.charAt(i);
          if (c >= 'A' && c <= 'M') out.append((char) (c+13));
          else if (c >= 'N' && c <= 'Z') out.append((char) (c-13));
          else if (c >= 'a' && c <= 'm') out.append((char) (c+13));
          else if (c >= 'n' && c <= 'z') out.append((char) (c-13));
          else out.append((char) c);
        } 
        return out.toString();
    
    }

    // We don't really need the comments. We just want to print them.    
    public Nodes makeComment(String data) {
        return new Nodes(new Comment(rot13(data)));
    }    

    public Nodes makeText(String data) {
        return new Nodes(new Text(rot13(data)));  
    }    

    public Nodes makeAttribute(String name, String namespace, 
      String value, Attribute.Type type) {
        return new Nodes(new Attribute(name, namespace, rot13(value), type));  
    }

    public Nodes makeProcessingInstruction(
      String target, String data) {
        return new Nodes(new ProcessingInstruction(rot13(target), rot13(data)));
    }

  public static void main(String[] args) {

    if (args.length <= 0) {
      System.out.println("Usage: java nu.xom.samples.StreamingROT13 URL");
      return;
    }
    
    try {
      Builder parser = new Builder(new StreamingROT13());
      
      // Read the document
      Document document = parser.build(args[0]); 
      
      // Write it out again
      Serializer serializer = new Serializer(System.out);
      serializer.write(document);

    }
    catch (IOException ex) { 
      System.out.println(
      "Due to an IOException, the parser could not encode " + args[0]
      ); 
    }
    catch (ParsingException ex) { 
      System.out.println(ex); 
      ex.printStackTrace(); 
    }
     
  } // end main
  
}
