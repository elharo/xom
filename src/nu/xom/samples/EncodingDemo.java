/* Copyright 2002, 2003 Elliotte Rusty Harold
   
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


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

/**
 * @author Elliotte Rusty Harold
 * @version 1.0
 *
 */
public class EncodingDemo {

    public static void main(String[] args) {
     
        String encoding = "ISO-8859-2";
        if (args.length > 0) encoding = args[0];
        Element root = new Element("root");
        Document doc = new Document(root);
     
        for (int i = 0xA0; i <= 0x1FF; i++) {
            Element data = new Element("data");
            data.appendChild((char) i + "");
            data.addAttribute(
              new Attribute("character", String.valueOf(i))
            );
            root.appendChild(data);
        }
     
        try {
            OutputStream out 
              = new FileOutputStream("data_" + encoding + ".xml");
            Serializer serializer = new Serializer(out, encoding);
            serializer.setIndent(4);
            serializer.write(doc);
            serializer.flush();
            out.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();   
        }
        
        
    }

}
