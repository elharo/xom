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
import java.io.OutputStreamWriter;
import java.io.Writer;

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
 *   Demonstrates filtered streaming via a <code>NodeFactory</code>
 *   subclass.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d22
 *
 */
public class StreamingTextExtractor extends NodeFactory {

    private Writer out;
    
    public StreamingTextExtractor(Writer out) {
      if (out == null) {
      throw new NullPointerException("Writer must be non-null.");
      }
      this.out = out;
    }
    
    public StreamingTextExtractor() {
      this(new OutputStreamWriter(System.out));
    }
    
    // We don't really need the comments. We just want to print them.    
    public Comment makeComment(String data) {
        return null;  
    }    

    public Text makeText(String data) {
        try {
            out.write(data);
        }
        catch (IOException ex) {
            System.err.println(ex);   
        }
        return null;  
    }    

    public Element makeRootElement(String name, String namespace) {
        Element result = new Element(name, namespace);  
        return result;  
    }
    
    public Element startMakingElement(String name, String namespace) {
        Element result = new Element(name, namespace);  
        return null;    
    }

    public Attribute makeAttribute(String name, String namespace, 
      String value, Attribute.Type type) {
        Attribute att = new Attribute(name, namespace, value, type);  
        return null;
    }

    public DocType makeDocType(String rootElementName, 
      String publicID, String systemID) {
        return null;    
    }

    public ProcessingInstruction makeProcessingInstruction(
      String target, String data) {
        return null; 
    }    
    
    public void endDocument(Document doc) {
        try {
            out.flush();
        }
        catch (IOException ex) {
           System.err.println(ex);   
        }
    }
    
    public static void main(String[] args) {
  
        if (args.length <= 0) {
          System.out.println(
            "Usage: java nu.xom.samples.StreamingTextExtractor URL"
          );
          return;
        }
        
        try {
          Builder parser = new Builder(new StreamingTextExtractor());
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