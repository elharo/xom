// Copyright 2003 Elliotte Rusty Harold
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
package nu.xom.tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Reader;
import java.io.Writer;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Comment;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.NodeFactory;
import nu.xom.ParsingException;
import nu.xom.ProcessingInstruction;
import nu.xom.Text;


/**
 * <p>
 *   Test that XOM can handle really big files.
 * </p>
 * 
 * @author Elliotte Rusty Harold
 * @version 1.0d22
 *
 */
public class MegaTest extends XOMTestCase {

    Reader in;
    Writer out;
    Builder builder;
    private final int expectedResult = 200000000;
    private int actualResult = 0;
    Thread generator;
    
    public static void main(String[] args) throws Exception {
        MegaTest test = new MegaTest();
        test.setUp();
        test.testMegaFile();
    }

    protected void setUp() throws IOException {
        PipedReader pin = new PipedReader();
        out = new PipedWriter(pin);
        in = new BufferedReader(pin);
        actualResult = 0;
        builder =  new Builder(new MinimalizingFactory());
        generator = new Generator();
        generator.start();
    }
    
    class Generator extends Thread {
        
        public void run() {
            try {
            out.write("<?xml version='1.0'?>\n");
            out.write("<root>\n");
            for (int i = 0; i < expectedResult; i++) {
                out.write("  <data>1</data>\n");
                // out.flush();  
                if (i % 10000 == 0) {
                    System.out.println(i / 10000);   
                } 
            }
            out.write("</root>\n"); 
            out.close(); 
            }
            catch (IOException ex) {
                fail("threw IOException " + ex);   
            } 
                       
        }
        
    }
    
    public void testMegaFile() 
      throws IOException, ParsingException {

        Document doc = builder.build(in);
        assertEquals(expectedResult, actualResult);

    } 


    class MinimalizingFactory extends NodeFactory {

        public Comment makeComment(String data) {
            return null;  
        }     
    
        protected Element finishMakingElement(Element element) {
            if (element.getQualifiedName().equals("data")) {
                actualResult += Integer.parseInt(element.getValue());
                return null;
            }  
            return element;      
        }
    
        public Attribute makeAttribute(String name, String URI, 
          String value, Attribute.Type type) {
            return null;
        }
    
        public DocType makeDocType(String rootElementName, 
          String publicID, String systemID) {
            return null;    
        }
    
        public Text makeWhiteSpaceInElementContent(String data) {
            return null;  
        }
    
        public Text makeText(String data) {
            data = data.trim();
            if ("".equals(data)) return null;
            return super.makeText(data);  
        }
    
        public ProcessingInstruction makeProcessingInstruction(
          String target, String data) {
            return null; 
        }          
        
    }

}