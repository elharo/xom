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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;


/**
 * 
 * <p>
 * Demonstrates the reading of attributes in namespaces,
 * as well as maintaining a stack of hierarchy-based state
 * during document traversal.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d22
 *
 */
public class XLinkSpider {

    private Set spidered = new HashSet();
    private Builder parser = new Builder();
    private List queue = new LinkedList();
    
    public static final String XLINK_NS 
      = "http://www.w3.org/1999/xlink";
    public static final String XML_NS 
      = "http://www.w3.org/XML/1998/namespace";
    
    public void search(URL url) {
        
        try {
            String systemID = url.toExternalForm();
            Document doc = parser.build(systemID);
            System.out.println(url);
            search(doc.getRootElement(), url);
        }
        catch (Exception ex) {
            // just skip this document
        }
        
        if (queue.isEmpty()) return;
        
        URL discovered = (URL) queue.remove(0);
        spidered.add(discovered);
        search(discovered);      
        
    }

    private void search(Element element, URL base) {

        Attribute href = element.getAttribute("href", XLINK_NS); 
        Attribute xmlbase = element.getAttribute("base", XML_NS);
        try {
            if (xmlbase != null) {
                base = new URL(base, xmlbase.getValue());
            }
        }
        catch (MalformedURLException ex) {
            // Probably just no protocol handler for the 
            // kind of URLs used inside this element
            return;
        }
        if (href != null) {
            String uri = href.getValue();
            // absolutize URL
            try {
                URL discovered = new URL(base, uri);
                // strip ref field if any
                discovered = new URL(
                  discovered.getProtocol(),
                  discovered.getHost(),
                  discovered.getFile()
                );
                
                if (!spidered.contains(discovered) 
                  && !queue.contains(discovered)) {
                    queue.add(discovered);   
                }
            }
            catch (MalformedURLException ex) {
                // skip this one   
            }
        }
        Elements children = element.getChildElements();
        for (int i = 0; i < children.size(); i++) {
            search(children.get(i), base);
        }
        
    }

    public static void main(String[] args) {
      
        XLinkSpider spider = new XLinkSpider();
        for (int i = 0; i < args.length; i++) { 
            try { 
                spider.search(new URL(args[i]));
            }
            catch (MalformedURLException ex) {
                System.err.println(ex);   
            }
        }
      
    }   // end main()

}