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

/**
 * <p>
 *   Demonstrates using the <code>Builder</code> and a custom 
 *   <code>NodeFactory</code> to list the comments in a document
 *   that contains very little else, thus saving 
 *   memory, and avoiding the overhead of building
 *   lots of objects we don't actually need.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0
 *
 */
public class StreamingCommentReader extends NodeFactory {

    private Nodes empty = new Nodes();

    // We don't really need the comments. We just want to print them.    
    public Nodes makeComment(String data) {
        System.out.println(data); 
        return empty;  
    }    

    // We don't need text nodes at all    
    public Nodes makeText(String data) {
        return empty;  
    }    

    public Element makeRootElement(String name, String namespace) {
        return new Element(name, namespace);    
    }
    
    public Element startMakingElement(String name, String namespace) {
        return null;    
    }

    public Nodes makeAttribute(String name, String URI, 
      String value, Attribute.Type type) {
        return empty;
    }

    public Nodes makeDocType(String rootElementName, 
      String publicID, String systemID) {
        return empty;    
    }

    public Nodes makeProcessingInstruction(
      String target, String data) {
        return empty; 
    }

    public static void main(String[] args) {
  
        if (args.length <= 0) {
          System.out.println("Usage: java nu.xom.samples.CommentReader URL");
          return;
        }
        
        try {
          Builder parser = new Builder(new StreamingCommentReader());
          parser.build(args[0]);
        }
        catch (ParsingException ex) {
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

}