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

package nu.xom.benchmarks;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;

/**
 * @author Elliotte Rusty Harold
 *
 */
class FullUnicode {

    private Document doc;

    public FullUnicode() {
        Element root = new Element("root");
        doc = new Document(root);           

        for (int i = 0x20; i <= 0xD7FF; i++) {
            Element data = new Element("d");
            data.appendChild(((char) i) + "");
            data.addAttribute(new Attribute("c", String.valueOf(i)));
            root.appendChild(data);
        }
        
        // skip surrogates between 0xD800 and 0xDFFF
        
        for (int i = 0xE000; i <= 0xFFFD; i++) {
            Element data = new Element("d");
            data.appendChild(((char) i) + "");
            data.addAttribute(new Attribute("c", String.valueOf(i)));
            root.appendChild(data);
        }
        
        System.gc();

        // Plane-1 characters are tricky because Java 
        // strings  encode them as surrogate pairs. First, fill  
        // a byte array with the characters from 1D100 to 1D1FF 
        // (the musical symbols)
        for (int i = 0; i < 256; i++) {
            // algorithm from RFC 2781
            int u = 0x1D100 + i;
            int uprime = u - 0x10000;
            int W1 = 0xD800;
            int W2 = 0xDC00;
            W2 = W2 | (uprime & 0x7FF );
            W1 = W1 | (uprime & 0xFF800);
            Element data = new Element("d");
            data.appendChild( ((char) W1) + "" + ((char) W2) );
            data.addAttribute(new Attribute("c", String.valueOf(u)));
            root.appendChild(data);
        }
        
    }

    // need to spawn a do-nothing thread to keep object live????

    public static void main(String[] args) throws InterruptedException {
        
        // get free memory????
        FullUnicode data = new FullUnicode();
        System.gc();
        System.gc();
        System.gc();
        // print free memory????
        Thread.sleep(50000);
        
    }
  

}
