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

import nu.xom.Builder;
import nu.xom.Comment;
import nu.xom.DocType;
import nu.xom.Element;
import nu.xom.NodeFactory;
import nu.xom.ParseException;
import nu.xom.ProcessingInstruction;
import nu.xom.Text;


/**
 * <p>
 *   Demonstrates recursive descent through an XML document,
 *   and the getter methods of the <code>Element</code> 
 *   and <code>Attribute</code> classes.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d18
 *
 */
public class StreamingTreeReporter extends NodeFactory {

    private Writer out;
    
    public StreamingTreeReporter(Writer out) {
      if (out == null) {
      throw new NullPointerException("Writer must be non-null.");
      }
      this.out = out;
    }
    
    public StreamingTreeReporter() {
      this(new OutputStreamWriter(System.out));
    }

    // We don't really need the comments.     
    public Comment makeComment(String data) {
        return null; 
    }    

    public Text makeText(String data) {
        return null;  
    }    

    public Element finishMakingElement(Element element) {
        TreePrinter.inspect(element);
        return element;
    }


    public DocType makeDocType(String rootElementName, 
      String publicID, String systemID) {
        return null;    
    }

    public ProcessingInstruction makeProcessingInstruction(
      String target, String data) {
        return null; 
    }

    public static void main(String[] args) {
  
        if (args.length <= 0) {
          System.out.println(
            "Usage: java nu.xom.samples.StreamingTreeReporter URL"
          );
          return;
        }
        
        try {
          Builder parser = new Builder(new StreamingTreeReporter());
          parser.build(args[0]);
        }
        catch (ParseException ex) {
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
