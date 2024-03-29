/* Copyright 2002, 2003, 2018 Elliotte Rusty Harold
   
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

import java.io.IOException;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;


/**
 * <p>
 * Demonstrates extracting the content 
 * of particular named elements
 * in a particular context 
 * from an XML document.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.3.0
 *
 */
public class ExampleLister {

    private static int chapter = 0;

    public static void list(Element root) {

        chapter = 0;
        if (root.getLocalName().equals("chapter")) {
            chapter++;
            exampleNumber = 0;
            list(root);    
        } 
        else {
            for (Element child : root.getChildElements()) {
            	if (child.getLocalName().equals("chapter")) {
                    chapter++;
                    exampleNumber = 0;
                    findExamples(child);    
                }
                else {
                    list(child);    
                }
            }
            
        }       
        
    }        

    
    private static int exampleNumber = 0;
  
    private static void findExamples(Element element) {        

        for (Element child : element.getChildElements()) {
             if (child.getQualifiedName().equals("example")) {
                printExample(child);
            }
            else {
                findExamples(child);    
            }  
        } 

    }

    
    private static void printExample(Element example) {

        exampleNumber++;
        Element title = example.getFirstChildElement("title");
        
        String caption = "Example " + chapter + "." + exampleNumber
         + ": " + title.getValue();
        
       System.out.println(caption);
    
    }
  
    
    public static void main(String[] args) {

        if (args.length <= 0) {
          System.out.println("Usage: java nu.xom.samples.ExampleLister URL");
          return;
        }
        String url = args[0];
        
        try {
            Builder builder = new Builder();
            // Read the document
            Document document = builder.build(args[0]);
         
             // List the examples
             list(document.getRootElement());
    
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
     
    } // end main
  
}