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

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Comment;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.NodeFactory;
import nu.xom.ParsingException;
import nu.xom.ProcessingInstruction;
import nu.xom.Text;

/**
 * <p>
 *   Demonstrates using the <code>Builder</code> and a custom 
 *   <code>NodeFactory</code> to list the comments in a document
 *   that contains very little else, thus saving 
 *   memory, and avoiding the overhead of building
 *   lots of objects we don't actually need.
 * </p>
 * 
 *  @author Elliotte Rusty Harold
 *  @version 1.0d22
 *
 */
public class StreamingCommentReader extends NodeFactory {

    // We don't really need the comments. We just want to print them.    
    public Comment makeComment(String data) {
        System.out.println(data); 
        return null;  
    }    

    // We don't need text nodes at all    
    public Text makeText(String data) {
        return null;  
    }    

    public Element makeRootElement(String name, String namespace) {
        return new Element(name, namespace);    
    }
    
    public Element startMakingElement(String name, String namespace) {
        return null;    
    }

    public Attribute makeAttribute(String name, String URI, 
      String value, Attribute.Type type) {
        return null;
    }

    public DocType makeDocType(String rootElementName, 
      String publicID, String systemID) {
        return null;    
    }

    public Text makeWhiteSpaceInElementContent(String data) {
        return null;  
    }

    public ProcessingInstruction makeProcessingInstruction(
      String target, String data) {
        return null; 
    }

    public static void main(String[] args) {
  
        if (args.length <= 0) {
          System.out.println("Usage: java nu.xom.samples.CommentReader URL");
          return;
        }
        
        try {
          Builder parser = new Builder(new StreamingCommentReader());
          Document doc = parser.build(args[0]);
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