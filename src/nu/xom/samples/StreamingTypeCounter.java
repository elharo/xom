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
import nu.xom.Element;
import nu.xom.Attribute;
import nu.xom.Nodes;
import nu.xom.NodeFactory;
import nu.xom.ParsingException;


/**
 * <p>
 * Demonstrates walking the element hierarchy of 
 * an XML document in a streaming fashion while  
 * storing state in the node factory.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0
 *
 */
public class StreamingTypeCounter extends NodeFactory{

    private int CDATA = 0;
    private int ID = 0;
    private int IDREF = 0;
    private int IDREFS = 0;
    private int NMTOKEN = 0;
    private int NMTOKENS = 0;
    private int ENTITY = 0;
    private int ENTITIES = 0;
    private int NOTATION = 0;
    
    private Nodes empty = new Nodes();

    public void printCount() {
        System.out.println("CDATA type attributes: " + CDATA);
        System.out.println("ID type attributes: " + ID );
        System.out.println("IDREF type attributes: " + IDREF );
        System.out.println("IDREFS type attributes: " + IDREFS );
        System.out.println("NMTOKEN type attributes: " + NMTOKEN );
        System.out.println("NMTOKENS type attributes: " + NMTOKENS );
        System.out.println("ENTITY type attributes: " + ENTITY );
        System.out.println("ENTITIES type attributes: " + ENTITIES );
        System.out.println("NOTATION type attributes: " + NOTATION );
    }

    public static void main(String[] args) {
  
        if (args.length == 0) {
            System.out.println(
              "Usage: java nu.xom.samples.StreamingTypeCounter URL"
            ); 
            return;
        } 
      
        StreamingTypeCounter counter = new StreamingTypeCounter();
        Builder builder = new Builder(counter);
     
        try {
            builder.build(args[0]);
            counter.printCount();
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

    // We don't need the comments.     
    public Nodes makeComment(String data) {
        return empty;  
    }    

    // We don't need text nodes at all    
    public Nodes makeText(String data) {
        return empty;  
    }    

    public Element startMakingElement(String name, String namespace) {
        // We only need to create the root element           
        return null;
    }
    
    public Nodes makeAttribute(String name, String URI, 
      String value, Attribute.Type type) {
        if (type.equals(Attribute.Type.CDATA))         CDATA++;
        else if (type.equals(Attribute.Type.UNDECLARED)) CDATA++;
        else if (type.equals(Attribute.Type.ID))       ID++;
        else if (type.equals(Attribute.Type.IDREF))    IDREF++;
        else if (type.equals(Attribute.Type.IDREFS))   IDREFS++;
        else if (type.equals(Attribute.Type.NMTOKEN))  NMTOKEN++;
        else if (type.equals(Attribute.Type.NMTOKENS)) NMTOKENS++;
        else if (type.equals(Attribute.Type.ENTITY))   ENTITY++;
        else if (type.equals(Attribute.Type.ENTITIES)) ENTITIES++;
        else if (type.equals(Attribute.Type.NOTATION)) NOTATION++;
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

}
