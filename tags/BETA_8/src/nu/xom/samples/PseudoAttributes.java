/* Copyright 2002, 2003 Elliotte Rusty Harold
   
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.ProcessingInstruction;


/**
 * <p>
 *   A utility class which converts pseudo-attributes in a
 *   <code>ProcessingInstruction</code> object into
 *   real <code>Attribute/code> objects.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d22
 *
 */
public class PseudoAttributes {

    public static Element getAttributes(ProcessingInstruction pi)
      throws ParsingException  {
  
        StringBuffer sb = new StringBuffer("<");
        sb.append(pi.getTarget());
        sb.append(" ");
        sb.append(pi.getValue());
        sb.append("/>");
        
        try {
            InputStream in = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
            Builder parser = new Builder();
              
            // This line will throw a ParsingException if the processing
            // instruction does not use pseudo-attributes
            Document doc = parser.build(in);
            Element root = doc.getRootElement();
            // detach root element
            doc.setRootElement(new Element("fauxRoot"));
            return root;
        }
        catch (Exception ex) {
            throw new ParsingException(pi.toXML() 
             + " is not in pseudo-attribute format.", ex);   
        }
    
    }
  
}
