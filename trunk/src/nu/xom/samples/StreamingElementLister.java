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
import nu.xom.ParseException;


/**
 * <p>
 * Demonstrates walking the element hierarchy of 
 * an XML document in a streaming fashion while storing state in the 
 * node factory.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d18
 *
 */
public class StreamingElementLister extends NodeFactory{

    private int depth = 0;

    public static void main(String[] args) {
  
        if (args.length == 0) {
            System.out.println(
              "Usage: java nu.xom.samples.StreamingElementLister URL"
            ); 
            return;
        } 
      
        Builder builder = new Builder(new StreamingElementLister());
     
        try {
            Document doc = builder.build(args[0]);
        }
        // indicates a well-formedness error
        catch (ParseException e) { 
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
        Element result = null;
        if (depth == 0) result = new Element(name, namespace);
        depth++; 
        printSpaces();
        System.out.println(name);           
        return result;
    }
    
    protected Element finishMakingElement(Element element) {
        depth--;
        return element;
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
  
    private void printSpaces() {    
        for (int i = 0; i <= depth; i++) {
            System.out.print(' '); 
        } 
    }

}
