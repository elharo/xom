/* Copyright 2002-2004 Elliotte Rusty Harold
   
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

import java.io.IOException;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Attribute;
import nu.xom.Nodes;
import nu.xom.NodeFactory;
import nu.xom.ParsingException;


/**
 * <p>
 * Demonstrates walking the element hierarchy of 
 * an XML document in a streaming fashion while storing state in the 
 * node factory.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0
 *
 */
public class StreamingElementLister extends NodeFactory{

    private int depth = 0;
    private Nodes empty = new Nodes();

    public static void main(String[] args) {
  
        if (args.length == 0) {
            System.out.println(
              "Usage: java nu.xom.samples.StreamingElementLister URL"
            ); 
            return;
        } 
      
        Builder builder = new Builder(new StreamingElementLister());
     
        try {
            builder.build(args[0]);
        }  
        catch (ParsingException ex) { 
            System.out.println(args[0] + " is not well-formed.");
            System.out.println(ex.getMessage());
        }  
        catch (IOException ex) { 
            System.out.println(ex);
        }  
  
    }

    // We don't need the comments.     
    public Nodes makeComment(String data) {
        return empty;  
    }    

    // We don't need text nodes at all    
    public Nodes makeText(String data) {
        return empty;  
    }    

    public Element startMakingElement(String name, String namespace) {
        depth++; 
        printSpaces();
        System.out.println(name);           
        return new Element(name, namespace);
    }
    
    public Nodes finishMakingElement(Element element) {
        depth--;
        if (element.getParent() instanceof Document) {
            return new Nodes(element);
        }
        else return empty;
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
  
    private void printSpaces() {    
        for (int i = 0; i <= depth; i++) {
            System.out.print(' '); 
        } 
    }

}
