/* Copyright 2002-2004 Elliotte Rusty Harold
   
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
   subject line. The XOM home page is located at http://www.xom.nu/
*/

package nu.xom.samples;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;

/**
 * <p>
 * Demonstrates extracting the content of particular named elements
 * from one XML document, and storing them into new files.
 * The names of these files are based on an attribute of the 
 * original element.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0
 *
 */
public class ExampleExtractor {

    private static int chapter = 0;
  
    public static void extract(Element element) 
      throws IOException {        

        Elements elements = element.getChildElements();
        for (int i = 0; i < elements.size(); i++) {
            Element child = elements.get(i);
            if (child.getQualifiedName().equals("chapter")) {
                chapter++;
            }       
             if (child.getQualifiedName().equals("example")) {
                extractExample(child, chapter);
            }
            else {
                extract(child);    
            }  
        } // end chapters loop

    }

    
    private static void extractExample(Element example, int chapter) 
      throws IOException {

        String fileName = example.getAttribute("id").getValue();
        Element programlisting 
          = example.getFirstChildElement("programlisting");
        
        // A few examples use screens instead of programlistings
        if (programlisting == null) {
            programlisting = example.getFirstChildElement("screen");
        } 
        // If it's still null, skip it
        if (programlisting == null) return;
        String code = programlisting.getValue();
        
        // write code into a file
        File dir = new File("examples2/" + chapter);
        dir.mkdirs();
        File file = new File(dir, fileName);
        System.out.println(file);
        FileOutputStream fout = new FileOutputStream(file);
        Writer out = new OutputStreamWriter(fout, "UTF-8");
        try {
            // Buffering almost always helps performance a lot
            out = new BufferedWriter(out);
            out.write(code);
            // Be sure to flush and close your streams
            out.flush();
        }
        finally {
            fout.close();
        }
    
    }
  
    
    public static void main(String[] args) {

        if (args.length <= 0) {
          System.out.println(
            "Usage: java nu.xom.samples.ExampleExtractor URL");
          return;
        }
        String url = args[0];
        
        try {
            Builder builder = new Builder();
            // Read the document
            Document document = builder.build(args[0]);
         
             // Extract the examples
             extract(document.getRootElement());
    
        }
        catch (ParsingException ex) {
            System.out.println(ex);
        }
        catch (IOException ex) { 
            System.out.println(
              "Due to an IOException, the parser could not read " + url
            ); 
            System.out.println(ex);
        } 
        catch (OutOfMemoryError ex) { 
            System.out.println(ex);
            ex.printStackTrace();
        } 
     
    } // end main
  
    
}
  
