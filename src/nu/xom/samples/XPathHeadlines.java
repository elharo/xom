/* Copyright 2005, 2018 Elliotte Rusty Harold
   
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
   elharo@ibiblio.org. Please include the word "XOM" in the
   subject line. The XOM home page is located at http://www.xom.nu/
*/

package nu.xom.samples;

import java.io.IOException;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Node;
import nu.xom.ParsingException;

/**
 * <p>
 * Use XPath to find just the headlines from an RSS feed.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.3.0
 */
public class XPathHeadlines {
  
    
    public static void main(String[] args) {
  
        String url = "http://www.bbc.co.uk/syndication/feeds/news/ukfs_news/world/rss091.xml";
        if (args.length > 0) {
          url = args[0];
        }
        
        try {
          Builder parser = new Builder();
          Document doc = parser.build(url);
          for (Node title : doc.query("//title")) {
            System.out.println(title.getValue());
          }
        }
        catch (ParsingException ex) {
          System.out.println(url + " is not well-formed.");
          System.out.println(ex.getMessage());
        }
        catch (IOException ex) { 
          System.out.println(
           "Due to an IOException, the parser could not read " + url
          ); 
        }
  
    }

    
}