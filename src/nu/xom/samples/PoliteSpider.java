/* Copyright 2002-2004, 2006, 2019 Elliotte Rusty Harold
   
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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.ProcessingInstruction;

/**
 * 
 * <p>
 * Demonstrates the reading of attributes in namespaces,
 * searching for particular processing instructions in the
 * document prolog, and the <code>getBaseURI()</code> method.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.3.1
 *
 */
public class PoliteSpider {

    private Set<URL> spidered = new HashSet<URL>();
    private Builder parser = new Builder();
    private List<URL> queue = new LinkedList<URL>();
    
    public static final String XLINK_NS 
     = "http://www.w3.org/1999/xlink";
    
    public void search(URL url) {
        
        try {
            String systemID = url.toExternalForm();
            Document doc = parser.build(systemID);
            
            boolean follow = true;
            boolean index = true;
            for (int i = 0; i < doc.getChildCount(); i++) {
                Node child = doc.getChild(i); 
                if (child instanceof Element) break;  
                if (child instanceof ProcessingInstruction){
                    ProcessingInstruction instruction 
                      = (ProcessingInstruction) child;
                    if (instruction.getTarget().equals("robots")) {
                        Element data 
                          = PseudoAttributes.getAttributes(instruction); 
                        Attribute indexAtt = data.getAttribute("index"); 
                        if (indexAtt != null) {
                            String value = indexAtt.getValue().trim();
                            if (value.equals("no")) index = false;
                        }
                        Attribute followAtt = data.getAttribute("follow"); 
                        if (followAtt != null) {
                            String value = followAtt.getValue().trim();
                            if (value.equals("no")) follow = false;
                        }
                    }   
                }  
            }
            
            if (index) System.out.println(url);
            if (follow) search(doc.getRootElement());
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
        Elements children = element.getChildElements();
        for (int i = 0; i < children.size(); i++) {
            search(children.get(i));
        }
    }

    public static void main(String[] args) {
      
        PoliteSpider spider = new PoliteSpider();
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