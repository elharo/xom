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
   elharo@metalab.unc.edu. Please include the word "XOM" in the
   subject line. The XOM home page is temporarily located at
   http://www.cafeconleche.org/XOM/  but will eventually move
   to http://www.xom.nu/  */

package nu.xom.samples;

import java.io.IOException;

import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.NodeFactory;
import nu.xom.Nodes;
import nu.xom.ParsingException;


/**
 * <p>
 * Non-recursive, streaming alternative to <code>NodeLister</code>.
 * </p>
 *
 * @author Elliotte Rusty Harold
 * @version 1.0d23
 *
 */
public class StreamingNodeLister extends NodeFactory {

    private Nodes empty = new Nodes();

  public static void main(String[] args) {
  
    if (args.length == 0) {
      System.out.println(
        "Usage: java nu.xom.samples.StreamingNodeLister URL"
      );
      return;
    } 
      
    Builder builder = new Builder(new StreamingNodeLister());
     
    try {
      builder.build(args[0]);      
    }
    // indicates a well-formedness error
    catch (ParsingException ex) { 
      System.out.println(args[0] + " is not well-formed.");
      System.out.println(ex.getMessage());
    }  
    catch (IOException ex) { 
      System.out.println(ex);
    }  
  
  }
  
  private int depth = 0;

    public Nodes makeComment(String data) {
        printSpaces();
        System.out.println("Comment");
        return empty; 
    }    

    public Nodes makeText(String data) {
        printSpaces();
        System.out.println("Text");
        return empty; 
    }    

    public Element startMakingElement(String name, String namespace) {
        depth++;
        printSpaces();
        System.out.println("Element: " + name);
        return super.startMakingElement(name, namespace);    
    }

    public Nodes finishMakingElement(Element element) {
        depth--;
        return new Nodes(element);
    }

    public Nodes makeDocType(String rootElementName, 
      String publicID, String systemID) {
        System.out.println("DOCTYPE");
        return empty; 
    }

    public Nodes makeProcessingInstruction(
      String target, String data) {
        printSpaces();
        System.out.println("Processing instruction: " + target);
        return empty; 
    }
  
  private void printSpaces() {
    
    for (int i = 0; i < depth; i++) {
      System.out.print(' '); 
    }
    
  }

}
