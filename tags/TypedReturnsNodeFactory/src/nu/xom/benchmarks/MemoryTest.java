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

import java.io.IOException;
import java.text.DecimalFormat;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;

/**
 * <p>
 *   Simple memory benchmark focusing on a big document
 *   full of small elements or a user supplied document.
 * </p>
 * 
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d22
 *
 */
class MemoryTest {

    public static Document makeFullUnicode() {
        
        Element root = new Element("root");
        Document doc = new Document(root);           

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
        
        return doc;
        
    }

    public static void main(String[] args) 
      throws IOException, ParsingException {
        
        DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(2);
        Runtime r = Runtime.getRuntime();
        System.gc(); System.gc(); System.gc();
        long before = r.totalMemory() - r.freeMemory();
        Document doc;
        if (args.length > 0) {
            Builder builder = new Builder();
            doc = builder.build(args[0]); 
            builder = null;  
        }
        else {
            doc = makeFullUnicode();
        }
        long after = r.totalMemory() - r.freeMemory();
        double usage = (after - before)/(1024.0*1024.0);
        System.out.println("Memory used: " 
          + format.format(usage) + "M");
        System.gc(); System.gc(); System.gc();
        long postGC = r.totalMemory() - r.freeMemory();
        usage = (after - before)/(1024.0*1024.0);
        System.out.println("Memory used after garbage collection: " 
          + format.format(usage) + "M");
       
        // Make sure the document isn't prematurely garbage collected
        System.out.println("Meaningless number: " 
          + doc.toXML().length());
    }

}
