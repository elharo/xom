/* Copyright 2002-2004, 2006, 2018 Elliotte Rusty Harold
   
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;


/**
 * 
 * <p>
 * Demonstrates the reading of attributes in namespaces
 * and the <code>getBaseURI()</code> method.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.3.0
 *
 */
public class XLinkSpider {

    private Set<URL> spidered = new HashSet<URL>();
    private Builder parser = new Builder();
    private List<URL> queue = new ArrayList<URL>();
    
    public static final String XLINK_NS 
      = "http://www.w3.org/1999/xlink";
    
    public void search(URL url) {
        
        try {
            String systemID = url.toExternalForm();
            Document doc = parser.build(systemID);
            System.out.println(url);
            search(doc.getRootElement());
        }
        catch (Exception ex) {
            // just skip this document
        }
        
        if (queue.isEmpty()) return;
        
        URL discovered = (URL) queue.remove(0);
        spidered.add(discovered);
        search(discovered);      
        
    }

    private void search(Element element) {

        Attribute href = element.getAttribute("href", XLINK_NS);
        
        URL base = null;
        try {
            base = new URL(element.getBaseURI());
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
                // remove fragment identifier if any
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
        for (Element child : element.getChildElements()) {
            search(child);
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
      
    }  // end main()

}