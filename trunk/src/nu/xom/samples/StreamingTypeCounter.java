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

import nu.xom.Builder;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Attribute;
import nu.xom.Comment;
import nu.xom.ProcessingInstruction;
import nu.xom.Text;
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
 * @version 1.0d22
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
            Document doc = builder.build(args[0]);
            counter.printCount();
        }
        // indicates a well-formedness error
        catch (ParsingException e) { 
            System.out.println(args[0] + " is not well-formed.");
            System.out.println(e.getMessage());
        }  
        catch (IOException e) { 
            System.out.println(e);
        }  
  
    }

    // We don't need the comments.     
    public Comment makeComment(String data) {
        return null;  
    }    

    // We don't need text nodes at all    
    public Text makeText(String data) {
        return null;  
    }    

    public Element startMakingElement(String name, String namespace) {
        // We only need to create the root element
        Element result = new Element(name, namespace);           
        return result;
    }
    
    public Attribute makeAttribute(String name, String URI, 
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

}
